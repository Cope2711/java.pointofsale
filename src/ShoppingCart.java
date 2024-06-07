import java.sql.SQLException;
import java.util.ArrayList;


public class ShoppingCart {

    ArrayList<Product> cart = new ArrayList<>();
    ProductManager productManager = new ProductManager();

    public void addProduct(String barcode) throws SQLException {
        Product product = productManager.getProduct(barcode);
        if (product != null) {
            cart.add(product);
        }
    }

    public void showCart() {
        System.out.println("Carrito de compras:");
        for (Product product : cart) {
            System.out.println(product.name + " - " + product.price);
        }
    }

    public void checkout() throws SQLException {
        if (cart.isEmpty()) {
            System.out.println("Carrito vacio, no se puede realizar la compra");
            return;
        }
        if (productManager.registerSell(cart, getTotal())) {
            System.out.println("Compra realizada exitosamente");
            clearCart();
        } else {
            System.out.println("Error al realizar la compra");
        }
    }

    public double getTotal() {
        double total = 0;
        for (Product product : cart) {
            total += product.price;
        }
        return total;
    }

    public void clearCart() {
        cart.clear();
    }

    public void removeProduct(String barcode) {
        for (Product product : cart) {
            if (product.barcode.equals(barcode)) {
                cart.remove(product);
                break;
            }
        }
    }

}
