package util;

import entity.Notification;
import entity.RegistrationStreamWrapper;
import main.Server;
import table.CoursesTable;
import table.ExamTable;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.time.LocalDateTime;

public class SendProctorNotification implements Runnable {
    Connection connection= Server.getConnection();
    @Override
    public void run() {
        try {
            PreparedStatement preparedStatement=connection.prepareStatement(ExamTable.GET_PROCTOR_DUTY_IN_20_MINS);
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()){
                sendToProctor(new Notification(
                        null,
                        "Admin",
                        null,
                        resultSet.getString(CoursesTable.TABLE_NAME+"."+CoursesTable.COURSE_NAME_COLUMN),
                        "You have an upcoming proctoring duty for Exam : "+
                                resultSet.getString(ExamTable.TABLE_NAME+"."+ExamTable.TITLE_COLUMN)+
                                " in "+resultSet.getString(CoursesTable.TABLE_NAME+"."+CoursesTable.COURSE_NAME_COLUMN),
                        null,
                                Timestamp.valueOf(LocalDateTime.now()),
                        false),
                        resultSet.getString(ExamTable.TABLE_NAME+"."+ExamTable.PROCTOR_ID_COLUMN)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void sendToProctor(Notification notification,String proctorID){
        for (RegistrationStreamWrapper r:Server.socketArrayList) {
            if(proctorID.equals(r.getRegistrationNumber())){
                ObjectOutputStream objectOutputStream=r.getOos();
                System.out.println("sending proctor notif, got his oos"+objectOutputStream);
                try {
                    synchronized (objectOutputStream){
                        objectOutputStream.writeObject(notification);
                        objectOutputStream.flush();
                        System.out.println("send notification to proctor");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
