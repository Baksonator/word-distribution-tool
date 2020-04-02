package cruncher;

import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class WorkDoneNotifier implements Runnable {

    private Future<Map<String, Long>> result;
    private String activeFileName;
    private CopyOnWriteArrayList<String> activeFiles;
    private CopyOnWriteArrayList<ObservableList<String>> resultObservableLists;
    private Object lock = new Object();

    public WorkDoneNotifier(Future<Map<String, Long>> result, String activeFileName, CopyOnWriteArrayList activeFiles,
                            CopyOnWriteArrayList<ObservableList<String>> resultObservableList) {
        this.result = result;
        this.activeFileName = activeFileName;
        this.activeFiles = activeFiles;
        this.resultObservableLists = resultObservableList;
    }

    @Override
    public void run() {
        try {
            Map<String, Long> realRes = result.get();

            // Ovo je za aktivne fajlove i zvezdicu
            activeFiles.remove(activeFileName);
            for (ObservableList<String> resultObservableList : resultObservableLists) {
                resultObservableList.set(resultObservableList.indexOf(activeFileName), activeFileName);
            }


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
}
