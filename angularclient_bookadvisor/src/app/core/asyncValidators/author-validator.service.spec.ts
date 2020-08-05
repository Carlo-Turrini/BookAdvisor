import {fakeAsync, getTestBed, TestBed, tick} from '@angular/core/testing';

import { AuthorValidatorService } from './author-validator.service';
import {GenreService} from "../services/genre.service";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {AbstractControl, FormControl} from "@angular/forms";
import {HttpResponse} from "@angular/common/http";

describe('AuthorValidatorService', () => {
  let injector: TestBed;
  let service: AuthorValidatorService;
  let httpMock: HttpTestingController;
  let URL = 'http://localhost:8080/authors';
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthorValidatorService]
    });
    injector = getTestBed();
    service = injector.get(AuthorValidatorService);
    httpMock = injector.get(HttpTestingController);

  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('validateFullname() should return null, control.value.length<2', fakeAsync(() => {
    let control: FormControl = new FormControl('p');
    service.validateFullname(control).then(data => {
      expect(data).toBe(null);
    });
    tick(600);
    httpMock.expectNone(URL + '/isFullnameUnique');
  }));

  it('validateFullname() should return {\'fullnameTaken\': true}', fakeAsync(() => {
    let control: FormControl = new FormControl('prova');
    service.validateFullname(control).then(data => {
      expect(data['fullnameTaken']).toBe(true);
    });
    tick(600);
    const req = httpMock.expectOne(URL + '/isFullnameUnique');
    expect(req.request.method).toBe('POST');
    req.event(new HttpResponse({body: false}));
  }));
});
