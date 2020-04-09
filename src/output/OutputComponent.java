package output;

import cruncher.CruncherComponent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class OutputComponent implements Runnable {

    private CopyOnWriteArrayList<CruncherComponent> cruncherComponents;
    protected BlockingQueue<ProcessedFile> inputQueue;

    public OutputComponent() {
        cruncherComponents = new CopyOnWriteArrayList<>();
        inputQueue = new LinkedBlockingQueue<>();
    }

    public BlockingQueue<ProcessedFile> getInputQueue() {
        return inputQueue;
    }

    public CopyOnWriteArrayList<CruncherComponent> getCruncherComponents() {
        return cruncherComponents;
    }
}
