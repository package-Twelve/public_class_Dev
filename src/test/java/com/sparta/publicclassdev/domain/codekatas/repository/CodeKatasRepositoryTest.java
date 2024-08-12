package com.sparta.publicclassdev.domain.codekatas.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.sparta.publicclassdev.domain.codekatas.entity.CodeKatas;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class CodeKatasRepositoryTest {
    
    @Autowired
    private CodeKatasRepository codeKatasRepository;
    
    @Test
    public void testFindByMarkDateIsNull() {
        CodeKatas codeKatas = CodeKatas.builder()
            .title("test")
            .contents("contents")
            .markDate(null)
            .build();
        codeKatasRepository.save(codeKatas);
        
        List<CodeKatas> result = codeKatasRepository.findByMarkDateIsNull();
        
        assertThat(result).isNotEmpty();
        assertThat(result).contains(codeKatas);
    }
    
    @Test
    public void testFindByMarkDate() {
        LocalDate today = LocalDate.now();
        CodeKatas codeKatas = CodeKatas.builder()
            .title("test")
            .contents("contents")
            .markDate(today)
            .build();
        codeKatasRepository.save(codeKatas);
        
        List<CodeKatas> result = codeKatasRepository.findByMarkDate(today);
        
        assertThat(result).isNotEmpty();
        assertThat(result).contains(codeKatas);
    }
}