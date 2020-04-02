package gui.controllers;

import input.FileInput;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class StartPauseAction implements EventHandler<ActionEvent> {

    private FileInput fileInput;
    private Button startBtn;

    public StartPauseAction(FileInput fileInput, Button startBtn) {
        this.fileInput = fileInput;
        this.startBtn = startBtn;
    }

    @Override
    public void handle(ActionEvent event) {
        if (fileInput.getPaused().get()) {
            synchronized (fileInput.getPauseSleepLock()) {
                if (fileInput.getPaused().compareAndSet(true, false)) {
                    fileInput.getPauseSleepLock().notify();
                }
            }
            startBtn.setText("Pause");
        } else {
            synchronized (fileInput.getPauseSleepLock()) {
                if (fileInput.getPaused().compareAndSet(false, true)) {
                    fileInput.getPauseSleepLock().notify();
                }
            }
            startBtn.setText("Start");
        }
    }
}
