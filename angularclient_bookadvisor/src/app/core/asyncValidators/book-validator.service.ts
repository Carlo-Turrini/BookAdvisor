import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AbstractControl} from "@angular/forms";

@Injectable({
  providedIn: 'root'
})
export class BookValidatorService {
  private timeout;
  private URL = "http://localhost:8080/libri";

  constructor(private readonly http: HttpClient) { }

  validateTitolo(control: AbstractControl): Promise<{[key: string]: boolean}> {
    clearTimeout(this.timeout);
    const value = control.value;

    //Non effettuo chiamate al server quando l'input è vuoto o più corto di due caratteri
    if(!value || value.length < 2) {
      return Promise.resolve(null);
    }

    return new Promise((resolve, reject) => {
      this.timeout = setTimeout(() => {
        let finalUrl: string = this.URL + "/isTitleUnique";
        this.http.post<boolean>(finalUrl, control.value).subscribe(flag => {
            if(!flag) {
              resolve({'titoloTaken': true});
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
