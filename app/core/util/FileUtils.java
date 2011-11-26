package core.util;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class FileUtils implements Serializable {

    public static Reader getReaderForFile(File file) throws IOException {
        if (file.getName().endsWith(".gz")) {
            return new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
        } else {
            return new FileReader(file);
        }
    }

}