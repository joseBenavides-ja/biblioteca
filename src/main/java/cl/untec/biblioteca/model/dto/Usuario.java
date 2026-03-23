package cl.untec.biblioteca.model.dto;

// DTO para transportar datos de autenticacion y perfil basico de usuario.
public class Usuario {

    private int id;
    private String username;
    private String nombre;
    private String rol;

    public Usuario() {
    }

    public Usuario(int id, String username, String nombre, String rol) {
        this.id = id;
        this.username = username;
        this.nombre = nombre;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }    

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNombre() {
        return nombre;
    }    

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }    

    public void setRol(String rol) {
        this.rol = rol;
    }
}