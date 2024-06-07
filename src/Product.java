import java.sql.ResultSet;
import java.sql.SQLException;

public class Product {
    public int id;
    public String name;
    public Double price;
    public int quantity;
    public String barcode;

    public Product(ResultSet product) throws SQLException {
        this.id = product.getInt("productID");
        this.name = product.getString("productName");
        this.price = product.getDouble("price");
        this.quantity = product.getInt("quantity");
        this.barcode = product.getString("productBarcode");
        product.close();
    }
}
