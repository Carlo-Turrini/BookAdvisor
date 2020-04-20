package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

@Repository
@CrossOrigin(origins = "http://localhost:4200")
public interface AuthorRepository extends JpaRepository<Author, Long> {
}
