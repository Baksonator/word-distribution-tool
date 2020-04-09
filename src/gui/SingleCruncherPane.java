package gui;

import app.App;
import cruncher.CounterCruncher;
import gui.controllers.RemoveCruncherAction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import output.OutputComponent;

public class SingleCruncherPane extends VBox {

    private CruncherPane cruncherPane;
    private CounterCruncher counterCruncher;
    private int arity;
    private String name;
    private ObservableList<String> activeFiles;

    public SingleCruncherPane(int arity, CruncherPane cruncherPane, OutputComponent outputComponent) {
        activeFiles = FXCollections.observableArrayList();
        counterCruncher = new CounterCruncher(arity, Integer.parseInt(App.prop.getProperty("counter_data_limit")), activeFiles);
        counterCruncher.getOutputComponents().add(outputComponent);
        this.arity = arity;
        this.cruncherPane = cruncherPane;

        initScene();
        startComponent();
    }

    private void startComponent() {
        Thread thread = new Thread(counterCruncher);
        thread.start();
    }

    private void initScene() {
        int counter = cruncherPane.getCounter();
        Label cruncherName = new Label("Name: Counter " + counter);
        this.name = "Counter " + counter;

        Label cruncherArity = new Label("Arity: " + arity);

        Button removeCruncherButton = new Button("Remove cruncher");
        removeCruncherButton.setOnAction(new RemoveCruncherAction(cruncherPane, this));

        Label crunchingLabel = new Label("Crunching:");


        ListView<String> activeListView = new ListView<>(activeFiles);
        activeListView.setMaxWidth(100);

        getChildren().add(cruncherName);
        getChildren().add(cruncherArity);
        getChildren().add(removeCruncherButton);
        getChildren().add(crunchingLabel);
        getChildren().add(activeListView);
    }

    public String getName() {
        return name;
    }

    public CounterCruncher getCounterCruncher() {
        return counterCruncher;
    }
}
