import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {UtenteFormComponent} from "./user/utente-form/utente-form.component";
import {BookFormComponent} from "./book/book-form/book-form.component";
import {LoginComponent} from "./login/login.component";
import {UtenteComponent} from "./user/utente/utente.component";
import {LibriListComponent} from "./book/libri-list/libri-list.component";
import {UtenteListComponent} from "./user/utente-list/utente-list.component";
import {LibroComponent} from "./book/libro/libro.component";
import {HomeComponent} from "./home/home.component";
import {AuthorListComponent} from "./author/author-list/author-list.component";
import {AuthorComponent} from "./author/author/author.component";


const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  { path: 'new-user',
    component: UtenteFormComponent
  },
  {
    path: 'new-book',
    component: BookFormComponent
  },
  {
    path: 'utente/:id',
    component: UtenteComponent
  },
  {
    path: 'libri',
    children: [
      {path: 'allByGenere/:genere', component: LibriListComponent, data: {tipo: 'genere'}, runGuardsAndResolvers: 'paramsChange'},
      {path: 'allContainingTitolo/:titolo', component: LibriListComponent, data: {tipo: 'titolo'}, runGuardsAndResolvers: 'paramsChange'},
      {path: '', component: LibriListComponent, data: {tipo: 'all'}}

    ]
  },
  {
    path: 'authors/:id',
    component: AuthorComponent
  },
  {
    path: 'authors',
    component: AuthorListComponent
  },
  {
    path: 'utenti',
    component: UtenteListComponent
  },
  {
    path: 'libro/:id',
    component: LibroComponent,
    runGuardsAndResolvers: 'paramsChange'
  },
  {
    path: 'home',
    component: HomeComponent
  },
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  }

];

@NgModule({
  imports: [RouterModule.forRoot(routes, {onSameUrlNavigation: 'reload'})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
