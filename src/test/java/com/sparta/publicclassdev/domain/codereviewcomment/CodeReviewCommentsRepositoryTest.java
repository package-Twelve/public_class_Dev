package com.sparta.publicclassdev.domain.codereviewcomment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sparta.publicclassdev.domain.codereview.entity.CodeReviews;
import com.sparta.publicclassdev.domain.codereview.repository.CodeReviewsRepository;
import com.sparta.publicclassdev.domain.codereviewcomment.entity.CodeReviewComments;
import com.sparta.publicclassdev.domain.codereviewcomment.repository.CodeReviewCommentsRepository;
import com.sparta.publicclassdev.domain.likes.entity.Likes;
import com.sparta.publicclassdev.domain.likes.entity.Likes.Status;
import com.sparta.publicclassdev.domain.likes.repository.LikesRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@ActiveProfiles("test")
public class CodeReviewCommentsRepositoryTest {

  @Autowired
  private CodeReviewsRepository codeReviewsRepository;

  @Autowired
  private CodeReviewCommentsRepository codeReviewCommentsRepository;

  @Autowired
  private UsersRepository usersRepository;

  private Users user;
  private CodeReviews codeReview;
  @Autowired
  private LikesRepository likesRepository;

  @BeforeEach
  void setUp() {
    user = Users.builder()
        .name("testuser")
        .email("test@example.com")
        .password("Test123!")
        .role(RoleEnum.USER)
        .build();

    Users user2 = Users.builder()
        .name("testuser2")
        .email("test2@example.com")
        .password("Test123!")
        .role(RoleEnum.USER)
        .build();

    usersRepository.saveAll(List.of(user, user2));

    codeReview = CodeReviews.builder()
        .title("Test Review")
        .category("#test")
        .contents("Test contents")
        .status(CodeReviews.Status.ACTIVE)
        .user(user)
        .build();

    codeReviewsRepository.save(codeReview);

    CodeReviewComments comment1 = CodeReviewComments.builder()
        .contents("Comment1")
        .status(CodeReviewComments.Status.ACTIVE)
        .user(user)
        .codeReviews(codeReview)
        .build();

    ReflectionTestUtils.setField(comment1, "createdAt", LocalDateTime.now());

    CodeReviewComments comment2 = CodeReviewComments.builder()
        .contents("Comment2")
        .status(CodeReviewComments.Status.ACTIVE)
        .user(user)
        .codeReviews(codeReview)
        .build();

    ReflectionTestUtils.setField(comment2, "createdAt", LocalDateTime.now().plusSeconds(1));

    codeReviewCommentsRepository.saveAll(List.of(comment1, comment2));

    Likes like1 = Likes.builder()
        .status(Status.LIKED)
        .user(user)
        .codeReviewComment(comment1)
        .build();

    Likes like2 = Likes.builder()
        .status(Status.LIKED)
        .user(user2)
        .codeReviewComment(comment1)
        .build();

    likesRepository.saveAll(List.of(like1, like2));
  }

  @Test
  @Transactional
  void testFindByCodeReviewIdWithDetails() {
    // given
    Long codeReviewId = codeReview.getId();

    // when
    List<Tuple> result = codeReviewCommentsRepository.findByCodeReviewIdWithDetails(codeReviewId);

    // then
    assertEquals(2, result.size());

    Tuple firstTuple = result.get(0);
    Tuple secondTuple = result.get(1);

    CodeReviewComments firstComment = (CodeReviewComments) firstTuple.get(0);
    CodeReviewComments secondComment = (CodeReviewComments) secondTuple.get(0);

    Long firstLikesCount = (Long) firstTuple.get(2);
    Long secondLikesCount = (Long) secondTuple.get(2);

    assertEquals(codeReviewId, firstComment.getCodeReviews().getId());
    assertEquals(codeReviewId, secondComment.getCodeReviews().getId());

    assertEquals(2L, firstComment.getId());
    assertEquals(1L, secondComment.getId());

    assertEquals(0, firstLikesCount);
    assertEquals(2, secondLikesCount);

  }
}
