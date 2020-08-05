/// <reference types="Cypress" />

describe('Prize Test Suite', () => {
    it('should create prize and delete it', () => {
        let title = 'Nuovo Titolo';
        cy.loginAsAdmin();
        cy.createBook(title);
        cy.get('[data-cy=prizeList]').should('be.visible');
        cy.get('[data-cy=newPrizeButton]').should('be.visible');
        cy.get('[data-cy=addPrize]').should('not.be.visible');
        cy.get('[data-cy=newPrizeButton]').click().wait(500);
        cy.get('[data-cy=prizeList]').should('not.be.visible');
        cy.get('[data-cy=addPrize]').should('not.be.undefined');
        cy.get('[data-cy=prizeName]').type('Premio di prova').wait(500);
        cy.get('[data-cy=yearAwarded]').type(2013);
        cy.get('[data-cy=prizeForm]').submit().wait(500);
        cy.get('[data-cy=prizeList]').should('be.visible'); 
        cy.get('[data-cy=addPrize]').should('not.be.visible');
        cy.get('[data-cy=prizeList]').contains('[data-cy=prizeName]', 'Premio di prova').as('premio');
        cy.get('@premio').should('exist');
        cy.get('@premio').parent().siblings().find('[data-cy=elimina]').click().wait(500);
        cy.get('@premio').parent().parent().parent().parent().parent().parent().siblings().find('[data-cy=conferma]').click().wait(500);
        cy.get('@premio').should('not.exist');
        cy.deleteBook(title);

        
    });
})