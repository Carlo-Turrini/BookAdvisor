import { Recensione } from './recensione';
import {AuthorOfBook} from "./author-of-book";

describe('Recensione', () => {
  it('should create an instance', () => {
    let authorsOfBook: AuthorOfBook[] = [];
    let author: AuthorOfBook = new AuthorOfBook(1, 'autore');
    authorsOfBook.push(author);
    expect(new Recensione(1, 'testo',4, 4, 4 ,4, false, 12, false, '03-09-2020', 1, 'Tarlo', 1, 'titolo', authorsOfBook, 'profileImage', 'coverImage')).toBeTruthy();
  });
});
