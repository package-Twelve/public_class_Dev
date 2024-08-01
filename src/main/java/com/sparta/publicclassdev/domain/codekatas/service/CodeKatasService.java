package com.sparta.publicclassdev.domain.codekatas.service;

import com.sparta.publicclassdev.domain.codekatas.dto.CodeKatasDto;
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
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CodeKatasService {
    
    private final CodeKatasRepository codeKatasRepository;
    private final JwtUtil jwtUtil;
    
    public CodeKatasDto createCodeKata(HttpServletRequest request, CodeKatasDto codeKatasDto) {
        checkAdminRole(request);
        CodeKatas codeKatas = CodeKatas.builder()
            .contents(codeKatasDto.getContents())
            .markDate(null)
            .build();
        
        codeKatasRepository.save(codeKatas);
        
        return new CodeKatasDto(codeKatas.getId(), codeKatasDto.getContents(),
            codeKatas.getMarkDate());
    }
    
    public CodeKatasDto getCodeKata(HttpServletRequest request, Long id) {
        checkAdminRole(request);
        CodeKatas codeKatas = codeKatasRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODEKATA));
        return new CodeKatasDto(codeKatas.getId(), codeKatas.getContents(),
            codeKatas.getMarkDate());
    }
    
    public List<CodeKatasDto> getAllCodeKatas(HttpServletRequest request) {
        checkAdminRole(request);
        List<CodeKatas> codeKatasList = codeKatasRepository.findAll();
        return codeKatasList.stream()
            .map(kata -> new CodeKatasDto(kata.getId(), kata.getContents(), kata.getMarkDate()))
            .collect(Collectors.toList());
    }
    
    public void deleteCodeKata(HttpServletRequest request, Long id) {
        checkAdminRole(request);
        CodeKatas codeKatas = codeKatasRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODEKATA));
        codeKatasRepository.delete(codeKatas);
    }
    
    public CodeKatasDto updateCodeKata(HttpServletRequest request, Long id, CodeKatasDto codeKatasDto) {
        checkAdminRole(request);
        CodeKatas codeKatas = codeKatasRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODEKATA));
        
        codeKatas.updateContents(codeKatasDto.getContents());
        codeKatas.markCodeKatas(null);
        codeKatasRepository.save(codeKatas);
        
        return new CodeKatasDto(codeKatasDto.getId(), codeKatasDto.getContents(),
            codeKatas.getMarkDate());
    }
    
    public CodeKatasDto getTodayCodeKata() {
        List<CodeKatas> markedKatas = codeKatasRepository.findByMarkDate(LocalDate.now());
        if (!markedKatas.isEmpty()) {
            CodeKatas codeKatas = markedKatas.get(0);
            return new CodeKatasDto(codeKatas.getId(), codeKatas.getContents(),
                codeKatas.getMarkDate());
        } else {
            return createRandomCodeKata();
        }
    }
    
    public CodeKatasDto createRandomCodeKata() {
        List<CodeKatas> unmarkedKatas = codeKatasRepository.findByMarkDateIsNull();
        if (unmarkedKatas.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_CODEKATA);
        }
        
        CodeKatas codeKatas = unmarkedKatas.get(new Random().nextInt(unmarkedKatas.size()));
        codeKatas.markCodeKatas(LocalDate.now());
        codeKatasRepository.save(codeKatas);
        
        return new CodeKatasDto(codeKatas.getId(), codeKatas.getContents(),
            codeKatas.getMarkDate());
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