package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.KartEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class KartRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private KartRepository kartRepository;

    private KartEntity testKart;

    @BeforeEach
    void setUp() {
        // Crear kart de prueba
        testKart = new KartEntity();
        testKart.setName("K001");
        testKart.setState(true);  // true = disponible

        // Persistir datos de prueba
        entityManager.persist(testKart);
        entityManager.flush();
    }

    @Test
    public void save_NewKart_Success() {
        // Arrange
        KartEntity newKart = new KartEntity();
        newKart.setName("K002");
        newKart.setState(true);

        // Act
        KartEntity saved = kartRepository.save(newKart);

        // Assert
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("K002", saved.getName());
        assertTrue(saved.getState());
    }

    @Test
    public void findById_ExistingKart_ReturnsKart() {
        // Act
        Optional<KartEntity> found = kartRepository.findById(testKart.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("K001", found.get().getName());
        assertTrue(found.get().getState());
    }

    @Test
    public void findById_NonExistingKart_ReturnsEmpty() {
        // Act
        Optional<KartEntity> found = kartRepository.findById(999L);

        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    public void findAll_ReturnsAllKarts() {
        // Arrange
        KartEntity secondKart = new KartEntity();
        secondKart.setName("K002");
        secondKart.setState(false);
        entityManager.persist(secondKart);
        entityManager.flush();

        // Act
        List<KartEntity> allKarts = kartRepository.findAll();

        // Assert
        assertNotNull(allKarts);
        assertEquals(2, allKarts.size());
        assertTrue(allKarts.stream().anyMatch(k -> k.getName().equals("K001")));
        assertTrue(allKarts.stream().anyMatch(k -> k.getName().equals("K002")));
    }

    @Test
    public void deleteById_ExistingKart_Success() {
        // Arrange
        Long id = testKart.getId();

        // Act
        kartRepository.deleteById(id);
        Optional<KartEntity> found = kartRepository.findById(id);

        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    public void update_ExistingKart_Success() {
        // Arrange
        testKart.setState(false);  // Cambiar estado a no disponible

        // Act
        KartEntity updated = kartRepository.save(testKart);

        // Assert
        assertNotNull(updated);
        assertEquals(testKart.getId(), updated.getId());
        assertEquals("K001", updated.getName());
        assertFalse(updated.getState());
    }

    @Test
    public void findByRutNativeQuery_ThrowsException() {
        // Act & Assert
        assertThrows(InvalidDataAccessResourceUsageException.class, () -> {
            kartRepository.findByRutNativeQuery("K001");
        });
    }

    @Test
    public void saveMultipleKarts_ValidateNamingPattern() {
        // Arrange
        String[] validNames = {"K001", "K002", "K003", "K004", "K005",
                "K006", "K007", "K008", "K009", "K010",
                "K011", "K012", "K013", "K014", "K015"};

        for (String name : validNames) {
            if (!name.equals("K001")) { // K001 ya existe del setUp
                KartEntity kart = new KartEntity();
                kart.setName(name);
                kart.setState(true);
                entityManager.persist(kart);
            }
        }
        entityManager.flush();

        // Act
        List<KartEntity> allKarts = kartRepository.findAll();

        // Assert
        assertEquals(15, allKarts.size());
        for (String validName : validNames) {
            final String name = validName;
            assertTrue(allKarts.stream().anyMatch(k -> k.getName().equals(name)),
                    "No se encontró el kart con nombre " + name);
        }
    }

    @Test
    public void updateKartState_ValidateStateChange() {
        // Arrange
        testKart.setState(true);
        kartRepository.save(testKart);

        // Act
        testKart.setState(false);
        KartEntity updated = kartRepository.save(testKart);

        // Assert
        assertFalse(updated.getState());
        Optional<KartEntity> found = kartRepository.findById(updated.getId());
        assertTrue(found.isPresent());
        assertFalse(found.get().getState());
    }
}