import { AuthorForm } from './author-form';

describe('AuthorForm', () => {
  it('should create an instance', () => {
    expect(new AuthorForm('Prova',1234, null, 'biografia')).toBeTruthy();
  });
});
