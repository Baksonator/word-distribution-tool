package input;

import app.App;
import cruncher.CruncherComponent;
import cruncher.ReadFile;
import javafx.concurrent.Task;

import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorkAssigner extends Task<String> {

    private BlockingQueue<String> filesToRead;
    private CopyOnWriteArrayList<CruncherComponent> cruncherComponents;
    private String currentlyReading;
    private final Object stopLock;
    private AtomicBoolean stopped;

    public WorkAssigner(BlockingQueue<String> filesToRead, CopyOnWriteArrayList<CruncherComponent> cruncherComponents,
                        Object stopLock, AtomicBoolean stopped) {
        this.filesToRead = filesToRead;
        this.cruncherComponents = cruncherComponents;
        this.currentlyReading = "Idle";
        this.stopLock = stopLock;
        this.stopped = stopped;
    }


    @Override
    public void run() {
        updateMessage(currentlyReading);

        while (true) {

            try {
                currentlyReading = filesToRead.take();


                if (currentlyReading.equals("\\")) {
                    break;
                }

                String copyReading = currentlyReading.replace("\\", "/");
                updateMessage(copyReading.split("/")[copyReading.split("/").length - 1]);

                Future<String> readFileFuture = App.inputThreadPool.submit(new FileReader(currentlyReading));
                String readFile = readFileFuture.get();

                if (readFile == null) {
                    updateMessage("Idle");
                    break;
                }

                ReadFile currFile;

                if (!stopped.get()) {
                    Iterator<CruncherComponent> cruncherComponentIterator = cruncherComponents.iterator();
                    while (cruncherComponentIterator.hasNext()) {
                        CruncherComponent currComponent = cruncherComponentIterator.next();
                        currFile = new ReadFile(currentlyReading, readFile);
                        currComponent.getInputQueue().put(currFile);
                        currFile = null;
                    }
                }
                currFile = null;


                currentlyReading = "Idle";
                updateMessage(currentlyReading);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }

        synchronized (stopLock) {
            stopLock.notify();
        }
//        App.inputThreadPool.shutdown();
    }

    public BlockingQueue<String> getFilesToRead() {
        return filesToRead;
    }

    @Override
    protected String call() throws Exception {
        return null;
    }

}
