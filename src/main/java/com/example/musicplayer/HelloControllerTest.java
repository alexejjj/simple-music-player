package com.example.musicplayer;

import javafx.util.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HelloControllerTest {
    //HelloController control;

    @Before
    public void setUp() throws Exception {
        System.out.println("Before HelloControllerTest.class");
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("After HelloControllerTest.class");
    }

    @Test
    public void initialize() {
    }

    @Test
    public void selectItem() {
//        String pMusic = HelloController.selectItem();
//        assertEquals(pMusic, "MainLibrary");
    }

    @Test
    public void playMedia() {
    }

    @Test
    public void slashSubstringWithoutSlash() {
        int slashResult = HelloController.slashSubstring("Stephen Sanches - Until i found you");
        assertEquals(0, slashResult);
    }

    @Test
    public void slashSubstringWithSlash() {
        int slashResult = HelloController.slashSubstring("Stephen Sanches\\Until i found you");
        assertEquals(16, slashResult);
    }

    @Test
    public void getTime() throws Exception {
        String result = HelloController.getTime(new Duration(342000.0));
        assertEquals(result, "05:42");
    }

    @Test
    public void pauseMedia() {
//        controller.pauseMedia();
//        assertEquals(controller.isPlaying, false);
    }

    @Test
    public void playPause() {
    }

    @Test
    public void update() {
    }

    @Test
    public void testUpdate() {
    }

    @Test
    public void search() {
    }

    @Test
    public void addPlaylist() {
    }

    @Test
    public void importPlaylist() {
    }

    @Test
    public void beginTimer() {
    }

    @Test
    public void cancelTimer() {
    }

    @Test
    public void cycleTrack() {
    }
}