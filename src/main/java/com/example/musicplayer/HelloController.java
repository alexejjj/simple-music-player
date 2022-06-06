package com.example.musicplayer;

import javafx.beans.binding.Bindings;

import java.util.concurrent.ThreadLocalRandom;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.JFileChooser;

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
    String rootFolder;
    TreeItem<String>[][] fileList;
    TreeItem<String>[] folderList;
    private String currentMusic;
    MediaPlayer player;
    private boolean isPlaying;
    private String peakedMusic;
    int songIndex;
    boolean isCycled;
    String tempRootFolder;
    String playPath;


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

    private String rootPath;
    private boolean existResourceFolder = false;


    String currentUsersHomeDir = System.getProperty("user.home");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //addFile = new Button();
        //myChoiceBox.getItems().addAll(filter);
        TreeItem<String> rootItem = new TreeItem<>("Music");
        this.treeView.setRoot(rootItem);
        String filePath = null;
        System.out.println(currentUsersHomeDir);
        for (int i = 0; i < directoryLister(currentUsersHomeDir).length; i++) {
            if (directoryLister(currentUsersHomeDir)[i].equals("MusicPlayer")) {
                 existResourceFolder = true;
                 break;
            }
        }
        if (!existResourceFolder) {
            File rootDirecory = new File(currentUsersHomeDir + "\\MusicPlayer\\");
            rootDirecory.mkdir();
            File rootMusicDirectory = new File(currentUsersHomeDir + "\\MusicPlayer\\Music\\");
            rootMusicDirectory.mkdir();
            File mainLibrary = new File(currentUsersHomeDir + "\\MusicPlayer\\Music\\MainLibrary\\");
            mainLibrary.mkdir();
        }
        filePath = currentUsersHomeDir + "\\MusicPlayer\\Music\\";
        playPath = currentUsersHomeDir + "\\MusicPlayer\\Music\\MainLibrary\\";

//        JFileChooser file = new JFileChooser();
//        file.setMultiSelectionEnabled(true);
//        file.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        file.setFileHidingEnabled(false);
//        if (file.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//            java.io.File f = file.getSelectedFile();
//            filePath = f.getPath();
//        }


        this.rootPath = filePath;
        rootFolder = rootPath;
        fileList = new TreeItem[directoryLister(rootFolder).length][];
        folderList = new TreeItem[directoryLister(rootFolder).length];
        for (int i = 0; i < directoryLister(rootFolder).length; i++) {
            folderList[i] = new TreeItem<>(directoryLister(rootFolder)[i].getName());
//            System.out.println(rootFolder + directoryLister(rootFolder)[i].getName() + "\\");
//            System.out.println(folderList[i].toString());
            tempRootFolder = rootFolder + directoryLister(rootFolder)[i].getName() + "\\";
            fileList[i] = new TreeItem[directoryLister(tempRootFolder).length];
            for (int j = 0; j < directoryLister(tempRootFolder).length; j++) {
                fileList[i][j] = new TreeItem<>(directoryLister(tempRootFolder)[j].getName());
                folderList[i].getChildren().addAll(fileList[i][j]);
            }
        }
        rootItem.getChildren().addAll(folderList);
        rootItem.setExpanded(true);
        //volumeSlider.valueProperty().addListener((ObservableValue<? extends Number> observableValue, Number number, Number t1) -> {
        //  player.setVolume(volumeSlider.getValue() * 0.01);
        //});


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


