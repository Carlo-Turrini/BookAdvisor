import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {LoggedInfo} from "../model/logged-info";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  logged: boolean = false;
  loggedUser: LoggedInfo = new LoggedInfo();

  private URL = 'http://localhost:8080';
  constructor(private http: HttpClient) {
    let loggedTemp = localStorage.getItem('logged');
    let loggedInfoIDTemp = localStorage.getItem('loggedInfoID');
    let loggedInfoIsAdminTemp = localStorage.getItem('loggedInfoIsAdmin');
    if(loggedTemp != null) {
      this.logged = JSON.parse(loggedTemp);
    }
    if(loggedInfoIDTemp != null && loggedInfoIsAdminTemp != null) {
      this.loggedUser.id= JSON.parse(loggedInfoIDTemp);
      this.loggedUser.admin= JSON.parse(loggedInfoIsAdminTemp);
    }
  }

  public async loginUser(username: string, password: string){
    var logSuccess: boolean = false;
    const req = this.http.post<void>(this.URL + "/authenticate", {username, password}, {withCredentials: true, observe: "response"});
    await req.toPromise().then(result => {
      let statusCode = result.status;
      if(statusCode == 200) {
        logSuccess = true;
      }
      else {
        logSuccess = false;
      }
    }).catch(err => {
      let statusCode = err.status;
      if(statusCode == 401) {
        logSuccess = false;
      }
    });
    if (logSuccess) {
      await this.http.get<LoggedInfo>(this.URL + "/utenti/loggedUserInfo", {withCredentials: true}).toPromise().then(data => {
        this.loggedUser = data;});
      this.logged = true;
      localStorage.setItem('logged', JSON.stringify(this.logged));
      localStorage.setItem('loggedInfoID', JSON.stringify(this.loggedUser.id));
      localStorage.setItem('loggedInfoIsAdmin', JSON.stringify(this.loggedUser.admin));
      console.log(this.loggedUser.admin);

    }
    return logSuccess;
  }

  public async logoutUser() {
    this.resetLogVars();
    var logoutSuccess: boolean = false;
    const r1 = this.http.get<void>(this.URL + "/logoutUser", {withCredentials: true, observe: "response"});
    await r1.toPromise().then(result => {let statusCode = result.status;
      if(statusCode == 200) {
        logoutSuccess = true;
      }
      else {
        logoutSuccess = false;
      }
    })
      .catch(error => {
        alert(error.error.status + ": " + error.error.message)
        logoutSuccess = false;
      });
    return logoutSuccess;
  }

  public sameUserOrAdminAuth(id: number): boolean {
    if(this.logged && (this.loggedUser.id == id || this.loggedUser.admin)) {
      return true;
    }
    else return false;
  }

  public sameUser(id: number): boolean {
    if(this.logged && this.loggedUser.id == id) {
      return true;
    }
    else return false;
  }

  public adminAuth(): boolean {
    if(this.logged && this.loggedUser.admin) {
      return true;
    }
    else return false;
  }

  public resetLogVars() {
    this.logged = false;
    this.loggedUser.id = null;
    this.loggedUser.admin = false;
    localStorage.removeItem('logged');
    localStorage.removeItem('loggedInfoID');
    localStorage.removeItem('loggedInfoIsAdmin');
  }
}
