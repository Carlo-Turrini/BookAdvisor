package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.dto.AuthorCardDTO;
import com.student.book_advisor.dto.AuthorDTO;
import com.student.book_advisor.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Repository
@CrossOrigin(origins = "http://localhost:4200")
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("SELECT new com.student.book_advisor.dto.AuthorCardDTO(a.id, a.authorsFullname, a.birthYear, a.deathYear) FROM Author a")
    public List<AuthorCardDTO> findAllToDTO();

    @Query("SELECT new com.student.book_advisor.dto.AuthorCardDTO(a.id, a.authorsFullname, a.birthYear, a.deathYear) FROM Author a WHERE a.authorsFullname LIKE CONCAT('%', :fullname, '%')")
    public List<AuthorCardDTO> findAllWithFullnameLikeX(@Param("fullname")String fullname);

    @Query("SELECT new com.student.book_advisor.dto.AuthorDTO(a.id, a.authorsFullname, a.biography, a.birthYear, a.deathYear) FROM Author a WHERE  a.authorsFullname = :fullname")
    public AuthorDTO getAuthorsDTOByFullname(@Param("fullname")String fullname);

    @Query("SELECT new com.student.book_advisor.dto.AuthorDTO(a.id, a.authorsFullname, a.biography, a.birthYear, a.deathYear) FROM Author a WHERE a.id = :id")
    public AuthorDTO getAuthorsDTOById(@Param("id")Long id);

    @Query("SELECT new com.student.book_advisor.dto.auxiliaryDTOs.AuthorOfBook(a.id, a.authorsFullname) FROM Author a JOIN a.authorJoinBookList authBook WHERE authBook.book.id = :bookID")
    public List<AuthorOfBook> findAuthorsOfBook(@Param("bookID")Long bookID);

    @Query("SELECT COUNT(a) FROM Author a WHERE a.authorsFullname = :authorsFullname")
    public Integer countAllAuthorsWithName(@Param("fullname")String authorsFullname);

    @Query("SELECT a.authorsPhotoPath FROM Author a WHERE a.id = :id")
    public String getAuthorsPhotoPath(@Param("id")Long id);

    @Query("SELECT new com.student.book_advisor.dto.auxiliaryDTOs.AuthorOfBook(a.id, a.authorsFullname) FROM Author a")
    public List<AuthorOfBook> findAllAuthorsOfBook();

    @Query("SELECT new com.student.book_advisor.dto.auxiliaryDTOs.AuthorOfBook(a.id, a.authorsFullname) FROM Author a WHERE a.authorsFullname = :fullname")
    public AuthorOfBook getAuthorOfBookByFullname(@Param("fullname")String authorsFullname);


}
