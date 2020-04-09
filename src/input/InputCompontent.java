package input;

import cruncher.CruncherComponent;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class InputCompontent implements Runnable {

    protected CopyOnWriteArrayList<CruncherComponent> cruncherComponents;

    public InputCompontent() {
        this.cruncherComponents = new CopyOnWriteArrayList<>();
    }

    public CopyOnWriteArrayList<CruncherComponent> getCruncherComponents() {
        return cruncherComponents;
    }

    public void addCruncher(CruncherComponent cruncherComponent) { this.cruncherComponents.add(cruncherComponent); }

    public void deleteCruncher(CruncherComponent cruncherComponent) {
        this.cruncherComponents.remove(cruncherComponent);
    }
}
