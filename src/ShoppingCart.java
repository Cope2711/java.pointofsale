import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class ShoppingCart {

    // El carrito de compras es un HashMap que contiene un producto y la cantidad de veces que se ha agregado al carrito
    public Map<Product, Integer> cart = new HashMap<>();
    // El administrador de productos
    private ProductManager productManager = new ProductManager();

    /**
     * Agrega un producto al carrito de compras
     * @param barcode Codigo de barras del producto
     * @throws SQLException Si ocurre un error al obtener el producto de la base de datos
     */
    public void addProduct(String barcode) throws SQLException {
        // Se obtiene el producto de la base de datos
        Product product = productManager.getProduct(barcode);
        // Si el producto no fue encontrado o no cuenta con cantidad en inventario
        if (product == null) { System.out.println("Producto con codigo de barras: " + barcode + " no encontrado."); return; }
        // Si el producto no cuenta con cantidad en inventario
        if (product.quantity == 0) { System.out.println("Producto con codigo de barras: " + barcode + " no cuenta con cantidad en inventario."); return; }
        // Se agrega el producto al carrito
        cart.put(product, cart.getOrDefault(product, 0) + 1);
    }

    /**
     * Muestra el contenido del carrito de compras
     */
    public void showCart() {
        System.out.println("Carrito de compras:");
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            System.out.println(entry.getKey().name + " - " + entry.getValue());
        }
    }

    /**
     * Remueve un producto del carrito de compras
     * @param product Producto a remover
     * @return true si el producto fue removido exitosamente, false si no se encontro el producto en el carrito
     */
    public boolean removeProduct(Product product) {
        // Si el producto no se encuentra en el carrito
        if (!cart.containsKey(product)) { System.out.println("Producto no encontrado en el carrito."); return false; }
        // Si el producto se encuentra en el carrito
        cart.remove(product);
        return true;

    }

    /**
     * Realiza la compra de los productos en el carrito
     * @throws SQLException Si ocurre un error al registrar la venta en la base de datos
     */
    public void checkout() throws SQLException {
        // Si el carrito esta vacio
        if (cart.isEmpty()) { System.out.println("Carrito vacio, no se puede realizar la compra."); return; }
        // Si ocurrio un error al registrar la venta
        if (!productManager.registerSell(this.cart, this.getTotalPrice())) { System.out.println("Error al registrar la venta."); return; }
        System.out.println("Compra realizada exitosamente.");
        this.clearCart();
    }

    /**
     * Limpia el carrito de compras
     */
    public void clearCart() {
        // Se limpia el carrito
        this.cart.clear();
    }

    /**
     * Obtiene el precio total de los productos en el carrito
     * @return Precio total
     */
    public double getTotalPrice() {
        // Precio total
        double totalPrice = 0;
        // Por cada producto en el carrito
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            // Se obtiene el precio del producto y se multiplica por la cantidad de veces que se ha agregado al carrito
            totalPrice += entry.getKey().price * entry.getValue();
        }
        return totalPrice;
    }



}
