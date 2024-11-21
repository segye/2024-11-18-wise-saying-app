package controller;

import entity.WiseSaying;
import service.WiseSayingService;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class WiseSayingController {
    private static final WiseSayingService service = new WiseSayingService();
    private final Scanner scanner;

    public WiseSayingController(Scanner scanner) {
        this.scanner = scanner;
    }
    public void run() throws IOException {
        System.out.println("== 명언 앱 ==");
        while (true) {
            System.out.print("명령) ");
            String command = scanner.nextLine();
            if (command.equals("등록")) {
                add();
            } else if (command.startsWith("목록")) {
                list(command);
            }
            else if (command.startsWith("삭제?id=")) {
                delete(command);
            } else if (command.startsWith("수정?id=")) {
                update(command);
            } else if (command.equals("빌드")) {
                build();
            } else if (command.equals("종료")) {
                end();
                break;
            }
        }
        scanner.close();
    }


    private void add() {
        System.out.print("명언 : ");
        String message = scanner.nextLine();
        System.out.print("작가 : ");
        String author = scanner.nextLine();
        int id = service.add(message, author);
        System.out.println(id + "번 명언이 등록되었습니다.");
    }

    private void list(String command) {
        int page = 1;
        int size = 5;
        String type = null;
        String keyword = null;

        if (command.contains("?")) {
            String[] strings = command.split("\\?");
            String param = strings[1];
            String[] params = param.split("&");

            for (String s : params) {
                String[] values = s.split("=");
                if (values[0].equals("page")) {
                    page = Integer.parseInt(values[1]);
                } else if (values[0].equals("keywordType")) {
                    type = values[1];
                } else if (values[0].equals("keyword")) {
                    keyword = values[1];
                }
            }

            List<WiseSaying> result = null;
            if (type != null && keyword != null) {
                if (type.equals("content")) {
                    result = service.findByMessage(keyword, page, size);
                } else if (type.equals("author")) {
                    result = service.findByAuthor(keyword, page, size);
                }
            } else {
                result = service.findAll(page, size);
            }
            int start = (page - 1) * size;
            int total = service.getCount();
            int totalPage = (int) Math.ceil((double) total / size);

            System.out.println("번호 / 작가 / 명언");
            System.out.println("----------------------");
            for (WiseSaying wiseSaying : result) {
                System.out.println(wiseSaying.getId() + " / " + wiseSaying.getAuthor() + " / " + wiseSaying.getMessage());
            }
            System.out.println("----------------------");
            System.out.print("페이지 : ");
            for (int i = 1; i <= totalPage; i++) {
                if (i == page) {
                    System.out.print("[" + i + "]");
                } else {
                    System.out.print(i);
                }
                if (i != totalPage) {
                    System.out.print(" / ");
                }
            }
            System.out.println();
        } else {
            List<WiseSaying> list = service.findAll(page, size);

            int total = service.getCount();
            int totalPage = (int) Math.ceil((double) total / size);

            System.out.println("번호 / 작가 / 명언");
            System.out.println("----------------------");
            for (WiseSaying wiseSaying : list) {
                System.out.println(wiseSaying.getId() + " / " + wiseSaying.getAuthor() + " / " + wiseSaying.getMessage());
            }
            System.out.println("----------------------");
            System.out.print("페이지 : ");
            for (int i = 1; i <= totalPage; i++) {
                if (i == page) {
                    System.out.print("[" + i + "]");
                } else {
                    System.out.print(i);
                }
                if (i != totalPage) {
                    System.out.print(" / ");
                }
            }
        }
        System.out.println();
    }

    private void delete(String command) {
        int id = Integer.parseInt(command.split("=")[1]);
        boolean result = service.delete(id);
        if (result) {
            System.out.println(id + "번 명언이 삭제되었습니다.");
        } else {
            System.out.println(id + "번 명언은 존재하지 않습니다.");
        }
    }

    private void update(String command) {
        int id = Integer.parseInt(command.split("=")[1]);
        handleUpdate(id);
    }
    private void build() {
        try {
            service.build();
            System.out.println("data.json 파일의 내용이 갱신되었습니다.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void end() {
        try {
            service.build();
            scanner.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
