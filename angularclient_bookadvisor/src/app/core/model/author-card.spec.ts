import { AuthorCard } from './author-card';

describe('AuthorCard', () => {
  it('should create an instance', () => {
    expect(new AuthorCard(1, 'Prova', 1234, null, 'image')).toBeTruthy();
  });
});
