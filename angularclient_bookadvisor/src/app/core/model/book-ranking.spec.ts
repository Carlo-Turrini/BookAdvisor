import { BookRanking } from './book-ranking';
import {AuthorOfBook} from "./author-of-book";

describe('BookRanking', () => {
  it('should create an instance', () => {
    let authorsOfBook: AuthorOfBook[] = [];
    let author: AuthorOfBook = new AuthorOfBook(1, 'autore');
    authorsOfBook.push(author);
    expect(new BookRanking(1, 1, 1, 'Prova', 'image', authorsOfBook)).toBeTruthy();
  });
});
