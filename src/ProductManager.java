import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProductManager {
    private DBManager dbManager = new DBManager();

    private String barcodeColumnName = "productBarcode";
    private String quantityColunmName = "quantity";
    private String productIDColumnName = "productID";

    private String salesDateTimeColumnName = "saleDateTime";
    private String salesTotalColumnName = "total";

    public Product getProduct(String barcode) throws SQLException {
        this.dbManager.connect();
        String query = String.format("SELECT * FROM products WHERE %s = ?", this.barcodeColumnName);
        PreparedStatement statement = dbManager.preparedStatement(query, false);
        statement.setString(1, barcode);
        ResultSet tableProduct = dbManager.executeSelect(statement);
        if (!tableProduct.next()) { System.out.println("El producto con codigo de barras: " + barcode + " no fue encontrado;"); this.dbManager.disconnect(); return null; }
        Product product = new Product(tableProduct);
        System.out.println("Producto con id "+ product.name + " creado exitosamente");
        this.dbManager.disconnect();
        return product;
    }

    public boolean registerSell(ArrayList<Product> cart, double totalPrice) throws SQLException {
        this.dbManager.connect();
        if (!checkProductQuantities(cart)) { System.out.println("Venta no registrada, uno o mas productos no cuentan con cantidad en inventario;"); this.dbManager.disconnect(); return false; }
        // Inserta la venta y obtén el ID de la venta
        String query = String.format("INSERT INTO sales (%s, %s) VALUES ('%s', %f);", this.salesDateTimeColumnName, this.salesTotalColumnName, this.getCurrentDateTimeForMySQL(), totalPrice);
        PreparedStatement statement = dbManager.preparedStatement(query, true);
        int rowsAffected = statement.executeUpdate();
        if (rowsAffected == 0) { System.out.println("Venta no registrada, error al insertar en la tabla sales;"); this.dbManager.disconnect(); return false; }
        ResultSet rs = statement.getGeneratedKeys();
        rs.next();
        int saleID = rs.getInt(1);;

        ArrayList<Product> cartSet = new ArrayList<>();
        for (Product product : cart) {
            boolean exits = false;
            for (Product productSet : cartSet) {
                if (product.id == productSet.id) {
                    exits = true;
                    break;
                }
            }
            if (!exits) {
                cartSet.add(product);
            }
        }

        // Usa el ID de la venta al insertar en sales_details
        for (Product product : cartSet) {
            int productQuantityInCart = countProductById(product.id, cart);
            query = "INSERT INTO sales_details (saleID, productID, quantity, price, total) VALUES (?, ?, ?, ?, ?);";
            statement = dbManager.preparedStatement(query, false);
            statement.setInt(1, saleID);
            statement.setInt(2, product.id);
            statement.setInt(3, productQuantityInCart);
            statement.setDouble(4, product.price);
            statement.setDouble(5, product.price * productQuantityInCart);
            rowsAffected = dbManager.executeUpdate(statement);
            if (rowsAffected == 0) { System.out.println("Venta no registrada, error al insertar en la tabla sales_details;"); this.dbManager.disconnect(); return false; }
            query = String.format("UPDATE products SET %s = %s - %d WHERE %s = ?;", this.quantityColunmName, this.quantityColunmName, productQuantityInCart, this.barcodeColumnName);
            statement = dbManager.preparedStatement(query, false);
            statement.setString(1, product.barcode);
            rowsAffected = dbManager.executeUpdate(statement);
            if (rowsAffected == 0) { System.out.println("Venta no registrada, error al actualizar la cantidad del producto;"); this.dbManager.disconnect(); return false; }
        }
        System.out.println("Venta registrada exitosamente con un total de: " + totalPrice);
        this.dbManager.disconnect();
        return true;
    }

    private boolean checkProductQuantities(ArrayList<Product> cart) {
        for (Product product : cart) {
            if (product.quantity == 0) {
                return false;
            }
        }
        return true;
    }

    private int countProductById(int id, ArrayList<Product> cart) {
        int count = 0;
        for (Product product : cart) {
            if (product.id == id) {
                count++;
            }
        }
        return count;
    }

    private String getCurrentDateTimeForMySQL() {
        // Obtener la fecha y hora actual
        LocalDateTime now = LocalDateTime.now();

        // Formatear la fecha y hora en el formato adecuado para MySQL
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }



/*INSERT INTO sales (" + this.salesDateTimeColumnName + "," + this.salesTotalColumnName + ") VALUES (" + this.getCurrentDateTimeForMySQL() + ") "
        -- Inserta una nueva venta en la tabla sales
        INSERT INTO sales (saleDate, total)
        VALUES ('2003-11-29 00:00:00', 15.6);

        -- Inserta los detalles de la venta en la tabla sales_details usando LAST_INSERT_ID() para obtener el saleID generado
        INSERT INTO sales_details (saleID, productID, quantity, price, total)
        VALUES (LAST_INSERT_ID(), 1, 1, 15.6, 15.6);

        this.dbManager.connect();
        // Se comprueba la cantidad del producto en inventario
        if (product.quantity == 0) { System.out.println("Venta no registra, producto no cuenta con cantidad en inventario, codigo de barras: " + product.barcode); this.dbManager.disconnect(); return false; }
        // Se construye el query para restar 1 a la cantidad del producto
        String query = String.format("UPDATE products SET " + this.quantityColunmName + " = " + this.quantityColunmName + " - 1 WHERE " + this.barcodeColumnName + " = ?;");
        PreparedStatement statement = dbManager.preparedStatement(query);
        statement.setString(1, product.barcode);
        // Guardar la cantidad de filas afectadas
        int rowsAffected = dbManager.executeUpdate(statement);
        // Dar feedback
        System.out.println("Venta registrada en producto con codigo de barras: " + product.barcode);
        // Retornar true o false conforme a la cantidad de filas afectadas
        this.dbManager.disconnect();
        return rowsAffected > 0;

        */

}
