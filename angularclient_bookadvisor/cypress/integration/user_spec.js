/// <reference types="Cypress" />

describe('User Test Suite', () => {
    it('should register new user, login as admin, navigate to all users and delete newly created user', () => {
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
        cy.get('[data-cy=username]').type('MarioRossi');
        cy.get('[data-cy=password').type('password');
        cy.get('[data-cy=loginForm]').submit().wait(500);
        cy.location('pathname').should('be.equal', '/home');
        cy.getCookie('access_token').should('exist');
        cy.get('[data-cy=users]').click().wait(500);
        cy.location('pathname').should('be.equal', '/utenti');
        cy.get('[data-cy=user]').contains('[data-cy=username]', '@TestUser').as('user').should('exist');
        cy.get('@user').parent().siblings().find('[data-cy=elimina]').click().wait(500);
        cy.get('@user').parent().parent().parent().parent().siblings().find('[data-cy=conferma]').click().wait(500);
        cy.get('@user').should('not.exist');
    });

    it('should create book, add to MyBooks read, add to BookRanking, modify MyBooks', () => {
        let title = 'Nuovo Titolo';
        cy.loginAsAdmin();
        cy.createBook(title);
        //Add to MyBooks
        cy.get('[data-cy=myBooks]').select('read').wait(500);   
        //Go to profile
        cy.get('[data-cy=profile]').click().wait(1000);
        cy.location('pathname').should('include', '/utente/');
        //Verify MyBooks is present
        cy.get('[data-cy=myBooksRead]').contains('[data-cy=bookTitle]', title).should('exist');
        //Add to BookRanking
        cy.get('[data-cy=newRankButton]').click().wait(500);
        cy.get('[data-cy=rankSelect]').select('10');
        cy.get('[data-cy=bookSelect]').select(title);
        cy.get('[data-cy=bookRankForm]').submit().wait(500);
        cy.get('[data-cy=bookInRank]').contains('[data-cy=title]', title).as('book').should('exist');
        cy.get('@book').parent().parent().parent().parent().siblings().contains('[data-cy=rank]', '2. ').should('exist');
        //Update MyBooks
        cy.get('[data-cy=myBooksRead]').contains('[data-cy=bookTitle]', title).parent().parent().siblings().find('[data-cy=updateMyBooksSelect]').select('toRead').wait(500);
        cy.get('[data-cy=myBooksRead]').contains('[data-cy=bookTitle]', title).should('not.exist');
        cy.get('[data-cy=myBooksToRead]').contains('[data-cy=bookTitle]', title).should('exist');
        //Verify BookRanking not present
        cy.get('[data-cy=bookInRank]').contains('[data-cy=title]', title).as('book').should('not.exist');
        cy.deleteBook(title);
        cy.get('[data-cy=profile]').click().wait(1000);
        //Verify MyBooks not present
        cy.contains('[data-cy=bookTitle]', title).should('not.exist');
    });

    it('should create book, add to MyBooks, add to BookRanking, delete from BookRanking', () => {
        let title = 'Nuovo Titolo';
        cy.loginAsAdmin();
        cy.createBook(title);
        //Add to MyBooks
        cy.get('[data-cy=myBooks]').select('read').wait(500);   
        //Go to profile
        cy.get('[data-cy=profile]').click().wait(1000);
        cy.location('pathname').should('include', '/utente/');
        //Verify MyBooks is present
        cy.get('[data-cy=myBooksRead]').contains('[data-cy=bookTitle]', title).should('exist');
        //Add to BookRanking
        cy.get('[data-cy=newRankButton]').click().wait(500);
        cy.get('[data-cy=rankSelect]').select('10');
        cy.get('[data-cy=bookSelect]').select(title);
        cy.get('[data-cy=bookRankForm]').submit().wait(500);
        cy.get('[data-cy=bookInRank]').contains('[data-cy=title]', title).as('book').should('exist');
        cy.get('@book').parent().parent().parent().parent().siblings().contains('[data-cy=rank]', '2. ').should('exist');
        cy.get('@book').parent().siblings().find('[data-cy=eliminaRank]').click().wait(500);
        cy.get('@book').parent().parent().parent().parent().parent().parent().siblings().find('[data-cy=conferma]').click().wait(500);
        //Verify BookRanking not present
        cy.get('[data-cy=bookInRank]').contains('[data-cy=title]', title).as('book').should('not.exist');
        cy.deleteBook(title);
        cy.get('[data-cy=profile]').click().wait(1000);
        //Verify MyBooks not present
        cy.contains('[data-cy=bookTitle]', title).should('not.exist');
    });

    it('should create book, add to MyBooks, delete from MyBooks', () => {
        let title = 'Nuovo Titolo';
        cy.loginAsAdmin();
        cy.createBook(title);
        //Add to MyBooks
        cy.get('[data-cy=myBooks]').select('read').wait(500);   
        //Go to profile
        cy.get('[data-cy=profile]').click().wait(1000);
        cy.location('pathname').should('include', '/utente/');
        //Verify MyBooks is present
        cy.get('[data-cy=myBooksRead]').contains('[data-cy=bookTitle]', title).as('myBook').should('exist');
        cy.get('@myBook').parent().siblings().find('[data-cy=eliminaMyBookRead]').click().wait(500);
        cy.get('@myBook').parent().parent().parent().parent().siblings().find('[data-cy=conferma]').click().wait(500);
        cy.contains('[data-cy=bookTitle]', title).should('not.exist');
        cy.deleteBook(title);
    });

    it('should create book, add to MyBooks, update MyBooks from book page', () => {
        let title = 'Nuovo Titolo';
        cy.loginAsAdmin();
        cy.createBook(title);
        //Add to MyBooks
        cy.get('[data-cy=myBooks]').select('read').wait(500);   
        //Go to profile
        cy.get('[data-cy=profile]').click().wait(1000);
        cy.location('pathname').should('include', '/utente/');
        //Verify MyBooks is present
        cy.get('[data-cy=myBooksRead]').contains('[data-cy=bookTitle]', title).as('myBook').should('exist');
        //Return to book page
        cy.get('[data-cy=libri]').click().wait(500);
        cy.location('pathname').should('be.equal', '/libri');
        cy.get('[data-cy=libro]').contains('[data-cy=titolo]', title).as('book')
        cy.get('@book').parent().parent().siblings().contains('[data-cy=scopri]', 'Scopri').click().wait(500);
        cy.location('pathname').should('include', '/libro/');
        //Update MyBooks
        cy.get('[data-cy=myBooks]').select('toRead').wait(500);   
        //Verify update
        cy.get('[data-cy=profile]').click().wait(1000);
        cy.location('pathname').should('include', '/utente/');
        cy.get('[data-cy=myBooksToRead]').contains('[data-cy=bookTitle]', title).as('myBook').should('exist');
        cy.deleteBook(title);
        cy.get('[data-cy=profile]').click().wait(1000);
        //Verify MyBooks not present
        cy.contains('[data-cy=bookTitle]', title).should('not.exist');
    });

    it('should create book, register user, login as user, write review, delete user', () => {
        let title = 'Nuovo Titolo';
        let username = 'TestUser';
        let email = 'test@gmail.com';
        let recensione = 'Testo unico per recensione di test';
        cy.loginAsAdmin();
        cy.createBook(title);
        cy.get('[data-cy=logout]').click().wait(500);
        cy.createUser(username, email);
        cy.get('[data-cy=username]').type(username);
        cy.get('[data-cy=password]').type('password');
        cy.get('[data-cy=loginForm]').submit().wait(500);
        cy.getCookie('access_token').should('exist');
        cy.get('[data-cy=libri]').click().wait(500);
        cy.get('[data-cy=libro]').contains('[data-cy=titolo]', title).as('book');
        cy.get('@book').parent().parent().siblings().contains('[data-cy=scopri]', 'Scopri').click().wait(500);
        cy.location('pathname').should('include', '/libro/');
        //Write review
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
        //Delete user
        cy.deleteLoggedInUser();
        //Assert review not present
        cy.get('[data-cy=libri]').click().wait(500);
        cy.get('@book').parent().parent().siblings().contains('[data-cy=scopri]', 'Scopri').click().wait(500);
        cy.location('pathname').should('include', '/libro/');
        cy.get('[data-cy=reviewList]').contains('[data-cy=testo]', recensione).should('not.exist');
        cy.loginAsAdmin();
        cy.deleteBook(title);
    });

    it('should create user, login, modify users email', () => {
        let username = 'TestUser';
        let email = 'test@gmail.com';
        let updatedEmail = 'modified@gmail.com';
        cy.createUser(username, email);
        cy.get('[data-cy=username]').type(username);
        cy.get('[data-cy=password]').type('password');
        cy.get('[data-cy=loginForm]').submit().wait(500);
        cy.getCookie('access_token').should('exist');
        cy.get('[data-cy=profile]').click().wait(500);
        cy.contains('[data-cy=email]', email).should('exist');
        cy.get('[data-cy=updateDropdown]').click().wait(100);
        cy.get('[data-cy=updateUser]').click().wait(500);
        cy.get('[data-cy=email]').clear().type(updatedEmail);
        cy.get('[data-cy=updateForm]').submit().wait(500);
        cy.contains('[data-cy=email]', email).should('not.exist');
        cy.contains('[data-cy=email]', updatedEmail).should('exist');
        cy.deleteLoggedInUser();
    });

    it('should create user, modify it\'s photo', () => {
        let fixturePath = 'photo.jpg';
        let username = 'TestUser';
        let email = 'test@gmail.com';
        cy.createUser(username, email);
        cy.get('[data-cy=username]').type(username);
        cy.get('[data-cy=password]').type('password');
        cy.get('[data-cy=loginForm]').submit().wait(500);
        cy.getCookie('access_token').should('exist');
        cy.get('[data-cy=profile]').click().wait(500);
        cy.location('pathname').should('include', '/utente/');
        cy.get('[data-cy=updateDropdown]').click().wait(100);
        cy.get('[data-cy=updatePhoto]').click().wait(500);
        cy.get('[data-cy=file]').attachFile(fixturePath);
        cy.get('[data-cy=uplodaFile]').click().wait(1000);
        cy.deleteLoggedInUser();
    });
})