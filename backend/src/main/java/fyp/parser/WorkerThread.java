package fyp.parser;

import fyp.utils.Helper;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by phucnguyen on 20/2/16.
 */
public class WorkerThread implements Runnable {
    @Override
    public void run() {
        System.out.println("Working thread starting...");
        Helper.writeLog("log/runtime.log", "Working thread STARTING...", true);
        ExecutorService executor = Executors.newFixedThreadPool(5);
        String root = "../data/grant";
        File dirs = new File(root);
        System.out.println(dirs.getAbsolutePath());
        File [] files = dirs.listFiles();
        int count = 0;
        for (File f: files) {
            if (f.isFile() && Helper.hasExtension(f.getName(), "xml")) {
                Helper.writeLog("log/runtime.log", "Parsing file " + f.getName(), true);
                USPTOParser parser = new USPTOParser(f.getAbsolutePath());
                executor.execute(parser);
                count++;
            }
        }
        if(count == 0){
            Helper.writeLog("log/runtime.log", "No new file to parse", true);
        }
        executor.shutdown();
        while(!executor.isTerminated()){

        }
        Helper.writeLog("log/runtime.log", "Working thread FINISHED!\n", true);
        System.out.println("Worker thread finished!");
    }
}
