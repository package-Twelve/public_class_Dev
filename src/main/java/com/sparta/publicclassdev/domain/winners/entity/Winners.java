package com.sparta.publicclassdev.domain.winners.entity;

import com.sparta.publicclassdev.domain.coderuns.entity.CodeRuns;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.global.entity.Timestamped;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "winners")
@Getter
@NoArgsConstructor
public class Winners extends Timestamped {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String code;
    
    private String language;
    
    private Long responseTime;
    
    private String result;
    
    private String teamName;
    
    private LocalDate date;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "coderuns_id")
    private CodeRuns codeRuns;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teams_id", insertable = false, updatable = false)
    private Teams teams;
    
    @Builder
    public Winners(String code, String language, Long responseTime, String result, String teamName,
                   LocalDate date, CodeRuns codeRuns, Teams teams) {
        this.code = code;
        this.language = language;
        this.responseTime = responseTime;
        this.result = result;
        this.teamName = teamName;
        this.date = date;
        this.codeRuns = codeRuns;
        this.teams = teams;
    }
}
