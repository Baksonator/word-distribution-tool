package cruncher;

import app.App;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import output.CacheOutput;
import output.OutputComponent;
import output.ProcessedFile;

import java.util.*;
import java.util.concurrent.*;

public class CounterCruncher extends CruncherComponent {

    private int counterLimit;
    private ObservableList<String> activeFiles;
    private ExecutorService myThreadPool;
    private final Object stopLock;

    public CounterCruncher(int arity, int counterLimit, ObservableList<String> activeFiles) {
        super(arity);
        this.counterLimit = counterLimit;
        this.activeFiles = activeFiles;
        this.myThreadPool = Executors.newCachedThreadPool();
        this.stopLock = new Object();
    }

    @Override
    public void run() {

        while (true) {

            try {
                ReadFile currentFile = inputQueue.take();

                if (currentFile.fileName.equals("\\")) {
                    break;
                }

                String copyReading = currentFile.fileName.replace("\\", "/");
                String copyReading1 = copyReading + "-arity" + arity;

                Platform.runLater(() -> activeFiles.add(copyReading
                        .split("/")[copyReading.split("/").length - 1]));

                Future<Map<String, Long>> result = App.cruncherThreadPool.submit(new BagOfWordsCounter(counterLimit,
                        currentFile.fileContents, arity, false, 0, currentFile.fileContents.length()));

                System.out.println("Started crunching: " + copyReading);

                Iterator<OutputComponent> outputComponentIterator = outputComponents.iterator();
                while (outputComponentIterator.hasNext()) {

                    CacheOutput outputComponent = (CacheOutput)outputComponentIterator.next();
                    ProcessedFile processedFile = new ProcessedFile(copyReading1,
                            result);

                    boolean exists = false;
                    if (!outputComponent.getResults().containsKey(copyReading1)) {
                        outputComponent.getInputQueue().put(processedFile);
                        Platform.runLater(() -> outputComponent.getResultObservableList()
                                .add(copyReading1.split("/")[copyReading1.split("/").length - 1] + "*"));
                    } else {
                        exists = true;
                    }

                    myThreadPool.submit(new WorkDoneNotifier(result, copyReading1, activeFiles, outputComponent,
                            exists, copyReading));

                }

            } catch (InterruptedException e) {
                Platform.runLater(() -> activeFiles.clear());
                e.printStackTrace();
                return;
            }

        }

        synchronized (stopLock) {
            stopLock.notify();
        }
    }

    public void stop() {
        try {
            synchronized (stopLock) {
                inputQueue.put(new ReadFile("\\", ""));
                stopLock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Object call() throws Exception {
        return null;
    }

    public ExecutorService getMyThreadPool() {
        return myThreadPool;
    }
}
