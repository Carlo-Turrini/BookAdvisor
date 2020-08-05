import {AuthorOfBook} from "./author-of-book";

export class MyBooks {
  id: number;
  titolo: string;
  authors: AuthorOfBook[];
  genres: string[];
  overallRating: number;
  coverImage: any;
  shelf: string;
  bookID: number;
  userID: number;

  constructor(id: number, titolo: string, authors: AuthorOfBook[], genres: string[], overallRating: number, coverImage: any, shelf: string, bookID: number, userID: number) {
    this.id = id;
    this.titolo = titolo;
    this.authors = authors;
    this.genres = genres;
    this.overallRating = overallRating;
    this.coverImage = coverImage;
    this.shelf = shelf;
    this.bookID = bookID;
    this.userID = userID;
  }
}
