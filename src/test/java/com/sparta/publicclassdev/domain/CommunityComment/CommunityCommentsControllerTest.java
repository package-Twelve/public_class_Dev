package com.sparta.publicclassdev.domain.CommunityComment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.publicclassdev.domain.communities.dto.CommunitiesRequestDto;
import com.sparta.publicclassdev.domain.communities.dto.CommunitiesResponseDto;
import com.sparta.publicclassdev.domain.communities.entity.Communities.Category;
import com.sparta.publicclassdev.domain.communities.service.CommunitiesService;
import com.sparta.publicclassdev.domain.communitycomments.dto.CommunityCommentResponseDto;
import com.sparta.publicclassdev.domain.communitycomments.dto.CommunityCommentsRequestDto;
import com.sparta.publicclassdev.domain.communitycomments.service.CommunityCommentsService;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class CommunityCommentsControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private CommunityCommentsService service;

    @Autowired
    private UsersRepository userRepository;

    private Long communityId;

    private Long commentId;

    @BeforeEach
    @Transactional
    public void setUp() throws Exception {
        Users user = Users.builder()
            .name("leesebi")
            .email("leesebi@email.com")
            .password("Test1234!")
            .role(RoleEnum.USER)
            .build();
        user = userRepository.save(user);

        CommunitiesRequestDto createRequestDto = CommunitiesRequestDto.builder()
            .title("Title")
            .content("Content")
            .category(Category.INFO)
            .build();

        CommunitiesResponseDto responseDto = communitiesService.createPost(createRequestDto, user);
        communityId = responseDto.getId();

        CommunityCommentsRequestDto requestDto = CommunityCommentsRequestDto.builder()
            .contents("test Comment")
            .build();
        CommunityCommentResponseDto commentResponseDto = service.createComment(communityId, requestDto, user);
        commentId = commentResponseDto.getCommentId();

    }


    @Test
    @Transactional
    @WithUserDetails(value = "leesebi@email.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createComment() throws Exception {
        CommunityCommentsRequestDto requestDto = CommunityCommentsRequestDto.builder()
            .contents("test Comment")
            .build();


        mvc.perform(post("/api/community/{community}/comments", communityId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value("댓글 생성 완료"));
    }

    @Test
    @Transactional
    @WithUserDetails(value="leesebi@email.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateComment() throws Exception {
        CommunityCommentsRequestDto requestDto = CommunityCommentsRequestDto.builder()
            .contents("Update Comment")
            .build();

        mvc.perform(put("/api/community/{communityId}/comments/{commentId}", communityId, commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("댓글 수정 완료"));

    }

    @Test
    @Transactional
    @WithUserDetails(value="leesebi@email.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void findComments() throws Exception {

        mvc.perform(get("/api/community/{communityId}/comments", communityId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("댓글 조회 완료"));
    }

    @Test
    @Transactional
    @WithUserDetails(value="leesebi@email.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteComment() throws Exception{

        mvc.perform(delete("/api/community/{communityId}/comments/{commentId}", communityId, commentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("댓글 삭제 완료"));
    }
}
