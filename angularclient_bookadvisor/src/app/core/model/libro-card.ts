import {AuthorOfBook} from "./author-of-book";

export class LibroCard {
  id: number;
  titolo: string;
  autori: AuthorOfBook[];
  generi: string[];
  overallRating: number;
  coverImage: any;

  constructor(id: number, titolo: string, autori: AuthorOfBook[], generi: string[], overallRating: number, coverImage: any) {
    this.id = id;
    this.titolo = titolo;
    this.autori = autori;
    this.generi = generi;
    this.overallRating = overallRating;
    this.coverImage = coverImage;
  }
}
