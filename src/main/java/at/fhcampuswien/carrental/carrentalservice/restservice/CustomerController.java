package at.fhcampuswien.carrental.carrentalservice.restservice;


import at.fhcampuswien.carrental.carrentalservice.entity.CustomerAttribute;
import at.fhcampuswien.carrental.carrentalservice.entity.RentalAttribute;
import at.fhcampuswien.carrental.carrentalservice.repository.CustomerRepository;
import at.fhcampuswien.carrental.carrentalservice.security.JwtResponse;
import at.fhcampuswien.carrental.carrentalservice.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@RestController
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerRepository repo;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @GetMapping("v1/Customers")
    List<CustomerAttribute> getCustomers() {
        return (List<CustomerAttribute>) repo.findAll();
    }

    @PostMapping("v1/Customers/login")
    ResponseEntity<JwtResponse> getCustomer(@RequestParam String email, @RequestParam String password) {
        CustomerAttribute customer;

        try {
            customer = repo.findByEmail(email);
        }catch (Exception e){
            System.out.println("No user with this email found " + email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user with this email found");
        }

        if(passwordEncoder.matches(password, customer.getPassword())) {
            final String token = jwtTokenUtil.generateToken(email);
            final JwtResponse response = new JwtResponse(token,  customer.getId());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("v1/Customers/register")
    ResponseEntity<Void> registerCustomer(@RequestBody CustomerAttribute newCustomer) {
        if(repo.findByEmail(newCustomer.getEmail())==null) {
            CustomerAttribute newCustomerID = new CustomerAttribute();
            newCustomer.setId(newCustomerID.getId());
            newCustomer.setPassword(passwordEncoder.encode(newCustomer.getPassword()));
            repo.save(newCustomer);

            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email already registered");
        }
    }

    @PutMapping("v1/Customers/{customerId}")
    ResponseEntity<Void> editCustomer(@PathVariable int customerId,@RequestBody CustomerAttribute editedCustomer){
        Optional<CustomerAttribute> customerOpt = repo.findById(customerId);
        if (customerOpt.isPresent()){
            CustomerAttribute customer = customerOpt.get();
            customer.setName(editedCustomer.getName());
            customer.setEmail(editedCustomer.getEmail());
            customer.setAddress(editedCustomer.getAddress());
            customer.setPassportNumber(editedCustomer.getPassportNumber());
            customer.setBirthdate(editedCustomer.getBirthdate());
            repo.save(customer);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("v1/Customers/{customerId}")
    ResponseEntity<CustomerAttribute> getCustomerDetails(@PathVariable int customerId){
        System.out.println("Requested Customer " + customerId);

        Optional<CustomerAttribute> customerOpt = repo.findById(Integer.valueOf(customerId));

        if(customerOpt.isPresent()) {
            CustomerAttribute customer = customerOpt.get();
            customer.setPassword("");
            return new ResponseEntity<>(customer, HttpStatus.OK);
        }
        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customer not found");
        }
    }

//    @DeleteMapping("v1/Customers/{sessionID}")
//    String deleteCustomer(@PathVariable int sessionID,@RequestBody Customer editedCustomer){
//
//        Session currentSession =Sessions.stream().filter(session -> sessionID==session.getSessionID()).findAny().orElse(null);
//
//        if (null==currentSession)
//        {
//            //TODO:Customer wird von der Datenbank gelöscht
//            Customers.removeIf(Customer -> currentSession.getAccountMail()==Customer.getEmail());
//            return "Account has been deleted";
//        }
//        else
//        {
//            return "no session under that id";
//        }
//
//    }

}
