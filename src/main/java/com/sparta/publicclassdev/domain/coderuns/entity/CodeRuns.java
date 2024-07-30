package com.sparta.publicclassdev.domain.coderuns.entity;

import com.sparta.publicclassdev.domain.codekatas.entity.CodeKatas;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.global.entity.Timestamped;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "coderuns")
@Getter
@NoArgsConstructor
public class CodeRuns extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private Long responseTime;

    private String result;

    private String language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teams_id")
    private Teams teams;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codekatas_id")
    private CodeKatas codeKatas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;

    @Builder
    public CodeRuns(String code, Long responseTime, String result, String language, Teams teams,
        CodeKatas codeKatas, Users users) {
        this.code = code;
        this.responseTime = responseTime;
        this.result = result;
        this.language = language;
        this.teams = teams;
        this.codeKatas = codeKatas;
        this.users = users;
    }

    public void updateResponseTime(Long responseTime, String result) {
        this.responseTime = responseTime;
        this.result = result;
    }
}
