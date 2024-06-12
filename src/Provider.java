import java.sql.ResultSet;
import java.sql.SQLException;

public class Provider {
    private String id;
    private String name;
    private String address;
    private String phone;

    /**
     * Constructor para crear un nuevo proveedor que se trajo de la base de datos
     * @param provider ResultSet con los datos del proveedor
     */
    public Provider(ResultSet provider) {
        try {
            this.id = provider.getString("providerID");
            this.name = provider.getString("providerName");
            this.address = provider.getString("providerAddress");
            this.phone = provider.getString("providerPhone");
            provider.close();
        } catch (SQLException e) {
            System.out.println("Error al obtener los datos del proveedor: " + e.getMessage() + " codigo de error: " + e.getSQLState());
        }
    }

    /**
     * Constructor para crear un proveedor en la base de datos
     * @param name Nombre del proveedor
     * @param address Direccion del proveedor
     * @param phone Telefono del proveedor
     */
    public Provider(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
