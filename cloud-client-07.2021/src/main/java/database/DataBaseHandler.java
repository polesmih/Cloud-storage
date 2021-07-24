package database;

import java.sql.*;

//connecting with database
public class DataBaseHandler extends Configs {
    Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException {
        String connectionString =
                "jdbc:mysql://"
                + dbHost
                + ":"
                + dbPort
                + "/"
                + dbName;

        Class.forName("com.mysql.cj.jdbc.Driver");

        dbConnection = DriverManager.getConnection(
                connectionString,
                dbUser, dbPass);

        return dbConnection;
    }

    // регистрация нового пользователя в БД
    public void registrationUser(User user) {
        String insert = "INSERT INTO " + Const.USER_TABLE
                + "(" + Const.USERS_NAME + ","
                + Const.USERS_LOGIN + ","
                + Const.USERS_PASSWORD + ")"
                + "VALUES(?, ?, ?)";// запрос для помещения данных в БД
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1, user.getName());
            prSt.setString(2, user.getLogin());
            prSt.setString(3, user.getPassword());

            prSt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    // возвращение пользователя из БД при авторизации
    public ResultSet getUser(User user) {
        ResultSet resSet = null;

        String select = "SELECT * FROM " + Const.USER_TABLE
                + " WHERE " + Const.USERS_LOGIN
                + "=? AND " + Const.USERS_PASSWORD
                + "=?";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, user.getLogin());
            prSt.setString(2, user.getPassword());

            resSet = prSt.executeQuery(); // получение данных из БД

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return resSet;
    }

}
