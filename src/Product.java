import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Product {
    private int id;
    private String name;
    private Double price;
    private int quantity;
    private String barcode;
    private Boolean active;

    /**
     * Constructor para crear un nuevo producto que se trajo de la base de datos
     * @param product ResultSet con los datos del producto
     * @throws SQLException Si ocurre un error al obtener los datos del producto
     */
    public Product(ResultSet product) throws SQLException {
        // Se obtienen los datos del producto de la base de datos
        try {
            this.id = product.getInt("productID");
            this.name = product.getString("productName");
            this.price = product.getDouble("price");
            this.quantity = product.getInt("quantity");
            this.barcode = product.getString("productBarcode");
            this.active = product.getBoolean("productActive");
        } catch (SQLException e) {
            System.out.println("Error al obtener los datos del producto: " + e.getMessage() + " codigo de error: " + e.getSQLState());
        }
    }

    /**
     * Constructor para crear un producto en la base de datos
     * @param name Nombre del producto
     * @param price Precio del producto
     * @param quantity Cantidad del producto
     * @param barcode Codigo de barras del producto
     */
    public Product(String name, Double price, int quantity, String barcode, Boolean active) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.barcode = barcode;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getBarcode() {
        return barcode;
    }

    public Boolean getActive() {
        return active;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
