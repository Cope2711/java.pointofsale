import java.sql.*;

public class DBManager {

    private Connection connection;

    public DBManager() {

    }

    private Connection getConnection(){
        return this.connection;
    }

    public PreparedStatement preparedStatement(String query, boolean returnGeneratedKeys) throws SQLException {
        if (returnGeneratedKeys) {
            return this.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        } else {
            return this.getConnection().prepareStatement(query);
        }
    }

    public void connect() {
        String URL = "jdbc:mysql://localhost:3306/";
        String USER = "root";
        String PASSWORD = "kaliworst14.";
        String DATABASE = "pointofsale";

        try {
            connection = DriverManager.getConnection(URL + DATABASE, USER, PASSWORD);
            System.out.println("Conexion exitosa");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        try {
            connection.close();
            System.out.println("Conexion cerrada exitosamente");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet executeSelect(PreparedStatement statement) {
        try {
            return statement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int executeUpdate(PreparedStatement statement) {
        try {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
