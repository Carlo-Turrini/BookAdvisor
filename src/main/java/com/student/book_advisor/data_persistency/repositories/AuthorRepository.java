package com.student.book_advisor.data_persistency.repositories;

import com.student.book_advisor.data_persistency.model.dto.AuthorCardDTO;
import com.student.book_advisor.data_persistency.model.dto.AuthorDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.data_persistency.model.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Repository
@CrossOrigin(origins = "http://localhost:4200")
public interface AuthorRepository extends JpaRepository<Author, Integer> {

    @Query("SELECT new com.student.book_advisor.data_persistency.model.dto.AuthorCardDTO(a.id, a.authorsFullname, a.birthYear, a.deathYear) FROM Author a")
    public List<AuthorCardDTO> findAllToDTO();

    @Query("SELECT new com.student.book_advisor.data_persistency.model.dto.AuthorDTO(a.id, a.authorsFullname, a.biography, a.birthYear, a.deathYear) FROM Author a WHERE a.id = :id")
    public AuthorDTO getAuthorsDTOById(@Param("id")Integer id);

    @Query("SELECT new com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook(a.id, a.authorsFullname) FROM Author a JOIN a.authorJoinBookList authBook WHERE authBook.book.id = :bookID")
    public List<AuthorOfBook> findAuthorsOfBook(@Param("bookID")Integer bookID);

    @Query("SELECT COUNT(a) FROM Author a WHERE a.authorsFullname = :fullname")
    public Integer countAllAuthorsWithName(@Param("fullname")String authorsFullname);

    @Query("SELECT a.authorsPhotoPath FROM Author a WHERE a.id = :id")
    public String getAuthorsPhotoPath(@Param("id")Integer id);

    @Query("SELECT new com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook(a.id, a.authorsFullname) FROM Author a")
    public List<AuthorOfBook> findAllAuthorsOfBook();

    @Query("SELECT new com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook(a.id, a.authorsFullname) FROM Author a WHERE a.authorsFullname = :fullname")
    public AuthorOfBook getAuthorOfBookByFullname(@Param("fullname")String authorsFullname);

    @Query("SELECT COUNT(ajb) FROM Author a JOIN AuthorJoinBook ajb ON ajb.author.id = :id WHERE a.id = :id")
    public Integer countAllBooksByAuthor(@Param("id") Integer id);


}
