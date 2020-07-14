package com.student.book_advisor.repositories;

import com.student.book_advisor.data_persistency.model.entities.Author;
import com.student.book_advisor.data_persistency.model.entities.AuthorJoinBook;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.data_persistency.repositories.AuthorJoinBookRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
public class AuthorJoinBookRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AuthorJoinBookRepository authorJoinBookRepository;

    Libro book = null;
    Author author = null;
    AuthorJoinBook ajb = null;

    @Before
    public void setup() {
        AuthorJoinBook ajb = new AuthorJoinBook();
        Libro book = new Libro();
        book.setTitolo("Test");
        book.setAnnoPubblicazione(2003);
        book.setPagine(345);
        book.setSinossi("Test");
        book = testEntityManager.persist(book);
        Author author = new Author();
        author.setAuthorsFullname("Mario Rossi");
        author.setBirthYear(1987);
        author.setBiography("Test");
        author = testEntityManager.persist(author);
        ajb.setAuthor(author);
        ajb.setBook(book);
        testEntityManager.persist(ajb);
        testEntityManager.flush();
    }

    @Test
    public void testFindByAuthorAndBook() {
        AuthorJoinBook found = authorJoinBookRepository.findByAuthorAndBook(author, book);
        assertThat(found).isEqualTo(ajb);
    }

    @Test
    public void testNotFoundByAuthorAndBook() {
        Author author2 = new Author();
        author2.setAuthorsFullname("Brock");
        author2.setBirthYear(1987);
        author2.setBiography("Test");
        author2 = testEntityManager.persist(author2);
        Libro book2 = new Libro();
        book2.setTitolo("Test2");
        book2.setAnnoPubblicazione(2003);
        book2.setPagine(345);
        book2.setSinossi("Test");
        book2 = testEntityManager.persist(book2);
        testEntityManager.flush();
        AuthorJoinBook notFound = authorJoinBookRepository.findByAuthorAndBook(author2, book);
        assertThat(notFound).isEqualTo(null);
        notFound = authorJoinBookRepository.findByAuthorAndBook(author, book2);
        assertThat(notFound).isEqualTo(null);
        notFound = authorJoinBookRepository.findByAuthorAndBook(author2, book2);
        assertThat(notFound).isEqualTo(null);
    }
}
