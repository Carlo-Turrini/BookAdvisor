/// <reference types="Cypress" />
// ***********************************************************
// This example support/index.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import './commands'
import '@cypress/code-coverage/support';

// Alternatively you can use CommonJS syntax:
// require('./commands')

Cypress.Commands.add('loginAsAdmin', () => {
    cy.visit('/');
    cy.location('pathname').should('be.equal', '/home');
    cy.get('[data-cy=login]').click();
    cy.location('pathname').should('be.equal', '/login');
    cy.get('[data-cy=username]').type('MarioRossi');
    cy.get('[data-cy=password').type('password');
    cy.get('[data-cy=loginForm]').submit().wait(500);
    cy.location('pathname').should('be.equal', '/home');
    cy.getCookie('access_token').should('exist');
    cy.get('[data-cy=newBook]').should('be.visible');
});

Cypress.Commands.add('loginAsUser', () => {
    cy.visit('/');
    cy.location('pathname').should('be.equal', '/home');
    cy.get('[data-cy=login]').click();
    cy.location('pathname').should('be.equal', '/login');
    cy.get('[data-cy=username]').type('Tarlo');
    cy.get('[data-cy=password').type('password');
    cy.get('[data-cy=loginForm]').submit().wait(500);
    cy.location('pathname').should('be.equal', '/home');
    cy.getCookie('access_token').should('exist');
    cy.get('[data-cy=newBook]').should('not.be.visible');
});

Cypress.Commands.add('createBook', (title) => {
    cy.get('[data-cy=newBook]').click().wait(500);
    cy.location('pathname').should('be.equal', '/new-book');
    cy.get('[data-cy=book]').should('be.visible');
    cy.get('[data-cy=addAuthor]').should('not.be.visible');
    cy.get('[data-cy=addGenre]').should('not.be.visible');
    cy.get('[data-cy=autori]').select('Robert Jordan');
    cy.get('[data-cy=titolo]').type(title).wait(500);
    cy.get('[data-cy=titoloSaga]').should('not.be.visible');
    cy.get('[data-cy=numInSaga]').should('not.be.visible');
    cy.get('[data-cy=saga]').check({force: true});
    cy.get('[data-cy=titoloSaga]').type('Nuova Saga');
    cy.get('[data-cy=numInSaga]').type(1);
    cy.get('[data-cy=pagine]').type(123);
    cy.get('[data-cy=annoPubblicazione]').type(2012);
    cy.get('[data-cy=generi]').select(['Romanzo', 'Fantasy']);
    cy.get('[data-cy=sinossi').type('sinossi');
    cy.get('[data-cy=bookForm]').submit().wait(500);
    cy.location('pathname').should('include', '/libri/allContainingTitolo/');
    cy.get('[data-cy=libro]').contains('[data-cy=titolo]', title).as('book')
    cy.get('@book').parent().parent().siblings().contains('[data-cy=scopri]', 'Scopri').click().wait(500);
    cy.location('pathname').should('include', '/libro/');
});

Cypress.Commands.add('deleteBook', (title) => {
    cy.get('[data-cy=libri]').click().wait(500);
    cy.get('[data-cy=libro]').contains('[data-cy=titolo]', title).as('book');
    cy.get('@book').parent().siblings().find('[data-cy=elimina]').click().wait(500);
    cy.get('@book').parent().parent().parent().parent().siblings().find('[data-cy=conferma]').click().wait(500);
    cy.location('pathname').should('be.equal', '/libri');
    cy.get('[data-cy=libro]').contains('[data-cy=titolo]', title).should('not.exist');
});

Cypress.Commands.add('createUser', (username, email) => {
    cy.visit('/');
    cy.location('pathname').should('be.equal', '/home');
    cy.get('[data-cy=login]').click();
    cy.location('pathname').should('be.equal', '/login');
    cy.get('[data-cy=registration]').click().wait(500);
    cy.location('pathname').should('be.equal', '/new-user');
    cy.get('[data-cy=name]').type('Name');
    cy.get('[data-cy=surname]').type('Surname');
    cy.get('[data-cy=username]').type(username).wait(500);
    cy.get('[data-cy=password]').type('password');
    cy.get('[data-cy=email]').type(email).wait(500);
    cy.get('[data-cy=description]').type('Test description');
    cy.get('[data-cy=userForm]').submit().wait(500);
    cy.location('pathname').should('be.equal', '/login');
});

Cypress.Commands.add('deleteLoggedInUser', () => {
    cy.get('[data-cy=profile]').click().wait(500);
    cy.location('pathname').should('include', '/utente/');
    cy.get('[data-cy=delete]').click().wait(500);
    cy.get('[data-cy=userDeleteModal]').find('[data-cy=conferma]').click().wait(500);
    cy.location('pathname').should('be.equal', '/home');
    cy.getCookie('access_token').should('not.exist');
});

Cypress.Commands.add('login', (username, password) => {
    cy.visit('/');
    cy.get('[data-cy=login]').click().wait(500);
    cy.get('[data-cy=username]').type(username);
    cy.get('[data-cy=password]').type(password);
    cy.get('[data-cy=loginForm]').submit().wait(500);
    cy.getCookie('access_token').should('exist');
});

Cypress.Commands.add('createAuthor', (fullname) => {
    cy.get('[data-cy=authors]').click().wait(500);
    cy.location('pathname').should('be.equal', '/authors');
    cy.get('[data-cy=newAuthorButton]').click().wait(500);
    cy.get('[data-cy=authorsFullname]').type(fullname).wait(500);
    cy.get('[data-cy=birthYear]').type(1987);
    cy.get('[data-cy=biography]').type('biografia');
    cy.get('[data-cy=authorForm]').submit().wait(500);
    cy.contains('[data-cy=fullname]', fullname).should('exist');
});

Cypress.Commands.add('deleteAuthorFromWithinHisProfilePage', (fullname) => {
    cy.get('[data-cy=eliminaAutore]').click().wait(500);
    cy.get('[data-cy=deleteAuthorModal]').find('[data-cy=conferma]').click().wait(500);
    cy.location('pathname').should('be.equal', '/home');
    cy.get('[data-cy=authors]').click().wait(500);
    cy.location('pathname').should('be.equal', '/authors');
    cy.contains('[data-cy=fullname]', fullname).as('autore').should('not.exist');
})

Cypress.Commands.add('deleteAuthor', (fullname) => {
    cy.get('[data-cy=authors]').click().wait(500);
    cy.location('pathname').should('be.equal', '/authors');
    cy.contains('[data-cy=fullname]', fullname).as('autore').should('exist');
    cy.get('@autore').parent().siblings().find('[data-cy=eliminaAutore]').click().wait(500);
    cy.get('@autore').parentsUntil('[data-cy=autore]').siblings().find('[data-cy=conferma]').click().wait(500);
    cy.contains('[data-cy=fullname]', fullname).as('autore').should('not.exist');
})
