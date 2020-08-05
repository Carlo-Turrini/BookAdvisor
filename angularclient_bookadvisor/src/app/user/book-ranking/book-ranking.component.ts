import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {BookRanking} from "../../core/model/book-ranking";
import {ActivatedRoute, Router} from "@angular/router";
import {BookRankingService} from "../../core/services/book-ranking.service";
import {AuthenticationService} from "../../core/services/authentication.service";

@Component({
  selector: 'app-book-ranking',
  templateUrl: './book-ranking.component.html',
  styleUrls: ['./book-ranking.component.css']
})
export class BookRankingComponent implements OnInit, OnChanges {
  @Input() private userID: number;
  @Input() private isRankModified: boolean;
  @Output() private updatedRank: EventEmitter<void> = new EventEmitter<void>();
  private bookRanking : BookRanking[] = [];
  private add: boolean = false;
  constructor(private route: ActivatedRoute, private router: Router, private bookRankingService: BookRankingService, private authenticationService: AuthenticationService) { }

  async ngOnInit() {
    await this.bookRankingService.getUsersBookRanking(this.userID).then(ranking => {ranking.forEach(bookRank => {this.bookRanking.push(bookRank)})})
      .catch(error => {
        let statusCode = error.error.status;
        alert(error.error.status + ": " + error.error.message)
        if(statusCode == 401) {
          this.router.navigate(['/login']);
        }
        else this.router.navigate(['/']);
      });
    //await r.toPromise().then(ranking => {this.bookRanking = ranking;})
  }

  ngOnChanges(changes: SimpleChanges) {
    console.log("Triggered onChanges");
    console.log(changes.isRankModified.currentValue);
    if(changes.isRankModified.currentValue) {
      this.updateRank()
    }
  }
  async updateRank() {
    this.bookRanking = [];
    await this.bookRankingService.getUsersBookRanking(this.userID).then(ranking => {ranking.forEach(bookRank => {this.bookRanking.push(bookRank)})})
      .catch(error => {
        let statusCode = error.error.status;
        alert(error.error.status + ": " + error.error.message)
        if(statusCode == 401) {
          this.router.navigate(['/login']);
        }
        else this.router.navigate(['/']);
      });
    this.updatedRank.emit();
  }

  async deleteBookFromRank(rankID: number) {
    if(this.authenticationService.sameUserOrAdminAuth(this.userID)) {
      await this.bookRankingService.deleteBookFromRanking(this.userID, rankID).then(ranking => {
        this.bookRanking = [];
        ranking.forEach(bookRank => {
          this.bookRanking.push(bookRank)
        });
      })
        .catch(error => {
          let statusCode = error.error.status;
          alert(error.error.status + ": " + error.error.message)
          if(statusCode == 401) {
            this.router.navigate(['/login']);
          }
          else this.router.navigate(['/']);
        });
    }
    else this.router.navigate(['/']);
  }

  addRank() {
    this.add = true;
  }
  addedHandler(bookRanking: BookRanking[]) {
    if(this.authenticationService.sameUserOrAdminAuth(this.userID)) {
      this.bookRanking = bookRanking;
      this.add = false;
    }
    else this.router.navigate(['/']);
  }

}
