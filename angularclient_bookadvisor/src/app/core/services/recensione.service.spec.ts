import {getTestBed, TestBed} from '@angular/core/testing';

import { RecensioneService } from './recensione.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {RecensioneForm} from "../model/recensione-form";
import {AuthorOfBook} from "../model/author-of-book";

describe('RecensioneService', () => {
  let injector: TestBed;
  let service: RecensioneService;
  let httpMock: HttpTestingController;
  const URL = 'http://localhost:8080/libri';
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [RecensioneService]
    });
    injector = getTestBed();
    service = injector.get(RecensioneService);
    httpMock = injector.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('newReview() should return Observable<object>', () => {
    const bookID: number = 1;
    const dummyObject = {};
    const recensioneForm: RecensioneForm = new RecensioneForm(4, 4, 4, 4, false, 'testo');

    service.newReview(bookID, recensioneForm).subscribe(object => {
      expect(object).toEqual(dummyObject);
    });

    const req = httpMock.expectOne(URL + '/' + bookID + '/recensioni');
    expect(req.request.method).toBe('POST');
    req.flush(dummyObject);
  });

  it('deleteReview() should return a Promise<void>', () => {
    const userId: number = 1;
    const reviewId: number = 1;

    service.deleteReview(userId, reviewId);

    const req = httpMock.expectOne('http://localhost:8080/utenti/' + userId + '/recensioni/' + reviewId);
    expect(req.request.method).toBe('DELETE');
  });

  it('allReviewsByUser() should return a Promise<Recensione[]>', () => {
    const userID: number = 1;
    const dummyRecensioni = [
      {id: 1, testo: 'testo1', rating: 4, originalityRating: 4, writingQualityRating: 4, pageTurnerRating: 4, containsSpoilers: false, numOfUsersConsideredReviewUseful: 12, reviewUsefulForLoggedUser: true, timestamp: '03-03-2020', userId: userID, username: 'username', bookId: 1, titolo: 'titolo1', autori: [{id: 1, authorsFullname: 'autore1'}], profileImage: 'profileImage', coverImage: 'coverImage'},
      {id: 2, testo: 'testo2', rating: 4, originalityRating: 4, writingQualityRating: 4, pageTurnerRating: 4, containsSpoilers: false, numOfUsersConsideredReviewUseful: 12, reviewUsefulForLoggedUser: false, timestamp: '03-03-2020', userId: userID, username: 'username', bookId: 2, titolo: 'titolo2', autori: [{id: 1, authorsFullname: 'autore1'}], profileImage: 'profileImage', coverImage: 'coverImage'}
    ];

    service.allReviewsByUser(userID).then(recensioni => {
      expect(recensioni.length).toBe(2);
      expect(recensioni).toEqual(dummyRecensioni);
    });

    const req = httpMock.expectOne('http://localhost:8080/utenti/' + userID + '/recensioni');
    expect(req.request.method).toBe('GET');
    req.flush(dummyRecensioni);
  });

  it('allReviewsByBook() should return a Promise<Recensione[]>', () => {
    const bookID: number = 1;
    const dummyRecensioni = [
      {id: 1, testo: 'testo1', rating: 4, originalityRating: 4, writingQualityRating: 4, pageTurnerRating: 4, containsSpoilers: false, numOfUsersConsideredReviewUseful: 12, reviewUsefulForLoggedUser: true, timestamp: '03-03-2020', userId: 1, username: 'username', bookId: bookID, titolo: 'titolo1', autori: [{id: 1, authorsFullname: 'autore1'}], profileImage: 'profileImage', coverImage: 'coverImage'},
      {id: 2, testo: 'testo2', rating: 4, originalityRating: 4, writingQualityRating: 4, pageTurnerRating: 4, containsSpoilers: false, numOfUsersConsideredReviewUseful: 12, reviewUsefulForLoggedUser: false, timestamp: '03-03-2020', userId: 2, username: 'username', bookId: bookID, titolo: 'titolo1', autori: [{id: 1, authorsFullname: 'autore1'}], profileImage: 'profileImage', coverImage: 'coverImage'}
    ];

    service.allReviewsByBook(bookID).then(recensioni => {
      expect(recensioni.length).toBe(2);
      expect(recensioni).toEqual(dummyRecensioni);
    });

    const req = httpMock.expectOne(URL + "/" + bookID + '/recensioni');
    expect(req.request.method).toBe('GET');
    req.flush(dummyRecensioni);
  });

  it('addUsefulReview() should return a Promise<void>', () => {
    const userID: number = 1;
    const reviewID: number = 1;

    service.addUsefulReview(reviewID, userID);

    const req = httpMock.expectOne("http://localhost:8080/recensioni/" + reviewID + "/isReviewUseful");
    expect(req.request.method).toBe('POST');
  });

  it('removeUsefulReview() should return a Promise<void>', () => {
    const userID: number = 1;
    const reviewID: number = 1;

    service.removeUsefulReview(reviewID, userID);

    const req = httpMock.expectOne("http://localhost:8080/recensioni/" + reviewID + "/isReviewUseful/" + userID);
    expect(req.request.method).toBe('DELETE');
  });
});
