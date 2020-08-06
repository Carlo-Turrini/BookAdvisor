import {Component, Input, OnInit} from '@angular/core';
import {Prize} from "../../core/model/prize";
import {ActivatedRoute, Router} from "@angular/router";
import {LibroService} from "../../core/services/libro.service";
import {AuthenticationService} from "../../core/services/authentication.service";

@Component({
  selector: 'app-prize-list',
  templateUrl: './prize-list.component.html',
  styleUrls: ['./prize-list.component.css']
})
export class PrizeListComponent implements OnInit {
  prizes: Prize[] = [];
  @Input() bookID: number;
  addPrize: boolean = false;
  constructor(private route: ActivatedRoute, private router: Router, private libroService: LibroService, public authenticationService: AuthenticationService) { }

  async ngOnInit() {
    await this.libroService.getBooksPrizes(this.bookID).then(prizesReturned => {prizesReturned.forEach(prize => {this.prizes.push(prize)})})
      .catch(error => {
        let statusCode = error.error.status;
        alert(error.error.status + ": " + error.error.message)
        if(statusCode == 401) {
          this.router.navigate(['/login']);
        }
        else this.router.navigate(['/']);
      });
  }

  newPrize() {
    if(this.authenticationService.adminAuth()) {
      this.addPrize = true;
    }
    else this.router.navigate(['/']);
  }

  prizeAdded(prize: Prize) {
    this.prizes.push(prize);
    this.addPrize = false;
  }

  async deletePrize(prizeID: number) {
    if(this.authenticationService.adminAuth()) {
      await this.libroService.deleteBooksPrize(prizeID, this.bookID).then(_ => {
        this.prizes.forEach(prize => {
          if (prize.id == prizeID) {
            this.prizes.splice(this.prizes.indexOf(prize), 1);
          }
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

}
