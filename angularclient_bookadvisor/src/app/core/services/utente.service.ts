import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

import {Observable} from "rxjs";
import {Utente} from "../model/utente";
import {UtenteForm} from "../model/utente-form";
import {UtenteCard} from "../model/utente-card";
import {UtenteUpdateForm} from "../model/utente-update-form";
import {MyBooks} from "../model/my-books";
import {BookRanking} from "../model/book-ranking";

@Injectable({
  providedIn: 'root'
})
export class UtenteService {

  URL = 'http://localhost:8080/utenti';

  constructor(private http: HttpClient) {
  }

  public findAll(): Promise<UtenteCard[]> {
    return this.http.get<UtenteCard[]>(this.URL, {withCredentials: true}).toPromise();
  }

  public addNewUser(utenteForm: UtenteForm): Observable<object>{
    return this.http.post<object>(this.URL, utenteForm, {withCredentials: true});
  }

  public getSingleUserById(id: number): Promise<Utente> {
    return this.http.get<Utente>(this.URL + "/" + id, {withCredentials: true}).toPromise();
  }

  public updateUser(utente: UtenteUpdateForm, id: number): Observable<object> {
    return this.http.put<object>(this.URL + "/" + id, utente, {withCredentials: true});
  }

  public deleteUser(id: number): Promise<void> {
    return this.http.delete<void>(this.URL + "/" + id, {withCredentials: true}).toPromise();
  }

  public uploadProfilePhoto(profilePhoto: any, id: number): Promise<any> {
    const uploadData = new FormData();
    uploadData.append('fotoProfilo', profilePhoto, profilePhoto.name)
    return this.http.post(this.URL + "/" + id + '/foto_profilo', uploadData, {withCredentials: true}).toPromise();
  }
}
