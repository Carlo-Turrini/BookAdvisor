/// <reference types="Cypress" />

describe('Book Test Suite', () => {
    it('should display all books', () => {
        cy.visit('/');
        cy.location('pathname').should('be.equal', '/home');
        cy.get('[data-cy=libri]').click().wait(100);
        cy.location('pathname').should('be.equal', '/libri');
    }); 

    it('should display all books by genre', () => {
        cy.visit('/');
        cy.location('pathname').should('be.equal', '/home');
        cy.get('[data-cy=genreDropdown]').click();
        cy.contains('[data-cy=genre]', 'Romanzo').click().wait(100);
        cy.location('pathname').should('be.equal', '/libri/allByGenere/Romanzo');
    });

    it('should display all books containing titolo', () => {
        cy.visit('/');
        cy.location('pathname').should('be.equal', '/home');
        cy.get('[data-cy=bookTitleForSearch]').type('La ruota del tempo');
        cy.get('[data-cy=searchByTitleForm]').submit().wait(100);
        cy.location('pathname').should('be.equal', '/libri/allContainingTitolo/La%20ruota%20del%20tempo')
    });

    it('should display book', () => {
        cy.visit('/');
        cy.location('pathname').should('be.equal', '/home');
        cy.get('[data-cy=libri]').click().wait(200);
        cy.location('pathname').should('be.equal', '/libri');
        cy.get('[data-cy=libro]').contains('[data-cy=titolo]', 'La ruota del tempo').as('book')
        cy.get('@book').parent().parent().siblings().contains('[data-cy=scopri]', 'Scopri').click().wait(100);
        cy.location('pathname').should('be.equal', '/libro/2');
    });

    it('should create new book and delete it', () => {
        cy.loginAsAdmin();
        cy.get('[data-cy=newBook]').click().wait(500);
        cy.location('pathname').should('be.equal', '/new-book');
        cy.get('[data-cy=book]').should('be.visible');
        cy.get('[data-cy=addAuthor]').should('not.be.visible');
        cy.get('[data-cy=addGenre]').should('not.be.visible');
        cy.get('[data-cy=autori]').select('Robert Jordan');
        cy.get('[data-cy=titolo]').type('Nuovo Titolo').wait(500);
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
        cy.location('pathname').should('be.equal', '/libri/allContainingTitolo/Nuovo%20Titolo');
        cy.get('[data-cy=libro]').contains('[data-cy=titolo]', 'Nuovo Titolo').as('book')
        cy.get('@book').parent().parent().siblings().contains('[data-cy=scopri]', 'Scopri').click().wait(500);
        cy.location('pathname').should('include', '/libro/');
        cy.get('[data-cy=elimina]').should('be.visible');
        cy.get('[data-cy=elimina]').click().wait(500);
        cy.get('[data-cy=conferma]').click().wait(500);
        cy.location('pathname').should('be.equal', '/home');
        cy.get('[data-cy=libri]').click().wait(500);
        cy.location('pathname').should('be.equal', '/libri');
        cy.get('[data-cy=libro]').contains('[data-cy=titolo]', 'Nuovo Titolo').should('not.exist');
    });

    it('should create new book, adding new author and new genre and then delete it', () => {
        cy.loginAsAdmin();
        cy.get('[data-cy=newBook]').click().wait(500);
        cy.location('pathname').should('be.equal', '/new-book');
        cy.get('[data-cy=book]').should('be.visible');
        cy.get('[data-cy=addAuthor]').should('not.be.visible');
        cy.get('[data-cy=addGenre]').should('not.be.visible');
        cy.get('[data-cy=newAuthorButton]').click().wait(500);
        cy.get('[data-cy=book]').should('not.be.visible');
        cy.get('[data-cy=addAuthor]').should('not.be.undefined');
        cy.get('[data-cy=addGenre]').should('not.be.visible');
        cy.get('[data-cy=authorsFullname]').type('Nuovo Autore').wait(500);
        cy.get('[data-cy=birthYear]').type(1987);
        cy.get('[data-cy=biography]').type('biografia');
        cy.get('[data-cy=authorForm]').submit().wait(500);
        cy.get('[data-cy=book]').should('be.visible');
        cy.get('[data-cy=addAuthor]').should('not.be.visible');
        cy.get('[data-cy=addGenre]').should('not.be.visible');
        cy.get('[data-cy=autori]').select('Nuovo Autore');
        cy.get('[data-cy=titolo]').type('Nuovo Titolo').wait(500);
        cy.get('[data-cy=titoloSaga]').should('not.be.visible');
        cy.get('[data-cy=numInSaga]').should('not.be.visible');
        cy.get('[data-cy=saga]').check({force: true});
        cy.get('[data-cy=titoloSaga]').type('Nuova Saga');
        cy.get('[data-cy=numInSaga]').type(1);
        cy.get('[data-cy=pagine]').type(123);
        cy.get('[data-cy=annoPubblicazione]').type(2012);
        cy.get('[data-cy=newGenreButton]').click().wait(500);
        cy.get('[data-cy=book]').should('not.be.visible');
        cy.get('[data-cy=addAuthor]').should('not.be.visible');
        cy.get('[data-cy=addGenre]').should('not.be.undefined');
        cy.get('[data-cy=genere]').type('Nuovo Genere').wait(500);
        cy.get('[data-cy=genreForm]').submit().wait(500);
        cy.get('[data-cy=book]').should('be.visible');
        cy.get('[data-cy=addAuthor]').should('not.be.visible');
        cy.get('[data-cy=addGenre]').should('not.be.visible');
        cy.get('[data-cy=generi]').select(['Romanzo', 'Nuovo Genere']);
        cy.get('[data-cy=sinossi').type('sinossi');
        cy.get('[data-cy=bookForm]').submit().wait(500);
        cy.location('pathname').should('be.equal', '/libri/allContainingTitolo/Nuovo%20Titolo');
        cy.get('[data-cy=libro]').contains('[data-cy=titolo]', 'Nuovo Titolo').as('book')
        cy.get('@book').parent().parent().siblings().contains('[data-cy=scopri]', 'Scopri').click().wait(500);
        cy.location('pathname').should('include', '/libro/');
        cy.get('[data-cy=elimina]').should('be.visible');
        cy.get('[data-cy=elimina]').click().wait(500);
        cy.get('[data-cy=conferma]').click().wait(500);
        cy.location('pathname').should('be.equal', '/home');
        cy.get('[data-cy=libri]').click().wait(500);
        cy.location('pathname').should('be.equal', '/libri');
        cy.get('[data-cy=libro]').contains('[data-cy=titolo]', 'Nuovo Titolo').should('not.exist');
        cy.deleteAuthor('Nuovo Autore');
    });

    it('should create book and delete it from all books page', () => {
        cy.loginAsAdmin();
        cy.get('[data-cy=newBook]').click().wait(500);
        cy.location('pathname').should('be.equal', '/new-book');
        cy.get('[data-cy=book]').should('be.visible');
        cy.get('[data-cy=addAuthor]').should('not.be.visible');
        cy.get('[data-cy=addGenre]').should('not.be.visible');
        cy.get('[data-cy=autori]').select('Robert Jordan');
        cy.get('[data-cy=titolo]').type('Nuovo Titolo').wait(500);
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
        cy.location('pathname').should('be.equal', '/libri/allContainingTitolo/Nuovo%20Titolo');
        cy.get('[data-cy=libri]').click().wait(500);
        cy.get('[data-cy=libro]').contains('[data-cy=titolo]', 'Nuovo Titolo').as('book')
        cy.get('@book').parent().siblings().find('[data-cy=elimina]').click().wait(500);
        cy.get('@book').parent().parent().parent().parent().siblings().find('[data-cy=conferma]').click().wait(500);
        cy.location('pathname').should('be.equal', '/libri');
        cy.get('[data-cy=libro]').contains('[data-cy=titolo]', 'Nuovo Titolo').should('not.exist');
    });

    it('should modify title of book, authors and genres', () => {
        let title = 'Nuovo Titolo';
        let updatedTitle = 'Titolo Aggiornato'
        cy.loginAsAdmin();
        cy.createBook(title);   
        cy.contains('[data-cy=titolo]', title).should('exist');
        cy.get('[data-cy=autori]').contains('[data-cy=autore]', 'Robert Jordan').should('exist');
        cy.get('[data-cy=generi]').contains('[data-cy=genere]', 'Romanzo ').should('exist');
        cy.get('[data-cy=generi]').contains('[data-cy=genere]', 'Fantasy ').should('exist');
        cy.get('[data-cy=modifyDropdown]').click().wait(500);
        cy.get('[data-cy=updateBook]').click().wait(500);
        cy.get('[data-cy=autori]').select('Pablo Neruda');
        cy.get('[data-cy=titolo]').clear().type(updatedTitle).wait(500);
        cy.get('[data-cy=generi]').select(['Biografia', 'Fantascienza']);
        cy.get('[data-cy=updateBookForm]').submit().wait(500);
        cy.contains('[data-cy=titolo]', title).should('not.exist');
        cy.get('[data-cy=autori]').contains('[data-cy=autore]', 'Robert Jordan').should('not.exist');
        cy.get('[data-cy=generi]').contains('[data-cy=genere]', 'Romanzo ').should('not.exist');
        cy.get('[data-cy=generi]').contains('[data-cy=genere]', 'Fantasy ').should('not.exist');
        cy.contains('[data-cy=titolo]', updatedTitle).should('exist');
        cy.get('[data-cy=autori]').contains('[data-cy=autore]', 'Pablo Neruda').should('exist');
        cy.get('[data-cy=generi]').contains('[data-cy=genere]', 'Biografia ').should('exist');
        cy.get('[data-cy=generi]').contains('[data-cy=genere]', 'Fantascienza ').should('exist');
        cy.deleteBook(updatedTitle);

    });

    it('should create book, add to MyBooks read shelf, add to BookRanking, write a Review, delete book', () => {
        let title = 'Nuovo Titolo';
        let recensione = 'Testo unico per recensione di test';
        cy.loginAsAdmin();
        cy.createBook(title);
        //Add to MyBooks
        cy.get('[data-cy=myBooks]').select('read').wait(500);   
        //Add review
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
        cy.get('[data-cy=reviewList]').contains('[data-cy=testo]', recensione).should('exist');
        //Go to profile
        cy.get('[data-cy=profile]').click().wait(1000);
        cy.location('pathname').should('include', '/utente/');
        //Verify review is present
        cy.get('[data-cy=reviewList]').contains('[data-cy=testo]', recensione).should('exist');
        //Verify MyBooks is present
        cy.get('[data-cy=myBooksRead]').contains('[data-cy=bookTitle]', title).should('exist');
        //Add to BookRanking
        cy.get('[data-cy=newRankButton]').click().wait(500);
        cy.get('[data-cy=rankSelect]').select('10');
        cy.get('[data-cy=bookSelect]').select(title);
        cy.get('[data-cy=bookRankForm]').submit().wait(500);
        cy.get('[data-cy=bookInRank]').contains('[data-cy=title]', title).as('book').should('exist');
        cy.get('@book').parent().parent().parent().parent().siblings().contains('[data-cy=rank]', '2. ').should('exist');
        cy.deleteBook(title);
        cy.get('[data-cy=profile]').click().wait(1000);
        //Verify review not present
        cy.get('[data-cy=reviewList]').contains('[data-cy=testo]', recensione).should('not.exist');
        //Verify MyBooks not present
        cy.get('[data-cy=myBooksRead]').contains('[data-cy=bookTitle]', title).should('not.exist');
        //Verify BookRanking not present
        cy.get('[data-cy=bookInRank]').contains('[data-cy=title]', title).as('book').should('not.exist');
    });

    it('should create book, modify it\'s photo', () => {
        let fixturePath = 'bookPhoto.jpg';
        let title = 'Nuovo Titolo';
        cy.loginAsAdmin();
        cy.createBook(title);
        cy.get('[data-cy=modifyDropdown]').click().wait(100);
        cy.get('[data-cy=updatePhoto]').click().wait(500);
        cy.get('[data-cy=file]').attachFile(fixturePath);
        cy.get('[data-cy=uplodaFile]').click().wait(1000);
        cy.deleteBook(title);
    });

    it('should redirect to /home', () => {
        cy.visit('/new-book').wait(100);
        cy.location('pathname').should('be.equal', '/home');
    });
})