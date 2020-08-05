import { Component, OnInit } from '@angular/core';
import {UtenteCard} from "../../core/model/utente-card";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {UtenteService} from "../../core/services/utente.service";
import {AuthenticationService} from "../../core/services/authentication.service";

@Component({
  selector: 'app-utente-list',
  templateUrl: './utente-list.component.html',
  styleUrls: ['./utente-list.component.css']
})
export class UtenteListComponent implements OnInit {
  utenti: UtenteCard[] = [];
  constructor(private http: HttpClient, private router: Router, private route: ActivatedRoute, private utenteService: UtenteService, private authenticationService: AuthenticationService) { }

  async ngOnInit() {
    if(this.authenticationService.adminAuth()) {
      await this.utenteService.findAll().then(utenti => {utenti.forEach(utente => this.utenti.push(utente))})
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
      this.router.navigate(['/']);
    }
  }

  async deleteUser(id: number) {
    if(this.authenticationService.adminAuth()) {
      await this.utenteService.deleteUser(id)
        .catch(error => {
          let statusCode = error.error.status;
          alert(error.error.status + ": " + error.error.message)
          if(statusCode == 401) {
            this.router.navigate(['/login']);
          }
          else this.router.navigate(['/']);
        });
      this.utenti.forEach(utente => {
        if (utente.id == id) {
          this.utenti.splice(this.utenti.indexOf(utente), 1);
        }
      });
    }
    else {
      this.router.navigate(['/']);
    }
  }

}
