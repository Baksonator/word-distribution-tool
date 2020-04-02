package cruncher;

import app.App;
import input.InputCompontent;
import javafx.collections.ObservableList;
import output.CacheOutput;
import output.OutputComponent;
import output.ProcessedFile;

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

                    Iterator<OutputComponent> outputComponentIterator = outputComponents.iterator();
                    while (outputComponentIterator.hasNext()) {
                        OutputComponent outputComponent = outputComponentIterator.next();
                        outputComponent.getCruncherComponents().remove(this);
                    }

                    break;
                }

                activeFiles.add(currentFile.fileName);


                // Ovo je za zvezdicu
                CopyOnWriteArrayList<ObservableList<String>> resultObservableLists = new CopyOnWriteArrayList<>();
                for (OutputComponent output : outputComponents) {
                    ((CacheOutput)output).getResultObservableList().add(currentFile.fileName + "-arity" + arity + "*");
                    resultObservableLists.add(((CacheOutput)output).getResultObservableList());
                }


                Future<Map<String, Long>> result = App.cruncherThreadPool.submit(new BagOfWordsCounter(counterLimit,
                        currentFile.fileContents, arity, false, 0, currentFile.fileContents.length()));


                Thread t = new Thread(new WorkDoneNotifier(result, currentFile.fileName + "-arity" + arity + "*"
                        , activeFiles, resultObservableLists));
                t.start();

                // Ovo je bilo zbog cuvanja fajla u memoriji
//                currentFile = null;

                Iterator<OutputComponent> outputComponentIterator = outputComponents.iterator();
                while (outputComponentIterator.hasNext()) {
                    OutputComponent outputComponent = outputComponentIterator.next();
                    ProcessedFile processedFile = new ProcessedFile(currentFile.fileName + "-arity" + arity,
                            result);
                    outputComponent.getInputQueue().put(processedFile);
                }

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
