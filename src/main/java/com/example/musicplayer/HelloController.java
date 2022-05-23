package com.example.musicplayer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.FileFilter;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.File;



public class HelloController implements Initializable {


    @FXML
    private TreeView<String> treeView;

    @FXML
    private String[] filter = {"name", "newness", "weight"};


    String libraryRoot;
    TreeItem<String>[] fileList;
    private String currentMusic;
    MediaPlayer player;
    private boolean isPlaying;
    private String peakedMusic;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TreeItem<String> rootItem = new TreeItem<>("C:\\Users\\Admin\\IdeaProjects\\bdc-player\\Library\\");
        this.treeView.setRoot(rootItem);
        libraryRoot = "C:\\Users\\Admin\\IdeaProjects\\bdc-player\\Library\\";
        fileList = new TreeItem[directoryLister(libraryRoot).length];
        for (int i = 0; i < directoryLister(libraryRoot).length; i++) {
            fileList[i] = new TreeItem<>(directoryLister(libraryRoot)[i].getName());
            rootItem.getChildren().addAll(fileList[i]);
        }

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

// если не нажимая паузы переключить трек на другой и нажать пдэй, то будет пауза, трек воспроизведётся после второго нажатия на плэй
    // НЕ СТИРАТЬ

}