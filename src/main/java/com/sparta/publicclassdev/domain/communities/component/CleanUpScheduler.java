package com.sparta.publicclassdev.domain.communities.component;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CleanUpScheduler {
    private final RedisTemplate<String, Object> redisTemplate;

    public CleanUpScheduler(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(fixedRate = 1800000)
    public void cleanUpOldSearchData(){
        String key = "searchRank";
        long currentTime = System.currentTimeMillis();

        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();

        Set<Object> rankAll = zSetOperations.reverseRange(key, 0, -1);
        if(rankAll != null && !rankAll.isEmpty()){
            deletePastKeyword(rankAll, currentTime);
        }
    }

    public void deletePastKeyword(Set<Object> keywordList, long currentTime) {

        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();

        for (Object keywords : keywordList) {
            String validTimeObj = (String) redisTemplate.opsForHash().get("keyword_data", keywords);

            if(validTimeObj != null){
                long time = Long.parseLong(validTimeObj);

                if(currentTime - time >= TimeUnit.MINUTES.toMillis(30)){
                    zSetOperations.remove("searchRank", keywords);
                    redisTemplate.opsForHash().delete("keyword_data", keywords);
                }
            }
        }
    }

}
