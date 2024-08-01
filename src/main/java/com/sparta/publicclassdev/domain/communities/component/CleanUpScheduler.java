package com.sparta.publicclassdev.domain.communities.component;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CleanUpScheduler {
    private final RedisTemplate<String, String> redisTemplate;

    public CleanUpScheduler(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(fixedRate = 1800000)
    public void cleanUpOldSearchData(){
        String key = "searchRank";
        long currentTime = System.currentTimeMillis();

        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();

        Set<String> rankAll = zSetOperations.reverseRange(key, 0, -1);
        if(rankAll != null && !rankAll.isEmpty()){
            deletePastKeyword(rankAll, currentTime);
        }
    }

    public void deletePastKeyword(Set<String> keywordList, long currentTime) {

        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();

        for (String keywords : keywordList) {
            String validTimeObj = (String) redisTemplate.opsForHash().get("keyword_data", keywords);

            if(validTimeObj != null){
                long time = Long.parseLong(validTimeObj);

                if(currentTime - time >= TimeUnit.MINUTES.toMillis(1)){
                    zSetOperations.remove("searchRank", keywords);
                    System.out.println(zSetOperations);
                    redisTemplate.opsForHash().delete("keyword_data", keywords);
                }
            }
        }
    }

}
