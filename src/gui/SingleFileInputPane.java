package gui;

import app.App;
import gui.controllers.*;
import input.FileInput;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SingleFileInputPane extends VBox {

    private Label nameLabel;
    private Label crunchersLabel;
    private ObservableList<String> addedCrunchersList;
    private ListView<String> addedCrunchersListView;
    private ObservableList<String> crunchersList;
    private ComboBox<String> crunchersComboBox;
    private Button linkCruncherBtn;
    private Button unlinkCruncherBtn;
    private Label dirsLabel;
    private ObservableList<String> dirsList;
    private ListView<String> dirsListView;
    private Button addDirBtn;
    private Button removeDirBtn;
    private Button startBtn;
    private Button removeDiskInputBtn;
    private Label activeJobLabel;
    private String disk;
    private int componentCount;
    private FileInput fileInputComponent;
    private FileInputsPane fileInputsPane;

    public SingleFileInputPane(FileInputsPane fileInputsPane) {
        this.disk = fileInputsPane.getDiskList().getSelectionModel().getSelectedItem();
        this.componentCount = fileInputsPane.getPaneCounter();
        this.fileInputsPane = fileInputsPane;

        this.fileInputComponent = new FileInput(disk, Integer.parseInt(App.prop.getProperty("file_input_sleep_time")));

        initScene();
        startComponent();
    }

    private void startComponent() {
        Thread inputComponentThread = new Thread(fileInputComponent);
        inputComponentThread.start();
    }

    private void initScene() {
        nameLabel = new Label("File Input " + componentCount + ": " + disk);

        crunchersLabel = new Label("Crunchers");

        addedCrunchersList = FXCollections.observableArrayList();
        addedCrunchersListView = new ListView<>(addedCrunchersList);

        crunchersList = FXCollections.observableArrayList();
        for (SingleCruncherPane singleCruncherPane : fileInputsPane.getApp().crunchers.getSingleCruncherPanes()) {
            crunchersList.add(singleCruncherPane.getName());
        }
        crunchersComboBox = new ComboBox<>(crunchersList);
        if (crunchersList.size() > 0) {
            crunchersComboBox.getSelectionModel().select(0);
        }

        HBox linkingHbox = new HBox();
        linkCruncherBtn = new Button("Link cruncher");
        linkCruncherBtn.setDisable(true);
        linkCruncherBtn.setOnAction(new LinkCruncher(crunchersComboBox, addedCrunchersList, this, linkCruncherBtn));
        unlinkCruncherBtn = new Button("Unlink cruncher");
        unlinkCruncherBtn.setDisable(true);
        unlinkCruncherBtn.setOnAction(new UnlinkCruncher(addedCrunchersListView, addedCrunchersList, this,
                linkCruncherBtn, crunchersComboBox));
        linkingHbox.getChildren().add(linkCruncherBtn);
        linkingHbox.getChildren().add(unlinkCruncherBtn);
        if (crunchersList.size() > 0) {
            linkCruncherBtn.setDisable(false);
        }

        crunchersComboBox.valueProperty().addListener(new CruncherComboBoxChange(linkCruncherBtn, addedCrunchersList));
        addedCrunchersListView.getSelectionModel().selectedItemProperty().addListener(new CruncherListViewChange(unlinkCruncherBtn));

        dirsLabel = new Label("Dirs:");

        dirsList = FXCollections.observableArrayList();
        dirsListView = new ListView<>(dirsList);

        HBox dirsHbox = new HBox();
        addDirBtn = new Button("Add dir");
        addDirBtn.setOnAction(new AddDirAction(fileInputComponent, dirsList, disk));
        removeDirBtn = new Button("Remove dir");
        removeDirBtn.setDisable(true);
        removeDirBtn.setOnAction(new RemoveDirAction(fileInputComponent, dirsList, dirsListView, removeDirBtn, disk));
        dirsHbox.getChildren().add(addDirBtn);
        dirsHbox.getChildren().add(removeDirBtn);

        dirsListView.getSelectionModel().selectedItemProperty().addListener(new DirsListViewChange(removeDirBtn));

        HBox lastHbox = new HBox();
        startBtn = new Button("Start");
        startBtn.setOnAction(new StartPauseAction(fileInputComponent, startBtn));
        removeDiskInputBtn = new Button("Remove disk input");
        removeDiskInputBtn.setOnAction(new RemoveFileInputAction(fileInputsPane, this));
        lastHbox.getChildren().add(startBtn);
        lastHbox.getChildren().add(removeDiskInputBtn);

        activeJobLabel = new Label("Idle");
        activeJobLabel.textProperty().bind(fileInputComponent.getWorkAssigner().messageProperty());

        getChildren().add(nameLabel);
        getChildren().add(crunchersLabel);
        getChildren().add(addedCrunchersListView);
        getChildren().add(crunchersComboBox);
        getChildren().add(linkingHbox);
        getChildren().add(dirsLabel);
        getChildren().add(dirsListView);
        getChildren().add(dirsHbox);
        getChildren().add(lastHbox);
        getChildren().add(activeJobLabel);
    }

    public String getDisk() {
        return disk;
    }

    public ObservableList<String> getCrunchersList() {
        return crunchersList;
    }

    public ComboBox<String> getCrunchersComboBox() {
        return crunchersComboBox;
    }

    public FileInputsPane getFileInputsPane() {
        return fileInputsPane;
    }

    public FileInput getFileInputComponent() {
        return fileInputComponent;
    }

    public ObservableList<String> getAddedCrunchersList() {
        return addedCrunchersList;
    }
}
