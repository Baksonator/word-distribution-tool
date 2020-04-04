package gui.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import output.CacheOutput;
import output.Unifier;

import java.util.ArrayList;
import java.util.List;

public class SumResultConfirmation implements EventHandler<ActionEvent> {

    private VBox vBox;
    private CacheOutput cacheOutput;
    private ListView<String> resultsListView;
    private TextField tfName;
    private ObservableList<String> resultsList;
    private Stage stage;

    public SumResultConfirmation(VBox vBox, CacheOutput cacheOutput, ListView<String> resultsListView, TextField tfName,
                                 ObservableList<String> resultsList, Stage stage) {
        this.vBox = vBox;
        this.cacheOutput = cacheOutput;
        this.resultsListView = resultsListView;
        this.tfName = tfName;
        this.resultsList = resultsList;
        this.stage = stage;
    }

    @Override
    public void handle(ActionEvent event) {
        String text = tfName.getText();

        if (resultsList.contains(text) || resultsList.contains(text + "*")) {
            resultExists();
            return;
        }

        ProgressBar progressBar = new ProgressBar();
        Label barLabel = new Label(text);
        vBox.getChildren().add(progressBar);
        vBox.getChildren().add(barLabel);

        resultsList.add(text + "*");

        List<String> resultsToSum = new ArrayList<>();
        for (String string : resultsListView.getSelectionModel().getSelectedItems()) {
            for (String name : cacheOutput.getResults().keySet()) {
                if (string.endsWith("*")) {
                    if (string.substring(0, string.length() - 1).equals(name.split("/")[name.split("/").length - 1])) {
                        resultsToSum.add(name);
                    }
                } else {
                    if (string.equals(name.split("/")[name.split("/").length - 1])) {
                        resultsToSum.add(name);
                    }
                }
            }
        }

        Unifier unifier = new Unifier(resultsToSum, cacheOutput, vBox, progressBar, text, resultsList, barLabel);
//        progressBar.progressProperty().bind(unifier.progressProperty());

        stage.close();

        cacheOutput.union(text, unifier);
    }

    private void resultExists() {
        Stage stage = new Stage();
        stage.setTitle("Result with that name exists");

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
