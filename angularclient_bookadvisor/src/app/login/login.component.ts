import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {CookieService} from "ngx-cookie-service";
import {Observable} from "rxjs";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthenticationService} from "../core/services/authentication.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  private loginSuccess: boolean;
  private messageChanged: boolean = false;
  private submitted: boolean = false;
  private returnRoute: ActivatedRoute;
  private message: string = null;
  private loginForm = new FormGroup({
    username: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(24)]),
    password: new FormControl('', [Validators.required, Validators.minLength(8), Validators.maxLength(64)])
  });

  constructor(private http: HttpClient, private cookieService: CookieService, private authenticationService: AuthenticationService, private router: Router, private route: ActivatedRoute) { }

  ngOnInit() {
      this.returnRoute = this.route.snapshot.queryParams['returnUrl'] || '';
      if(this.authenticationService.logged) {
        if(this.returnRoute.toString().includes('login')) {
          this.router.navigate(['/']);
        }
        else this.router.navigate([this.returnRoute]);
      }
  }

  async onSubmit() {
    if (!this.authenticationService.logged) {
      this.submitted = true;
      this.loginSuccess = await this.authenticationService.loginUser(this.loginForm.value.username, this.loginForm.value.password);
      if (!this.loginSuccess) {
        this.message = "Utente o password errati!";
        this.messageChanged = true;
      }
      this.loginForm.reset();
      this.submitted = false;
      if (this.loginSuccess) {
        console.log('*** *** ', this.returnRoute)
        this.router.navigate([this.returnRoute]);
      }
    }
    else {
      this.router.navigate([this.returnRoute]);
    }
  }

  resetMessageChanged() {
    this.messageChanged = false;
  }

  isInvalidAndDirty(field: string) {
    const ctrl = this.loginForm.get(field);
    return !ctrl.valid && ctrl.dirty;
  }

  hasError(field: string, error: string) {
    const ctrl = this.loginForm.get(field);
    return ctrl.dirty && ctrl.hasError(error);
  }
}
