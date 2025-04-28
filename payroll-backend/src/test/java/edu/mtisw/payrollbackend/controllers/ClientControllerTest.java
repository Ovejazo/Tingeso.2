package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.services.ClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    private final SimpleDateFormat dateFormat;

    public ClientControllerTest() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Test
    public void listClients_ShouldReturnClients() throws Exception {
        // Crear clientes de prueba
        ClientEntity client1 = new ClientEntity();
        client1.setId(1L);
        client1.setName("Ovejazo");
        client1.setRut("12.345.678-9");
        client1.setCash(100000);
        client1.setFrecuency(5);
        client1.setDateOfBirth(dateFormat.parse("1990-01-01"));

        ClientEntity client2 = new ClientEntity();
        client2.setId(2L);
        client2.setName("María González");
        client2.setRut("98.765.432-1");
        client2.setCash(150000);
        client2.setFrecuency(3);
        client2.setDateOfBirth(dateFormat.parse("1985-06-15"));

        ArrayList<ClientEntity> clientList = new ArrayList<>();
        clientList.add(client1);
        clientList.add(client2);

        when(clientService.getClient()).thenReturn(clientList);

        mockMvc.perform(get("/api/v1/clients/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Ovejazo")))
                .andExpect(jsonPath("$[0].rut", is("12.345.678-9")))
                .andExpect(jsonPath("$[1].name", is("María González")))
                .andExpect(jsonPath("$[1].rut", is("98.765.432-1")));
    }

    @Test
    public void getClientById_ShouldReturnClient() throws Exception {
        ClientEntity client = new ClientEntity();
        client.setId(1L);
        client.setName("Ovejazo");
        client.setRut("12.345.678-9");
        client.setCash(100000);
        client.setFrecuency(5);
        client.setDateOfBirth(dateFormat.parse("1990-01-01"));

        when(clientService.getClientById(1L)).thenReturn(client);

        mockMvc.perform(get("/api/v1/clients/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Ovejazo")))
                .andExpect(jsonPath("$.rut", is("12.345.678-9")))
                .andExpect(jsonPath("$.cash", is(100000)))
                .andExpect(jsonPath("$.frecuency", is(5)));
    }

    @Test
    public void saveClient_ShouldReturnSavedClient() throws Exception {
        ClientEntity savedClient = new ClientEntity();
        savedClient.setId(1L);
        savedClient.setName("Ovejazo");
        savedClient.setRut("12.345.678-9");
        savedClient.setCash(100000);
        savedClient.setFrecuency(5);
        savedClient.setDateOfBirth(dateFormat.parse("1990-01-01"));

        when(clientService.saveClient(any(ClientEntity.class))).thenReturn(savedClient);

        String clientJson = """
            {
                "name": "Ovejazo",
                "rut": "12.345.678-9",
                "cash": 100000,
                "frecuency": 5,
                "dateOfBirth": "1990-01-01"
            }
            """;

        mockMvc.perform(post("/api/v1/clients/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Ovejazo")))
                .andExpect(jsonPath("$.rut", is("12.345.678-9")))
                .andExpect(jsonPath("$.cash", is(100000)))
                .andExpect(jsonPath("$.frecuency", is(5)));
    }

    @Test
    public void updateClient_ShouldReturnUpdatedClient() throws Exception {
        ClientEntity updatedClient = new ClientEntity();
        updatedClient.setId(1L);
        updatedClient.setName("Ovejazo Updated");
        updatedClient.setRut("12.345.678-9");
        updatedClient.setCash(150000);
        updatedClient.setFrecuency(6);
        updatedClient.setDateOfBirth(dateFormat.parse("1990-01-01"));

        when(clientService.updateClient(any(ClientEntity.class))).thenReturn(updatedClient);

        String clientJson = """
            {
                "id": 1,
                "name": "Ovejazo Updated",
                "rut": "12.345.678-9",
                "cash": 150000,
                "frecuency": 6,
                "dateOfBirth": "1990-01-01"
            }
            """;

        mockMvc.perform(put("/api/v1/clients/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Ovejazo Updated")))
                .andExpect(jsonPath("$.rut", is("12.345.678-9")))
                .andExpect(jsonPath("$.cash", is(150000)))
                .andExpect(jsonPath("$.frecuency", is(6)));
    }

    @Test
    public void deleteClientById_ShouldReturn204() throws Exception {
        when(clientService.deleteClient(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/clients/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}