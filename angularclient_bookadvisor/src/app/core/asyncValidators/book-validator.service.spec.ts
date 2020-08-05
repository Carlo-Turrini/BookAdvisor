import {fakeAsync, getTestBed, TestBed, tick} from '@angular/core/testing';

import { BookValidatorService } from './book-validator.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {FormControl} from "@angular/forms";
import {HttpResponse} from "@angular/common/http";

describe('BookValidatorService', () => {
  let injector: TestBed;
  let service: BookValidatorService;
  let httpMock: HttpTestingController;
  let URL = 'http://localhost:8080/libri';
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BookValidatorService]
    })
    injector = getTestBed();
    service = injector.get(BookValidatorService);
    httpMock = injector.get(HttpTestingController);

  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('validateTitolo() should return null, control.value.length<2', fakeAsync(() => {
    let control: FormControl = new FormControl('p');
    service.validateTitolo(control).then(data => {
      expect(data).toBe(null);
    });
    tick(600);
    httpMock.expectNone(URL + '/isTitleUnique');
  }));

  it('validateTitolo() should return {\'titoloTaken\': true}', fakeAsync(() => {
    let control: FormControl = new FormControl('prova');
    service.validateTitolo(control).then(data => {
      expect(data['titoloTaken']).toBe(true);
    });
    tick(600);
    const req = httpMock.expectOne(URL + '/isTitleUnique');
    expect(req.request.method).toBe('POST');
    req.event(new HttpResponse({body: false}));
  }));
});
