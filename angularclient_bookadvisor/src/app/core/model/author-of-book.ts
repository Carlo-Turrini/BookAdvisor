export class AuthorOfBook {
  id: number;
  authorsFullname: string;

  constructor(id: number, authorsFullname: string) {
    this.id = id;
    this.authorsFullname = authorsFullname;
  }
}
