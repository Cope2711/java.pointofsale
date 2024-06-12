import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryManager {
    private DBManager dbManager = new DBManager();

    private final String ORDERID_COLUMN_NAME = "orderID";
    private final String PROVIDERID_COLUMN_NAME = "providerID";
    private final String PRODUCTID_COLUMN_NAME = "productID";
    private final String QUANTITY_COLUMN_NAME = "quantity";
    private final String PRICE_COLUMN_NAME = "price";
    private final String ARRIVAL_COLUMN_NAME = "orderArrival";
    private final String STATUS_COLUMN_NAME = "status";

    public void connect() {
        dbManager.connect();
    }

    public void disconnect() {
        dbManager.disconnect();
    }

    /**
     * Crea una nueva orden en la base de datos
     * @param order la orden que se quiere crear
     * @return true si la orden fue creada exitosamente, false si no
     */
    public boolean createOrder(Order order) {
        try {
            String query = String.format("INSERT INTO inventory (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)",
                    this.PROVIDERID_COLUMN_NAME, this.PRODUCTID_COLUMN_NAME, this.QUANTITY_COLUMN_NAME, this.PRICE_COLUMN_NAME, this.STATUS_COLUMN_NAME);
            PreparedStatement statement = this.dbManager.preparedStatement(query);
            statement.setInt(1, order.getProviderID());
            statement.setInt(2, order.getProductID());
            statement.setInt(3, order.getQuantity());
            statement.setDouble(4, order.getPrice());
            statement.setString(5, order.getStatus());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) { System.out.println("Error al crear la orden: No se afectaron filas"); return false; }
            System.out.println("Orden creada exitosamente, proovedor: " + order.getProviderID());
            return true;
        } catch (SQLException e) {
            this.handleSQLException(e);
            return false;
        }
    }

    /**
     * Confirma que una orden ha sido recibida y actualiza la cantidad del producto disponible
     * @param order la orden que se quiere confirmar
     * @return true si la orden fue confirmada exitosamente, false si no
     */
    public boolean confirmRecieved(Order order) {
        // Actualiza el status de la orden a "Recibido"
        order.setStatus("Recibido");
        try {
            // Actualiza la fecha de llegada de la orden y el status
            String query = String.format("UPDATE inventory SET %s = CURRENT_TIMESTAMP, %s = ? WHERE %s = ?",
                    this.ARRIVAL_COLUMN_NAME, this.STATUS_COLUMN_NAME, this.ORDERID_COLUMN_NAME);
            PreparedStatement statement = dbManager.preparedStatement(query);
            statement.setString(1, order.getStatus());
            statement.setInt(2, order.getID());

            // Actualiza la cantidad de productos disponibles, sumandole la cantidad de la orden
            ProductManager productManager = new ProductManager();
            productManager.connect();
            Product product = productManager.getProductByID(order.getProductID());
            product.setQuantity(product.getQuantity() + order.getQuantity());
            productManager.modifyProduct(product);
            productManager.disconnect();

            // Ejecuta la actualización
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) { System.out.println("Error al confirmar la orden: No se afectaron filas"); return false; }
            System.out.println("Orden confirmada exitosamente, id: " + order.getID());

            return true;
        } catch (SQLException e) {
            // En caso de error, se imprime el mensaje de error y se retorna false
            this.handleSQLException(e);
            return false;
        }
    }

    /**
     * Obtiene una orden de la base de datos
     * @param orderID el id de la orden que se quiere obtener
     * @return la orden si fue encontrada, null si no
     */
    public Order getOrder(int orderID) {
        try {
            // Obtiene la orden de la base de datos
            String query = String.format("SELECT * FROM inventory WHERE %s = ?", ORDERID_COLUMN_NAME);
            PreparedStatement statement = dbManager.preparedStatement(query);
            statement.setInt(1, orderID);
            ResultSet tableOrder = statement.executeQuery();
            // Si no se encontró la orden, se imprime un mensaje de error y se retorna null
            if (!tableOrder.next()) { System.out.println("Error al obtener la orden: No se encontró la orden"); return null; }
            Order order = new Order(tableOrder);
            System.out.println("Orden obtenida exitosamente, id: " + orderID);
            return order;
        } catch (SQLException e) {
            // En caso de error, se imprime el mensaje de error y se retorna null
            this.handleSQLException(e);
            return null;
        }
    }

    /**
     * Modifica una orden en la base de datos
     * @param order la orden ya modificada que se quiere actualizar
     * @return true si la orden fue modificada exitosamente, false si no
     */
    public boolean modifyOrder(Order order) {
        try {
            // Actualiza la orden en la base de datos
            String query = String.format("UPDATE inventory SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?",
                    this.PROVIDERID_COLUMN_NAME, this.PRODUCTID_COLUMN_NAME, this.QUANTITY_COLUMN_NAME, this.PRICE_COLUMN_NAME, this.STATUS_COLUMN_NAME, this.ORDERID_COLUMN_NAME);
            PreparedStatement statement = dbManager.preparedStatement(query);
            statement.setInt(1, order.getProviderID());
            statement.setInt(2, order.getProductID());
            statement.setInt(3, order.getQuantity());
            statement.setDouble(4, order.getPrice());
            statement.setString(5, order.getStatus());
            statement.setInt(6, order.getID());
            int rowsAffected = statement.executeUpdate();
            // Si no se afectaron filas, se imprime un mensaje de error y se retorna false
            if (rowsAffected == 0) { System.out.println("Error al modificar la orden: No se afectaron filas"); return false; }
            System.out.println("Orden modificada exitosamente, id: " + order.getID());
            return true;
        } catch (SQLException e) {
            // En caso de error, se imprime el mensaje de error y se retorna false
            this.handleSQLException(e);
            return false;
        }
    }

    private void handleSQLException(SQLException e) {
        if (e.getSQLState().equals("23000")) {
            System.out.println("La orden ya existe en la base de datos o la clave que se desea modificar ya le pertenece a otro producto;");
        } else if (e.getSQLState().equals("22001")) {
            System.out.println("El campo que se desea modificar es demasiado largo;");
        } else {
            System.out.println("Error al interactuar con la base de datos: " + e.getMessage() + " número de excepción: " + e.getSQLState());
        }
    }
}
