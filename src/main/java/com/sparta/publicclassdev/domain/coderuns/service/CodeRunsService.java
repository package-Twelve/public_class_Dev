package com.sparta.publicclassdev.domain.coderuns.service;

import com.sparta.publicclassdev.domain.codekatas.entity.CodeKatas;
import com.sparta.publicclassdev.domain.codekatas.repository.CodeKatasRepository;
import com.sparta.publicclassdev.domain.coderuns.dto.CodeRunsRequestDto;
import com.sparta.publicclassdev.domain.coderuns.dto.CodeRunsResponseDto;
import com.sparta.publicclassdev.domain.coderuns.entity.CodeRuns;
import com.sparta.publicclassdev.domain.coderuns.repository.CodeRunsRepository;
import com.sparta.publicclassdev.domain.coderuns.runner.CodeRunner;
import com.sparta.publicclassdev.domain.coderuns.runner.JavaCodeRunner;
import com.sparta.publicclassdev.domain.coderuns.runner.JavaScriptCodeRunner;
import com.sparta.publicclassdev.domain.coderuns.runner.PythonCodeRunner;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.teams.repository.TeamsRepository;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CodeRunsService {

    private final CodeRunsRepository codeRunsRepository;
    private final TeamsRepository teamsRepository;
    private final CodeKatasRepository codeKatasRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public CodeRunsResponseDto runCode(Long teamsId, Long codeKatasId, CodeRunsRequestDto requestDto) {
        Users users = getCurrentUser();
        Teams teams = findTeamById(teamsId);
        checkUserTeam(users, teams);

        CodeKatas codeKatas = findCodeKatasById(codeKatasId);
        CodeRunner codeRunner = getCodeRunner(requestDto.getLanguage());
        String result = codeRunner.runCode(requestDto.getCode());
        
        Long responseTime = runTime(result);

        CodeRuns codeRuns = getCreateCodeRun(teamsId, codeKatasId, requestDto.getCode(),
            requestDto.getLanguage(), result, responseTime, teams, codeKatas, users);

        codeRunsRepository.save(codeRuns);

        return new CodeRunsResponseDto(codeKatasId, teamsId, users.getId(), responseTime, result);
    }
    
    private long runTime(String result) {
        String[] lines = result.split("\\n");
        for (String line : lines) {
            if (line.startsWith("Execution time:")) {
                String timeStr = line.replace("Execution time:", "").replace(" ms", "").trim();
                return Long.parseLong(timeStr);
            }
        }
        throw new CustomException(ErrorCode.INVALID_REQUEST);
    }

    public List<CodeRuns> getCodeRunsByTeam(Long teamsId) {
        Users users = getCurrentUser();
        Teams teams = findTeamById(teamsId);
        checkUserTeam(users, teams);

        return codeRunsRepository.findAllByTeamsId(teamsId);
    }

    private CodeRuns getCreateCodeRun(Long teamsId, Long codeKatasId, String code, String language,
                                      String result, long responseTime, Teams teams, CodeKatas codeKatas, Users users) {
        Optional<CodeRuns> runCode = codeRunsRepository.findByTeamsIdAndCodeKatasId(teamsId, codeKatasId);
        CodeRuns codeRuns;
        if (runCode.isPresent()) {
            codeRuns = runCode.get();
            if (responseTime < codeRuns.getResponseTime()) {
                codeRuns.updateResponseTime(responseTime, result);
                codeRunsRepository.save(codeRuns);
            }
        } else {
            codeRuns = CodeRuns.builder()
                .code(code)
                .responseTime(responseTime)
                .result(result)
                .language(language)
                .teams(teams)
                .codeKatas(codeKatas)
                .users(users)
                .build();
            codeRunsRepository.save(codeRuns);
        }
        return codeRuns;
    }

    private CodeRunner getCodeRunner(String language) {
        switch (language.toLowerCase()) {
            case "python":
                return new PythonCodeRunner();
            case "javascript":
                return new JavaScriptCodeRunner();
            case "java":
                return new JavaCodeRunner();
            default:
                throw new CustomException(ErrorCode.NOT_SUPPORT_LANGUAGE);
        }
    }

    private Users getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return usersRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Teams findTeamById(Long teamsId) {
        return teamsRepository.findById(teamsId)
            .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));
    }

    private CodeKatas findCodeKatasById(Long codeKatasId) {
        return codeKatasRepository.findById(codeKatasId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODEKATA));
    }

    private void checkUserTeam(Users user, Teams team) {
        boolean isInTeam = user.getTeamUsers().stream()
            .anyMatch(teamUser -> teamUser.getTeams().equals(team));
        if (!isInTeam) {
            throw new CustomException(ErrorCode.USER_NOT_TEAM);
        }
    }
}
