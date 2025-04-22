package edu.mtisw.payrollbackend.controllers;


import edu.mtisw.payrollbackend.entities.BookingEntity;
import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/booking")
@CrossOrigin("*")
public class BookingController {
    @Autowired
    BookingService bookingService;

    @GetMapping("/")
    public ResponseEntity<List<BookingEntity>> listClient() {
        List<BookingEntity> booking = bookingService.getBooking();
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/")
    public ResponseEntity<BookingEntity> saveBooking(@RequestBody BookingEntity booking) {
        BookingEntity bookingNew = bookingService.saveBooking(booking);
        return ResponseEntity.ok(bookingNew);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteBookingById(@PathVariable Long id) throws Exception {
        var isDeleted = bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
