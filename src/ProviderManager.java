import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProviderManager {
    private DBManager dbManager = new DBManager();

    private final String PROVIDER_NAME_COLUMN = "providerName";
    private final String PROVIDER_PHONE_COLUMN = "providerPhone";
    private final String PROVIDER_ADDRESS_COLUMN = "providerAddress";

    /**
     * Conecta a la base de datos
     */
    public void connect() {
        dbManager.connect();
    }

    /**
     * Desconecta de la base de datos
     */
    public void disconnect() {
        dbManager.disconnect();
    }

    /**
     * Crea un proveedor en la base de datos
     * @param provider Proveedor a crear
     * @return true si se creó el proveedor, false si no
     */
    public boolean createProvider(Provider provider) {
        try {
            String query = String.format("INSERT INTO providers (%s, %s, %s) VALUES (?, ?, ?)", this.PROVIDER_NAME_COLUMN, this.PROVIDER_PHONE_COLUMN, this.PROVIDER_ADDRESS_COLUMN);
            PreparedStatement statement = dbManager.preparedStatement(query);
            statement.setString(1, provider.getName());
            statement.setString(2, provider.getPhone());
            statement.setString(3, provider.getAddress());
            int rowsAffected = dbManager.executeAffected(statement);
            if (rowsAffected == 0) { System.out.println("Proveedor no creado, ha ocurrido un error"); return false; }
            System.out.println("Se creó el proveedor con nombre: " + provider.getName() + " exitosamente");
            return true;
        } catch (SQLException e) {
            this.handleSQLException(e);
            return false;
        }
    }

    /**
     * Obtiene un proveedor de la base de datos
     * @param providerID ID del proveedor a obtener
     * @return Proveedor obtenido, null si no se encontró
     */
    public Provider getProvider(int providerID) {
        try {
            String query = "SELECT * FROM providers WHERE providerID = ?";
            PreparedStatement statement = dbManager.preparedStatement(query);
            statement.setInt(1, providerID);
            ResultSet providerTable = dbManager.executeSelect(statement);
            if (!providerTable.next()) { System.out.println("No se encontró el proveedor con ID: " + providerID); return null; }
            Provider provider = new Provider(providerTable);
            System.out.println("Se encontró el proveedor con el nombre: " + provider.getName());
            return provider;
        } catch (SQLException e) {
            this.handleSQLException(e);
            return null;
        }
    }

    /**
     * Modifica un proveedor en la base de datos
     * @param provider Proveedor modificado
     * @return true si se modificó el proveedor, false si no
     */
    public boolean modifyProvider(Provider provider) {
        try {
            String query = String.format("UPDATE providers SET %s = ?, %s = ?, %s = ? WHERE providerID = ?", this.PROVIDER_NAME_COLUMN, this.PROVIDER_PHONE_COLUMN, this.PROVIDER_ADDRESS_COLUMN);
            PreparedStatement statement = dbManager.preparedStatement(query);
            statement.setString(1, provider.getName());
            statement.setString(2, provider.getPhone());
            statement.setString(3, provider.getAddress());
            statement.setInt(4, Integer.parseInt(provider.getId()));
            int rowsAffected = dbManager.executeAffected(statement);
            if (rowsAffected == 0) { System.out.println("Proveedor no modificado, ha ocurrido un error"); return false; }
            System.out.println("Se modificó el proveedor con ID: " + provider.getId() + " exitosamente");
            return true;
        } catch (SQLException e) {
            this.handleSQLException(e);
            return false;
        }
    }

    private void handleSQLException(SQLException e) {
        if (e.getSQLState().equals("23000")) {
            System.out.println("El proveedor ya existe en la base de datos o la clave que se desea modificar ya le pertenece a otro campo;");
        } else if (e.getSQLState().equals("22001")) {
            System.out.println("El campo que se desea modificar es demasiado largo;");
        } else {
            System.out.println("Error al interactuar con la base de datos: " + e.getMessage() + " número de excepción: " + e.getSQLState());
        }
    }
}
