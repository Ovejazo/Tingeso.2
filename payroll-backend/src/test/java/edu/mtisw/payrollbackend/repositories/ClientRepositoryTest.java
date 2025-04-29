package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ClientRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClientRepository clientRepository;

    private SimpleDateFormat dateFormat;
    private Date currentDate;
    private ClientEntity testClient;

    @BeforeEach
    void setUp() throws ParseException {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        currentDate = dateFormat.parse("2025-04-29 00:28:06");

        // Crear cliente de prueba
        testClient = new ClientEntity();
        testClient.setName("Ovejazo");
        testClient.setRut("12.345.678-9");
        testClient.setCash(100000);
        testClient.setFrecuency(5);
        testClient.setDateOfBirth(dateFormat.parse("1990-04-28 00:00:00"));

        // Limpiar y guardar datos de prueba
        entityManager.persist(testClient);
        entityManager.flush();
    }

    @Test
    public void findByRut_ExistingRut_ReturnsClient() {
        // Act
        ClientEntity found = clientRepository.findByRut("12.345.678-9");

        // Assert
        assertNotNull(found);
        assertEquals("Ovejazo", found.getName());
        assertEquals("12.345.678-9", found.getRut());
        assertEquals(100000, found.getCash());
        assertEquals(5, found.getFrecuency());
    }

    @Test
    public void findByRut_NonExistingRut_ReturnsNull() {
        // Act
        ClientEntity found = clientRepository.findByRut("99.999.999-9");

        // Assert
        assertNull(found);
    }

    @Test
    public void findByName_ExistingName_ReturnsClientList() {
        // Arrange
        ClientEntity secondClient = new ClientEntity();
        secondClient.setName("Ovejazo");
        secondClient.setRut("98.765.432-1");
        secondClient.setCash(50000);
        secondClient.setFrecuency(3);
        secondClient.setDateOfBirth(currentDate);
        entityManager.persist(secondClient);
        entityManager.flush();

        // Act
        List<ClientEntity> foundClients = clientRepository.findByName("Ovejazo");

        // Assert
        assertNotNull(foundClients);
        assertEquals(2, foundClients.size());
        assertTrue(foundClients.stream().allMatch(client -> client.getName().equals("Ovejazo")));
    }

    @Test
    public void findByName_NonExistingName_ReturnsEmptyList() {
        // Act
        List<ClientEntity> foundClients = clientRepository.findByName("NonExistingName");

        // Assert
        assertNotNull(foundClients);
        assertTrue(foundClients.isEmpty());
    }

    @Test
    public void findByRutNativeQuery_ExistingRut_ReturnsClient() {
        // Act
        ClientEntity found = clientRepository.findByRutNativeQuery("12.345.678-9");

        // Assert
        assertNotNull(found);
        assertEquals("Ovejazo", found.getName());
        assertEquals("12.345.678-9", found.getRut());
        assertEquals(100000, found.getCash());
        assertEquals(5, found.getFrecuency());
    }

    @Test
    public void findByRutNativeQuery_NonExistingRut_ReturnsNull() {
        // Act
        ClientEntity found = clientRepository.findByRutNativeQuery("99.999.999-9");

        // Assert
        assertNull(found);
    }

    @Test
    public void save_NewClient_Success() throws ParseException {
        // Arrange
        ClientEntity newClient = new ClientEntity();
        newClient.setName("NuevoCliente");
        newClient.setRut("11.111.111-1");
        newClient.setCash(75000);
        newClient.setFrecuency(1);
        newClient.setDateOfBirth(dateFormat.parse("1995-01-01 00:00:00"));

        // Act
        ClientEntity saved = clientRepository.save(newClient);
        ClientEntity found = clientRepository.findByRut("11.111.111-1");

        // Assert
        assertNotNull(saved);
        assertNotNull(found);
        assertEquals("NuevoCliente", found.getName());
        assertEquals("11.111.111-1", found.getRut());
        assertEquals(75000, found.getCash());
        assertEquals(1, found.getFrecuency());
    }

    @Test
    public void deleteById_ExistingClient_Success() {
        // Arrange
        Long id = testClient.getId();

        // Act
        clientRepository.deleteById(id);
        ClientEntity found = clientRepository.findByRut("12.345.678-9");

        // Assert
        assertNull(found);
    }

    @Test
    public void findAll_ReturnsAllClients() {
        // Arrange
        ClientEntity secondClient = new ClientEntity();
        secondClient.setName("Cliente2");
        secondClient.setRut("22.222.222-2");
        secondClient.setCash(50000);
        secondClient.setFrecuency(2);
        secondClient.setDateOfBirth(currentDate);
        entityManager.persist(secondClient);
        entityManager.flush();

        // Act
        List<ClientEntity> allClients = clientRepository.findAll();

        // Assert
        assertNotNull(allClients);
        assertEquals(2, allClients.size());
    }
}