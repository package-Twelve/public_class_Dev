package com.sparta.publicclassdev.domain.communities.service;

import com.sparta.publicclassdev.domain.communities.dto.CommunitiesRankDto;
import com.sparta.publicclassdev.domain.communities.dto.CommunitiesRequestDto;
import com.sparta.publicclassdev.domain.communities.dto.CommunitiesResponseDto;
import com.sparta.publicclassdev.domain.communities.dto.CommunitiesUpdateRequestDto;
import com.sparta.publicclassdev.domain.communities.entity.Communities;
import com.sparta.publicclassdev.domain.communities.repository.CommunitiesRepository;
import com.sparta.publicclassdev.domain.communitycomments.dto.CommunityCommentResponseDto;
import com.sparta.publicclassdev.domain.communitycomments.entity.CommunityComments;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommunitiesService {

    private final CommunitiesRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void cleanUpOldSearchData(){
        String key = "searchRank";
        Long currentTime = System.currentTimeMillis();

        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();

        Set<Object> rankAll = zSetOperations.reverseRange(key, 0, -1);
        if(rankAll != null && !rankAll.isEmpty()){
            deletePastKeyword(rankAll, currentTime);
        }
    }

    public CommunitiesResponseDto createPost(CommunitiesRequestDto requestDto, Users user) {
        Communities community = Communities.builder()
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .category(requestDto.getCategory())
            .user(user)
            .build();

        Communities saveCommunity = repository.save(community);

        return new CommunitiesResponseDto(saveCommunity.getId(), saveCommunity.getCreatedAt(), saveCommunity.getTitle(), saveCommunity.getTitle(), saveCommunity.getCategory());
    }

    public CommunitiesResponseDto updatePost(Users user, Long communityId, CommunitiesUpdateRequestDto requestDto) {
        Communities community = checkCommunity(communityId);
        if(!user.getRole().equals(RoleEnum.ADMIN)){
            if(!Objects.equals(community.getUser().getId(), user.getId())){
                throw new CustomException(ErrorCode.NOT_UNAUTHORIZED);
            }
        }

        community.updateContent(requestDto.getContent());
        repository.save(community);
        return new CommunitiesResponseDto(community.getTitle(), community.getContent(), community.getCategory());
    }

    public void deletePost(Long communityId, Users user) {
        Communities community = checkCommunity(communityId);

        if(!user.getRole().equals(RoleEnum.ADMIN)){
            if(!Objects.equals(community.getUser().getId(), user.getId())){
                throw new CustomException(ErrorCode.NOT_UNAUTHORIZED);
            }
        }

        repository.delete(community);
    }

    public List<CommunitiesResponseDto> findPosts() {
        List<Communities> postList = repository.findAllByOrderByCreatedAtDesc();
        return postList.stream().map(communities -> new CommunitiesResponseDto(communities.getId(), communities.getCreatedAt(), communities.getTitle(), communities.getContent(), communities.getCategory()))
            .collect(Collectors.toList());
    }

    public CommunitiesResponseDto findPost(Long communityId) {
        Communities community = checkCommunity(communityId);
        List<CommunityComments> commentsList = community.getCommentsList();
        if (commentsList == null) {
            commentsList = Collections.emptyList();
        }
        List<CommunityCommentResponseDto> responseDto = commentsList.stream().map(communityComments -> new CommunityCommentResponseDto(communityComments.getContent(), communityComments.getId(), communityComments.getId()))
            .toList();
        return new CommunitiesResponseDto(community.getId(), community.getTitle(), community.getContent(), community.getCreatedAt(), community.getCategory(), community.getUser().getName(), responseDto);
    }

    public Communities checkCommunity(Long communityId){
        return repository.findById(communityId).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND_COMMUNITY_POST)

        );
    }


    public List<CommunitiesResponseDto> searchPost(String keyword) {
        List<Communities> communityPage = repository.findByTitleContainingIgnoreCase(keyword);
        Long currentTime = System.currentTimeMillis();

        if(!communityPage.isEmpty()){
            redisTemplate.opsForZSet().incrementScore("searchRank",keyword,1);
            redisTemplate.opsForHash().put("keyword_data", keyword, String.valueOf(currentTime));
        }

        return communityPage.stream()
            .map(communities -> new CommunitiesResponseDto(communities.getId(), communities.getCreatedAt(), communities.getTitle(), communities.getContent(), communities.getCategory()))
            .collect(Collectors.toList());
    }

    public List<CommunitiesRankDto> rank() {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();

        Set<ZSetOperations.TypedTuple<Object>> typedTuples = zSetOperations.reverseRangeWithScores("searchRank", 0, 4);

        return typedTuples.stream().map(typedTuple -> new CommunitiesRankDto(
            (String) typedTuple.getValue())).toList();
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
