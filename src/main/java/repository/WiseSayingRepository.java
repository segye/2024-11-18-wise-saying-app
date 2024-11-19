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
    private static final String last_dir =  dir + "/lastId.txt";

    private int id;

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
                WiseSaying wiseSaying = objectMapper.readValue(file, WiseSaying.class);
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
        File file = new File(dir, id + ".json");
        objectMapper.writeValue(file, wiseSaying);

        return id;
    }

    public void deleteById(int id) {
        // 맵에서 해당 ID 삭제
        WiseSaying removed = wiseSayingMap.remove(id);
        
        if (removed == null) {
            throw new RuntimeException(id + "번 명언은 존재하지 않습니다.");
        }

        // 파일 삭제
        File file = new File(dir, id + ".json");
        if (file.exists()) {
            if (!file.delete()) {
                throw new RuntimeException(id + ".json 파일 삭제에 실패했습니다.");
            }
        }
    }

    public void update(int id, String message, String author) throws IOException {
        WiseSaying wiseSaying = new WiseSaying(id, message, author);
        wiseSayingMap.put(id ,wiseSaying);
        File file = new File(dir, id +".json");
        objectMapper.writeValue(file, wiseSaying);
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
        objectMapper.writeValue(file, wiseSayingMap.values());
    }

    public void saveLastId() throws IOException {
        File file = new File(dir, "lastId.txt");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write(String.valueOf(id));
        bufferedWriter.close();
    }

    private void readLastId(){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(dir, "lastId.txt")));
            id = Integer.parseInt(bufferedReader.readLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
