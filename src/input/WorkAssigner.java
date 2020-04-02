package input;

import app.App;
import cruncher.CruncherComponent;
import cruncher.ReadFile;
import javafx.concurrent.Task;

import java.util.Iterator;
import java.util.concurrent.*;

public class WorkAssigner extends Task<String> {

    private BlockingQueue<String> filesToRead;
    private CopyOnWriteArrayList<CruncherComponent> cruncherComponents;
    private String currentlyReading;  // Za prikazivanje aktivnosti

    public WorkAssigner(BlockingQueue filesToRead, CopyOnWriteArrayList cruncherComponents) {
        this.filesToRead = filesToRead;
        this.cruncherComponents = cruncherComponents;
        this.currentlyReading = "Idle";
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

                System.out.println(currentlyReading + " : " + readFile.length());

                ReadFile currFile;

                Iterator<CruncherComponent> cruncherComponentIterator = cruncherComponents.iterator();
                while (cruncherComponentIterator.hasNext()) {
                    CruncherComponent currComponent = cruncherComponentIterator.next();
                    currFile = new ReadFile(currentlyReading, readFile);
                    currComponent.getInputQueue().put(currFile);
                    currFile = null;
                }
                currFile = null;

                currentlyReading = "Idle";
                updateMessage(currentlyReading);

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

    @Override
    protected String call() throws Exception {
        return null;
    }
}
