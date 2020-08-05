import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {BarRatingModule} from "ngx-bar-rating";
import {PhotoUploadModule} from "../photo-upload/photo-upload.module";
import {AppRoutingModule} from "../app-routing.module";
import {ReactiveFormsModule} from "@angular/forms";
import {BookRankingComponent} from "./book-ranking/book-ranking.component";
import {BookRankingFormComponent} from "./book-ranking-form/book-ranking-form.component";
import {MyBooksListComponent} from "./my-books-list/my-books-list.component";
import {UtenteFormComponent} from "./utente-form/utente-form.component";
import {UtenteUpdateComponent} from "./utente-update/utente-update.component";
import {UtenteComponent} from "./utente/utente.component";
import {UtenteListComponent} from "./utente-list/utente-list.component";
import {ReviewModule} from "../review/review.module";



@NgModule({
  declarations: [
    BookRankingComponent,
    BookRankingFormComponent,
    MyBooksListComponent,
    UtenteFormComponent,
    UtenteUpdateComponent,
    UtenteComponent,
    UtenteListComponent
  ],
  imports: [
    CommonModule,
    BarRatingModule,
    PhotoUploadModule,
    AppRoutingModule,
    ReactiveFormsModule,
    ReviewModule
  ],
  exports: [
    BookRankingComponent,
    BookRankingFormComponent,
    MyBooksListComponent,
    UtenteFormComponent,
    UtenteUpdateComponent,
    UtenteComponent,
    UtenteListComponent
  ]
})
export class UserModule { }
