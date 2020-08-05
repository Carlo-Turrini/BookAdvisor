import { MyBooksRead } from './my-books-read';

describe('MyBooksRead', () => {
  it('should create an instance', () => {
    expect(new MyBooksRead(1, 'Titolo')).toBeTruthy();
  });
});
