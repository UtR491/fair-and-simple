package controllers;

import entity.Student;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import main.UdpUtil;

import java.awt.image.BufferedImage;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

public class ProctorController {
    @FXML
    public GridPane studentVideoGridPane;
    @FXML
    public ListView<Student> allStudentsListView;
    @FXML
    public ImageView topLeftImageView;
    @FXML
    public ImageView topRightImageView;
    @FXML
    public ImageView bottomLeftImageView;
    @FXML
    public ImageView bottomRightImageView;

    public List<ImageView> imageViewHolder;
    private DatagramSocket getVideoFeedSocket = null;
    private DatagramPacket receivedSnapshot = null;
    private Queue<Integer> studentsOnDisplay = null;
    private Map<Integer, ImageView> registrationNumberImageWindowMap = null;

    public void callFirst(DatagramSocket videoFeedSocket, List<Student> students) {

        getVideoFeedSocket = videoFeedSocket;

        imageViewHolder = new ArrayList<>(Arrays.asList(topLeftImageView, topRightImageView, bottomLeftImageView, bottomRightImageView));

        allStudentsListView.setCellFactory(param -> new ListCell<Student>() {
            @Override
            protected void updateItem(Student item, boolean empty) {
                super.updateItem(item, empty);
                if(empty || item == null) setText(null);
                else setText(item.getName() + " - " + item.getRegistrationNumber());
            }
        });
        studentsOnDisplay = new LinkedList<>();
        allStudentsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() == 2) {
                    boolean alreadyOnDisplay = false;
                    for(Integer s : studentsOnDisplay) {
                        if(allStudentsListView.getSelectionModel().getSelectedItem().getRegistrationNumber() == s) {
                            alreadyOnDisplay = true;
                            break;
                        }
                    }
                    if(!alreadyOnDisplay) {
                        if(studentsOnDisplay.size() == 4) {
                            replaceStudent(allStudentsListView.getSelectionModel().getSelectedItem().getRegistrationNumber(),
                                    studentsOnDisplay.poll());
                        } else {
                            registrationNumberImageWindowMap.put(
                                    allStudentsListView.getSelectionModel().getSelectedItem().getRegistrationNumber(),
                                    imageViewHolder.get(studentsOnDisplay.size()));
                        }
                    }
                }
            }

            private void replaceStudent(Integer incoming, Integer outgoing) {
                studentsOnDisplay.remove();
                studentsOnDisplay.add(incoming);
                ImageView imageView = registrationNumberImageWindowMap.get(outgoing);
                registrationNumberImageWindowMap.remove(outgoing);
                registrationNumberImageWindowMap.put(incoming, imageView);
            }
        });

        Thread readVideoDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    Object[] data = (Object[]) UdpUtil.getObjectFromPort(videoFeedSocket);
                    String registrationNumber = (String) UdpUtil.byteArrayToObject((byte[]) data[0]);
                    BufferedImage image = UdpUtil.byteArrayToBufferedImage((byte[]) data[1]);
                    if(image != null && registrationNumber != null) {
                        if(studentsOnDisplay.contains(Integer.valueOf(registrationNumber))) {
                            registrationNumberImageWindowMap.get(Integer.valueOf(registrationNumber))
                                    .setImage(SwingFXUtils.toFXImage(image, null));
                        }
                    }
                }
            }
        });
        readVideoDataThread.setDaemon(true);
        readVideoDataThread.start();
    }
}
