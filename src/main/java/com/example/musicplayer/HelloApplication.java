package com.example.musicplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("player.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1100, 550);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        scene.getStylesheets().add("style.css");
        stage.show();
        }

public static void main(String[] args) {
        launch();
        }
        }