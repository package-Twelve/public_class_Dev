package com.sparta.publicclassdev.domain.coderuns.runner;

import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class PythonCodeRunner implements CodeRunner {
    
    @Override
    public String runCode(String code) {
        validateCode(code);
        return runScript("python3", ".py", code);
    }
    
    private void validateCode(String code) {
        if (code.contains("import os") || code.contains("import sys") || code.contains("open(") || code.contains("import subprocess")) {
            throw new CustomException(ErrorCode.INVALID_CODE);
        }
    }
    
    private String runScript(String command, String fileExtension, String code) {
        try {
            File scriptFile = File.createTempFile("script", fileExtension);
            try (FileWriter fileWriter = new FileWriter(scriptFile)) {
                fileWriter.write(code);
            }
            
            Long startTime = System.currentTimeMillis();
            
            ProcessBuilder processBuilder = new ProcessBuilder(command, scriptFile.getAbsolutePath());
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                process.destroy();
                throw new CustomException(ErrorCode.TIMEOUT);
            }
            
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
            
            Long endTime = System.currentTimeMillis();
            Long responseTime = endTime - startTime;
            scriptFile.delete();
            
            stringBuilder.append("Execution time: ").append(responseTime).append(" ms");
            
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
