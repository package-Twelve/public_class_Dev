package com.sparta.publicclassdev.domain.communitycomments.repository;

import com.sparta.publicclassdev.domain.communities.entity.Communities;
import com.sparta.publicclassdev.domain.communitycomments.entity.CommunityComments;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityCommentsRepository extends JpaRepository<CommunityComments, Long> {
    List<CommunityComments> findByCommunity(Communities community);
}
