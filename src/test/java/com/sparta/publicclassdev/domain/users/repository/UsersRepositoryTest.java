package com.sparta.publicclassdev.domain.users.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class UsersRepositoryTest {
    @Autowired
    UsersRepository usersRepository;

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
    }
    @Test
    @DisplayName("이름으로 회원가져오기 테스트")
    void findByNameTest() {
        String name1 = "testuser1";
        String name2 = "testuser2";

        Users user1 = usersRepository.findByName(name1).orElse(new Users());
        Users user2 = usersRepository.findByName(name2).orElse(new Users());

        assertEquals(user1.getName(), name1);
        assertEquals(user2.getName(), name2);
    }
    @Test
    @DisplayName("이메일로 회원가져오기 테스트")
    void findByEmailTest() {
        String email1 = "test@email.com";
        String email2 = "test2@email.com";

        Users user1 = usersRepository.findByEmail(email1).orElse(new Users());
        Users user2 = usersRepository.findByEmail(email2).orElse(new Users());

        assertEquals(user1.getEmail(), email1);
        assertEquals(user2.getEmail(), email2);
    }
}
