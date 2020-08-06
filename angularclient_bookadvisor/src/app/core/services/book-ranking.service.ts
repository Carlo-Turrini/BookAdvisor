import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {BookRanking} from "../model/book-ranking";

@Injectable({
  providedIn: 'root'
})
export class BookRankingService {
  URL = "http://localhost:8080/api/utenti";
  constructor(private http: HttpClient) { }

  public addBookToRanking(userID: number, myBookID: number, rank: number): Promise<BookRanking[]>{
    return this.http.post<BookRanking[]>(this.URL + "/" + userID + "/bookRank", {myBookID, rank} , {withCredentials: true}).toPromise();
  }
  public deleteBookFromRanking(userID: number, rankID: number): Promise<BookRanking[]> {
    return this.http.delete<BookRanking[]>(this.URL + "/" + userID + "/bookRank/" + rankID, {withCredentials: true}).toPromise();
  }
  public getUsersBookRanking(userID: number): Promise<BookRanking[]> {
    return this.http.get<BookRanking[]>(this.URL + "/" + userID + "/bookRank", {withCredentials: true}).toPromise();
  }
}
