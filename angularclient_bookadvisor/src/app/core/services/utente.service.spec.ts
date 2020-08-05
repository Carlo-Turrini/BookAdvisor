import {getTestBed, TestBed} from '@angular/core/testing';

import { UtenteService } from './utente.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {UtenteForm} from "../model/utente-form";
import {UtenteUpdateForm} from "../model/utente-update-form";

describe('UtenteService', () => {
  let injector: TestBed;
  let service: UtenteService;
  let httpMock: HttpTestingController;
  const URL = 'http://localhost:8080/utenti';
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UtenteService]
    });
    injector = getTestBed();
    service = injector.get(UtenteService);
    httpMock = injector.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('findAll() should return a Promise<UtenteCard[]>', () => {
    const dummyUtentiCard = [
      {id: 1, username: 'Username1', nome: 'Nome1', cognome: 'Cognome1', profileImage: 'image'},
      {id: 2, username: 'Username2', nome: 'Nome2', cognome: 'Cognome2', profileImage: 'image'}
    ];

    service.findAll().then(utentiCard => {
      expect(utentiCard.length).toBe(2);
      expect(utentiCard).toEqual(dummyUtentiCard);
    });

    const req = httpMock.expectOne(URL);
    expect(req.request.method).toBe('GET');
    req.flush(dummyUtentiCard);
  });

  it('addNewUser() should return an Observable<object>', () => {
    const dummyObject = {username: ['usernameTaken'], email: ['emailTaken']};
    const utenteForm: UtenteForm = new UtenteForm('Nome', 'Cognome', 'Username', 'password', 'n.c@gmail.com', 'descrizione');

    service.addNewUser(utenteForm).subscribe(object => {
      expect(object).toEqual(dummyObject);
    });

    const req = httpMock.expectOne(URL);
    expect(req.request.method).toBe('POST');
    req.flush(dummyObject);
  });

  it('updateUser() should return an Observable<object>', () => {
    const dummyObject = {email: ['emailTaken']};
    const utenteForm: UtenteUpdateForm = new UtenteUpdateForm('Nome', 'Cognome', 'password', 'n.c@gmail.com', 'descrizione');
    const userID: number = 1;

    service.updateUser(utenteForm, userID).subscribe(object => {
      expect(object).toEqual(dummyObject);
    });

    const req = httpMock.expectOne(URL + '/' + userID);
    expect(req.request.method).toBe('PUT');
    req.flush(dummyObject);
  });

  it('getSingleUserById() should return a Promise<Utente>', () => {
    const userID: number = 1;
    const dummyUtente = {  id: userID, name: 'Nome', surname: 'Cognome', username: 'Username', email: 'n.c@gmail.com', profilePhoto: 'image', description: 'descrizione'};

    service.getSingleUserById(userID).then(utente => {
      expect(utente).toEqual(dummyUtente);
    });

    const req = httpMock.expectOne(URL + '/' + userID);
    expect(req.request.method).toBe('GET');
    req.flush(dummyUtente);
  });

  it('deleteUser() should return a Promise<void>', () => {
    const userID: number = 1;

    service.deleteUser(userID);

    const req = httpMock.expectOne(URL + '/' + userID);
    expect(req.request.method).toBe('DELETE');
  });

  it('uploadProfilePhoto() should return a Promise<any>', () => {
    const userID: number = 1;
    const dummyAny = { img: 'immagine'};
    const image: File = new File([], 'immagine.png');

    service.uploadProfilePhoto(image, userID).then(any => {
      expect(any).toEqual(dummyAny);
    });

    const req = httpMock.expectOne(URL + '/' + userID + '/foto_profilo');
    expect(req.request.method).toBe('POST');
    req.flush(dummyAny);
  })
});
