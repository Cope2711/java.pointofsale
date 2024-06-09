import java.sql.*;

public class DBManager {

    // Conexion a la base de datos
    private Connection connection;

    // Constructor
    private Connection getConnection(){
        return this.connection;
    }

    /**
     * Crea un PreparedStatement
     * @param query Consulta SQL
     * @return PreparedStatement
     * @throws SQLException Si ocurre un error al crear el PreparedStatement
     */
    public PreparedStatement preparedStatement(String query) throws SQLException {
        return this.getConnection().prepareStatement(query);
    }

    /**
     * Crea un PreparedStatement
     * @param query Consulta SQL
     * @param returnGeneratedKeys Si se desean obtener las llaves generadas
     * @return PreparedStatement
     * @throws SQLException Si ocurre un error al crear el PreparedStatement
     */
    public PreparedStatement preparedStatement(String query, boolean returnGeneratedKeys) throws SQLException {
        if (returnGeneratedKeys) {
            return this.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        } else {
            return this.getConnection().prepareStatement(query);
        }
    }

    /**
     * Conecta a la base de datos
     */
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

    /**
     * Desconecta de la base de datos
     */
    public void disconnect() {
        try {
            connection.close();
            System.out.println("Conexion cerrada exitosamente");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Ejecuta una consulta SELECT
     * @param statement PreparedStatement
     * @return ResultSet
     */
    public ResultSet executeSelect(PreparedStatement statement) {
        try {
            return statement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Ejecuta una consulta que afecta a la base de datos
     * @param statement PreparedStatement
     * @return Numero de filas afectadas
     */
    public int executeAffected(PreparedStatement statement) {
        try {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
