import { LibroCard } from './libro-card';
import {AuthorOfBook} from "./author-of-book";

describe('LibroCard', () => {
  it('should create an instance', () => {
    let authorsOfBook: AuthorOfBook[] = [];
    let author: AuthorOfBook = new AuthorOfBook(1, 'autore');
    authorsOfBook.push(author);
    let generi: string[] = [];
    generi.push("Romanzo");
    expect(new LibroCard(1, 'Titolo', authorsOfBook, generi, 4.3, 'image')).toBeTruthy();
  });
});
