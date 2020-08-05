export class UtenteUpdateForm {
  nome: string;
  cognome: string;
  password: string;
  email: string;
  descrizione: string;

  constructor(nome: string, cognome: string, password: string, email: string, descrizione: string) {
    this.nome = nome;
    this.cognome = cognome;
    this.password = password;
    this.email = email;
    this.descrizione = descrizione;
  }
}
