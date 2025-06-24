// src/main/java/grupo5/gestion_inventario/controller/AuthController.java
package grupo5.gestion_inventario.controller;

import grupo5.gestion_inventario.config.JwtUtil;
import grupo5.gestion_inventario.clientpanel.dto.AuthRequest;
import grupo5.gestion_inventario.clientpanel.dto.AuthResponse;
import grupo5.gestion_inventario.model.Client;
import grupo5.gestion_inventario.model.Employee;
import grupo5.gestion_inventario.model.EmployeeRole;
import grupo5.gestion_inventario.superpanel.repository.AdminUserRepository;
import grupo5.gestion_inventario.repository.ClientRepository;
import grupo5.gestion_inventario.repository.EmployeeRepository;
import grupo5.gestion_inventario.superpanel.model.AdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private ClientRepository clientRepository;

// Reemplaza tu método de login actual con este
@PostMapping("/login")
public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
    try {
        System.out.println(">>> INTENTANDO AUTENTICAR AL USUARIO: " + authRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        System.out.println(">>> AUTENTICACIÓN EXITOSA PARA: " + userDetails.getUsername());

        // --- Lógica para generar el token (la misma que ya tenías) ---
        Set<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        String token;
        Long clientId = null;
        Long employeeId = null;

        if (roles.stream().anyMatch(r -> r.equals("ROLE_ADMINISTRADOR") || r.equals("ROLE_CAJERO") || r.equals("ROLE_MULTIFUNCION"))) {
            Employee employee = employeeRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new Exception("Empleado no encontrado post-auth"));
            token = jwtUtil.generateToken(employee);
            clientId = employee.getClient().getId();
            employeeId = employee.getId();
        } else {
            AdminUser admin = adminUserRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new Exception("Admin no encontrado post-auth"));
            token = jwtUtil.generateToken(admin);
        }

        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setClientId(clientId);
        resp.setEmployeeId(employeeId);
        resp.setRoles(roles);

        return ResponseEntity.ok(resp);
        // --- Fin de la lógica de éxito ---

    } catch (Exception e) {
        // ESTA ES LA TRAMPA PARA EL ERROR
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.err.println("!!!!!!!!!! ERROR DE AUTENTICACIÓN DETECTADO !!!!!!!!!!");
        System.err.println("CAUSA DEL ERROR: " + e.getClass().getName());
        System.err.println("MENSAJE DEL ERROR: " + e.getMessage());
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        // Devolvemos una respuesta de error clara al frontend
        return ResponseEntity.status(401).body("Credenciales inválidas o error en el servidor.");
    }
}
}
