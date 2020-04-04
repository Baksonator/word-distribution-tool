package output;

import app.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CacheOutput extends OutputComponent {

    private ObservableList<String> resultObservableList;
    private ConcurrentHashMap<String, Future<Map<String, Long>>> results;
    private int sortProgressLimit;
    private AtomicBoolean workFinished;
    private ExecutorService sortThreadPool;
    private ConcurrentHashMap<String, Map<String, Long>> sumResults;

    public CacheOutput(int sortProgressLimit, ObservableList<String> resultObservableList) {
        super();
        this.results = new ConcurrentHashMap();
        this.sortProgressLimit = sortProgressLimit;
        this.resultObservableList = resultObservableList;
        this.workFinished = new AtomicBoolean(false);
        this.sortThreadPool = Executors.newCachedThreadPool();
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
        sortThreadPool.shutdown();
        try {
            sortThreadPool.awaitTermination(100, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        workFinished.set(true);
    }

    public void union(String resultName, Unifier unifier) {
//        Future<Map<String, Long>> future = App.outputThreadPool.submit(unifier);
        results.put(resultName, ((Future<Map<String, Long>>)App.outputThreadPool.submit(unifier)));
        System.out.println(resultName);
        System.out.println("IS IT DONE " + results.get(resultName).isDone());
//        try {
//            System.out.println(results.get(resultName).get().size());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
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
