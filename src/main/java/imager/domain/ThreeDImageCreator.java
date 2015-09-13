package imager.domain;
// Press ESC to finish recording - this is important.   If you just press close, file will corrupt.

import SimpleOpenNI.*;
import ch.qos.logback.classic.LoggerContext;
import imager.domain.Correlators.CorrelatorForTwoPointClouds;
import imager.domain.Correlators.CorrelatorMoveZLast;
import imager.domain.PointClouds.Frame;
import imager.domain.ThreeDObjects.IntPVector3D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Random;

public class ThreeDImageCreator extends PApplet { //Note PApplet doesnt have a constructor and neither does ThreeDImageCreator.
    //private static final long serialVersionUID = 1L;
    //TODO - try calling drawing during calculations with noloop(), redraw()
    private final boolean useStepByStepCorrelator = true;
    static boolean playFlag;  //Gets set in main. This is static??
    private boolean setupHasAlreadyBeenRun = false;  //Works round bug in PApplet that calls setup() twice.
    public static float veryHighFloat = 99e20f;
    public static float invalidInt = -99999;
    public Random randomGenerator = new Random();
    BufferedWriter excelLogFileWriter;
    SimpleOpenNI ni; //SimpleOpenNIWrapper ni;
    float rotX = radians(180);  // by default rotate the whole scene 180deg around the intX-axis,
    float rotY = radians(0);
    float zoomF = 1f;
    //int     steps   = 1;  // to speed up the drawing, draw every third point
    int noFramesInFilm;  //number of frames in film
    int currentMovingFrame = 1; //current frame being correlated

    //int noOfFramesToCorrelate = 2; //13; //How many frames we actually use   // THERE ARE 285 FRAMES total
    //int firstFrame = 50;  //50 and 120 are good numbers for opposite sides.
    //int lastFrame = 62; //noFramesInFilm - 150;

    int noOfFramesToCorrelate = 2; //13; //How many frames we actually use   // THERE ARE 285 FRAMES total
    int firstFrame = 50;  //50 and 120 are good numbers for opposite sides.
    int lastFrame = 120; //noFramesInFilm - 150;

    static String myONIPlayOrRecordFileNameWithPath;
    static String myLogFileNameWithPath;  //This is static as set in main??
    //static String myONIMostRecentFileNameWithPath;
    Logger myLogger;
    boolean correlationIsInProgress = false;
    //PointCloud cloudArray[] = new PointCloud[noOfFramesToCorrelate];
    Frame frameArray[] = new Frame[noOfFramesToCorrelate];
    int displayMode = 0; //0 = original images, 1 = cloud being correlated, 2 = final image.

    CorrelatorForTwoPointClouds correlatorForTwoPointClouds;
    //PVector centreOfScreen = new PVector();
    //PFont textBoxFont = loadFont("C:\\Program Files (x86)\\processing-2.2.1\\modes\\java\\libraries\\minim\\examples\\SoundSpectrum\\data\\ArialMT-12.vlw");
    //PFont textBoxFont = loadFont("C:\\Program Files\\processing-2.2.1\\modes\\java\\libraries\\net\\examples\\CarnivoreClient\\data\\CourierNew-12.vlw");
    //PFont textBoxFont = createFont("Georgia", 32);
    //PFont textBoxFont = createFont("Monospaced.bold", 32); //good
    //PFont textBoxFont = createFont("Monospaced.plain", 32);  //doesnt work
    //PFont textBoxFont = createFont("Lucida console", 32);  //not great a bit blurred
    PFont textBoxFont = createFont("Consolas Bold", 32);

    int totalWidth = 1250;
    int totalHeight = 750;
    int mainPGraphicsWidth = totalWidth;
    //int mainPGraphicsHeight = (int) (totalHeight*0.75);
    int mainPGraphicsHeight = totalHeight;



    //int graphPGraphicsWidth = (int)(totalWidth*0.75);
    int graphPGraphicsWidth = totalWidth;
    //int graphPGraphicsHeight = totalHeight- mainPGraphicsHeight;
    int graphPGraphicsHeight = (int) (totalHeight * 0.25);
    //int textPGraphicsWidth = totalWidth- graphPGraphicsWidth;
    int textPGraphicsWidth = (int) (totalWidth * 0.25);
    //int textPGraphicsHeight = graphPGraphicsHeight;
    int textPGraphicsHeight = totalHeight;
    PGraphics mainPGraphics;
    PGraphics graphPGraphics;
    PGraphics textPGraphics;
    PVector mainPGraphicsOriginPVector;
    PVector graphPGraphicsOriginPVector;
    PVector textPGraphicsOriginPVector;
    //added
    boolean waitForButtonPressBeforeEachCorrelationStep = true;
    boolean testStartRun = false;
    long correlationStartTime; //Need to keep this as global variable unfortunately for now as each step is covered in draw right now.
    boolean drawEachStep = true;

    public boolean isDrawEachStep() {
        return drawEachStep;
    }

    public long getCorrelationStartTime() {
        return correlationStartTime;
    }

    public BufferedWriter getExcelLogFileWriter() {
        return excelLogFileWriter;
    }

    public int getGraphPGraphicsWidth() {
        return graphPGraphicsWidth;
    }

    public int getGraphPGraphicsHeight() {
        return graphPGraphicsHeight;
    }

    public CorrelatorForTwoPointClouds getCorrelatorForTwoPointClouds() {
        return correlatorForTwoPointClouds;
    }
    public void setCorrelatorForTwoPointClouds(CorrelatorForTwoPointClouds correlatorForTwoPointClouds) {
        this.correlatorForTwoPointClouds = correlatorForTwoPointClouds;
    }

