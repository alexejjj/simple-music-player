module com.example.musicplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;
    requires org.apache.commons.lang3;
    requires junit;
    requires org.testng;


    opens com.example.musicplayer to javafx.fxml;
    exports com.example.musicplayer;
}
