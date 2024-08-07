package com.sparta.publicclassdev.domain.codereview;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sparta.publicclassdev.domain.codereview.entity.CodeReviews;
import com.sparta.publicclassdev.domain.codereview.entity.CodeReviews.Status;
import com.sparta.publicclassdev.domain.codereview.repository.CodeReviewsRepository;
import com.sparta.publicclassdev.domain.codereviewcomment.repository.CodeReviewCommentsRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@ActiveProfiles("test")
public class CodeReviewsRepositoryTest {

  @Autowired
  private CodeReviewsRepository codeReviewsRepository;

  @Autowired
  private UsersRepository usersRepository;

  @BeforeEach
  void setUp() {
    Users user1 = Users.builder()
        .name("testuser1")
        .email("test@email.com")
        .password("Test123!")
        .role(RoleEnum.USER)
        .build();
    user1 = usersRepository.save(user1);

    Users user2 = Users.builder()
        .name("testuser2")
        .email("test2@email.com")
        .password("Test123!")
        .role(RoleEnum.USER)
        .build();
    user2 = usersRepository.save(user2);

    CodeReviews codeReview1 = CodeReviews.builder()
        .title("Title1")
        .category("#category ")
        .contents("Contents")
        .status(Status.ACTIVE)
        .user(user1)
        .build();

    ReflectionTestUtils.setField(codeReview1, "createdAt", LocalDateTime.now());

    codeReviewsRepository.save(codeReview1);

    CodeReviews codeReview2 = CodeReviews.builder()
        .title("Title2")
        .category("#category2 ")
        .contents("Contents2")
        .status(Status.ACTIVE)
        .user(user2)
        .build();

    ReflectionTestUtils.setField(codeReview2, "createdAt", LocalDateTime.now().plusSeconds(1));

    codeReviewsRepository.save(codeReview2);
  }

  @Test
  @Transactional
  void testFindAllWhereStatusIsActiveOrderByCreatedAtDesc() {
    // given
    Pageable pageable = PageRequest.of(0, 10);

    // when
    Page<Tuple> result = codeReviewsRepository.findAllWhereStatusIsActiveOrderByCreatedAtDesc(
        pageable);

    // then
    assertEquals(2, result.getContent().size());

    Tuple firstTuple = result.getContent().get(0);
    Tuple secondTuple = result.getContent().get(1);

    CodeReviews firstCodeReview = (CodeReviews) firstTuple.get(0);
    CodeReviews secondCodeReview = (CodeReviews) secondTuple.get(0);
    String firstUserName = (String) firstTuple.get(1);
    String secondUserName = (String) secondTuple.get(1);

    assertEquals("Title2", firstCodeReview.getTitle());
    assertEquals("Title1", secondCodeReview.getTitle());
    assertEquals("testuser2", firstUserName);
    assertEquals("testuser1", secondUserName);
  }

  @Test
  @Transactional
  void testFindAllByCategory() {
    // given
    String searchCategory = "#category2 ";
    Pageable pageable = PageRequest.of(0, 10);

    // when
    Page<Tuple> result = codeReviewsRepository.findAllByCategory(searchCategory, pageable);

    // then
    assertEquals(1, result.getTotalElements()); // category1에 해당하는 CodeReviews가 1개 있어야 함

    Tuple firstTuple = result.getContent().get(0);

    CodeReviews codeReview = (CodeReviews) firstTuple.get(0);
    String username = (String) firstTuple.get(1);

    assertEquals("Title2", codeReview.getTitle());
    assertEquals("testuser2", username);
  }
}
