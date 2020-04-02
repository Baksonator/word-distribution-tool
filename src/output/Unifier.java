package output;

import javafx.concurrent.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

// TODO Promeniti u javafx.concurrent.Task kada se bude zvzalo sa fronta
public class Unifier implements Callable<Map<String, Long>> {

    private List<String> resultsToSum;
    private CacheOutput cacheOutput;

    public Unifier(List<String> resultsToSum, CacheOutput cacheOutput) {
        this.resultsToSum = resultsToSum;
        this.cacheOutput = cacheOutput;
    }

    @Override
    public Map<String, Long> call() throws Exception {
        Map<String, Long> finalResult = new HashMap<>();

        for (String resultName : resultsToSum) {
            Map<String, Long> result = cacheOutput.take(resultName);
            for (Map.Entry<String, Long> entry : result.entrySet()) {
                finalResult.merge(entry.getKey(), entry.getValue(), Long::sum);
            }

            // TODO Update Progress
        }

        return finalResult;
    }
}
