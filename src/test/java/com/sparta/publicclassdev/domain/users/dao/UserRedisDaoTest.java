package com.sparta.publicclassdev.domain.users.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class UserRedisDaoTest {
    @Autowired
    private UserRedisDao userRedisDao;
    private String key;
    private String refreshToken;
    private String accessToken;
    @BeforeAll
    void setUp() {
        key = "test@email.com";
        refreshToken = "Bearer refreshToken";
        accessToken = "Bearer accessToken";
    }
    @Test
    @Order(1)
    void setRefreshToken() {
        userRedisDao.setRefreshToken(key, refreshToken, 60 * 60 * 1000L * 336);
    }

    @Test
    @Order(2)
    void getRefreshToken() {
        assertEquals(userRedisDao.getRefreshToken(key), refreshToken);
    }

    @Test
    @Order(3)
    void hasKey() {
        assertTrue(userRedisDao.hasKey(key));
    }

    @Test
    @Order(4)
    void deleteRefreshToken() {
        userRedisDao.deleteRefreshToken(key);
        assertNull(userRedisDao.getRefreshToken(key));
    }

    @Test
    void setBlackList() {
        userRedisDao.setBlackList(accessToken, "logout", 1775011L);
    }

    @Test
    void getBlackList() {
        assertEquals(userRedisDao.getBlackList(accessToken), "logout");
    }

    @Test
    void deleteBlackList() {
        userRedisDao.deleteBlackList(accessToken);
        assertNull(userRedisDao.getBlackList(accessToken));
    }
}