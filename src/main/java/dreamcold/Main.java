package dreamcold;



import dreamcold.task.Task;
import dreamcold.util.FileWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {

    private static String dir=null;

    private static String outputdir=null;

    private static Integer numThreads=4;





    public static void main(String[] args){
        if (args==null||args.length!=2){
            System.out.println("参数数量不正确");
            System.out.println("请正常输入参数：要提取的Java的项目的路径，要输出到的文件的路径");
            System.exit(0);
        }
        dir=args[0];
        outputdir=args[1];
        FileWriter.outputPath=outputdir;
        travelDir();

    }


    private static void travelDir(){
        if (dir != null) {
            File root = new File(dir);
            if(root.exists() && root.isDirectory()) {
                File[] projs = root.listFiles();
                int cnt=0;
                int all=projs.length;
                for(File proj : projs) {
                    cnt++;
                    dir = proj.getPath();

                    extractDir();
                    System.out.printf("all is %d , now is the %d",all,cnt);
                }
            }
        }
    }


    private static void extractDir() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
        LinkedList<Task> tasks = new LinkedList<Task>();
        try {
            Files.walk(Paths.get(dir)).filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".java")).forEach(f -> {
                Task task = new Task(f);
                tasks.add(task);
                System.out.println("解析 "+f.getFileName()+"...");
            });
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}
