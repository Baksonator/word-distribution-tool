package gui.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;

public class CruncherComboBoxChange implements ChangeListener<String> {

    private Button linkCruncherBtn;
    private ObservableList<String> addedCrunchersList;

    public CruncherComboBoxChange(Button linkCruncherBtn, ObservableList<String> addedCrunchersList) {
        this.linkCruncherBtn = linkCruncherBtn;
        this.addedCrunchersList = addedCrunchersList;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (addedCrunchersList.contains(newValue)) {
            linkCruncherBtn.setDisable(true);
        } else {
            if (newValue != null) {
                linkCruncherBtn.setDisable(false);
            }
        }
    }
}
