import controller.WiseSayingController;

import java.io.IOException;
public class Main {
    private static final WiseSayingController controller = new WiseSayingController();
    public static void main(String[] args) throws IOException {
        controller.run();
    }

}
