package com.sparta.publicclassdev.domain.codereview.repository;

import com.sparta.publicclassdev.domain.codereview.entity.CodeReviews;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeReviewsRepository extends JpaRepository<CodeReviews, Long> {

  @Query("SELECT cr, u.name FROM CodeReviews cr LEFT JOIN Users u ON cr.user.id = u.id WHERE cr.status = 'ACTIVE' ORDER BY cr.createdAt DESC")
  Page<Tuple> findAllWhereStatusIsActiveOrderByCreatedAtDesc(Pageable pageable);

  @Query("SELECT cr, u.name FROM CodeReviews cr LEFT JOIN Users u ON cr.user.id = u.id WHERE cr.status = 'ACTIVE' AND cr.category LIKE CONCAT('%', :category, '%') ORDER BY cr.createdAt DESC")
  Page<Tuple> findAllByCategory(String category, Pageable pageable);
}
