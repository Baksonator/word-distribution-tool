package output;

import app.App;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.util.Map;
import java.util.concurrent.*;

public class CacheOutput extends OutputComponent {

    private ObservableList<String> resultObservableList;
    private ConcurrentHashMap<String, Future<Map<String, Long>>> results;
    private int sortProgressLimit;
    private ExecutorService sortThreadPool;
    private final Object stopLock;

    public CacheOutput(int sortProgressLimit, ObservableList<String> resultObservableList) {
        super();
        this.results = new ConcurrentHashMap<>();
        this.sortProgressLimit = sortProgressLimit;
        this.resultObservableList = resultObservableList;
        this.sortThreadPool = Executors.newCachedThreadPool();
        this.stopLock = new Object();
    }

    @Override
    public void run() {

        while (true) {
            try {
                ProcessedFile currFile = inputQueue.take();

                if (currFile.fileName.equals("\\")) {
                    break;
                }

                results.put(currFile.fileName, currFile.bagCounts);

                System.out.println("Output received file: " + currFile.fileName);

            } catch (InterruptedException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    resultObservableList.clear();
                });
                return;
            }
        }

        synchronized (stopLock) {
            stopLock.notify();
        }
    }

    public void union(String resultName, Unifier unifier) {
        results.put(resultName, App.outputThreadPool.submit(unifier));
    }

    public Map<String, Long> take(String resultName) throws InterruptedException {
        try {
            return results.get(resultName).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Long> poll(String resultName) {
        if (results.get(resultName).isDone()) {
            try {
                return results.get(resultName).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void stop() {
        try {
            synchronized (stopLock) {
                inputQueue.put(new ProcessedFile("\\", null));
                stopLock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<String> getResultObservableList() {
        return resultObservableList;
    }

    public ConcurrentHashMap<String, Future<Map<String, Long>>> getResults() {
        return results;
    }

    public void setResultObservableList(ObservableList<String> resultObservableList) {
        this.resultObservableList = resultObservableList;
    }

    public int getSortProgressLimit() {
        return sortProgressLimit;
    }

    public ExecutorService getSortThreadPool() {
        return sortThreadPool;
    }
}
