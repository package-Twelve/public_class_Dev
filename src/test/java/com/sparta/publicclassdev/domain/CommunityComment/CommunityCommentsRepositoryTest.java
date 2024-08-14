package com.sparta.publicclassdev.domain.CommunityComment;

import static org.assertj.core.api.Assertions.assertThat;

import com.sparta.publicclassdev.domain.communities.entity.Communities;
import com.sparta.publicclassdev.domain.communities.entity.Communities.Category;
import com.sparta.publicclassdev.domain.communities.repository.CommunitiesRepository;
import com.sparta.publicclassdev.domain.communitycomments.entity.CommunityComments;
import com.sparta.publicclassdev.domain.communitycomments.repository.CommunityCommentsRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class CommunityCommentsRepositoryTest {

    @Autowired
    private CommunitiesRepository communitiesRepository;

    @Autowired
    private CommunityCommentsRepository commentsRepository;

    @Autowired
    private UsersRepository usersRepository;

    private Communities community;

    @BeforeEach
    void setUp(){
        Users user = Users.builder()
            .name("test")
            .email("leesebi@email.com")
            .password("Test1234!")
            .role(RoleEnum.USER)
            .build();

        usersRepository.save(user);

        community = Communities.builder()
            .title("title")
            .content("content")
            .category(Category.INFO)
            .user(user)
            .build();
        communitiesRepository.save(community);

        CommunityComments comments = CommunityComments.builder()
            .content("comments")
            .community(community)
            .user(user)
            .build();

        CommunityComments comments2 = CommunityComments.builder()
            .content("comments2")
            .community(community)
            .user(user)
            .build();

        commentsRepository.save(comments);
        commentsRepository.save(comments2);
    }

    @Test
    void testFindByCommunity(){
        List<CommunityComments> commentsList = commentsRepository.findByCommunity(community);

        assertThat(commentsList).hasSize(2);
    }
}