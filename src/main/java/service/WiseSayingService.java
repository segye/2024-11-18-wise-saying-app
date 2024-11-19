package service;

import entity.WiseSaying;
import repository.WiseSayingRepository;

import java.io.IOException;
import java.util.Scanner;

public class WiseSayingService {
    private static final WiseSayingRepository repository = new WiseSayingRepository();

    public void add(Scanner scanner) {
        try{
            System.out.print("명언 : ");
            String message = scanner.nextLine();

            System.out.print("작가 : ");
            String author = scanner.nextLine();

            int id = repository.add(message, author);

            repository.saveLastId();
            System.out.println(id + "번 명언이 등록되었습니다.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void findAll() {
        System.out.println("번호 / 작가 / 명언");
        System.out.println("----------------------");
        for (WiseSaying wiseSaying : repository.findAll()) {
            System.out.println(wiseSaying.getId() + " / " + wiseSaying.getAuthor() + " / " + wiseSaying.getMessage());
        }
    }

    public void delete(String command) {
        int id = Integer.parseInt(command.split("=")[1]);

        try {
            repository.deleteById(id);
            System.out.println(id + "번 명언이 삭제되었습니다.");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage()); // 명언이 존재하지 않는 경우 예외 메시지 출력
        }
    }

    public void update(Scanner scanner, String command) throws IOException {
        int id = Integer.parseInt(command.split("=")[1]);

        WiseSaying wiseSaying = repository.findById(id);
        System.out.println("명언(기존) : " + wiseSaying.getMessage());
        System.out.print("명언 : ");
        String updateMessage = scanner.nextLine();

        System.out.println("작가(기존) : " + wiseSaying.getAuthor());
        System.out.print("작가 : ");
        String updateAuthor = scanner.nextLine();

        repository.update(id, updateMessage, updateAuthor);
    }

    public void build() throws IOException {
        repository.fileBuild();
        System.out.println("data.json 파일의 내용이 갱신되었습니다.");
    }

}
