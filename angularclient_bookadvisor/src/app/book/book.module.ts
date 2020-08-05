import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {BarRatingModule} from "ngx-bar-rating";
import {PhotoUploadModule} from "../photo-upload/photo-upload.module";
import {AppRoutingModule} from "../app-routing.module";
import {ReactiveFormsModule} from "@angular/forms";
import {BookFormComponent} from "./book-form/book-form.component";
import {AuthorModule} from "../author/author.module";
import {GenreFormComponent} from "./genre-form/genre-form.component";
import {LibriListComponent} from "./libri-list/libri-list.component";
import {UpdateLibroComponent} from "./update-libro/update-libro.component";
import {ReviewFormComponent} from "../review/review-form/review-form.component";
import {LibroComponent} from "./libro/libro.component";
import {PrizeListComponent} from "./prize-list/prize-list.component";
import {PrizeFormComponent} from "./prize-form/prize-form.component";
import {ReviewModule} from "../review/review.module";



@NgModule({
  declarations: [
    BookFormComponent,
    GenreFormComponent,
    LibriListComponent,
    UpdateLibroComponent,
    LibroComponent,
    PrizeListComponent,
    PrizeFormComponent
  ],
  imports: [
    CommonModule,
    BarRatingModule,
    PhotoUploadModule,
    AppRoutingModule,
    ReactiveFormsModule,
    AuthorModule,
    ReviewModule
  ],
  exports: [
    BookFormComponent,
    GenreFormComponent,
    LibriListComponent,
    UpdateLibroComponent,
    LibroComponent,
    PrizeListComponent,
    PrizeFormComponent
  ]
})
export class BookModule { }
