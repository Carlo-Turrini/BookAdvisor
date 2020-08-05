import {AuthorOfBook} from "./author-of-book";

export class LibroForm {
  titolo: string;
  sinossi: string;
  autori: AuthorOfBook[];
  annoPubblicazione: number;
  pagine: number;
  saga: boolean;
  titoloSaga: string;
  numInSaga: number;
  generi: string[];

  constructor(titolo: string, sinossi: string, autori: AuthorOfBook[], generi: string[], annoPubblicazione: number, pagine: number, saga: boolean, titoloSaga: string, numInSaga: number) {
    this.titolo = titolo;
    this.sinossi = sinossi;
    this.autori = autori;
    this.generi = generi;
    this.annoPubblicazione = annoPubblicazione;
    this.pagine = pagine;
    this.saga = saga;
    this.titoloSaga = titoloSaga;
    this.numInSaga = numInSaga;
  }
}
