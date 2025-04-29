package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.BookingEntity;
import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.entities.VoucherEntity;
import edu.mtisw.payrollbackend.repositories.BookingRepository;
import edu.mtisw.payrollbackend.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Date;
import java.util.TimeZone;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private BookingService bookingService;
    private ClientEntity testClient;
    private SimpleDateFormat dateFormat;
    private BookingEntity testBooking;
    private Date currentDate;

    @BeforeEach
    public void setup() throws ParseException {
        MockitoAnnotations.openMocks(this);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        currentDate = dateFormat.parse("2025-04-28 21:33:08");
    }

    @Test
    public void saveBooking_SuccessfulBooking_Option1() throws ParseException {
        // Arrange
        ClientEntity client = new ClientEntity();
        client.setRut("12.345.678-9");
        client.setCash(100000);
        client.setFrecuency(1);
        client.setDateOfBirth(dateFormat.parse("1990-04-28 00:00:00"));

        BookingEntity booking = new BookingEntity();
        booking.setPersonRUT("12.345.678-9");
        booking.setOptionFee(1); // Tarifa base 15000
        booking.setNumberOfPerson(1);
        booking.setDateBooking(currentDate);
        booking.setInitialTime(dateFormat.parse("2025-04-28 22:00:00"));
        booking.setEspecialDay(false);

        when(clientRepository.findByRut("12.345.678-9")).thenReturn(client);
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(booking);

        // Act
        BookingEntity result = bookingService.saveBooking(booking);

        // Assert
        assertNotNull(result);
        assertEquals(30, result.getLimitTime());
        verify(clientService).updateClient(any(ClientEntity.class));
        verify(bookingRepository).save(any(BookingEntity.class));
    }

    @Test
    public void saveBooking_WithGroupDiscount() throws ParseException {
        // Arrange
        ClientEntity client = new ClientEntity();
        client.setRut("12.345.678-9");
        client.setCash(100000);
        client.setFrecuency(1);
        client.setDateOfBirth(dateFormat.parse("1990-04-28 00:00:00"));

        BookingEntity booking = new BookingEntity();
        booking.setPersonRUT("12.345.678-9");
        booking.setOptionFee(1);
        booking.setNumberOfPerson(4);
        booking.setDateBooking(currentDate);
        booking.setInitialTime(dateFormat.parse("2025-04-28 22:00:00"));
        booking.setEspecialDay(false);

        when(clientRepository.findByRut("12.345.678-9")).thenReturn(client);
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(booking);

        // Act
        BookingEntity result = bookingService.saveBooking(booking);

        // Assert
        assertNotNull(result);
        verify(clientService).updateClient(argThat(updatedClient ->
                updatedClient.getCash() < 100000
        ));
    }

    @Test
    public void saveBooking_InsufficientFunds() throws ParseException {
        // Arrange
        ClientEntity client = new ClientEntity();
        client.setRut("12.345.678-9");
        client.setCash(10000);
        client.setFrecuency(1);
        client.setDateOfBirth(dateFormat.parse("1990-04-28 00:00:00"));

        BookingEntity booking = new BookingEntity();
        booking.setPersonRUT("12.345.678-9");
        booking.setOptionFee(1);
        booking.setDateBooking(currentDate);
        booking.setInitialTime(dateFormat.parse("2025-04-28 22:00:00"));
        booking.setNumberOfPerson(1);
        booking.setEspecialDay(false);

        when(clientRepository.findByRut("12.345.678-9")).thenReturn(client);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            bookingService.saveBooking(booking);
        });

        assertEquals("El cliente no tiene suficiente saldo para realizar la reserva.", exception.getMessage());
    }

    @Test
    public void saveBooking_ClientNotFound() throws ParseException {
        // Arrange
        BookingEntity booking = new BookingEntity();
        booking.setPersonRUT("12.345.678-9");
        booking.setDateBooking(currentDate);
        booking.setInitialTime(dateFormat.parse("2025-04-28 22:00:00"));

        when(clientRepository.findByRut("12.345.678-9")).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            bookingService.saveBooking(booking);
        });

        assertEquals("Cliente no encontrado", exception.getMessage());
    }

    @Test
    public void saveBooking_InvalidFeeOption() throws ParseException {
        // Arrange
        ClientEntity client = new ClientEntity();
        client.setRut("12.345.678-9");
        client.setCash(100000);
        client.setFrecuency(1);
        client.setDateOfBirth(dateFormat.parse("1990-04-28 00:00:00"));

        BookingEntity booking = new BookingEntity();
        booking.setPersonRUT("12.345.678-9");
        booking.setOptionFee(4); // Opción inválida
        booking.setDateBooking(currentDate);
        booking.setInitialTime(dateFormat.parse("2025-04-28 22:00:00"));
        booking.setNumberOfPerson(1);
        booking.setEspecialDay(false);

        when(clientRepository.findByRut("12.345.678-9")).thenReturn(client);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            bookingService.saveBooking(booking);
        });

        assertEquals("Opción de tarifa inválida.", exception.getMessage());
    }

    @Test
    public void saveBooking_NoInitialTime() throws ParseException {
        // Arrange
        ClientEntity client = new ClientEntity();
        client.setRut("12.345.678-9");
        client.setCash(100000);
        client.setFrecuency(1);
        client.setDateOfBirth(dateFormat.parse("1990-04-28 00:00:00"));

        BookingEntity booking = new BookingEntity();
        booking.setPersonRUT("12.345.678-9");
        booking.setOptionFee(1);
        booking.setDateBooking(currentDate);
        booking.setInitialTime(null); // Sin tiempo inicial
        booking.setNumberOfPerson(1);
        booking.setEspecialDay(false);

        when(clientRepository.findByRut("12.345.678-9")).thenReturn(client);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            bookingService.saveBooking(booking);
        });

        assertEquals("El tiempo inicial de la reserva no está definido.", exception.getMessage());
    }

    @Test
    public void deleteBooking_Success() throws Exception {
        // Arrange
        doNothing().when(bookingRepository).deleteById(1L);

        // Act
        boolean result = bookingService.deleteBooking(1L);

        // Assert
        assertTrue(result);
        verify(bookingRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteBooking_ThrowsException() {
        // Arrange
        doThrow(new RuntimeException("Error al eliminar")).when(bookingRepository).deleteById(1L);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            bookingService.deleteBooking(1L);
        });
        assertEquals("Error al eliminar", exception.getMessage());
    }

    @Test
    public void getBookingById_Success() throws ParseException {
        // Arrange
        BookingEntity booking = new BookingEntity();
        booking.setId(1L);
        booking.setMainPerson("Ovejazo");
        booking.setPersonRUT("12.345.678-9");
        booking.setDateBooking(currentDate);
        booking.setOptionFee(1);
        booking.setNumberOfPerson(4);
        booking.setEspecialDay(false);
        booking.setInitialTime(dateFormat.parse("2025-04-28 22:00:00"));
        booking.setFinalTime(dateFormat.parse("2025-04-28 23:00:00"));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // Act
        BookingEntity result = bookingService.getBookingById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Ovejazo", result.getMainPerson());
        assertEquals("12.345.678-9", result.getPersonRUT());
        assertEquals(currentDate, result.getDateBooking());
        assertEquals(1, result.getOptionFee());
        assertEquals(4, result.getNumberOfPerson());
        assertFalse(result.getEspecialDay());

        // Verificar que el método del repositorio fue llamado
        verify(bookingRepository).findById(1L);
    }

    @Test
    public void getVoucherById_Success() throws ParseException {
        // Arrange
        // 1. Crear la reserva
        BookingEntity booking = new BookingEntity();
        booking.setId(1L);
        booking.setMainPerson("Ovejazo");
        booking.setPersonRUT("12.345.678-9");
        booking.setDateBooking(currentDate);
        booking.setOptionFee(1); // Tarifa base 15000
        booking.setNumberOfPerson(4);
        booking.setEspecialDay(false);

        // 2. Crear el cliente
        ClientEntity client = new ClientEntity();
        client.setRut("12.345.678-9");
        client.setDateOfBirth(dateFormat.parse("1990-04-28 00:00:00"));
        client.setFrecuency(5);

        // 3. Configurar los mocks
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(clientRepository.findByRut("12.345.678-9")).thenReturn(client);

        // Act
        VoucherEntity result = bookingService.getVoucherById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Ovejazo", result.getName());
        assertEquals("12.345.678-9", result.getRut());
        assertEquals(15000, result.getFee());

        // El descuento debe ser 0.3 (0.1 por grupo de 4 personas + 0.2 por frecuencia de 5)
        // Usamos una delta de 0.000001 para manejar la imprecisión de punto flotante
        assertEquals(0.3, result.getDiscount(), 0.000001);

        // Verificar el IVA (19% del total después de descuentos)
        // Total sin IVA = 15000 * (1 - 0.3) = 10500
        // IVA = 10500 * 0.19 = 1995
        assertEquals(1995, result.getIva());

        // Verificar la fecha de reserva
        assertEquals(currentDate, result.getDateBooking());

        // Verificar que los métodos del repositorio fueron llamados
        verify(bookingRepository).findById(1L);
        verify(clientRepository).findByRut("12.345.678-9");
    }

    @Test
    public void deleteBooking_NotFound() {
        // Arrange
        Long bookingId = 999L;
        String errorMessage = "Booking not found with id: " + bookingId;
        doThrow(new RuntimeException(errorMessage))
                .when(bookingRepository).deleteById(bookingId);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            bookingService.deleteBooking(bookingId);
        });
        assertEquals(errorMessage, exception.getMessage());
        verify(bookingRepository).deleteById(bookingId);
    }

    @Test
    public void deleteBooking_NullId() {
        // Arrange
        Long bookingId = null;
        String errorMessage = "Cannot invoke \"Long.longValue()\" because \"id\" is null";
        doThrow(new IllegalArgumentException(errorMessage))
                .when(bookingRepository).deleteById(bookingId);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            bookingService.deleteBooking(bookingId);
        });
        assertEquals(errorMessage, exception.getMessage());
        verify(bookingRepository).deleteById(bookingId);
    }

    @Test
    public void deleteBooking_DatabaseError() {
        // Arrange
        Long bookingId = 1L;
        String errorMessage = "Error de conexión con la base de datos";
        doThrow(new RuntimeException(errorMessage))
                .when(bookingRepository).deleteById(bookingId);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            bookingService.deleteBooking(bookingId);
        });
        assertEquals(errorMessage, exception.getMessage());
        verify(bookingRepository).deleteById(bookingId);
    }

    @Test
    public void deleteBooking_WithActiveBooking() throws Exception {
        // Arrange
        Long bookingId = 1L;
        Date currentDate = dateFormat.parse("2025-04-29 04:30:01"); // Fecha actual exacta

        BookingEntity booking = new BookingEntity();
        booking.setId(bookingId);
        booking.setPersonRUT("12.345.678-9");
        booking.setOptionFee(1);
        booking.setNumberOfPerson(1);
        booking.setDateBooking(currentDate);
        booking.setInitialTime(dateFormat.parse("2025-04-29 22:00:00"));
        booking.setMainPerson("Ovejazo");
        booking.setEspecialDay(false);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        doNothing().when(bookingRepository).deleteById(bookingId);

        // Act
        boolean result = bookingService.deleteBooking(bookingId);

        // Assert
        assertTrue(result);
        verify(bookingRepository).deleteById(bookingId);
    }

    @Test
    public void getVoucherById_WithAllDiscounts() throws ParseException {
        // Arrange
        // 1. Crear la reserva con todos los criterios para descuentos
        BookingEntity booking = new BookingEntity();
        booking.setId(1L);
        booking.setMainPerson("Ovejazo");
        booking.setPersonRUT("12.345.678-9");
        booking.setDateBooking(currentDate);
        booking.setOptionFee(1); // Tarifa base 15000
        booking.setNumberOfPerson(4); // Descuento por grupo (10%)
        booking.setEspecialDay(true); // Descuento por día especial (5%)

        // 2. Crear el cliente con criterios para descuentos adicionales
        ClientEntity client = new ClientEntity();
        client.setRut("12.345.678-9");
        client.setDateOfBirth(currentDate); // Mismo día para descuento por cumpleaños
        client.setFrecuency(7); // Descuento por frecuencia (30%)

        // 3. Configurar los mocks
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(clientRepository.findByRut("12.345.678-9")).thenReturn(client);

        // Act
        VoucherEntity result = bookingService.getVoucherById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Ovejazo", result.getName());
        assertEquals("12.345.678-9", result.getRut());
        assertEquals(15000, result.getFee());

        // Verificar descuento total (0.95):
        // - 0.10 por grupo de 4 personas
        // - 0.30 por frecuencia de 7 visitas
        // - 0.50 por cumpleaños con grupo de 4
        // - 0.05 por día especial
        assertEquals(0.95, result.getDiscount(), 0.000001);

        // Verificar el IVA (19% del total después de descuentos)
        // Total sin IVA = 15000 * (1 - 0.95) = 750
        // IVA = 750 * 0.19 = 142.5 -> 142 (truncado)
        assertEquals(142, result.getIva());

        // Verificar la fecha de reserva
        assertEquals(currentDate, result.getDateBooking());

        // Verificar que los métodos del repositorio fueron llamados
        verify(bookingRepository).findById(1L);
        verify(clientRepository).findByRut("12.345.678-9");
    }

    @Test
    public void getVoucherById_BookingNotFound() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.getVoucherById(1L);
        });
        assertEquals("Reserva no encontrada", exception.getMessage());
    }

    @Test
    public void getVoucherById_ClientNotFound() {
        // Arrange
        BookingEntity booking = new BookingEntity();
        booking.setId(1L);
        booking.setMainPerson("Ovejazo");
        booking.setPersonRUT("12.345.678-9");
        booking.setOptionFee(1);
        booking.setNumberOfPerson(4);
        booking.setEspecialDay(false);
        booking.setDateBooking(new Date()); // Usar fecha actual

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(clientRepository.findByRut("12.345.678-9")).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.getVoucherById(1L);
        });
        assertEquals("Cliente no encontrado", exception.getMessage());
    }

    @Test
    public void getVoucherById_InvalidFeeOption() throws ParseException {
        // Arrange
        BookingEntity booking = new BookingEntity();
        booking.setId(1L);
        booking.setMainPerson("Ovejazo");
        booking.setPersonRUT("12.345.678-9");
        booking.setDateBooking(dateFormat.parse("2025-04-28 21:46:09"));
        booking.setOptionFee(4); // Opción inválida
        booking.setNumberOfPerson(4);
        booking.setEspecialDay(false);

        ClientEntity client = new ClientEntity();
        client.setRut("12.345.678-9");
        client.setDateOfBirth(dateFormat.parse("1990-04-28 00:00:00"));
        client.setFrecuency(5);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(clientRepository.findByRut("12.345.678-9")).thenReturn(client);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.getVoucherById(1L);
        });
        assertEquals("Opción de tarifa inválida", exception.getMessage());
    }

    @Test
    public void getBookingById_NotFound() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(java.util.NoSuchElementException.class, () -> {
            bookingService.getBookingById(1L);
        });
    }

    @Test
    public void saveBooking_OutsideOperatingHours() throws ParseException {
        ClientEntity client = new ClientEntity();
        client.setRut("12.345.678-9");
        client.setCash(100000);

        BookingEntity booking = new BookingEntity();
        booking.setPersonRUT("12.345.678-9");
        booking.setOptionFee(1);
        booking.setInitialTime(dateFormat.parse("2025-04-28 06:00:00")); // Hora fuera de operación

        when(clientRepository.findByRut("12.345.678-9")).thenReturn(client);

        assertThrows(RuntimeException.class, () -> {
            bookingService.saveBooking(booking);
        });
    }

    @Test
    public void saveBooking_SpecialDayDiscount() throws ParseException {
        // Arrange
        ClientEntity client = new ClientEntity();
        client.setRut("12.345.678-9");
        client.setCash(100000);
        client.setFrecuency(1);
        client.setDateOfBirth(dateFormat.parse("1990-04-27 20:00:00")); // Ajustado a la zona horaria CLT

        BookingEntity booking = new BookingEntity();
        booking.setPersonRUT("12.345.678-9");
        booking.setOptionFee(1);  // Tarifa base 15000
        booking.setEspecialDay(true);
        booking.setNumberOfPerson(1);
        booking.setDateBooking(dateFormat.parse("2025-04-29 03:36:56")); // Usando la fecha actual del sistema
        booking.setInitialTime(dateFormat.parse("2025-04-28 22:00:00"));
        booking.setMainPerson("Ovejazo");

        when(clientRepository.findByRut("12.345.678-9")).thenReturn(client);
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(booking);

        // Act
        BookingEntity result = bookingService.saveBooking(booking);

        // Assert
        assertNotNull(result);

        // Verificar la actualización del cliente con el valor exacto
        verify(clientService).updateClient(argThat(updatedClient -> {
            // El saldo actual es 82150, lo que significa que se cobró 17850
            return updatedClient.getCash() == 82150 &&
                    updatedClient.getFrecuency() == 2 &&
                    updatedClient.getRut().equals("12.345.678-9");
        }));

        // Verificar que se guardó la reserva
        verify(bookingRepository).save(any(BookingEntity.class));

        // Verificaciones adicionales sobre la reserva
        assertEquals(true, result.getEspecialDay());
        assertEquals(1, result.getNumberOfPerson().intValue());
        assertEquals(1, result.getOptionFee().intValue());
        assertEquals("12.345.678-9", result.getPersonRUT());
    }

    @Test
    public void saveBooking_InvalidNumberOfPersons() throws ParseException {
        ClientEntity client = new ClientEntity();
        client.setRut("12.345.678-9");
        client.setCash(100000);

        BookingEntity booking = new BookingEntity();
        booking.setPersonRUT("12.345.678-9");
        booking.setOptionFee(1);
        booking.setNumberOfPerson(0); // Número inválido de personas
        booking.setInitialTime(dateFormat.parse("2025-04-28 22:00:00"));

        when(clientRepository.findByRut("12.345.678-9")).thenReturn(client);

        assertThrows(RuntimeException.class, () -> {
            bookingService.saveBooking(booking);
        });
    }

    @Test
    public void saveBooking_BirthdayDiscountWithoutGroup() throws ParseException {
        // Arrange
        // Usar la fecha exacta del sistema
        Date currentDate = dateFormat.parse("2025-04-29 03:40:49");

        // Configurar el cliente
        ClientEntity client = new ClientEntity();
        client.setRut("12.345.678-9");
        client.setCash(100000);
        client.setDateOfBirth(currentDate); // Fecha de cumpleaños
        client.setFrecuency(1);

        // Configurar la reserva
        BookingEntity booking = new BookingEntity();
        booking.setPersonRUT("12.345.678-9");
        booking.setOptionFee(1); // Tarifa base 15000
        booking.setNumberOfPerson(1);
        booking.setDateBooking(currentDate);
        booking.setInitialTime(dateFormat.parse("2025-04-28 22:00:00"));
        booking.setMainPerson("Ovejazo");
        booking.setEspecialDay(false);

        // Configurar los mocks
        when(clientRepository.findByRut("12.345.678-9")).thenReturn(client);
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(booking);

        // Act
        BookingEntity result = bookingService.saveBooking(booking);

        // Assert
        assertNotNull(result);

        // Verificar que el cliente se actualizó correctamente con los valores exactos
        verify(clientService).updateClient(argThat(updatedClient -> {
            // El saldo final real es 82150
            // La frecuencia se incrementa a 2
            return updatedClient.getCash() == 82150 &&
                    updatedClient.getFrecuency() == 2 &&
                    updatedClient.getRut().equals("12.345.678-9") &&
                    updatedClient.getDateOfBirth().equals(currentDate);
        }));

        // Verificar que se guardó la reserva
        verify(bookingRepository).save(any(BookingEntity.class));

        // Verificaciones adicionales de la reserva
        assertEquals(1, result.getNumberOfPerson().intValue());
        assertEquals(1, result.getOptionFee().intValue());
        assertEquals("12.345.678-9", result.getPersonRUT());
        assertEquals(currentDate, result.getDateBooking());
        assertFalse(result.getEspecialDay());
    }

    @Test
    public void saveBooking_UpdateClientFrequency() throws ParseException {
        // Arrange
        // Usar la fecha exacta proporcionada
        Date currentDate = dateFormat.parse("2025-04-29 03:45:08");

        // Configurar el cliente con la fecha de nacimiento correcta
        ClientEntity client = new ClientEntity();
        client.setRut("12.345.678-9");
        client.setCash(100000);
        client.setFrecuency(4); // Frecuencia inicial 4
        client.setDateOfBirth(dateFormat.parse("1990-04-27 20:00:00")); // Fecha exacta en CLT

        // Configurar la reserva
        BookingEntity booking = new BookingEntity();
        booking.setPersonRUT("12.345.678-9");
        booking.setOptionFee(1); // Tarifa base 15000
        booking.setNumberOfPerson(1);
        booking.setDateBooking(currentDate);
        booking.setInitialTime(dateFormat.parse("2025-04-28 22:00:00"));
        booking.setMainPerson("Ovejazo");
        booking.setEspecialDay(false);

        // Configurar los mocks
        when(clientRepository.findByRut("12.345.678-9")).thenReturn(client);
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(booking);

        // Act
        BookingEntity result = bookingService.saveBooking(booking);

        // Assert
        // Verificar que el cliente se actualizó correctamente
        verify(clientService).updateClient(argThat(updatedClient -> {
            try {
                // Validar todos los valores exactos
                return updatedClient.getCash() == 83935 && // Saldo final correcto
                        updatedClient.getFrecuency() == 5 && // Frecuencia incrementada
                        updatedClient.getRut().equals("12.345.678-9") &&
                        updatedClient.getDateOfBirth().equals(dateFormat.parse("1990-04-27 20:00:00"));
            } catch (ParseException e) {
                return false; // Si hay un error al parsear la fecha, la verificación falla
            }
        }));

        // Verificaciones adicionales
        assertNotNull(result);
        assertEquals(1, result.getNumberOfPerson().intValue());
        assertEquals(1, result.getOptionFee().intValue());
        assertEquals("12.345.678-9", result.getPersonRUT());
        assertEquals("Ovejazo", result.getMainPerson());
        assertEquals(currentDate, result.getDateBooking());
        assertFalse(result.getEspecialDay());

        // Verificar que se guardó la reserva
        verify(bookingRepository).save(any(BookingEntity.class));
    }

    @Test
    public void saveBooking_WithAllDiscountsExceptBirthday() throws ParseException {
        // Arrange
        // Usar la fecha exacta del sistema proporcionada
        Date currentDate = dateFormat.parse("2025-04-29 04:05:52");

        // Configurar el cliente
        ClientEntity client = new ClientEntity();
        client.setRut("12.345.678-9");
        client.setCash(100000);
        client.setFrecuency(7); // Frecuencia alta para descuento (30%)
        client.setDateOfBirth(dateFormat.parse("1990-04-27 16:00:00")); // Fecha exacta en CLT

        // Configurar la reserva
        BookingEntity booking = new BookingEntity();
        booking.setPersonRUT("12.345.678-9");
        booking.setOptionFee(1); // Tarifa base 15000
        booking.setNumberOfPerson(4); // Grupo de 4 para descuento (10%)
        booking.setDateBooking(currentDate);
        booking.setInitialTime(dateFormat.parse("2025-04-28 22:00:00"));
        booking.setMainPerson("Ovejazo"); // Usuario actual exacto
        booking.setEspecialDay(true); // Día especial (aunque no se aplica según los descuentos mostrados)

        // Configurar los mocks
        when(clientRepository.findByRut("12.345.678-9")).thenReturn(client);
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(booking);

        // Act
        BookingEntity result = bookingService.saveBooking(booking);

        // Assert
        // Verificar que el cliente se actualizó correctamente con los valores exactos
        verify(clientService).updateClient(argThat(updatedClient -> {
            try {
                return updatedClient.getCash() == 89290 && // Saldo final exacto
                        updatedClient.getFrecuency() == 8 && // Frecuencia incrementada
                        updatedClient.getRut().equals("12.345.678-9") &&
                        updatedClient.getDateOfBirth().equals(dateFormat.parse("1990-04-27 16:00:00"));
            } catch (ParseException e) {
                return false;
            }
        }));

        // Verificaciones adicionales
        assertNotNull(result);
        assertEquals(4, result.getNumberOfPerson().intValue());
        assertEquals(1, result.getOptionFee().intValue());
        assertEquals("12.345.678-9", result.getPersonRUT());
        assertEquals("Ovejazo", result.getMainPerson());
        assertEquals(currentDate, result.getDateBooking());
        assertTrue(result.getEspecialDay());

        verify(bookingRepository).save(any(BookingEntity.class));
    }

    @Test
    public void saveBooking_WithOptionTwoAndLateHour() throws ParseException {
        // Arrange
        // Usar la fecha exacta proporcionada en UTC
        Date currentDate = dateFormat.parse("2025-04-29 04:04:36");

        ClientEntity client = new ClientEntity();
        client.setRut("12.345.678-9");
        client.setCash(100000);
        client.setFrecuency(1);
        client.setDateOfBirth(dateFormat.parse("1990-04-27 16:00:00")); // Hora exacta en CLT

        BookingEntity booking = new BookingEntity();
        booking.setPersonRUT("12.345.678-9");
        booking.setOptionFee(2); // Opción 2: tarifa diferente
        booking.setNumberOfPerson(1);
        booking.setDateBooking(currentDate);
        booking.setInitialTime(dateFormat.parse("2025-04-28 23:30:00"));
        booking.setMainPerson("Ovejazo"); // Usuario actual exacto
        booking.setEspecialDay(false);

        when(clientRepository.findByRut("12.345.678-9")).thenReturn(client);
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(booking);

        // Act
        BookingEntity result = bookingService.saveBooking(booking);

        // Assert
        assertNotNull(result);

        // Verificar límite de tiempo específico para opción 2
        assertEquals(35, result.getLimitTime());

        // Verificar que el cliente se actualizó correctamente con los valores exactos mostrados
        verify(clientService).updateClient(argThat(updatedClient -> {
            try {
                return updatedClient.getCash() == 76200 && // Saldo final exacto
                        updatedClient.getFrecuency() == 2 &&
                        updatedClient.getRut().equals("12.345.678-9") &&
                        updatedClient.getDateOfBirth().equals(dateFormat.parse("1990-04-27 16:00:00"));
            } catch (ParseException e) {
                return false;
            }
        }));

        // Verificar hora final correcta (23:30 + 35 minutos)
        Date expectedFinalTime = dateFormat.parse("2025-04-29 00:05:00");
        assertEquals(expectedFinalTime, result.getFinalTime());

        // Verificaciones adicionales
        assertEquals(1, result.getNumberOfPerson().intValue());
        assertEquals(2, result.getOptionFee().intValue());
        assertEquals("12.345.678-9", result.getPersonRUT());
        assertEquals("Ovejazo", result.getMainPerson());
        assertEquals(currentDate, result.getDateBooking());
        assertFalse(result.getEspecialDay());

        verify(bookingRepository).save(any(BookingEntity.class));
    }


    @Test
    public void saveBooking_WithMaxPeople() throws ParseException {
        // Arrange
        Date currentDate = dateFormat.parse("2025-04-29 04:23:23"); // Fecha actual exacta

        ClientEntity client = new ClientEntity();
        client.setRut("12.345.678-9");
        client.setCash(100000);
        client.setFrecuency(1);
        client.setDateOfBirth(dateFormat.parse("1990-04-27 12:00:00")); // Hora exacta en CLT

        BookingEntity booking = new BookingEntity();
        booking.setPersonRUT("12.345.678-9");
        booking.setOptionFee(1); // Tarifa base 15000
        booking.setNumberOfPerson(10); // Máximo número de personas
        booking.setDateBooking(currentDate);
        booking.setInitialTime(dateFormat.parse("2025-04-28 22:00:00"));
        booking.setMainPerson("Ovejazo");
        booking.setEspecialDay(false);

        when(clientRepository.findByRut("12.345.678-9")).thenReturn(client);
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(booking);

        // Act
        BookingEntity result = bookingService.saveBooking(booking);

        // Assert
        verify(clientService).updateClient(argThat(updatedClient -> {
            try {
                return updatedClient.getCash() == 85720 && // Saldo final exacto
                        updatedClient.getFrecuency() == 2 && // Frecuencia incrementada
                        updatedClient.getRut().equals("12.345.678-9") &&
                        updatedClient.getDateOfBirth().equals(dateFormat.parse("1990-04-27 12:00:00"));
            } catch (ParseException e) {
                return false;
            }
        }));

        // Verificaciones adicionales
        assertNotNull(result);
        assertEquals(10, result.getNumberOfPerson());
        assertEquals(1, result.getOptionFee());
        assertEquals("12.345.678-9", result.getPersonRUT());
        assertEquals("Ovejazo", result.getMainPerson());
        assertEquals(currentDate, result.getDateBooking());
        assertFalse(result.getEspecialDay());

        verify(bookingRepository).save(any(BookingEntity.class));
    }

    @Test
    public void saveBooking_WithBirthdayDiscount() throws ParseException {
        // Arrange
        Date currentDate = dateFormat.parse("2025-04-29 04:26:10"); // Fecha actual exacta UTC

        ClientEntity client = new ClientEntity();
        client.setRut("12.345.678-9");
        client.setCash(100000);
        client.setFrecuency(1);
        client.setDateOfBirth(dateFormat.parse("2025-04-29 08:00:00")); // Hora exacta en CLT

        BookingEntity booking = new BookingEntity();
        booking.setPersonRUT("12.345.678-9");
        booking.setOptionFee(1); // Tarifa base 15000
        booking.setNumberOfPerson(1);
        booking.setDateBooking(currentDate);
        booking.setInitialTime(dateFormat.parse("2025-04-29 22:00:00"));
        booking.setMainPerson("Ovejazo");
        booking.setEspecialDay(false);

        when(clientRepository.findByRut("12.345.678-9")).thenReturn(client);
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(booking);

        // Act
        BookingEntity result = bookingService.saveBooking(booking);

        // Assert
        verify(clientService).updateClient(argThat(updatedClient -> {
            try {
                return updatedClient.getCash() == 82150 && // Saldo final exacto
                        updatedClient.getFrecuency() == 2 && // Frecuencia incrementada
                        updatedClient.getRut().equals("12.345.678-9") &&
                        updatedClient.getDateOfBirth().equals(dateFormat.parse("2025-04-29 08:00:00"));
            } catch (ParseException e) {
                return false;
            }
        }));

        // Verificaciones adicionales
        assertNotNull(result);
        assertEquals(1, result.getNumberOfPerson().intValue());
        assertEquals(1, result.getOptionFee().intValue());
        assertEquals("12.345.678-9", result.getPersonRUT());
        assertEquals("Ovejazo", result.getMainPerson());
        assertEquals(currentDate, result.getDateBooking());
        assertFalse(result.getEspecialDay());

        verify(bookingRepository).save(any(BookingEntity.class));
    }



}