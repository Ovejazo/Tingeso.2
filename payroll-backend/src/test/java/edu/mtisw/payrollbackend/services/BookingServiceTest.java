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
}