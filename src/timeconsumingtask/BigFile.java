package filesAsync;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Callable;

public class FilesAsync implements Callable<String> {

    String _dirName;
    String _fileName;
    Integer _min = 0;
    Integer _max = 100;

    public FilesAsync(String dirName, String fileName, Integer min, Integer max) {
        this._dirName = dirName;
        this._fileName = fileName;
        this._min = min;
        this._max = max;
    }

    @Override
    public String call() throws Exception {
        return createBigFile();
    }

    public String createBigFile() {
        return createBigFile(_dirName, _fileName, _min, _max);
    }

    public static String createBigFile(String dirName, String fileName, Integer min, Integer max) {
        StringBuilder status;
        try {
            File dir = new File(dirName);
            if(!dir.exists()){
                 dir.mkdir();
            }
            File file = new File(dir.getAbsolutePath(), fileName);
            try {
                if(!file.exists()) {
                    file.createNewFile();
                    FileWriter writer = new FileWriter(file);

                    try {
                        for (var i = min; i < max + 1; i++) {
                            writer.write(i + "\n");
                        }
                    } catch (IOException ex) {
                        throw ex;
                    } finally {
                        writer.close();
                    }

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e){
                throw e;
            }
            status = new StringBuilder("OK");
        }catch (Exception e){
            System.out.println(e.getMessage());
            status = new StringBuilder("FAIL");
        }

        return String.format("%6s %s/%s -> %d -- %d", status, dirName, fileName, min, max);
    }


}
