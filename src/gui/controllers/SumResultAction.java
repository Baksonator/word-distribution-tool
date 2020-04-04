package gui.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import output.CacheOutput;

public class SumResultAction implements EventHandler<ActionEvent> {

    private VBox vBox;
    private CacheOutput cacheOutput;
    private ListView<String> resultsListView;
    private ObservableList<String> resultsList;

    public SumResultAction(VBox vBox, CacheOutput cacheOutput, ListView<String> resultsListView, ObservableList<String> resultsList) {
        this.vBox = vBox;
        this.cacheOutput = cacheOutput;
        this.resultsListView = resultsListView;
        this.resultsList = resultsList;
    }

    @Override
    public void handle(ActionEvent event) {
        Stage stage = new Stage();
        stage.setTitle("Enter arity");

        VBox vBoxLocal = new VBox();

        TextField tfName = new TextField("sum");

        HBox hBox = new HBox();
        Button okBtn = new Button("OK");
        okBtn.setOnAction(new SumResultConfirmation(vBox, cacheOutput, resultsListView, tfName, resultsList, stage));
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
            }
        });
        hBox.getChildren().add(okBtn);
        hBox.getChildren().add(cancelBtn);

        vBoxLocal.getChildren().add(tfName);
        vBoxLocal.getChildren().add(hBox);

        stage.setScene(new Scene(vBoxLocal, 300, 300));
        stage.show();
    }
}
