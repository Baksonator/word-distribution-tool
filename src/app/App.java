package app;

import gui.CruncherPane;
import gui.FileInputsPane;
import gui.OutputPane;
import gui.controllers.ShutdownAction;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import output.CacheOutput;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class App extends Application {

    public static ExecutorService inputThreadPool;
    public static ForkJoinPool cruncherThreadPool;
    public static ExecutorService outputThreadPool;

    public static Properties prop = new Properties();

    public static final String PATH = "C:/Users/Bogdan/IdeaProjects/kids_2020_d1_bogdan_bakarec_rn2016/";

    private HBox mainPane = new HBox();

    public FileInputsPane fileInputs;
    public CruncherPane crunchers;
    public OutputPane output;

    public static Stage primaryStage;

    public CacheOutput cacheOutput;

    public static void main(String[] args) { launch(); }

    @Override
    public void start(Stage primaryStage) {
        try (InputStream input = new FileInputStream("C:\\Users\\Bogdan\\IdeaProjects\\kids_2020_d1_bogdan_bakarec_rn2016\\data\\app.properties")) {
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputThreadPool = Executors.newFixedThreadPool(prop.getProperty("disks").split(";").length);
        cruncherThreadPool = ForkJoinPool.commonPool();
//        cruncherThreadPool = new ForkJoinPool(60);
//        cruncherThreadPool = Executors.newCachedThreadPool();
        outputThreadPool = Executors.newCachedThreadPool();

        //////////////////////////////////////////////////////////////////////

        output = new OutputPane(this);

        fileInputs = new FileInputsPane(this);

        crunchers = new CruncherPane(this);

        mainPane.getChildren().add(fileInputs);
        mainPane.getChildren().add(new Separator(Orientation.VERTICAL));
        mainPane.getChildren().add(crunchers);
        mainPane.getChildren().add(new Separator(Orientation.VERTICAL));
        mainPane.getChildren().add(output);

        Scene mainScene = new Scene(mainPane);

        primaryStage.setOnCloseRequest(new ShutdownAction(this));

        primaryStage.setScene(mainScene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

}
