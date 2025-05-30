package co.edu.ucentral.disquera.Persistencia.Entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    public enum Rol {
        ADMIN,
        ARTISTA,
    }

    @Id
    @Column(name = "usu_usuario")
    @NotBlank(message = "El usuario no puede estar vacío")
    @Size(min = 3, max = 20, message = "El usuario debe tener entre 3 y 20 caracteres")
    private String usuario;

    @Column(name = "usu_contrasena", nullable = false)
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    @Column(name = "usu_nombre", nullable = false)
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @Column(name = "usu_cedula", nullable = false)
    @NotBlank(message = "La cédula no puede estar vacía")
    @Pattern(regexp = "\\d+", message = "La cédula solo puede contener números")
    private String cedula;

    @Column(name = "usu_telefono", nullable = false)
    @NotBlank(message = "El teléfono no puede estar vacío")
    @Pattern(regexp = "\\d+", message = "El teléfono solo puede contener números")
    private String telefono;

    @Column(name = "usu_correo", nullable = false)
    @Email(message = "El correo electrónico no es válido")
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    private String correo;

    @Column(name = "usu_codigo_recuperacion")
    private String codigoRecuperacion;

    @Column(name = "usu_expiracion_codigo")
    private LocalDateTime expiracionCodigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "usu_rol", nullable = false)
    private Rol rol;

    // Getters y setters
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public String getCodigoRecuperacion() { return codigoRecuperacion; }
    public void setCodigoRecuperacion(String codigoRecuperacion) { this.codigoRecuperacion = codigoRecuperacion; }
    public LocalDateTime getExpiracionCodigo() { return expiracionCodigo; }
    public void setExpiracionCodigo(LocalDateTime expiracionCodigo) { this.expiracionCodigo = expiracionCodigo; }
}
