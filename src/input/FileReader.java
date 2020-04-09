package input;

import app.App;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public class FileReader implements Callable<String> {

    private String filePath;

    public FileReader(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String call() throws Exception {
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            String readFile = new String(data, StandardCharsets.US_ASCII);
            return readFile;
        } catch (OutOfMemoryError e) {
            stopApp();
        }
        return null;
    }

    private void stopApp() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("Out of memory - Shutting down");

            VBox vBox = new VBox();

            Button okBtn = new Button("OK");
            okBtn.setOnAction(event -> {
                Platform.exit();
                System.exit(0);
            });
            vBox.getChildren().add(okBtn);

            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });

            App.inputThreadPool.shutdownNow();
            App.outputThreadPool.shutdownNow();
            App.cruncherThreadPool.shutdownNow();

            stage.setScene(new Scene(vBox, 300, 300));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        });
    }

}