//    private void bindCurrentTimeLabel(){  //showing the time of a song elapsed
//        labelCurrentTime.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                return getTime(player.getCurrentTime());
//            }
//        }, player.currentTimeProperty()));
//    }
//
//
//    public String getTime(Duration time){
//
//        int minutes = (int) time.toMinutes();
//        int seconds = (int) time.toSeconds();
//
//        //we don't want to go to 61 seconds, thus this if statement:
//
//        if (seconds > 59) seconds %= 60;  //if it's 61, it'll return 1 etc.
//        if (minutes > 59) minutes %= 60;
//
//        return String.format("%02d:%02d", minutes, seconds);
//
//    }
//
    public void playMedia(){
//        this.beginTimer();
        if (player != null && peakedMusic.equals(currentMusic)){
            player.play();
        } else {
            currentMusic = peakedMusic;
            File f = new File(playPath + currentMusic);
            URI u = f.toURI();
            Media pick = new Media(u.toString()); //throws here
            player = new MediaPlayer(pick);
//            if (isCycled) {
//                isCycled = false;
//                cycleTrack();
//            }
            player.play();
//
//            String currentMusicNew = currentMusic.replace("_", " ");
//
//
//            player.totalDurationProperty().addListener(new ChangeListener<Duration>() {
//                @Override
//                public void changed(ObservableValue<? extends Duration> observableValue, Duration oldDuration, Duration newDuration) {
//                    labelTotalTime.setText(getTime(newDuration));
//                }
//            });
//
//            bindCurrentTimeLabel();
//
//
//            songLabel.setText(currentMusicNew.substring(0,currentMusicNew.length()-13));
        }
    }

    public void pauseMedia(){
//        this.cancelTimer();
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
//
//    @FXML
//    TextField textAreaSearch = new TextField();
//
    public void upadate() {
        this.rootPath = currentUsersHomeDir + "\\MusicPlayer\\Music\\";
        rootFolder = rootPath;
        TreeItem<String> rootItem = new TreeItem<>("Music");
        this.treeView.setRoot(rootItem);
        fileList = new TreeItem[directoryLister(rootFolder).length][];
        folderList = new TreeItem[directoryLister(rootFolder).length];
        for (int i = 0; i < directoryLister(rootFolder).length; i++) {
            folderList[i] = new TreeItem<>(directoryLister(rootFolder)[i].getName());
            System.out.println(rootFolder + directoryLister(rootFolder)[i].getName() + "\\");
            System.out.println(folderList[i].toString());
            tempRootFolder = rootFolder + directoryLister(rootFolder)[i].getName() + "\\";
            fileList[i] = new TreeItem[directoryLister(tempRootFolder).length];
            for (int j = 0; j < directoryLister(tempRootFolder).length; j++) {
                fileList[i][j] = new TreeItem<>(directoryLister(tempRootFolder)[j].getName());
                folderList[i].getChildren().addAll(fileList[i][j]);
            }
        }
        rootItem.getChildren().addAll(folderList);
        rootItem.setExpanded(true);
    }
//    public void upadate(String keyWord) {
//        TreeItem<String> rootItem = new TreeItem<>("Library");
//        fileList = new TreeItem[directoryLister(rootFolder).length];
//        this.treeView.setRoot(rootItem);
//        for (int i = 0; i < directoryLister(rootFolder).length; i++) {
//            if (directoryLister(rootFolder)[i].getName().contains(keyWord)) {
//                fileList[i] = new TreeItem<>(directoryLister(rootFolder)[i].getName());
//                rootItem.getChildren().addAll(fileList[i]);
//            }
//        }
//        rootItem.setExpanded(true);
//    }
//
//    public void search() {
//        String searchRequest = textAreaSearch.getText();
//        upadate(searchRequest);
//    }
//
//    public void addFile() {
//        String filePath = null;
//        JFileChooser file = new JFileChooser();
//        file.setMultiSelectionEnabled(true);
//        file.setFileSelectionMode(JFileChooser.FILES_ONLY);
//        file.setFileHidingEnabled(false);
//        if (file.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//            java.io.File f = file.getSelectedFile();
//            filePath = f.getPath();
//        }
//        moveFile(filePath, rootPath + filePath.substring(filePath.lastIndexOf("\\") + 1));
//        upadate();
//    }
//
//    public void deleteFile() {
//        File file = new File(rootPath +peakedMusic);
//        if(file.delete()){
//            System.out.println(peakedMusic + " file deleted");
//        }else System.out.println(peakedMusic + " file not found");
//        upadate();
//    }
//
    private static void moveFile(String src, String dest ) {
        Path result = null;
        try {
            result =  Files.move(Paths.get(src), Paths.get(dest));
        } catch (IOException e) {}
    }


    public void addPlaylist() throws IOException {
        FXMLLoader fxmlLoader1 = new FXMLLoader(HelloApplication.class.getResource("playlist.fxml"));
        Scene secondScene = new Scene(fxmlLoader1.load());
        Stage stagePlaylist = new Stage();
        stagePlaylist.setScene(secondScene);
        stagePlaylist.show();
    }

    public void importPlaylist() {
        JFileChooser file = new JFileChooser();
        file.setMultiSelectionEnabled(true);
        file.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        file.setFileHidingEnabled(false);
        String playlistPath = null;
        if (file.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            java.io.File f = file.getSelectedFile();
            playlistPath = f.getPath();
        }
        moveFile(playlistPath, rootPath + playlistPath.substring(playlistPath.lastIndexOf("\\") + 1));
        upadate();
    }

//    public void beginTimer(){
//
//        timer = new Timer();
//        task = new TimerTask() {
//
//            @Override
//            public void run() {
//                isPlaying = true;
//                double current = player.getCurrentTime().toSeconds();
//                double end = player.getTotalDuration().toSeconds();
//                songProgressBar.setProgress(current/end);
//
//                if(current/end == 1){
//                    cancelTimer();
//                }
//            }
//        };
//        timer.scheduleAtFixedRate(task,1000,1000);
//    }
//
//    public void cancelTimer(){
//        isPlaying = false;
//    }
//
//    public void playPrevious() {
//        int currentIndex = Arrays.stream(fileList).map(TreeItem::getValue).toList().indexOf(currentMusic);
//        if (currentIndex < 1) {
//            pauseMedia();
//            player.seek(new Duration(0.0));
//            playMedia();
//            return;
//        }
//        peakedMusic = fileList[currentIndex - 1].getValue();
//        pauseMedia();
//        playMedia();
//    }
//
//    public void playNext() {
//        int currentIndex = Arrays.stream(fileList).map(TreeItem::getValue).toList().indexOf(currentMusic);
//        if (currentIndex == fileList.length - 1) {
//            pauseMedia();
//            player.seek(new Duration(0.0));
//            playMedia();
//            return;
//        }
//        peakedMusic = fileList[currentIndex + 1].getValue();
//        pauseMedia();
//        playMedia();
//    }
//
//    public void cycleTrack() {
//        if (isCycled) {
//            isCycled = false;
//            player.cycleCountProperty().set(0);
//        } else {
//            player.cycleCountProperty().set(Integer.MAX_VALUE);
//            isCycled = true;
//        }
//    }
//
//    public void shuffleTrack() {
//        Random rnd = ThreadLocalRandom.current();
//        for (int i = fileList.length - 1; i > 0; i--)
//        {
//            int index = rnd.nextInt(i + 1);
//            TreeItem<String> a = fileList[index];
//            fileList[index] = fileList[i];
//            fileList[i] = a;
//        }
//        List<TreeItem<String>> tracksList = treeView.getRoot().getChildren();
//        for(int i = 0; i < tracksList.size(); i++) {
//            tracksList.set(i, fileList[i]);
//        }
//    }

// choosing another song while the current one is playing will lead to the one playing getting stopped.
// You should then therefore click on the song the second time.

}