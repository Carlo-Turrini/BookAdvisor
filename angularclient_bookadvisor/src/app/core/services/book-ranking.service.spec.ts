import {getTestBed, TestBed} from '@angular/core/testing';

import { BookRankingService } from './book-ranking.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {AuthorOfBook} from "../model/author-of-book";

describe('BookRankingService', () => {
  let injector: TestBed;
  let service: BookRankingService;
  let httpMock: HttpTestingController;
  const URL = "http://localhost:8080/utenti";
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BookRankingService]
    });
    injector = getTestBed();
    service = injector.get(BookRankingService);
    httpMock = injector.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('addBookToRanking() should return a Promise<BookRanking[]>', () => {
    const userID = 1;
    const dummyBookRanking = [
      {id: 1, bookID: 1, bookRank: 1, bookTitle: 'titolo', bookCoverPhoto: 'photo', bookAuthors: [{id: 1, authorsFullname: 'autore'}]},
      {id: 2, bookID: 2, bookRank: 2, bookTitle: 'titolo1', bookCoverPhoto: 'photo', bookAuthors: [{id: 1, authorsFullname: 'autore'}]}
    ];

    service.addBookToRanking(userID, 1, 1).then(bookRanking => {
      expect(bookRanking.length).toBe(2);
      expect(bookRanking).toEqual(dummyBookRanking);
    });

    const req = httpMock.expectOne(URL + '/' + userID + '/bookRank');
    expect(req.request.method).toBe('POST');
    req.flush(dummyBookRanking);
  });

  it('deleteBookFromRanking() should return a Promise<BookRanking[]>', () => {
    const userID = 1;
    const rankID = 3;
    const dummyBookRanking = [
      {id: 1, bookID: 1, bookRank: 1, bookTitle: 'titolo', bookCoverPhoto: 'photo', bookAuthors: [{id: 1, authorsFullname: 'autore'}]},
      {id: 2, bookID: 2, bookRank: 2, bookTitle: 'titolo1', bookCoverPhoto: 'photo', bookAuthors: [{id: 1, authorsFullname: 'autore'}]}
    ];

    service.deleteBookFromRanking(userID, rankID).then(bookRanking => {
      expect(bookRanking.length).toBe(2);
      expect(bookRanking).toEqual(dummyBookRanking);
    });

    const req = httpMock.expectOne(URL + '/' + userID + '/bookRank/' + rankID);
    expect(req.request.method).toBe('DELETE');
    req.flush(dummyBookRanking);
  });

  it('getUsersBookRanking() should return a Promise<BookRanking[]>', () => {
    const userID = 1;
    const dummyBookRanking = [
      {id: 1, bookID: 1, bookRank: 1, bookTitle: 'titolo', bookCoverPhoto: 'photo', bookAuthors: [{id: 1, authorsFullname: 'autore'}]},
      {id: 2, bookID: 2, bookRank: 2, bookTitle: 'titolo1', bookCoverPhoto: 'photo', bookAuthors: [{id: 1, authorsFullname: 'autore'}]}
    ];

    service.getUsersBookRanking(userID).then(bookRanking => {
      expect(bookRanking.length).toBe(2);
      expect(bookRanking).toEqual(dummyBookRanking);
    });

    const req = httpMock.expectOne(URL + '/' + userID + '/bookRank');
    expect(req.request.method).toBe('GET');
    req.flush(dummyBookRanking);
  });
});
