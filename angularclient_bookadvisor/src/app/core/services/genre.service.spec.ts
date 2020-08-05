import {getTestBed, TestBed} from '@angular/core/testing';

import { GenreService } from './genre.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {GenreForm} from "../model/genre-form";

describe('GenreService', () => {
  let injector: TestBed;
  let service: GenreService;
  let httpMock: HttpTestingController;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [GenreService]
    })
    injector = getTestBed();
    service = injector.get(GenreService);
    httpMock = injector.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getAllGenres() should return a Promise<string[]>', () => {
    const dummyGenres = [
      "Romanzo",
      "Fantasy"
    ];

    service.getAllGenres().then(genres => {
      expect(genres.length).toBe(2);
      expect(genres).toEqual(dummyGenres);
    });

    const req = httpMock.expectOne('http://localhost:8080/genres');
    expect(req.request.method).toBe('GET');
    req.flush(dummyGenres);
  })

  it('addGenre() should return a Promise<string[]>', () => {
    const dummyGenres = [
      "Romanzo",
      "Fantasy"
    ];
    const genreForm: GenreForm = new GenreForm('Romanzo storico');

    service.addGenre(genreForm).then(genres => {
      expect(genres.length).toBe(2);
      expect(genres).toEqual(dummyGenres);
    });

    const req = httpMock.expectOne('http://localhost:8080/genres');
    expect(req.request.method).toBe('POST');
    req.flush(dummyGenres);
  });
});
