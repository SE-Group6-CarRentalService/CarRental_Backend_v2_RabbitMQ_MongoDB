package at.fhcampuswien.carrental.carrentalservice.restservice;

import at.fhcampuswien.carrental.carrentalservice.entity.CarAttribute;
import at.fhcampuswien.carrental.carrentalservice.entity.RentalAttribute;
import at.fhcampuswien.carrental.carrentalservice.repository.CarRepository;
import at.fhcampuswien.carrental.carrentalservice.repository.RentalRepository;
import at.fhcampuswien.carrental.carrentalservice.services.CurrencyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@CrossOrigin(origins = "*")
public class RentalController {

    @Autowired
    RentalRepository repo;

    @Autowired
    CarRepository carRepository;

    CurrencyConverter currencyConverter = new CurrencyConverter();

    @GetMapping("v1/rentals")
    List<RentalAttribute> getRentals(@RequestParam(defaultValue = "USD") String currency) {
        return convertCurrency(currency, repo.findAll());
}

    @PostMapping("v1/rentals")
    ResponseEntity<Void> createRental(@RequestBody RentalAttribute newRental) {
        if(repo.findByCarId(newRental.getCarId()).isEmpty()) {
            RentalAttribute newRentalID = new RentalAttribute();
            newRental.setId(newRentalID.getId());
            repo.save(newRental);
            Optional<CarAttribute> rentedCarOpt = carRepository.findById(newRental.getCarId());
            if (rentedCarOpt.isPresent()){
                CarAttribute rentedCar = rentedCarOpt.get();
                rentedCar.setRentalId(String.valueOf(newRental.getId()));
                carRepository.save(rentedCar);
            }


            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        else{
            System.out.println("Car is already rented or not found!");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Car is already rented");
        }

    }

    @PutMapping("v1/rentals/{rentalId}")
    ResponseEntity<Void> returnRental(@PathVariable int rentalId) {
        Optional<RentalAttribute> rentalOpt = repo.findById(rentalId);
        if (rentalOpt.isPresent()){
            RentalAttribute rental = rentalOpt.get();
            if (rental.getRented().equals("true")){
                rental.setRented("false");
                rental.setReturnDate(getReturnDate());
                returnCar(rental.getCarId());
                repo.save(rental);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("v1/rentals/{customerId}")
    List<RentalAttribute> getRentalDetails(@PathVariable int customerId, @RequestParam(defaultValue = "USD") String currency) {
        return convertCurrency(currency, repo.findByCustomerId(customerId));
    }

    @DeleteMapping("v1/rentals/{rentalId}")
    ResponseEntity<Void> deleteRental(@PathVariable int rentalId) {
        Optional<RentalAttribute> rental = repo.findById(rentalId);
        if (rental.isPresent()){
            if (rental.get().getRented().equals("true")){
                returnCar(rental.get().getCarId());
            }
        }
        repo.deleteById(rentalId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void returnCar(int carId) {
        Optional<CarAttribute> rentedCarOpt = carRepository.findById(carId);
        if (rentedCarOpt.isPresent()){
            CarAttribute rentedCar = rentedCarOpt.get();
            rentedCar.setRentalId("0");
            carRepository.save(rentedCar);
        }
    }

    private List<RentalAttribute> convertCurrency(String currency, Iterable<RentalAttribute> rentalAttributes){
        List<RentalAttribute> rentalList = new ArrayList<>();
        for (RentalAttribute rentalAttribute : rentalAttributes) {
            //rentalAttribute.setTotalCost(currencyConverter.convertCurrency(currency, rentalAttribute.getTotalCost()));
            rentalList.add(rentalAttribute);
        }

        if (rentalList.isEmpty()){
            return null;
        }
        return rentalList;
    }

    private String getReturnDate(){
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy");
        return dateFormat.format(date);
    }


}
