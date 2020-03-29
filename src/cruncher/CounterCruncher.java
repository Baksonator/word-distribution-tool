package cruncher;

import app.App;
import input.InputCompontent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

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

                System.out.println(currentFile.fileName + " : " + currentFile.fileContents.length());

                activeFiles.add(currentFile.fileName);

                // TODO Don't work with array, kills memory, have to work with original string

                Future<HashMap<String, Long>> result = App.cruncherThreadPool.submit(new BagOfWordsCounter(counterLimit,
                        currentFile.fileContents.split(" "), arity, false, 0, 0));

                Thread t = new Thread(new HelperWorker(result));
                t.start();

                // TODO Contact Outputs
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public CopyOnWriteArrayList<String> getActiveFiles() {
        return activeFiles;
    }
}
