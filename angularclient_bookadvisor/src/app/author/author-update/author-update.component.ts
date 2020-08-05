import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Utente} from "../../core/model/utente";
import {UtenteForm} from "../../core/model/utente-form";
import {Author} from "../../core/model/author";
import {AuthorForm} from "../../core/model/author-form";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthorService} from "../../core/services/author.service";
import {AuthorValidatorService} from "../../core/asyncValidators/author-validator.service";
import {AuthenticationService} from "../../core/services/authentication.service";

@Component({
  selector: 'app-author-update',
  templateUrl: './author-update.component.html',
  styleUrls: ['./author-update.component.css']
})
export class AuthorUpdateComponent implements OnInit {
  @Input() private author: Author;
  @Output() private updated: EventEmitter<AuthorForm> = new EventEmitter<AuthorForm>();
  private submitted: boolean = false;
  authorForm = new FormGroup({
    authorsFullname: new FormControl('', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]),
    birthYear: new FormControl('', [Validators.required, Validators.min(-2000), Validators.max(9998)]),
    deathYear: new FormControl('', [Validators.min(-1999), Validators.max(9999)]),
    biography: new FormControl('', [Validators.required, Validators.minLength(1), Validators.maxLength(4000)])
  });
  constructor(private route: ActivatedRoute, private router: Router, private authorService: AuthorService, private authorValidatorService: AuthorValidatorService, private authenticationService: AuthenticationService) {
  }

  ngOnInit() {
    if(this.authenticationService.adminAuth()) {
      this.authorForm.controls.authorsFullname.setValue(this.author.authorsFullname);
      this.authorForm.controls.birthYear.setValue(this.author.birthYear);
      this.authorForm.controls.deathYear.setValue(this.author.deathYear);
      this.authorForm.controls.biography.setValue(this.author.biography);
    }
    else this.router.navigate(['/']);
  }

  async onSubmit() {
    if(this.authenticationService.adminAuth()) {
      const updatedAuthor = new AuthorForm(this.authorForm.value.authorsFullname, this.authorForm.value.birthYear, this.authorForm.value.deathYear, this.authorForm.value.biography);
      this.authorService.updateAuthor(updatedAuthor, this.author.id).subscribe(async (data) => {
        for (const fieldName of Object.keys(data)) {
          const serverErrors = data[fieldName];
          const errors = {};
          for (const serverError of serverErrors) {
            errors[serverError] = true;
          }
          const control = this.authorForm.get(fieldName);
          control.setErrors(errors);
          control.markAsDirty();
        }
        if (this.authorForm.valid) {
          this.authorForm.reset();
          this.submitted = false;
          this.updated.emit(updatedAuthor);
        }

      },
        error => {
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
    const ctrl = this.authorForm.get(field);
    return !ctrl.valid && ctrl.dirty;
  }

  hasError(field: string, error: string) {
    const ctrl = this.authorForm.get(field);
    return ctrl.dirty && ctrl.hasError(error);
  }

}
