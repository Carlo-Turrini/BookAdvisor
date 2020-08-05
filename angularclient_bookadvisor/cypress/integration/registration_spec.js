/// <reference types="Cypress" />

describe('Registration Test Suite', () => {
    it('should register new user and login, visit profile and delete', () => {
        cy.visit('/');
        cy.location('pathname').should('be.equal', '/home');
        cy.get('[data-cy=login]').click().wait(500);
        cy.location('pathname').should('be.equal', '/login');
        cy.get('[data-cy=registration]').click().wait(500);
        cy.location('pathname').should('be.equal', '/new-user');
        cy.get('[data-cy=name]').type('Name');
        cy.get('[data-cy=surname]').type('Surname');
        cy.get('[data-cy=username]').type('TestUser').wait(500);
        cy.get('[data-cy=password]').type('password');
        cy.get('[data-cy=email]').type('test@gmail.com').wait(500);
        cy.get('[data-cy=description]').type('Test description');
        cy.get('[data-cy=userForm]').submit().wait(500);
        cy.location('pathname').should('be.equal', '/login');
        cy.get('[data-cy=username]').type('TestUser');
        cy.get('[data-cy=password').type('password');
        cy.get('[data-cy=loginForm]').submit().wait(500);
        cy.location('pathname').should('be.equal', '/home');
        cy.getCookie('access_token').should('exist');
        cy.get('[data-cy=profile]').click().wait(500);
        cy.location('pathname').should('include', '/utente/');
        cy.get('[data-cy=delete]').click();
        cy.get('[data-cy=conferma]').click().wait(500);
        cy.location('pathname').should('be.equal', '/home');
        cy.getCookie('access_token').should('not.exist');
    });

    it('should not complete registration due to errors: usernameTaken and emailTaken', () => {
        let username = 'TestUser';
        let email = 'test@gmail.com';
        cy.createUser(username, email);
        cy.get('[data-cy=registration]').click().wait(500);
        cy.location('pathname').should('be.equal', '/new-user');
        cy.get('[data-cy=name]').type('Name');
        cy.get('[data-cy=surname]').type('Surname');
        cy.get('[data-cy=username]').type(username).wait(500);
        cy.get('[data-cy=password]').type('password');
        cy.get('[data-cy=email]').type(email).wait(500);
        cy.get('[data-cy=description]').type('Test description');
        cy.get('[data-cy=userForm]').submit().wait(500);
        cy.location('pathname').should('be.equal', '/new-user');
        cy.get('[data-cy=usernameTaken]').should('be.visible');
        cy.get('[data-cy=emailTaken]').should('be.visible');
        cy.login(username, 'password');
        cy.deleteLoggedInUser();
    });

})