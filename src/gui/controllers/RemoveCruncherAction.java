package gui.controllers;

import cruncher.CounterCruncher;
import gui.CruncherPane;
import gui.SingleCruncherPane;
import gui.SingleFileInputPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class RemoveCruncherAction implements EventHandler<ActionEvent> {

    private CruncherPane cruncherPane;
    private SingleCruncherPane singleCruncherPane;

    public RemoveCruncherAction(CruncherPane cruncherPane, SingleCruncherPane singleCruncherPane) {
        this.cruncherPane = cruncherPane;
        this.singleCruncherPane = singleCruncherPane;
    }

    @Override
    public void handle(ActionEvent event) {
        CounterCruncher counterCruncher = singleCruncherPane.getCounterCruncher();

        for (SingleFileInputPane singleFileInputPane : cruncherPane.getApp().fileInputs.getFileInputPanes()) {
            singleFileInputPane.getFileInputComponent().deleteCruncher(counterCruncher);

            singleFileInputPane.getAddedCrunchersList().remove(singleCruncherPane.getName());

            singleFileInputPane.getCrunchersList().remove(singleCruncherPane.getName());
        }

        cruncherPane.getSingleCruncherPanes().remove(singleCruncherPane);
        cruncherPane.getChildren().remove(singleCruncherPane);
    }
}
