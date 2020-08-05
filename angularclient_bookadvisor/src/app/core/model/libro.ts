import {Recensione} from "./recensione";
import {AuthorOfBook} from "./author-of-book";

export class Libro {
  id: number;
  annoPubblicazione: number;
  pagine: number;
  titolo: string;
  sinossi: string;
  autori: AuthorOfBook[];
  copertina: any;
  saga: boolean;
  titoloSaga: string;
  numInSaga: number;
  generi: string[];
  overallRating: number;
  overallWritingQualityRating: number;
  overallPageTurnerRating: number;
  overallOriginalityRating: number;
  bookShelf: string;



  /*constructor(id: number, titolo: string, autori: string, annoPubblicazione: number, pagine: number, genere: string, sinossi: string, saga: boolean, titoloSaga: string, numInSaga: number, copertina: any, overallRating: number ) {
    this.id = id;
    this.annoPubblicazione = annoPubblicazione;
    this.pagine = pagine;
    this.titolo = titolo;
    this.sinossi = sinossi;
    this.autori = autori;
    this.copertina = copertina;
    this.genere = genere;
    this.saga = saga;
    this.titoloSaga = titoloSaga;
    this.numInSaga = numInSaga;
    this.overallRating = overallRating;
  }*/
  constructor() {}
}
