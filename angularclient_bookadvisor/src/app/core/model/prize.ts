export class Prize {
  id: number;
  yearAwarded: number;
  prizeName: string;

  constructor(id: number, yearAwarded: number, prizeName: string) {
    this.id = id;
    this.yearAwarded = yearAwarded;
    this.prizeName = prizeName;
  }
}
