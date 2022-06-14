package com.example.musicplayer;

import javafx.beans.binding.Bindings;

import java.awt.event.MouseEvent;
import java.beans.EventHandler;
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
    private MediaPlayer player;
    private boolean isPlaying;
    private String peakedMusic;
    private int songIndex;
    private boolean isCycled;
    private boolean isShuffled;
    private String tempRootFolder;
    private String playPath;


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
    private Map<TreeItem<String>, String> treeItemToPath = new LinkedHashMap<>();


    String currentUsersHomeDir = System.getProperty("user.home");

    /**
     * Метод для запуска программы
     * @param url
     * @param resourceBundle
     */
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
        playPath = currentUsersHomeDir + "\\MusicPlayer\\Music\\";

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
                treeItemToPath.put(fileList[i][j], directoryLister(tempRootFolder)[j].getPath().substring(filePath.length()));
            }
        }
        rootItem.getChildren().addAll(folderList);
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

    /**
     *Метод для выбора трека
     */
    public void selectItem() {
        TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();
        if (item != null) {
            System.out.println(treeItemToPath);
            System.out.println(item.getValue());
            System.out.println(item);
            peakedMusic = treeItemToPath.get(item);
            System.out.println(" PEAKED: " + peakedMusic);
            System.out.println(treeItemToPath);
        }
    }
