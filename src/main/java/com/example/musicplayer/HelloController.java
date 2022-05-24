package com.example.musicplayer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.swing.JFileChooser;

import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


public class HelloController implements Initializable {

    //@FXML
    //private Slider songProgressBar;

    @FXML
    private Button addFile;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private String[] filter = {"name", "newness", "weight"};

    @FXML
    String libraryRoot;
    TreeItem<String>[] fileList;
    private String currentMusic;
    MediaPlayer player;
    private boolean isPlaying;
    private String peakedMusic;

    //@FXML
    //private ProgressBar progressBar;

    @FXML
    private Label songLabel;

    @FXML
    private ChoiceBox myChoiceBox;

    @FXML
    private Slider volumeSlider = new Slider();

    @FXML
    private ScrollBar scrollBar;



    private int character; // СТОЙ СЕК
    private String libraryPath = "C:\\Users\\thedi\\Desktop\\123\\Library\\";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addFile = new Button();
        myChoiceBox.getItems().addAll(filter);
        TreeItem<String> rootItem = new TreeItem<>("Library");
        this.treeView.setRoot(rootItem);
        libraryRoot = "C:\\Users\\thedi\\Desktop\\123\\Library\\";
        fileList = new TreeItem[directoryLister(libraryRoot).length];
        for (int i = 0; i < directoryLister(libraryRoot).length; i++) {
            fileList[i] = new TreeItem<>(directoryLister(libraryRoot)[i].getName());
            rootItem.getChildren().addAll(fileList[i]);
        }

        volumeSlider.valueProperty().addListener((ObservableValue<? extends Number> observableValue, Number number, Number t1) -> {
            player.setVolume(volumeSlider.getValue() * 0.01);
        });


    }

    private File[] directoryLister(String root) {
        File rootDir = new File(root);
        File[] fileList = rootDir.listFiles();
        return fileList;
    }


    public void selectItem() {
        TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();
        if(item != null) {
            System.out.println(item.getValue());
            peakedMusic = item.getValue();
        }
    }

    public void playMedia(){
        if (player != null && peakedMusic.equals(currentMusic)){
            player.play();
        } else {
            currentMusic = peakedMusic;
            File f = new File(libraryRoot + currentMusic);
            URI u = f.toURI();
            Media pick = new Media(u.toString()); //throws here
            player = new MediaPlayer(pick);
            player.play();

            String currentMusicNew = currentMusic.replace("_", " ");


            songLabel.setText(currentMusicNew.substring(0,currentMusicNew.length()-13));
        }
    }

    public void pauseMedia(){
        player.pause();
    }

    public void playPause() {
        if (isPlaying) {
            pauseMedia();
            isPlaying = false;
        } else {
            playMedia();
            isPlaying = true;
        }
    }


    public void addFile() {
        String filePath = null;
        JFileChooser file = new JFileChooser();
        file.setMultiSelectionEnabled(true);
        file.setFileSelectionMode(JFileChooser.FILES_ONLY);
        file.setFileHidingEnabled(false);
        if (file.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            java.io.File f = file.getSelectedFile();
            filePath = f.getPath();
        }
        moveFile(filePath, libraryPath + filePath.substring(filePath.lastIndexOf("\\") + 1));
    }
    private static void moveFile(String src, String dest ) {
        Path result = null;
        try {
            result =  Files.move(Paths.get(src), Paths.get(dest));
        } catch (IOException e) {}
    }



// choosing another song while the current one is playing will lead to the one playing getting stopped.
// You should then therefore click on the song the second time.

}