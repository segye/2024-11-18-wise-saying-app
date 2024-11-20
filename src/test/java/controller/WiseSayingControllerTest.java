package controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.TestUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

class WiseSayingControllerTest {

    private ByteArrayOutputStream output;
    private Scanner scanner;
    private WiseSayingController controller;
    private final String dir = "db/wiseSaying";


    @BeforeEach
    public void ready() {
        output = TestUtil.setOutToByteArray();
    }

    @AfterEach
    public void end() {
        TestUtil.clearSetOutToByteArray(output);
        File file = new File(dir);
        if (file.exists()) {
            for (File f : file.listFiles()) {
                f.delete();
            }
        }
    }

    @Test
    @DisplayName("등록 테스트")
    public void addTest() throws IOException {
        String input = "등록\n현재를 사랑하라.\n작자미상\n종료\n";

        scanner = TestUtil.genScanner(input);

        controller = new WiseSayingController(scanner);

        controller.run();

        assertThat(output.toString())
                .contains("명언 :")
                .contains("작가 :")
                .contains("1번 명언이 등록되었습니다.");

        File addedFile = new File(dir + "/1.json");
        assertThat(addedFile.exists()).isTrue();

        String content = new String(Files.readAllBytes(addedFile.toPath()));
        assertThat(content).contains("message\": \"현재를 사랑하라.");
        assertThat(content).contains("author\": \"작자미상");
    }
    @Test
    @DisplayName("목록 테스트")
    public void listTest() throws IOException {
        String input = "등록\n과거에 집착하지 마라.\n작자미상\n등록\n현재를 사랑하라.\n홍길동\n목록\n종료\n";

        // Scanner 객체 생성
        scanner = TestUtil.genScanner(input);

        controller = new WiseSayingController(scanner);

        controller.run();

        String outputString = output.toString();

        // 결과 확인
        assertThat(outputString)
                .startsWith("== 명언 앱 ==\n")
                .contains("번호 / 작가 / 명언")
                .contains("2 / 홍길동 / 현재를 사랑하라.")
                .contains("1 / 작자미상 / 과거에 집착하지 마라.")
                .endsWith("명령) ");
    }

    @Test
    @DisplayName("삭제 테스트")
    public void deleteTest() throws IOException {
        String input = "등록\n과거에 집착하지 마라.\n작자미상\n삭제?id=1\n삭제?id=2\n종료\n";

        scanner = TestUtil.genScanner(input);

        controller = new WiseSayingController(scanner);

        controller.run();

        String outputString = output.toString();

        assertThat(outputString)
                .contains("1번 명언이 삭제되었습니다.")
                .contains("2번 명언은 존재하지 않습니다.");

        File deletedFile = new File(dir + "/1.json");
        assertThat(deletedFile.exists()).isFalse();  // 삭제된 파일이 존재하지 않아야 함
    }

    @Test
    @DisplayName("수정 테스트")
    public void updateTest() throws IOException {
        String input = "등록\n과거에 집착하지 마라.\n작자미상\n수정?id=1\n현재와 자신을 사랑하라.\n홍길동\n수정?id=2\n종료\n";

        scanner = TestUtil.genScanner(input);

        controller = new WiseSayingController(scanner);

        controller.run();

        String outputString = output.toString();

        assertThat(outputString)
                .contains("명언(기존) : 과거에 집착하지 마라.")
                .contains("작가(기존) : 작자미상")
                .contains("2번 명언은 존재하지 않습니다.");
    }

    @Test
    @DisplayName("빌드 테스트")
    public void buildTest() throws IOException {
        String input = "등록\n현재를 사랑하라.\n작자미상\n빌드\n종료\n";

        scanner = TestUtil.genScanner(input);

        controller = new WiseSayingController(scanner);

        controller.run();

        String outputString = output.toString();

        assertThat(outputString)
                .contains("data.json 파일의 내용이 갱신되었습니다.");

        File file = new File("db/wiseSaying/data.json");
        assertThat(file.exists()).isTrue();

        String content = new String(Files.readAllBytes(file.toPath()));
        assertThat(content).contains("[");
        assertThat(content).contains("message\": \"현재를 사랑하라.");
        assertThat(content).contains("author\": \"작자미상");
    }


}