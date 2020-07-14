package com.student.book_advisor.data_persistency.repositories;

import com.student.book_advisor.data_persistency.model.dto.MyBooksDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.MyBooksReadDTO;
import com.student.book_advisor.data_persistency.model.entities.MyBooks;
import com.student.book_advisor.enums.BookShelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyBooksRepository extends JpaRepository<MyBooks, Integer> {

    @Query("DELETE FROM MyBooks mb WHERE mb.id = :id AND mb.usersInfo.id = :userID")
    public void deleteMyBookByUserIDAndId(@Param("id")Integer id, @Param("userID") Integer userID);

    @Query("SELECT new com.student.book_advisor.data_persistency.model.dto.MyBooksDTO(mb.id, l.id, l.titolo, mb.ShelfType, mb.usersInfo.id, AVG(r.rating)) FROM MyBooks mb JOIN Libro l ON (mb.book.id = l.id) LEFT JOIN Recensione r ON (r.libro.id = l.id) WHERE mb.usersInfo.id = :userID GROUP BY mb.id, l.id, l.titolo, mb.ShelfType, mb.usersInfo.id")
    public List<MyBooksDTO> getMyBooksByUserID(@Param("userID")Integer userID);

    @Query("SELECT mb FROM MyBooks mb WHERE mb.id = :id AND mb.usersInfo.id = :userID")
    public MyBooks getByIdAndUserId(@Param("id")Integer id, @Param("userID")Integer userID);

    //Toglibile
    @Query("SELECT mb FROM MyBooks mb WHERE mb.usersInfo.id = :userID AND mb.book.id = :bookID")
    public MyBooks getByBookIDAndUserID(@Param("bookID")Integer bookID, @Param("userID")Integer userID);

    @Query("SELECT mb.ShelfType FROM MyBooks mb WHERE mb.usersInfo.id = :userID AND mb.book.id = :bookID")
    public BookShelf getBookShelfByBookIDAndUserID(@Param("bookID")Integer bookID, @Param("userID")Integer userID);

    @Query("SELECT new com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.MyBooksReadDTO(mb.id, b.titolo) FROM MyBooks mb JOIN Libro b ON (mb.book.id = b.id) WHERE mb.ShelfType = 'read' AND mb.id NOT IN (SELECT br.myBooks.id FROM BookRanking br) ")
    public List<MyBooksReadDTO> getAllMyBooksReadButNotInRank(@Param("userID")Integer userID);

}
