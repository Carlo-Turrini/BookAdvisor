import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {AuthenticationService} from "../../core/services/authentication.service";
import {MyBooksService} from "../../core/services/my-books.service";
import {MyBooks} from "../../core/model/my-books";

@Component({
  selector: 'app-my-books-list',
  templateUrl: './my-books-list.component.html',
  styleUrls: ['./my-books-list.component.css']
})
export class MyBooksListComponent implements OnInit {
  myBooksRead: MyBooks[] = [];
  myBooksReading: MyBooks[] = [];
  myBooksToRead: MyBooks[] = [];
  shelves: string[] = ["read", "reading", "toRead"];
  @Input() userID: number;
  @Output() private rankModified: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor(private route: ActivatedRoute, private router: Router, private myBooksService: MyBooksService, public authenticationService: AuthenticationService) { }

  async ngOnInit() {
    await this.myBooksService.getAllUsersMyBooks(this.userID).then(books => {books.forEach(book => {
      if(book.shelf == "read") {
        this.myBooksRead.push(book);
      }
      else if(book.shelf == "reading") {
        this.myBooksReading.push(book);
      }
      else if(book.shelf == "toRead") {
        this.myBooksToRead.push(book);
      }
    })})
      .catch(error => {
        let statusCode = error.error.status;
        alert(error.error.status + ": " + error.error.message)
        if(statusCode == 401) {
          this.router.navigate(['/login']);
        }
        else this.router.navigate(['/']);
      });
  }

  async deleteMyBooks(bookID: number, shelf: string) {
    if(this.authenticationService.sameUserOrAdminAuth(this.userID)) {
      await this.myBooksService.deleteBookFromMyBooks(bookID, this.userID).then(isRankModified => {
          if(isRankModified) {
            this.rankModified.emit(isRankModified);
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
      if(shelf == "read") {
        this.myBooksRead.forEach(book => {
          if(book.bookID == bookID) {
            this.myBooksRead.splice(this.myBooksRead.indexOf(book), 1);
          }
        });
      }
      else if(shelf == "reading") {
        this.myBooksReading.forEach(book => {
          if(book.bookID == bookID) {
            this.myBooksReading.splice(this.myBooksReading.indexOf(book), 1);
          }
        });
      }
      else if(shelf == "toRead") {
        this.myBooksToRead.forEach(book => {
          if(book.bookID == bookID) {
            this.myBooksToRead.splice(this.myBooksToRead.indexOf(book), 1);
          }
        });
      }
    }
    else this.router.navigate(['/']);
  }

  async updateMyBooks(bookID: number, oldShelf: string, event: any) {
    let newShelf: string = event.target.value;
    if(this.authenticationService.sameUserOrAdminAuth(this.userID)) {
      await this.myBooksService.updateMyBook(bookID, this.userID, newShelf).then(isRankModified => {
        if(isRankModified) {
          this.rankModified.emit(isRankModified);
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
      if(oldShelf == "read") {
        this.myBooksRead.forEach(book => {
          if(book.bookID == bookID) {
            this.myBooksRead.splice(this.myBooksRead.indexOf(book), 1);
            this.bookToShelf(book, newShelf);
          }
        });
      }
      else if(oldShelf == "reading") {
        this.myBooksReading.forEach(book => {
          if(book.bookID == bookID) {
            this.myBooksReading.splice(this.myBooksReading.indexOf(book), 1);
            this.bookToShelf(book, newShelf);
          }
        });
      }
      else if(oldShelf == "toRead") {
        this.myBooksToRead.forEach(book => {
          if(book.bookID == bookID) {
            this.myBooksToRead.splice(this.myBooksToRead.indexOf(book), 1);
            this.bookToShelf(book, newShelf);
          }
        });
      }
    }
    else this.router.navigate(['/']);
  }

  bookToShelf(book: MyBooks, shelf: string) {
    book.shelf = shelf;
    if(shelf == "read") {
      this.myBooksRead.push(book);
    }
    else if(shelf == "reading") {
      this.myBooksReading.push(book);
    }
    else if(shelf == "toRead") {
      this.myBooksToRead.push(book);
    }
  }

}
