package input;

import cruncher.CruncherComponent;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class InputCompontent implements Runnable {

    protected CopyOnWriteArrayList<CruncherComponent> cruncherComponents;

    public InputCompontent() {
        this.cruncherComponents = new CopyOnWriteArrayList<>();
    }

}
