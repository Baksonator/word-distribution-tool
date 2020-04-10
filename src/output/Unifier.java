package output;

import app.App;
import gui.SingleCruncherPane;
import gui.SingleFileInputPane;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class Unifier implements Callable<Map<String, Long>> {

    private List<String> resultsToSum;
    private CacheOutput cacheOutput;
    private VBox vBox;
    private ProgressBar progressBar;
    private String name;
    private ObservableList<String> resultsList;
    private Label barLabel;
    private static int progress = 0;

    public Unifier(List<String> resultsToSum, CacheOutput cacheOutput, VBox vBox, ProgressBar progressBar,
                   String name, ObservableList<String> resultsList, Label barLabel) {
        this.resultsToSum = resultsToSum;
        this.cacheOutput = cacheOutput;
        this.vBox = vBox;
        this.progressBar = progressBar;
        this.name = name;
        this.resultsList = resultsList;
        this.barLabel = barLabel;
    }

    @Override
    public Map<String, Long> call() throws Exception {
        try {
            Map<String, Long> finalResult = new HashMap<>();

            List<Map<String, Long>> results = new ArrayList<>();

            try {
                for (String resultName : resultsToSum) {
                    Map<String, Long> mapResult = cacheOutput.take(resultName);
                    results.add(mapResult);

                }
            } catch (InterruptedException e) {
                return null;
            }

            int jobSize = results.size();

            for (Map<String, Long> result : results) {
                for (Map.Entry<String, Long> entry : result.entrySet()) {
                    finalResult.merge(entry.getKey(), entry.getValue(), Long::sum);
                }

                progress++;
                Platform.runLater(() -> progressBar.setProgress((double) progress / (double) jobSize));
            }


            Platform.runLater(() -> {
                vBox.getChildren().remove(progressBar);
                vBox.getChildren().remove(barLabel);
                resultsList.set(resultsList.indexOf(name + "*"), name);
            });

            return finalResult;
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

            for (SingleCruncherPane singleCruncherPane : App.cruncherPane1.getSingleCruncherPanes()) {
                singleCruncherPane.getCounterCruncher().getMyThreadPool().shutdownNow();
            }

            for (SingleFileInputPane singleFileInputPane : App.fileInputsPane1.getFileInputPanes()) {
                singleFileInputPane.getFileInputComponent().interrupt();
                singleFileInputPane.getFileInputComponent().getWorkAssignerThread().interrupt();
            }

            App.outputPane1.getCacheOutput().getSortThreadPool().shutdownNow();

            stage.setScene(new Scene(vBox, 300, 300));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        });
    }

}
