package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.BookingEntity;
import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.entities.EmployeeEntity;
import edu.mtisw.payrollbackend.repositories.BookingRepository;
import edu.mtisw.payrollbackend.repositories.ClientRepository;
import edu.mtisw.payrollbackend.services.ClientService;

import org.hibernate.internal.build.AllowPrintStacktrace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class BookingService {
    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    ClientService clientService;

    public ArrayList<BookingEntity> getBooking(){
        return (ArrayList<BookingEntity>) bookingRepository.findAll();
    }

    public ClientEntity updateClient(ClientEntity client) {
        return clientRepository.save(client);
    }

    public BookingEntity saveBooking(BookingEntity booking){
        /*
        * Aquí la reserva se hara dependiendo de la tarifa que escoja el cliente
        */

        /*
        La opción 1 de tarifa tiene:
        * Un valor de 15.000.
        * Una duración de 10 minutos máximos.
        * Una reserva de 30 minutos máximos.
        */
        //Conseguimos al cliente que va a pagar.
        ClientEntity client = clientRepository.findByRut(booking.getPersonRUT());
        if (client == null) {
            throw new RuntimeException("Cliente no encontrado");
        }

        //Vamos a conseguir los valores de cada opción de tarifa, la duración y las vueltas posibles
        int tarifaBase = 0;
        int duracionReservaMin = 0;
        int vueltas = 0;

        switch (booking.getOptionFee()) {
            case 1:
                tarifaBase = 15000;
                duracionReservaMin = 30;
                vueltas = 10;
                break;
            case 2:
                tarifaBase = 20000;
                duracionReservaMin = 30;
                vueltas = 15;
                break;
            case 3:
                tarifaBase = 25000;
                duracionReservaMin = 35;
                vueltas = 20;
                break;
            default:
                throw new RuntimeException("Opción de tarifa inválida.");
        }

        //Colocamos el tiempo maximo de la reserva
        booking.setLimitTime(duracionReservaMin);

        //vamos a pensar en los descuentos por grupo
        int nPersonas = booking.getNumberOfPerson();
        double descuentoGrupo = 0;

        if (nPersonas >= 3 && nPersonas <= 5) descuentoGrupo = 0.10;
        else if (nPersonas >= 6 && nPersonas <= 10) descuentoGrupo = 0.20;
        else if (nPersonas >= 11 && nPersonas <= 15) descuentoGrupo = 0.30;

        // 3. Descuento por frecuencia
        int visitasCliente = client.getFrecuency();
        double descuentoFrecuencia = 0;

        if (visitasCliente >= 7) descuentoFrecuencia = 0.30;
        else if (visitasCliente >= 5) descuentoFrecuencia = 0.20;
        else if (visitasCliente >= 2) descuentoFrecuencia = 0.10;

        //Vamos a conseguir el cumpleaños del cliente
        double descuentoCumpleaños = 0;
        boolean esCumpleaños = client.getDateOfBirth() == booking.getDateBooking();
        if((esCumpleaños) && (nPersonas >= 3)){
            if (nPersonas <= 5) descuentoCumpleaños = 0.5;
        }

        //Aplicamos los descuentos
        double descuentoTotal = descuentoGrupo + descuentoFrecuencia + descuentoCumpleaños;
        System.out.println("\nDescuento grupo: " + descuentoGrupo);
        System.out.println("\nDescuento Frecuencia: " + descuentoFrecuencia);
        System.out.println("\nDescuento Cumpleaños: " + descuentoCumpleaños);
        double totalSinIVA = tarifaBase - (1 * descuentoTotal);

        /*
        * Calculamos el iva
        * Conseguimos el valor del IVA obteniendo el total multiplicado por 0.19
        * Luego le sumamos ese valor al total sin iva obteniendo el total con el iva incluido
        * */
        double IVA = totalSinIVA * 0.19;
        double totalConIVA = totalSinIVA + IVA;


        client.setCash((int) (client.getCash() - totalConIVA));
        client.setFrecuency(client.getFrecuency() + 1); // o según políticas del negocio
        clientService.updateClient(client);


        return bookingRepository.save(booking);

        /*
        if(booking.getOptionFee() == 1){
            valorTarifa = 15000;
            /*
            * Hay que obtener al cliente y restarle el dinero y hacerle un descuento si es que es feriado
            * o si son muchas personas en un grupo


            //encuentro al cliente
            ClientEntity client = clientRepository.findByRut(booking.getPersonRUT());

            //Consigo el dinero del cliente
            client.setCash(client.getCash() - valorTarifa);

            //Se puede hacer un update para actualizar al cliente desde la reserva.
            clientService.updateClient(client);

            return bookingRepository.save(booking);

        //la tarifa vale 20.000
        } else if (booking.getOptionFee() == 2) {
            valorTarifa = 20000;


            return bookingRepository.save(booking);

        //La tarifa vale 25.000
        } else if (booking.getOptionFee() == 3) {
            valorTarifa = 25000;


            return bookingRepository.save(booking);
        }
        */

    }
}
