package output;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class Unifier implements Callable<Map<String, Long>> {

    private List<String> resultsToSum;
    private CacheOutput cacheOutput;
    private VBox vBox;
    private ProgressBar progressBar;
    private String name;
    private ObservableList<String> resultsList;
    private Label barLabel;
    static int progress = 0;

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
        Map<String, Long> finalResult = new HashMap<>();

        List<Map<String, Long>> results = new ArrayList<>();

        for (String resultName : resultsToSum) {
            Map<String, Long> mapResult = cacheOutput.take(resultName);
            results.add(mapResult);

        }

        int jobSize = results.size();
//        int progress = 0;

        for (Map<String, Long> result : results) {
            for (Map.Entry<String, Long> entry : result.entrySet()) {
                finalResult.merge(entry.getKey(), entry.getValue(), Long::sum);
            }
            progress++;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress((double)progress / (double)jobSize);
//                    progressBar.progressProperty().set(progress);
                }
            });
//            updateProgress(progress, jobSize);

        }


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBox.getChildren().remove(progressBar);
                vBox.getChildren().remove(barLabel);
                resultsList.set(resultsList.indexOf(name + "*"), name);
            }
        });

        return finalResult;
    }
}
