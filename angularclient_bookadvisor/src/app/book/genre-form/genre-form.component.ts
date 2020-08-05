import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {GenreService} from "../../core/services/genre.service";
import {GenreValidatorService} from "../../core/asyncValidators/genre-validator.service";
import {AuthenticationService} from "../../core/services/authentication.service";
import {GenreForm} from "../../core/model/genre-form";

@Component({
  selector: 'app-genre-form',
  templateUrl: './genre-form.component.html',
  styleUrls: ['./genre-form.component.css']
})
export class GenreFormComponent implements OnInit {
  @Output() genreSubmitted: EventEmitter<string> = new EventEmitter<string>(); //metti l'handler nel component bookForm e bookUpdate
  @Output() backToBookForm: EventEmitter<void> = new EventEmitter<void>();
  submitted : boolean = false;
  genreForm = new FormGroup({
    genre: new FormControl('', [Validators.required, Validators.minLength(1), Validators.maxLength(20)], this.genreValidatorService.validateGenreUniqueness.bind(this.genreValidatorService))
  });
  constructor(private route: ActivatedRoute, private router: Router, private genreService: GenreService, private genreValidatorService: GenreValidatorService, private authenticationService: AuthenticationService) { }

  ngOnInit() {
  }

  async onSubmit() {

    if(this.authenticationService.adminAuth()) {
      this.submitted = true;
      const newGenre = new GenreForm(this.genreForm.value.genre);
      await this.genreService.addGenre(newGenre).then(async (data) => {
        for(const fieldName of Object.keys(data)) {
          const serverErrors = data[fieldName];
          const errors = {};
          for(const serverError of serverErrors) {
            errors[serverError] = true;
          }
          const control = this.genreForm.get(fieldName);
          control.setErrors(errors);
          control.markAsDirty();
        }
        if(this.genreForm.valid) {
          this.genreForm.reset();
          this.submitted = false;
          this.genreSubmitted.emit(newGenre.genre);
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
    }
    else {
      this.router.navigate(["/"]);
    }
    this.submitted = false;
  }

  isInvalidAndDirty(field: string) {
    const ctrl = this.genreForm.get(field);
    return !ctrl.valid && ctrl.dirty;
  }

  hasError(field: string, error: string) {
    const ctrl = this.genreForm.get(field);
    return ctrl.dirty && ctrl.hasError(error);
  }

}
