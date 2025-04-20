package edu.mtisw.payrollbackend.services;


import edu.mtisw.payrollbackend.entities.KartEntity;
import edu.mtisw.payrollbackend.repositories.KartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class KartService{

    @Autowired
    KartRepository kartRepository;

    public ArrayList<KartEntity> getKart(){
        return (ArrayList<KartEntity>) kartRepository.findAll();
    }
    public KartEntity saveKart(KartEntity kart){
        return kartRepository.save(kart);
    }
    public KartEntity getKartById(Long id){
        return kartRepository.findById(id).get();
    }




}