    static public void main(String args[]) {
        System.out.println("Starting main");
        System.out.println("no of args = " + args.length);
        //Main can either be called directly, or from the JavaFX GUI.
        //Always expects -cp <classpath> to be passed if called directly ie 2 args
        //If called from JavaFX GUI it expects 3 args eg file name.
        System.out.println("Args passed are as follows:");
        for (int i = 0; i < args.length; i++) {
            System.out.println("arg " + i + "= " + args[i]);
        }
        if (args.length == 2 || args.length == 0) {
            System.out.printf("No args passed other than classpath. Setting defaults\n");
            playFlag = true;
            myONIPlayOrRecordFileNameWithPath = Defaults.getPlayFilePath();
            System.out.println("myONIPlayOrRecordFileNameWithPath = "+myONIPlayOrRecordFileNameWithPath);
            myLogFileNameWithPath = Defaults.getLogFilePath(); // getDefaultDirectoryForPlayRecordAndLogfile()+"\\LogFile.txt";
            System.out.println("myLogFileNameWithPath = "+ myLogFileNameWithPath);
        } else if (args.length == 3) {
            System.out.printf("Some additional args passed. Called from JavaFX GUI\n");
            playFlag = Boolean.valueOf(args[0]);
            myONIPlayOrRecordFileNameWithPath = args[1];//was 1
            myLogFileNameWithPath = args[2]; // was 2
        } else System.out.println("Don't know how to process this number of arguments yet");
        //If a constructor does not explicitly invoke a superclass constructor, the Java compiler automatically inserts a call to the no-argument constructor of the superclass.
        PApplet.main(new String[]{"--present", "imager.domain.ThreeDImageCreator"});  //This is needed or it silently doesn't work.  String should have same name as class.
    }

