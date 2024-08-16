package com.sparta.publicclassdev.domain.CommunityComment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sparta.publicclassdev.domain.communities.dto.CommunitiesRequestDto;
import com.sparta.publicclassdev.domain.communities.dto.CommunitiesResponseDto;
import com.sparta.publicclassdev.domain.communities.entity.Communities;
import com.sparta.publicclassdev.domain.communities.entity.Communities.Category;
import com.sparta.publicclassdev.domain.communities.repository.CommunitiesRepository;
import com.sparta.publicclassdev.domain.communities.service.CommunitiesService;
import com.sparta.publicclassdev.domain.communitycomments.dto.CommunityCommentResponseDto;
import com.sparta.publicclassdev.domain.communitycomments.dto.CommunityCommentsRequestDto;
import com.sparta.publicclassdev.domain.communitycomments.entity.CommunityComments;
import com.sparta.publicclassdev.domain.communitycomments.repository.CommunityCommentsRepository;
import com.sparta.publicclassdev.domain.communitycomments.service.CommunityCommentsService;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommunityCommentsServiceTest {
    @Autowired
    private CommunityCommentsService commentsService;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private CommunitiesRepository communitiesRepository;

    @Autowired
    private CommunityCommentsRepository repository;

    @Autowired
    private UsersRepository usersRepository;


    private Users createTestUser(){
        Users user = Users.builder()
            .name("admin user")
            .email("admin@email.com")
            .password("Admin1234!!")
            .role(RoleEnum.ADMIN)
            .build();

        ReflectionTestUtils.setField(user, "id", 3L);

        return usersRepository.save(user);
    }

    private Communities createTestCommunity(Users user) {
        CommunitiesRequestDto requestDto = CommunitiesRequestDto.builder()
            .title("Test title")
            .content("Test content")
            .category(Category.INFO)
            .build();

        CommunitiesResponseDto responseDto = communitiesService.createPost(requestDto, user);
        return communitiesRepository.findById(responseDto.getId()).orElse(null);
    }

    private CommunityComments createTestCommunityComment(Communities community, Users user){
        CommunityCommentsRequestDto requestDto = CommunityCommentsRequestDto.builder()
            .contents("comment")
            .build();

        CommunityCommentResponseDto responseDto = commentsService.createComment(community.getId(), requestDto, user);
        return repository.findById(responseDto.getCommentId()).orElse(null);
    }

    private CommunityComments createTestCommunityComment2(Communities community, Users user){
        CommunityCommentsRequestDto requestDto = CommunityCommentsRequestDto.builder()
            .contents("comment2")
            .build();

        CommunityCommentResponseDto responseDto = commentsService.createComment(community.getId(), requestDto, user);
        return repository.findById(responseDto.getCommentId()).orElse(null);
    }

    private CommunityCommentsRequestDto createRequestDto(){
        CommunityCommentsRequestDto requestDto = CommunityCommentsRequestDto.builder()
            .contents("comment")
            .build();
        return requestDto;
    }

    @Test
    @Transactional
    void createComment() {
        Users user = createTestUser();
        Communities community = createTestCommunity(user);
        CommunityCommentsRequestDto requestDto = createRequestDto();

        CommunityCommentResponseDto comments = commentsService.createComment(community.getId(), requestDto, user);

        assertEquals(comments.getContent(), "comment");
    }

    @Test
    @Transactional
    void updateComment() {
        Users user = createTestUser();
        Communities communities = createTestCommunity(user);
        CommunityComments comments = createTestCommunityComment(communities, user);

        CommunityCommentsRequestDto updateRequestDto = CommunityCommentsRequestDto.builder()
            .contents("Update Comment")
            .build();

        CommunityCommentResponseDto responseDto = commentsService.updateComment(communities.getId(), comments.getId(), updateRequestDto, user);

        assertEquals(updateRequestDto.getContents(), "Update Comment");
    }

    @Test
    @Transactional
    void findComments() {
        Users user = createTestUser();
        Communities communities = createTestCommunity(user);
        CommunityComments comments = createTestCommunityComment(communities, user);
        CommunityComments comments1 = createTestCommunityComment2(communities, user);

        List<CommunityCommentResponseDto> responseDto = commentsService.findComments(communities.getId());

        assertThat(responseDto).hasSize(2);
        assertEquals(responseDto.get(0).getContent(), "comment");
        assertEquals(responseDto.get(1).getContent(), "comment2");

    }

    @Test
    @Transactional
    void deleteComment() {
        Users user = createTestUser();
        Communities communities = createTestCommunity(user);
        CommunityComments comments = createTestCommunityComment(communities, user);

        commentsService.deleteComment(comments.getId(), communities.getId(), user);

        assertTrue(repository.findById(comments.getId()).isEmpty());
    }
}