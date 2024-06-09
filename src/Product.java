import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Product {
    public int id;
    public String name;
    public Double price;
    public int quantity;
    public String barcode;

    // Constructor
    public Product(ResultSet product) throws SQLException {
        // Se obtienen los datos del producto de la base de datos
        this.id = product.getInt("productID");
        this.name = product.getString("productName");
        this.price = product.getDouble("price");
        this.quantity = product.getInt("quantity");
        this.barcode = product.getString("productBarcode");
        product.close();
    }

    // Sobreescriuta del metodo equals para que funcionen los metodos de comparacion de objetos con products
    @Override
    public boolean equals(Object o) {
        // Si el objeto es el mismo
        if (this == o) return true;
        // Si el objeto es nulo o no es de la misma clase
        if (o == null || getClass() != o.getClass()) return false;
        // Se castea el objeto a Product
        Product product = (Product) o;
        // Se comparan los atributos de los objetos
        return Objects.equals(id, product.id) &&
                Objects.equals(barcode, product.barcode) &&
                Objects.equals(name, product.name) &&
                Objects.equals(price, product.price);
    }

    // Sobreescriuta del metodo hashCode para que funcionen los metodos de comparacion de objetos con products
    @Override
    public int hashCode() {
        // Se obtiene el hash de los atributos del objeto
        return Objects.hash(barcode, name, price);
    }
}
