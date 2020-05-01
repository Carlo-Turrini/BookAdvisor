package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.entities.Libro;
import com.student.book_advisor.entities.Saga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SagaRepository extends JpaRepository<Saga,Long> {

    public Saga findByBook(Libro book);
}
