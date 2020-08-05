import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Recensione} from "../../core/model/recensione";
import {Observable} from "rxjs";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";

import {RecensioneService} from "../../core/services/recensione.service";
import {RecensioneForm} from "../../core/model/recensione-form";
import {async} from "rxjs/internal/scheduler/async";
import {AuthenticationService} from "../../core/services/authentication.service";

@Component({
  selector: 'app-review-form',
  templateUrl: './review-form.component.html',
  styleUrls: ['./review-form.component.css']
})
export class ReviewFormComponent implements OnInit {

  @Input() bookId: number;
  @Output() reviewSubmitted: EventEmitter<boolean> = new EventEmitter(); //RICORDA DI METTERE L'handler nel component libro!
  submitted: boolean = false;
  reviewForm = new FormGroup({
    rating: new FormControl('', [Validators.required, Validators.min(0), Validators.max(5)]),
    originalityRating: new FormControl('', [Validators.required, Validators.min(0), Validators.max(5)]),
    pageTurnerRating: new FormControl('', [Validators.required, Validators.min(0), Validators.max(5)]),
    writingQualityRating: new FormControl('', [Validators.required, Validators.min(0), Validators.max(5)]),
    containsSpoilers: new FormControl('', Validators.required),
    testo: new FormControl('', [Validators.required, Validators.minLength(1), Validators.maxLength(2048)])
});

  constructor(private route: ActivatedRoute, private router: Router, private reviewService: RecensioneService, private authenticationService: AuthenticationService) { }

  ngOnInit() {
    if(!this.authenticationService.logged) {
      this.router.navigate(['/']);
    }
    this.reviewForm.controls.containsSpoilers.setValue(false);
  }

  async onSubmit() {
    if(this.authenticationService.logged) {
      this.submitted = true;
      const newReview = new RecensioneForm(this.reviewForm.value.rating, this.reviewForm.value.originalityRating, this.reviewForm.value.writingQualityRating, this.reviewForm.value.pageTurnerRating, this.reviewForm.value.containsSpoilers, this.reviewForm.value.testo);
      const r = this.reviewService.newReview(this.bookId, newReview);
      await r.toPromise().then(async (data) => {
        for (const fieldName of Object.keys(data)) {
          const serverErrors = data[fieldName];
          const errors = {};
          for (const serverError of serverErrors) {
            errors[serverError] = true;
          }
          const control = this.reviewForm.get(fieldName);
          control.setErrors(errors);
          control.markAsDirty();
        }
        if (this.reviewForm.valid) {
          this.reviewForm.reset();
          this.reviewSubmitted.emit(true);
        }
      })
        .catch(error => {
          let statusCode = error.error.status;
          alert(error.error.status + ": " + error.error.message)
          if(statusCode == 401) {
            this.router.navigate(['/login']);
          }
          else this.router.navigate(['/']);
        });
      this.submitted = false;
    }
    else this.router.navigate(['/']);
  }
  isInvalidAndDirty(field: string) {
    const ctrl = this.reviewForm.get(field);
    return !ctrl.valid && ctrl.dirty;
  }

  hasError(field: string, error: string) {
    const ctrl = this.reviewForm.get(field);
    return ctrl.dirty && ctrl.hasError(error);
  }

}
