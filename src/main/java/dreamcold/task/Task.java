package dreamcold.task;


import com.github.javaparser.ParseException;
import dreamcold.parser.FileParser;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

public class Task implements Callable<Void> {
    String code = null;
    String className=null;


    public Task(Path path) {
        try {
            this.code = new String(Files.readAllBytes(path));
            String fileName=path.getFileName().toString();
            fileName=fileName.substring(0,fileName.length()-5);
            this.className=fileName;
        } catch (IOException|StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            this.code = "";
            this.className="";
        }
    }

    @Override
    public Void call() throws Exception {
        try {
            FileParser featureExtractor = new FileParser(code,className);
            featureExtractor.extractFeatures();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
