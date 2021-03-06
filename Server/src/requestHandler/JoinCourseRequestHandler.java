package requestHandler;

import main.Server;
import request.JoinCourseRequest;
import response.JoinCourseResponse;
import table.CoursesTable;
import table.EnrollmentTable;

import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JoinCourseRequestHandler extends RequestHandler {
    private Connection connection;
    private ObjectOutputStream oos;
    private JoinCourseRequest joinCourseRequest;

    public JoinCourseRequestHandler(Connection connection, ObjectOutputStream oos, JoinCourseRequest joinCourseRequest) {
        this.connection = connection;
        this.oos = oos;
        this.joinCourseRequest = joinCourseRequest;
    }

    @Override
    public void sendResponse(String userID)  {
        ResultSet resultSet = null;
        int result=0;
        int courseId = 0;
        try {
            PreparedStatement preparedStatement=connection.prepareStatement(CoursesTable.GET_COURSE_ID_BY_COURSE_CODE);
            preparedStatement.setString(1,joinCourseRequest.getCourseCode());
            System.out.println("Join course query");
            System.out.println(preparedStatement);
            resultSet=preparedStatement.executeQuery();
            while (resultSet.next()){
                preparedStatement=connection.prepareStatement(EnrollmentTable.QUERY_JOIN_COURSE_BY_ID);
                courseId = resultSet.getInt(1);
                System.out.println("Course ID = " + courseId);
                preparedStatement.setInt(1,courseId);
                preparedStatement.setInt(2,Integer.parseInt(userID));
                System.out.println(preparedStatement);
                result=preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
            if(result==0) Server.sendResponse(oos, new JoinCourseResponse("", ""));
            else Server.sendResponse(oos, new JoinCourseResponse("Successful", String.valueOf(courseId)));
    }
}
