package gui;

import app.App;
import gui.controllers.OutputResultsListViewChange;
import gui.controllers.SingleResultAction;
import gui.controllers.SumResultAction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import output.CacheOutput;

public class OutputPane extends HBox {

    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis();
    private ObservableList<String> resultsList;
    private CacheOutput cacheOutput;

    public OutputPane(App app) {
        cacheOutput = new CacheOutput(Integer.parseInt(App.prop.getProperty("sort_progress_limit")), null);
        app.cacheOutput = cacheOutput;

        initScene();
        cacheOutput.setResultObservableList(resultsList);

        startComponent();
    }

    private void startComponent() {
        Thread cachecOutputThread = new Thread(cacheOutput);
        cachecOutputThread.start();
    }

    private void initScene() {
        LineChart<Number, Number> resultChart = new LineChart<>(xAxis, yAxis);
        resultChart.setPrefWidth(1000);

        VBox vbox = new VBox();

        resultsList = FXCollections.observableArrayList();
        ListView<String> resultsListView = new ListView<>(resultsList);
        resultsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button singleResultBtn = new Button("Single result");
        singleResultBtn.setDisable(true);
        singleResultBtn.setOnAction(new SingleResultAction(resultsListView, cacheOutput, resultChart, vbox));

        Button sumResultBtn = new Button("Sum result");
        sumResultBtn.setDisable(true);
        sumResultBtn.setOnAction(new SumResultAction(vbox, cacheOutput, resultsListView, resultsList));

        resultsListView.setOnMouseClicked(new OutputResultsListViewChange(resultsListView,
                singleResultBtn, sumResultBtn));

        vbox.getChildren().add(resultsListView);
        vbox.getChildren().add(singleResultBtn);
        vbox.getChildren().add(sumResultBtn);

        getChildren().add(resultChart);
        getChildren().add(vbox);
    }

    public CacheOutput getCacheOutput() {
        return cacheOutput;
    }
}
