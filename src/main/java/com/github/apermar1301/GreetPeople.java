package com.github.apermar1301;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GreetPeople {

    public static void main(String[] args) {
        new GreetPeople().launchPipeline();
    }

    public void launchPipeline() {
        try {
            ProcessBuilder namesProcessBuilder = createNamesProcessBuilder("8");
            ProcessBuilder greetingProcessBuilder = createGreetingProcessBuilder();

            List<Process> pipelineProcesses = ProcessBuilder.startPipeline(
                    List.of(namesProcessBuilder, greetingProcessBuilder)
            );

            Process namesProcess = pipelineProcesses.get(0);
            Process greetingProcess = pipelineProcesses.get(1);

            CompletableFuture<List<String>> namesErrorFuture = CompletableFuture.supplyAsync(
                    () -> readLines(namesProcess.getErrorStream())
            );
            CompletableFuture<List<String>> greetingErrorFuture = CompletableFuture.supplyAsync(
                    () -> readLines(greetingProcess.getErrorStream())
            );

            try (BufferedReader outputReader = new BufferedReader(
                    new InputStreamReader(greetingProcess.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = outputReader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int namesExitCode = namesProcess.waitFor();
            int greetingExitCode = greetingProcess.waitFor();

            List<String> namesErrors = namesErrorFuture.join();
            List<String> greetingErrors = greetingErrorFuture.join();

            handleProcessErrors(namesExitCode, namesErrors, "names.jar");
            handleProcessErrors(greetingExitCode, greetingErrors, "greeting.jar");

        } catch (IOException e) {
            System.err.println("Pipeline I/O Error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Pipeline interrupted: " + e.getMessage());
        }
    }

    private void handleProcessErrors(int exitCode, List<String> errors, String componentName) {
        if (exitCode != 0 || !errors.isEmpty()) {
            if (errors.isEmpty()) {
                System.err.println("Error: " + componentName + " exited with code " + exitCode);
            } else {
                for (String error : errors) {
                    System.err.println(error);
                }
            }
        }
    }

    private ProcessBuilder createNamesProcessBuilder(String count) {
        File namesJar = findJar("names.jar");
        if (namesJar != null) {
            return new ProcessBuilder(getJavaBin(), "-jar", namesJar.getAbsolutePath(), count);
        }
        return new ProcessBuilder(getJavaBin(), "-cp", getAbsoluteClasspath(), Names.class.getName(), count);
    }

    private ProcessBuilder createGreetingProcessBuilder() {
        File greetingJar = findJar("greeting.jar");
        if (greetingJar != null) {
            return new ProcessBuilder(getJavaBin(), "-jar", greetingJar.getAbsolutePath());
        }
        return new ProcessBuilder(getJavaBin(), "-cp", getAbsoluteClasspath(), Greeting.class.getName());
    }

    private static File findJar(String jarName) {
        try {
            File codeSource = new File(GreetPeople.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File dir = codeSource.isFile() ? codeSource.getParentFile() : codeSource;
            File candidate = new File(dir, jarName);
            if (candidate.exists()) {
                return candidate;
            }
        } catch (Exception ignored) {
        }

        File inCurrentDir = new File(jarName);
        if (inCurrentDir.exists()) {
            return inCurrentDir;
        }

        File inTarget = new File("target", jarName);
        if (inTarget.exists()) {
            return inTarget;
        }

        return null;
    }

    private static String getJavaBin() {
        String javaHome = System.getProperty("java.home");
        return javaHome + File.separator + "bin" + File.separator + "java";
    }

    private static String getAbsoluteClasspath() {
        String rawClasspath = System.getProperty("java.class.path");
        return Arrays.stream(rawClasspath.split(File.pathSeparator))
                .map(path -> new File(path).getAbsolutePath())
                .collect(Collectors.joining(File.pathSeparator));
    }

    private static List<String> readLines(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().toList();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}
