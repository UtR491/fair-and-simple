package util;

import controller.SingleChatCardFXMLController;
import controller.SingleImageChatCardFXMLController;
import controller.SingleNotificationCardFXMLController;
import entity.*;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import org.controlsfx.control.Notifications;
import sun.awt.image.ToolkitImage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Objects;
public class ChatUtil implements Runnable {

    ObjectInputStream ois;

    public ChatUtil(ObjectInputStream ois) {
        System.out.println("constructor called");
        this.ois=ois;
        System.out.println("ois assigned");
    }

    @Override
    public void run() {
        Message message2 = null;
        System.out.println(Thread.currentThread());
        while (true){
            System.out.println("inside socket is connected loop");
            try {
                System.out.println("waiting for message object");
                Object o = ois.readObject();
                if(o instanceof String && o.equals("disconnected")) {
                    break;
                } else if(o instanceof Message) {
                    message2 = (Message)o;
                }
                System.out.println("response sent on chat thread");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            final Message message = message2;
            System.out.println("Message received from sender id "+ message.getSenderID()+": "+message.getText());
            if(message instanceof Notification) {
                if(message instanceof AddedInformation && Main.profileScreenController != null) {
                    System.out.println("It was added information and our profile screen is not null so we will refresh");
                    Platform.runLater(() -> Main.profileScreenController.refreshButtonResponse());
                } else if(message instanceof ExamScheduledInformation) {
                    Platform.runLater(() -> Main.profileScreenController.refreshButtonResponse());
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Notifications.create()
                                .title("Notification from " + message.getSenderName() + " in " + message.getCourseName())
                                .text(message.getText())
                                .show();

                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/SingleNotificationCardFXML.fxml"));
                        try {
                            Node node = fxmlLoader.load();
                            SingleNotificationCardFXMLController singleNotificationCardFXMLController = fxmlLoader.getController();
                            singleNotificationCardFXMLController.courseLabel.setText(message.getCourseName());
                            singleNotificationCardFXMLController.messageLabel.setText(message.getText());
                            singleNotificationCardFXMLController.timestampLabel.setText(message.getSentAt().toString());
                            Main.notificationVbox.getChildren().add(node);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                continue;
            }

             if(Main.chatVBox == null || !message.getCourseID().equals(Main.lastOpenCourseId)) {
                if(!Objects.equals(message.getSenderID(), Main.userRegistrationNumber)) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Notifications.create()
                                    .title("Message from " + message.getSenderName() + " in " + message.getCourseName())
                                    .text(message.getText())
                                    .show();
                        }
                    });
                }
            }

//            if(Main.chatVBox != null)
            if(Main.chatVBox != null && message.getCourseID().equals(Main.lastOpenCourseId)){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if(message.getImage() != null) {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/SingleImageChatCardFXML.fxml"));
                            try {
                                Node node = fxmlLoader.load();
                                SingleImageChatCardFXMLController singleImageChatCardFXMLController = fxmlLoader.getController();
                                BufferedImage bufferedImage=  ((ToolkitImage)message.getImage().getImage()).getBufferedImage();
                                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                                singleImageChatCardFXMLController.imageView.setImage(image);
                                singleImageChatCardFXMLController.vBox.setAlignment(message.getSenderID().equals(Main.userRegistrationNumber) ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
                                singleImageChatCardFXMLController.nameLabel.setText(message.getSenderID().equals(Main.userRegistrationNumber)?"Me":message.getSenderName());
                                if(message.getSenderID().equals(Main.userRegistrationNumber))
                                    singleImageChatCardFXMLController.chatImageHBox.setAlignment(Pos.TOP_RIGHT);
                                singleImageChatCardFXMLController.timestampLabel.setText(message.getSentAt().toString());
                                singleImageChatCardFXMLController.nameHBox.backgroundProperty().set(new Background(new BackgroundFill(Color.web(
                                        (message.getSenderID().equals(Main.userRegistrationNumber)) ? Main.myColor : Main.otherColor),
                                        CornerRadii.EMPTY,
                                        Insets.EMPTY)));
                                Main.chatVBox.getChildren().add(node);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/SingleChatCardFXML.fxml"));
                            try {
                                Node node = fxmlLoader.load();
                                SingleChatCardFXMLController singleChatCardFXMLController = fxmlLoader.getController();
                                singleChatCardFXMLController.messageLabel.setText(message.getText());
                                singleChatCardFXMLController.messageLabel.setAlignment(message.getSenderID().equals(Main.userRegistrationNumber) ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
                                singleChatCardFXMLController.nameLabel.setText(message.getSenderID().equals(Main.userRegistrationNumber)?"Me":message.getSenderName());
                                if(message.getSenderID().equals(Main.userRegistrationNumber))
                                    singleChatCardFXMLController.chatCardHBox.setAlignment(Pos.TOP_RIGHT);
                                singleChatCardFXMLController.timestampLabel.setText(message.getSentAt().toString());
                                singleChatCardFXMLController.nameHBox.backgroundProperty().set(new Background(new BackgroundFill(Color.web(
                                        (message.getSenderID().equals(Main.userRegistrationNumber)) ? Main.myColor : Main.otherColor),
                                        CornerRadii.EMPTY,
                                        Insets.EMPTY)));
                                Main.chatVBox.getChildren().add(node);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }
}
