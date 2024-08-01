package com.sparta.publicclassdev.domain.communitycomments.entity;

import com.sparta.publicclassdev.domain.communities.entity.Communities;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.global.entity.Timestamped;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "communitycomments")
@Getter
@NoArgsConstructor
public class CommunityComments extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "communities_id")
    private Communities community;

    @Builder
    public CommunityComments(String content, Users user, Communities community){
        this.community = community;
        this.user = user;
        this.content = content;
    }

    public void updateContent(String contents) {
        this.content = contents;
    }
}
