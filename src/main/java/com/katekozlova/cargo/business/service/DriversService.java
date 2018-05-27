package com.katekozlova.cargo.business.service;


import com.google.common.collect.Lists;
import com.katekozlova.cargo.data.entity.*;
import com.katekozlova.cargo.data.repository.DriverRepository;
import com.katekozlova.cargo.data.repository.WaypointRepository;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.Hours;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DriversService {

    static final Logger logger = LoggerFactory.getLogger(DriversService.class);

    private final DriverRepository driverRepository;

    @Autowired
    public DriversService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public List<Driver> getAllDrivers() {
        return Lists.newArrayList(driverRepository.findAll());
    }

    public void deleteDriver(Driver driver) {
        Driver tempDriver = driver;
        driverRepository.delete(driver);
        logger.info("driver was deleted: {}", tempDriver);
    }

    public Driver updateDriver(Driver driver) {
        driver = driverRepository.save(driver);
        logger.info("driver was updated: {}", driver);
        return driver;
    }

    public Driver createDriver(Driver driver) {
        driver.setDriverStatus(DriverStatus.REST);
        driver = driverRepository.save(driver);
        logger.info("new driver was created: {}", driver);
        return driver;
    }

    public Driver findDriverById(long id) {
        return driverRepository.findById(id);
    }

    public List<Driver> getDriversByStatus(DriverStatus driverStatus) {
        return driverRepository.findByStatus(driverStatus);
    }



//    @Scheduled(fixedRate = 30000, initialDelay = 20000)
//    @Transactional
//    public void scheduleTask() {
//        driverRepository.updateDriver();
//        System.out.println("Траляля");
//    }
}


