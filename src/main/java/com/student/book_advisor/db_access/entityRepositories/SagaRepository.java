package com.student.book_advisor.db_access.entityRepositories;

import com.student.book_advisor.db_access.entities.Libro;
import com.student.book_advisor.db_access.entities.Saga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SagaRepository extends JpaRepository<Saga,Integer> {

    public Saga findByBook(Libro book);
}
