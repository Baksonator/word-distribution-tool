package cruncher;

import app.App;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.*;
import java.util.concurrent.*;

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
        try {
            HashMap<String, Long> result = new HashMap<>();

            if (smallJob) {

                LinkedList<String> currentWindowList = new LinkedList<>();

                if (length - start - 1 < arity) {

                    return result;

                } else {
                    int lastIndex = start;
                    int c = 0;
                    int k = start;
                    for (; k < length; k++) {
                        if (c < arity) {
                            if (text.charAt(k) == ' ' || text.charAt(k) == '\n') {
                                currentWindowList.add(text.substring(lastIndex, k));

                                c++;
                                lastIndex = k + 1;
                            }
                        } else {
                            break;
                        }

                    }

                    if (k == length) {
                        currentWindowList.add(text.substring(lastIndex, k));

                        List<String> help = (LinkedList) currentWindowList.clone();
                        Collections.sort(help);

                        String key = String.join(" ", help);
                        result.put(key, 1L);

                        return result;
                    }

                    List<String> help = (LinkedList) currentWindowList.clone();
                    Collections.sort(help);

                    String key = String.join(" ", help);
                    result.put(key, 1L);

                    for (; k < length; k++) {
                        if (text.charAt(k) == ' ' || text.charAt(k) == '\n') {
                            currentWindowList.remove(0);
                            currentWindowList.add(text.substring(lastIndex, k));

                            lastIndex = k + 1;

                            help = (LinkedList) currentWindowList.clone();
                            Collections.sort(help);

                            key = String.join(" ", help);
                            if (result.containsKey(key)) {
                                result.put(key, result.get(key) + 1);
                            } else {
                                result.put(key, 1L);
                            }

                        }
                    }

                    if (k == length) {
                        currentWindowList.remove(0);
                        currentWindowList.add(text.substring(lastIndex, k));

                        help = (LinkedList) currentWindowList.clone();
                        Collections.sort(help);

                        key = String.join(" ", help);
                        if (result.containsKey(key)) {
                            result.put(key, result.get(key) + 1);
                        } else {
                            result.put(key, 1L);
                        }

                    }

                }
            } else {

                ArrayList<BagOfWordsCounter> counters = new ArrayList<>();
                ArrayList<Future<Map<String, Long>>> threadCounters = new ArrayList<>();

                Map<String, Long> cornerBags = new HashMap<>();
                StringBuilder sb = new StringBuilder();

                int lastIndex = 0;
                for (int l = counterLimit; l < text.length(); l += counterLimit) {
                    while (l < text.length() && text.charAt(l) != ' ' && text.charAt(l) != '\n') {
                        l++;
                    }
                    counters.add(new BagOfWordsCounter(counterLimit, text, arity, true, lastIndex, l));

                    String[] currentWindow = new String[arity];
                    String[] copyOfWindow = new String[arity];
                    int c = arity - 2;
                    int k = l - 1;
                    int lastIndexChar = l;
                    while (c >= 0) {
                        if (text.charAt(k) == ' ' || text.charAt(k) == '\n') {
                            currentWindow[c] = text.substring(k + 1, lastIndexChar).intern();
                            copyOfWindow[c] = currentWindow[c].intern();

                            lastIndexChar = k;
                            c--;
                        }
                        k--;
                    }

                    k = l + 1;
                    lastIndexChar = l + 1;
                    c = arity - 2;
                    while (c >= 0 && k < text.length()) {
                        if (text.charAt(k) == ' ' || text.charAt(k) == '\n') {
                            currentWindow[arity - 1] = text.substring(lastIndexChar, k).intern();
                            copyOfWindow[arity - 1] = currentWindow[arity - 1].intern();

                            lastIndexChar = k + 1;
                            c--;

                            Arrays.sort(copyOfWindow);

                            sb = new StringBuilder();
                            for (int i = 0; i < arity - 1; i++) {
                                sb.append(copyOfWindow[i]);
                                sb.append(" ");
                            }
                            sb.append(copyOfWindow[arity - 1]);

                            String key = sb.toString().intern();

                            if (cornerBags.containsKey(key)) {
                                cornerBags.put(key, cornerBags.get(key) + 1);
                            } else {
                                cornerBags.put(key, 1L);
                            }

                            for (int i = 0; i < arity - 1; i++) {
                                currentWindow[i] = currentWindow[i + 1].intern();
                                copyOfWindow[i] = currentWindow[i].intern();
                            }

                        }
                        k++;
                    }

                    lastIndex = l + 1;
                }

                System.out.println("Pustio");

                for (BagOfWordsCounter bagOfWordsCounter : counters) {
//                threadCounters.add(App.cruncherThreadPool.submit(bagOfWordsCounter));
                    bagOfWordsCounter.fork();
                }

                BagOfWordsCounter thisBagOfWordsCounter = new BagOfWordsCounter(counterLimit, text, arity, true, lastIndex, text.length());
//            Future<Map<String, Long>> lastBitFuture = App.cruncherThreadPool.submit(thisBagOfWordsCounter);
                Map<String, Long> lastBit = thisBagOfWordsCounter.compute();

                List<Map<String, Long>> resultList = new ArrayList<>();

//            for (Future<Map<String, Long>> bagOfWordsCounterFuture : threadCounters) {
//                try {
//                    resultList.add(bagOfWordsCounterFuture.get());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//            }

                for (BagOfWordsCounter bagOfWordsCounter : counters) {
                    resultList.add(bagOfWordsCounter.join());
                }

//            Map<String, Long> lastBit = null;
//            try {
//                lastBit = lastBitFuture.get();
//            } catch (InterruptedException e) {
//
//
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }

                System.out.println("Stigao");

                for (Map<String, Long> singleMap : resultList) {
                    for (String key : singleMap.keySet()) {
                        if (lastBit.containsKey(key)) {
                            lastBit.put(key, lastBit.get(key) + singleMap.get(key));
                        } else {
                            lastBit.put(key, singleMap.get(key));
                        }
                    }
                    singleMap = null;
                }

                for (String key : cornerBags.keySet()) {
                    if (lastBit.containsKey(key)) {
                        lastBit.put(key, lastBit.get(key) + cornerBags.get(key));
                    } else {
                        lastBit.put(key, cornerBags.get(key));
                    }
                }

                text = null;

                System.gc();

                return lastBit;
            }

            return result;
        } catch (OutOfMemoryError e) {
            stopApp();
        }
        return new HashMap<>();
    }

    private void stopApp() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = new Stage();
                stage.setTitle("Shutting down");

                VBox vBox = new VBox();

                Button okBtn = new Button("OK");
                okBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Platform.exit();
                        System.exit(0);
                    }
                });
                vBox.getChildren().add(okBtn);

                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        Platform.exit();
                        System.exit(0);
                    }
                });

                App.inputThreadPool.shutdownNow();
                App.cruncherThreadPool.shutdownNow();
                App.outputThreadPool.shutdownNow();

                stage.setScene(new Scene(vBox, 300, 300));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            }
        });
    }

}
