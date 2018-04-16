package com.katekozlova.cargo.business.service;


import com.google.common.collect.Lists;
import com.katekozlova.cargo.data.entity.Driver;
import com.katekozlova.cargo.data.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriversService {

    private final DriverRepository driverRepository;

    @Autowired
    public DriversService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public List<Driver> getAllDrivers() {
        return Lists.newArrayList(driverRepository.findAll());
    }

    public void deleteDriver(long id) {
        driverRepository.deleteById(id);
    }

    public Driver createAndUpdate(Driver driver) {
        return driverRepository.save(driver);
    }

    //    public Optional<Driver> findById(long id) {
////        if (driverRepository.findById(id).isPresent()) {
////
////        }
//        return driverRepository.findById(id);
//    }
    public Driver findById(long id) {
        return driverRepository.findDriverById(id);
    }
}


