package main;

import entity.Message;
import entity.RegistrationStreamWrapper;
import request.*;
import requestHandler.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class RequestIdentifier implements Runnable{
    Socket socket;
    ObjectOutputStream oos=null;
    ObjectInputStream ois=null;
    ServerSocket chatServerSocket;
    public String userID;

    public RequestIdentifier(Socket socket, ServerSocket chatServerSocket){
        this.socket=socket;
        this.chatServerSocket = chatServerSocket;
        try {
            oos=new ObjectOutputStream(socket.getOutputStream());
            ois=new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0); // dont create this client connection.
        }
    }

    @Override
    public void run() {

        System.out.println("We are here");
        while (socket.isConnected()){
            Object request;
            try {
                System.out.println("Waiting for a request");
                request = Server.receiveRequest(ois);
                System.out.println("Request received");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace(); // basically the client disconnected so end this thread.
                break;
            }
            System.out.println("Request came");
            if(request==null) break;
             else if(request instanceof LoginRequest){
                System.out.println("Login request");
                userID=((LoginRequest) request).getUsername();
                LoginRequestHandler loginRequestHandler=new LoginRequestHandler(oos,(LoginRequest)request,Server.getConnection());
                loginRequestHandler.sendResponse(userID);

                try {
                    Socket chatSocket=chatServerSocket.accept();
                    ObjectOutputStream objectOutputStream=new ObjectOutputStream(chatSocket.getOutputStream());
                    ObjectInputStream objectInputStream = new ObjectInputStream(chatSocket.getInputStream());
                    String registrationNumber = (String) objectInputStream.readObject();
                    System.out.println(chatSocket);
                    System.out.println("connection established with client");
                    Server.socketArrayList.add(new RegistrationStreamWrapper(registrationNumber, objectOutputStream));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(request instanceof RegisterRequest){
                RegisterRequestHandler registerRequestHandler=new RegisterRequestHandler((RegisterRequest)request,oos,Server.getConnection());
                registerRequestHandler.sendResponse(userID);
            }
            else if(request instanceof TeacherLoginRequest){
                System.out.println("Login request");
                userID=((TeacherLoginRequest) request).getUsername();
                TeacherLoginRequestHandler teacherLoginRequestHandler=new TeacherLoginRequestHandler(Server.getConnection(),oos,(TeacherLoginRequest)request);
                teacherLoginRequestHandler.sendResponse(userID);
            }
            else if(request instanceof TeacherRegisterRequest){
                System.out.println("Teacher register request came.");
                TeacherRegisterRequestHandler teacherRegisterRequestHandler=new TeacherRegisterRequestHandler(Server.getConnection(), oos,(TeacherRegisterRequest)request);
                teacherRegisterRequestHandler.sendResponse(userID);
            } else if(request instanceof TeacherCoursesRequest) {
                TeacherCoursesRequestHandler handler = new TeacherCoursesRequestHandler(Server.getConnection(), oos, (TeacherCoursesRequest) request);
                handler.sendResponse(userID);
            } else if(request instanceof CreateCourseRequest) {
                System.out.println("Request is a create team request by teacher " + ((CreateCourseRequest) request).getTeacherId());
                CreateCourseRequestHandler handler = new CreateCourseRequestHandler(Server.getConnection(), oos, (CreateCourseRequest) request);
                handler.sendResponse(userID);
            } else if(request instanceof ExamResultRequest) {
                ExamResultRequestHandler handler = new ExamResultRequestHandler(Server.getConnection(), oos, (ExamResultRequest) request);
                handler.sendResponse(userID);
            } else if(request instanceof SetExamRequest) {
                SetExamRequestHandler handler = new SetExamRequestHandler(Server.getConnection(), oos, (SetExamRequest) request);
                handler.sendResponse(userID);
            } else if(request instanceof TeacherExamRequest) {
                TeacherExamRequestHandler handler = new TeacherExamRequestHandler(Server.getConnection(), oos, (TeacherExamRequest) request);
                handler.sendResponse(userID);
            } else if(request instanceof TeacherChangePasswordRequest) {
                TeacherChangePasswordRequestHandler handler = new TeacherChangePasswordRequestHandler(Server.getConnection(), oos, (TeacherChangePasswordRequest) request);
                handler.sendResponse(userID);
            }
            else if(request instanceof JoinCourseRequest){
                JoinCourseRequestHandler joinCourseRequestHandler=new JoinCourseRequestHandler(Server.getConnection(),oos,(JoinCourseRequest)request);
                joinCourseRequestHandler.sendResponse(userID);
            }
            else if(request instanceof CoursesListRequest){
                CoursesListRequestHandler coursesListRequestHandler=new CoursesListRequestHandler(Server.getConnection(),oos);
                coursesListRequestHandler.sendResponse(userID);
            }
            else if(request instanceof ChangePasswordRequest){
                ChangePasswordRequestHandler changePasswordRequestHandler=new ChangePasswordRequestHandler(Server.getConnection(),oos,(ChangePasswordRequest)request);
                changePasswordRequestHandler.sendResponse(userID);
            }
            else if(request instanceof LogOutRequest){
                LogOutRequestHandler logOutRequestHandler=new LogOutRequestHandler(Server.getConnection(),oos);
                logOutRequestHandler.sendResponse(userID);
            }
            else if(request instanceof ParticipantsListRequest){
                ParticipantsListRequestHandler participantsListRequestHandler=new ParticipantsListRequestHandler(Server.getConnection(),oos,(ParticipantsListRequest)request);
                participantsListRequestHandler.sendResponse(userID);
            }
            else if(request instanceof ExamsListRequest){
                ExamsListRequestHandler examsListRequestHandler=new ExamsListRequestHandler(Server.getConnection(),oos,(ExamsListRequest)request);
                examsListRequestHandler.sendResponse(userID);
            }
            else if(request instanceof CourseStudentRequest) {
                CourseStudentRequestHandler courseStudentRequestHandler = new CourseStudentRequestHandler(Server.getConnection(), oos, (CourseStudentRequest) request);
                courseStudentRequestHandler.sendResponse(userID);
            }
            else if(request instanceof TeacherChangeProfilePictureRequest) {
                TeacherChangeProfilePictureRequestHandler teacherChangeProfilePictureRequestHandler = new TeacherChangeProfilePictureRequestHandler(Server.getConnection(), oos, (TeacherChangeProfilePictureRequest) request);
                teacherChangeProfilePictureRequestHandler.sendResponse(userID);
            }
            else if(request instanceof AttemptExamRequest) {
                AttemptExamRequestHandler requestHandler = new AttemptExamRequestHandler(Server.getConnection(), oos, (AttemptExamRequest) request);
                requestHandler.sendResponse(userID);
            }
            else if(request instanceof SubmitExamRequest) {
                SubmitExamRequestHandler requestHandler = new SubmitExamRequestHandler(Server.getConnection(), oos, (SubmitExamRequest) request);
                requestHandler.sendResponse(userID);
            }
            else if (request instanceof CourseDetailsRequest){
                CourseDetailsRequestHandler courseDetailsRequestHandler=new CourseDetailsRequestHandler(Server.getConnection(),oos,(CourseDetailsRequest)request);
                courseDetailsRequestHandler.sendResponse(userID);
            }
            else if (request instanceof ChangeProfilePicRequest){
                ChangeProfilePicRequestHandler changeProfilePicRequestHandler=new ChangeProfilePicRequestHandler(Server.getConnection(),oos,(ChangeProfilePicRequest)request);
                changeProfilePicRequestHandler.sendResponse(userID);
            }
            else if( request instanceof GetProfilePicRequest){
                GetProfilePicRequestHandler getProfilePicRequestHandler=new GetProfilePicRequestHandler(Server.getConnection(),oos,(GetProfilePicRequest)request);
                getProfilePicRequestHandler.sendResponse(userID);
            }
            else if(request instanceof UpcomingExamsRequest){
                UpcomingExamsRequestHandler upcomingExamsRequestHandler=new UpcomingExamsRequestHandler(Server.getConnection(),oos,(UpcomingExamsRequest)request);
                upcomingExamsRequestHandler.sendResponse(userID);
            }
            else if(request instanceof ExamsHistoryRequest){
                ExamsHistoryRequestHandler examsHistoryRequestHandler=new ExamsHistoryRequestHandler(Server.getConnection(),oos,(ExamsHistoryRequest)request);
                examsHistoryRequestHandler.sendResponse(userID);
            }
            else if(request instanceof ChangeTeacherProfilePicRequest){
                ChangeTeacherProfilePicRequestHandler changeTeacherProfilePicRequestHandler=new ChangeTeacherProfilePicRequestHandler(Server.getConnection(),oos,(ChangeTeacherProfilePicRequest)request);
                changeTeacherProfilePicRequestHandler.sendResponse(userID);
            }
            else if(request instanceof GetTeacherProfilePicRequest) {
                GetTeacherProfilePicRequestHandler getTeacherProfilePicRequestHandler = new GetTeacherProfilePicRequestHandler(Server.getConnection(), oos, (GetTeacherProfilePicRequest) request);
                getTeacherProfilePicRequestHandler.sendResponse(userID);
            }
            else if(request instanceof Message) {
                SendMessageRequestHandler sendMessageRequestHandler = new SendMessageRequestHandler(Server.getConnection(), oos, (Message) request);
                sendMessageRequestHandler.sendResponse(userID);
                sendMessageRequestHandler.sendToAll();// send the message to every connected person
            }
            else if(request instanceof ProctoringDutyRequest) {
                ProctoringDutyRequestHandler requestHandler = new ProctoringDutyRequestHandler(Server.getConnection(), oos, (ProctoringDutyRequest) request);
                requestHandler.sendResponse(userID);
            }
            else if(request instanceof GetQuestionsRequest) {
                GetQuestionsRequestHandler getQuestionsRequestHandler = new GetQuestionsRequestHandler(Server.getConnection(),oos,(GetQuestionsRequest) request);
                getQuestionsRequestHandler.sendResponse(userID);
            }
            else if(request instanceof ProctoringRequest) {
                ProctoringRequestHandler requestHandler = new ProctoringRequestHandler(Server.getConnection(), oos, (ProctoringRequest) request);
                requestHandler.sendResponse(userID);
            }
            else if(request instanceof DisplayMessagesRequest) {
                DisplayMessagesRequestHandler displayMessagesRequestHandler = new DisplayMessagesRequestHandler(Server.getConnection(), oos, (DisplayMessagesRequest) request);
                displayMessagesRequestHandler.sendResponse(userID);
            }
            else{
                Server.sendResponse(oos, null);
            }
        }
        System.out.println("Client disconnected!!");
    }
}
