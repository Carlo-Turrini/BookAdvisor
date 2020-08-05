import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Utente} from "../../core/model/utente";
import {ActivatedRoute, NavigationEnd, Router} from "@angular/router";
import {UtenteService} from "../../core/services/utente.service";
import {HttpClient} from "@angular/common/http";
import {AuthenticationService} from "../../core/services/authentication.service";
import {UtenteUpdateForm} from "../../core/model/utente-update-form";
import {MyBooksListComponent} from "../my-books-list/my-books-list.component";

@Component({
  selector: 'app-utente',
  templateUrl: './utente.component.html',
  styleUrls: ['./utente.component.css']
})
export class UtenteComponent implements OnInit, OnDestroy {
  @ViewChild('myBooksList', {static: false}) myBooksListComponent = MyBooksListComponent;
  utente: Utente;
  update: boolean = false;
  upload: boolean = false;
  isRankModified: boolean = false;
  navigationSubscription;
  reinit: boolean = false;

  constructor(private http: HttpClient, private router: Router, private route: ActivatedRoute, private userService : UtenteService, private authenticationService: AuthenticationService) {
    this.utente = new Utente();
    this.navigationSubscription = this.router.events.subscribe((e:any) => {
      if( e instanceof NavigationEnd) {
        this.initialise();
      }
    });
  }

  ngOnInit() {
  }

  ngOnDestroy() {
    if(this.navigationSubscription) {
      this.navigationSubscription.unsubscribe();
    }
  }

  async initialise() {
    window.scrollTo(0,0);
    this.update = false;
    this.upload = false;
    this.isRankModified = false;
    await this.userService.getSingleUserById(parseInt(this.route.snapshot.paramMap.get('id'))).then(data => {this.utente = data})
      .catch(error => {
        let statusCode = error.error.status;
        alert(error.error.status + ": " + error.error.message)
        if(statusCode == 401) {
          this.router.navigate(['/login']);
        }
        else this.router.navigate(['/']);
      });
    this.reinitialiseSubComponents();
  }

  reinitialiseSubComponents() {
    this.reinit = true;
    setTimeout(() => { this.reinit = false;});
  }

  uploadedHandler(photoUrl: any) {
    if(this.authenticationService.sameUserOrAdminAuth(this.utente.id)) {
      this.utente.profilePhoto = photoUrl;
      this.upload = false;
    }
  }

  updateUser() {
    if(this.authenticationService.sameUserOrAdminAuth(this.utente.id)) {
      this.update = true;
      this.upload = false;
    }
  }

  uploadPhoto() {
    if(this.authenticationService.sameUserOrAdminAuth(this.utente.id)) {
      this.upload = true;
      this.update = false;
    }
  }

  resetUpdateUpload() {
    this.upload = false;
    this.update = false;
  }


  updatedHandler(utenteUpdateForm: UtenteUpdateForm) {
    if(this.authenticationService.sameUserOrAdminAuth(this.utente.id)) {
      this.utente.name = utenteUpdateForm.nome;
      this.utente.surname = utenteUpdateForm.cognome;
      this.utente.email = utenteUpdateForm.email;
      this.utente.description = utenteUpdateForm.descrizione;
      this.update = false;
    }
  }

  async deleteUser() {
    if(this.authenticationService.sameUserOrAdminAuth(this.utente.id)) {
      await this.userService.deleteUser(this.utente.id)
        .catch(error => {
          let statusCode = error.error.status;
          alert(error.error.status + ": " + error.error.message)
          if(statusCode == 401) {
            this.router.navigate(['/login']);
          }
          else this.router.navigate(['/']);
        });
      if(this.authenticationService.sameUser(this.utente.id)) {
        this.authenticationService.resetLogVars()
      }
      this.router.navigate(['/']);
    }
  }

  modifiedRank(isRankMod : boolean) {
    this.isRankModified = isRankMod;
  }

  updatedRank() {
    this.isRankModified = false;
  }

}

