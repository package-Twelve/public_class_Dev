package com.sparta.publicclassdev.domain.Community;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sparta.publicclassdev.domain.communities.dto.CommunitiesRequestDto;
import com.sparta.publicclassdev.domain.communities.dto.CommunitiesResponseDto;
import com.sparta.publicclassdev.domain.communities.dto.CommunitiesUpdateRequestDto;
import com.sparta.publicclassdev.domain.communities.entity.Communities;
import com.sparta.publicclassdev.domain.communities.entity.Communities.Category;
import com.sparta.publicclassdev.domain.communities.repository.CommunitiesRepository;
import com.sparta.publicclassdev.domain.communities.service.CommunitiesService;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.global.exception.CustomException;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@ActiveProfiles("test")
class CommunitiesServiceTest {

    @Autowired
    private CommunitiesRepository communityRepository;

    @Autowired
    private CommunitiesService service;

    @Autowired
    private UsersRepository usersRepository;


    private CommunitiesRequestDto createTestRequestDto(){
        CommunitiesRequestDto requestDto = CommunitiesRequestDto.builder()
            .title("Test title")
            .content("Test content")
            .category(Category.INFO)
            .build();

        return requestDto;
    }


    private Users createTestUser() {
        Users user = Users.builder()
            .name("test user")
            .email("test@email.com")
            .password("Test1234!")
            .role(RoleEnum.USER)
            .build();

        ReflectionTestUtils.setField(user, "id", 1L);

        return usersRepository.save(user);
    }

    private Users createTestUser2(){
        Users user = Users.builder()
            .name("test user2")
            .email("test2@email.com")
            .password("Test1234!!")
            .role(RoleEnum.USER)
            .build();

        ReflectionTestUtils.setField(user, "id", 2L);

        return usersRepository.save(user);
    }

    private Users createTestAdmin(){
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

        CommunitiesResponseDto responseDto = service.createPost(requestDto, user);
        return communityRepository.findById(responseDto.getId()).orElse(null);
    }

    private Communities createTestCommunity2(Users user){
        CommunitiesRequestDto requestDto = CommunitiesRequestDto.builder()
            .title("Test title2")
            .content("Test content2")
            .category(Category.GOSSIP)
            .build();

        CommunitiesResponseDto responseDto = service.createPost(requestDto, user);
        return communityRepository.findById(responseDto.getId()).orElse(null);
    }

    @Test
    @DisplayName("생성 테스트")
    @Transactional
    void createPost() {
        CommunitiesRequestDto requestDto = createTestRequestDto();
        Users user = createTestUser();

        CommunitiesResponseDto responseDto = service.createPost(requestDto, user);

        Communities saveCommunity = communityRepository.findById(responseDto.getId()).orElse(null);

        assertEquals("Test title", saveCommunity.getTitle());
        assertEquals("Test content", saveCommunity.getContent());
        assertEquals(Category.INFO, saveCommunity.getCategory());
    }

    @Test
    @DisplayName("수정 테스트")
    @Transactional
    void updatePost() {
        Users user = createTestUser();
        Communities community = createTestCommunity(user);

        CommunitiesUpdateRequestDto updateRequestDto = CommunitiesUpdateRequestDto.builder()
            .content("Update Content")
            .build();

        CommunitiesResponseDto responseDto = service.updatePost(user, community.getId(), updateRequestDto);

        Communities updatedCommunity = communityRepository.findById(community.getId()).orElse(null);

        assertEquals(community.getTitle(), updatedCommunity.getTitle());
        assertEquals("Update Content", updatedCommunity.getContent());
        assertEquals(community.getCategory(), updatedCommunity.getCategory());
    }

    @Test
    @DisplayName("사용자가 아닌 사용자가 수정하려고 할 때")
    @Transactional
    void updatePost_unauthorization(){
        Users createUser = createTestUser();
        Users anotherUser = createTestUser2();

        Communities community = createTestCommunity(createUser);

        CommunitiesUpdateRequestDto updateRequestDto = CommunitiesUpdateRequestDto.builder()
            .content("Update Content")
            .build();

        assertThrows(CustomException.class, () -> {
            service.updatePost(anotherUser, community.getId(), updateRequestDto);
        });

    }

    @Test
    @DisplayName("admin 유저가 수정하는 경우")
    @Transactional
    void updatePost_adminUser(){
        Users admin = createTestAdmin();
        Communities community = createTestCommunity(admin);

        CommunitiesUpdateRequestDto updateRequestDto = CommunitiesUpdateRequestDto.builder()
            .content("Update Content")
            .build();

        CommunitiesResponseDto responseDto = service.updatePost(admin, community.getId(), updateRequestDto);
        Communities updatedCommunity = communityRepository.findById(community.getId()).orElse(null);

        assertEquals("Update Content", responseDto.getContent());
    }


    @Test
    @DisplayName("삭제 테스트")
    @Transactional
    void deletePost() {
        Users user = createTestUser();

        Communities community = createTestCommunity(user);

        service.deletePost(community.getId(), user);

        assertTrue(communityRepository.findById(community.getId()).isEmpty());
    }

    @Test
    @DisplayName("작성자가 아닌 다른 사용자가 지우려고 하는 경우")
    @Transactional
    void deletePost_unAuthorization(){
        Users createUser = createTestUser();
        Users anotherUser = createTestUser2();

        Communities community = createTestCommunity(createUser);

        assertThrows(CustomException.class, ()->{
            service.deletePost(community.getId(), anotherUser);
        });
    }

    @Test
    @DisplayName("admin 유저가 삭제할 경우")
    @Transactional
    void deletePost_admin(){
        Users createUser = createTestUser();
        Users adminUser = createTestAdmin();

        Communities communities = createTestCommunity(createUser);

        service.deletePost(communities.getId(), adminUser);

        assertTrue(communityRepository.findById(communities.getId()).isEmpty());
    }

    @Test
    @Transactional
    void findPosts() {
        Users user = createTestUser();

        Communities community = createTestCommunity(user);
        Communities community2 = createTestCommunity2(user);

        List<CommunitiesResponseDto> communityList = service.findPosts();

        assertNotNull(communityList);
        assertEquals(2, communityList.size());
    }

    @Test
    @Transactional
    void findPost() {
        Users user = createTestUser();

        Communities community = createTestCommunity(user);

        CommunitiesResponseDto responseDto = service.findPost(community.getId());

        assertNotNull(responseDto);
    }

    @Test
    @Transactional
    void searchPost() {

    }

    @Test
    void rank() {
    }
}