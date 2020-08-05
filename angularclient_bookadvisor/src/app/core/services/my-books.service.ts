import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {MyBooks} from "../model/my-books";
import {MyBooksRead} from "../model/my-books-read";

@Injectable({
  providedIn: 'root'
})
export class MyBooksService {
  URL = "http://localhost:8080/utenti";
  constructor(private http: HttpClient) { }

  public deleteBookFromMyBooks(bookID: number, userID: number): Promise<boolean> {
    return this.http.delete<boolean>(this.URL + "/" + userID + "/myBooks/" + bookID, {withCredentials: true}).toPromise();
  }
  public addMyBookForUser(bookID: number, userID: number, shelf: string): Promise<any> {
    return this.http.post<any>(this.URL + "/" + userID + "/myBooks/" + bookID, shelf, {withCredentials: true} ).toPromise();
  }
  public updateMyBook(bookID: number, userID: number, shelf: string): Promise<boolean> {
    return this.http.put<boolean>(this.URL + "/" + userID + "/myBooks/" + bookID, shelf, {withCredentials: true} ).toPromise();
  }
  public getAllUsersMyBooks(userID: number): Promise<MyBooks[]> {
    return this.http.get<MyBooks[]>(this.URL + "/" + userID + "/myBooks", {withCredentials: true}).toPromise();
  }

  public getAllUsersMyBooksRead(userID: number): Promise<MyBooksRead[]> {
    return this.http.get<MyBooksRead[]>(this.URL + "/" + userID + "/myBooks/booksReadNotInRank", {withCredentials: true}).toPromise();
  }
}
