package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    //public BookingEntity findByRut(String rut);
    //List<BookingEntity> findByName(String category);

    /*
    @Query(value = "SELECT * FROM booking WHERE booking.rut = :rut", nativeQuery = true)
    BookingEntity findByRutNativeQuery(@Param("rut") String rut);
    */
}
