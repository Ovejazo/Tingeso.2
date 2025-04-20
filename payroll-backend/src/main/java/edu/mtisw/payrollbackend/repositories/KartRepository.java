package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.KartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KartRepository extends JpaRepository<KartEntity, Long> {

    //List<KartEntity> findByNumber(Integer number);

    @Query(value = "SELECT * FROM kart WHERE kart.number = :number", nativeQuery = true)
    KartEntity findByRutNativeQuery(@Param("number") String number);

}
