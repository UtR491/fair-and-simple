package controllers;

import javafx.scene.image.ImageView;
import main.GuiUtil;

import java.net.DatagramSocket;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class VideoReader implements Runnable {

    private final DatagramSocket getVideoFeedSocket;
    private final List<ImageView> imageViewHolder;
    private Map<Integer, ImageView> registrationNumberImageWindowMap;
    private Queue<Integer> studentsOnDisplay;

    public VideoReader(DatagramSocket getVideoFeedSocket, List<ImageView> imageViewHolder, Map<Integer, ImageView> registrationNumberImageWindowMap, Queue<Integer> studentsOnDisplay) {
        this.getVideoFeedSocket = getVideoFeedSocket;
        this.imageViewHolder = imageViewHolder;
        this.registrationNumberImageWindowMap = registrationNumberImageWindowMap;
        this.studentsOnDisplay = studentsOnDisplay;
    }

    @Override
    public void run() {
        while(true) {

        }
    }

    public void setRegistrationNumberImageWindowMap(Map<Integer, ImageView> registrationNumberImageWindowMap) {
        this.registrationNumberImageWindowMap = registrationNumberImageWindowMap;
    }

    public void setStudentsOnDisplay(Queue<Integer> studentsOnDisplay) {
        this.studentsOnDisplay = studentsOnDisplay;
    }
}
