import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {LibroService} from "../core/services/libro.service";
import {UtenteService} from "../core/services/utente.service";
import {AuthenticationService} from "../core/services/authentication.service";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthorService} from "../core/services/author.service";

@Component({
  selector: 'app-photo-upload',
  templateUrl: './photo-upload.component.html',
  styleUrls: ['./photo-upload.component.css']
})
export class PhotoUploadComponent implements OnInit {

  @Input() private tipo: string;
  @Input() private id: number;
  private submitted = true;
  @Input() private photoUrl: any;
  private photo$: Observable<any>;
  private message: string;
  private messageChanged: boolean = false;
  private selectedFile: any;
  private photoChanged: boolean = false;
  @Output() private uploaded: EventEmitter<any> = new EventEmitter<any>();
  private photoForm  = new FormGroup( {
    photo: new FormControl('', Validators.required)
  });

  constructor(private http: HttpClient, private libroService: LibroService, private utenteService: UtenteService, private authorService: AuthorService, private authenticationService: AuthenticationService, private router: Router, private route: ActivatedRoute) { }

  ngOnInit() {
    if(this.tipo == 'profilo' && !this.authenticationService.sameUserOrAdminAuth(this.id)) {
      this.router.navigate(['/']);
    }
    else if(this.tipo == 'copertina' && !this.authenticationService.adminAuth()) {
      this.router.navigate(['/']);
    }
    else if(this.tipo == 'autore' && !this.authenticationService.adminAuth()) {
      this.router.navigate(['/']);
    }
  }

  public onFileChanged(files) {
    var imagePath;
    if (files.length === 0)
      return;

    var mimeType = files[0].type;
    if (mimeType.match(/image\/*/) == null) {
      this.message = "Only images are supported.";
      this.messageChanged = true;
      return;
    }
    this.selectedFile = files[0];
    this.photoChanged = true;
    var reader = new FileReader();
    imagePath = files;
    reader.readAsDataURL(files[0]);
    reader.onload = (_event) => {
      this.photoUrl = reader.result;
    }
  }
  async onUpload() {
    this.submitted = true;
    if(this.tipo == "profilo" && this.authenticationService.sameUserOrAdminAuth(this.id)) {
      await this.utenteService.uploadProfilePhoto(this.selectedFile, this.id).then(data => {
          console.log(data)
          this.photoUrl = data.img;
        })
        .catch(error => {
          let statusCode = error.error.status;
          alert(error.error.status + ": " + error.error.message)
          if(statusCode == 401) {
            this.router.navigate(['/login']);
          }
          else this.router.navigate(['/']);
        });
    }
    else if(this.tipo == "copertina" && this.authenticationService.adminAuth()) {
      await this.libroService.uploadCoverPhoto(this.selectedFile, this.id).then(data => {
        console.log(data)
        this.photoUrl = data.img;
      })
      .catch(error => {
          let statusCode = error.error.status;
          alert(error.error.status + ": " + error.error.message)
          if(statusCode == 401) {
            this.router.navigate(['/login']);
          }
          else this.router.navigate(['/']);
        });
    }
    else if(this.tipo == "autore" && this.authenticationService.adminAuth()) {
      await this.authorService.uploadAuthorsPhoto(this.id, this.selectedFile).then(data => {
          console.log(data)
          this.photoUrl = data.img;
        })
        .catch(error => {
          let statusCode = error.error.status;
          alert(error.error.status + ": " + error.error.message)
          if(statusCode == 401) {
            this.router.navigate(['/login']);
          }
          else this.router.navigate(['/']);
        });
    }
    else this.router.navigate(['/']);
    this.uploaded.emit(this.photoUrl);
    this.messageChanged = true;
    this.message = "Upload avvenuto con successo!";
    this.photoForm.reset();
    this.submitted = false;
  }

  resetMessageChanged() {
    this.messageChanged = false;
    this.message = '';
  }

  isInvalidAndDirty(field: string) {
    const ctrl = this.photoForm.get(field);
    return !ctrl.valid && ctrl.dirty;
  }

  hasError(field: string, error: string) {
    const ctrl = this.photoForm.get(field);
    return ctrl.dirty && ctrl.hasError(error);
  }

}
