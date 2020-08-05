import {getTestBed, TestBed} from '@angular/core/testing';

import { AuthorService } from './author.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {AuthorForm} from "../model/author-form";
import {compareNumbers} from "@angular/compiler-cli/src/diagnostics/typescript_version";

describe('AuthorService', () => {
  let injector: TestBed;
  let service: AuthorService;
  let httpMock: HttpTestingController;
  const URL = "http://localhost:8080/authors";
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthorService]
    });
    injector = getTestBed();
    service = injector.get(AuthorService);
    httpMock = injector.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getAllAuthors() should return a Promise<AuthorCard[]>', () => {
    const dummyAuthorCards = [
      {id: 1, authorsFullname: 'autore1', birthYear: 1923, deathYear: null, authorsPhoto: 'photo'},
      {id: 2, authorsFullname: 'autore2', birthYear: 1910, deathYear: 1945, authorsPhoto: 'photo'},
      {id: 3, authorsFullname: 'autore3', birthYear: 1930, deathYear: 1999, authorsPhoto: 'photo'}
    ];

    service.getAllAuthors().then(autori => {
      expect(autori.length).toBe(3);
      expect(autori).toEqual(dummyAuthorCards);
    });

    const req = httpMock.expectOne(URL);
    expect(req.request.method).toBe('GET');
    req.flush(dummyAuthorCards);
  });

  it('addNewAuthor() should return a Observable<object>', () => {
    const dummyObject = {titolo: ['titoloTaken']};
    const authorForm: AuthorForm = new AuthorForm('Prova', 1933, 2002, 'biografia');
    service.addNewAuthor(authorForm).subscribe(data => {
      expect(data).toEqual(dummyObject);
    });

    const req = httpMock.expectOne(URL);
    expect(req.request.method).toBe('POST');
    req.flush(dummyObject);
  });

  it('updateAuthor() should return a Observable<object>', () => {
    const dummyObject = {titolo: ['titoloTaken']};
    const authorForm: AuthorForm = new AuthorForm('Prova', 1933, 2002, 'biografia');
    const authorID: number = 1;
    service.updateAuthor(authorForm, authorID).subscribe(data => {
      expect(data).toEqual(dummyObject);
    });

    const req = httpMock.expectOne(URL + '/' + authorID);
    expect(req.request.method).toBe('PUT');
    req.flush(dummyObject);
  });

  it('deleteAuthor should return a Promise<void>', () => {
    const authorID: number = 1;
    service.deleteAuthor(authorID);

    const req = httpMock.expectOne(URL + '/' + authorID);
    expect(req.request.method).toBe('DELETE');
  })

  it('getAuthor() should return a Promise<Author>', () => {
    const authorID: number = 1;
    const dummyAuthor = {id: 1, authorsFullname: 'nome', birthYear: 1923, deathYear: null, biography: 'biografia', authorsPhoto: 'photo'};
    service.getAuthor(authorID).then(autore => {
      expect(autore).toEqual(dummyAuthor);
    })

    const req = httpMock.expectOne(URL + '/' + authorID);
    expect(req.request.method).toBe('GET');
    req.flush(dummyAuthor);
  });

  it( 'uploadAuthorsPhoto() should return a Promise<any>', () => {
    const authorID: number = 1;
    const dummyAny = { img: 'immagine'};
    const image: File = new File([], 'immagine.png');
    service.uploadAuthorsPhoto(authorID, image).then(data => {
      expect(data).toEqual(dummyAny);
    });

    const req = httpMock.expectOne(URL + '/' + authorID + '/authors_photo');
    expect(req.request.method).toBe('POST');
    req.flush(dummyAny);
  });

  it('getAllAuthorsForBookForm() should return a Promise<AuthorOfBook[]>', () => {
    const dummyAuthorsOfBook = [
      {id: 1, authorsFullname: 'autore1'},
      {id: 2, authorsFullname: 'autore2'}
    ];

    service.getAllAuthorsForBookForm().then(authorsOfBook => {
      expect(authorsOfBook.length).toBe(2);
      expect(authorsOfBook).toEqual(dummyAuthorsOfBook);
    });

    const req = httpMock.expectOne(URL + '/authorsForBookForm');
    expect(req.request.method).toBe('GET');
    req.flush(dummyAuthorsOfBook);
  });

  it('getAuthorOfBookFromFullname() should return a Promise<AuthorOfBook>', () => {
    const fullName: string = 'autore1';
    const dummyAuthorOfBook = {id: 1, authorsFullname: fullName};

    service.getAuthorOfBookFromFullname(fullName).then(authorOfBook => {
      expect(authorOfBook).toEqual(dummyAuthorOfBook);
    });

    const req = httpMock.expectOne(URL + '/byName/' + fullName);
    expect(req.request.method).toBe('GET');
    req.flush(dummyAuthorOfBook);
  })

});
