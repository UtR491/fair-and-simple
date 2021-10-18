package requestHandler;

import request.LoginRequest;
import response.LoginResponse;
import table.StudentTable;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginRequestHandler extends RequestHandler {
    private ObjectOutputStream oos;
    private LoginRequest loginRequest;
    private Connection connection;

    public LoginRequestHandler(ObjectOutputStream oos, LoginRequest loginRequest, Connection connection) {
        this.oos = oos;
        this.loginRequest = loginRequest;
        this.connection = connection;
    }

    @Override
    public void sendResponse() throws SQLException {
        PreparedStatement preparedStatement = null;
        preparedStatement=connection.prepareStatement(StudentTable.QUERY_LOGIN);
        preparedStatement.setInt(1,Integer.parseInt(loginRequest.getUsername()));
        preparedStatement.setString(2,loginRequest.getPassword());
        ResultSet resultSet=preparedStatement.executeQuery();
        LoginResponse response;
        if(!resultSet.next())response=null;
        do{
            response=new LoginResponse(resultSet.getString(StudentTable.COLUMN_FIRST_NAME),
                    resultSet.getString(StudentTable.COLUMN_LAST_NAME),resultSet.getString(StudentTable.COLUMN_EMAIL_ID),
                    resultSet.getInt(StudentTable.COLUMN_REGISTRATION_NUMBER));
            preparedStatement=connection.prepareStatement(StudentTable.QUERY_UPDATE_LAST_ACTIVE);
            preparedStatement.setString(1,loginRequest.getUsername());
            preparedStatement.execute();
        }while (resultSet.next());
        try {
            oos.writeObject(response);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
