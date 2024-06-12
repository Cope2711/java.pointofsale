import java.sql.ResultSet;
import java.sql.SQLException;

public class Order {
    private int id;
    private int providerID;
    private int productID;
    private int quantity;
    private double price;
    private double total;
    private String date;
    private String arrival;
    private String status;

    /**
     * Constructor para crear una nueva orden en la base de datos
     * @param providerID el id del proveedor
     * @param productID el id del producto
     * @param quantity la cantidad de productos
     * @param date la fecha de la orden
     * @param arrival la fecha de llegada de la orden
     */
    public Order(int providerID, int productID, int quantity, double price, String date, String arrival, String status) {
        this.providerID = providerID;
        this.productID = productID;
        this.quantity = quantity;
        this.price = price;
        this.total = price * quantity;
        this.date = date;
        this.arrival = arrival;
        this.status = status;
    }

    /**
     * Constructor para crear una orden a partir de un ResultSet
     * @param order
     */
    public Order(ResultSet order) throws SQLException {
        try {
            this.id = order.getInt("orderID");
            this.providerID = order.getInt("providerID");
            this.productID = order.getInt("productID");
            this.quantity = order.getInt("quantity");
            this.price = order.getDouble("price");
            this.total = order.getDouble("total");
            this.date = order.getString("orderDate");
            this.arrival = order.getString("orderArrival");
            this.status = order.getString("status");
        } catch (SQLException e) {
            System.out.println("Error al crear el objeto order: " + e.getMessage() + " codigo de error: " + e.getSQLState());
        }
    }

    public int getID() {
        return this.id;
    }

    public int getProviderID() {
        return this.providerID;
    }

    public int getProductID() {
        return this.productID;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public double getPrice() {
        return this.price;
    }

    public double getTotal() {
        return this.total;
    }

    public String getDate() {
        return this.date;
    }

    public String getArrival() {
        return this.arrival;
    }

    public String getStatus() {
        return this.status;
    }

    public void setProviderID(int providerID) {
        this.providerID = providerID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}