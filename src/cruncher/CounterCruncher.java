package cruncher;

import app.App;
import input.InputCompontent;

import java.util.*;
import java.util.concurrent.*;

public class CounterCruncher extends CruncherComponent {

    private int counterLimit;
    private CopyOnWriteArrayList<String> activeFiles;

    public CounterCruncher(int arity, int counterLimit) {
        super(arity);
        this.counterLimit = counterLimit;
        this.activeFiles = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {

        while (true) {

            try {
                ReadFile currentFile = inputQueue.take();

                if (currentFile.fileName.equals("\\")) {

                    Iterator<InputCompontent> inputCompontentIterator = inputCompontents.iterator();
                    while (inputCompontentIterator.hasNext()) {
                        InputCompontent currComponent = inputCompontentIterator.next();

                        if (currComponent.getCruncherComponents().contains(this)) {
                            currComponent.deleteCruncher(this);
                        }
                    }
                    break;
                }

                activeFiles.add(currentFile.fileName);

                Future<Map<String, Long>> result = App.cruncherThreadPool.submit(new BagOfWordsCounter(counterLimit,
                        currentFile.fileContents, arity, false, 0, currentFile.fileContents.length()));

                Thread t = new Thread(new HelperWorker(result));
                t.start();

                currentFile = null;

                // TODO Contact Outputs
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public void stop() {
        try {
            inputQueue.put(new ReadFile("\\", ""));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public CopyOnWriteArrayList<String> getActiveFiles() {
        return activeFiles;
    }
}
