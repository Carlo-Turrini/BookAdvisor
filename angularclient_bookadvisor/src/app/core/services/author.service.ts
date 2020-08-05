import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AuthorCard} from "../model/author-card";
import {Observable} from "rxjs";
import {AuthorForm} from "../model/author-form";
import {Author} from "../model/author";
import {AuthorOfBook} from "../model/author-of-book";

@Injectable({
  providedIn: 'root'
})
export class AuthorService {
  URL = "http://localhost:8080/authors"
  constructor(private http: HttpClient) { }

  public getAllAuthors(): Promise<AuthorCard[]> {
    return this.http.get<AuthorCard[]>(this.URL, {withCredentials: true}).toPromise();
  }

  public addNewAuthor(author: AuthorForm): Observable<object> {
    return this.http.post<object>(this.URL, author, {withCredentials: true});
  }

  public updateAuthor(author: AuthorForm, authorID: number): Observable<object> {
    return this.http.put<object>(this.URL + "/" + authorID, author, {withCredentials: true});
  }
  public deleteAuthor(authorID: number): Promise<void> {
    return this.http.delete<void>(this.URL + "/" + authorID, {withCredentials: true}).toPromise();
  }

  public getAuthor(authorID: number): Promise<Author> {
    return this.http.get<Author>(this.URL + "/" + authorID, {withCredentials: true}).toPromise();
  }

  public uploadAuthorsPhoto(authorID: number, authorsPhoto: any): Promise<any> {
    const uploadData = new FormData();
    uploadData.append('authorsPhoto', authorsPhoto, authorsPhoto.name);
    return this.http.post<any>(this.URL + "/" + authorID + "/authors_photo", uploadData, {withCredentials: true}).toPromise();
  }

  public getAllAuthorsForBookForm(): Promise<AuthorOfBook[]> {
    return this.http.get<AuthorOfBook[]>(this.URL + "/authorsForBookForm", {withCredentials: true}).toPromise();
  }

  public getAuthorOfBookFromFullname(authorsFullname: string): Promise<AuthorOfBook> {
    return this.http.get<AuthorOfBook>(this.URL + "/byName/" + authorsFullname, {withCredentials: true}).toPromise();
  }
}
