import {Component, OnChanges} from '@angular/core';
import {CookieService} from "ngx-cookie-service";
import {HttpClient} from "@angular/common/http";
import {AuthenticationService} from "./core/services/authentication.service";
import {Router} from "@angular/router";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {GenreService} from "./core/services/genre.service";
import {GenreFormComponent} from "./book/genre-form/genre-form.component";
import {BookFormComponent} from "./book/book-form/book-form.component";


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'BookAdvisor';
  private submitted: boolean = false;
  generi : string[] = [];
  updateGenresSubscription = null;
  titleSearchForm = new FormGroup({
      titolo: new FormControl('', [Validators.required])
    });

  constructor(private cookieService: CookieService, private http: HttpClient, public authenticationService: AuthenticationService, private router: Router, private genreService: GenreService) {
    this.initialise();
  }
  async initialise() {
    await this.genreService.getAllGenres().then(genres => {genres.forEach(genre => {this.generi.push(genre)})})
      .catch(error => {
        let statusCode = error.error.status;
        alert(error.error.status + ": " + error.error.message)
        if(statusCode == 401) {
          this.router.navigate(['/login']);
        }
        else this.router.navigate(['/']);
      });
  }

  logout() {
    if(this.authenticationService.logged) {
      this.authenticationService.logoutUser();
      this.router.navigate(['/']);
    }
  }

  login() {
    var state = this.router.routerState;
    this.router.navigate(["/login"], {queryParams: {returnUrl: state.snapshot.url}});
  }

   onSubmit() {
    this.submitted = true;
    this.router.navigate(["/libri/allContainingTitolo/" + this.titleSearchForm.value.titolo]);
    this.titleSearchForm.reset();
    this.submitted = false;
  }

  onActivate(component) {
    console.log(component);
    if(component instanceof BookFormComponent) {
      this.updateGenresSubscription = component.updateGenres.subscribe(data => {this.generi.push(data)});
    }
  }

  onDeactivate() {
    if(this.updateGenresSubscription) {
      this.updateGenresSubscription.unsubscribe();
    }
  }

}
