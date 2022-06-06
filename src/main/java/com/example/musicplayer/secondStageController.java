package com.example.musicplayer;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;

public class secondStageController {
    @FXML
    TextField playlistName = new TextField();

    String currentUsersHomeDir = System.getProperty("user.home");

    public void createPlaylist() {
        String playlistName = this.playlistName.getText();
        File newPlaylist = new File(currentUsersHomeDir + "\\MusicPlayer\\Music\\" + playlistName + "\\");
        newPlaylist.mkdir();

    }


}
