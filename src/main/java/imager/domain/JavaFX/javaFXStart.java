package imager.domain.JavaFX;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import imager.domain.Defaults;
import imager.domain.ThreeDImageCreator;

import java.io.File;
import java.util.Date;

public class javaFXStart extends Application {    //JavaFX.Application must always implement start method.
    public void start(Stage myStage){
        System.out.println("in myStage");
        new JavaFXStageChoosePlayOrRecordIt();
    }
}

class JavaFXStageChoosePlayOrRecordIt extends Stage{ //Split each window out into separate class to make it more flexible
    String myONIandLogDirectory = Defaults.getDefaultDirectoryForPlayRecordAndLogfile(); //"C:\\Users\\John\\Documents\\KinectData\\";
    //String myONIMostRecentFileNameWithFullPath = myONIandLogDirectory + "copyOfMostRecentFile.oni";
    String myONIRecordFileName = myONIandLogDirectory + "recording_" + (new Date()).getTime() + ".oni";
    String myLogFileNameWithPath = myONIandLogDirectory +"LogFile.txt";

    JavaFXStageChoosePlayOrRecordIt() {
        System.out.println("in javaFXStageChoosePlayOrRecordIt constructor");
        Scene scene = new Scene(new Group());
        setTitle("Play or Record");
        setWidth(300);
        setHeight(190);
        VBox vbox = new VBox();
        vbox.setLayoutX(20);
        vbox.setLayoutY(20);
        HBox hbox1 = new HBox();

        Button buttonRecord = new Button("Record");
        buttonRecord.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                close();
                recordFile();
            }
        });

        final Button buttonPlay = new Button("Play");
        buttonPlay.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                close();
                chooseFileAndPlayIt();
            }
        });

        hbox1.getChildren().add(buttonRecord);
        hbox1.getChildren().add(buttonPlay);
        //hbox1.getChildren().add(label);
        hbox1.setSpacing(10);
        hbox1.setAlignment(Pos.BOTTOM_CENTER);

        vbox.getChildren().add(hbox1);
        vbox.setSpacing(10);
        ((Group)scene.getRoot()).getChildren().add(vbox);

        setScene(scene);
        show();
    }

    private void chooseFileAndPlayIt(){
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a .ONI file to play back");
        fileChooser.setInitialDirectory(new File(myONIandLogDirectory));
        //fileChooser.setInitialFileName(myONIMostRecentFileNameWithFullPath);
        File myFile = fileChooser.showOpenDialog(this);
        if (myFile == null) {
            System.out.println("No file chosen - exiting");
        } else {
            System.out.println("JavaFXStageChoosePlayOrRecordIt.chooseFileAndPlayIt " + myFile.getAbsolutePath());
            playFile(myFile);
        }
    }

    private void playFile(File myFile){
        ThreeDImageCreator.main(new String[]{"True", myFile.getAbsolutePath(), myLogFileNameWithPath});
    }

    private void recordFile(){
        ThreeDImageCreator.main(new String[]{"False", myONIRecordFileName, myLogFileNameWithPath});
    }
}