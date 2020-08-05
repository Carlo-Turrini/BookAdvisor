import {getTestBed, TestBed} from '@angular/core/testing';

import { LibroService } from './libro.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {Libro} from "../model/libro";
import {AuthorOfBook} from "../model/author-of-book";
import {LibroForm} from "../model/libro-form";
import {PrizeForm} from "../model/prize-form";

describe('LibroService', () => {
  let injector: TestBed;
  let service: LibroService;
  let httpMock: HttpTestingController;
  const URL = 'http://localhost:8080/libri';
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [LibroService]
    });
    injector = getTestBed();
    service = injector.get(LibroService);
    httpMock = injector.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('findAllBooks() should return a Promise<LibroCard[]>', () => {
    const dummyLibriCard = [
      {id: 1, titolo: 'Titolo1', autori: [{id: 1, authorsFullname: 'autore1'}], generi: ["Romanzo"], overallRating: 4, coverImage: 'image'},
      {id: 2, titolo: 'Titolo2', autori: [{id: 1, authorsFullname: 'autore1'}], generi: ["Romanzo"], overallRating: 4, coverImage: 'image'}
    ];

    service.findAllBooks().then(libriCard => {
      expect(libriCard.length).toBe(2);
      expect(libriCard).toEqual(dummyLibriCard);
    });

    const req = httpMock.expectOne(URL);
    expect(req.request.method).toBe('GET');
    req.flush(dummyLibriCard);
  });

  it('findAllBooksByGenre() should return a Promise<LibroCard[]>', () => {
    const genre: string = "Romanzo";
    const dummyLibriCard = [
      {id: 1, titolo: 'Titolo1', autori: [{id: 1, authorsFullname: 'autore1'}], generi: [genre], overallRating: 4, coverImage: 'image'},
      {id: 2, titolo: 'Titolo2', autori: [{id: 1, authorsFullname: 'autore1'}], generi: [genre], overallRating: 4, coverImage: 'image'}
    ];


    service.findAllBooksByGenre(genre).then(libriCard => {
      expect(libriCard.length).toBe(2);
      expect(libriCard).toEqual(dummyLibriCard);
    });

    const req = httpMock.expectOne(URL + '?genere=' + genre);
    expect(req.request.method).toBe('GET');
    req.flush(dummyLibriCard);
  });

  it('findAllBooks() should return a Promise<LibroCard[]>', () => {
    const titolo: string = 'Titolo';
    const dummyLibriCard = [
      {id: 1, titolo: 'Titolo1', autori: [{id: 1, authorsFullname: 'autore1'}], generi: ["Romanzo"], overallRating: 4, coverImage: 'image'},
      {id: 2, titolo: 'Titolo2', autori: [{id: 1, authorsFullname: 'autore1'}], generi: ["Romanzo"], overallRating: 4, coverImage: 'image'}
    ];

    service.findAllBooksContainingTitolo(titolo).then(libriCard => {
      expect(libriCard.length).toBe(2);
      expect(libriCard).toEqual(dummyLibriCard);
    });

    const req = httpMock.expectOne(URL + '?titolo=' + titolo);
    expect(req.request.method).toBe('GET');
    req.flush(dummyLibriCard);
  });

  it('findAllBooksByGenre() should return a Promise<LibroCard[]>', () => {
    const author: string = "autore1";
    const dummyLibriCard = [
      {id: 1, titolo: 'Titolo1', autori: [{id: 1, authorsFullname: author}], generi: ['Romanzo'], overallRating: 4, coverImage: 'image'},
      {id: 2, titolo: 'Titolo2', autori: [{id: 1, authorsFullname: author}], generi: ['Romanzo'], overallRating: 4, coverImage: 'image'}
    ];


    service.findAllBooksByAuthor(author).then(libriCard => {
      expect(libriCard.length).toBe(2);
      expect(libriCard).toEqual(dummyLibriCard);
    });

    const req = httpMock.expectOne(URL + '?author=' + author);
    expect(req.request.method).toBe('GET');
    req.flush(dummyLibriCard);
  });

  it('getBooksInSaga() should return a Promise<LibroCard[]>', () => {
    const saga: string = 'Saga';
    const bookID: number = 3;
    const dummyLibriCard = [
      {id: 1, titolo: 'Titolo1', autori: [{id: 1, authorsFullname: 'autore1'}], generi: ["Romanzo"], overallRating: 4, coverImage: 'image'},
      {id: 2, titolo: 'Titolo2', autori: [{id: 1, authorsFullname: 'autore1'}], generi: ["Romanzo"], overallRating: 4, coverImage: 'image'}
    ];

    service.getBooksInSaga(saga, bookID).then(libriCard => {
      expect(libriCard.length).toBe(2);
      expect(libriCard).toEqual(dummyLibriCard);
    });

    const req = httpMock.expectOne(URL + '?titoloSaga=' + saga + '&bookId=' + bookID);
    expect(req.request.method).toBe('GET');
    req.flush(dummyLibriCard);
  });

  it('newBook() should return an Observable<object>', () => {
    let authorsOfBook: AuthorOfBook[] = [];
    let author: AuthorOfBook = new AuthorOfBook(1, 'autore');
    authorsOfBook.push(author);
    let generi: string[] = [];
    generi.push("Romanzo");
    const libroForm: LibroForm = new LibroForm('Titolo', 'sinossi', authorsOfBook, generi, 1234, 123, false, '', null);
    const dummyObject = {titolo: ['titoloTaken']};

    service.newBook(libroForm).subscribe(object => {
      expect(object).toEqual(dummyObject);
    });

    const req = httpMock.expectOne(URL);
    expect(req.request.method).toBe('POST');
    req.flush(dummyObject);
  });


  it('updateBook() should return an Observable<object>', () => {
    let authorsOfBook: AuthorOfBook[] = [];
    let author: AuthorOfBook = new AuthorOfBook(1, 'autore');
    authorsOfBook.push(author);
    let generi: string[] = [];
    generi.push("Romanzo");
    const bookID: number = 1;
    const libroForm: LibroForm = new LibroForm('Titolo', 'sinossi', authorsOfBook, generi, 1234, 123, false, '', null);
    const dummyObject = {titolo: ['titoloTaken']};

    service.updateBook(libroForm, bookID).subscribe(object => {
      expect(object).toEqual(dummyObject);
    });

    const req = httpMock.expectOne(URL + '/' + bookID);
    expect(req.request.method).toBe('PUT');
    req.flush(dummyObject);
  });

  it('findBook() should return a Promise<Libro>', () => {
    const bookID: number = 1;
    const dummyLibro = {id: bookID, annoPubblicazione: 2002, pagine: 123, titolo: 'Titolo', sinossi: 'sinossi', autori: [{id: 1, authorsFullname: 'autore1'}], copertina: 'image', saga: true, titoloSaga: 'saga', numInSaga: 1, generi: ['Romanzo'], overallRating: 4, overallWritingQualityRating: 4, overallPageTurnerRating: 4, overallOriginalityRating: 4, bookShelf: 'read'};

    service.findBook(bookID).then(book => {
      expect(book).toEqual(dummyLibro);
    });

    const req = httpMock.expectOne(URL + '/' + bookID);
    expect(req.request.method).toBe('GET');
    req.flush(dummyLibro);
  });

  it('deleteBook() should return a Promise<void>', () => {
    const bookID: number = 1;

    service.deleteBook(bookID);

    const req = httpMock.expectOne(URL + '/' + bookID);
    expect(req.request.method).toBe('DELETE');
  });

  it('uploadCoverPhoto() should return a Promise<any>', () => {
    const bookID: number = 1;
    const dummyAny = { img: 'immagine'};
    const image: File = new File([], 'immagine.png');

    service.uploadCoverPhoto(image, bookID).then(any => {
      expect(any).toEqual(dummyAny);
    });

    const req = httpMock.expectOne(URL + '/' + bookID + '/foto_copertina');
    expect(req.request.method).toBe('POST');
    req.flush(dummyAny);
  });

  it('getBookOverallRating() should return a Promise<OverallRatingsForBook>', () => {
    const bookID: number = 1;
    const dummyRatings = {overallRating: 4, overallOriginalityRating: 4, overallWritingQualityRating: 4, overallPageTurnerRating: 4};

    service.getBookOverallRating(bookID).then(ratings => {
      expect(ratings).toEqual(dummyRatings);
    });

    const req = httpMock.expectOne(URL + '/' + bookID + '/overallRatings');
    expect(req.request.method).toBe('GET');
    req.flush(dummyRatings);
  });

  it('getBooksPrizes() should return a Promise<Prize[]>', () => {
    const bookID: number = 1;
    const dummyPrizes = [
      {id: 1, yearAwarded: 2002, prizeName: 'Premio1'},
      {id: 2, yearAwarded: 2002, prizeName: 'Premio2'}
    ];

    service.getBooksPrizes(bookID).then(prizes => {
      expect(prizes.length).toBe(2);
      expect(prizes).toEqual(dummyPrizes);
    });

    const req = httpMock.expectOne(URL + '/' + bookID + '/prizes');
    expect(req.request.method).toBe('GET');
    req.flush(dummyPrizes);
  });

  it('addPrizeToBook() should return an Observable<object>', () => {
    const bookID: number = 1;
    const dummyObject = {prizeName: ['prizeTaken']};
    const prizeForm: PrizeForm = new PrizeForm(2002, 'Premio1');

    service.addPrizeToBook(prizeForm, bookID).subscribe(object => {
      expect(object).toEqual(dummyObject);
    });

    const req = httpMock.expectOne(URL + '/' + bookID + '/prizes');
    expect(req.request.method).toBe('POST');
    req.flush(dummyObject);
  });

  it('deleteBooksPrize() should return a Promise<void>', () => {
    const bookID: number = 1;
    const prizeID: number = 1;

    service.deleteBooksPrize(prizeID, bookID);

    const req = httpMock.expectOne(URL + '/' + bookID + '/prizes/' + prizeID);
    expect(req.request.method).toBe('DELETE');
  });

  it('getPrizeOfBook() should return a Promise<Prize>', () => {
    const bookID: number = 1;
    const prizeName: string = 'Premio1';
    const dummyPrize = {id: 1, yearAwarded: 2002, prizeName: prizeName};

    service.getPrizeOfBook(prizeName, bookID).then(prize => {
      expect(prize).toEqual(dummyPrize);
    });

    const req = httpMock.expectOne(URL + '/' + bookID + '/prizes/' + prizeName);
    expect(req.request.method).toBe('GET');
    req.flush(dummyPrize);
  });

});