    public String getStringLoggerInfo() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory(); //Only needed to print logback's internal status
        return lc.toString();         // print logback's internal status
    }
    public String getStringClasspath(){
        String myResult = "";
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader)cl).getURLs();
        for(URL url: urls)
            myResult += ("CLASSPATH = " + url.getFile()+"\n");
        return myResult;
    }
    public void setup() {  //TODO This is a hack to try and understand why setup was running twice. Need to check if this is still happening
        System.out.println("Setup ThreeDImageCreator = " + hashCode() + " Other details = "+this);
        long start_time = System.currentTimeMillis();

        if (!setupHasAlreadyBeenRun) {
            System.out.println("running reallySetup");
            reallySetup();
            setupHasAlreadyBeenRun = true;
        } else {
            System.out.println("Setup has already been run");
        }
        long end_time = System.currentTimeMillis();
        long timeDiff = end_time - start_time;
        myLogger.info("TIME TO SETUP = "+timeDiff+ " ms");
    }

    public void reallySetup() {
        //printFontList();
        System.out.println("Setting up logger");
        myLogger = LoggerFactory.getLogger(this.getClass());  //This prints a lot of stuff out.
        System.out.println("Finished setting up logger");

        //myLogger.trace(getStringLoggerInfo());
        myLogger.trace("");
        myLogger.trace(getStringClasspath());
        myLogger.trace("textPGraphicsWidth = " + textPGraphicsWidth);
        myLogger.trace("textPGraphicsHeight = " + textPGraphicsHeight);
        myLogger.trace("STARTING reallySetup!!! " + Integer.toHexString(System.identityHashCode(this)));
        if (playFlag) {        // PLAYING, this works without the camera
            setupExcelLogFileWriter();
            setupCorrelator();
            loadFrames();
            size(totalWidth, totalHeight, P3D);
            setupPGraphicsAreasForPlay(); //Note this must come after size() with P3D
        } else { //RECORDING
            setUpForRecordFromFile();
        }
    }

    private void loadFrames() {
        //boolean testFlag = false;
        if (Defaults.getRunInTestMode()) loadTestFrames();
        else loadFramesFromFile();
    }

    private void loadTestFrames() {
        noFramesInFilm = 2;
        int hueIncrementPerFrame = getHueIncrementPerFrame(noFramesInFilm);
        frameArray[0] = new Frame(this, 0, hueIncrementPerFrame * 0, null, true); //last param is 'isTest'
        frameArray[1] = new Frame(this, 1, hueIncrementPerFrame * 1, frameArray[0], true);

        /*for (int myFrameNo = 0; myFrameNo < noFramesInFilm; myFrameNo++) {
            frameArray[myFrameNo] = new Frame(this, myFrameNo, hueIncrementPerFrame * myFrameNo, null);
        }*/
        correlatorForTwoPointClouds.setupFrames(frameArray[0], frameArray[1]);
    }

    private void loadFramesFromFile() {
        //ni = new SimpleOpenNIWrapper(this, myONIPlayOrRecordFileNameWithPath);  //TODO check file exists
        ni = new SimpleOpenNI(this, myONIPlayOrRecordFileNameWithPath);  //TODO check file exists
        ni.update();  //Need to do this before finding no of frames.
        noFramesInFilm = ni.framesPlayer();
        //println("This file has " + noFramesInFilm + " frames.");
        myLogger.debug("This file has " + noFramesInFilm + " frames.");
        int frameSkip = round((lastFrame - firstFrame) / (noOfFramesToCorrelate - 1));  //9 divisions gives 10 frames
        int hueIncrementPerFrame = getHueIncrementPerFrame(noOfFramesToCorrelate); //casting to int is redundant here
        int frameNo;
        Frame previousFrame = null;
        for (int counter = 0; counter < noOfFramesToCorrelate; counter++) {
            frameNo = firstFrame + counter * frameSkip;
            ni.seekPlayer(frameNo);   //Find a first image
            ni.update();  //need to do update after seekPlayer()
            int myHue = counter * hueIncrementPerFrame;
            //cloudArray[counter] = new PointCloud(this, frameNo, myHue);
            if (counter > 0) previousFrame = frameArray[counter - 1]; //else set to null above.
            frameArray[counter] = new Frame(this, frameNo, myHue, previousFrame, false);  //last param is 'isTest'
            myLogger.debug(String.format("Counter = %4d, frameNo = %4d, myHue = %4d", counter, frameNo, myHue));  //Neater way of formatting output than concatenating using +.  http://examples.javacodegeeks.com/core-java/lang/string/java-string-format-example/
        }
        //correlatorForTwoPointClouds.setupPointClouds(cloudArray[0], cloudArray[1]);
        correlatorForTwoPointClouds.setupFrames(frameArray[0], frameArray[1]);
    }

    private int getHueIncrementPerFrame(int noOfFramesToCorrelate) {
        return 255 / noOfFramesToCorrelate; //casting to int is redundant here
    }

    private void setupPGraphicsAreasForPlay() {
        mainPGraphics = createGraphics(mainPGraphicsWidth, mainPGraphicsHeight, P3D);
        graphPGraphics = createGraphics(graphPGraphicsWidth, graphPGraphicsHeight, P3D);
        textPGraphics = createGraphics(textPGraphicsWidth, textPGraphicsHeight, P2D);
        mainPGraphicsOriginPVector = new PVector(0, 0);
        //graphPGraphicsOriginPVector = new PVector(textPGraphicsWidth, mainPGraphicsHeight);
        graphPGraphicsOriginPVector = new PVector(0, mainPGraphicsHeight - graphPGraphicsHeight);
        //textPGraphicsOriginPVector = new PVector(0, mainPGraphicsHeight);
        textPGraphicsOriginPVector = new PVector(0, 0);
    }

    private void setupCorrelator() {
        //if (useStepByStepCorrelator) {
            correlatorForTwoPointClouds = new CorrelatorMoveZLast(this);  //only used on play, not record currently.
        /*} else {
            correlatorForTwoPointClouds = new CorrelatorFast(this);
        }*/
    }

    private void setUpForRecordFromFile() {
        myLogger.debug("Recording");
        //ni = new SimpleOpenNIWrapper(this);  //TODO check file exists
        ni = new SimpleOpenNI(this);  //TODO check file exists
        if (!ni.enableRecorder(myONIPlayOrRecordFileNameWithPath))
            println("Can't enable recorder with file " + myONIPlayOrRecordFileNameWithPath);
        if (!ni.init()) myLogger.warn("Can't initialise ni instance");
        ni.setMirror(false);  //new - added from alternativeViewpoint.
        if (!ni.enableDepth()) myLogger.warn("Can't open depth for recording.");
        if (!ni.enableRGB()) myLogger.warn("Can't open rgb for recording.");
        //if (!ni.enableIR()) myLogger.war ("Can't open IR for recording."); //IR doesn't work with simpleOpenNI It locks the screen up.
        if (!ni.alternativeViewPointDepthToImage()) myLogger.warn("Can't set alternative viewpoint");
        if (!ni.enableRecorder(myONIPlayOrRecordFileNameWithPath))
            myLogger.warn("Can't open file " + myONIPlayOrRecordFileNameWithPath);
        if (!ni.addNodeToRecording(SimpleOpenNI.NODE_DEPTH, false)) myLogger.warn("Can't add depth node to recording");
        if (!ni.addNodeToRecording(SimpleOpenNI.NODE_IMAGE, false)) myLogger.warn("Can't add image node to recording");
        //ni.addNodeToRecording(SimpleOpenNI.NODE_IR, false);  //IR doesn't work.  TODO - try doing this before enablingIR
        size(ni.depthWidth() + 10 + ni.rgbWidth(), ni.depthHeight() > ni.rgbHeight() ? ni.depthHeight() : ni.rgbHeight(), P3D);
        colorMode(HSB);
        stroke(255, 255, 255);
        smooth();
        //perspective(radians(45), width / height, 10, 150000);
        perspective(radians(45), width / height, 10, 150000);
    }

    public void setupExcelLogFileWriter() {
        excelLogFileWriter = null;
        myLogger.debug("Opening logfile " + myLogFileNameWithPath);
        try {
            FileWriter myWriter = new FileWriter(myLogFileNameWithPath);
            excelLogFileWriter = new BufferedWriter(myWriter);
            excelLogFileWriter.write("methodCallTime, frameNumber, correlateBoxAtAnglesDontDrawSteps, parameters,  time,  totalAngleShift, xCount , yCount, zCount, staticMostRecentSumOfMatchedPoints,  correlationScore,  overallMinScore");
            excelLogFileWriter.newLine();
            //myWriter.write();
            //cloudArray[counter].writeAsciiFormatToFileWriter(myWriter, noOfFramesToCorrelate, writeCorrelationCloudsInsteadOfFinalImage);  //Second param is resolution.  1 is too fine.  30 too granular.//second parameter is resolution
        } catch (IOException e) {
            e.printStackTrace();
        }
        myLogger.debug("Finished opening logfile");
    }

    public SimpleOpenNI getNi() {
        return ni;
    }

    /*public void drawWord(String myString, PVector myPVector){
        myLogger.debug("In new draw");
        PFont font;
// The font must be located in the sketch's
// "data" directory to load successfully
        font = loadFont("C:\\Program Files (x86)\\processing-2.2.1\\modes\\java\\libraries\\minim\\examples\\SoundSpectrum\\data\\ArialMT-12.vlw");
        textFont(font, 32);
        text(myString, myPVector.intX, myPVector.intY, myPVector.z);
        //
    }
    /*private void setDefaultPerspective(){
        //size(100, 100, P3D);
        noFill();
        double fov = PI/3.0;
        float cameraZ = (float)((height/2.0) / tan((float)(fov/2.0)));
        perspective((float) fov, width/height, (float)(cameraZ/10.0), (float)(cameraZ*10.0));
    }*/
    public void draw() {
        background(0, 0, 0);
        //camera((float) (width / 2.0), (float) (250 / 2.0), 300, (float) (width / 2.0), (float) (250 / 2.0), 0f, 0f, 1f, 0f);   //camera((float)(width/2.0), (float)(height/2.0), 300, (float)(width/2.0), (float)(height/2.0), 0f, 0f, 1f, 0f);
        /*mainViewport.stroke(color(50,50,50));
        mainViewport.rect(-10,-10,20,20);
        image(mainViewport,10,10);*/
        if (playFlag) {
            mainPGraphics.beginDraw();
            mainPGraphics.clear();
            mainPGraphics.colorMode(HSB, 255, 255, 255);
            mainPGraphics.background(0);  //0 = black

            mainPGraphics.stroke(255, 255, 255); //TODO is this really needed?
            mainPGraphics.smooth(); //TODO is this really needed?
            mainPGraphics.perspective(radians(45), mainPGraphics.width / mainPGraphics.height, 10, 150000);
            float imageY = 200;
            mainPGraphics.camera((float) (mainPGraphics.width / 2.0), imageY, 500, (float) (mainPGraphics.width / 2.0), imageY, 0f, 0f, 1f, 0f);   //camera((float)(width/2.0), (float)(height/2.0), 300, (float)(width/2.0), (float)(height/2.0), 0f, 0f, 1f, 0f);
            //camera(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)

            /*setDefaultPerspective();
            drawFont();
            perspective(radians(45), width / height, 10, 150000);  //This was further up in the original setup
            */
            //myLogger.debug("DRAWING WORD");
            // drawWord("My Word is very long", centreOfScreen);
            //myLogger.debug("in draw() Playing back");
            mainPGraphics.translate(mainPGraphics.width / 2, mainPGraphics.height / 4); //height/2
            mainPGraphics.rotateX(rotX);
            mainPGraphics.rotateY(rotY);
            mainPGraphics.scale(zoomF);

            if(testStartRun){
                waitForButtonPressBeforeEachCorrelationStep = false;
                startCorrelateGoing('l');
                testStartRun = false;
            }

            //If we're in the middle of doing a correlation, take another step then draw again.
            if (correlationIsInProgress && !waitForButtonPressBeforeEachCorrelationStep) doACorrelationStep();
            //Draw frames

            switch (displayMode) {
                case 0:
                    for (int counter = 0; counter < 2; counter++) {
                        //myLogger.debug("Drawing zPointPlane number "+counter);
                        frameArray[counter].getPointCloudCorrelationRealWorld().getzPointPlane().draw(mainPGraphics); //can remove zPointPlane and add true in the 2nd argument here to draw pointcloud.
                        //cloudArray[counter].myZPointPlane.drawMe(mainPGraphics);
                        //cloudArray[counter].drawTopOfHeadShiftedModel();
                    }
                    break;
                case 1:  //display original images
                    for (int counter = 0; counter < noOfFramesToCorrelate; counter++) {  //0 to 9
                        //cloudArray[counter].drawOriginalModel(mainPGraphics);
                        frameArray[counter].getPointCloudOriginalRealWorld().draw(mainPGraphics, true);
                    }
                    break;
                case 2:  //display two images being correlated
                    for (int counter = 0; counter < noOfFramesToCorrelate; counter++) {
                        //cloudArray[counter].drawTopOfHeadShiftedModel(mainPGraphics);
                        frameArray[counter].getPointCloudTopOfHeadShiftedRealWorld().draw(mainPGraphics, true);
                    }
                    break;
                case 3:
                    //manually correlate frame by frame
                    for (int counter = 0; counter <= currentMovingFrame; counter++) {
                        //cloudArray[counter].drawZMatrix(true);  //useRealColors
                        //cloudArray[counter].drawShiftedModel(mainPGraphics, true);
                        frameArray[counter].getPointCloudCorrelationRealWorld().drawShifted(mainPGraphics, true);
                    }
                    break;
                case 4:
                    for (int counter = 0; counter < noOfFramesToCorrelate; counter++) {
                        //cloudArray[counter].drawFinalModel(mainPGraphics, true);  //parameter is boolean useRealColors
                        frameArray[counter].getPointCloudFinalRealWorld().draw(mainPGraphics, true);
                    }
                    //myFinalDisplayModel.drawIt();
                    break;
                case 5:
                    for (int counter = 0; counter < noOfFramesToCorrelate; counter++) {
                        //cloudArray[counter].drawFinalModel(mainPGraphics, false);
                        frameArray[counter].getPointCloudFinalRealWorld().draw(mainPGraphics, false);
                    }
                    //myFinalDisplayModel.drawIt();
                    break;
            }

            mainPGraphics.endDraw();
            correlatorForTwoPointClouds.drawChart(graphPGraphics);  //Draws the chart onto the PGraphics object
            correlatorForTwoPointClouds.drawText(textPGraphics, textBoxFont);

            //myLogger.debug("mainPGraphicsOriginPVector.intX = " + mainPGraphicsOriginPVector.x + " mainPGraphicsOriginPVector.intY = " + mainPGraphicsOriginPVector.y);
            //myLogger.debug("mainPGraphicsWidth = " + mainPGraphicsWidth + " mainPGraphicsHeight = " + mainPGraphicsHeight);
            image(mainPGraphics, mainPGraphicsOriginPVector.x, mainPGraphicsOriginPVector.y);
            image(textPGraphics, textPGraphicsOriginPVector.x, textPGraphicsOriginPVector.y);
            image(graphPGraphics, graphPGraphicsOriginPVector.x, graphPGraphicsOriginPVector.y);

        } else { //Draw what's recording
            //translate(0,0,-1000);  // set the rotation center of the scene 1000 infront of the camera
            println("In draw() - recording");
            ni.update();
            image(ni.depthImage(), 0, 0);
            image(ni.rgbImage(), ni.depthWidth() + 10, 0);
            //image(ni.irImage(), ni.depthWidth() + ni.rgbWidth()+20,0);
        }
    }

    public void correlateAfterManualMove() {
        float myScore = correlatorForTwoPointClouds.correlationScoreOnePosition();  //Drawlines
        myLogger.debug("Manual move score = " + myScore);
        //cloudArray[currentMovingFrame].printAnglesAndOffsets();
        frameArray[currentMovingFrame].getPointCloudCorrelationRealWorld().printAnglesAndOffsets();  //TODO is this right place for angles and offsets.
    }

    /*
    public void startAllFrameCorrelation() { //ss than no of frames used as array is zero based so if noOfFramesToCorrelate = 2, last frame = 1.
        myLogger.debug("***************** Correlating cloud " + counter + "(Frame " + cloudArray[counter].frameNumber + ") against cloud " + (counter - 1) + "(Frame " + cloudArray[counter - 1].frameNumber + ")********");
        correlatorForTwoPointClouds.setupPointClouds(cloudArray[counter - 1], cloudArray[counter]);
        if (counter == (noOfFramesToCorrelate - 1)) {
            thisIsTheLastOne = true;
        }
        correlatorForTwoPointClouds.startCorrelate(thisIsTheLastOne, "full");
    }
}
*/
    /*private void stmyCorrelatorForTwoPointClouds.correlate(thisIsTheLastOne, "full");
                    correlatorForTwoPointClouds.startCorrelateGoing('l', true); //l = long, correlateColors
                }*/
    /*            break;
            //TODO - raise error on case else?
        }
    }artTwoFrameCorrelation() { //Just correlate one frame for a performance imager.junit
    myLogger.info("startTwoFrameCorrelation, slowCorrelationIsInProgress = " + correlationIsInProgress);
    if(!correlationIsInProgress) {
        correlatorForTwoPointClouds.setupPointClouds(cloudArray[0], cloudArray[1]);
        //correlatorForTwoPointClouds.correlate(true, "simple");
        correlationIsInProgress = true;
        correlatorForTwoPointClouds.startCorrelate(true, "simple");
    }
}*/

    private void startTwoFrameCorrelation() {
        myLogger.info("** startTwoFrameCorrelation");
        //correlatorForTwoPointClouds.setupPointClouds(cloudArray[0], cloudArray[1]);
        //TODO - do we need to set up frames rather than pointclouds here?
        //correlatorForTwoPointClouds.setupPointClouds(frameArray[0].pointCloudCorrelationRealWorld, frameArray[1].pointCloudCorrelationRealWorld);
        correlatorForTwoPointClouds.setupFrames(frameArray[0], frameArray[1]);
        correlatorForTwoPointClouds.startCorrelate(true, "simple");
    }

    public void startAllFrameCorrelation() {
        boolean thisIsTheLastOne = false;
        for (int counter = 1; counter < noOfFramesToCorrelate; counter++) { //must be less than no of frames used as array is zero based so if noOfFramesToCorrelate = 2, last frame = 1.
            myLogger.info("***************** Correlating cloud " + counter + "(Frame " + frameArray[counter].getFrameNo() + ") against cloud " + (counter - 1) + "(Frame " + frameArray[counter - 1].getFrameNo() + ")********");
            //TODO - is this correct or do we need to set up Frames here?
            //correlatorForTwoPointClouds.setupPointClouds(frameArray[counter - 1].pointCloudCorrelationRealWorld, frameArray[counter].pointCloudCorrelationRealWorld);
            correlatorForTwoPointClouds.setupFrames(frameArray[counter - 1], frameArray[counter]);
            if (counter == (noOfFramesToCorrelate - 1)) {
                thisIsTheLastOne = true;
            }

            myLogger.info("startAllFrameCorrelation "+ (counter-1) + ", "+counter+" thisIsTheLastOne = "+ thisIsTheLastOne );
            correlatorForTwoPointClouds.startCorrelate(thisIsTheLastOne, "full");
        }
    }

    /*private void stepCorrelate(){ //called directly on the correlator now.
        boolean isFinished = correlatorForTwoPointClouds.stepCorrelateReturnTrueWhenDone();
        if (isFinished) correlationIsInProgress = false;
    }*/

    /*public void testSetUp(){
        testStartRun = true; //Prepare to start auto correlating for performance test
        //The correlation is triggered in draw, like the others.
    }*/

    /*public void timeStartCorrelateGoing(char myChar){  //Expose this publicly as a test

        System.out.println("timeStartCorrelateGoing()");
        System.out.println("in timeStartCorrelateGoing, now ThreeDImageCreator = "+hashCode());
        myLogger.info("Starting test correlation");
        waitForButtonPressBeforeEachCorrelationStep = false;
        Long myStartTime = Defaults.getTimeInMS();
        startCorrelateGoing(myChar);
        Long myEndTime = Defaults.getTimeInMS();
        Float diffInSecs = (float) ((myEndTime-myStartTime)/1000);
        myLogger.info("");
        myLogger.info("===================================");
        myLogger.info("Time difference in seconds = "+diffInSecs);
        myLogger.info("===================================");
    }*/

    private void startCorrelateGoing(char typeOfCorrelation) {
        if (!correlationIsInProgress) {
            correlationIsInProgress = true;
            correlationStartTime = System.currentTimeMillis(); //Defaults.getTimeInMS();

            switch (typeOfCorrelation) {
                /*case 's':  //short
                    myLogger.info("Starting two frame correlation going");
                    //correlatorForTwoPointClouds.setupPointClouds(frameArray[0].pointCloudCorrelationRealWorld, frameArray[1].pointCloudCorrelationRealWorld);
                    //correlatorForTwoPointClouds.setupFrames(frameArray[0], frameArray[1]);  //This is already called from startTwoFrameCorrelation()
                    startTwoFrameCorrelation();
                    //correlatorForTwoPointClouds.startCorrelateGoing('s', true, 10, 5, 'intY');  //s = short, correlateColors, minScoreForGoodMatch, zMatrixPointIncrement, axis
                    break;*/
                case 'l': //long
                    myLogger.info("Starting multiple frame correlation going");
                    startAllFrameCorrelation();  //Still using the original method here.
                    break;
                //TODO - raise error on case else?
            }
        } else {
            myLogger.info("Tried to start correlation when correlation already in progress");
        }
    }
   /* void drawTimeline() {  //Shows which frame we're on.
        pushStyle();
        stroke(255, 255, 0);
        line(10, height - 20, width - 10, height - 20);
        stroke(0);
        rectMode(CENTER);
        fill(255, 255, 0);
        int pos = (int) ((width - 2 * 10) * (float) ni.curFramePlayer() / (float) ni.framesPlayer());
        rect(pos, height - 20, 7, 17);
        popStyle();
        popStyle();
    }*/

    public void keyPressed() {
        //TODO - simplify this line by moving the place where correlator is referenced to frame for example?
        CorrelatorForTwoPointClouds myCorrelator = frameArray[currentMovingFrame].getPointCloudCorrelationRealWorld().getCorrelatorForTwoPointClouds();
        switch (key) {
            case ' ':
                //if(isSliding){isSliding = false;} else {isSliding = true;} //This means next time draw() is called, it calls the next correlation slide.
                //getCorrelationScore(cloudArray[0], cloudArray[1]);//ni.setMirror(!ni.mirror());
                break;
            case 'a': //rotate around intY axis
                //cloudArray[1].rotateCloudAndSetupZPointPlane( -0.05f, 'intY');
                myCorrelator.rotateSecondCloud(-0.025f, 'y', true);
                correlateAfterManualMove();
                //PApplet.println("Correlation score = " + cloudArray[1].correlatorForTwoPointClouds.correlationScoreOnePosition(true));
                //yAngle += -0.05f;
                break;
            case 'x':
                //cloudArray[1].rotateCloudAndSetupZPointPlane( 0.05f, 'intY');
                myCorrelator.rotateSecondCloud(0.025f, 'y', true);
                correlateAfterManualMove();
                //PApplet.println("Correlation score = " + cloudArray[1].correlatorForTwoPointClouds.correlationScoreOnePosition(true));
                //yAngle += 0.05f;
                break;
            case 's':  //rotate around intX axis
                //cloudArray[1].rotateCloudAndSetupZPointPlane( 0.05f, 'z');
                myCorrelator.rotateSecondCloud(0.025f, 'z', true);
                correlateAfterManualMove();
                //PApplet.println("Correlation score = " + cloudArray[1].correlatorForTwoPointClouds.correlationScoreOnePosition(true));
                //xAngle += 0.05f;
                break;
            case 'z':
                //cloudArray[1].rotateCloudAndSetupZPointPlane( -0.05f, 'z');
                myCorrelator.rotateSecondCloud(-0.025f, 'z', true);
                correlateAfterManualMove();
                //PApplet.println("Correlation score = " + cloudArray[1].correlatorForTwoPointClouds.correlationScoreOnePosition(true));
                //xAngle += -0.05f;
                break;
            case 'd':
                myCorrelator.rotateSecondCloud(0.025f, 'x', true);
                correlateAfterManualMove();
                //PApplet.println("Correlation score = " + cloudArray[1].correlatorForTwoPointClouds.correlationScoreOnePosition(true));
                break;
            case 'c':
                myCorrelator.rotateSecondCloud(-0.025f, 'x', true);
                correlateAfterManualMove();
                //PApplet.println("Correlation score = " + cloudArray[1].correlatorForTwoPointClouds.correlationScoreOnePosition(true));
                break;
            case 'k': //shift up
                //cloudArray[1].shiftByPVector(new PVector(0, 10), true);
                myCorrelator.shiftSecondZMatrixByIntPVector(new IntPVector3D(0, 1, 0));
                correlateAfterManualMove();
                //PApplet.println("Correlation score = " + cloudArray[1].correlatorForTwoPointClouds.correlationScoreOnePosition(true));
                break;
            case ',': //shift down
                //cloudArray[1].shiftByPVector(new PVector(0, -10), true);
                myCorrelator.shiftSecondZMatrixByIntPVector(new IntPVector3D(0, -1, 0));
                correlateAfterManualMove();
                //PApplet.println("Correlation score = " + cloudArray[1].correlatorForTwoPointClouds.correlationScoreOnePosition(true));
                break;
            case 'm': //shift left
                //cloudArray[1].shiftByPVector(new PVector(-10, 0), true);
                myCorrelator.shiftSecondZMatrixByIntPVector(new IntPVector3D(-1, 0, 0));
                correlateAfterManualMove();
                //PApplet.println("Correlation score = " + cloudArray[1].correlatorForTwoPointClouds.correlationScoreOnePosition(true));
                break;
            case 'l': //shift right
                //cloudArray[1].shiftByPVector(new PVector(10, 0), true);
                myCorrelator.shiftSecondZMatrixByIntPVector(new IntPVector3D(1, 0, 0));
                correlateAfterManualMove();
                //PApplet.println("Correlation score = " + cloudArray[1].correlatorForTwoPointClouds.correlationScoreOnePosition(true));
                break;
            case 'j': //shift back
                myCorrelator.shiftSecondZMatrixByIntPVector(new IntPVector3D(0, 0, 1));
                correlateAfterManualMove();
                //PApplet.println("Correlation score = " + cloudArray[1].myComyCorrelatorForTwoPointCloudsrrelator.correlationScoreOnePosition(true));
                break;
            case 'n': //shift forward
                myCorrelator.shiftSecondZMatrixByIntPVector(new IntPVector3D(0, 0, -1));
                correlateAfterManualMove();
                //PApplet.println("Correlation score = " + cloudArray[1].correlatorForTwoPointClouds.correlationScoreOnePosition(true));
                break;
            case 'y': //Auto set shift and rotate
                myCorrelator.rotateSecondCloud(0.05f, 'x', true);
                myCorrelator.rotateSecondCloud(-1.225f, 'y', true);
                myCorrelator.rotateSecondCloud(0.15f, 'z', true);
                //myCorrelator.shiftSecondZMatrixByIntPVector(-25, -5, 4);
                correlateAfterManualMove();
                //PApplet.println("Correlation score = " + cloudArray[1].correlatorForTwoPointClouds.correlationScoreOnePosition(true));
                break;
            case '1': //correlate - start off step by step then pause
                //startAllFrameCorrelation();
                if (!correlationIsInProgress) {
                    drawEachStep = true;
                    waitForButtonPressBeforeEachCorrelationStep = true;
                    startCorrelateGoing('l');}
                break;
            case '2': //correlate - execute one step
                drawEachStep = true;
                if (correlationIsInProgress)
                    if (waitForButtonPressBeforeEachCorrelationStep) doACorrelationStep();
                break;
            case '3': //correlate - run continuously and time showing progress on chart.
                if (!correlationIsInProgress) {
                    waitForButtonPressBeforeEachCorrelationStep = false;
                    drawEachStep = true;
                    startCorrelateGoing('l');
                    //startCorrelateGoing('l');
                                    //startCorrelateGoing('s'); //s = short
                }
            case '4': //correlate - run continuously and time no chart updates.
                if (!correlationIsInProgress) {
                    waitForButtonPressBeforeEachCorrelationStep = false;
                    drawEachStep = false;
                    startCorrelateGoing('l');
                    //startCorrelateGoing('l');
                    //startCorrelateGoing('s'); //s = short
                }
                break;
            case '0': //switch display mode
                displayMode++;
                if (displayMode == 6) displayMode = 0;
                break;
            case 'p': //switch display mode
                writeFinalImageToFile(true, false);  //first parameter = (true for use all frames, false for frame 0) second parameter = (true to write shifted clouds, false to write final clouds)
                break;
            case 'f': //Freeze current frame, save and move to next.
                freezeFrameSaveAndMoveOntoNext();  //true for use all frames, false for frame 0
                break;
        }

        switch (keyCode) {
            case LEFT:
                // jump back
                //ni.seekPlayer(-3, SimpleOpenNI.PLAYER_SEEK_CUR);
                //ni.seekPlayer(40, SimpleOpenNI.PLAYER_SEEK_SET);
                rotY += 0.1f;
                break;
            case RIGHT:
                // jump forward
                //ni.seekPlayer(3, SimpleOpenNI.PLAYER_SEEK_CUR);
                //ni.seekPlayer(140, SimpleOpenNI.PLAYER_SEEK_SET);
                rotY -= 0.1f;
                break;
            case UP:
                if (keyEvent.isShiftDown())  //TODO - replace with keypressed() and imager.junit
                    zoomF += 0.02f;
                else
                    rotX += 0.1f;
                break;
            case DOWN:
                if (keyEvent.isShiftDown()) {
                    zoomF -= 0.02f;
                    if (zoomF < 0.01)
                        zoomF = (float) 0.01;
                } else
                    rotX -= 0.1f;
                break;

        }
        //delay(5000);
    }

    //To print 3D model
    //1) press key 'p' to export to 'Test.asc' file
    //2) open in MeshLab - use defaults (no row headings)

    //Reduce number of points
    //Filters > cleaning and repairing > Merge close vertices - use real world unit of 4 (5 seems very difficult to get pivoting ball working)

    //Filters > Remeshing, Simplification and Reconstruction
    //     Surface Reconstruction > Ball Pivoting -
    //set pivoting ball radius 10, clustering radius = 1% to reduce no of holes.  With merge close vertices = 4,
    //set pivoting ball radius 8, clustering radius = 1% to reduce no of holes.  With merge close vertices = 4,
    //3 then 5,1
    // nothing, then 9,20 - most holes closed, some smoothing over tip of nose - try lower resolution mesh.
    // nothing then 7,20/9,1 - still some small holes
    // nothing then 9,10 - pretty good - most holes closed, some smoothing over tip.
    // nothing then 9,1 - pretty good - few holes.  Could be higher definition though.
    // nothing then 7,1 - good resolution - more holes.  Can holes be fixed?

    // 3 then 8,1
    //2 then 7,1 works well but too detailed to import into 3d printer?
    //3 then 7,1
    // 5 then 9,1
    //Fix holes by
    //Filters > Remeshing, Simplification and Reconstruction > close holes

    //4) Show in solid rather than pointcloud view to show the difference

    //5) File > Export Mesh as >  3ds format.   NOTE 3ds, not STL if editing in BLENDER!   .STL FORMAT LOSES RESOLUTION
    //Edit in blender and save as .stl.
    //6) Open .stl file up in UP! software.


    //Create mesh with as few holes as possible

    // Filters > Remeshing, Simplification and Reconstruction > Simplification - MC Edge Collapse
    // Filters > Normals, curvature, orientation > reorient all faces coherently
    //TODO Alternatively could output directly as .stl format (series of triangles)
    // Maybe output directly from ZMatrix for relief carved type effect (no undercutting).

    public void writeFinalImageToFile(boolean writeAllFrames, boolean writeCorrelationCloudsInsteadOfFinalImage) {
        FileWriter myFileWriter = null;
        String myFileName = "Test.asc";
        myLogger.debug("Starting to print " + myFileName);
        try {
            myFileWriter = new FileWriter(myFileName);
            BufferedWriter myWriter = new BufferedWriter(myFileWriter);
            //myWriter.write();
            if (writeAllFrames) {
                for (int counter = 0; counter < noOfFramesToCorrelate; counter++) {  //0 to 9
                    //cloudArray[counter].writeAsciiFormatToFileWriter(myWriter, noOfFramesToCorrelate, writeCorrelationCloudsInsteadOfFinalImage);  //Second param is resolution.  1 is too fine.  30 too granular.//second parameter is resolution
                    frameArray[counter].getPointCloudFinalRealWorld().writeAsciiFormatToFileWriter(myWriter, noOfFramesToCorrelate);  //Second param is resolution.  1 is too fine.  30 too granular.//second parameter is resolution
                }
            } else {
                frameArray[0].getPointCloudFinalRealWorld().writeAsciiFormatToFileWriter(myWriter, noOfFramesToCorrelate);  //1 is too fine.  30 too granular.//second parameter is resolution
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (myFileWriter != null) {
                try {
                    myFileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        myLogger.debug("Finished Printing");
    }

    public void freezeFrameSaveAndMoveOntoNext() {
        //Save current frame
        //cloudArray[currentMovingFrame].saveCumulativeCorrelationOffsetsAsFinal();
        frameArray[currentMovingFrame].getPointCloudCorrelationRealWorld().saveCumulativeCorrelationOffsetsAsFinal();

        //Set the absolute final offsets to move the correlating image back to the original one.
        //cloudArray[currentMovingFrame].finalCorrelationRealWorldShiftToCloud0 = PVector.add(cloudArray[currentMovingFrame].finalCorrelationRealWorldShiftToNextCloud, new PVector());
        //cloudArray[currentMovingFrame].finalCorrelationRealWorldRotateToCloud0 = PVector.add(cloudArray[currentMovingFrame].finalCorrelationRealWorldRotateToNextCloud, new PVector());
        //cloudArray[currentMovingFrame].finalCorrelationRealWorldShiftToCloud0 = PVector.add(cloudArray[currentMovingFrame].finalCorrelationRealWorldShiftToNextCloud, cloudArray[currentMovingFrame-1].finalCorrelationRealWorldShiftToCloud0);
        //cloudArray[currentMovingFrame].finalCorrelationRealWorldRotateToCloud0 = PVector.add(cloudArray[currentMovingFrame].finalCorrelationRealWorldRotateToNextCloud, cloudArray[currentMovingFrame-1].finalCorrelationRealWorldRotateToCloud0);
        //NOW CHANGE THE FINAL IMAGE (final image may not actually be needed when doing it manually this way and setting everything to line up with the first frame.

        //cloudArray[currentMovingFrame].alignPointsInFinalImageToCloud0();
        //frameArray[currentMovingFrame].pointCloudCorrelationRealWorld.alignPointsInFinalImageToCloud0();
        //frameArray[currentMovingFrame].pointCloudFinalRealWorld.alignPointsInFinalImageToCloud0();
        frameArray[currentMovingFrame].alignPointsInFinalImageToCloud0();

        //Move to the next frame if we're not at the end.
        if (currentMovingFrame < (noOfFramesToCorrelate - 1)) {
            //cloudArray[currentMovingFrame].resetCumulativeCorrelationOffsetsAndCorrelationCloud();
            currentMovingFrame++;

            // correlatorForTwoPointClouds.setupPointClouds(cloudArray[currentMovingFrame - 1], cloudArray[currentMovingFrame]);
            correlatorForTwoPointClouds.setupFrames(frameArray[currentMovingFrame - 1], frameArray[currentMovingFrame]);
        }
    }

    public void point(PVector myPVector) {
        point(myPVector.x, myPVector.y, myPVector.z);
    }

    private void printFontList() {
        String[] fontList = PFont.list();
        System.out.println("BEFORE PRINTING FONT LIST");
        for (int i = 0; i < fontList.length; i++) {
            System.out.println(fontList[i]);
        }
        System.out.println("AFTER PRINTING FONT LIST");
    }

    private void testLogger() {
        //System.out.println("Fully qualified class name = " + ThreeDImageCreator.class.getName());
        myLogger.trace("John Trace");
        myLogger.debug("John Debug");
        myLogger.info("John Info");
        myLogger.warn("John Warn");
        myLogger.error("John Error");
    }

    private void doACorrelationStep() {
        correlationIsInProgress = !correlatorForTwoPointClouds.stepCorrelateReturnTrueWhenDone();
    }

}
