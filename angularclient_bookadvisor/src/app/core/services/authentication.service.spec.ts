import {async, fakeAsync, flushMicrotasks, getTestBed, TestBed, tick} from '@angular/core/testing';

import { AuthenticationService } from './authentication.service';
import {AuthorService} from "./author.service";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {HttpResponse} from "@angular/common/http";

describe('AuthenticationService', () => {
  let injector: TestBed;
  let service: AuthenticationService;
  let httpMock: HttpTestingController;
  const URL = 'http://localhost:8080';
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthenticationService]
    });
    injector = getTestBed();
    service = injector.get(AuthenticationService);
    httpMock = injector.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('loginUser() should be successful',  fakeAsync(() => {
    const username: string = 'username';
    const password: string = 'password';
    const dummyLoggedUser = {id: 1, admin: true};

    service.loginUser(username, password).then(result => {
      expect(result).toBe(true);
    });
    const req1 = httpMock.expectOne(URL + '/authenticate');
    expect(req1.request.method).toBe('POST');
    req1.flush({}, {status: 200, statusText: 'OK'})
    flushMicrotasks();
    const req2 = httpMock.expectOne(URL + '/utenti/loggedUserInfo');
    expect(req2.request.method).toBe('GET');
    req2.flush(dummyLoggedUser);
    flushMicrotasks();
    expect(service.logged).toBe(true);
    expect(service.loggedUser.id).toBe(1);
    expect(service.loggedUser.admin).toBe(true);
  }));


  it('loginUser() should be unsuccessful',  fakeAsync(() => {
    const username: string = 'username';
    const password: string = 'password';

    service.loginUser(username, password).then(result => {
      expect(result).toBe(false);
    });
    const req1 = httpMock.expectOne(URL + '/authenticate');
    expect(req1.request.method).toBe('POST');
    req1.flush({}, {status: 401, statusText: 'UNAUTHORIZED'})
    flushMicrotasks();
    const req2 = httpMock.expectNone(URL + '/utenti/loggedUserInfo');
    expect(service.logged).toBe(false);
    expect(service.loggedUser.id).toBe(null);
    expect(service.loggedUser.admin).toBe(false);
  }));

  it('logoutUser() should logout user', () => {
    service.logged = true;
    service.loggedUser.id= 1;
    service.loggedUser.admin= true;

    service.logoutUser().then(value => {
      expect(value).toEqual(true);
    });

    const req = httpMock.expectOne(URL + '/logoutUser');
    expect(req.request.method).toBe('GET');
    req.flush({}, {status: 200, statusText: 'OK'});
    expect(service.logged).toBe(false);
    expect(service.loggedUser.id).toBe(null);
    expect(service.loggedUser.admin).toBe(false);
  });
});
