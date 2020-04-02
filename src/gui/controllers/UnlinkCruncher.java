package gui.controllers;

import cruncher.CounterCruncher;
import gui.SingleFileInputPane;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import java.util.stream.Collectors;

public class UnlinkCruncher implements EventHandler<ActionEvent> {

    private ListView<String> addedCrunchersListView;
    private ObservableList<String> addedCrunchersList;
    private SingleFileInputPane singleFileInputPane;

    public UnlinkCruncher(ListView<String> addedCrunchersListView, ObservableList<String> addedCrunchersList, SingleFileInputPane singleFileInputPane) {
        this.addedCrunchersListView = addedCrunchersListView;
        this.addedCrunchersList = addedCrunchersList;
        this.singleFileInputPane = singleFileInputPane;
    }

    @Override
    public void handle(ActionEvent event) {
        String selectedItem = addedCrunchersListView.getSelectionModel().getSelectedItem();

        addedCrunchersList.remove(selectedItem);

        CounterCruncher counterCruncher = singleFileInputPane.getFileInputsPane().getApp().crunchers.
                getSingleCruncherPanes()
                .stream()
                .filter(cp -> cp.getName().equals(selectedItem))
                .collect(Collectors.toList()).get(0).getCounterCruncher();

        singleFileInputPane.getFileInputComponent().deleteCruncher(counterCruncher);
    }
}
