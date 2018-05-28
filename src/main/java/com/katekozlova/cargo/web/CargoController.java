package com.katekozlova.cargo.web;

import com.katekozlova.cargo.business.service.CargoService;
import com.katekozlova.cargo.data.entity.Cargo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping(value = "/cargo")
public class CargoController {

    private final CargoService cargoService;

    @Autowired
    public CargoController(CargoService cargoService) {
        this.cargoService = cargoService;
    }

    @GetMapping(value = "/list")
    public ModelAndView List() {
        List<Cargo> cargo = cargoService.getAllCargo();
        return new ModelAndView("cargo/list", "cargo", cargo);
    }

    @PostMapping("/search")
    public String searchCargo(@RequestParam("number") long cargoNumber, Model model) {
        model.addAttribute("cargo", this.cargoService.getCargoByNumber(cargoNumber));
        return "cargo/search";
    }

    @GetMapping(value = "/status")
    public ModelAndView status() {
        List<Cargo> cargo = cargoService.getAllCargo();
        return new ModelAndView("cargo/status", "cargo", cargo);
    }
}
