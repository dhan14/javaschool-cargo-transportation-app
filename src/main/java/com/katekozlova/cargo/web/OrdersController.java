package com.katekozlova.cargo.web;

import com.katekozlova.cargo.business.service.OrderService;
import com.katekozlova.cargo.business.service.WaypointService;
import com.katekozlova.cargo.business.validation.OrderValidator;
import com.katekozlova.cargo.data.entity.Driver;
import com.katekozlova.cargo.data.entity.Order;
import com.katekozlova.cargo.data.entity.Truck;
import com.katekozlova.cargo.data.entity.Waypoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping(value = "/orders")
public class OrdersController {

    private final OrderService orderService;


    @Autowired
    public OrdersController(OrderService orderService) {
        this.orderService = orderService;
    }


    @GetMapping(value = "/list")
    public ModelAndView list() {
        List<Order> orders = orderService.getAllOrders();
        orderService.setExistingOrdersTravelTime();
        return new ModelAndView("orders/list", "orders", orders);
    }

    @GetMapping(value = "/waypoints/{id}")
    public ModelAndView getWaypoints(@PathVariable("id") long id) {
        List<Waypoint> waypoints = orderService.getOrderWaypoints(id);
        return new ModelAndView("orders/waypoints", "waypoints", waypoints);
    }

    @GetMapping(value = "/drivers/{id}")
    public ModelAndView getDriversToOrder(@PathVariable("id") long id) {
        List<Driver> drivers = orderService.getOrdersDrivers(id);
        return new ModelAndView("orders/drivers", "drivers", drivers);
    }

    @GetMapping(value = "/{id}/trucks")
    public ModelAndView getTrucks(@PathVariable("id") long id, Model model) {
        List<Truck> trucks = orderService.getTrucks(orderService.findById(id));
        Order order = orderService.findById(id);
        model.addAttribute("order", order);
        return new ModelAndView("orders/trucks", "trucks", trucks);
    }

    @GetMapping(value = "/status")
    public ModelAndView getStatus() {
        List<Order> orders = orderService.getAllOrders();
        return new ModelAndView("orders/status", "orders", orders);
    }

    @GetMapping(value = "/create")
    public String createNewOrder() {
        return "orders/create";
    }
}
