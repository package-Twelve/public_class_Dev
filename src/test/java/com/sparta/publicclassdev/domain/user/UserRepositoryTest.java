package com.sparta.publicclassdev.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
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
    void testFindByName() {
        // given
        String name1 = "testuser1";
        String name2 = "testuser2";

        // when
        Optional<Users> optionalUsers1 = usersRepository.findByName(name1);
        Optional<Users> optionalUsers2 = usersRepository.findByName(name2);

        Users user1 = optionalUsers1.orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        Users user2 = optionalUsers2.orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        // then
        assertEquals(1L, user1.getId());
        assertEquals(2L, user2.getId());
    }
    @Test
    void testFindByEmail() {
        // given
        String email1 = "test@email.com";
        String email2 = "test2@email.com";

        // when
        Optional<Users> optionalUsers1 = usersRepository.findByEmail(email1);
        Optional<Users> optionalUsers2 = usersRepository.findByEmail(email2);

        Users user1 = optionalUsers1.orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        Users user2 = optionalUsers2.orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        // then
        assertEquals(1L, user1.getId());
        assertEquals(2L, user2.getId());
    }
}
