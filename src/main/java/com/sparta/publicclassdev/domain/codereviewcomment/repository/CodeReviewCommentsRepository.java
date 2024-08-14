package com.sparta.publicclassdev.domain.codereviewcomment.repository;

import com.sparta.publicclassdev.domain.codereviewcomment.entity.CodeReviewComments;
import jakarta.persistence.Tuple;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CodeReviewCommentsRepository extends JpaRepository<CodeReviewComments, Long> {

  @Query("SELECT crc, u.name, COUNT(l) AS likes FROM CodeReviewComments crc LEFT JOIN Users u ON crc.user.id = u.id LEFT JOIN Likes l ON crc.id = l.codeReviewComment.id AND l.status = 'LIKED' WHERE crc.codeReviews.id = :codeReviewsId AND crc.status = 'ACTIVE' GROUP BY crc.id, u.name ORDER BY crc.createdAt DESC")
  List<Tuple> findByCodeReviewIdWithDetails(Long codeReviewsId);
}
