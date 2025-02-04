package cruncher;

import input.InputCompontent;
import javafx.concurrent.Task;
import output.OutputComponent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class CruncherComponent extends Task  {

    protected int arity;
    protected CopyOnWriteArrayList<OutputComponent> outputComponents;
    protected CopyOnWriteArrayList<InputCompontent> inputCompontents;
    protected BlockingQueue<ReadFile> inputQueue;

    public CruncherComponent(int arity) {
        this.arity = arity;
        this.outputComponents = new CopyOnWriteArrayList<>();
        this.inputCompontents = new CopyOnWriteArrayList<>();
        this.inputQueue = new LinkedBlockingQueue<>();
    }

    public BlockingQueue<ReadFile> getInputQueue() {
        return inputQueue;
    }

    public CopyOnWriteArrayList<OutputComponent> getOutputComponents() {
        return outputComponents;
    }
}
