import {Component, OnDestroy, OnInit} from '@angular/core';
import {Libro} from "../../core/model/libro";
import {ActivatedRoute, NavigationEnd, Router} from "@angular/router";
import {LibroService} from "../../core/services/libro.service";
import {AuthenticationService} from "../../core/services/authentication.service";
import {HttpClient} from "@angular/common/http";
import {LibroForm} from "../../core/model/libro-form";
import {LibroCard} from "../../core/model/libro-card";
import {MyBooksService} from "../../core/services/my-books.service";
import {OverallRatingsForBook} from "../../core/model/overall-ratings-for-book";

@Component({
  selector: 'app-libro',
  templateUrl: './libro.component.html',
  styleUrls: ['./libro.component.css']
})
export class LibroComponent implements OnInit, OnDestroy {
  libro: Libro;
  altriLibriSaga: LibroCard[] = [];
  update: boolean = false;
  upload: boolean = false;
  recensione: boolean = false;
  shelves: string[] = ["read", "reading", "toRead"];
  navigationSubscription;
  reinit: boolean = false;

  constructor(private http: HttpClient, private router: Router, private route: ActivatedRoute, private libroService: LibroService, private authenticationService: AuthenticationService, private myBooksService: MyBooksService) {
    this.libro = new Libro();
    this.navigationSubscription = this.router.events.subscribe((e:any) => {
      if( e instanceof NavigationEnd) {
        this.initialise();
      }
    });
  }

  public async initialise() {
    window.scrollTo(0,0);
    this.altriLibriSaga = [];
    this.update = false;
    this.upload = false;
    this.recensione = false;
    await this.libroService.findBook(parseInt(this.route.snapshot.paramMap.get('id'))).then(data => {this.libro = data;})
      .catch(error => {
        let statusCode = error.error.status;
        alert(error.error.status + ": " + error.error.message)
        if(statusCode == 401) {
          this.router.navigate(['/login']);
        }
        else this.router.navigate(['/']);
      });
    if(this.libro.saga) {
      await this.libroService.getBooksInSaga(this.libro.titoloSaga, this.libro.id).then(libri => {libri.forEach(libro => {this.altriLibriSaga.push(libro);})})
        .catch(error => {
          let statusCode = error.error.status;
          alert(error.error.status + ": " + error.error.message)
          if(statusCode == 401) {
            this.router.navigate(['/login']);
          }
          else this.router.navigate(['/']);
        });
    }
    this.reinitialiseSubComponents();
  }

  reinitialiseSubComponents() {
    this.reinit = true;
    setTimeout(() => { this.reinit = false;});
  }

  ngOnInit() {
  }

  ngOnDestroy() {
    if(this.navigationSubscription) {
      this.navigationSubscription.unsubscribe();
    }
  }

  uploadedHandler(photoUrl: any) {
    if(this.authenticationService.adminAuth()) {
      this.libro.copertina = photoUrl;
      this.upload = false;
    }
  }


  updateLibro() {
    if(this.authenticationService.adminAuth()) {
      this.update = true;
      this.upload = false;
      this.recensione = false;
    }
  }

  uploadPhoto() {
    if(this.authenticationService.adminAuth()) {
      this.upload = true;
      this.update = false;
      this.recensione = false;
    }
  }

  resetUpdateUpload() {
    this.upload = false;
    this.update = false;
    this.recensione = false;
  }


  updatedHandler(libroForm: LibroForm) {
    if(this.authenticationService.adminAuth()) {
      this.libro.titolo = libroForm.titolo;
      this.libro.autori = libroForm.autori;
      this.libro.annoPubblicazione = libroForm.annoPubblicazione;
      this.libro.pagine = libroForm.pagine;
      this.libro.sinossi = libroForm.sinossi;
      this.libro.generi = libroForm.generi;
      this.libro.saga = libroForm.saga;
      this.libro.titoloSaga = libroForm.titoloSaga;
      this.libro.numInSaga = libroForm.numInSaga;
      this.update = false;
    }
    else {
      this.router.navigate(['/']);
    }
  }

  async deleteLibro() {
    if(this.authenticationService.adminAuth()) {
      await this.libroService.deleteBook(this.libro.id)
        .catch(error => {
          let statusCode = error.error.status;
          alert(error.error.status + ": " + error.error.message)
          if(statusCode == 401) {
            this.router.navigate(['/login']);
          }
          else this.router.navigate(['/']);
        });
      this.router.navigate(['/']);
    }
  }

  addReview() {
    if(this.authenticationService.logged) {
      this.recensione = true;
    }
  }

  async updateMyBooks(event: any) {
    if(this.authenticationService.logged) {
      let newShelf: string = event.target.value;
      if (this.libro.bookShelf != null) {
        await this.myBooksService.updateMyBook(this.libro.id, this.authenticationService.loggedUser.id, newShelf).then(_ => {
          this.libro.bookShelf = newShelf;
        })
          .catch(error => {
            let statusCode = error.error.status;
            alert(error.error.status + ": " + error.error.message)
            if(statusCode == 401) {
              this.router.navigate(['/login']);
            }
            else this.router.navigate(['/']);
          });
      } else {
        await this.myBooksService.addMyBookForUser(this.libro.id, this.authenticationService.loggedUser.id, newShelf).then(shelf => {
          this.libro.bookShelf = shelf.shelf;
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
    }
  }

  async reviewSubmitted() {
    await this.libroService.getBookOverallRating(this.libro.id).then(data => {
      this.libro.overallRating = data.overallRating;
      this.libro.overallPageTurnerRating = data.overallPageTurnerRating;
      this.libro.overallOriginalityRating = data.overallOriginalityRating;
      this.libro.overallWritingQualityRating = data.overallWritingQualityRating;
    });

    this.recensione = false;
  }

}
