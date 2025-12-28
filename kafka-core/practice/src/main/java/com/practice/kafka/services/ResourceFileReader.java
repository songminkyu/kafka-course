package com.practice.kafka.services;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceFileReader {

    public static List<String> getResourceFiles() {
        List<String> filenames = new ArrayList<>();

        try {
            // 프로젝트 루트에서 resources 폴더로 직접 접근
            String projectPath = System.getProperty("user.dir");
            Path resourcesPath = Paths.get(projectPath, "practice", "src", "main", "resources");

            System.out.println("Resources 경로: " + resourcesPath);

            if (Files.exists(resourcesPath)) {
                filenames = Files.list(resourcesPath)
                        .filter(Files::isRegularFile)
                        .map(path -> path.getFileName().toString())
                        .collect(Collectors.toList());
            } else {
                System.out.println("Resources 폴더를 찾을 수 없습니다: " + resourcesPath);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return filenames;
    }

    // 파일 내용도 읽기
    public static String readResourceFile(String filename) {
        try {
            String projectPath = System.getProperty("user.dir");
            Path filePath = Paths.get(projectPath, "practice", "src", "main", "resources", filename);

            if (Files.exists(filePath)) {
                return new String(Files.readAllBytes(filePath));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println("=== Resources 파일 목록 ===");
        List<String> files = getResourceFiles();

        for (String file : files) {
            System.out.println("- " + file);
        }

        // 파일 읽기 예시
        if (files.contains("pizza_sample.txt")) {
            String content = readResourceFile("pizza_sample.txt");
            System.out.println("\n=== pizza_sample.txt 내용 ===");
            System.out.println(content);
        }
    }
}