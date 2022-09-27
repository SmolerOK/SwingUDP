import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDatabase {

    private static final Logger LOGGER = LogManager.getLogger(ConnectionDatabase.class);
    private static final String DBPATH = "D:\\MyDataBase\\CLIENTDB.FDB";
    private static final String URL = "jdbc:firebirdsql://localhost:3050/" + DBPATH + "?encoding=UTF8";
    private static final String LOGIN = "SYSDBA";
    private static final String PASSWORD = "root";

    Connection connection;

    public ConnectionDatabase() {

        try {
            LOGGER.info("Попытка подключения к базе данных по пути: " + DBPATH + " .");

            connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);

            LOGGER.info("Успешное подключение к базе данных.");
        } catch (SQLException e) {
            LOGGER.error(e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public Connection getConnection() {
        return connection;
    }

}