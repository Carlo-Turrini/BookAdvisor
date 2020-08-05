import { Component, OnInit } from '@angular/core';
import {Utente} from "../../core/model/utente";
import {ActivatedRoute, Router} from "@angular/router";
import {UtenteService} from "../../core/services/utente.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Observable} from "rxjs";
import {UserValidatorService} from "../../core/asyncValidators/user-validator.service";
import {UtenteForm} from "../../core/model/utente-form";
import {AuthenticationService} from "../../core/services/authentication.service";

@Component({
  selector: 'app-utente-form',
  templateUrl: './utente-form.component.html',
  styleUrls: ['./utente-form.component.css']
})
export class UtenteFormComponent implements OnInit {
  submitted: boolean = false;
  utenteForm = new FormGroup( {
    nome: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(24)]),
    cognome: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(24)]),
    username: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(24)], this.userValidatorService.validateUsername.bind(this.userValidatorService)),
    password: new FormControl('', [Validators.required, Validators.minLength(8), Validators.maxLength(50)]),
    email: new FormControl('', [Validators.required, Validators.email], this.userValidatorService.validateEmail.bind(this.userValidatorService)),
    descrizione: new FormControl('', [Validators.maxLength(2048)])
  });

  constructor(private route: ActivatedRoute, private router: Router, private userService: UtenteService, private userValidatorService: UserValidatorService, private authenticationService: AuthenticationService) {
  }

  ngOnInit() {
    if(this.authenticationService.logged) {
      this.router.navigate(['/']);
    }
  }

  async onSubmit() {
    this.submitted = true;
    const newUser = new UtenteForm(this.utenteForm.value.nome, this.utenteForm.value.cognome, this.utenteForm.value.username,this.utenteForm.value.password, this.utenteForm.value.email, this.utenteForm.value.descrizione);
    const r = this.userService.addNewUser(newUser);
    await r.toPromise().then(async (data) => {
      for(const fieldName of Object.keys(data)) {
        const serverErrors = data[fieldName];
        const errors = {};
        for(const serverError of serverErrors) {
          errors[serverError] = true;
        }
        const control = this.utenteForm.get(fieldName);
        control.setErrors(errors);
        control.markAsDirty();
      }
      if(this.utenteForm.valid) {
        this.utenteForm.reset();
        this.submitted = false;
        this.goToLogin();
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
  goToLogin() {
    this.router.navigate(['/login']);
  }

  isInvalidAndDirty(field: string) {
    const ctrl = this.utenteForm.get(field);
    return !ctrl.valid && ctrl.dirty;
  }

  hasError(field: string, error: string) {
    const ctrl = this.utenteForm.get(field);
    return ctrl.dirty && ctrl.hasError(error);
  }

}
