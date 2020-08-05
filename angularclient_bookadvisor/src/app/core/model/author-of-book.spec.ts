import { AuthorOfBook } from './author-of-book';

describe('AuthorOfBook', () => {
  it('should create an instance', () => {
    expect(new AuthorOfBook(1, 'Prova')).toBeTruthy();
  });
});
