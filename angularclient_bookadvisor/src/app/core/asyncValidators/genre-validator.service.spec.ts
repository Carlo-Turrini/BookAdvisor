import {fakeAsync, getTestBed, TestBed, tick} from '@angular/core/testing';

import { GenreValidatorService } from './genre-validator.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {FormControl} from "@angular/forms";
import {HttpResponse} from "@angular/common/http";

describe('GenreValidatorService', () => {
  let injector: TestBed;
  let service: GenreValidatorService;
  let httpMock: HttpTestingController;
  let URL = 'http://localhost:8080/genres';
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [GenreValidatorService]
    })
    injector = getTestBed();
    service = injector.get(GenreValidatorService);
    httpMock = injector.get(HttpTestingController);

  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('validateGenreUniqueness() should return null, control.value.length<2', fakeAsync(() => {
    let control: FormControl = new FormControl('p');
    service.validateGenreUniqueness(control).then(data => {
      expect(data).toBe(null);
    });
    tick(600);
    httpMock.expectNone(URL + '/isGenreUnique');
  }));

  it('validateGenreUniqueness() should return {\'genreTaken\': true}', fakeAsync(() => {
    let control: FormControl = new FormControl('prova');
    service.validateGenreUniqueness(control).then(data => {
      expect(data['genreTaken']).toBe(true);
    });
    tick(600);
    const req = httpMock.expectOne(URL + '/isGenreUnique');
    expect(req.request.method).toBe('POST');
    req.event(new HttpResponse({body: false}));
  }));
});
