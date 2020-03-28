package input;

import cruncher.CruncherComponent;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileInput extends InputCompontent  {

    private WorkAssigner workAssigner;
    private final String disk;
    private final int sleepTime;
    private CopyOnWriteArrayList<String> directories;
    private ConcurrentHashMap<String, Long> lastModifiedFile;
    private ConcurrentHashMap<String, String> parentDirectories;
    private AtomicBoolean paused;
    private BlockingQueue<String> filesToRead;
    private final Object pauseSleepLock;
    private boolean working;

    public FileInput(String disk, int sleepTime, Object pauseSleepLock) {
        super();
        this.disk = disk;
        this.sleepTime = sleepTime;
        this.directories = new CopyOnWriteArrayList<>();
        this.lastModifiedFile = new ConcurrentHashMap<>();
        this.parentDirectories = new ConcurrentHashMap<>();
        this.paused = new AtomicBoolean(true);
        this.filesToRead = new LinkedBlockingQueue<>();
        this.pauseSleepLock = pauseSleepLock;
        this.workAssigner = new WorkAssigner(this.filesToRead, this.cruncherComponents);
        this.working = true;
    }

    @Override
    public void run() {
        Thread workAssignerThread = new Thread(workAssigner);
        workAssignerThread.start();

        while (working) {
            synchronized (pauseSleepLock) {
                if (paused.get()) {
                    try {
                        pauseSleepLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("Started scan");
            Iterator<String> iteratorDirectories = directories.iterator();

            while (iteratorDirectories.hasNext()) {
                String directoryPath = iteratorDirectories.next();
                File directory = new File(directoryPath);

                try {
                    readDirectory(directory, directoryPath);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Stopped scan");

            synchronized (pauseSleepLock) {
                if (!paused.get()) {
                    try {
                        pauseSleepLock.wait(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void readDirectory(File directory, String parentDirectory) throws InterruptedException {
        for (File fileEntry : directory.listFiles()) {

            if (fileEntry.isDirectory()) {

                readDirectory(fileEntry, parentDirectory);

            } else {

                if (lastModifiedFile.containsKey(fileEntry.getAbsolutePath())) {

                    if (lastModifiedFile.get(fileEntry.getAbsolutePath()) < fileEntry.lastModified()) {

                        workAssigner.getFilesToRead().put(fileEntry.getAbsolutePath());
                        lastModifiedFile.put(fileEntry.getAbsolutePath(), fileEntry.lastModified());
                        parentDirectories.put(fileEntry.getAbsolutePath(), parentDirectory);
                    }

                } else {

                    workAssigner.getFilesToRead().put(fileEntry.getAbsolutePath());
                    lastModifiedFile.put(fileEntry.getAbsolutePath(), fileEntry.lastModified());
                    parentDirectories.put(fileEntry.getAbsolutePath(), parentDirectory);
                }
            }
        }
    }

    public void stop() {
        try {
            workAssigner.getFilesToRead().put("\\");
            working = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addDirectory(String directory) {
        directories.add(directory);
    }

    public void deleteDirectory(String directory) {
        directories.remove(directory);

        ArrayList<String> filesToRemove = new ArrayList<>();
        Iterator<String> removeFileIterator = lastModifiedFile.keySet().iterator();

        while (removeFileIterator.hasNext()) {

            String filePath = removeFileIterator.next();
            if (parentDirectories.get(filePath).equals(directory)) {
                filesToRemove.add(filePath);
                parentDirectories.remove(filePath);
            }
        }

        for (String toRemove : filesToRemove) {
            lastModifiedFile.remove(toRemove);
        }
    }

    public AtomicBoolean getPaused() {
        return paused;
    }

    public void setPaused(AtomicBoolean paused) {
        this.paused = paused;
    }

    public void addCruncher(CruncherComponent cruncherComponent) {
        this.cruncherComponents.add(cruncherComponent);
    }

    public void deleteCruncher(CruncherComponent cruncherComponent) {
        this.cruncherComponents.remove(cruncherComponent);
    }

}
