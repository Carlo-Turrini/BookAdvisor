import {Component, OnDestroy, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, NavigationEnd, Router} from "@angular/router";
import {AuthenticationService} from "../../core/services/authentication.service";
import {LibroService} from "../../core/services/libro.service";
import {LibroCard} from "../../core/model/libro-card";

@Component({
  selector: 'app-libri-list',
  templateUrl: './libri-list.component.html',
  styleUrls: ['./libri-list.component.css']
})
export class LibriListComponent implements OnInit, OnDestroy {
  libri: LibroCard[] = [];
  titoloPagina: string;
  navigationSubscription;


  constructor(private http: HttpClient, private router: Router, private route: ActivatedRoute, public authenticationService: AuthenticationService, private libroService: LibroService) {
    this.titoloPagina = '';
    this.navigationSubscription = this.router.events.subscribe((e:any) => {
      if( e instanceof NavigationEnd) {
        this.initialise();
      }
    });

   }
  public async initialise() {
    window.scrollTo(0,0);
    this.libri = [];
    const r = this.route.data;
    r.subscribe(async data => {
      switch(data.tipo) {
        case "all": {
          await this.libroService.findAllBooks().then(libri => {libri.forEach(libro => this.libri.push(libro))});
          this.titoloPagina = "Libri";
          break;
        }
        case "genere": {
          var genere: string = this.route.snapshot.paramMap.get('genere');
          await this.libroService.findAllBooksByGenre(genere).then(libri => {libri.forEach(libro => this.libri.push(libro))});
          this.titoloPagina = "Libri di " + genere;
          break;
        }
        case "titolo": {
          var titolo: string = this.route.snapshot.paramMap.get('titolo');
          await this.libroService.findAllBooksContainingTitolo(titolo).then(libri => {libri.forEach(libro => this.libri.push(libro))});
          this.titoloPagina = "Libri con " + titolo + " nel titolo";
          break;
        }
      }
    });
    await r.toPromise()
      .catch(error => {
      let statusCode = error.error.status;
      alert(error.error.status + ": " + error.error.message)
      if(statusCode == 401) {
        this.router.navigate(['/login']);
      }
      else this.router.navigate(['/']);
    });

  }
  ngOnInit() {

  }
  ngOnDestroy() {
    if(this.navigationSubscription) {
      this.navigationSubscription.unsubscribe();
    }
  }

  async deleteLibro(id: number) {
    if(this.authenticationService.adminAuth()) {
      await this.libroService.deleteBook(id)
        .catch(error => {
          let statusCode = error.error.status;
          alert(error.error.status + ": " + error.error.message)
          if(statusCode == 401) {
            this.router.navigate(['/login']);
          }
          else this.router.navigate(['/']);
        });
      this.libri.forEach(libro => {
        if (libro.id == id) {
          this.libri.splice(this.libri.indexOf(libro), 1);
        }
      });
    }
  }


}
