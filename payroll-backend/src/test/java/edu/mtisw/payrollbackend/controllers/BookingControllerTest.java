package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.BookingEntity;
import edu.mtisw.payrollbackend.entities.VoucherEntity;
import edu.mtisw.payrollbackend.services.BookingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.mockito.Mockito.when;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void listBookings_ShouldReturnBookings() throws Exception {
        // Crear primera reserva
        BookingEntity booking1 = new BookingEntity();
        booking1.setId(1L);
        booking1.setCodigo(1001);
        booking1.setDateBooking(dateFormat.parse("2025-04-28 10:00:00"));
        booking1.setInitialTime(dateFormat.parse("2025-04-29 14:00:00"));
        booking1.setFinalTime(dateFormat.parse("2025-04-29 16:00:00"));
        booking1.setNumberOfPerson(4);
        booking1.setLimitTime(120);
        booking1.setMainPerson("Juan Pérez");
        booking1.setPersonRUT("12.345.678-9");
        booking1.setOptionFee(1);
        booking1.setEspecialDay(false);

        // Crear segunda reserva
        BookingEntity booking2 = new BookingEntity();
        booking2.setId(2L);
        booking2.setCodigo(1002);
        booking2.setDateBooking(dateFormat.parse("2025-04-28 11:00:00"));
        booking2.setInitialTime(dateFormat.parse("2025-04-29 16:00:00"));
        booking2.setFinalTime(dateFormat.parse("2025-04-29 18:00:00"));
        booking2.setNumberOfPerson(2);
        booking2.setLimitTime(120);
        booking2.setMainPerson("María González");
        booking2.setPersonRUT("98.765.432-1");
        booking2.setOptionFee(2);
        booking2.setEspecialDay(true);

        // Crear un ArrayList específicamente
        ArrayList<BookingEntity> bookingList = new ArrayList<>();
        bookingList.add(booking1);
        bookingList.add(booking2);

        // Configurar el mock para retornar un ArrayList
        when(bookingService.getBooking()).thenReturn(bookingList);

        mockMvc.perform(get("/api/v1/booking/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].mainPerson", is("Juan Pérez")))
                .andExpect(jsonPath("$[0].personRUT", is("12.345.678-9")))
                .andExpect(jsonPath("$[1].mainPerson", is("María González")))
                .andExpect(jsonPath("$[1].personRUT", is("98.765.432-1")));
    }

    @Test
    public void getBookingById_ShouldReturnBooking() throws Exception {
        BookingEntity booking = new BookingEntity();
        booking.setId(1L);
        booking.setCodigo(1001);
        booking.setDateBooking(dateFormat.parse("2025-04-28 10:00:00"));
        booking.setInitialTime(dateFormat.parse("2025-04-29 14:00:00"));
        booking.setFinalTime(dateFormat.parse("2025-04-29 16:00:00"));
        booking.setNumberOfPerson(4);
        booking.setLimitTime(120);
        booking.setMainPerson("Juan Pérez");
        booking.setPersonRUT("12.345.678-9");
        booking.setOptionFee(1);
        booking.setEspecialDay(false);

        given(bookingService.getBookingById(1L)).willReturn(booking);

        mockMvc.perform(get("/api/v1/booking/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mainPerson", is("Juan Pérez")))
                .andExpect(jsonPath("$.personRUT", is("12.345.678-9")))
                .andExpect(jsonPath("$.numberOfPerson", is(4)))
                .andExpect(jsonPath("$.codigo", is(1001)));
    }

    @Test
    public void saveBooking_ShouldReturnSavedBooking() throws Exception {
        BookingEntity savedBooking = new BookingEntity();
        savedBooking.setId(1L);
        savedBooking.setCodigo(1001);
        savedBooking.setDateBooking(dateFormat.parse("2025-04-28 10:00:00"));
        savedBooking.setInitialTime(dateFormat.parse("2025-04-29 14:00:00"));
        savedBooking.setFinalTime(dateFormat.parse("2025-04-29 16:00:00"));
        savedBooking.setNumberOfPerson(4);
        savedBooking.setLimitTime(120);
        savedBooking.setMainPerson("Juan Pérez");
        savedBooking.setPersonRUT("12.345.678-9");
        savedBooking.setOptionFee(1);
        savedBooking.setEspecialDay(false);

        given(bookingService.saveBooking(Mockito.any(BookingEntity.class))).willReturn(savedBooking);

        String bookingJson = """
            {
                "codigo": 1001,
                "dateBooking": "2025-04-28T10:00:00.000Z",
                "initialTime": "2025-04-29T14:00:00.000Z",
                "finalTime": "2025-04-29T16:00:00.000Z",
                "numberOfPerson": 4,
                "limitTime": 120,
                "mainPerson": "Juan Pérez",
                "personRUT": "12.345.678-9",
                "optionFee": 1,
                "especialDay": false
            }
            """;

        mockMvc.perform(post("/api/v1/booking/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mainPerson", is("Juan Pérez")))
                .andExpect(jsonPath("$.personRUT", is("12.345.678-9")))
                .andExpect(jsonPath("$.numberOfPerson", is(4)))
                .andExpect(jsonPath("$.codigo", is(1001)));
    }

    @Test
    public void deleteBookingById_ShouldReturn204() throws Exception {
        when(bookingService.deleteBooking(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/booking/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getVoucherById_ShouldReturnVoucher() throws Exception {
        VoucherEntity voucher = new VoucherEntity();
        voucher.setId(1L);
        // Aquí puedes agregar más campos según la estructura de tu VoucherEntity

        given(bookingService.getVoucherById(1L)).willReturn(voucher);

        mockMvc.perform(get("/api/v1/booking/voucher/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)));
    }
}