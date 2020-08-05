import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {LibroForm} from "../../core/model/libro-form";
import {Libro} from "../../core/model/libro";
import {Observable} from "rxjs";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {LibroService} from "../../core/services/libro.service";
import {BookValidatorService} from "../../core/asyncValidators/book-validator.service";
import {AuthenticationService} from "../../core/services/authentication.service";
import {AuthorService} from "../../core/services/author.service";
import {GenreService} from "../../core/services/genre.service";
import {AuthorOfBook} from "../../core/model/author-of-book";

@Component({
  selector: 'app-update-libro',
  templateUrl: './update-libro.component.html',
  styleUrls: ['./update-libro.component.css']
})
export class UpdateLibroComponent implements OnInit {
  @Output() updated: EventEmitter<LibroForm> = new EventEmitter<LibroForm>();
  @Input() libro: Libro;
  book$: Observable<Libro>;
  private authors: AuthorOfBook[] = [];
  private genres: string[] = [];
  private addAuthor: boolean = false;
  private addGenre: boolean = false;
  submitted:boolean = false;
  bookForm = new FormGroup({
    titolo: new FormControl('',[Validators.required, Validators.minLength(1), Validators.maxLength(128)]),
    saga: new FormControl('', [Validators.required]),
    titoloSaga: new FormControl('', [Validators.maxLength(64)]),
    numInSaga: new FormControl('', [Validators.max(99)]),
    autori: new FormControl('', [Validators.required]),
    pagine: new FormControl('', [Validators.required, Validators.min(1), Validators.max(9999)]),
    annoPubblicazione: new FormControl('', [Validators.required, Validators.min(0), Validators.max(new Date().getFullYear())]),
    sinossi: new FormControl('', [Validators.required, Validators.minLength(1), Validators.maxLength(2048)]),
    generi: new FormControl('', Validators.required),

  });

  constructor(private route: ActivatedRoute, private router: Router, private bookService: LibroService, private bookValidatorService: BookValidatorService, private authenticationService: AuthenticationService, private authorService: AuthorService, private genreService: GenreService) { }

  async ngOnInit() {
    if(this.authenticationService.adminAuth()) {
      await this.genreService.getAllGenres().then(genreList => {genreList.forEach(genre => {this.genres.push(genre)})})
        .catch(error => {
          let statusCode = error.error.status;
          alert(error.error.status + ": " + error.error.message)
          if(statusCode == 401) {
            this.router.navigate(['/login']);
          }
          else this.router.navigate(['/']);
        });
      await this.authorService.getAllAuthorsForBookForm().then(authorsList => {authorsList.forEach(author => {this.authors.push(author)})})
        .catch(error => {
          let statusCode = error.error.status;
          alert(error.error.status + ": " + error.error.message)
          if(statusCode == 401) {
            this.router.navigate(['/login']);
          }
          else this.router.navigate(['/']);
        });
      this.libro.autori.forEach(autore => {console.log(autore.authorsFullname + " " + autore.id)});
      this.bookForm.controls.titolo.setValue(this.libro.titolo);
      let authorsForFormState: AuthorOfBook[] = [];
      this.libro.autori.forEach(autore => {this.authors.forEach(author => {
        if(autore.id == author.id && autore.authorsFullname == author.authorsFullname) {
          authorsForFormState.push(author);
        }
      });});
      this.bookForm.controls.autori.setValue(authorsForFormState);
      this.bookForm.controls.annoPubblicazione.setValue(this.libro.annoPubblicazione);
      this.bookForm.controls.pagine.setValue(this.libro.pagine);
      this.bookForm.controls.generi.setValue(this.libro.generi);
      this.bookForm.controls.sinossi.setValue(this.libro.sinossi);
      this.bookForm.controls.saga.setValue(this.libro.saga);
      this.bookForm.controls.titoloSaga.setValue(this.libro.titoloSaga);
      this.bookForm.controls.numInSaga.setValue(this.libro.numInSaga);
      if(this.bookForm.value.saga) {
        this.bookForm.controls.titoloSaga.setValidators([Validators.required, Validators.maxLength(64)]);
        this.bookForm.controls.numInSaga.setValidators([Validators.required, Validators.max(99)]);
        this.bookForm.controls.titoloSaga.updateValueAndValidity();
        this.bookForm.controls.numInSaga.updateValueAndValidity();

      }
      if(this.bookForm.value.saga == null) {
        this.bookForm.controls.saga.setValue(false);
      }
    }
    else {
      this.router.navigate(['/']);
    }
  }

  async onSubmit() {
    if(this.authenticationService.adminAuth()) {
      this.submitted = true;
      const updatedBook = new LibroForm(this.bookForm.value.titolo, this.bookForm.value.sinossi, this.bookForm.value.autori, this.bookForm.value.generi, this.bookForm.value.annoPubblicazione, this.bookForm.value.pagine, this.bookForm.value.saga, this.bookForm.value.titoloSaga, this.bookForm.value.numInSaga);
      const r = this.bookService.updateBook(updatedBook, this.libro.id);
      await r.toPromise().then(async (data) => {
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
          this.updated.emit(updatedBook);
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
    else {
      this.bookForm.reset();
      this.submitted = false;
      this.router.navigate(['/']);
    }
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
    this.authors.push(authorOfBook);
    this.addAuthor = false;
  }
  genreAdded(genre: string) {
    this.genres.push(genre);
    this.addGenre = false;
  }

  backToBookForm() {
    this.addGenre = false;
    this.addAuthor = false;
  }

}
