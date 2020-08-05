import { UtenteCard } from './utente-card';

describe('UtenteCard', () => {
  it('should create an instance', () => {
    expect(new UtenteCard(1, 'Tarlo', 'Prova' ,'Prova', 'image')).toBeTruthy();
  });
});
