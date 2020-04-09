package gui;

import app.App;
import gui.controllers.AddCruncherAction;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class CruncherPane extends VBox {

    private List<SingleCruncherPane> singleCruncherPanes;
    private App app;
    private int counter;

    public CruncherPane(App app) {
        this.app = app;
        singleCruncherPanes = new ArrayList<>();
        counter = 0;

        initScene();
    }

    private void initScene() {
        Label headerLabel = new Label("Crunchers");

        Button addCruncherBtn = new Button("Add cruncher");
        addCruncherBtn.setOnAction(new AddCruncherAction(this));

        getChildren().add(headerLabel);
        getChildren().add(addCruncherBtn);
    }

    public List<SingleCruncherPane> getSingleCruncherPanes() {
        return singleCruncherPanes;
    }

    public int getCounter() {
        return counter++;
    }

    public App getApp() {
        return app;
    }
}
