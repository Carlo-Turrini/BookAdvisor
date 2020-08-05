import { UtenteUpdateForm } from './utente-update-form';

describe('UtenteUpdateForm', () => {
  it('should create an instance', () => {
    expect(new UtenteUpdateForm('Prova', 'Prova', '', 'p.p@gmail.com', 'descrizione')).toBeTruthy();
  });
});
