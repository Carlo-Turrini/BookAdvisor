export class AuthorForm {
  authorsFullname: string;
  birthYear: number;
  deathYear: number;
  biography: string;

  constructor(authorsFullname: string, birthYear: number, deathYear: number, biography: string) {
    this.authorsFullname = authorsFullname;
    this.birthYear = birthYear;
    this.deathYear = deathYear;
    this.biography = biography;
  }
}
