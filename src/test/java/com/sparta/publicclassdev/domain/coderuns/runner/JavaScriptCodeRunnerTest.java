package com.sparta.publicclassdev.domain.coderuns.runner;

import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JavaScriptCodeRunnerTest {
    
    private CodeRunner codeRunner;
    
    @BeforeEach
    void setUp() {
        codeRunner = new JavaScriptCodeRunner();
    }
    
    @Test
    @DisplayName("유효한 JavaScript 코드 테스트")
    void runValidJavaScriptCode() {
        String code = "console.log('Hello, World!');";
        String output = codeRunner.runCode(code);
        assertTrue(output.contains("Hello, World!"), "결과값 : 'Hello, World!'");
    }
    
    @Test
    @DisplayName("허용되지 않는 작업이 포함된 코드 테스트")
    void runJavaScriptCodeWithDisallowedOperations() {
        String code = "const fs = require('fs'); console.log('Hello, World!');";
        CustomException thrown = assertThrows(CustomException.class, () -> codeRunner.runCode(code));
        assertEquals(ErrorCode.INVALID_CODE, thrown.getErrorCode(), "INVALID_CODE 에러 발생");
    }
}
