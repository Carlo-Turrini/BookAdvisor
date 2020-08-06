import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthenticationService} from "../../core/services/authentication.service";
import {AuthorService} from "../../core/services/author.service";
import {AuthorValidatorService} from "../../core/asyncValidators/author-validator.service";
import {AuthorForm} from "../../core/model/author-form";
import {AuthorOfBook} from "../../core/model/author-of-book";
import {AuthorCard} from "../../core/model/author-card";

@Component({
  selector: 'app-author-form',
  templateUrl: './author-form.component.html',
  styleUrls: ['./author-form.component.css']
})
export class AuthorFormComponent implements OnInit {
  submitted: boolean = false;
  authorForm = new FormGroup({
    authorsFullname: new FormControl('', [Validators.required, Validators.minLength(3), Validators.maxLength(50)], this.authorValidatorService.validateFullname.bind(this.authorValidatorService)),
    birthYear: new FormControl('', [Validators.required, Validators.min(-2000), Validators.max(9998)]),
    deathYear: new FormControl('', [Validators.min(-1999), Validators.max(9999)]),
    biography: new FormControl('', [Validators.required, Validators.minLength(1), Validators.maxLength(4000)])
  });
  @Input() private from: string;
  @Output() private authorSubmitted: EventEmitter<AuthorOfBook> = new EventEmitter<AuthorOfBook>();
  @Output() private authorSubmittedInList: EventEmitter<AuthorCard[]> = new EventEmitter<AuthorCard[]>();
  @Output() goBack: EventEmitter<void> = new EventEmitter<void>();
  constructor(private route: ActivatedRoute, private router: Router, private authorService: AuthorService, private authorValidatorService: AuthorValidatorService, private authenticationService: AuthenticationService) {
  }

  ngOnInit() {
  }

  async onSubmit() {
    if(this.authenticationService.adminAuth()) {
      const newAuthor = new AuthorForm(this.authorForm.value.authorsFullname, this.authorForm.value.birthYear, this.authorForm.value.deathYear, this.authorForm.value.biography);
      this.authorService.addNewAuthor(newAuthor).subscribe(async (data) => {
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
          if(this.from == "bookForm") {
            this.emitAuthorOfBook(newAuthor.authorsFullname);
          }
          else if(this.from == "authorsList") {
            this.goToAuthorsList();
          }
        }

      },
        error => {
        let statusCode = error.error.status;
        alert(error.error.status + ": " + error.error.message)
        if(statusCode == 401) {
          this.router.navigate(['/login']);
        }
        else this.router.navigate(['/']);
      })

      this.submitted = false;
    }
    else this.router.navigate(['/']);
  }

  async emitAuthorOfBook(authorsFullname: string) {
    await this.authorService.getAuthorOfBookFromFullname(authorsFullname).then(author => {this.authorSubmitted.emit(author)})
      .catch(error => {
        let statusCode = error.error.status;
        alert(error.error.status + ": " + error.error.message)
        if(statusCode == 401) {
          this.router.navigate(['/login']);
        }
        else this.router.navigate(['/']);
      });
  }
  async goToAuthorsList() {
    await this.authorService.getAllAuthors().then(authors => {this.authorSubmittedInList.emit(authors)})
      .catch(error => {
        let statusCode = error.error.status;
        alert(error.error.status + ": " + error.error.message)
        if(statusCode == 401) {
          this.router.navigate(['/login']);
        }
        else this.router.navigate(['/']);
      });
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
