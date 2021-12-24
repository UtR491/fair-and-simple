package table;

public class AnswerFilesTable {
    public final static String TABLE_NAME = "answer_files";
    public final static String COLUMN_EXAM_ID = "examID";
    public final static String COLUMN_REGISTRATION_NO = "registrationNo";
    public final static String COLUMN_COURSE_ID = "courseID";
    public final static String COLUMN_ANSWER_PATH = "answerPath";

    public final static String ADD_FILE_PATH_QUERY = "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?);";
}
