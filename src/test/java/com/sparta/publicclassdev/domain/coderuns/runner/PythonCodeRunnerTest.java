package com.sparta.publicclassdev.domain.coderuns.runner;

import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PythonCodeRunnerTest {
    
    private CodeRunner codeRunner;
    
    @BeforeEach
    void setUp() {
        codeRunner = new PythonCodeRunner();
    }
    
    @Test
    @DisplayName("유효한 Python 코드 테스트")
    void runPythonCode() {
        String code = "print('Python')";
        String result = codeRunner.runCode(code);
        assertTrue(result.contains("Python"));
    }
    
    @Test
    @DisplayName("허용되지 않는 작업이 포함된 코드 테스트")
    void runInvalidCode() {
        String code = "import os; print('Hello, World!')";
        CustomException thrown = assertThrows(CustomException.class, () -> codeRunner.runCode(code));
        assertEquals(ErrorCode.INVALID_CODE, thrown.getErrorCode(), "INVALID_CODE 에러 발생");
    }
}
