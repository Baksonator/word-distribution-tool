package input;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Callable;

public class FileReader implements Callable<String> {

    private String filePath;

    public FileReader(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String call() throws Exception {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();

        String readFile = new String(data, "UTF-8");
        return readFile;
    }


}
