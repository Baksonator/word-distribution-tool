package gui.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class OutputResultsListViewChange implements EventHandler<MouseEvent> {

    private ListView<String> resultsListView;
    private Button singleResultBtn;
    private Button sumResultBtn;

    public OutputResultsListViewChange(ListView<String> resultsListView, Button singleResultBtn, Button sumResultBtn) {
        this.resultsListView = resultsListView;
        this.singleResultBtn = singleResultBtn;
        this.sumResultBtn = sumResultBtn;
    }

    @Override
    public void handle(MouseEvent event) {
        if (resultsListView.getSelectionModel().getSelectedItems().size() > 0) {
            if (resultsListView.getSelectionModel().getSelectedItems().size() == 1) {
                singleResultBtn.setDisable(false);
                sumResultBtn.setDisable(true);
            } else {
                sumResultBtn.setDisable(false);
                singleResultBtn.setDisable(true);
            }
        } else {
            singleResultBtn.setDisable(true);
            sumResultBtn.setDisable(true);
        }
    }
}
