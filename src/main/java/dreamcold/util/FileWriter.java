package dreamcold.util;

import java.io.File;
import java.io.IOException;

public class FileWriter {

    public static  String outputPath;

    public static void wirteContent(String content){
        wirteContent(content,outputPath);
    }


    public static void wirteContent(String content,String path){
        java.io.FileWriter fw=null;
        try {
            File file=new File(path);
            fw=new java.io.FileWriter(file,true);
            fw.write(content);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if (fw!=null){
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
