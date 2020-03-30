package cruncher;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HelperWorker implements Runnable {

    private Future<Map<String, Long>> result;

    public HelperWorker(Future<Map<String, Long>> result) {
        this.result = result;
    }

    @Override
    public void run() {
        try {
            Map<String, Long> realRes = result.get();
//            for (String key : realRes.keySet()) {
//                System.out.println(key + " : " + realRes.get(key));
//            }
            System.out.println(realRes.keySet().size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
