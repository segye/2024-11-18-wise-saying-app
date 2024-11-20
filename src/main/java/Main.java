import controller.WiseSayingController;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final WiseSayingController controller = new WiseSayingController(new Scanner(System.in));
    public static void main(String[] args) throws IOException {
        controller.run();
    }

}
