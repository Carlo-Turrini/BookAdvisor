import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Utente} from "../../core/model/utente";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {UtenteService} from "../../core/services/utente.service";
import {UserValidatorService} from "../../core/asyncValidators/user-validator.service";
import {AuthenticationService} from "../../core/services/authentication.service";
import {UtenteUpdateForm} from "../../core/model/utente-update-form";

@Component({
  selector: 'app-utente-update',
  templateUrl: './utente-update.component.html',
  styleUrls: ['./utente-update.component.css']
})
export class UtenteUpdateComponent implements OnInit {
  @Input() private utente: Utente;
  @Output() private updated: EventEmitter<UtenteUpdateForm> = new EventEmitter<UtenteUpdateForm>();
  submitted: boolean = false;
  utenteUpdateForm = new FormGroup( {
    nome: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(24)]),
    cognome: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(24)]),
    passwordChange: new FormControl('', [Validators.required]),
    password: new FormControl('',[]),
    email: new FormControl('', [Validators.required, Validators.email]),
    descrizione: new FormControl('', [Validators.maxLength(2048)]),
  });

  constructor(private route: ActivatedRoute, private router: Router, private userService: UtenteService, private userValidatorService: UserValidatorService, private authenticationService: AuthenticationService) {
  }

  ngOnInit() {
    if(this.authenticationService.sameUserOrAdminAuth(this.utente.id)) {
      this.utenteUpdateForm.controls.nome.setValue(this.utente.name);
      this.utenteUpdateForm.controls.cognome.setValue(this.utente.surname);
      this.utenteUpdateForm.controls.email.setValue(this.utente.email);
      this.utenteUpdateForm.controls.descrizione.setValue(this.utente.description);
      this.utenteUpdateForm.controls.passwordChange.setValue(false);
    }
    else this.router.navigate(['/']);
  }

  async onSubmit() {
    if(this.authenticationService.sameUserOrAdminAuth(this.utente.id)) {
      this.submitted = true;
      const updatedUser = new UtenteUpdateForm(this.utenteUpdateForm.value.nome, this.utenteUpdateForm.value.cognome, this.utenteUpdateForm.value.password, this.utenteUpdateForm.value.email, this.utenteUpdateForm.value.descrizione);
      const r = this.userService.updateUser(updatedUser, this.utente.id);
      await r.toPromise().then(async (data) => {
        for (const fieldName of Object.keys(data)) {
          const serverErrors = data[fieldName];
          const errors = {};
          for (const serverError of serverErrors) {
            errors[serverError] = true;
          }
          const control = this.utenteUpdateForm.get(fieldName);
          control.setErrors(errors);
          control.markAsDirty();
        }
        if (this.utenteUpdateForm.valid) {
          this.utenteUpdateForm.reset();
          this.submitted = false;
          this.updated.emit(updatedUser);
        }
      })
        .catch(error => {
          let statusCode = error.error.status;
          alert(error.error.status + ": " + error.error.message)
          if(statusCode == 401) {
            this.router.navigate(['/login']);
          }
          else this.router.navigate(['/']);
        });
      this.submitted = false;
    }
    else this.router.navigate(['/']);
  }

  isInvalidAndDirty(field: string) {
    const ctrl = this.utenteUpdateForm.get(field);
    return !ctrl.valid && ctrl.dirty;
  }

  hasError(field: string, error: string) {
    const ctrl = this.utenteUpdateForm.get(field);
    return ctrl.dirty && ctrl.hasError(error);
  }

  clearPasswordField(event : any) {
    this.utenteUpdateForm.get('password').reset();
    if(this.utenteUpdateForm.value.passwordChange) {
      this.utenteUpdateForm.controls.password.setValidators([Validators.required, Validators.minLength(8), Validators.maxLength(50)]);
      this.utenteUpdateForm.controls.password.updateValueAndValidity();
    }
    else {
      this.utenteUpdateForm.controls.password.clearValidators();
      this.utenteUpdateForm.controls.password.updateValueAndValidity();
    }
  }
}
