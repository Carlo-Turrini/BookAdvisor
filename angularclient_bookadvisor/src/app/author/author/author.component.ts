import {Component, OnDestroy, OnInit} from '@angular/core';
import {Author} from "../../core/model/author";
import {LibroCard} from "../../core/model/libro-card";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, NavigationEnd, Router} from "@angular/router";
import {LibroService} from "../../core/services/libro.service";
import {AuthenticationService} from "../../core/services/authentication.service";
import {AuthorService} from "../../core/services/author.service";
import {AuthorForm} from "../../core/model/author-form";

@Component({
  selector: 'app-author',
  templateUrl: './author.component.html',
  styleUrls: ['./author.component.css']
})
export class AuthorComponent implements OnInit, OnDestroy {
  author: Author;
  booksByAuthor: LibroCard[] = [];
  update: boolean = false;
  upload: boolean = false;
  navigationSubscription
  constructor(private http: HttpClient, private router: Router, private route: ActivatedRoute, private authorService : AuthorService, private bookService: LibroService,  public authenticationService: AuthenticationService) {
    this.author = new Author();
    this.navigationSubscription = this.router.events.subscribe((e:any) => {
      if( e instanceof NavigationEnd) {
        this.initialise();
      }
    });
  }

  public async initialise() {
    window.scrollTo(0, 0);
    this.booksByAuthor = [];
    this.update = false;
    this.upload = false;
    await this.authorService.getAuthor(parseInt(this.route.snapshot.paramMap.get('id'))).then(data => {this.author = data})
      .catch(error => {
        let statusCode = error.error.status;
        alert(error.error.status + ": " + error.error.message)
        if(statusCode == 401) {
          this.router.navigate(['/login']);
        }
        else this.router.navigate(['/']);
      });
    await this.bookService.findAllBooksByAuthor(this.author.authorsFullname).then(books => {books.forEach(book => this.booksByAuthor.push(book))})
      .catch(error => {
        let statusCode = error.error.status;
        alert(error.error.status + ": " + error.error.message)
        if(statusCode == 401) {
          this.router.navigate(['/login']);
        }
        else this.router.navigate(['/']);
      });
  }
  ngOnDestroy() {
    if(this.navigationSubscription) {
      this.navigationSubscription.unsubscribe();
    }
  }

  ngOnInit() {
  }

  uploadedHandler(photoUrl: any) {
    if(this.authenticationService.adminAuth()) {
      this.author.authorsPhoto = photoUrl;
      this.upload = false;
    }
  }

  updateAuthor() {
    if(this.authenticationService.adminAuth()) {
      this.update = true;
      this.upload = false;
    }
  }

  uploadPhoto() {
    if(this.authenticationService.adminAuth()) {
      this.upload = true;
      this.update = false;
    }
  }

  resetUpdateUpload() {
    this.upload = false;
    this.update = false;
  }

  updatedHandler(authorForm: AuthorForm) {
    if(this.authenticationService.adminAuth()) {
      this.author.authorsFullname = authorForm.authorsFullname;
      this.author.birthYear = authorForm.birthYear;
      this.author.deathYear = authorForm.deathYear;
      this.author.biography = authorForm.biography;
      this.update = false;
    }
  }

  async deleteAuthor() {
    if(this.authenticationService.adminAuth()) {
      let success: boolean = true;
      await this.authorService.deleteAuthor(this.author.id)
        .catch(error => {
          success = false;
          let statusCode = error.error.status;
          alert(error.error.status + ": " + error.error.message)
          if(statusCode == 401) {
            this.router.navigate(['/login']);
          }
          else if(statusCode != 403) this.router.navigate(['/']);
        });
      if(success) {
        this.router.navigate(['/']);
      }
    }
  }

}
