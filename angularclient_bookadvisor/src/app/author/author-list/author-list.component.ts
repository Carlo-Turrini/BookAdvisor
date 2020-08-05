import { Component, OnInit } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthenticationService} from "../../core/services/authentication.service";
import {AuthorCard} from "../../core/model/author-card";
import {AuthorService} from "../../core/services/author.service";

@Component({
  selector: 'app-author-list',
  templateUrl: './author-list.component.html',
  styleUrls: ['./author-list.component.css']
})
export class AuthorListComponent implements OnInit {
  private authors: AuthorCard[] = [];
  private  addAuthor: boolean = false;
  private authorFilterString: string = '';
  constructor(private http: HttpClient, private router: Router, private route: ActivatedRoute, private authorService: AuthorService, private authenticationService: AuthenticationService) { }

  //aggiungi bottone form per new Author poi aggiungi un emitter che fornisca il nuovo autore aggiunto sottoforma di card!
  async ngOnInit() {
    await this.authorService.getAllAuthors().then(authors => {authors.forEach(author => this.authors.push(author))})
      .catch(error => {
        let statusCode = error.error.status;
        alert(error.error.status + ": " + error.error.message)
        if(statusCode == 401) {
          this.router.navigate(['/login']);
        }
        else this.router.navigate(['/']);
      });
  }

  async deleteAuthor(id: number) {
    if(this.authenticationService.adminAuth()) {
      let success: boolean = true;
      await this.authorService.deleteAuthor(id)
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
        this.authors.forEach(author => {
          if (author.id == id) {
            this.authors.splice(this.authors.indexOf(author), 1);
          }
        });
      }
    }
    else {
      this.router.navigate(['/']);
    }
  }

  newAuthor() {
    this.addAuthor = true;
  }

  authorAdded(authors: AuthorCard[]) {
    this.authors = authors;
    this.addAuthor = false;
  }

  updateAuthorFilterString(event) {
    this.authorFilterString = event.target.value;
  }

  backToAuthorsList() {
    this.addAuthor = false;
  }

}
