package com.sparta.publicclassdev.domain.Community;

import com.sparta.publicclassdev.domain.communities.component.CleanUpScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CleanUpSchedulerTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private CleanUpScheduler cleanUpScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    void cleanUpOldSearchData() {
        Set<Object> mockSet = Set.of("keyword1", "keyword2");
        when(zSetOperations.reverseRange("searchRank", 0, -1)).thenReturn(mockSet);

        cleanUpScheduler.cleanUpOldSearchData();

        verify(zSetOperations, times(1)).reverseRange("searchRank", 0, -1);
    }

    @Test
    void deletePastKeyword() {
        Set<Object> mockKeywords = Set.of("keyword1");
        long currentTime = System.currentTimeMillis();

        when(hashOperations.get("keyword_data", "keyword1"))
            .thenReturn(String.valueOf(currentTime - 31 * 60 * 1000));

        cleanUpScheduler.deletePastKeyword(mockKeywords, currentTime);

        verify(zSetOperations, times(1)).remove("searchRank", "keyword1");
        verify(hashOperations, times(1)).delete("keyword_data", "keyword1");
    }
}
