package at.fhcampuswien.carrental.carrentalservice.restservice;



import at.fhcampuswien.carrental.carrentalservice.entity.CarAttribute;
import at.fhcampuswien.carrental.carrentalservice.repository.CarRepository;
import at.fhcampuswien.carrental.carrentalservice.services.CurrencyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@CrossOrigin(origins = "*")
public class CarController {

    @Autowired
    private CarRepository repo;

    CurrencyConverter currencyConverter = new CurrencyConverter();

    @GetMapping("v1/Cars")
    List<CarAttribute> getCars(@RequestParam(defaultValue = "USD") String currency) {
        return convertCurrency(currency,repo.findAll());
    }

    @GetMapping("v1/Cars/{id}")
    CarAttribute getCar(@PathVariable int id){

        Optional<CarAttribute> optionalCar = repo.findById(id);

        return optionalCar.orElse(null);

    }

    @PostMapping("v1/Cars")
    ResponseEntity<Void> saveCar(@RequestBody List<CarAttribute> newCar) {

        if (!newCar.isEmpty()){
            repo.saveAll(newCar);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No value found");
    }

    private List<CarAttribute> convertCurrency(String currency, Iterable<CarAttribute> carAttributes){
        List<CarAttribute> carList = new ArrayList<>();
        double conversionRate = 1;
        if (!currency.equals("USD")){
            conversionRate = currencyConverter.convertCurrency(currency, 1d);
        }
        System.out.println(conversionRate);
        for (CarAttribute carAttribute : carAttributes) {
            carAttribute.setPriceusd(roundDouble(carAttribute.getPriceusd() * conversionRate));
            carList.add(carAttribute);
        }

        if (carList.isEmpty()){
            return null;
        }
        return carList;
    }

    private double roundDouble(double price){
        String priceStr = String.format("%.2f",price).replace(",",".");
        return Double.parseDouble(priceStr);
    }

}
