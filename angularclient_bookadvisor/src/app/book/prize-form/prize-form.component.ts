import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {AuthenticationService} from "../../core/services/authentication.service";
import {PrizeValidatorService} from "../../core/asyncValidators/prize-validator.service";
import {LibroService} from "../../core/services/libro.service";
import {Prize} from "../../core/model/prize";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {PrizeForm} from "../../core/model/prize-form";

@Component({
  selector: 'app-prize-form',
  templateUrl: './prize-form.component.html',
  styleUrls: ['./prize-form.component.css']
})
export class PrizeFormComponent implements OnInit {
  @Input() bookID: number;
  @Output() prizeAdded: EventEmitter<Prize> = new EventEmitter<Prize>();
  @Output() goBack: EventEmitter<void> = new EventEmitter<void>();
  prizeForm;
  constructor(private route: ActivatedRoute, private router: Router, private libroService: LibroService, private prizeValidatorService: PrizeValidatorService, private authenticationService: AuthenticationService) { }

  ngOnInit() {
    console.log(this.bookID);
    this.prizeForm = new FormGroup({
      yearAwarded: new FormControl('', [Validators.required, Validators.min(0), Validators.max(9999)]),
      prizeName: new FormControl('', [Validators.required, Validators.minLength(1), Validators.maxLength(64)], this.prizeValidatorService.validatePrizeAss(this.bookID).bind(this.prizeValidatorService))
    })
  }

  async onSubmit() {
    if(this.authenticationService.adminAuth()) {
      const newPrize = new PrizeForm(this.prizeForm.value.yearAwarded, this.prizeForm.value.prizeName);
      this.libroService.addPrizeToBook(newPrize, this.bookID).subscribe(async (data) => {
        for (const fieldName of Object.keys(data)) {
          const serverErrors = data[fieldName];
          const errors = {};
          for (const serverError of serverErrors) {
            errors[serverError] = true;
          }
          const control = this.prizeForm.get(fieldName);
          control.setErrors(errors);
          control.markAsDirty();
        }
        if (this.prizeForm.valid) {
          this.prizeForm.reset();
          this.emitPrizeOfBook(newPrize.prizeName);
        }

      },
        error => {
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

  private async emitPrizeOfBook(prizeName: string) {
    let prize: Prize;
    await this.libroService.getPrizeOfBook(prizeName, this.bookID).then(p => {prize = p; this.prizeAdded.emit(prize);})
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
    const ctrl = this.prizeForm.get(field);
    return !ctrl.valid && ctrl.dirty;
  }

  hasError(field: string, error: string) {
    const ctrl = this.prizeForm.get(field);
    return ctrl.dirty && ctrl.hasError(error);
  }

}
