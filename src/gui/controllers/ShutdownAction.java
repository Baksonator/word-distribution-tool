package gui.controllers;

import app.App;
import gui.FileInputsPane;
import gui.SingleCruncherPane;
import gui.SingleFileInputPane;
import input.FileInput;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

//                App.inputThreadPool.shutdown();
//                App.outputThreadPool.shutdown();

                for (SingleFileInputPane singleFileInputPane : app.fileInputs.getFileInputPanes()) {
                    singleFileInputPane.getFileInputComponent().stop();
                    Object pauseSleepLock = singleFileInputPane.getFileInputComponent().getPauseSleepLock();
                    synchronized (pauseSleepLock) {
                        pauseSleepLock.notify();
                    }
                }

                for (SingleCruncherPane singleCruncherPane : app.crunchers.getSingleCruncherPanes()) {
//                    singleCruncherPane.getCounterCruncher().getMyThreadPool().shutdown();
                    singleCruncherPane.getCounterCruncher().stop();
                }

                app.cacheOutput.stop();

//                app.cacheOutput.getSortThreadPool().shutdown();

                App.cruncherThreadPool.awaitQuiescence(100, TimeUnit.DAYS);
                try {
                    if (App.inputThreadPool.isShutdown()) {
                        App.inputThreadPool.awaitTermination(100, TimeUnit.DAYS);
                    }
                    App.outputThreadPool.awaitTermination(100, TimeUnit.DAYS);

                    for (SingleCruncherPane singleCruncherPane : app.crunchers.getSingleCruncherPanes()) {
                        singleCruncherPane.getCounterCruncher().getMyThreadPool().awaitTermination(100, TimeUnit.DAYS);
                    }

                    app.cacheOutput.getSortThreadPool().awaitTermination(100, TimeUnit.DAYS);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        stage.close();
                    }
                });;

//                boolean running = false;
//                while (!running) {
//                    running = true;
//                    for (SingleFileInputPane singleFileInputPane : app.fileInputs.getFileInputPanes()) {
//                        running = running && singleFileInputPane.getFileInputComponent().getWorkAssigner().getWorkFinished().get();
//                    }
//
//                    for (SingleCruncherPane singleCruncherPane : app.crunchers.getSingleCruncherPanes()) {
//                        running = running && singleCruncherPane.getCounterCruncher().getWorkFinished().get();
//                    }
//
//                    running = running && app.cacheOutput.getWorkFinished().get();
//                }
////                    Thread.sleep(5000);
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        stage.close();
//                    }
//                });

            }
        });

        VBox vBox = new VBox();

        stage.setScene(new Scene(vBox, 300, 300));

//        for (SingleFileInputPane singleFileInputPane : app.fileInputs.getFileInputPanes()) {
//            singleFileInputPane.getFileInputComponent().stop();
//            Object pauseSleepLock = singleFileInputPane.getFileInputComponent().getPauseSleepLock();
//            synchronized (pauseSleepLock) {
//                pauseSleepLock.notify();
//            }
//        }
//
//        for (SingleCruncherPane singleCruncherPane : app.crunchers.getSingleCruncherPanes()) {
//            singleCruncherPane.getCounterCruncher().stop();
//        }
//
//        app.cacheOutput.stop();

        t.start();

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
