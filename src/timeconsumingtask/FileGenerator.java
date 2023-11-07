package filesAsync;

import measurer.ExecMeasurer;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class FileGeneratorAsync {

    static int dirNum = 10;
    static int fileNum = 10;
    static int startNumberInFile = 0;
    static int lastNumberInFile = 1000_000;


    public static void main(String[] args) {

        String baseDirName = "dirForFiles";

        File baseDir = new File(baseDirName);
        File baseDir1 = new File(baseDir.getAbsolutePath() + "/" + "1");
        File baseDir2 = new File(baseDir.getAbsolutePath() + "/" + "2");


        if(baseDir.exists()){
            try{
                recursivlyDeleteDirectory(baseDir.getAbsolutePath());
            }catch (IOException e){
                System.out.println(e.getMessage());
                return;
            }
        }

        baseDir.mkdir();
        baseDir1.mkdir();
        baseDir2.mkdir();

        ExecMeasurer measurerForAction =
                new ExecMeasurer(() -> action( baseDir1, "dir", "file"));

        ExecMeasurer measurerForActionConcurrent =
                new ExecMeasurer(() -> FileGeneratorAsync.actionAsync(baseDir2 , "dir", "file"));


        System.out.println("ActionAsync: " + measurerForActionConcurrent.measureExecutionTimeMillis() + " millis");
        System.out.println("Action     : " + measurerForAction.measureExecutionTimeMillis() + " millis");


    }

    static String nameGenerator(String base, int i){
        return base + i;
    }

    static void action( File baseDir, String dirBase, String fileBase){

        ArrayList<String> results = new ArrayList<>();
        for(int i = 0; i < dirNum; i++){
            String dirName = baseDir.getAbsolutePath() + "/" + nameGenerator(dirBase, i);
            for (int j = 0; j < fileNum; j++){
                String fileName = nameGenerator(fileBase, j);
                String f = null;
                try {
                    f = FilesAsync.createBigFile(dirName, fileName, startNumberInFile, lastNumberInFile);
                    results.add(f);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }
        while(!results.isEmpty()){
            var r = results.get(0);
                try {
//                    System.out.println(r);
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }finally {
                    results.remove(r);
                }

        }
    }

    static void actionAsync(File baseDir, String dirBase, String fileBase){
        int thrdNumber = 10;
        ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(thrdNumber);

        ArrayList<Future<String>> results = new ArrayList<>();
        for(int i = 0; i < dirNum; i++){
            String dirName = baseDir.getAbsolutePath()+ "/" + nameGenerator(dirBase, i);
            for (int j = 0; j < fileNum; j++){
                String fileName = nameGenerator(fileBase, j);
                Future<String> f = executor.submit(new FilesAsync(dirName, fileName, startNumberInFile, lastNumberInFile));
                results.add(f);
            }
        }
        while(!results.isEmpty()){
            var r = results.get(0);
            if(r.isDone()){
                try {
//                    System.out.println(r.resultNow());
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }finally {
                    results.remove(r);
                }

            }
        }
        executor.shutdown();
    }


    // ******************************************************************************
    static void recursivlyDeleteDirectory(String fullDirPath) throws IOException {
        Path directoryPath = Paths.get(fullDirPath);
        Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
