import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProductManager {
    // El administrador de la base de datos
    private DBManager dbManager = new DBManager();

    // Nombres de las columnas de la tabla products
    private final String BARCODE_COLUMN_NAME = "productBarcode";
    private final String QUANTITY_COLUMN_NAME = "quantity";

    // Nombres de las columnas de la tabla sales
    private final String SALES_DATE_TIME_COLUMN_NAME = "saleDateTime";
    private final String SALES_TOTAL_COLUMN_NAME = "total";

    // Constructor
    public ProductManager() {
        this.dbManager.connect();
    }

    /**
     * Obtiene un producto de la base de datos
     * @param barcode Codigo de barras del producto
     * @return El producto encontrado, null si no se encontro el producto
     * @throws SQLException Si ocurre un error al obtener el producto de la base de datos
     */
    public Product getProduct(String barcode) throws SQLException {
        // Consulta SQL para obtener el producto con el codigo de barras especificado
        String query = String.format("SELECT * FROM products WHERE %s = ?", this.BARCODE_COLUMN_NAME);
        PreparedStatement statement = dbManager.preparedStatement(query);
        statement.setString(1, barcode);
        ResultSet tableProduct = dbManager.executeSelect(statement);
        // Si no se encontro el producto
        if (!tableProduct.next()) { System.out.println("El producto con codigo de barras: " + barcode + " no fue encontrado;"); return null; }
        Product product = new Product(tableProduct);
        System.out.println("Producto con id "+ product.name + " creado exitosamente");
        return product;
    }

    /**
     * Registra una venta en la base de datos
     * @param cart Carrito de compras
     * @param totalPrice Precio total de la venta
     * @return true si la venta fue registrada exitosamente, false si ocurrio un error
     * @throws SQLException Si ocurre un error al registrar la venta en la base de datos
     */
    public boolean registerSell(Map<Product, Integer> cart, double totalPrice) throws SQLException {
        int saleID = insertSale(totalPrice);
        // Si ocurrio un error al insertar la venta
        if (saleID == -1) return false;

        // Si ocurrio un error al insertar los detalles de la venta
        if (!insertSaleDetails(cart, saleID)) return false;

        System.out.println("Venta registrada exitosamente con un total de: " + totalPrice);
        return true;
    }

    /**
     * Inserta una venta en la tabla sales
     * @param totalPrice Precio total de la venta
     * @return El ID de la venta insertada
     * @throws SQLException Si ocurre un error al insertar la venta en la base de datos
     */
    private int insertSale(double totalPrice) throws SQLException {
        // Consulta SQL para insertar una venta en la tabla sales
        String query = String.format("INSERT INTO sales (%s, %s) VALUES ('%s', %f);", this.SALES_DATE_TIME_COLUMN_NAME, this.SALES_TOTAL_COLUMN_NAME, this.getCurrentDateTimeForMySQL(), totalPrice);
        PreparedStatement statement = dbManager.preparedStatement(query, true);
        int rowsAffected = statement.executeUpdate();
        if (rowsAffected == 0) {
            System.out.println("Venta no registrada, error al insertar en la tabla sales;");
            return -1;
        }
        ResultSet rs = statement.getGeneratedKeys();
        rs.next();
        return rs.getInt(1);
    }

    /**
     * Inserta los detalles de una venta en la tabla sales_details
     * @param cart Carrito de compras
     * @param saleID ID de la venta
     * @return true si los detalles de la venta fueron registrados exitosamente, false si ocurrio un error
     * @throws SQLException Si ocurre un error al insertar los detalles de la venta en la base de datos
     */
    private boolean insertSaleDetails(Map<Product, Integer> cart, int saleID) throws SQLException {
        // Por cada producto en el carrito
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            Product product = entry.getKey(); // Producto
            int productQuantityInCart = entry.getValue(); // Cantidad del producto en el carrito
            // Consulta SQL para insertar los detalles de la venta en la tabla sales_details
            String query = "INSERT INTO sales_details (saleID, productID, quantity, price) VALUES (?, ?, ?, ?);";
            PreparedStatement statement = dbManager.preparedStatement(query);
            statement.setInt(1, saleID);
            statement.setInt(2, product.id);
            statement.setInt(3, productQuantityInCart);
            statement.setDouble(4, product.price);
            int rowsAffected = dbManager.executeAffected(statement);
            if (rowsAffected == 0) {
                System.out.println("Venta no registrada, error al insertar en la tabla sales_details;");
                return false;
            }
            // Actualizar la cantidad del producto en la tabla products
            if (!updateProductQuantity(product.barcode, productQuantityInCart)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Actualiza la cantidad de un producto en la tabla products
     * @param barcode Codigo de barras del producto
     * @param productQuantityInCart Cantidad del producto en el carrito
     * @return true si la cantidad del producto fue actualizada exitosamente, false si ocurrio un error
     * @throws SQLException Si ocurre un error al actualizar la cantidad del producto en la base de datos
     */
    private boolean updateProductQuantity(String barcode, int productQuantityInCart) throws SQLException {
        // Consulta SQL para actualizar la cantidad del producto en la tabla products
        String query = String.format("UPDATE products SET %s = %s - %d WHERE %s = ?;", this.QUANTITY_COLUMN_NAME, this.QUANTITY_COLUMN_NAME, productQuantityInCart, this.BARCODE_COLUMN_NAME);
        PreparedStatement statement = dbManager.preparedStatement(query);
        statement.setString(1, barcode);
        int rowsAffected = dbManager.executeAffected(statement);
        if (rowsAffected == 0) {
            System.out.println("Venta no registrada, error al actualizar la cantidad del producto;");
            return false;
        }
        return true;
    }

    /**
     * Obtiene la fecha y hora actual en el formato adecuado para MySQL
     * @return Fecha y hora actual en el formato adecuado para MySQL
     */
    private String getCurrentDateTimeForMySQL() {
        // Obtener la fecha y hora actual
        LocalDateTime now = LocalDateTime.now();

        // Formatear la fecha y hora en el formato adecuado para MySQL
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

}
