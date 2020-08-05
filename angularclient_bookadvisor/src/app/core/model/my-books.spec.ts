import { MyBooks } from './my-books';
import {AuthorOfBook} from "./author-of-book";

describe('MyBooks', () => {
  it('should create an instance', () => {
    let authorsOfBook: AuthorOfBook[] = [];
    let author: AuthorOfBook = new AuthorOfBook(1, 'autore');
    authorsOfBook.push(author);
    let generi: string[] = [];
    generi.push("Romanzo");
    expect(new MyBooks(1, 'Titolo', authorsOfBook, generi, 4.3, 'image', 'read', 1, 1)).toBeTruthy();
  });
});
