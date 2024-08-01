package com.sparta.publicclassdev.domain.codekatas.service;

import com.sparta.publicclassdev.domain.codekatas.dto.CodeKatasDto;
import com.sparta.publicclassdev.domain.codekatas.entity.CodeKatas;
import com.sparta.publicclassdev.domain.codekatas.repository.CodeKatasRepository;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CodeKatasService {
    
    private final CodeKatasRepository codeKatasRepository;
    
    public CodeKatasDto createCodeKata(CodeKatasDto codeKatasDto) {
        checkAdminRole();
        CodeKatas codeKatas = CodeKatas.builder()
            .contents(codeKatasDto.getContents())
            .markDate(null)
            .build();
        
        codeKatasRepository.save(codeKatas);
        
        return new CodeKatasDto(codeKatas.getId(), codeKatasDto.getContents(),
            codeKatas.getMarkDate());
    }
    
    public CodeKatasDto getCodeKata(Long id) {
        checkAdminRole();
        CodeKatas codeKatas = codeKatasRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODEKATA));
        return new CodeKatasDto(codeKatas.getId(), codeKatas.getContents(),
            codeKatas.getMarkDate());
    }
    
    public List<CodeKatasDto> getAllCodeKatas() {
        checkAdminRole();
        List<CodeKatas> codeKatasList = codeKatasRepository.findAll();
        return codeKatasList.stream()
            .map(kata -> new CodeKatasDto(kata.getId(), kata.getContents(), kata.getMarkDate()))
            .collect(Collectors.toList());
    }
    
    public void deleteCodeKata(Long id) {
        checkAdminRole();
        CodeKatas codeKatas = codeKatasRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODEKATA));
        codeKatasRepository.delete(codeKatas);
    }
    
    public CodeKatasDto updateCodeKata(Long id, CodeKatasDto codeKatasDto) {
        checkAdminRole();
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
    
    private void checkAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED);
        }
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        boolean isAdmin = userDetails.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED);
        }
    }
}
