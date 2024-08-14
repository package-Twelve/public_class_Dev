package com.sparta.publicclassdev.domain.Community;

import static org.assertj.core.api.Assertions.assertThat;

import com.sparta.publicclassdev.domain.communities.entity.Communities;
import com.sparta.publicclassdev.domain.communities.entity.Communities.Category;
import com.sparta.publicclassdev.domain.communities.repository.CommunitiesRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@ActiveProfiles("test")
class CommunitiesRepositoryTest {

    @Autowired
    CommunitiesRepository communitiesRepository;

    @Autowired
    UsersRepository usersRepository;

    private Users user;

    @BeforeEach
    void setUp() {
        user = Users.builder()
            .name("test")
            .email("leesebi@email.com")
            .password("Test1234!")
            .role(RoleEnum.USER)
            .build();

        usersRepository.save(user);

        for(int i=0; i<7; i++){
            Communities communities = Communities.builder()
                .title("title : "+i)
                .content("content : "+i)
                .category(Category.INFO)
                .user(user)
                .build();
            communitiesRepository.save(communities);

            ReflectionTestUtils.setField(communities, "createdAt", LocalDateTime.now().plusMinutes(i));

            ReflectionTestUtils.setField(communities, "modifiedAt", LocalDateTime.now().plusMinutes(i));
        }
    }

    @Test
    @Order(1)
    @Transactional
    void findPostByUserLimit5() {
        List<Communities> communitiesList = communitiesRepository.findPostByUserLimit5(user);

        assertThat(communitiesList).hasSize(5);


        assertThat(communitiesList.get(0).getTitle()).isEqualTo("title : 6");
        assertThat(communitiesList.get(4).getTitle()).isEqualTo("title : 2");
    }

    @Test
    @Order(2)
    @Transactional
    void findByTitleContainingIgnoreCase() {
        List<Communities> communitiesList = communitiesRepository.findByTitleContainingIgnoreCase("title");

        assertThat(communitiesList).hasSize(7);
    }

    @Test
    @Order(3)
    @Transactional
    void findAllByOrderByCreatedAtDesc() {
        List<Communities> communitiesList = communitiesRepository.findAllByOrderByCreatedAtDesc();

        communitiesList.forEach(community ->
            System.out.println("Title: " + community.getTitle() + ", CreatedAt: " + community.getCreatedAt())
        );

        assertThat(communitiesList).hasSize(7);

        assertThat(communitiesList.get(0).getTitle()).isEqualTo("title : 0");
        assertThat(communitiesList.get(6).getTitle()).isEqualTo("title : 6");
    }
}