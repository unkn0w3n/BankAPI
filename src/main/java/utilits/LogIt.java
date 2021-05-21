package utilits;

import model.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class LogIt {
    private static final String SQL_LOG_INSERT = "INSERT INTO logs(type, message, created_at) VALUES (?, ?, CURRENT_TIMESTAMP())";

    public static void write(String type, String message) throws Exception {
            if(!type.equals("error") && !type.equals("warning") && !type.equals("error")){
                throw new Exception("Log type can by only: error, warning, info");
            }
            if(message.length()<1){
                throw new Exception("Log message length is null");
            }

            Connection connection = Database.getH2Connection();
            Connection db = connection;
            PreparedStatement preparedStatement = db.prepareStatement(SQL_LOG_INSERT);
            preparedStatement.setString(1,  type);
            preparedStatement.setString(2,  message);
            preparedStatement.execute();
            db.close();
    }
}
