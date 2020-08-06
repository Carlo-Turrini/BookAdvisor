import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {BookRanking} from "../../core/model/book-ranking";
import {ActivatedRoute, Router} from "@angular/router";
import {BookRankingService} from "../../core/services/book-ranking.service";
import {MyBooksService} from "../../core/services/my-books.service";
import {AuthenticationService} from "../../core/services/authentication.service";
import {MyBooksRead} from "../../core/model/my-books-read";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-book-ranking-form',
  templateUrl: './book-ranking-form.component.html',
  styleUrls: ['./book-ranking-form.component.css']
})
export class BookRankingFormComponent implements OnInit {
  @Input() private userID: number;
  @Output() private  added: EventEmitter<BookRanking[]> = new EventEmitter<BookRanking[]>();
  @Output() goBack: EventEmitter<void> = new EventEmitter<void>();
  myBooksRead: MyBooksRead[] = [];
  private bookRank: BookRanking[] = [];
  ranks: number[] = [1,2,3,4,5,6,7,8,9,10];
  bookRankForm = new FormGroup({
    myBookID: new FormControl('', [Validators.required]),
    rank: new FormControl('', [Validators.required, Validators.min(1), Validators.max(10)])
  })
  constructor(private route: ActivatedRoute, private router: Router, private bookRankingService: BookRankingService, private myBooksService: MyBooksService, private authenticationService: AuthenticationService) { }

  async ngOnInit() {
    await this.myBooksService.getAllUsersMyBooksRead(this.userID).then(myBooksRead => {myBooksRead.forEach(myBook => {this.myBooksRead.push(myBook)})})
      .catch(error => {
        let statusCode = error.error.status;
        alert(error.error.status + ": " + error.error.message)
        if(statusCode == 401) {
          this.router.navigate(['/login']);
        }
        else this.router.navigate(['/']);
      });
  }

  async onSubmit() {
    await this.bookRankingService.addBookToRanking(this.userID, this.bookRankForm.value.myBookID, this.bookRankForm.value.rank).then(data => { data.forEach(bookInRank => {this.bookRank.push(bookInRank)});
      this.bookRankForm.reset();
      this.added.emit(this.bookRank);
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

  isInvalidAndDirty(field: string) {
    const ctrl = this.bookRankForm.get(field);
    return !ctrl.valid && ctrl.dirty;
  }

  hasError(field: string, error: string) {
    const ctrl = this.bookRankForm.get(field);
    return ctrl.dirty && ctrl.hasError(error);
  }

}
