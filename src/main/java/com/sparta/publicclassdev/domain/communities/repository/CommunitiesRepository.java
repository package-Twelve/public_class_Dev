package com.sparta.publicclassdev.domain.communities.repository;

import com.sparta.publicclassdev.domain.communities.entity.Communities;
import com.sparta.publicclassdev.domain.users.entity.Users;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommunitiesRepository extends JpaRepository<Communities, Long> {
    @Query("select c from Communities c where c.user = :user order by c.modifiedAt desc limit 5")
    List<Communities> findPostByUserLimit5(Users user);

    List<Communities> findByTitleContainingIgnoreCase(String keyword);
}
