package gui.controllers;

import cruncher.CounterCruncher;
import gui.SingleFileInputPane;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

public class UnlinkCruncher implements EventHandler<ActionEvent> {

    private ListView<String> addedCrunchersListView;
    private ObservableList<String> addedCrunchersList;
    private SingleFileInputPane singleFileInputPane;
    private Button linkCruncherBtn;
    private ComboBox<String> crunchersComboBox;

    public UnlinkCruncher(ListView<String> addedCrunchersListView, ObservableList<String> addedCrunchersList,
                          SingleFileInputPane singleFileInputPane, Button linkCruncherBtn, ComboBox<String> crunchersComboBox) {
        this.addedCrunchersListView = addedCrunchersListView;
        this.addedCrunchersList = addedCrunchersList;
        this.singleFileInputPane = singleFileInputPane;
        this.linkCruncherBtn = linkCruncherBtn;
        this.crunchersComboBox = crunchersComboBox;
    }

    @Override
    public void handle(ActionEvent event) {
        String selectedItem = addedCrunchersListView.getSelectionModel().getSelectedItem();

        addedCrunchersList.remove(selectedItem);

        CounterCruncher counterCruncher = singleFileInputPane.getFileInputsPane().getApp().crunchers.
                getSingleCruncherPanes()
                .stream()
                .filter(cp -> cp.getName().equals(selectedItem))
                .findFirst().get().getCounterCruncher();

        singleFileInputPane.getFileInputComponent().deleteCruncher(counterCruncher);

        if (selectedItem.equals(crunchersComboBox.getSelectionModel().getSelectedItem())) {
            linkCruncherBtn.setDisable(false);
        }
    }
}
