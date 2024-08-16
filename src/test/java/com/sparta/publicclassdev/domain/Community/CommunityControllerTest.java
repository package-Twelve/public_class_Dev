package com.sparta.publicclassdev.domain.Community;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.publicclassdev.domain.communities.dto.CommunitiesRequestDto;
import com.sparta.publicclassdev.domain.communities.dto.CommunitiesResponseDto;
import com.sparta.publicclassdev.domain.communities.dto.CommunitiesUpdateRequestDto;
import com.sparta.publicclassdev.domain.communities.entity.Communities.Category;
import com.sparta.publicclassdev.domain.communities.service.CommunitiesService;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class CommunityControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CommunitiesService service;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UsersRepository usersRepository;

    private Long communityId;

    @BeforeEach
    @Transactional
    public void setUp() throws Exception {

        Users user = Users.builder()
            .name("leesebi")
            .email("leesebi@email.com")
            .password("Test1234!")
            .role(RoleEnum.USER)
            .build();
        user = usersRepository.save(user);

        CommunitiesRequestDto createRequestDto = CommunitiesRequestDto.builder()
            .title("Title")
            .content("Content")
            .category(Category.INFO)
            .build();

        CommunitiesResponseDto responseDto = service.createPost(createRequestDto, user);
        communityId = responseDto.getId();
    }


    @Test
    @WithUserDetails(value = "leesebi@email.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Order(1)
    @Transactional
    void createPost() throws Exception {
        CommunitiesRequestDto requestDto = CommunitiesRequestDto.builder()
            .title("title")
            .content("test Content")
            .category(Category.INFO)
            .build();

        mvc.perform(post("/api/community")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value("커뮤니티 게시글 생성"));
    }

    @Test
    @WithUserDetails(value = "leesebi@email.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Order(2)
    @Transactional
    void findPost() throws Exception{

        mvc.perform(get("/api/community/{communityId}", communityId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("단건 조회 완료"));
    }

    @Test
    @WithUserDetails(value = "leesebi@email.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Order(3)
    @Transactional
    void findPosts() throws Exception{
        mvc.perform(get("/api/community"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("전체 조회 완료"));
    }

    @Test
    @WithUserDetails(value = "leesebi@email.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Order(4)
    @Transactional
    void updatePost() throws Exception {
        CommunitiesUpdateRequestDto requestDto = CommunitiesUpdateRequestDto.builder()
            .content("Update Content")
            .build();

        mvc.perform(put("/api/community/{communityId}", communityId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("수정 완료"));
    }

    @Test
    @WithUserDetails(value = "leesebi@email.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Order(5)
    @Transactional
    void searchPost() throws Exception {
        String keyword = "Title";
        mvc.perform(get("/api/community/search")
            .param("keyword", keyword)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("조회 완료"));
    }

    @Test
    @WithUserDetails(value = "leesebi@email.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Order(6)
    @Transactional
    void ranking() throws Exception {
        mvc.perform(get("/api/community/searchRank")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("조회 완료"));
    }

    @Test
    @WithUserDetails(value = "leesebi@email.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Order(7)
    @DirtiesContext
    @Transactional
    void deletePost() throws Exception {
        mvc.perform(delete("/api/community/{communityId}", communityId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("삭제 완료"));
    }
}