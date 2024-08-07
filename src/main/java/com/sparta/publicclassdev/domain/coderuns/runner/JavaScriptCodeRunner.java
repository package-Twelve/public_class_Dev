package com.sparta.publicclassdev.domain.coderuns.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class JavaScriptCodeRunner implements CodeRunner {

    @Override
    public String runCode(String code) {
        return runScript("node", ".js", code);
    }

    private String runScript(String command, String fileExtension, String code) {
        try {
            File scriptFile = File.createTempFile("script", fileExtension);
            try (FileWriter fileWriter = new FileWriter(scriptFile)){
                fileWriter.write(code);
            }
            
            Long startTime = System.currentTimeMillis();

            ProcessBuilder processBuilder = new ProcessBuilder(command, scriptFile.getAbsolutePath());
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

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
