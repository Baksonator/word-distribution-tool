package input;

import app.App;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorkAssigner implements Runnable {

    private BlockingQueue<String> filesToRead;
    private CopyOnWriteArrayList<String> cruncherComponents;
    private String currentlyReading;  // Za prikazivanje aktivnosti

    public WorkAssigner(BlockingQueue filesToRead, CopyOnWriteArrayList cruncherComponents) {
        this.filesToRead = filesToRead;
        this.cruncherComponents = cruncherComponents;
        this.currentlyReading = "";
    }

    @Override
    public void run() {
        while (true) {

            try {
                currentlyReading = filesToRead.take();

                if (currentlyReading.equals("\\")) {
                    break;
                }

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
