package at.fhcampuswien.carrental.carrentalservice.restservice;


import at.fhcampuswien.carrental.carrentalservice.entity.CustomerAttribute;
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
            final JwtResponse response = new JwtResponse(token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    //change to JWT/Tokens, therefore they invalidate themself after the given period
//    @PostMapping("v1/Customers/logout")
//    ResponseEntity<Void> deleteSession(@RequestBody Session session) {
//        System.out.println("Session Logout " + session);
//        System.out.println("Sessions " + Sessions);
//        if (Sessions.contains(session))
//        {
//            Sessions.remove(session);
//            return new ResponseEntity<>(HttpStatus.OK);
//        }
//        else
//        {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no session under that id");
//        }
//    }

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

//TODO Change User attributes later on

//    @PutMapping("v1/Customers/{sessionID}")
//    String editCustomer(@PathVariable int sessionID,@RequestBody Customer editedCustomer){
//
//        if (null==Sessions.stream().filter(session -> sessionID==session.getSessionID()).findAny().orElse(null))
//        {
//            //TODO:Customer wird in der Datenbank erstezt/Attribute verändert
//            return "User info has been changed";
//        }
//        else
//        {
//            return "no session under that id";
//        }
//
//    }

//    @GetMapping("v1/Customers/{sessionID}")
//    Customer getCustomerDetails(@PathVariable Integer sessionID){
//
//        Session currentSession =Sessions.stream().filter(session -> sessionID==session.getSessionID()).findAny().orElse(null);
//
//        if (Sessions.contains(currentSession))
//        {
//            //TODO: Customer von der Datenbank holen
//            return Customers.stream().filter(customer -> currentSession.getAccountMail() == customer.getEmail()).findAny().orElse(null);
//        }
//        else
//        {
//            return null;
//        }
//
//    }

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
