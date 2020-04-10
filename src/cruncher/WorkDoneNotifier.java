package cruncher;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import output.CacheOutput;
import output.ProcessedFile;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class WorkDoneNotifier extends Task {

    private Future<Map<String, Long>> result;
    private String activeFileName;
    private String activeFileName1;
    private ObservableList<String> activeFiles;
    private CacheOutput cacheOutput;
    private boolean exists;

    public WorkDoneNotifier(Future<Map<String, Long>> result, String activeFileName, ObservableList<String> activeFiles,
                            CacheOutput cacheOutput, boolean exists, String activeFileName1) {
        this.result = result;
        this.activeFileName = activeFileName;
        this.activeFiles = activeFiles;
        this.cacheOutput = cacheOutput;
        this.exists = exists;
        this.activeFileName1 = activeFileName1;
    }

    @Override
    public void run() {
        try {
            result.get();

            if (exists) {
                ProcessedFile processedFile = new ProcessedFile(activeFileName, result);
                cacheOutput.getInputQueue().put(processedFile);
            } else {
                Platform.runLater(() -> {
                    String toPut = activeFileName.split("/")[activeFileName.split("/").length - 1];
                    cacheOutput.getResultObservableList()
                            .set(cacheOutput.getResultObservableList().indexOf(toPut + "*"), toPut);
                });
            }

            System.out.println("Finished crunching: " + activeFileName1);

            Platform.runLater(() -> activeFiles.remove(activeFileName1.split("/")[activeFileName1
                    .split("/").length - 1]));


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Platform.runLater(() -> {
                activeFiles.clear();
                cacheOutput.getResultObservableList().clear();
            });
        }
    }

    @Override
    protected Object call() throws Exception {
        return null;
    }
}
