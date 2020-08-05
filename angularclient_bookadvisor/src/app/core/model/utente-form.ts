export class UtenteForm {
  nome: string;
  cognome: string;
  username: string;
  password: string;
  email: string;
  descrizione: string;

  constructor(nome: string, cognome: string, username: string, password: string, email: string, descrizione: string) {
    this.nome = nome;
    this.cognome = cognome;
    this.username = username;
    this.password = password;
    this.email = email;
    this.descrizione = descrizione;
  }
}
