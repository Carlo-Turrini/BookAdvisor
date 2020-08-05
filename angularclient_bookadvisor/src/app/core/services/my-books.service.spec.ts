import {getTestBed, TestBed} from '@angular/core/testing';

import { MyBooksService } from './my-books.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {HttpResponse} from "@angular/common/http";

describe('MyBooksService', () => {
  let injector: TestBed;
  let service: MyBooksService;
  let httpMock: HttpTestingController;
  const URL = "http://localhost:8080/utenti";
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [MyBooksService]
    });
    injector = getTestBed();
    service = injector.get(MyBooksService);
    httpMock = injector.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('deleteFromMyBooks() should return a Promise<boolean>', () => {
    const userID: number = 1;
    const bookID: number = 1;
    const dummyResult = true;

    service.deleteBookFromMyBooks(bookID, userID).then(bool => {
      expect(bool).toEqual(dummyResult);
    });

    const req = httpMock.expectOne(URL + '/' + userID + '/myBooks/' + bookID);
    expect(req.request.method).toBe('DELETE');
    req.event(new HttpResponse<boolean>({body: dummyResult}));
  });

  it('addMyBookForUser() should return a Promise<string>', () => {
    const userID: number = 1;
    const bookID: number = 1;
    const shelf: string = 'read';
    const dummyResultShelf = 'read';

    service.addMyBookForUser(bookID, userID, shelf).then(shelfResult => {
      expect(shelfResult).toEqual(dummyResultShelf);
    });

    const req = httpMock.expectOne(URL + '/' + userID + '/myBooks/' + bookID);
    expect(req.request.method).toBe('POST');
    req.flush(dummyResultShelf);
  });

  it('updateMyBook() should return a Promise<boolean>', () => {
    const userID: number = 1;
    const bookID: number = 1;
    const shelf: string = 'reading';
    const dummyResult = true;

    service.updateMyBook(bookID, userID, shelf).then(result => {
      expect(result).toEqual(dummyResult);
    });

    const req = httpMock.expectOne(URL + '/' + userID + '/myBooks/' + bookID);
    expect(req.request.method).toBe('PUT');
    req.event(new HttpResponse<boolean>({body: dummyResult}));
  });

  it('getAllUsersMyBooks() should return a Promise<MyBooks[]>', () => {
    const userID: number = 1;
    const dummyMyBooks  = [
      {id: 1, titolo: 'titolo1', authors: [{id: 1, authorsFullname: 'autore1'}], genres: ['Romanzo'], overallRating: 4, coverImage: 'image', shelf: 'read', bookID: 1, userID: userID},
      {id: 2, titolo: 'titolo2', authors: [{id: 1, authorsFullname: 'autore1'}], genres: ['Romanzo'], overallRating: 4, coverImage: 'image', shelf: 'reading', bookID: 2, userID: userID}
    ];

    service.getAllUsersMyBooks(userID).then(myBooks => {
      expect(myBooks.length).toBe(2);
      expect(myBooks).toEqual(dummyMyBooks);
    });

    const req = httpMock.expectOne(URL + '/' + userID + '/myBooks');
    expect(req.request.method).toBe('GET');
    req.flush(dummyMyBooks);
  });

  it('getAllUsersMyBooksRead() should return a Promise<MyBooksRead[]>', () => {
    const userID: number = 1;
    const dummyMyBooksRead = [
      {myBooksID: 1, title: 'titolo1'},
      {myBooksID: 2, title: 'titolo2'}
    ];

    service.getAllUsersMyBooksRead(userID).then(myBooksRead => {
      expect(myBooksRead.length).toBe(2);
      expect(myBooksRead).toEqual(dummyMyBooksRead);
    });

    const req = httpMock.expectOne(URL + '/' + userID + '/myBooks/booksReadNotInRank');
    expect(req.request.method).toBe('GET');
    req.flush(dummyMyBooksRead);

  });
});
