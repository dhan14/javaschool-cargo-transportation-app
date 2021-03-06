package com.katekozlova.cargo.business.service;

import com.google.common.collect.Lists;
import com.google.maps.DirectionsApi;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import com.katekozlova.cargo.data.entity.*;
import com.katekozlova.cargo.data.repository.DriverRepository;
import com.katekozlova.cargo.data.repository.OrderRepository;
import com.katekozlova.cargo.data.repository.TruckRepository;
import com.katekozlova.cargo.data.repository.WaypointRepository;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private static final long TRUCK_SPEED = 70;
    private static final String API_KEY = "AIzaSyADJkrdtuvmf2VbH6Ruj0Y_Ftse3fyg3x4";

    private final OrderRepository orderRepository;
    private final WaypointRepository waypointRepository;
    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, WaypointRepository waypointRepository, DriverRepository driverRepository,
                        TruckRepository truckRepository) {
        this.orderRepository = orderRepository;
        this.waypointRepository = waypointRepository;
        this.driverRepository = driverRepository;
        this.truckRepository = truckRepository;
    }

    public List<Order> getAllOrders() {
        return Lists.newArrayList(orderRepository.findAll());
    }

    public Order findById(long id) {
        return orderRepository.findById(id);
    }

    public Order findByUniqueNumber(long uniqueNumber) {
        return orderRepository.findByUniqueNumber(uniqueNumber);
    }

    public List<Waypoint> getOrderWaypoints(long id) {
        return waypointRepository.findByOrder(id);
    }

    public List<Driver> getOrdersDrivers(long id) {
        return driverRepository.findByOrder(id);
    }

    public static long getDriveDist(String addrOne, String addrTwo) throws ApiException, InterruptedException, IOException {

        GeoApiContext distCalcer = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();

        DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(distCalcer);
        DistanceMatrix result = req.origins(addrOne)
                .destinations(addrTwo)
                .mode(TravelMode.DRIVING)
                .avoid(DirectionsApi.RouteRestriction.TOLLS)
                .language("en-US")
                .await();

        long distApart = result.rows[0].elements[0].distance.inMeters;

        return distApart / 1000;
    }

    public Order create(Order order) {
        order.setOrderStatus(OrderStatus.NO);
        order = orderRepository.save(order);
        logger.info("order was created: {}", order);
        return order;
    }

    public List<Truck> getTrucks(Order order) {
        City city = order.getWaypoints().get(0).getCity();
        return truckRepository.findByOrderTruckStateCapacity(TruckState.SERVICEABLE, findMaxCargoWeight(order.getWaypoints()), city);
    }


    public void saveTruckToOrder(Order order) {
        order.getTruck().setOrder(order);
        Truck tempTruck = order.getTruck();
        truckRepository.save(order.getTruck());
        logger.info("truck was changed(set order): {}", tempTruck.getOrder());
    }

    public List<Driver> getDrivers(Order order) {
        List<Driver> drivers = driverRepository.findByOrderAndCurrentCity(order.getTruck()
                .getCurrentCity().getId());
        return getDriversByHours(drivers, order);
    }

    private List<Driver> getDriversByHours(List<Driver> drivers, Order order) {
        List<Driver> appropriateDrivers = new ArrayList<>();
        final DateTime now = new DateTime();
        final DateTime endOfMonth = now.dayOfMonth().withMaximumValue().millisOfDay().withMaximumValue();
        final long hoursBetween = Hours.hoursBetween(now, endOfMonth).getHours();

        order.setTravelTime(getTravelTime(order.getWaypoints()));
        for (Driver driver : drivers) {
            if (driver.getHoursPerMonth() + Math.min(hoursBetween, order.getTravelTime()) <= 176) {
                appropriateDrivers.add(driver);
            }
        }
        return appropriateDrivers;
    }

    public void saveWaipoints(Order order, Waypoint waypoint) {
        try {
            order.getWaypoints().add(waypoint);
        } catch (NullPointerException e) {
            List<Waypoint> waypoints = new ArrayList<>();
            waypoints.add(waypoint);
            order.setWaypoints(waypoints);
        }
        logger.info("order was changed(set waypoints): {}", order);
    }

    private long findMaxCargoWeight(List<Waypoint> waypoints) {
        long maxWeight = 0;
        long sumWeight = 0;
        for (Waypoint waypoint : waypoints) {
            if (waypoint.getWaypointType().equals(WaypointType.SHIPMENT)) {
                sumWeight += waypoint.getCargo().getWeight();
                maxWeight = Math.max(maxWeight, sumWeight);
            } else {
                sumWeight -= waypoint.getCargo().getWeight();
            }
        }
        return maxWeight;
    }

    public void getOrderToDriver(Order order) {
        for (Driver driver : order.getDrivers()) {
            driver.setOrder(order);
            driver.setDriverStatus(DriverStatus.IN_SHIFT);
            driverRepository.save(driver);
            logger.info("driver was changed(setOrder): {}", driver.getOrder());
        }
    }

    public void getTruckToDriver(Order order) {
        for (Driver driver : order.getDrivers()) {
            driver.setCurrentTruck(order.getTruck());
            driverRepository.save(driver);
            logger.info("driver was changed(setTruck): {}", driver);
        }
    }

    public void saveDriversToOrder(Order order) {
        getOrderToDriver(order);
        getTruckToDriver(order);
    }

    public void getOrderIdToWaypoint(Order order) {
        for (Waypoint waypoint : order.getWaypoints()) {
            waypoint.setOrder(order);
            waypointRepository.save(waypoint);
            logger.info("waypoint was changed(setOrder): {}", waypoint);
        }
    }

    public long getTravelTime(List<Waypoint> waypoints) {
        long travelDistance = 0;
        long tempDistance = 0;

        for (int i = 0; i < waypoints.size() - 1; i++) {
            try {
                tempDistance = getDriveDist(waypoints.get(i).getCity().getName(), waypoints.get(i + 1).getCity().getName());
            } catch (ApiException | IOException e) {
                logger.error("Exception", e);
            } catch (InterruptedException e) {
                logger.error("Exception", e);
                Thread.currentThread().interrupt();
            }
            travelDistance += tempDistance;
        }
        return travelDistance / TRUCK_SPEED;
    }

    public void saveOrder(Order order) {
        order.setOrderStatus(OrderStatus.NO);
        orderRepository.save(order);
        logger.info("order was created: {}", order);
        getOrderIdToWaypoint(order);
        saveTruckToOrder(order);
        saveDriversToOrder(order);
    }

    public void setExistingOrdersTravelTime() {
        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            order.setTravelTime(50);
            logger.info("order was changed(setTravelTime): {}", order);
        }
    }

    public List<Waypoint> getTestWaypoints() {
        List<Waypoint> waypoints = waypointRepository.findAll();
        List<Waypoint> testWaypoints = new ArrayList<>();
        for(int i = 10; i < waypoints.size(); i++) {
            testWaypoints.add(waypoints.get(i));
        }
        return testWaypoints;
    }

}
