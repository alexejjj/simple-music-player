package com.example.musicplayer;

import javafx.beans.binding.Bindings;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javafx.collections.ObservableList;
import org.apache.commons.lang3.time.StopWatch;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFileChooser;

import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.File;
import java.util.concurrent.Callable;


public class HelloController implements Initializable {

    @FXML
    private ProgressBar songProgressBar;
    private Timer timer;
    private TimerTask task;

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
    int songIndex;
    boolean isCycled;


    @FXML
    private Label songLabel;

    @FXML
    private ChoiceBox myChoiceBox;

    @FXML
    private Slider volumeSlider = new Slider();

    @FXML
    private Label labelCurrentTime;
    @FXML
    private Label labelTotalTime;

    private boolean atEndOfSong = false;

    private String libraryPath;
                                                                                                   // keep '\\' at the end of the path

    @Override
    // IN CASE YOU MAKE ANY CHANGES IN THIS METHOD, PLEASE ADD ALL OF THEM
    // TO THE SAME METHOD BELOW, USING CTRL+F "Initialize" AND BY COPYING AND PASTING ALL THE CHANGES MADE
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addFile = new Button();
        myChoiceBox.getItems().addAll(filter);
        TreeItem<String> rootItem = new TreeItem<>("Library");
        this.treeView.setRoot(rootItem);
        String filePath = null;
        JFileChooser file = new JFileChooser();
        file.setMultiSelectionEnabled(true);
        file.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        file.setFileHidingEnabled(false);
        if (file.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            java.io.File f = file.getSelectedFile();
            filePath = f.getPath();
        }
        this.libraryPath = filePath + "\\";
        libraryRoot = libraryPath;
        fileList = new TreeItem[directoryLister(libraryRoot).length];
        for (int i = 0; i < directoryLister(libraryRoot).length; i++) {
            fileList[i] = new TreeItem<>(directoryLister(libraryRoot)[i].getName());
            rootItem.getChildren().addAll(fileList[i]);
        }
        rootItem.setExpanded(true);
        volumeSlider.valueProperty().addListener((ObservableValue<? extends Number> observableValue, Number number, Number t1) -> {
            player.setVolume(volumeSlider.getValue() * 0.01);
        });


    }


    private void bindCurrentTimeLabel(){  //showing the time of a song elapsed
        labelCurrentTime.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getTime(player.getCurrentTime());
            }
        }, player.currentTimeProperty()));
    }


    public String getTime(Duration time){

        int minutes = (int) time.toMinutes();
        int seconds = (int) time.toSeconds();

        //we don't want to go to 61 seconds, thus this if statement:

        if (seconds > 59) seconds %= 60;  //if it's 61, it'll return 1 etc.
        if (minutes > 59) minutes %= 60;

        return String.format("%02d:%02d", minutes, seconds);

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
        this.beginTimer();
        if (player != null && peakedMusic.equals(currentMusic)){
            player.play();
        } else {
            currentMusic = peakedMusic;
            File f = new File(libraryRoot + currentMusic);
            URI u = f.toURI();
            Media pick = new Media(u.toString()); //throws here
            player = new MediaPlayer(pick);
            if (isCycled) {
                isCycled = false;
                cycleTrack();
            }
            player.play();

            String currentMusicNew = currentMusic.replace("_", " ");


            player.totalDurationProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observableValue, Duration oldDuration, Duration newDuration) {
                    labelTotalTime.setText(getTime(newDuration));
                }
            });

            bindCurrentTimeLabel();


            songLabel.setText(currentMusicNew.substring(0,currentMusicNew.length()-13));
        }
    }

    public void pauseMedia(){
        this.cancelTimer();
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

    public void deleteFile() {
        File file = new File(libraryPath+peakedMusic);
        if(file.delete()){
            System.out.println(peakedMusic + " file deleted");
        }else System.out.println(peakedMusic + " file not found");
        upadate();
    }


    @FXML
    TextField textAreaSearch = new TextField();

    public void upadate() {
        TreeItem<String> rootItem = new TreeItem<>("Library");
        fileList = new TreeItem[directoryLister(libraryRoot).length];
        this.treeView.setRoot(rootItem);
        for (int i = 0; i < directoryLister(libraryRoot).length; i++) {
            fileList[i] = new TreeItem<>(directoryLister(libraryRoot)[i].getName());
            rootItem.getChildren().addAll(fileList[i]);
        }
        rootItem.setExpanded(true);
    }
    public void upadate(String keyWord) {
        TreeItem<String> rootItem = new TreeItem<>("Library");
        fileList = new TreeItem[directoryLister(libraryRoot).length];
        this.treeView.setRoot(rootItem);
        for (int i = 0; i < directoryLister(libraryRoot).length; i++) {
            if (directoryLister(libraryRoot)[i].getName().contains(keyWord)) {
                fileList[i] = new TreeItem<>(directoryLister(libraryRoot)[i].getName());
                rootItem.getChildren().addAll(fileList[i]);
            }
        }
        rootItem.setExpanded(true);
    }

    public void search() {
        String searchRequest = textAreaSearch.getText();
        upadate(searchRequest);
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
        upadate();
    }

    private static void moveFile(String src, String dest ) {
        Path result = null;
        try {
            result =  Files.move(Paths.get(src), Paths.get(dest));
        } catch (IOException e) {}
    }

    public void beginTimer(){

        timer = new Timer();
        task = new TimerTask() {

            @Override
            public void run() {
                isPlaying = true;
                double current = player.getCurrentTime().toSeconds();
                double end = player.getTotalDuration().toSeconds();
                songProgressBar.setProgress(current/end);

                if(current/end == 1){
                    cancelTimer();
                }
            }
        };
        timer.scheduleAtFixedRate(task,1000,1000);
    }

    public void cancelTimer(){
        isPlaying = false;
    }

    public void playPrevious() {
        int currentIndex = Arrays.stream(fileList).map(TreeItem::getValue).toList().indexOf(currentMusic);
        if (currentIndex < 1) {
            pauseMedia();
            player.seek(new Duration(0.0));
            playMedia();
            return;
        }
        peakedMusic = fileList[currentIndex - 1].getValue();
        pauseMedia();
        playMedia();
    }

    public void playNext() {
        int currentIndex = Arrays.stream(fileList).map(TreeItem::getValue).toList().indexOf(currentMusic);
        if (currentIndex == fileList.length - 1) {
            pauseMedia();
            player.seek(new Duration(0.0));
            playMedia();
            return;
        }
        peakedMusic = fileList[currentIndex + 1].getValue();
        pauseMedia();
        playMedia();
    }

    public void cycleTrack() {
        if (isCycled) {
            isCycled = false;
            player.cycleCountProperty().set(0);
        } else {
            player.cycleCountProperty().set(Integer.MAX_VALUE);
            isCycled = true;
        }
    }

    public void shuffleTrack() {
        Random rnd = ThreadLocalRandom.current();
        for (int i = fileList.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            TreeItem<String> a = fileList[index];
            fileList[index] = fileList[i];
            fileList[i] = a;
        }
        List<TreeItem<String>> tracksList = treeView.getRoot().getChildren();
        for(int i = 0; i < tracksList.size(); i++) {
            tracksList.set(i, fileList[i]);
        }
    }

// choosing another song while the current one is playing will lead to the one playing getting stopped.
// You should then therefore click on the song the second time.

}