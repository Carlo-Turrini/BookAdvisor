export class OverallRatingsForBook {
  overallRating: number;
  overallOriginalityRating: number;
  overallWritingQualityRating: number;
  overallPageTurnerRating: number;

  constructor(overallRating: number, overallOriginalityRating: number, overallWritingQualityRating: number, overallPageTurnerRating: number) {
    this.overallRating = overallRating;
    this.overallOriginalityRating = overallOriginalityRating;
    this.overallWritingQualityRating = overallWritingQualityRating;
    this.overallPageTurnerRating = overallPageTurnerRating;
  }
}
