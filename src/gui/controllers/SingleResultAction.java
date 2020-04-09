package gui.controllers;

import gui.SorterTask;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import output.CacheOutput;

import java.util.Map;

public class SingleResultAction implements EventHandler<ActionEvent> {

    private ListView<String> resultsListView;
    private CacheOutput cacheOutput;
    private LineChart<Number, Number> resultChart;
    private VBox vBox;

    public SingleResultAction(ListView<String> resultsListView, CacheOutput cacheOutput, LineChart<Number, Number> resultChart, VBox vBox) {
        this.resultsListView = resultsListView;
        this.cacheOutput = cacheOutput;
        this.resultChart = resultChart;
        this.vBox = vBox;
    }

    @Override
    public void handle(ActionEvent event) {
        String selectedResult = resultsListView.getSelectionModel().getSelectedItem();

        if (!selectedResult.endsWith("*")) {
            String nameToShow = selectedResult;
            for (String name : cacheOutput.getResults().keySet()) {
                if (name.endsWith(selectedResult)) {
                    selectedResult = name;
                    break;
                }
            }

            Map<String, Long> unsortedResult = cacheOutput.poll(selectedResult);


            if (unsortedResult != null) {

                ProgressBar progressBar = new ProgressBar();
                Label barLabel = new Label(nameToShow);
                vBox.getChildren().add(progressBar);
                vBox.getChildren().add(barLabel);

                cacheOutput.getSortThreadPool().submit(new SorterTask(cacheOutput.getSortProgressLimit(), unsortedResult,
                        resultChart, progressBar, barLabel, vBox));

            } else {
                notReady();
            }

        } else {
            notReady();
        }
    }

    private void notReady() {
        Stage stage = new Stage();
        stage.setTitle("Result not ready");

        VBox vBox = new VBox();

        Button okBtn = new Button("OK");
        okBtn.setOnAction(event -> stage.close());
        vBox.getChildren().add(okBtn);

        stage.setScene(new Scene(vBox, 300, 300));
        stage.show();
    }
}
