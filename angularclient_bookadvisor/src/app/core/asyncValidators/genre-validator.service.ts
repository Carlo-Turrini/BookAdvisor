import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AbstractControl} from "@angular/forms";

@Injectable({
  providedIn: 'root'
})
export class GenreValidatorService {
  private timeout;
  private URL = "http://localhost:8080/api/genres";

  constructor(private readonly http: HttpClient) { }

  validateGenreUniqueness(control: AbstractControl): Promise<{[key: string]: boolean}> {
    clearTimeout(this.timeout);
    const value = control.value;

    //Non effettuo chiamate al server quando l'input è vuoto o più corto di due caratteri
    if(!value || value.length < 2) {
      return Promise.resolve(null);
    }

    return new Promise((resolve, reject) => {
      this.timeout = setTimeout(() => {
        let finalUrl: string = this.URL + "/isGenreUnique";
        this.http.post<boolean>(finalUrl, control.value).subscribe(flag => {
            if(!flag) {
              resolve({'genreTaken': true});
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
