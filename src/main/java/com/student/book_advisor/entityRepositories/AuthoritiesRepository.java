package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.entities.Authorities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Repository
@CrossOrigin(origins = "http://localhost:4200")
public interface AuthoritiesRepository extends JpaRepository<Authorities, Integer> {
    @Query("SELECT a FROM Authorities a WHERE a.usersInfo.id = :userID")
    public List<Authorities> findAllByUserID(@Param("userID")Integer userID);
}
