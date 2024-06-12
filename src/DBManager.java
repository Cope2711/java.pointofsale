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
        String URL = System.getenv("DB_URL");
        String USER = System.getenv("DB_USER");
        String PASSWORD = System.getenv("DB_PASSWORD");
        String DATABASE = System.getenv("DB_DATABASE");

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
    public ResultSet executeSelect(PreparedStatement statement) throws SQLException {
        try {
            return statement.executeQuery();
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Ejecuta una consulta que afecta a la base de datos
     * @param statement PreparedStatement
     * @return Numero de filas afectadas
     */
    public int executeAffected(PreparedStatement statement) throws SQLException {
        try {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }
}
