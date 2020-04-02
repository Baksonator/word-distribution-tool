package gui.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;

import java.util.List;

public class FileInputComboBoxChange implements ChangeListener<String> {

    private List<String> addedDisks;
    private Button addFileInputBtn;

    public FileInputComboBoxChange(List<String> addedDisks, Button addFileInputBtn) {
        this.addedDisks = addedDisks;
        this.addFileInputBtn = addFileInputBtn;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (addedDisks.contains(newValue)) {
            addFileInputBtn.setDisable(true);
        } else {
            addFileInputBtn.setDisable(false);
        }
    }
}
