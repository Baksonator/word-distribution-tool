package app;

import cruncher.CounterCruncher;
import cruncher.CruncherComponent;
import input.FileInput;
import input.InputCompontent;
import output.CacheOutput;
import output.Unifier;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

// C:\\Users\\Bogdan\\IdeaProjects\\kids_2020_d1_bogdan_bakarec_rn2016\\data\\disk1\\A     pause

public class App {

    public static ExecutorService inputThreadPool;
    public static ForkJoinPool cruncherThreadPool;
    public static ExecutorService outputThreadPool;

    public static void main(String[] args) {
        // TODO Threadpool size read froom number of disks
        inputThreadPool = Executors.newFixedThreadPool(2);
        cruncherThreadPool = ForkJoinPool.commonPool();
//        cruncherThreadPool = Executors.newCachedThreadPool();
        outputThreadPool = Executors.newCachedThreadPool();

        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("C:\\Users\\Bogdan\\IdeaProjects\\kids_2020_d1_bogdan_bakarec_rn2016\\data\\app.properties")) {

            prop.load(input);

        } catch (IOException e) {
            e.printStackTrace();
        }

        final Object pauseSleepLock = new Object();

        final Object pauseSleepLock2 = new Object();

        FileInput inputCompontent = new FileInput("disk1", Integer.parseInt(prop.getProperty("file_input_sleep_time")), pauseSleepLock);

        FileInput inputComponent2 = new FileInput("disk2", Integer.parseInt(prop.getProperty("file_input_sleep_time")), pauseSleepLock2);

        CounterCruncher counterCruncher = new CounterCruncher(1, Integer.parseInt(prop.getProperty("counter_data_limit")));

//        CounterCruncher counterCruncher2 = new CounterCruncher(2, Integer.parseInt(prop.getProperty("counter_data_limit")));

        CacheOutput cacheOutput = new CacheOutput(Integer.parseInt(prop.getProperty("sort_progress_limit")));

        Thread inputComponentThread = new Thread(inputCompontent);

        Thread inputComponentThread2 = new Thread(inputComponent2);

        Thread counterCruncherThread = new Thread(counterCruncher);

//        Thread counterCruncherThread2 = new Thread(counterCruncher2);

        Thread cachecOutputThread = new Thread(cacheOutput);

        inputComponentThread.start();

        inputComponentThread2.start();

        counterCruncherThread.start();

//        counterCruncherThread2.start();

        cachecOutputThread.start();

        inputCompontent.addDirectory("C:\\Users\\Bogdan\\IdeaProjects\\kids_2020_d1_bogdan_bakarec_rn2016\\data\\disk1\\A");
        inputCompontent.addDirectory("C:\\Users\\Bogdan\\IdeaProjects\\kids_2020_d1_bogdan_bakarec_rn2016\\data\\disk1\\B");
        inputComponent2.addDirectory("C:\\Users\\Bogdan\\IdeaProjects\\kids_2020_d1_bogdan_bakarec_rn2016\\data\\disk2\\C");
        inputComponent2.addDirectory("C:\\Users\\Bogdan\\IdeaProjects\\kids_2020_d1_bogdan_bakarec_rn2016\\data\\disk2\\D");

        while (true) {
            Scanner sc = new Scanner(System.in);
            String command = sc.nextLine();

            if (command.equals("exit")) {
                inputCompontent.stop();
                inputComponent2.stop();
                synchronized (pauseSleepLock) {
                    pauseSleepLock.notify();
                }
                synchronized (pauseSleepLock2) {
                    pauseSleepLock2.notify();
                }
                counterCruncher.stop();
//                counterCruncher2.stop();
                cacheOutput.stop();
                break;
            } else if (command.startsWith("addDir1")) {
                inputCompontent.addDirectory(command.split(" ")[1]);
            } else if (command.startsWith("removeDir")) {
                inputCompontent.deleteDirectory(command.split(" ")[1]);
            } else if (command.equals("start")) {
                synchronized (pauseSleepLock) {
                    if (inputCompontent.getPaused().compareAndSet(true, false)) {
                        pauseSleepLock.notify();
                    }
                }
                synchronized (pauseSleepLock2) {
                    if (inputComponent2.getPaused().compareAndSet(true, false)) {
                        pauseSleepLock2.notify();
                    }
                }
            } else if (command.equals("pause")) {
                synchronized (pauseSleepLock) {
                    if (inputCompontent.getPaused().compareAndSet(false, true)) {
                        pauseSleepLock.notify();
                    }
                }
                synchronized (pauseSleepLock2) {
                    if (inputComponent2.getPaused().compareAndSet(false, true)) {
                        pauseSleepLock2.notify();
                    }
                }
            } else if (command.equals("addCrunch")) {
                inputCompontent.addCruncher(counterCruncher);
                counterCruncher.getInputCompontents().add(inputCompontent);
//                inputCompontent.addCruncher(counterCruncher2);
//                counterCruncher2.getInputCompontents().add(inputCompontent);
                inputComponent2.addCruncher(counterCruncher);
                counterCruncher.getInputCompontents().add(inputComponent2);
            } else if (command.equals("removeCrunch")) {
                inputCompontent.deleteCruncher(counterCruncher);
                inputComponent2.deleteCruncher(counterCruncher);
            } else if (command.startsWith("addDir2")) {
                inputComponent2.addDirectory(command.split(" ")[1]);
            } else if (command.equals("addOutput")) {
                counterCruncher.getOutputComponents().add(cacheOutput);
//                counterCruncher2.getOutputComponents().add(cacheOutput);
            } else if (command.equals("union")) {
                List<String> resultsToSum = new ArrayList<>();
                resultsToSum.add("C:\\Users\\Bogdan\\IdeaProjects\\kids_2020_d1_bogdan_bakarec_rn2016\\data\\disk1\\A\\wiki-1.txt-arity1");
                resultsToSum.add("C:\\Users\\Bogdan\\IdeaProjects\\kids_2020_d1_bogdan_bakarec_rn2016\\data\\disk1\\A\\wiki-2.txt-arity1");
                Unifier unifier = new Unifier(resultsToSum, cacheOutput);
                cacheOutput.union("sumica", unifier);
            }
        }

        inputThreadPool.shutdown();
        cruncherThreadPool.shutdown();
        outputThreadPool.shutdown();
    }

}
