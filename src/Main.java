import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException {
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct("12412421");
        cart.addProduct("1234567890123");
        cart.addProduct("1234567890123");
        cart.showCart();
        cart.checkout();
        cart.showCart();
    }
}
