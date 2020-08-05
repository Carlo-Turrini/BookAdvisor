export class AuthorCard {
  id: number;
  authorsFullname: string;
  birthYear: number;
  deathYear: number;
  authorsPhoto: any;

  constructor(id: number, authorsFullname: string, birthYear: number, deathYear: number, authorsPhoto: any) {
    this.id = id;
    this.authorsFullname = authorsFullname;
    this.birthYear = birthYear;
    this.deathYear = deathYear;
    this.authorsPhoto = authorsPhoto;
  }
}
