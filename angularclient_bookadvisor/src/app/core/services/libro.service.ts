import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

import {Observable} from "rxjs";
import {Libro} from "../model/libro";
import {LibroForm} from "../model/libro-form";
import {LibroCard} from "../model/libro-card";
import {Prize} from "../model/prize";
import {OverallRatingsForBook} from "../model/overall-ratings-for-book";
import {PrizeForm} from "../model/prize-form";

@Injectable({
  providedIn: 'root'
})
export class LibroService {
  URL = 'http://localhost:8080/api/libri';
  constructor(private http: HttpClient) { }

  public findAllBooks(): Promise<LibroCard[]> {
    return this.http.get<LibroCard[]>(this.URL, {withCredentials: true}).toPromise();
  }

  public findAllBooksByGenre(genere: String): Promise<LibroCard[]> {
    return this.http.get<LibroCard[]>(this.URL + '?genere=' + genere, {withCredentials: true}).toPromise();
  }

  public findAllBooksContainingTitolo(titolo: string): Promise<LibroCard[]> {
    return this.http.get<LibroCard[]>(this.URL + '?titolo=' + titolo, {withCredentials: true}).toPromise();
  }

  public newBook(newBook: LibroForm): Observable<object> {
    return this.http.post<object>(this.URL, newBook, {withCredentials: true});
  }

  public findBook(id: number): Promise<Libro> {
    return this.http.get<Libro>(this.URL + "/" + id, {withCredentials: true}).toPromise();
  }

  public updateBook(book: LibroForm, id: number): Observable<object> {
    return this.http.put<object>(this.URL + "/" + id, book, {withCredentials: true});
  }

  public deleteBook(id: number): Promise<void> {
    return this.http.delete<void>(this.URL + "/" + id, {withCredentials: true}).toPromise();
  }

  public uploadCoverPhoto(coverPhoto: any, id: number): Promise<any> {
    const uploadData = new FormData();
    uploadData.append('copertina', coverPhoto, coverPhoto.name)
    return this.http.post(this.URL + "/" + id + '/foto_copertina', uploadData, {withCredentials: true}).toPromise();
  }

  public getBookOverallRating(id: number): Promise<OverallRatingsForBook> {
    return this.http.get<OverallRatingsForBook>(this.URL + "/" + id + "/overallRatings",{withCredentials: true}).toPromise();
  }

  public getBooksInSaga(titoloSaga: string, id: number): Promise<LibroCard[]> {
    return this.http.get<LibroCard[]>(this.URL + "?titoloSaga=" + titoloSaga + "&bookId=" + id, {withCredentials: true}).toPromise();
  }

  public getBooksPrizes(id: number): Promise<Prize[]> {
    return this.http.get<Prize[]>(this.URL + "/" + id + "/prizes", {withCredentials: true}).toPromise();
  }

  public addPrizeToBook(prize: PrizeForm, id: number): Observable<object> {
    return this.http.post<object>(this.URL + "/" + id + "/prizes", prize, {withCredentials: true});
  }

  public deleteBooksPrize(prizeID: number, bookID: number): Promise<void> {
    return this.http.delete<void>(this.URL + "/" + bookID + "/prizes/" + prizeID, {withCredentials: true}).toPromise();
  }

  public getPrizeOfBook(prizeName: string, bookID: number): Promise<Prize> {
    return this.http.get<Prize>(this.URL + "/" + bookID + "/prizes/" + prizeName, {withCredentials: true}).toPromise();
  }

  public findAllBooksByAuthor(author: string): Promise<LibroCard[]> {
    return this.http.get<LibroCard[]>(this.URL + "?author=" + author, {withCredentials: true}).toPromise();
  }
}
