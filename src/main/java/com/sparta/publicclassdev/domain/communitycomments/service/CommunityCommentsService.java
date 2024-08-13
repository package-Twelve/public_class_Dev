package com.sparta.publicclassdev.domain.communitycomments.service;

import com.sparta.publicclassdev.domain.communities.entity.Communities;
import com.sparta.publicclassdev.domain.communities.repository.CommunitiesRepository;
import com.sparta.publicclassdev.domain.communitycomments.dto.CommunityCommentResponseDto;
import com.sparta.publicclassdev.domain.communitycomments.dto.CommunityCommentsRequestDto;
import com.sparta.publicclassdev.domain.communitycomments.entity.CommunityComments;
import com.sparta.publicclassdev.domain.communitycomments.repository.CommunityCommentsRepository;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommunityCommentsService {

    private final CommunityCommentsRepository repository;
    private final CommunitiesRepository communityRepository;
    public CommunityCommentResponseDto createComment(Long communityId, CommunityCommentsRequestDto requestDto, Users user) {
        Communities community = checkCommunity(communityId);

        CommunityComments comment = CommunityComments.builder()
            .content(requestDto.getContents())
            .user(user)
            .community(community)
            .build();

        repository.save(comment);

        return new CommunityCommentResponseDto(requestDto.getContents(), community.getId());
    }

    public CommunityCommentResponseDto updateComment(Long communityId, Long commentId, CommunityCommentsRequestDto requestDto, Users user) {
        Communities community = checkCommunity(communityId);
        CommunityComments comment = checkComment(commentId);

        if(!Objects.equals(comment.getUser().getId(), user.getId())){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED);
        }

        comment.updateContent(requestDto.getContents());

        repository.save(comment);

        return new CommunityCommentResponseDto(requestDto.getContents(), community.getId());
    }

    public List<CommunityCommentResponseDto> findComments(Long communityId) {
        Communities community = checkCommunity(communityId);

        List<CommunityComments> comment = repository.findByCommunity(community);
        return comment.stream()
            .map(CommunityComments -> new CommunityCommentResponseDto(CommunityComments.getContent(), CommunityComments.getCommunity().getId()))
            .collect(Collectors.toList());
    }

    public void deleteComment(Long commentId, Long communityId, Users user) {
        checkCommunity(communityId);
        CommunityComments comment = checkComment(commentId);

        if(!Objects.equals(comment.getUser().getId(), user.getId())){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED);
        }

        repository.delete(comment);
    }

    public Communities checkCommunity(Long communityId){
        return communityRepository.findById(communityId).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND_COMMUNITY_POST)
        );
    }

    public CommunityComments checkComment(Long commentId){
        return repository.findById(commentId).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND_COMMUNITY_COMMENT)
        );
    }
}
