import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {RecensioneService} from "../../core/services/recensione.service";
import {AuthenticationService} from "../../core/services/authentication.service";
import {Recensione} from "../../core/model/recensione";

@Component({
  selector: 'app-review-list',
  templateUrl: './review-list.component.html',
  styleUrls: ['./review-list.component.css']
})
export class ReviewListComponent implements OnInit {
  @Input() identifier: number;
  @Input() type: string;
  @Output() changeToReviewList: EventEmitter<void> = new EventEmitter<void>();
  @Output() newReview: EventEmitter<void> = new EventEmitter<void>();
  private recensioni: Recensione[];
  private add: boolean = false;
  constructor(private http: HttpClient, private router: Router, private route: ActivatedRoute, private reviewService: RecensioneService, private authenticationService: AuthenticationService) { }

  async ngOnInit() {
    this.recensioni = [];
    if(this.type == "user") {
      await this.reviewService.allReviewsByUser(this.identifier).then(recensioni => {recensioni.forEach(recensione => {this.recensioni.push(recensione);})})
        .catch(error => {
          let statusCode = error.error.status;
          alert(error.error.status + ": " + error.error.message)
          if(statusCode == 401) {
            this.router.navigate(['/login']);
          }
          else this.router.navigate(['/']);
        });
    }
    else if(this.type == "book") {
      await this.reviewService.allReviewsByBook(this.identifier).then(reviews => {reviews.forEach(recensione => this.recensioni.push(recensione))})
        .catch(error => {
          let statusCode = error.error.status;
          alert(error.error.status + ": " + error.error.message)
          if(statusCode == 401) {
            this.router.navigate(['/login']);
          }
          else this.router.navigate(['/']);
        });
    }
  }

  async deleteReview(reviewId: number, userId: number) {
    if(this.authenticationService.sameUserOrAdminAuth(this.authenticationService.loggedUser.id)) {
      await this.reviewService.deleteReview(userId, reviewId)
        .catch(error => {
          let statusCode = error.error.status;
          alert(error.error.status + ": " + error.error.message)
          if(statusCode == 401) {
            this.router.navigate(['/login']);
          }
          else this.router.navigate(['/']);
        });
      this.recensioni.forEach(recensione => {
        if (recensione.id == reviewId) {
          this.recensioni.splice(this.recensioni.indexOf(recensione), 1);
        }
      });
    }
  }

  async setUsefulReview(review: Recensione) {
    if(this.authenticationService.logged) {
      if(review.reviewUsefulForLoggedUser) {
        await this.reviewService.removeUsefulReview(review.id, this.authenticationService.loggedUser.id).then(_ => {review.reviewUsefulForLoggedUser = false; review.numOfUsersConsideredReviewUseful--;})
          .catch(error => {
            let statusCode = error.error.status;
            alert(error.error.status + ": " + error.error.message)
            if(statusCode == 401) {
              this.router.navigate(['/login']);
            }
            else this.router.navigate(['/']);
          });
      }
      else {
        await this.reviewService.addUsefulReview(review.id, this.authenticationService.loggedUser.id).then(_ => {review.reviewUsefulForLoggedUser = true; review.numOfUsersConsideredReviewUseful++;})
          .catch(error => {
            let statusCode = error.error.status;
            alert(error.error.status + ": " + error.error.message)
            if(statusCode == 401) {
              this.router.navigate(['/login']);
            }
            else this.router.navigate(['/']);
          });
      }
    }
  }

  async reviewAddedHandler(success: boolean) {
    if(success) {
      this.recensioni = [];
      await this.reviewService.allReviewsByBook(this.identifier).then(recensioni => {
        recensioni.forEach(recensione => {
          this.recensioni.push(recensione);
        })
      })
        .catch(error => {
          let statusCode = error.error.status;
          alert(error.error.status + ": " + error.error.message)
          if(statusCode == 401) {
            this.router.navigate(['/login']);
          }
          else this.router.navigate(['/']);
        });
      this.changeToReviewList.emit();
    }
    this.add = false;
  }

  addReview() {
    if(this.authenticationService.logged) {
      this.newReview.emit();
      this.add = true;
    }
  }

}
