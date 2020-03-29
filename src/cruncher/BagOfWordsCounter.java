package cruncher;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;

public class BagOfWordsCounter extends RecursiveTask<Map<String, Long>> {

    private int counterLimit;
    private String text;
    private int arity;
    private boolean smallJob;
    private int start;
    private int length;

    public BagOfWordsCounter(int counterLimit, String text, int arity, boolean smallJob, int start, int length) {
        this.counterLimit = counterLimit;
        this.text = text;
        this.arity = arity;
        this.smallJob = smallJob;
        this.start = start;
        this.length = length;
    }

    @Override
    protected Map<String, Long> compute() {
        HashMap<String, Long> result = new HashMap<>();

//        return result;

        if (smallJob) {

            String[] currentWindow = new String[arity];
            String[] copyOfWindow = new String[arity];
//            int[] wordStarts = new int[arity];

            if (length - 1 < arity) {

                return result;

            } else {
                int lastIndex = start;
                int c = 0;
                int k = start;
                for (; k < length; k++) {
                    if (c < arity) {
                        if (text.charAt(k) == ' ' || text.charAt(k) == '\n') {
                            currentWindow[c] = text.substring(lastIndex, k);
                            copyOfWindow[c] = currentWindow[c];
//                            wordStarts[c] = lastIndex;
                            c++;
                            lastIndex = k + 1;
                        }
                    } else {
                        break;
                    }
                }

                Arrays.sort(copyOfWindow);
//
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < arity - 1; i++) {
                    sb.append(copyOfWindow[i]);
                    sb.append(" ");
                }
                sb.append(copyOfWindow[arity - 1]);

                String key = sb.toString();
//                String key = text.substring(wordStarts[0], k);
//                String key = "bleja";

                result.put(key, 1L);

                for (; k < length; k++) {
                    if (text.charAt(k) == ' ' || text.charAt(k) == '\n') {
                        for (int i = 0; i < arity - 1; i++) {
                            currentWindow[i] = currentWindow[i + 1];
                            copyOfWindow[i] = currentWindow[i];
//                            wordStarts[i] = wordStarts[i + 1];
                        }
                        currentWindow[arity - 1] = text.substring(lastIndex, k);
                        copyOfWindow[arity - 1] = currentWindow[arity - 1];
//                        wordStarts[arity - 1] = lastIndex;

                        lastIndex = k + 1;

                        Arrays.sort(copyOfWindow);
//
                        sb = new StringBuilder();
                        for (int i = 0; i < arity - 1; i++) {
                            sb.append(copyOfWindow[i]);
                            sb.append(" ");
                        }
                        sb.append(copyOfWindow[arity - 1]);
//
                        key = sb.toString();
//                        key = text.substring(wordStarts[0], k);
//                        key = "bleja";

                        if (result.containsKey(key)) {
                            result.put(key, result.get(key) + 1);
                        } else {
                            result.put(key, 1L);
                        }

                    }
                }

            }
        } else {

            ArrayList<BagOfWordsCounter> counters = new ArrayList<>();
            long currentSize = 0;

            int lastIndex = 0;
            boolean sendWhenCan = false;
            for (int l = 0; l < text.length(); l++) {
                if (sendWhenCan && (text.charAt(l) == ' ' || text.charAt(l) == '\n')) {
                    counters.add(new BagOfWordsCounter(counterLimit, text, arity, true, lastIndex, l));

                    lastIndex = l + 1;
                    currentSize = 0;

                    sendWhenCan = false;
                } else {
                    if (currentSize >= counterLimit) {
                        sendWhenCan = true;
                        if (text.charAt(l) == ' ' || text.charAt(l) == '\n') {
                            counters.add(new BagOfWordsCounter(counterLimit, text, arity, true, lastIndex, l));

                            lastIndex = l + 1;
                            currentSize = 0;

                            sendWhenCan = false;
                        }
                    }
                }
                currentSize++;
            }
//            counters.add(new BagOfWordsCounter(counterLimit, text, arity, true, lastIndex, text.length()));

//            for (String word : text) {
//                currentSize += word.length();
//
//                if (currentSize >= counterLimit) {
//                    counters.add(new BagOfWordsCounter(counterLimit, text, arity,
//                            true, lastIndex, counter - lastIndex));
//                    currentSize = 0;
//                    lastIndex = counter + 1;
//                }
//
//                counter++;
//            }

            for (BagOfWordsCounter bagOfWordsCounter : counters) {
                bagOfWordsCounter.fork();
            }

            BagOfWordsCounter thisBagOfWordsCounter = new BagOfWordsCounter(counterLimit, text, arity, true, lastIndex, text.length());
            Map<String, Long> lastBit = thisBagOfWordsCounter.compute();

            List<Map<String, Long>> resultList = new ArrayList<>();

            for (BagOfWordsCounter bagOfWordsCounter : counters) {
                resultList.add(bagOfWordsCounter.join());
//                bagOfWordsCounter.join();
            }
            resultList.add(lastBit);

            for (Map<String, Long> singleMap : resultList) {
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
//
        return result;
    }


}
