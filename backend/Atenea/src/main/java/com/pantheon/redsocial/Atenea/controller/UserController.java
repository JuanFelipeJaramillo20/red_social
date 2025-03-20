package com.pantheon.redsocial.Atenea.controller;

import com.pantheon.redsocial.Atenea.model.User;
import com.pantheon.redsocial.Atenea.payload.UserRegisterRequest;
import com.pantheon.redsocial.Atenea.payload.UserSearchResponse;
import com.pantheon.redsocial.Atenea.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Gestión de Usuarios", description = "Operaciones relacionadas con la gestión de usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Registra un nuevo usuario.
     *
     * @param userRegisterRequest Datos del usuario a registrar.
     * @return Respuesta con el usuario creado o un mensaje de error.
     */
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea una cuenta de usuario con nombre de usuario, correo electrónico y contraseña.")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequest userRegisterRequest) {
        try {
            User createdUser = userService.registerUser(userRegisterRequest);
            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * Obtiene la información del perfil del usuario autenticado.
     *
     * @param userDetails Información del usuario autenticado.
     * @return Perfil del usuario.
     */
    @Operation(summary = "Obtener el perfil del usuario autenticado", description = "Devuelve los detalles del perfil del usuario actualmente autenticado.")
    @GetMapping("/me")
    public ResponseEntity<User> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userService.getUserProfile(username);
        return ResponseEntity.ok(user);
    }

    /**
     * Actualiza la información del perfil del usuario autenticado.
     *
     * @param userDetails Información del usuario autenticado.
     * @param updatedUser Datos actualizados del perfil del usuario.
     * @return Perfil actualizado del usuario.
     */
    @Operation(summary = "Actualizar el perfil del usuario autenticado", description = "Permite al usuario autenticado actualizar los detalles de su perfil.")
    @PutMapping("/me")
    public ResponseEntity<User> updateUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody User updatedUser) {
        String username = userDetails.getUsername();
        User updated = userService.updateUserProfile(username, updatedUser);
        return ResponseEntity.ok(updated);
    }

    /**
     * Busca usuarios por nombre de usuario o nombre completo con paginación.
     *
     * @param userDetails Información del usuario autenticado.
     * @param keyword Palabra clave de búsqueda.
     * @param page Número de página (por defecto: 0).
     * @param size Cantidad de resultados por página (por defecto: 10).
     * @return Lista de usuarios que coinciden con la búsqueda.
     */
    @Operation(summary = "Buscar usuarios con paginación", description = "Permite a los usuarios autenticados buscar otros usuarios por nombre de usuario o nombre completo.")
    @GetMapping("/search")
    public ResponseEntity<Page<UserSearchResponse>> searchUsers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Page<User> usersPage = userService.searchUsers(keyword, page, size);

        Page<UserSearchResponse> responsePage = usersPage.map(user ->
                new UserSearchResponse(user.getUsername(), user.getFullName(), user.getAvatarUrl()));

        return ResponseEntity.ok(responsePage);
    }

    /**
     * Solicita un token para restablecer la contraseña.
     *
     * @param email Correo electrónico del usuario que solicita el restablecimiento.
     * @return Mensaje de confirmación junto con el token generado.
     */
    @Operation(summary = "Solicitar un token de restablecimiento de contraseña", description = "Genera un token de restablecimiento de contraseña para un usuario con un correo electrónico válido.")
    @PostMapping("/reset-password/request")
    public ResponseEntity<?> requestPasswordReset(@RequestParam String email) {
        String resetToken = userService.generatePasswordResetToken(email);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Token de restablecimiento de contraseña generado.");
        response.put("resetToken", resetToken);

        return ResponseEntity.ok(response);
    }

    /**
     * Restablece la contraseña utilizando un token válido.
     *
     * @param token Token de restablecimiento proporcionado por el usuario.
     * @param newPassword Nueva contraseña a establecer.
     * @return Mensaje de confirmación del restablecimiento.
     */
    @Operation(summary = "Restablecer contraseña usando un token", description = "Permite a los usuarios restablecer su contraseña utilizando un token de restablecimiento válido.")
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<?> confirmPasswordReset(@RequestParam String token, @RequestParam String newPassword) {
        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok(Map.of("message", "La contraseña ha sido restablecida exitosamente."));
    }
}
