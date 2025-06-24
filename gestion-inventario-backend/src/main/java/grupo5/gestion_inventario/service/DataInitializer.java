package grupo5.gestion_inventario.service;

import grupo5.gestion_inventario.superpanel.model.AdminUser;
import grupo5.gestion_inventario.superpanel.repository.AdminUserRepository;
import grupo5.gestion_inventario.model.Client;
import grupo5.gestion_inventario.model.Employee;
import grupo5.gestion_inventario.model.EmployeeRole;
import grupo5.gestion_inventario.repository.ClientRepository;
import grupo5.gestion_inventario.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override

    public void run(String... args) throws Exception {
    // Forzar la creación/actualización de un usuario de prueba infalible
    try {
        // Buscamos un cliente por defecto o lo creamos si no existe
        // Esto asegura que el empleado tenga un cliente válido al cual pertenecer
        Client defaultClient = clientRepository.findById(1L).orElseGet(() -> {
            Client newClient = new Client();
            newClient.setName("Cliente Defecto");
            newClient.setEmail("cliente@defecto.com");
            newClient.setPasswordHash(passwordEncoder.encode("clientepass"));
            newClient.setEstado("ACTIVO");
            newClient.setPlan("BASICO");
            newClient.setTaxPercentage(BigDecimal.ZERO);
            newClient.setTelefono("000000");
            return clientRepository.save(newClient);
        });

        // Ahora, creamos o actualizamos el empleado de prueba
        Employee testEmployee = employeeRepository.findByEmail("tester@final.com")
                .orElse(new Employee()); // Crea uno nuevo si no existe

        testEmployee.setClient(defaultClient); // Lo asociamos al cliente
        testEmployee.setName("Usuario Final de Prueba");
        testEmployee.setEmail("tester@final.com");
        testEmployee.setPasswordHash(passwordEncoder.encode("final123")); // Contraseña simple y nueva
        testEmployee.setRole(EmployeeRole.ADMINISTRADOR);
        
        employeeRepository.save(testEmployee);
        
        System.out.println(">>> USUARIO DE PRUEBA 'tester@final.com' CON CONTRASEÑA 'final123' FUE CREADO/ACTUALIZADO EXITOSAMENTE. <<<");

    } catch (Exception e) {
        System.err.println("Error al inicializar el usuario de prueba: " + e.getMessage());
        e.printStackTrace();
    }
}
}
