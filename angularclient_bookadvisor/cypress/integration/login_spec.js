/// <reference types="Cypress" />

describe('Login test suite', () => {
    it('should log in as admin', () => {
        cy.visit('/');
        cy.location('pathname').should('be.equal', '/home');
        cy.get('[data-cy=login]').click().wait(500);
        cy.location('pathname').should('be.equal', '/login');
        cy.get('[data-cy=username]').type('MarioRossi');
        cy.get('[data-cy=password]').type('password');
        cy.get('[data-cy=loginForm]').submit().wait(500);
        cy.location('pathname').should('be.equal', '/home');
        cy.getCookie('access_token').should('exist');
        cy.get('[data-cy=newBook]').should('be.visible');

    });

    it('should log in as user', () => {
        cy.visit('/');
        cy.location('pathname').should('be.equal', '/home');
        cy.get('[data-cy=login]').click().wait(500);
        cy.location('pathname').should('be.equal', '/login');
        cy.get('[data-cy=username]').type('Tarlo');
        cy.get('[data-cy=password]').type('password');
        cy.get('[data-cy=loginForm]').submit().wait(500);
        cy.location('pathname').should('be.equal', '/home');
        cy.getCookie('access_token').should('exist');
        cy.get('[data-cy=newBook]').should('not.be.visible');
    });

    it('should not log in with wrong credentials', () => {
        cy.visit('/');
        cy.location('pathname').should('be.equal', '/home');
        cy.get('[data-cy=login]').click().wait(500)
        cy.location('pathname').should('be.equal', '/login');
        cy.get('[data-cy=username]').type('MarioRossi');
        cy.get('[data-cy=password]').type('wrongPassword');
        cy.get('[data-cy=loginForm]').submit().wait(500);   
        cy.location('pathname').should('be.equal', '/login');
        cy.getCookie('access_token').should('not.exist');
        cy.get('[data-cy=message]').should('be.visible');
    });

    it('should logout after login', () => {
        cy.visit('/');
        cy.location('pathname').should('be.equal', '/home');
        cy.get('[data-cy=login]').click();
        cy.location('pathname').should('be.equal', '/login');
        cy.get('[data-cy=username]').type('Tarlo');
        cy.get('[data-cy=password]').type('password');
        cy.get('[data-cy=loginForm]').submit().wait(500);
        cy.location('pathname').should('be.equal', '/home');
        cy.getCookie('access_token').should('exist');
        cy.get('[data-cy=logout]').click().wait(500);
        cy.getCookie('access_token').should('not.exist');
    });

    it('should redirect to last visited page if you try to login after login', () => {
        cy.visit('/libri');
        cy.location('pathname').should('be.equal', '/libri');
        cy.get('[data-cy=login]').click();
        cy.location('pathname').should('be.equal', '/login');
        cy.get('[data-cy=login]').click();
        cy.location('pathname').should('be.equal', '/login');
        cy.get('[data-cy=username]').type('MarioRossi');
        cy.get('[data-cy=password]').type('password');
        cy.get('[data-cy=loginForm]').submit().wait(500);
        cy.location('pathname').should('be.equal', '/libri');
        cy.go('back');
        cy.location('pathname').should('be.equal', '/home');
    });

    it('should redirect to /home if you try to register user after login', () => {
        cy.visit('/libri');
        cy.location('pathname').should('be.equal', '/libri');
        cy.get('[data-cy=login]').click();
        cy.location('pathname').should('be.equal', '/login');
        cy.get('[data-cy=registration]').click();
        cy.location('pathname').should('be.equal', '/new-user');
        cy.get('[data-cy=login]').click();
        cy.location('pathname').should('be.equal', '/login');
        cy.get('[data-cy=username]').type('MarioRossi');
        cy.get('[data-cy=password]').type('password');
        cy.get('[data-cy=loginForm]').submit().wait(500);
        cy.location('pathname').should('be.equal', '/home');
    });

})