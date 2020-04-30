package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.dto.MyBooksDTO;
import com.student.book_advisor.entities.MyBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyBooksRepository extends JpaRepository<MyBooks, Long> {

    @Query("DELETE FROM MyBooks mb WHERE mb.id = :id AND mb.usersInfo.id = :userID")
    public void deleteMyBookByUserIDAndId(@Param("id")Long id, @Param("userID") Long userID);

    @Query("SELECT new com.student.book_advisor.dto.MyBooksDTO(mb.id, b.id, b.titolo, mb.ShelfType, mb.usersInfo.id) FROM MyBooks mb JOIN mb.book b WHERE mb.usersInfo.id = :userID")
    public List<MyBooksDTO> getMyBooksByUserID(@Param("userID")Long userID);

    @Query("SELECT mb FROM MyBooks mb WHERE mb.id = :id AND mb.usersInfo.id = :userID")
    public MyBooks getByIdAndUserId(@Param("id")Long id, @Param("userID")Long userID);

    @Query("SELECT mb FROM MyBooks mb WHERE mb.usersInfo.id = :userID AND mb.book.id = :bookID")
    public MyBooks getByBookIDAndUserID(@Param("bookID")Long bookID, @Param("userID")Long userID);
}
