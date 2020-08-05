import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {RecensioneForm} from "../model/recensione-form";
import {Recensione} from "../model/recensione";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class RecensioneService {

  constructor(private http: HttpClient) {}

  URL = 'http://localhost:8080/libri';

  public newReview(bookId: number, newReview: RecensioneForm): Observable<object> {
    return this.http.post<object>(this.URL + "/" + bookId + '/recensioni', newReview, {withCredentials: true});
  }

  public deleteReview(userId: number, reviewId: number): Promise<void> {
    return this.http.delete<void>('http://localhost:8080/utenti/' + userId + '/recensioni/' + reviewId, {withCredentials: true}).toPromise();
  }

  public allReviewsByUser(userId: number): Promise<Recensione[]> {
    return this.http.get<Recensione[]>('http://localhost:8080/utenti/' + userId + '/recensioni', {withCredentials: true}).toPromise();
  }

  public allReviewsByBook(bookId: number): Promise<Recensione[]> {
    return this.http.get<Recensione[]>(this.URL + "/" + bookId + '/recensioni', {withCredentials: true}).toPromise();
  }

  public addUsefulReview(reviewID: number, userID: number): Promise<void> {
    return this.http.post<void>("http://localhost:8080/recensioni/" + reviewID + "/isReviewUseful", userID, {withCredentials:true} ).toPromise();
  }

  public removeUsefulReview(reviewID: number, userID: number): Promise<void> {
    return this.http.delete<void>("http://localhost:8080/recensioni/" + reviewID + "/isReviewUseful/" + userID, {withCredentials: true}).toPromise();
  }
}
