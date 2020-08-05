export class PrizeForm {
  yearAwarded: number;
  prizeName: string;

  constructor(yearAwarded: number, prizeName: string) {
    this.yearAwarded = yearAwarded;
    this.prizeName = prizeName;
  }
}
