package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private SimpleDateFormat dateFormat;
    private Date currentDate;
    private ClientEntity testClient;

    @BeforeEach
    public void setup() throws ParseException {
        MockitoAnnotations.openMocks(this);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentDate = dateFormat.parse("2025-04-28 21:57:56");

        testClient = new ClientEntity();
        testClient.setId(1L);
        testClient.setName("Ovejazo");
        testClient.setRut("12.345.678-9");
        testClient.setCash(100000);
        testClient.setFrecuency(5);
        testClient.setDateOfBirth(dateFormat.parse("1990-04-28 00:00:00"));
    }

    @Test
    public void getClient_Success() {
        // Arrange
        ArrayList<ClientEntity> clientList = new ArrayList<>(Arrays.asList(testClient));
        when(clientRepository.findAll()).thenReturn(clientList);

        // Act
        ArrayList<ClientEntity> result = clientService.getClient();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ovejazo", result.get(0).getName());
        verify(clientRepository).findAll();
    }

    @Test
    public void saveClient_Success() {
        // Arrange
        when(clientRepository.findByRut(testClient.getRut())).thenReturn(null);
        when(clientRepository.save(any(ClientEntity.class))).thenReturn(testClient);

        // Act
        ClientEntity result = clientService.saveClient(testClient);

        // Assert
        assertNotNull(result);
        assertEquals("Ovejazo", result.getName());
        assertEquals("12.345.678-9", result.getRut());
        assertEquals(100000, result.getCash());
        verify(clientRepository).save(testClient);
    }

    @Test
    public void saveClient_EmptyName() {
        // Arrange
        testClient.setName("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.saveClient(testClient);
        });
        assertEquals("El nombre del cliente no puede estar vacío.", exception.getMessage());
        verify(clientRepository, never()).save(any(ClientEntity.class));
    }

    @Test
    public void saveClient_EmptyRut() {
        // Arrange
        testClient.setRut("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.saveClient(testClient);
        });
        assertEquals("El RUT del cliente no puede estar vacío.", exception.getMessage());
        verify(clientRepository, never()).save(any(ClientEntity.class));
    }

    @Test
    public void saveClient_NegativeCash() {
        // Arrange
        testClient.setCash(-1000);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.saveClient(testClient);
        });
        assertEquals("El saldo del cliente no puede ser negativo.", exception.getMessage());
        verify(clientRepository, never()).save(any(ClientEntity.class));
    }

    @Test
    public void saveClient_DuplicateRut() {
        // Arrange
        when(clientRepository.findByRut(testClient.getRut())).thenReturn(testClient);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.saveClient(testClient);
        });
        assertEquals("Ya existe un cliente registrado con el RUT: 12.345.678-9", exception.getMessage());
        verify(clientRepository, never()).save(any(ClientEntity.class));
    }

    @Test
    public void updateClient_Success() {
        // Arrange
        when(clientRepository.save(any(ClientEntity.class))).thenReturn(testClient);

        // Act
        ClientEntity result = clientService.updateClient(testClient);

        // Assert
        assertNotNull(result);
        assertEquals("Ovejazo", result.getName());
        assertEquals("12.345.678-9", result.getRut());
        verify(clientRepository).save(testClient);
    }

    @Test
    public void deleteClient_Success() throws Exception {
        // Arrange
        doNothing().when(clientRepository).deleteById(1L);

        // Act
        boolean result = clientService.deleteClient(1L);

        // Assert
        assertTrue(result);
        verify(clientRepository).deleteById(1L);
    }

    @Test
    public void deleteClient_Error() {
        // Arrange
        doThrow(new RuntimeException("Error al eliminar")).when(clientRepository).deleteById(1L);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            clientService.deleteClient(1L);
        });
        assertEquals("Error al eliminar", exception.getMessage());
    }

    @Test
    public void getClientById_Success() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));

        // Act
        ClientEntity result = clientService.getClientById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Ovejazo", result.getName());
        assertEquals("12.345.678-9", result.getRut());
        verify(clientRepository).findById(1L);
    }

    @Test
    public void saveClient_NullCash() {
        // Arrange
        testClient.setCash(null);
        when(clientRepository.findByRut(testClient.getRut())).thenReturn(null);
        when(clientRepository.save(any(ClientEntity.class))).thenReturn(testClient);

        // Act
        ClientEntity result = clientService.saveClient(testClient);

        // Assert
        assertNotNull(result);
        assertEquals("Ovejazo", result.getName());
        assertEquals("12.345.678-9", result.getRut());
        assertNull(result.getCash());
        verify(clientRepository).save(testClient);
    }

    @Test
    public void saveClient_NullName() {
        // Arrange
        testClient.setName(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.saveClient(testClient);
        });
        assertEquals("El nombre del cliente no puede estar vacío.", exception.getMessage());
        verify(clientRepository, never()).save(any(ClientEntity.class));
    }

    @Test
    public void saveClient_NullRut() {
        // Arrange
        testClient.setRut(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.saveClient(testClient);
        });
        assertEquals("El RUT del cliente no puede estar vacío.", exception.getMessage());
        verify(clientRepository, never()).save(any(ClientEntity.class));
    }
}