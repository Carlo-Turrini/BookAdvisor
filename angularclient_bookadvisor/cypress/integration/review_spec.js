/// <reference types="Cypress" />

describe('Review Test Suite', () => {
    it('should create a review put a like to the review, unlike the review and delete the review', () => {
        let title = 'Nuovo Titolo';
        let recensione = 'Testo unico per recensione di test';
        cy.loginAsAdmin();
        cy.createBook(title);
        cy.get('[data-cy=newReviewButton]').click().wait(500);
        cy.get('[data-cy=reviewList]').should('not.be.visible');
        cy.get('[data-cy=addReview]').should('not.be.undefined');
        cy.get('[data-cy=rating]').children().first().children().first().children().first().next().next().click({force: true});
        cy.get('[data-cy=originalityRating]').children().first().children().first().children().first().next().next().click({force: true});
        cy.get('[data-cy=writingQualityRating]').children().first().children().first().children().first().next().next().click({force: true});
        cy.get('[data-cy=pageTurnerRating]').children().first().children().first().children().first().next().next().click({force: true});
        cy.get('[data-cy=testo]').type(recensione);
        cy.get('[data-cy=reviewForm]').submit().wait(500);
        cy.get('[data-cy=reviewList]').should('be.visible');
        cy.get('[data-cy=addReview]').should('not.be.visible');
        cy.get('[data-cy=reviewList]').contains('[data-cy=testo]', recensione).as('review');
        cy.get('@review').parents().find('[data-cy=like]').click().wait(500);
        cy.get('@review').parents().contains('[data-cy=like]', ' 1').should('exist');
        cy.get('@review').parents().find('[data-cy=like]').click().wait(500);
        cy.get('@review').parents().contains('[data-cy=like]', ' 0').should('exist');
        cy.get('@review').parent().parent().parent().siblings().find('[data-cy=eliminaRecensione]').find('[data-cy=elimina]').click().wait(500);
        cy.get('@review').parent().parent().parent().parent().parent().parent().parent().siblings().find('[data-cy=conferma]').click().wait(500);
        cy.get('@review').should('not.exist');     
        cy.deleteBook(title);
    });
});