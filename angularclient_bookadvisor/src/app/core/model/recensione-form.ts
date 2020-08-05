export class RecensioneForm {
  rating: number;
  originalityRating: number;
  writingQualityRating: number;
  pageTurnerRating: number;
  containsSpoilers: boolean;
  testo: String;

  constructor(rating: number, originalityRating: number, writingQualityRating: number, pageTurnerRating: number, containsSpoilers: boolean, testo: String) {
    this.rating = rating;
    this.testo = testo;
    this.originalityRating = originalityRating;
    this.writingQualityRating = writingQualityRating;
    this.pageTurnerRating = pageTurnerRating;
    this.containsSpoilers = containsSpoilers;
  }
}
