package imager.domain;

/**
 * Created by John on 26/06/2015.
 */
public class Defaults {
    public static String getDefaultDirectoryForPlayRecordAndLogfile(){
        return "C:\\Users\\John\\Documents\\KinectData\\";
    }
    public static boolean getRunInTestMode(){
        return false;
    }
    public static String getPlayFilePath() {
        return Defaults.getDefaultDirectoryForPlayRecordAndLogfile() + "originalJamie.oni";
    }
    public static String getLogFilePath() {
        return Defaults.getDefaultDirectoryForPlayRecordAndLogfile() + "LogFile.txt";
    }


   /* public static void delay(int myDelayMS){
        Long myStartTime = System.currentTimeMillis();
        do {//nothing
        } while ((System.currentTimeMillis() - myStartTime)< myDelayMS );  //Delay 5s  Gives enough time to read the time diff, in certain logging levels

    }*/
   /* public static void delay(int myDelayMS){
        Long myStartTime = (new Date()).getTime();
        do {//nothing
        } while (((new Date()).getTime() - myStartTime)< myDelayMS );  //Delay 5s  Gives enough time to read the time diff, in certain logging levels

    }*/
    /*public static Long getTimeInMS(){
        return System.currentTimeMillis();
        //long myTime = ((new Date()).getTime());
        //return myTime;
    }*/

}
