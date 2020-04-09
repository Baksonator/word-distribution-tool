package gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import java.util.*;

public class SorterTask extends Task {

    private int sortProgressLimit;
    private Map<String, Long> unsortedResult;
    private LineChart<Number, Number> resultChart;
    private ProgressBar progressBar;
    private Label barProgress;
    private VBox vBox;
    private static int compareCount = 0;

    public SorterTask(int sortProgressLimit, Map<String, Long> unsortedResult, LineChart<Number, Number> resultChart, ProgressBar progressBar, Label barProgress, VBox vBox) {
        this.sortProgressLimit = sortProgressLimit;
        this.unsortedResult = unsortedResult;
        this.resultChart = resultChart;
        this.progressBar = progressBar;
        this.barProgress = barProgress;
        this.vBox = vBox;
    }

    @Override
    protected Object call() throws Exception {
        int mapSize = unsortedResult.size();
        int totalCompares = mapSize * (int)(Math.log10(mapSize) / Math.log10(2));


        Set<Map.Entry<String, Long>> entries = unsortedResult.entrySet();

        Comparator<Map.Entry<String, Long>> valueComparator = (o1, o2) -> {
            compareCount++;

            if (compareCount % sortProgressLimit == 0) {
                Platform.runLater(() -> progressBar.setProgress((double)compareCount / (double)totalCompares));
            }

            Long v1 = o1.getValue();
            Long v2 = o2.getValue();
            return v2.compareTo(v1);
        };

        List<Map.Entry<String, Long>> listOfEntries = new ArrayList<>(entries);

        listOfEntries.sort(valueComparator);

        LinkedHashMap<String, Long> sortedResult = new LinkedHashMap<>(listOfEntries.size());

        for (Map.Entry<String, Long> entry : listOfEntries) {
            sortedResult.put(entry.getKey(), entry.getValue());
        }

        int i = 0;
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Long> entry : sortedResult.entrySet()) {
            if (i == 100) {
                break;
            }
            i++;
            series.getData().add(new XYChart.Data<>(i, entry.getValue()));
        }

        Platform.runLater(() -> {
            resultChart.getData().clear();
            resultChart.getData().addAll(series);
            vBox.getChildren().remove(progressBar);
            vBox.getChildren().remove(barProgress);
        });

        return null;
    }
}
