package cruncher;

import output.OutputComponent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class CruncherComponent {

    protected int arity;
    protected CopyOnWriteArrayList<OutputComponent> outputComponents;
//    protected BlockingQueue<>

}
