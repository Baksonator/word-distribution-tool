package cruncher;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import output.CacheOutput;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class WorkDoneNotifier extends Task {

    private Future<Map<String, Long>> result;
    private String activeFileName;
    private ObservableList<String> activeFiles;
    private CopyOnWriteArrayList<ObservableList<String>> resultObservableLists;
    private Object lock = new Object();
    private CacheOutput cacheOutput;
    private boolean exists;

    public WorkDoneNotifier(Future<Map<String, Long>> result, String activeFileName, ObservableList activeFiles,
                            CopyOnWriteArrayList<ObservableList<String>> resultObservableList, CacheOutput cacheOutput,
                            boolean exists) {
        this.result = result;
        this.activeFileName = activeFileName;
        this.activeFiles = activeFiles;
        this.resultObservableLists = resultObservableList;
        this.cacheOutput = cacheOutput;
        this.exists = exists;
    }

    @Override
    public void run() {
        try {
            Map<String, Long> realRes = result.get();

            if (exists) {
                cacheOutput.getResults().put(activeFileName, result);
            }

            // Ovo je za aktivne fajlove i zvezdicu
//            activeFiles.remove(activeFileName);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    activeFiles.remove(activeFileName.split("/")[activeFileName.split("/").length - 1]);
                }
            });

//            for (ObservableList<String> resultObservableList : resultObservableLists) {
//                resultObservableList.set(resultObservableList.indexOf(activeFileName), activeFileName);
//            }
            // TODO Pitaj da ako vec postoji tu


            System.out.println(realRes.keySet().size());
//            System.gc();
//            synchronized (lock) {
//                lock.wait();
//            }
//            System.out.println(realRes.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Object call() throws Exception {
        return null;
    }
}
