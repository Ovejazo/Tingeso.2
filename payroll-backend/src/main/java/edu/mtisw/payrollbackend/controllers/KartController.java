package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.entities.KartEntity;
import edu.mtisw.payrollbackend.services.KartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/karts")
@CrossOrigin("*")
public class KartController {

    @Autowired
    KartService kartService;

    @GetMapping("/")
    public ResponseEntity<List<KartEntity>> listClient() {
        List<KartEntity> karts = kartService.getKart();
        return ResponseEntity.ok(karts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KartEntity> getKartbyId(@PathVariable Long id) {
        KartEntity karts = kartService.getKartById(id);
        return ResponseEntity.ok(karts);
    }

    @PostMapping("/")
    public ResponseEntity<KartEntity> saveClient(@RequestBody KartEntity kart) {
        KartEntity kartsNew = kartService.saveKart(kart);
        return ResponseEntity.ok(kartsNew);
    }
}
