import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {LibroService} from "../../core/services/libro.service";
import {BookValidatorService} from "../../core/asyncValidators/book-validator.service";
import {LibroForm} from "../../core/model/libro-form";
import {AuthenticationService} from "../../core/services/authentication.service";
import {GenreService} from "../../core/services/genre.service";
import {AuthorService} from "../../core/services/author.service";
import {AuthorOfBook} from "../../core/model/author-of-book";

@Component({
  selector: 'app-book-form',
  templateUrl: './book-form.component.html',
  styleUrls: ['./book-form.component.css']
})
export class BookFormComponent implements OnInit {
  submitted:boolean;
  @Output() updateGenres: EventEmitter<string> = new EventEmitter<string>();
  bookForm = new FormGroup({
    titolo: new FormControl('',[Validators.required, Validators.minLength(1), Validators.maxLength(128)], this.bookValidatorService.validateTitolo.bind(this.bookValidatorService)),
    saga: new FormControl('', [Validators.required]),
    titoloSaga: new FormControl('', [Validators.maxLength(64)]),
    numInSaga: new FormControl('', [Validators.max(99)]),
    autori: new FormControl('', [Validators.required]),
    pagine: new FormControl('', [Validators.required, Validators.min(1), Validators.max(9999)]),
    annoPubblicazione: new FormControl('', [Validators.required, Validators.min(0), Validators.max(new Date().getFullYear())]),
    sinossi: new FormControl('', [Validators.required, Validators.minLength(1), Validators.maxLength(2048)]),
    generi: new FormControl('', [Validators.required])
  });
  private genres: string[] = [];
  private authors: AuthorOfBook[] = [];
  private addAuthor: boolean = false;
  private addGenre: boolean = false;
  //Aggiungi metodo per new Author e metodo per new Genre

  constructor(private route: ActivatedRoute, private router: Router, private bookService: LibroService, private bookValidatorService: BookValidatorService, private authenticationService: AuthenticationService, private genreService: GenreService, private authorService: AuthorService) { }

  async ngOnInit() {
    if(!this.authenticationService.adminAuth()) {
      this.router.navigate(['/']);
    }
    else {
      this.bookForm.controls.saga.setValue(false);
      await this.genreService.getAllGenres().then(genreList => {genreList.forEach(genre => {this.genres.push(genre)})});
      await this.authorService.getAllAuthorsForBookForm().then(authorsList => { authorsList.forEach(author => {this.authors.push(author)});
      })
    }
  }

  onSubmit() {
    if(this.authenticationService.adminAuth()) {
      this.submitted = true;
      const newBook = new LibroForm(this.bookForm.value.titolo, this.bookForm.value.sinossi, this.bookForm.value.autori, this.bookForm.value.generi, this.bookForm.value.annoPubblicazione, this.bookForm.value.pagine, this.bookForm.value.saga, this.bookForm.value.titoloSaga, this.bookForm.value.numInSaga);
      this.bookService.newBook(newBook).subscribe(async (data) => {
        for (const fieldName of Object.keys(data)) {
          const serverErrors = data[fieldName];
          const errors = {};
          for (const serverError of serverErrors) {
            errors[serverError] = true;
          }
          const control = this.bookForm.get(fieldName);
          control.setErrors(errors);
          control.markAsDirty();
        }
        if (this.bookForm.valid) {
          this.bookForm.reset();
          this.submitted = false;
          this.goToBook(newBook.titolo)
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
    else {
      this.router.navigate(['/']);
    }
  }

  goToBook(titolo: string) {
      this.router.navigate(['/libri/allContainingTitolo/' + titolo]);
  }

  isInvalidAndDirty(field: string) {
    const ctrl = this.bookForm.get(field);
    return !ctrl.valid && ctrl.dirty;
  }

  hasError(field: string, error: string) {
    const ctrl = this.bookForm.get(field);
    return ctrl.dirty && ctrl.hasError(error);
  }

  clearSagaFields(event : any) {
    this.bookForm.get('titoloSaga').reset();
    this.bookForm.get('numInSaga').reset();
    if(this.bookForm.value.saga) {
      this.bookForm.controls.titoloSaga.setValidators([Validators.required, Validators.maxLength(64)]);
      this.bookForm.controls.numInSaga.setValidators([Validators.required, Validators.max(99)]);

    }
    else {
      this.bookForm.controls.titoloSaga.setValidators([Validators.maxLength(64)]);
      this.bookForm.controls.numInSaga.setValidators([Validators.max(99)]);
      this.bookForm.controls.titoloSaga.updateValueAndValidity();
      this.bookForm.controls.numInSaga.updateValueAndValidity();
    }
  }
  newAuthor() {
    this.addAuthor = true;
  }
  newGenre() {
    this.addGenre = true;
  }
  authorAdded(authorOfBook: AuthorOfBook) {
    console.log(authorOfBook.authorsFullname)
    this.authors.push(authorOfBook);
    this.addAuthor = false;
  }
  genreAdded(genre: string) {
    this.genres.push(genre);
    this.addGenre = false;
    this.updateGenres.emit(genre);
  }

  backToBookForm() {
    this.addGenre = false;
    this.addAuthor = false;
  }

}
