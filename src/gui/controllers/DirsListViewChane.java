package gui.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;

public class DirsListViewChane implements ChangeListener<String> {

    private Button removeDirBtn;

    public DirsListViewChane(Button removeDirBtn) {
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
