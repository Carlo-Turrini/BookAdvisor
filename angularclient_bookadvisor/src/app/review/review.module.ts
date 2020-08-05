import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ReviewListComponent} from "./review-list/review-list.component";
import {ReviewFormComponent} from "./review-form/review-form.component";
import {BarRatingModule} from "ngx-bar-rating";
import {PhotoUploadModule} from "../photo-upload/photo-upload.module";
import {AppRoutingModule} from "../app-routing.module";
import {ReactiveFormsModule} from "@angular/forms";



@NgModule({
  declarations: [
    ReviewListComponent,
    ReviewFormComponent
  ],
  imports: [
    CommonModule,
    BarRatingModule,
    PhotoUploadModule,
    AppRoutingModule,
    ReactiveFormsModule,
  ],
  exports: [
    ReviewListComponent,
    ReviewFormComponent
  ]
})
export class ReviewModule { }
