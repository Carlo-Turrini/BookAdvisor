import {AuthorOfBook} from "./author-of-book";


export class Recensione {
  id: number;
  rating: number;
  originalityRating: number;
  writingQualityRating: number;
  pageTurnerRating: number;
  containsSpoilers: boolean;
  numOfUsersConsideredReviewUseful: number;
  reviewUsefulForLoggedUser: boolean;
  testo: string;
  timestamp: string;
  userId: number;
  username: string;
  profileImage: any;
  bookId: number;
  titolo: string;
  autori: AuthorOfBook[];
  coverImage: any;


  constructor(id: number, testo: string, rating: number, originalityRating: number, writingQualityRating: number, pageTurnerRating: number, containsSpoilers: boolean, numOfUsersConsideredReviewUseful: number, reviewUsefulForLoggedUser: boolean, timestamp: string, userId: number, username: string, bookId: number, titolo: string, autori: AuthorOfBook[], profileImage: any, coverImage: any) {
    this.id = id;
    this.rating = rating;
    this.originalityRating = originalityRating;
    this.writingQualityRating = writingQualityRating;
    this.pageTurnerRating = pageTurnerRating;
    this.containsSpoilers = containsSpoilers;
    this.numOfUsersConsideredReviewUseful = numOfUsersConsideredReviewUseful;
    this.reviewUsefulForLoggedUser = reviewUsefulForLoggedUser;
    this.testo = testo;
    this.timestamp = timestamp;
    this.userId = userId;
    this.username = username;
    this.bookId = bookId;
    this.titolo = titolo;
    this.autori = autori;
    this.profileImage = profileImage;
    this.coverImage = coverImage;
  }
}
