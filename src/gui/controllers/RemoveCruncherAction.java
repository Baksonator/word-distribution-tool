package gui.controllers;

import cruncher.CounterCruncher;
import gui.CruncherPane;
import gui.SingleCruncherPane;
import gui.SingleFileInputPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.stream.Collectors;

public class RemoveCruncherAction implements EventHandler<ActionEvent> {

    private CruncherPane cruncherPane;
    private SingleCruncherPane singleCruncherPane;

    public RemoveCruncherAction(CruncherPane cruncherPane, SingleCruncherPane singleCruncherPane) {
        this.cruncherPane = cruncherPane;
        this.singleCruncherPane = singleCruncherPane;
    }

    @Override
    public void handle(ActionEvent event) {
        for (SingleFileInputPane singleFileInputPane : cruncherPane.getApp().fileInputs.getFileInputPanes()) {
            CounterCruncher counterCruncher = singleFileInputPane.getFileInputsPane().getApp().crunchers.
                    getSingleCruncherPanes()
                    .stream()
                    .filter(cp -> cp.getName().equals(singleCruncherPane.getName()))
                    .collect(Collectors.toList()).get(0).getCounterCruncher();

            singleFileInputPane.getFileInputComponent().deleteCruncher(counterCruncher);

            singleFileInputPane.getAddedCrunchersList().remove(singleCruncherPane.getName());

            singleFileInputPane.getCrunchersList().remove(singleCruncherPane.getName());
        }

        cruncherPane.getSingleCruncherPanes().remove(singleCruncherPane);
        cruncherPane.getChildren().remove(singleCruncherPane);
    }
}
