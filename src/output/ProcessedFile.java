package output;

import java.util.Map;
import java.util.concurrent.Future;

public class ProcessedFile {

    protected String fileName;
    protected Future<Map<String, Long>> bagCounts;

    public ProcessedFile(String fileName, Future<Map<String, Long>> bagCounts) {
        this.fileName = fileName;
        this.bagCounts = bagCounts;
    }

}
