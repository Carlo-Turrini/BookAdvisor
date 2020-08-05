import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AuthorComponent} from "./author/author.component";
import {BarRatingModule} from "ngx-bar-rating";
import {PhotoUploadModule} from "../photo-upload/photo-upload.module";
import {AuthorUpdateComponent} from "./author-update/author-update.component";
import {AppRoutingModule} from "../app-routing.module";
import {ReactiveFormsModule} from "@angular/forms";
import {AuthorFormComponent} from "./author-form/author-form.component";
import {AuthorListComponent} from "./author-list/author-list.component";



@NgModule({
  declarations: [
    AuthorComponent,
    AuthorUpdateComponent,
    AuthorFormComponent,
    AuthorListComponent
  ],
  imports: [
    CommonModule,
    BarRatingModule,
    PhotoUploadModule,
    AppRoutingModule,
    ReactiveFormsModule
  ],
  exports: [
    AuthorComponent,
    AuthorUpdateComponent,
    AuthorFormComponent,
    AuthorListComponent
  ]
})
export class AuthorModule { }
