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
    private final String PRODUCT_ID_COLUMN_NAME = "productID";
    private final String PRODUCT_NAME_COLUMN_NAME = "productName";
    private final String PRICE_COLUMN_NAME = "price";

    // Nombres de las columnas de la tabla sales
    private final String SALES_DATE_TIME_COLUMN_NAME = "saleDateTime";
    private final String SALES_TOTAL_COLUMN_NAME = "total";

    /**
     * Conecta a la base de datos
     */
    public void connect() {
        this.dbManager.connect();
    }

    /**
     * Desconecta de la base de datos
     */
    public void disconnect() {
        this.dbManager.disconnect();
    }

    /**
     * Crea un producto en la base de datos
     * @param product Producto a crear
     * @return true si el producto fue creado exitosamente, false si ocurrio un error
     */
    public boolean createProduct(Product product) {
        // Consulta SQL para insertar un producto en la tabla products
        String query = String.format("INSERT INTO products (%s, %s, %s, %s) VALUES (?, ?, ?, ?);",
                this.PRODUCT_NAME_COLUMN_NAME, this.BARCODE_COLUMN_NAME, this.PRICE_COLUMN_NAME, this.QUANTITY_COLUMN_NAME);

        // Crear un PreparedStatement y ejecutar la consulta en un solo bloque try-catch
        try {
            PreparedStatement statement = dbManager.preparedStatement(query);
            statement.setString(1, product.getName());
            statement.setString(2, product.getBarcode());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getQuantity());

            // Ejecutar la consulta
            int rowsAffected = dbManager.executeAffected(statement);

            // Si no se inserta ningún producto
            if (rowsAffected == 0) {
                System.out.println("Producto no creado, error al insertar en la tabla products;");
                return false;
            }

            System.out.println("Producto con nombre " + product.getName() + " creado exitosamente en la base de datos!");
            return true;
        } catch (SQLException e) {
            this.handleSQLException(e);
            return false;
        }
    }

    /**
     * Modifica un producto en la base de datos
     * @param product Producto modificado
     * @return true si el producto fue modificado exitosamente, false si ocurrio un error
     */
    public boolean modifyProduct(Product product) {
        // Consulta SQL para modificar un producto en la tabla products
        String query = String.format("UPDATE products SET %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?;",
                this.PRODUCT_NAME_COLUMN_NAME, this.PRICE_COLUMN_NAME, this.QUANTITY_COLUMN_NAME,
                this.BARCODE_COLUMN_NAME, this.PRODUCT_ID_COLUMN_NAME);

        try {
            // Crear un PreparedStatement
            PreparedStatement statement = dbManager.preparedStatement(query);
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice());
            statement.setInt(3, product.getQuantity());
            statement.setString(4, product.getBarcode());
            statement.setInt(5, product.getId());

            // Ejecutar la consulta
            int rowsAffected = dbManager.executeAffected(statement);

            // Si no se modificó ningún producto
            if (rowsAffected == 0) {
                System.out.println("Producto no modificado, error al modificar en la tabla products;");
                return false;
            }

            System.out.println("Producto con nombre " + product.getName() + " modificado exitosamente en la base de datos!");
            return true;
        } catch (SQLException e) {
            this.handleSQLException(e);
            return false;
        }
    }

    /**
     * Obtiene un producto de la base de datos
     * @param barcode Codigo de barras del producto
     * @return Product si fue encontrado, null si no fue encontrado
     */
    public Product getProductByBarcode(String barcode) {
        // Consulta SQL para obtener el producto con el código de barras especificado
        String query = String.format("SELECT * FROM products WHERE %s = ?", this.BARCODE_COLUMN_NAME);

        try {
            // Crear un PreparedStatement
            PreparedStatement statement = dbManager.preparedStatement(query);
            statement.setString(1, barcode);

            // Ejecutar la consulta
            ResultSet tableProduct = dbManager.executeSelect(statement);

            // Obtener el producto
            if (!tableProduct.next()) {
                System.out.println("El producto con código de barras: " + barcode + " no fue encontrado;");
                return null;
            }

            Product product = new Product(tableProduct);
            System.out.println("Producto con nombre " + product.getName() + " obtenido exitosamente");
            return product;
        } catch (SQLException e) {
            this.handleSQLException(e);
            return null;
        }
    }

    public Product getProductByID(int productID) {
        try {
            // Consulta SQL para obtener el producto con el código de barras especificado
            String query = String.format("SELECT * FROM products WHERE %s = ?", this.PRODUCT_ID_COLUMN_NAME);
            PreparedStatement statement = dbManager.preparedStatement(query);
            statement.setInt(1, productID);

            // Ejecutar la consulta
            ResultSet tableProduct = dbManager.executeSelect(statement);

            // Obtener el producto
            if (!tableProduct.next()) {
                System.out.println("El producto con ID: " + productID + " no fue encontrado;");
                return null;
            }

            Product product = new Product(tableProduct);
            System.out.println("Producto con nombre " + product.getName() + " obtenido exitosamente");
            return product;

        } catch (SQLException e) {
            this.handleSQLException(e);
            return null;
        }
    }

    /**
     * Registra una venta en la base de datos
     * @param cart Carrito de compras
     * @param totalPrice Precio total de la venta
     * @return true si la venta fue registrada exitosamente, false si ocurrio un error
     * @throws SQLException Si ocurre un error al registrar la venta en la base de datos
     */
    public boolean registerSell(Map<Product, Integer> cart, double totalPrice) {
        try {
            int saleID = insertSale(totalPrice);

            // Si ocurrió un error al insertar la venta
            if (saleID == -1) return false;

            // Si ocurrió un error al insertar los detalles de la venta
            if (!insertSaleDetails(cart, saleID)) return false;

            System.out.println("Venta registrada exitosamente con un total de: " + totalPrice);
            return true;
        } catch (SQLException e) {
            this.handleSQLException(e);
            return false;
        }
    }

    /**
     * Inserta una venta en la tabla sales
     * @param totalPrice Precio total de la venta
     * @return El ID de la venta insertada
     * @throws SQLException Si ocurre un error al insertar la venta en la base de datos
     */
    private int insertSale(double totalPrice) throws SQLException {
        // Consulta SQL para insertar una venta en la tabla sales
        String query = String.format("INSERT INTO sales (%s, %s) VALUES ('%s', %f);",
                this.SALES_DATE_TIME_COLUMN_NAME, this.SALES_TOTAL_COLUMN_NAME, this.getCurrentDateTimeForMySQL(), totalPrice);
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
            statement.setInt(2, product.getId());
            statement.setInt(3, productQuantityInCart);
            statement.setDouble(4, product.getPrice());
            int rowsAffected = dbManager.executeAffected(statement);
            if (rowsAffected == 0) {
                System.out.println("Venta no registrada, error al insertar en la tabla sales_details;");
                return false;
            }
            // Actualizar la cantidad del producto en la tabla products
            if (!updateProductQuantity(product.getBarcode(), productQuantityInCart)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Actualiza la cantidad de un producto en la tabla products, se utiliza para restar la cantidad de un producto vendido
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

    private void handleSQLException(SQLException e) {
        if (e.getSQLState().equals("23000")) {
            System.out.println("El producto ya existe en la base de datos o la clave que se desea modificar ya le pertenece a otro producto;");
        } else if (e.getSQLState().equals("22001")) {
            System.out.println("El campo que se desea modificar es demasiado largo;");
        } else {
            System.out.println("Error al interactuar con la base de datos: " + e.getMessage() + " número de excepción: " + e.getSQLState());
        }
    }

}
