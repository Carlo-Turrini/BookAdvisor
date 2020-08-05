import { LibroForm } from './libro-form';
import {AuthorOfBook} from "./author-of-book";

describe('LibroForm', () => {
  it('should create an instance', () => {
    let authorsOfBook: AuthorOfBook[] = [];
    let author: AuthorOfBook = new AuthorOfBook(1, 'autore');
    authorsOfBook.push(author);
    let generi: string[] = [];
    generi.push("Romanzo");
    expect(new LibroForm('Titolo', 'sinossi', authorsOfBook, generi, 1234, 123, false, '', null)).toBeTruthy();
  });
});
