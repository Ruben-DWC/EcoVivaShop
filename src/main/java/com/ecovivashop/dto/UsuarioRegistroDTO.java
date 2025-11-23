package com.ecovivashop.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRegistroDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    private String telefono;
    private String dni;
    private String direccion;

    @AssertTrue(message = "Debe aceptar los términos y condiciones")
    private boolean aceptaTerminos;

    // Método para convertir a Usuario
    public com.ecovivashop.entity.Usuario toUsuario() {
        com.ecovivashop.entity.Usuario usuario = new com.ecovivashop.entity.Usuario();
        usuario.setNombre(this.nombre);
        usuario.setApellido(this.apellido);
        usuario.setEmail(this.email);
        usuario.setPassword(this.password);
        usuario.setTelefono(this.telefono);
        usuario.setDni(this.dni);
        usuario.setDireccion(this.direccion);
        return usuario;
    }
}