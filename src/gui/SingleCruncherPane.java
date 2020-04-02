package gui;

import app.App;
import cruncher.CounterCruncher;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import output.OutputComponent;

public class SingleCruncherPane extends VBox {

    private Label cruncherName;
    private Label cruncherArity;
    private Button removeCruncherButton;
    private CruncherPane cruncherPane;
    private CounterCruncher counterCruncher;
    private int arity;
    private String name;

    public SingleCruncherPane(int arity, CruncherPane cruncherPane, OutputComponent outputComponent) {
        counterCruncher = new CounterCruncher(arity, Integer.parseInt(App.prop.getProperty("counter_data_limit")));
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
        cruncherName = new Label("Name: Counter " + counter);
        this.name = "Counter " + counter;

        cruncherArity = new Label("Arity: " + arity);

        removeCruncherButton = new Button("Remove cruncher");

        getChildren().add(cruncherName);
        getChildren().add(cruncherArity);
        getChildren().add(removeCruncherButton);
    }

    public String getName() {
        return name;
    }

    public CounterCruncher getCounterCruncher() {
        return counterCruncher;
    }
}
