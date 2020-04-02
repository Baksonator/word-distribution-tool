package gui.controllers;

import cruncher.CounterCruncher;
import gui.SingleFileInputPane;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;

import java.util.stream.Collectors;

public class LinkCruncher implements EventHandler<ActionEvent> {

    private ComboBox<String> crunchersComboBox;
    private ObservableList<String> addedCrunchersList;
    private SingleFileInputPane singleFileInputPane;

    public LinkCruncher(ComboBox<String> crunchersComboBox, ObservableList<String> addedCrunchersList, SingleFileInputPane singleFileInputPane) {
        this.crunchersComboBox = crunchersComboBox;
        this.addedCrunchersList = addedCrunchersList;
        this.singleFileInputPane = singleFileInputPane;
    }

    @Override
    public void handle(ActionEvent event) {
        String selectedItem = crunchersComboBox.getSelectionModel().getSelectedItem();

        addedCrunchersList.add(selectedItem);

        CounterCruncher counterCruncher = singleFileInputPane.getFileInputsPane().getApp().crunchers.
                getSingleCruncherPanes()
                .stream()
                .filter(cp -> cp.getName().equals(selectedItem))
                .collect(Collectors.toList()).get(0).getCounterCruncher();

        singleFileInputPane.getFileInputComponent().addCruncher(counterCruncher);
        counterCruncher.getInputCompontents().add(singleFileInputPane.getFileInputComponent());
    }
}
