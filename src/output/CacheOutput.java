package output;

import app.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CacheOutput extends OutputComponent {

    private ObservableList<String> resultObservableList;
    private ConcurrentHashMap<String, Future<Map<String, Long>>> results;
    private int sortProgressLimit;
    private AtomicBoolean workFinished;

    public CacheOutput(int sortProgressLimit) {
        super();
        this.results = new ConcurrentHashMap();
        this.sortProgressLimit = sortProgressLimit;
        this.resultObservableList = FXCollections.observableArrayList(); // TODO Ovo treba da se setuje iz GUI-a
        this.workFinished = new AtomicBoolean(false);
    }

    @Override
    public void run() {

        while (true) {
            try {
                ProcessedFile currFile = inputQueue.take();

                if (currFile.fileName == "\\") {
                    break;
                }

                System.out.println("Tu sam : " + currFile.fileName);

                // Ako vec ima u mapi, vidi da pokrenes novog worker-a da se blokira i stavi ga tamo tek kad je spreman

                results.put(currFile.fileName, currFile.bagCounts);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        App.outputThreadPool.shutdown();
        try {
            App.outputThreadPool.awaitTermination(100, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        workFinished.set(true);
    }

    public void union(String resultName, Unifier unifier) {
        results.put(resultName, (Future<Map<String, Long>>) App.outputThreadPool.submit(unifier));
    }

    public Map<String, Long> take(String resultName) {
        try {
            return results.get(resultName).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Long> poll(String resultName) {
        if (results.get(resultName).isDone()) {
            try {
                return results.get(resultName).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void stop() {
        try {
            inputQueue.put(new ProcessedFile("\\", null));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<String> getResultObservableList() {
        return resultObservableList;
    }

    public AtomicBoolean getWorkFinished() {
        return workFinished;
    }

    public ConcurrentHashMap<String, Future<Map<String, Long>>> getResults() {
        return results;
    }
}
