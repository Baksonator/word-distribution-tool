package cruncher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HelperWorker implements Runnable {

    private Future<Map<String, Long>> result;
    private Object lock = new Object();

    public HelperWorker(Future<Map<String, Long>> result) {
        this.result = result;
    }

    @Override
    public void run() {
        try {
            Map<String, Long> realRes = result.get();
            System.out.println(realRes.keySet().size());
            System.gc();
//            synchronized (lock) {
//                lock.wait();
//            }
//            System.out.println(realRes.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
