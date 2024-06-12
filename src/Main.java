import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.addProduct("12412421");
        shoppingCart.addProduct("01");
        shoppingCart.checkout();
    }
}
