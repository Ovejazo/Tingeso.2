package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.KartEntity;
import edu.mtisw.payrollbackend.services.KartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KartController.class)
public class KartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KartService kartService;

    @Test
    public void listKarts_ShouldReturnKarts() throws Exception {
        // Crear karts de prueba
        KartEntity kart1 = new KartEntity();
        kart1.setId(1L);
        kart1.setState(true);
        kart1.setName("K001");

        KartEntity kart2 = new KartEntity();
        kart2.setId(2L);
        kart2.setState(false);
        kart2.setName("K002");

        ArrayList<KartEntity> kartList = new ArrayList<>();
        kartList.add(kart1);
        kartList.add(kart2);

        when(kartService.getKart()).thenReturn(kartList);

        mockMvc.perform(get("/api/v1/karts/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("K001")))
                .andExpect(jsonPath("$[0].state", is(true)))
                .andExpect(jsonPath("$[1].name", is("K002")))
                .andExpect(jsonPath("$[1].state", is(false)));
    }

    @Test
    public void getKartById_ShouldReturnKart() throws Exception {
        KartEntity kart = new KartEntity();
        kart.setId(1L);
        kart.setState(true);
        kart.setName("K001");

        when(kartService.getKartById(1L)).thenReturn(kart);

        mockMvc.perform(get("/api/v1/karts/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("K001")))
                .andExpect(jsonPath("$.state", is(true)));
    }

    @Test
    public void saveKart_ShouldReturnSavedKart() throws Exception {
        KartEntity savedKart = new KartEntity();
        savedKart.setId(1L);
        savedKart.setState(true);
        savedKart.setName("K003");

        when(kartService.saveKart(any(KartEntity.class))).thenReturn(savedKart);

        String kartJson = """
            {
                "state": true,
                "name": "K003"
            }
            """;

        mockMvc.perform(post("/api/v1/karts/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(kartJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("K003")))
                .andExpect(jsonPath("$.state", is(true)));
    }

    @Test
    public void updateKart_ShouldReturnUpdatedKart() throws Exception {
        KartEntity updatedKart = new KartEntity();
        updatedKart.setId(1L);
        updatedKart.setState(false);
        updatedKart.setName("K001");

        when(kartService.updateKart(any(KartEntity.class))).thenReturn(updatedKart);

        String kartJson = """
            {
                "id": 1,
                "state": false,
                "name": "K001"
            }
            """;

        mockMvc.perform(put("/api/v1/karts/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(kartJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("K001")))
                .andExpect(jsonPath("$.state", is(false)));
    }

    @Test
    public void deleteKartById_ShouldReturn204() throws Exception {
        when(kartService.deleteKart(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/karts/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    public void saveKart_WithInvalidName_ShouldStillWork() throws Exception {
        KartEntity savedKart = new KartEntity();
        savedKart.setId(1L);
        savedKart.setState(true);
        savedKart.setName("K015"); // Último número válido

        when(kartService.saveKart(any(KartEntity.class))).thenReturn(savedKart);

        String kartJson = """
            {
                "state": true,
                "name": "K015"
            }
            """;

        mockMvc.perform(post("/api/v1/karts/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(kartJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("K015")))
                .andExpect(jsonPath("$.state", is(true)));
    }

    @Test
    public void updateKart_StateChange_ShouldWork() throws Exception {
        // Primero creamos un kart disponible
        KartEntity updatedKart = new KartEntity();
        updatedKart.setId(1L);
        updatedKart.setState(false); // Cambiamos de disponible a no disponible
        updatedKart.setName("K001");

        when(kartService.updateKart(any(KartEntity.class))).thenReturn(updatedKart);

        String kartJson = """
            {
                "id": 1,
                "state": false,
                "name": "K001"
            }
            """;

        mockMvc.perform(put("/api/v1/karts/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(kartJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("K001")))
                .andExpect(jsonPath("$.state", is(false)));
    }
}