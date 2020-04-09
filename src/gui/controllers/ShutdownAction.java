package gui.controllers;

import app.App;
import gui.SingleCruncherPane;
import gui.SingleFileInputPane;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.concurrent.TimeUnit;

public class ShutdownAction implements EventHandler<WindowEvent> {

    private App app;

    public ShutdownAction(App app) {
        this.app = app;
    }

    @Override
    public void handle(WindowEvent event) {
        Stage stage = new Stage();
        stage.setTitle("Shutting down");

        Thread t = new Thread(() -> {

            for (SingleCruncherPane singleCruncherPane : app.crunchers.getSingleCruncherPanes()) {
                singleCruncherPane.getCounterCruncher().stop();
            }

            app.cacheOutput.stop();

            for (SingleFileInputPane singleFileInputPane : app.fileInputs.getFileInputPanes()) {
                singleFileInputPane.getFileInputComponent().stop();
                Object pauseSleepLock = singleFileInputPane.getFileInputComponent().getPauseSleepLock();
                synchronized (pauseSleepLock) {
                    pauseSleepLock.notify();
                }
            }

            App.inputThreadPool.shutdown();

            for (SingleCruncherPane singleCruncherPane : app.crunchers.getSingleCruncherPanes()) {
                singleCruncherPane.getCounterCruncher().getMyThreadPool().shutdown();
            }

            App.outputThreadPool.shutdown();
            app.cacheOutput.getSortThreadPool().shutdown();

            App.cruncherThreadPool.awaitQuiescence(100, TimeUnit.DAYS);
            try {
                App.inputThreadPool.awaitTermination(100, TimeUnit.DAYS);
                App.outputThreadPool.awaitTermination(100, TimeUnit.DAYS);

                for (SingleCruncherPane singleCruncherPane : app.crunchers.getSingleCruncherPanes()) {
                    singleCruncherPane.getCounterCruncher().getMyThreadPool().awaitTermination(100, TimeUnit.DAYS);
                }

                app.cacheOutput.getSortThreadPool().awaitTermination(100, TimeUnit.DAYS);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Platform.runLater(stage::close);

        });

        VBox vBox = new VBox();

        stage.setScene(new Scene(vBox, 300, 300));

        t.start();

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
