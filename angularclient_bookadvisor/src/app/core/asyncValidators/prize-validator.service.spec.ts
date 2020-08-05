import {fakeAsync, getTestBed, TestBed, tick} from '@angular/core/testing';

import { PrizeValidatorService } from './prize-validator.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {AsyncValidatorFn, FormControl, ValidationErrors} from "@angular/forms";
import {HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs";

describe('PrizeValidatorService', () => {
  let injector: TestBed;
  let service: PrizeValidatorService;
  let httpMock: HttpTestingController;
  let URL = 'http://localhost:8080/libri';
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PrizeValidatorService]
    })
    injector = getTestBed();
    service = injector.get(PrizeValidatorService);
    httpMock = injector.get(HttpTestingController);

  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('validatePrizeAss() should return null, control.value.length<2', fakeAsync(() => {
    const bookID: number = 1;
    let control: FormControl = new FormControl('p');
    let result: Promise<ValidationErrors>;
    result = <Promise<ValidationErrors>> service.validatePrizeAss(bookID)(control);
    result.then(data => {
      expect(data).toBe(null);
    });
    tick(600);
    httpMock.expectNone(URL + '/' + bookID + '/prizes/isPrizeAssigned');
  }));

  it('validatePrizeAss() should return {\'prizeAssigned\': true}', fakeAsync(() => {
    const bookID: number = 1;
    let control: FormControl = new FormControl('prova');
    let result: Promise<ValidationErrors>;
    result = <Promise<ValidationErrors>> service.validatePrizeAss(bookID)(control);
    result.then(data => {
      expect(data['prizeAssigned']).toBe(true);
    })
    tick(600);
    const req = httpMock.expectOne(URL + '/' + bookID + '/prizes/isPrizeAssigned');
    expect(req.request.method).toBe('POST');
    req.event(new HttpResponse({body: true}));
  }));
});
