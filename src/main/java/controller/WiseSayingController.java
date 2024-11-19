package controller;

import service.WiseSayingService;

import java.io.IOException;
import java.util.Scanner;

public class WiseSayingController {
    private static final WiseSayingService service = new WiseSayingService();


    public void run() throws IOException {
        System.out.println("== 명언 앱 ==");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("명령) ");
            String command = scanner.nextLine();

            if (command.equals("등록")) {
                service.add(scanner);
            } else if (command.equals("목록")) {
                service.findAll();
            } else if (command.startsWith("삭제?id=")) {
                service.delete(command);
            } else if (command.startsWith("수정?id=")) {
                service.update(scanner, command);
            } else if (command.equals("빌드")) {
                service.build();
            } else if (command.equals("종료")) {
                service.build();
                scanner.close();
                break;
            }
        }
    }
}
