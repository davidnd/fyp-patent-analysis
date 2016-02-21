package fyp.parser;

import fyp.utils.Helper;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by phucnguyen on 20/2/16.
 */
public class WorkerThread implements Runnable {
    @Override
    public void run() {
        System.out.println("Working thread starting...");
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Helper.writeLog("log/runtime.log", "Working thread STARTING at " + dateFormat.format(date), true);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        String root = "../data/grant";
        File dirs = new File(root);
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
        Date finsihed = new Date();
        Helper.writeLog("log/runtime.log", "Working thread FINISHED at " + dateFormat.format(finsihed) + "\n", true);
        System.out.println("Worker thread finished!");
    }
}
