import {AuthorOfBook} from "./author-of-book";

export class BookRanking {
  id: number;
  bookID: number;
  bookRank: number;
  bookTitle: string;
  bookCoverPhoto: any;
  bookAuthors: AuthorOfBook[];

  constructor(id: number, bookID: number, bookRank: number, bookTitle: string, bookCoverPhoto: any, bookAuthors: AuthorOfBook[]) {
    this.id = id;
    this.bookID = bookID;
    this.bookRank = bookRank;
    this.bookTitle = bookTitle;
    this.bookCoverPhoto = bookCoverPhoto;
    this.bookAuthors = bookAuthors;
  }
}
