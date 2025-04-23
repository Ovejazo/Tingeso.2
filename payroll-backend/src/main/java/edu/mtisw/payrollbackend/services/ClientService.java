package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.entities.EmployeeEntity;
import edu.mtisw.payrollbackend.repositories.ClientRepository;
import edu.mtisw.payrollbackend.repositories.EmployeeRepository;
import edu.mtisw.payrollbackend.repositories.ExtraHoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ClientService {
    @Autowired
    ClientRepository clientRepository;

    public ArrayList<ClientEntity> getClient(){
        return (ArrayList<ClientEntity>) clientRepository.findAll();
    }

    public ClientEntity saveClient(ClientEntity client){
        //Revisamos que el cliente tenga nombre.
        if (client.getName() == null || client.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }

        //Revisamos que el RUT no sea nulo.
        if (client.getRut() == null || client.getRut().trim().isEmpty()) {
            throw new IllegalArgumentException("El RUT del cliente no puede estar vacío.");
        }

        //Validamos que el saldo no sea negativo
        if (client.getCash() != null && client.getCash() < 0) {
            throw new IllegalArgumentException("El saldo del cliente no puede ser negativo.");
        }

        // Revisar que el RUT no se repita
        if (clientRepository.findByRut(client.getRut()) != null) {
            throw new IllegalArgumentException("Ya existe un cliente registrado con el RUT: " + client.getRut());
        }

        return clientRepository.save(client);
    }

    public ClientEntity getClientById(Long id){
        return clientRepository.findById(id).get();
    }

    public ClientEntity updateClient(ClientEntity client) {
        return clientRepository.save(client);
    }

    public boolean deleteClient(Long id) throws Exception {
        try{
            clientRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

}
