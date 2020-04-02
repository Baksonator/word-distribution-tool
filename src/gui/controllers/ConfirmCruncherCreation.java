package gui.controllers;

import app.App;
import gui.CruncherPane;
import gui.SingleCruncherPane;
import gui.SingleFileInputPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public class ConfirmCruncherCreation implements EventHandler<ActionEvent> {

    private CruncherPane cruncherPane;
    private int arity;
    private Stage stage;

    public ConfirmCruncherCreation(CruncherPane cruncherPane, int arity, Stage stage) {
        this.cruncherPane = cruncherPane;
        this.arity = arity;
        this.stage = stage;
    }

    @Override
    public void handle(ActionEvent event) {
        SingleCruncherPane singleCruncherPane = new SingleCruncherPane(arity, cruncherPane, cruncherPane.getApp().cacheOutput);
        cruncherPane.getChildren().add(singleCruncherPane);
        cruncherPane.getSingleCruncherPanes().add(singleCruncherPane);
        for (SingleFileInputPane singleFileInputPane : cruncherPane.getApp().fileInputs.getFileInputPanes()) {
            singleFileInputPane.getCrunchersList().add(singleCruncherPane.getName());
            if (singleFileInputPane.getCrunchersList().size() == 1) {
                singleFileInputPane.getCrunchersComboBox().getSelectionModel().select(0);
            }
        }
        stage.close();
    }
}
