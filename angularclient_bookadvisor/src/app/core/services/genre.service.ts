import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {GenreForm} from "../model/genre-form";

@Injectable({
  providedIn: 'root'
})
export class GenreService {
  URL = "http://localhost:8080/genres";
  constructor(private http: HttpClient) { }

  public getAllGenres(): Promise<string[]> {
    return this.http.get<string[]>(this.URL, {withCredentials: true}).toPromise();
  }
  public addGenre(genre: GenreForm): Promise<string[]> {
    return this.http.post<string[]>(this.URL, genre, {withCredentials: true}).toPromise();
  }
}
