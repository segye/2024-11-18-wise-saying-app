package repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.WiseSaying;

import java.io.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WiseSayingRepository {
    private static final Map<Integer, WiseSaying> wiseSayingMap = new LinkedHashMap<>(); // 삽입 순서 보장
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String dir = "db/wiseSaying";
    private static final String last_dir = dir + "/lastId.txt";

    private int id = 0;

    public WiseSayingRepository() {
        // 디렉토리 생성
        File directory = new File(dir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        loadFromFiles();
    }

    private void loadFromFiles() {
        File directory = new File(dir);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json") && !name.equals("data.json")); // data.json 제외
        if (files == null) return;

        for (File file : files) {
            try {
                WiseSaying wiseSaying = readFromFile(file);
                wiseSayingMap.put(wiseSaying.getId(), wiseSaying);
                readLastId();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int add(String message, String author) throws IOException {
        id++;

        WiseSaying wiseSaying = new WiseSaying(id, message, author);

        wiseSayingMap.put(id, wiseSaying);

        String json = String.format(
                "{\n  \"id\": %d,\n  \"message\": \"%s\",\n  \"author\": \"%s\"\n}",
                wiseSaying.getId(),
                wiseSaying.getMessage(),
                wiseSaying.getAuthor()
        );

        File file = new File(dir, id + ".json");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write(json);
        bufferedWriter.close();
        return id;
    }

    public boolean deleteById(int id) {
        WiseSaying removed = wiseSayingMap.remove(id);

        if (removed == null) {
            return false;
        }

        // 파일 삭제
        File file = new File(dir, id + ".json");
        if (file.exists()) {
            if (!file.delete()) {
                throw new RuntimeException(id + ".json 파일 삭제에 실패했습니다.");
            }
        }
        return true;
    }

    public void update(int id, String message, String author) throws IOException {
        WiseSaying wiseSaying = new WiseSaying(id, message, author);
        wiseSayingMap.put(id, wiseSaying);

        String json = String.format(
                "{\n  \"id\": %d,\n  \"message\": \"%s\",\n  \"author\": \"%s\"\n}",
                wiseSaying.getId(),
                wiseSaying.getMessage(),
                wiseSaying.getAuthor()
        );
        File file = new File(dir, id + ".json");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write(json);
        bufferedWriter.close();
    }

    public WiseSaying findById(int id) {
        return wiseSayingMap.get(id);
    }

    public List<WiseSaying> findAll() {
        return wiseSayingMap.values().stream()
                .sorted(Comparator.comparingLong(WiseSaying::getId).reversed())
                .toList();
    }

    public void fileBuild() throws IOException {
        File file = new File(dir, "data.json");
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        int idx = 0;
        for (WiseSaying wiseSaying : wiseSayingMap.values()) {
            sb.append(String.format(
                    " {\n  \"id\": %d,\n  \"message\": \"%s\",\n  \"author\": \"%s\"\n }",
                    wiseSaying.getId(),
                    wiseSaying.getMessage(),
                    wiseSaying.getAuthor()
            ));
            if (++idx < wiseSayingMap.size()) {
                sb.append(",\n");
            }
        }
        sb.append("\n]");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write(sb.toString());
        bufferedWriter.close();

    }

    public void saveLastId() throws IOException {
        File file = new File(dir, "lastId.txt");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write(String.valueOf(id));
        bufferedWriter.close();
    }

    private void readLastId() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(dir, "lastId.txt")));
            id = Integer.parseInt(bufferedReader.readLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private WiseSaying readFromFile(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line.trim());
        }

        String json = sb.toString();

        int id = extractJson(json, "id");
        String message = extractJsonString(json, "message");
        String author = extractJsonString(json, "author");

        return new WiseSaying(id, message, author);
    }

    private int extractJson(String json, String field) {
        String pattern = "\"" + field + "\":";
        int start = json.indexOf(pattern) + pattern.length();
        int end = json.indexOf(",", start);

        if (end == -1) {
            end = json.indexOf("}", start);
        }

        return Integer.parseInt(json.substring(start, end).trim());
    }

    private String extractJsonString(String json, String field) {
        String pattern = "\"" + field + "\": \"";
        int start = json.indexOf(pattern) + pattern.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

}
