package app;

import input.FileInput;
import input.InputCompontent;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// C:\\Users\\Bogdan\\IdeaProjects\\kids_2020_d1_bogdan_bakarec_rn2016\\data\\disk1\\A     pause

public class App {

    public static ExecutorService inputThreadPool;

    public static void main(String[] args) {
        // TODO Threadpool size read froom number of disks
        inputThreadPool = Executors.newFixedThreadPool(2);

        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("C:\\Users\\Bogdan\\IdeaProjects\\kids_2020_d1_bogdan_bakarec_rn2016\\data\\app.properties")) {

            prop.load(input);

        } catch (IOException e) {
            e.printStackTrace();
        }

        final Object pauseSleepLock = new Object();

        FileInput inputCompontent = new FileInput("disk1", Integer.parseInt(prop.getProperty("file_input_sleep_time")), pauseSleepLock);

        Thread inputComponentThread = new Thread(inputCompontent);

        inputComponentThread.start();

        while (true) {
            Scanner sc = new Scanner(System.in);
            String command = sc.nextLine();

            if (command.equals("exit")) {
                inputCompontent.stop();
                synchronized (pauseSleepLock) {
                    pauseSleepLock.notify();
                }
                break;
            } else if (command.startsWith("addDir")) {
                inputCompontent.addDirectory(command.split(" ")[1]);
            } else if (command.startsWith("removeDir")) {
                inputCompontent.deleteDirectory(command.split(" ")[1]);
            } else if (command.equals("start")) {
                synchronized (pauseSleepLock) {
                    if (inputCompontent.getPaused().compareAndSet(true, false)) {
                        pauseSleepLock.notify();
                    }
                }
            } else if (command.equals("pause")) {
                synchronized (pauseSleepLock) {
                    if (inputCompontent.getPaused().compareAndSet(false, true)) {
                        pauseSleepLock.notify();
                    }
                }
            }
        }

        inputThreadPool.shutdown();
    }

}
