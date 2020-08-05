import {getTestBed, TestBed} from '@angular/core/testing';

import { XsrfInterceptorService } from './xsrf-interceptor.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {HTTP_INTERCEPTORS, HttpClient} from "@angular/common/http";
import {GenreService} from "../services/genre.service";
import {CookieService} from "ngx-cookie-service";

describe('XsrfInterceptorService', () => {
  let injector: TestBed;
  let service: GenreService;
  let httpMock: HttpTestingController;
  let cookieServiceMock = jasmine.createSpyObj<CookieService>('CookieService', ['set']);

  const URL = 'http://localhost:8080/genres';
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [GenreService, {
        provide: HTTP_INTERCEPTORS,
        useClass: XsrfInterceptorService,
        multi: true
      }, {provide: CookieService, useValue: cookieServiceMock}]
    });
    injector = getTestBed();
    service = injector.get(GenreService);
    httpMock = injector.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should intercept HTTP requests', () => {
    const dummyGenres = [
      "Romanzo",
      "Fantasy"
    ];
    service.getAllGenres().then(response => {expect(response).toBeTruthy()});
    const req = httpMock.expectOne('http://localhost:8080/genres');
    expect(req.request.headers.has('X-XSRF-TOKEN')).toBe(true);
    req.flush(dummyGenres);

  })
});
