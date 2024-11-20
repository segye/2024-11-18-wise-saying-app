package controller;

import entity.WiseSaying;
import service.WiseSayingService;

import java.io.IOException;
import java.util.Scanner;

public class WiseSayingController {
    private static final WiseSayingService service = new WiseSayingService();
    private final Scanner scanner = new Scanner(System.in);
    public void run() throws IOException {
        System.out.println("== 명언 앱 ==");
        while (true) {
            System.out.print("명령) ");
            String command = scanner.nextLine();

            if (command.equals("등록")) {
                System.out.print("명언 : ");
                String message = scanner.nextLine();
                System.out.print("작가 : ");
                String author = scanner.nextLine();
                int id = service.add(message, author);
                System.out.println(id + "번 명언이 등록되었습니다.");
            } else if (command.equals("목록")) {
                System.out.println("번호 / 작가 / 명언");
                System.out.println("----------------------");
                for (WiseSaying wiseSaying : service.findAll()) {
                    System.out.println(wiseSaying.getId() + " / " + wiseSaying.getAuthor() + " / " + wiseSaying.getMessage());
                }
            } else if (command.startsWith("삭제?id=")) {
                int id = Integer.parseInt(command.split("=")[1]);
                boolean result = service.delete(id);
                if (result) {
                    System.out.println(id + "번 명언이 삭제되었습니다.");
                } else {
                    System.out.println(id + "번 명언은 존재하지 않습니다.");
                }
            } else if (command.startsWith("수정?id=")) {
                int id = Integer.parseInt(command.split("=")[1]);
                handleUpdate(id);
            } else if (command.equals("빌드")) {
                service.build();
                System.out.println("data.json 파일의 내용이 갱신되었습니다.");
            } else if (command.equals("종료")) {
                service.build();
                scanner.close();
                break;
            }
        }
    }

    private void handleUpdate(int id) {
        WiseSaying wiseSaying = service.findById(id);
        if (wiseSaying == null) {
            System.out.println(id + "번 명언은 존재하지 않습니다.");
            return;
        }

        System.out.println("명언(기존) : " + wiseSaying.getMessage());
        System.out.print("명언 : ");
        String updateMessage = scanner.nextLine();

        System.out.println("작가(기존) : " + wiseSaying.getAuthor());
        System.out.print("작가 : ");
        String updateAuthor = scanner.nextLine();

        try {
            boolean result = service.update(id, updateMessage, updateAuthor);
            if (!result) {
                System.out.println(id + "번 명언은 존재하지 않습니다.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
