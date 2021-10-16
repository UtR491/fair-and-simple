package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import request.Request;
import response.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TeacherApplication extends Application {

    private static String teacherId = "";
    static ObjectOutputStream outputStream;
    private static ObjectInputStream inputStream;
    public static Object tempHolder = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(TeacherApplication.class.getResource("../views/TeacherLoginView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 593);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Teacher Login");
            primaryStage.setMinHeight(590);
            primaryStage.setMinWidth(600);
            primaryStage.show();
            connectToServer();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 6969);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getTeacherId() {
        return teacherId;
    }

    public static void setTeacherId(String id) {
        if(teacherId.equals("")) teacherId = id;
    }

    public static void sendRequest(Request request) {
        try {
            outputStream.writeObject(request);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Response receiveResponse() {
        try {
            Response response = (Response) inputStream.readObject();
            System.out.println("Response is " + response);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}