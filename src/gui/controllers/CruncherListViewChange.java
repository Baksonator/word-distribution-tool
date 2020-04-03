package gui.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;

public class CruncherListViewChange implements ChangeListener<String> {

    private Button unlinkCruncherBtn;

    public CruncherListViewChange(Button unlinkCruncherBtn) {
        this.unlinkCruncherBtn = unlinkCruncherBtn;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (newValue != null) {
            unlinkCruncherBtn.setDisable(false);
        } else {
            unlinkCruncherBtn.setDisable(true);
        }
    }
}
