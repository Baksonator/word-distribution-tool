package gui.controllers;

import app.App;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
            for (String name : cacheOutput.getResults().keySet()) {
                if (name.endsWith(selectedResult)) {
                    selectedResult = name;
                    break;
                }
            }

            Map<String, Long> unsortedResult = cacheOutput.poll(selectedResult);

//            for (Map.Entry<String, Future<Map<String, Long>>> entry : cacheOutput.getResults().entrySet()) {
//                try {
//                    System.out.println(entry.getKey());
//                    System.out.println(entry.getValue());
//                    System.out.println(entry.getValue().get());
//                    System.out.println(entry.getKey() + " : " + entry.getValue().get().size());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//            }

            if (unsortedResult != null) {

                ProgressBar progressBar = new ProgressBar();
                Label barLabel = new Label(selectedResult);
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
        okBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
            }
        });
        vBox.getChildren().add(okBtn);

        stage.setScene(new Scene(vBox, 300, 300));
        stage.show();
    }
}
