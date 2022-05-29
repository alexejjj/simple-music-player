package com.example.musicplayer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

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



    private int character;
    private String libraryPath = "C:\\Users\\thedi\\Desktop\\BDC musicplayer\\Library\\"; // поменять только тут
                                                                                                    // важно сохраниить \\
                                                                                                        // на конце
    @Override
    // ЕСЛИ ВЫ ЧТО-ТО МЕНЯЕТЕ В ЭТОМ МЕТОДЕ - ПРОСЬБА ДОБАВИТЬ ИЗМЕНЕНИЯ В
    // ЭТОТ ЖЕ МЕТОД НИЖЕ, МОЖНО CNTRL+F И ВБИТЬ "initialive", отсюда тупо скопировать то что вы поменяли и вставить туда
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addFile = new Button();
        myChoiceBox.getItems().addAll(filter);
        TreeItem<String> rootItem = new TreeItem<>("Library");
        this.treeView.setRoot(rootItem);
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


            songLabel.setText(currentMusicNew.substring(0,currentMusicNew.length()-13));
        }
    }

    public void pauseMedia(){
        this.cancelTimer();
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
        initialize(HelloApplication.class.getResource("player.fxml"));
    }
    @FXML
    public void initialize(URL url) {
        addFile = new Button();
        myChoiceBox.getItems().addAll(filter);
        TreeItem<String> rootItem = new TreeItem<>("Library");
        this.treeView.setRoot(rootItem);
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
        initialize(HelloApplication.class.getResource("player.fxml"));
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
       player.pause();
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
        List<TreeItem<String>> tracks = new ArrayList<>();
        Arrays.stream(fileList).map(t -> tracks.add(t));
        Collections.shuffle(tracks);
        for (int i = 0; i<tracks.size(); i++){
            fileList[i] = tracks.get(i);
        }
    }

// choosing another song while the current one is playing will lead to the one playing getting stopped.
// You should then therefore click on the song the second time.

}