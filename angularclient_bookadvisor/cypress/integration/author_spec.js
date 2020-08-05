/// <reference types="Cypress" />

describe('Author Test Suite', () => {
    
    it('should create author, then delete it', () => {
        let fullname = 'Nuovo Autore';
        cy.loginAsAdmin();
        cy.get('[data-cy=authors]').click().wait(500);
        cy.location('pathname').should('be.equal', '/authors');
        cy.get('[data-cy=newAuthorButton]').click().wait(500);
        cy.get('[data-cy=authorsFullname]').type(fullname).wait(500);
        cy.get('[data-cy=birthYear]').type(1987);
        cy.get('[data-cy=biography]').type('biografia');
        cy.get('[data-cy=authorForm]').submit().wait(500);
        cy.contains('[data-cy=fullname]', fullname).as('autore').should('exist');
        cy.get('@autore').parent().siblings().find('[data-cy=eliminaAutore]').click().wait(500);
        cy.get('@autore').parentsUntil('[data-cy=autore]').siblings().find('[data-cy=conferma]').click().wait(500);
        cy.contains('[data-cy=fullname]', fullname).as('autore').should('not.exist');
    });

    it('should create author, visit profile, then delete it', () => {
        let fullname = 'Nuovo Autore';
        cy.loginAsAdmin();
        cy.createAuthor(fullname);
        cy.contains('[data-cy=fullname]', fullname).as('autore');
        cy.get('@autore').parentsUntil('[data-cy=authorCardBody]').siblings().find('[data-cy=scopriAutore]').click().wait(500);
        cy.location('pathname').should('include', '/authors/');
        cy.get('[data-cy=eliminaAutore]').click().wait(500);
        cy.get('[data-cy=deleteAuthorModal]').find('[data-cy=conferma]').click().wait(500);
        cy.location('pathname').should('be.equal', '/home');
        cy.get('[data-cy=authors]').click().wait(500);
        cy.location('pathname').should('be.equal', '/authors');
        cy.contains('[data-cy=fullname]', fullname).as('autore').should('not.exist');
    });

    it('should create author, modify it\'s name, then delete it', () => {
        let fullname = 'Nuovo Autore';
        let updatedFullname = 'Modified Name';
        cy.loginAsAdmin();
        cy.createAuthor(fullname);
        cy.contains('[data-cy=fullname]', fullname).as('autore');
        cy.get('@autore').parentsUntil('[data-cy=authorCardBody]').siblings().find('[data-cy=scopriAutore]').click().wait(500);
        cy.location('pathname').should('include', '/authors/');
        cy.contains('[data-cy=fullname]', fullname).should('exist');
        cy.get('[data-cy=updateDropdown]').click().wait(100);
        cy.get('[data-cy=updateAuthor]').click().wait(500);
        cy.get('[data-cy=fullname]').clear().type(updatedFullname);
        cy.get('[data-cy=updateForm]').submit().wait(500);
        cy.contains('[data-cy=fullname]', fullname).should('not.exist');
        cy.contains('[data-cy=fullname]', updatedFullname).should('exist');
        cy.deleteAuthorFromWithinHisProfilePage(updatedFullname);
    });

    it('should create author, modify it\'s photo', () => {
        let fixturePath = 'photo.jpg';
        let fullname = 'Nuovo Autore';
        cy.loginAsAdmin();
        cy.createAuthor(fullname);
        cy.contains('[data-cy=fullname]', fullname).as('autore');
        cy.get('@autore').parentsUntil('[data-cy=authorCardBody]').siblings().find('[data-cy=scopriAutore]').click().wait(500);
        cy.location('pathname').should('include', '/authors/');
        cy.get('[data-cy=updateDropdown]').click().wait(100);
        cy.get('[data-cy=updatePhoto]').click().wait(500);
        cy.get('[data-cy=file]').attachFile(fixturePath);
        cy.get('[data-cy=uplodaFile]').click().wait(1000);
        cy.deleteAuthorFromWithinHisProfilePage(fullname);
    });

    it('should attempt to delete author from allAuthors page, but fails', () => {
        let fullname = 'Pablo Neruda';
        const stub = cy.stub();
        cy.on('window:alert', stub);

        cy.loginAsAdmin();
        cy.get('[data-cy=authors]').click().wait(500);
        cy.location('pathname').should('be.equal', '/authors');
        cy.contains('[data-cy=fullname]', fullname).as('autore').should('exist');
        cy.get('@autore').parent().siblings().find('[data-cy=eliminaAutore]').click().wait(500);
        cy.get('@autore').parentsUntil('[data-cy=autore]').siblings().find('[data-cy=conferma]').click().wait(500).then(() => {
            expect(stub.getCall(0)).to.be.calledWith('403: Non cancellabile: l\'autore possiede dei libri!')
        });
        cy.contains('[data-cy=fullname]', fullname).as('autore').should('exist');
    });

    it('should attempt to delete author from his page, but fails', () => {
        let fullname = 'Pablo Neruda';
        const stub = cy.stub();
        cy.on('window:alert', stub);

        cy.loginAsAdmin();
        cy.get('[data-cy=authors]').click().wait(500);
        cy.location('pathname').should('be.equal', '/authors');
        cy.contains('[data-cy=fullname]', fullname).as('autore').should('exist');
        cy.get('@autore').parentsUntil('[data-cy=authorCardBody]').siblings().find('[data-cy=scopriAutore]').click().wait(500);
        cy.location('pathname').should('include', '/authors/');
        cy.get('[data-cy=eliminaAutore]').click().wait(500);
        cy.get('[data-cy=deleteAuthorModal]').find('[data-cy=conferma]').click().wait(500).then(() => {
            expect(stub.getCall(0)).to.be.calledWith('403: Non cancellabile: l\'autore possiede dei libri!')
        });
        cy.location('pathname').should('include', '/authors/');
    });
})