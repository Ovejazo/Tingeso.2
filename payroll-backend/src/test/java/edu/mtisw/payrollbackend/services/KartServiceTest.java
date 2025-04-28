package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.KartEntity;
import edu.mtisw.payrollbackend.repositories.KartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class KartServiceTest {

    @Mock
    private KartRepository kartRepository;

    @InjectMocks
    private KartService kartService;

    private KartEntity testKart;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Crear un kart de prueba
        testKart = new KartEntity();
        testKart.setId(1L);
        testKart.setName("K001");
        testKart.setState(true); // true = disponible
    }

    @Test
    public void getKart_Success() {
        // Arrange
        ArrayList<KartEntity> kartList = new ArrayList<>(Arrays.asList(testKart));
        when(kartRepository.findAll()).thenReturn(kartList);

        // Act
        ArrayList<KartEntity> result = kartService.getKart();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("K001", result.get(0).getName());
        assertTrue(result.get(0).getState());
        verify(kartRepository).findAll();
    }

    @Test
    public void saveKart_Success() {
        // Arrange
        when(kartRepository.save(any(KartEntity.class))).thenReturn(testKart);

        // Act
        KartEntity result = kartService.saveKart(testKart);

        // Assert
        assertNotNull(result);
        assertEquals("K001", result.getName());
        assertTrue(result.getState());
        verify(kartRepository).save(testKart);
    }

    @Test
    public void getKartById_Success() {
        // Arrange
        when(kartRepository.findById(1L)).thenReturn(Optional.of(testKart));

        // Act
        KartEntity result = kartService.getKartById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("K001", result.getName());
        assertTrue(result.getState());
        verify(kartRepository).findById(1L);
    }

    @Test
    public void getKartById_NotFound() {
        // Arrange
        when(kartRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(java.util.NoSuchElementException.class, () -> {
            kartService.getKartById(1L);
        });
        verify(kartRepository).findById(1L);
    }

    @Test
    public void updateKart_Success() {
        // Arrange
        KartEntity updatedKart = new KartEntity();
        updatedKart.setId(1L);
        updatedKart.setName("K002");
        updatedKart.setState(false); // false = no disponible

        when(kartRepository.save(any(KartEntity.class))).thenReturn(updatedKart);

        // Act
        KartEntity result = kartService.updateKart(updatedKart);

        // Assert
        assertNotNull(result);
        assertEquals("K002", result.getName());
        assertFalse(result.getState());
        verify(kartRepository).save(updatedKart);
    }

    @Test
    public void deleteKart_Success() throws Exception {
        // Arrange
        doNothing().when(kartRepository).deleteById(1L);

        // Act
        boolean result = kartService.deleteKart(1L);

        // Assert
        assertTrue(result);
        verify(kartRepository).deleteById(1L);
    }

    @Test
    public void deleteKart_Error() {
        // Arrange
        doThrow(new RuntimeException("Error al eliminar kart")).when(kartRepository).deleteById(1L);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            kartService.deleteKart(1L);
        });
        assertEquals("Error al eliminar kart", exception.getMessage());
        verify(kartRepository).deleteById(1L);
    }

    @Test
    public void getKart_EmptyList() {
        // Arrange
        when(kartRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        ArrayList<KartEntity> result = kartService.getKart();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(kartRepository).findAll();
    }

    @Test
    public void saveKart_ValidKartName() {
        // Arrange
        KartEntity validKart = new KartEntity();
        validKart.setId(1L);
        validKart.setName("K015");
        validKart.setState(true);

        when(kartRepository.save(any(KartEntity.class))).thenReturn(validKart);

        // Act
        KartEntity result = kartService.saveKart(validKart);

        // Assert
        assertNotNull(result);
        assertEquals("K015", result.getName());
        assertTrue(result.getState());
        verify(kartRepository).save(validKart);
    }
}