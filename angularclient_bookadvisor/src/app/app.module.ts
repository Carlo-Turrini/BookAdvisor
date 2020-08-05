import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from "@angular/forms";

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {HTTP_INTERCEPTORS, HttpClientModule, HttpClientXsrfModule} from "@angular/common/http";
import {BarRatingModule} from "ngx-bar-rating";
import {CookieService} from "ngx-cookie-service";
import { LoginComponent } from './login/login.component';
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import { HomeComponent } from './home/home.component';
import {XsrfInterceptorService} from "./core/xsrfInterceptor/xsrf-interceptor.service";
import {AuthorModule} from "./author/author.module";
import {BookModule} from "./book/book.module";
import {UserModule} from "./user/user.module";
import {CoreModule} from "./core/core.module";

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    BarRatingModule,
    FontAwesomeModule,
    AuthorModule,
    BookModule,
    UserModule,
    CoreModule
  ],
  providers: [CookieService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: XsrfInterceptorService,
      multi: true
    }],
  bootstrap: [AppComponent]
})
export class AppModule { }
