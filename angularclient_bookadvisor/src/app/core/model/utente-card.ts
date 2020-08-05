export class UtenteCard {
  id: number;
  nome: string;
  cognome: string;
  username: string;
  profileImage: any;

  constructor(id: number, username: string, nome: string, cognome: string, profileImage: any) {
    this.id = id;
    this.username = username;
    this.nome = nome;
    this.cognome = cognome;
    this.profileImage = profileImage;
  }
}
