package input;

import app.App;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorkAssigner implements Runnable {

    private BlockingQueue<String> filesToRead;
    private CopyOnWriteArrayList<String> cruncherComponents;
    private final Object pauseSleepLock;
    private AtomicBoolean paused;
    private final Object emptyQueueLock;
    private String currentlyReading;  // Za prikazivanje aktivnosti

    public WorkAssigner(BlockingQueue filesToRead, CopyOnWriteArrayList cruncherComponents, Object pauseSleepLock,
                        AtomicBoolean paused, Object emptyQueueLock) {
        this.filesToRead = filesToRead;
        this.cruncherComponents = cruncherComponents;
        this.pauseSleepLock = pauseSleepLock;
        this.paused = paused;
        this.emptyQueueLock = emptyQueueLock;
        this.currentlyReading = "";
    }

    @Override
    public void run() {
        while (true) {

            // TODO Vidi da li mozes samo da se spinujes ili je ovo pozeljnije
            synchronized (emptyQueueLock) {
                if (filesToRead.isEmpty()) {
                    try {
                        emptyQueueLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            synchronized (pauseSleepLock) {
                if (paused.get()) {
                    try {
                        pauseSleepLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                currentlyReading = filesToRead.take();
                Future<String> readFileFuture = App.inputThreadPool.submit(new FileReader(currentlyReading));
                String readFile = readFileFuture.get();

                System.out.println(currentlyReading + " : " + readFile.length());
                // TODO Slanje na Crunchere
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e1) {
                e1.printStackTrace();
            }

        }
    }

    public BlockingQueue<String> getFilesToRead() {
        return filesToRead;
    }

    public void setFilesToRead(BlockingQueue<String> filesToRead) {
        this.filesToRead = filesToRead;
    }
}
