package gui.controllers;

import gui.CruncherPane;
import gui.SingleCruncherPane;
import gui.SingleFileInputPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ConfirmCruncherCreation implements EventHandler<ActionEvent> {

    private CruncherPane cruncherPane;
    private TextField tfArity;
    private Stage stage;

    public ConfirmCruncherCreation(CruncherPane cruncherPane, TextField tfArity, Stage stage) {
        this.cruncherPane = cruncherPane;
        this.tfArity = tfArity;
        this.stage = stage;
    }

    @Override
    public void handle(ActionEvent event) {
        SingleCruncherPane singleCruncherPane = new SingleCruncherPane(Integer.parseInt(tfArity.getText()), cruncherPane, cruncherPane.getApp().cacheOutput);
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
