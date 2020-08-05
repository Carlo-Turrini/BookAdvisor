import { Injectable } from '@angular/core';
import {CookieService} from "ngx-cookie-service";
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import { v4 as uuidv4 } from 'uuid';

@Injectable({
  providedIn: 'root'
})
export class XsrfInterceptorService implements HttpInterceptor{
  private xsrfToken = uuidv4();
  constructor(private cookieService: CookieService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    this.cookieService.set('XSRF-TOKEN', this.xsrfToken, undefined,'/', 'localhost');
    let xsrfReq = req.clone({setHeaders: { 'X-XSRF-TOKEN': this.xsrfToken}, withCredentials: true});
    return next.handle(xsrfReq);
  }
}