// fix

    /**
     *Метод для запуска трека
     */
    public void playMedia() {
        this.beginTimer();
        if (player != null && peakedMusic.equals(currentMusic)) {
            player.play();
        } else {
            currentMusic = peakedMusic;
            File f = new File(playPath + currentMusic);
            URI u = f.toURI();
            System.out.println(f);
            System.out.println(u);
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

            songLabel.setText(currentMusicNew.substring(slashSubstring(currentMusicNew), currentMusicNew.length() - 13));
        }
    }

    /**
     *Метод, который возвращает место первого слэша в названии песни
     * @param songName название трека
     * @return место первого слэша в названии
     */
    public static int slashSubstring(String songName) {
        int slash = 0;
        for (int i = 0; i < songName.length(); i++) {
            if (songName.charAt(i) == '\\') {
                slash = i + 1;
                break;
            }
        }
        return slash;
    }


    private void bindCurrentTimeLabel() {  //showing the time of a song elapsed
        labelCurrentTime.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getTime(player.getCurrentTime());
            }
        }, player.currentTimeProperty()));
    }


    /**
     * Метод, который возвращает отформатированное время
     * @param time время, которое надо перевести в строку
     * @return отформатированную строку
     */
    public static String getTime(Duration time) {

        int minutes = (int) time.toMinutes();
        int seconds = (int) time.toSeconds();

        //considering the fact that there's only 60 sec/min

        if (seconds > 59) seconds %= 60;  //if it's 61, it'll return 1 etc.
        if (minutes > 59) minutes %= 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     *Метод для остновки проигрывания трека
     */
    public void pauseMedia() {
        this.cancelTimer();
        player.pause();
    }

    /**
     *Метод для кнопки играть/стоп
     */
    public void playPause() {
        if (isPlaying) {
            pauseMedia();
            isPlaying = false;
        } else {
            playMedia();
            isPlaying = true;
        }
    }

    /**
     *Метод для обновления музыкальной базы
     */
    public void update() {
        this.rootPath = currentUsersHomeDir + "\\MusicPlayer\\Music\\";
        rootFolder = rootPath;
        TreeItem<String> rootItem = new TreeItem<>("Music");
        this.treeView.setRoot(rootItem);
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
    }

    /**
     * Метод для обновления музыкальной базы по фильтру
     * @param keyWord ключевое слово
     */
    public void update(String keyWord) {
        this.rootPath = currentUsersHomeDir + "\\MusicPlayer\\Music\\";
        rootFolder = rootPath;
        TreeItem<String> rootItem = new TreeItem<>("Music");
        this.treeView.setRoot(rootItem);
        fileList = new TreeItem[directoryLister(rootFolder).length][];
        folderList = new TreeItem[directoryLister(rootFolder).length];
        for (int i = 0; i < directoryLister(rootFolder).length; i++) {
            folderList[i] = new TreeItem<>(directoryLister(rootFolder)[i].getName());
//            System.out.println(rootFolder + directoryLister(rootFolder)[i].getName() + "\\");
//            System.out.println(folderList[i].toString());
            tempRootFolder = rootFolder + directoryLister(rootFolder)[i].getName() + "\\";
            fileList[i] = new TreeItem[directoryLister(tempRootFolder).length];
            for (int j = 0; j < directoryLister(tempRootFolder).length; j++) {
                if (directoryLister(tempRootFolder)[j].getName().contains(keyWord)) {
                    fileList[i][j] = new TreeItem<>(directoryLister(tempRootFolder)[j].getName());
                    folderList[i].getChildren().addAll(fileList[i][j]);
                }
            }
        }
        rootItem.getChildren().addAll(folderList);
        rootItem.setExpanded(true);
    }

    @FXML
    TextField textAreaSearch = new TextField();

    /**
     *Метод для поиска
     */
    public void search() {
        String searchRequest = textAreaSearch.getText();
        update(searchRequest);
    }

    /**
     *Метод для добавления файла
     */
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
        moveFile(filePath, rootPath + "//" + peakedMusic + "//" + filePath.substring(filePath.lastIndexOf("\\") + 1));
        update();
    }

    /**
     *Метод для удаления файла
     */
    public void deleteFile() {
        currentMusic = peakedMusic;
        File file = new File(playPath + currentMusic + "\\");
        if (file.delete()) {
            System.out.println(peakedMusic + " file deleted");
        }
        update();
    }

    private static void moveFile(String src, String dest) {
        Path result = null;
        try {
            result = Files.move(Paths.get(src), Paths.get(dest));
        } catch (IOException e) {
        }
    }

    /**
     * Метод для добавления плейлиста
     * @throws IOException
     */
    public void addPlaylist() throws IOException {
        FXMLLoader fxmlLoader1 = new FXMLLoader(HelloApplication.class.getResource("playlist.fxml"));
        Scene secondScene = new Scene(fxmlLoader1.load());
        Stage stagePlaylist = new Stage();
        stagePlaylist.setScene(secondScene);
        stagePlaylist.show();
    }

    /**
     *Метод для импорта плейлиста
     */
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
        update();
    }


    /**
     *Метод для начала таймера
     */
    public void beginTimer() {

        timer = new Timer();
        task = new TimerTask() {

            @Override
            public void run() {
                isPlaying = true;
                double current = player.getCurrentTime().toSeconds();
                double end = player.getTotalDuration().toSeconds();
                songProgressBar.setProgress(current / end);

                if (current / end == 1) {
                    cancelTimer();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    /**
     *Метод для остановки таймера
     */
    public void cancelTimer() {
        isPlaying = false;
    }

    /**
     * Метод для проигрывания предыдущего трека
     */
    public void playPrevious() {
        List<String> playlistTracks = getPlaylistItemsPaths(peakedMusic);
        int currentIndex = playlistTracks.indexOf(peakedMusic);
        if (currentIndex != 0) {
            peakedMusic = playlistTracks.get(currentIndex - 1);
        }
        pauseMedia();
        playMedia();
    }

    /**
     * Метод для получения пути плейлиста
     * @param musicName название файла
     * @return путь плейлиста
     */
    public List<String> getPlaylistItemsPaths(String musicName) {
        int musicNameStartWithIndex = musicName.lastIndexOf('\\');
        String playlistTitlePath = musicName.substring(0, musicNameStartWithIndex);
        List<String> playListItemsPaths = treeItemToPath.values()
                .stream().sequential()
                .filter(i -> i.contains(playlistTitlePath))
                .toList();

        playListItemsPaths = new ArrayList<>(playListItemsPaths);

        if (isShuffled) {
            Collections.shuffle(playListItemsPaths);
        }

        return playListItemsPaths;
    }


    /**
     * Метод для проигрывания следующего трека
     */
    public void playNext() {
        List<String> playlistTracks = getPlaylistItemsPaths(peakedMusic);
        int currentIndex = playlistTracks.indexOf(peakedMusic);
        if (currentIndex != playlistTracks.size() - 1) {
            peakedMusic = playlistTracks.get(currentIndex + 1);
        }
        pauseMedia();
        playMedia();
    }

    /**
     * Метод для зацикливания трека
     */
    public void cycleTrack() {
        if (isCycled) {
            isCycled = false;
            player.cycleCountProperty().set(0);
        } else {
            player.cycleCountProperty().set(Integer.MAX_VALUE);
            isCycled = true;
        }
    }

    /**
     * Метод для перемешивания треков
     */
    public void shuffleTrack() {
        isShuffled = !isShuffled;
    }

// choosing another song while the current one is playing will lead to the one playing getting stopped.
// You should then therefore click on the song the second time.

}