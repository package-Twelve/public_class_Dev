package com.sparta.publicclassdev.domain.codekatas.service;

import com.sparta.publicclassdev.domain.codekatas.dto.CodeKatasRequestDto;
import com.sparta.publicclassdev.domain.codekatas.dto.CodeKatasResponseDto;
import com.sparta.publicclassdev.domain.codekatas.entity.CodeKatas;
import com.sparta.publicclassdev.domain.codekatas.repository.CodeKatasRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import com.sparta.publicclassdev.global.security.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CodeKatasService {
    
    private final CodeKatasRepository codeKatasRepository;
    private final JwtUtil jwtUtil;
    
    public CodeKatasResponseDto createCodeKata(HttpServletRequest request, CodeKatasRequestDto requestDto) {
        checkAdminRole(request);
        CodeKatas codeKatas = CodeKatas.builder()
            .title(requestDto.getTitle())
            .contents(requestDto.getContents())
            .markDate(null)
            .build();
        
        codeKatasRepository.save(codeKatas);
        
        return new CodeKatasResponseDto(codeKatas.getId(), codeKatas.getTitle(), requestDto.getContents(), codeKatas.getMarkDate());
    }
    
    public CodeKatasResponseDto getCodeKata(HttpServletRequest request, Long id) {
        checkAdminRole(request);
        CodeKatas codeKatas = codeKatasRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODEKATA));
        return new CodeKatasResponseDto(codeKatas.getId(), codeKatas.getTitle(), codeKatas.getContents(), codeKatas.getMarkDate());
    }
    
    public Page<CodeKatasResponseDto> getAllCodeKatas(HttpServletRequest request, Pageable pageable) {
        checkAdminRole(request);
        Page<CodeKatas> codeKatasPage = codeKatasRepository.findAll(pageable);
        return codeKatasPage.map(codeKatas -> new CodeKatasResponseDto(codeKatas.getId(), codeKatas.getTitle(), codeKatas.getContents(), codeKatas.getMarkDate()));
    }
    
    public void deleteCodeKata(HttpServletRequest request, Long id) {
        checkAdminRole(request);
        CodeKatas codeKatas = codeKatasRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODEKATA));
        codeKatasRepository.delete(codeKatas);
    }
    
    public CodeKatasResponseDto updateCodeKata(HttpServletRequest request, Long id, CodeKatasRequestDto requestDto) {
        checkAdminRole(request);
        CodeKatas codeKatas = codeKatasRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODEKATA));
        
        codeKatas.updateContents(requestDto.getTitle(), requestDto.getContents());
        codeKatas.markCodeKatas(null);
        codeKatasRepository.save(codeKatas);
        
        return new CodeKatasResponseDto(codeKatas.getId(), codeKatas.getTitle(), codeKatas.getContents(), codeKatas.getMarkDate());
    }
    
    public CodeKatasResponseDto getTodayCodeKata() {
        List<CodeKatas> markKatas = codeKatasRepository.findByMarkDate(LocalDate.now());
        if (!markKatas.isEmpty()) {
            CodeKatas codeKatas = markKatas.get(0);
            return new CodeKatasResponseDto(codeKatas.getId(), codeKatas.getTitle(), codeKatas.getContents(), codeKatas.getMarkDate());
        } else {
            return createRandomCodeKata();
        }
    }
    
    public CodeKatasResponseDto createRandomCodeKata() {
        List<CodeKatas> unmarkedKatas = codeKatasRepository.findByMarkDateIsNull();
        if (unmarkedKatas.isEmpty()) {
            CodeKatas defaultKata = CodeKatas.builder()
                .title("기본 코드카타")
                .contents("기본 코드카타입니다. 코드카타를 추가해주세요")
                .markDate(null)
                .build();
            
            codeKatasRepository.save(defaultKata);
            
            unmarkedKatas = List.of(defaultKata);
        }
        
        CodeKatas codeKatas = unmarkedKatas.get(new Random().nextInt(unmarkedKatas.size()));
        codeKatas.markCodeKatas(LocalDate.now());
        codeKatasRepository.save(codeKatas);
        
        return new CodeKatasResponseDto(codeKatas.getId(), codeKatas.getTitle(), codeKatas.getContents(), codeKatas.getMarkDate());
    }
    
    private void checkAdminRole(HttpServletRequest request) {
        String token = jwtUtil.getJwtFromHeader(request);
        Claims claims = jwtUtil.getUserInfoFromToken(token);
        String role = "ROLE_" + claims.get("auth").toString().trim();
        if (!RoleEnum.ADMIN.getAuthority().equals(role)) {
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED);
        }
    }
}