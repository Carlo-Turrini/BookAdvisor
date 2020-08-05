import { UtenteForm } from './utente-form';

describe('UtenteForm', () => {
  it('should create an instance', () => {
    expect(new UtenteForm('Prova' ,'Prova', 'Prova', 'password', 'p.p@gmai.com', 'descrizione')).toBeTruthy();
  });
});
