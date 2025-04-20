package edu.mtisw.payrollbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "kart")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    //El estado TRUE muestra que esta disponible y FALSE si es que no esta disponible el vehiculo
    private Boolean state;

    //Los nombres van desde K001, K002, K003, …, K015.
    private String name;
}
