package com.sparta.publicclassdev.domain.communities.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sparta.publicclassdev.domain.communitycomments.entity.CommunityComments;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.global.entity.Timestamped;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "communities")
@Getter
@NoArgsConstructor
public class Communities extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(mappedBy = "community", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<CommunityComments> commentsList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users user;

    @Builder
    public Communities(String title, String content, Category category, Users user){
        this.title = title;
        this.content = content;
        this.category = category;
        this.user = user;
    }

    public void updateContent(String content){
        this.content = content;
    }


    @Getter
    @RequiredArgsConstructor
    public enum Category{
        INFO("INFO"),
        GOSSIP("GOSSIP"),
        RECRUIT("RECRUIT");

        @JsonValue
        private final String value;

        @JsonCreator
        public static Category from(String sub){
            for(Category category : Category.values()){
                if(category.getValue().equals(sub)){
                    return category;
                }
            }
            throw new CustomException(ErrorCode.NOT_FOUND_CATEGORY);
        }

    }
}
