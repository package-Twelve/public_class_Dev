package com.sparta.publicclassdev.domain.likes.repository;

import com.sparta.publicclassdev.domain.likes.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

  Likes findByUserIdAndCodeReviewCommentId(Long id, Long codeReviewCommentsId);
}
