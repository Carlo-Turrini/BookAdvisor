import {fakeAsync, getTestBed, TestBed, tick} from '@angular/core/testing';

import { UserValidatorService } from './user-validator.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {FormControl} from "@angular/forms";
import {HttpResponse} from "@angular/common/http";

describe('UserValidatorService', () => {
  let injector: TestBed;
  let service: UserValidatorService;
  let httpMock: HttpTestingController;
  let URL = 'http://localhost:8080/utenti';
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserValidatorService]
    });
    injector = getTestBed();
    service = injector.get(UserValidatorService);
    httpMock = injector.get(HttpTestingController);

  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('validateUsername() should return null, control.value.length<2', fakeAsync(() => {
    let control: FormControl = new FormControl('p');
    service.validateUsername(control).then(data => {
      expect(data).toBe(null);
    });
    tick(600);
    httpMock.expectNone(URL + '/isUsernameUnique');
  }));

  it('validateUsername() should return {\'fullnameTaken\': true}', fakeAsync(() => {
    let control: FormControl = new FormControl('prova');
    service.validateUsername(control).then(data => {
      expect(data['usernameTaken']).toBe(true);
    });
    tick(600);
    const req = httpMock.expectOne(URL + '/isUsernameUnique');
    expect(req.request.method).toBe('POST');
    req.event(new HttpResponse({body: false}));
  }));

  it('validateEmail() should return null, control.value.length<2', fakeAsync(() => {
    let control: FormControl = new FormControl('p');
    service.validateEmail(control).then(data => {
      expect(data).toBe(null);
    });
    tick(600);
    httpMock.expectNone(URL + '/isEmailUnique');
  }));

  it('validateEmail() should return {\'fullnameTaken\': true}', fakeAsync(() => {
    let control: FormControl = new FormControl('prova@gmail.com');
    service.validateEmail(control).then(data => {
      expect(data['emailTaken']).toBe(true);
    });
    tick(600);
    const req = httpMock.expectOne(URL + '/isEmailUnique');
    expect(req.request.method).toBe('POST');
    req.event(new HttpResponse({body: false}));
  }));
});
