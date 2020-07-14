package com.student.book_advisor.data_persistency.repositories;

import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.data_persistency.model.entities.Saga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SagaRepository extends JpaRepository<Saga,Integer> {

    public Saga findByBook(Libro book);
}
