package at.fhcampuswien.carrental.carrentalservice.restservice;


import at.fhcampuswien.carrental.carrentalservice.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
public class CarController {

    @Autowired
    private CarRepository repo;

    static List<Car> Cars = new ArrayList<>();

    static int lastCarid=0;

    static int lastRentalId =0;

    @GetMapping("v1/Cars")
    List<Car> getCars() {

        //TODO:Alle Cars von der DB holen

        return (List<Car>) repo.findAll();
    }

    @GetMapping("v1/Cars/{id}")
    Car getCar(@PathVariable int id)
    {
        //TODO ein Car von der DB holen
        return Cars.stream().filter(car -> id==car.getID()).findAny().orElse(null);
    }

    @PostMapping("v1/Cars")
    String saveCar(@RequestBody Car newCar) {

        lastCarid = lastCarid+1;

        //Car CartoAdd = new Car(lastCarid,Model,Year,Price,Automatic,Mileage,Fuel,InitialLocation);
        //TODO car in die DB speichern
        Cars.add(newCar);

        return "Car was saved";
    }

    @DeleteMapping("v1/Cars/{id}")
    String deleteEmployee(@PathVariable(value = "id") int id) {

        //TODO: car von der datenbank löschen
        Cars.removeIf(car -> id==car.getID());
        //repo.deleteCarBy(id);
        repo.deleteAllById(id);
        return "Car was deleted";
    }

}