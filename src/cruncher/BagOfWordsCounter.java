package cruncher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class BagOfWordsCounter extends RecursiveTask<HashMap<String, Long>> {

    private int counterLimit;
    private String[] text;
    private int arity;
    private boolean smallJob;
    private int start;
    private int length;

    public BagOfWordsCounter(int counterLimit, String[] text, int arity, boolean smallJob, int start, int length) {
        this.counterLimit = counterLimit;
        this.text = text;
        this.arity = arity;
        this.smallJob = smallJob;
        this.start = start;
        this.length = length;
    }

    @Override
    protected HashMap<String, Long> compute() {
        HashMap<String, Long> result = new HashMap<>();

        if (smallJob) {

            String[] currentWindow = new String[arity];
            String[] copyOfWindow = new String[arity];

            if (length - 1 < arity) {

                return result;

            } else {
                for (int i = 0; i < arity; i++) {
                    currentWindow[i] = text[start + i].toLowerCase();
                    copyOfWindow[i] = text[start + i].toLowerCase();
                }

                Arrays.sort(copyOfWindow);

                result.put(String.join(" ", copyOfWindow), 1L);

                for (int i = arity; i <= length; i++) {
                    for (int j = 0; j < arity - 1; j++) {
                        currentWindow[i] = currentWindow[i + 1];
                        copyOfWindow[i] = currentWindow[i];
                    }

                    currentWindow[arity - 1] = text[start + i];
                    copyOfWindow[arity - 1] = currentWindow[arity - 1];

                    Arrays.sort(copyOfWindow);

                    StringBuilder sb = new StringBuilder();

                    for (int j = 0; j < arity - 1; j++) {
                        sb.append(copyOfWindow[j]);
                        sb.append(" ");
                    }
                    sb.append(copyOfWindow[arity - 1]);

                    String key = sb.toString();
//                    String key = String.join(" ", copyOfWindow);

                    if (result.containsKey(key)) {
                        result.put(key, result.get(key) + 1);
                    } else {
                        result.put(key, 1L);
                    }
                }

            }

        } else {

            ArrayList<BagOfWordsCounter> counters = new ArrayList<>();
            long currentSize = 0;

            int counter = 0;
            int lastIndex = 0;
            for (String word : text) {
                currentSize += word.length();

                if (currentSize >= counterLimit) {
                    counters.add(new BagOfWordsCounter(counterLimit, text, arity,
                            true, lastIndex, counter - lastIndex));
                    currentSize = 0;
                    lastIndex = counter + 1;
                }

                counter++;
            }

            for (BagOfWordsCounter bagOfWordsCounter : counters) {
                bagOfWordsCounter.fork();
            }

            List<HashMap<String, Long>> resultList = new ArrayList<>();

            for (BagOfWordsCounter bagOfWordsCounter : counters) {
                resultList.add(bagOfWordsCounter.join());
            }

            for (HashMap<String, Long> singleMap : resultList) {
                for (String key : singleMap.keySet()) {
                    if (result.containsKey(key)) {
                        result.put(key, result.get(key) + singleMap.get(key));
                    } else {
                        result.put(key, singleMap.get(key));
                    }
                }
            }

            // TODO Take into account neighbouring strings when joining

        }

        return result;
    }


}
