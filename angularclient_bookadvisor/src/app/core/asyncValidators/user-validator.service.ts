import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AbstractControl} from "@angular/forms";

@Injectable({
  providedIn: 'root'
})
export class UserValidatorService {

  private timeout;
  private URL = "http://localhost:8080/api/utenti";

  constructor(private readonly http: HttpClient) { }

  validateUsername(control: AbstractControl): Promise<{[key: string]: boolean}> {
    clearTimeout(this.timeout);
    const value = control.value;

    //Non effettuo chiamate al server quando l'input è vuoto o più corto di due caratteri
    if(!value || value.length < 2) {
      return Promise.resolve(null);
    }

    return new Promise((resolve, reject) => {
      this.timeout = setTimeout(() => {
        let finalUrl: string = this.URL + "/isUsernameUnique";
        this.http.post<boolean>(finalUrl, control.value).subscribe(flag => {
          if(!flag) {
            resolve({'usernameTaken': true});
          }
          else {
            resolve(null);
          }
        },
          (err) => { console.log(err) });
      }, 500)
    })
  }

  validateEmail(control: AbstractControl): Promise<{[key: string]: boolean}> {
    clearTimeout(this.timeout);
    const value = control.value;

    //Non effettuo chiamate al server quando l'input è vuoto o più corto di due caratteri
    if(!value || value.length < 2) {
      return Promise.resolve(null);
    }

    return new Promise((resolve, reject) => {
      this.timeout = setTimeout(() => {
        let finalUrl: string = this.URL + "/isEmailUnique";
        this.http.post<boolean>(finalUrl, control.value).subscribe(flag => {
            if(!flag) {
              resolve({'emailTaken': true});
            }
            else {
              resolve(null);
            }
          },
          (err) => { console.log(err) });
      }, 500)
    })
  }
}
