package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.entities.UsersInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersInfoRepository extends JpaRepository<UsersInfo, Long> {
}
