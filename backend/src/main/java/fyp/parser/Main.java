package fyp.parser;

import fyp.utils.Helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
class Main{
    public static void main(String[] args) throws InterruptedException{
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Helper.writeLog("log/runtime.log", "Starting the parser at " + dateFormat.format(date), true);
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
        WorkerThread worker = new WorkerThread();
        scheduledThreadPool.scheduleWithFixedDelay(worker, 0, 10, TimeUnit.SECONDS);
    }
}