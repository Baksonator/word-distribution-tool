package gui.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
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
        ProgressBar progressBar = new ProgressBar();
        vBox.getChildren().add(progressBar);

        String text = tfName.getText();

        resultsList.add(text + "*");

        List<String> resultsToSum = new ArrayList<>();
        for (String string : resultsListView.getSelectionModel().getSelectedItems()) {
            for (String name : cacheOutput.getResults().keySet()) {
                if (string.equals(name.split("/")[name.split("/").length - 1])) {
                    resultsToSum.add(name);
                }
            }
        }

        Unifier unifier = new Unifier(resultsToSum, cacheOutput, vBox, progressBar, text, resultsList);
        progressBar.progressProperty().bind(unifier.progressProperty());

        cacheOutput.union(tfName.getText(), unifier);

        stage.close();
    }
}
