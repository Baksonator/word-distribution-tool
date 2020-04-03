package gui.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;

public class DirsListViewChange implements ChangeListener<String> {

    private Button removeDirBtn;

    public DirsListViewChange(Button removeDirBtn) {
        this.removeDirBtn = removeDirBtn;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (newValue != null) {
            removeDirBtn.setDisable(false);
        } else {
            removeDirBtn.setDisable(true);
        }
    }
}
