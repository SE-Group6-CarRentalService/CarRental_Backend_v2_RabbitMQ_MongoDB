package at.fhcampuswien.carrental.carrentalservice.restservice;


import at.fhcampuswien.carrental.carrentalservice.entity.CustomerAttribute;
import at.fhcampuswien.carrental.carrentalservice.entity.RentalAttribute;
import at.fhcampuswien.carrental.carrentalservice.repository.CustomerRepository;
import at.fhcampuswien.carrental.carrentalservice.security.JwtResponse;
import at.fhcampuswien.carrental.carrentalservice.security.JwtTokenUtil;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RestController
@CrossOrigin(origins = "*")
public class CustomerController {

    private CustomerAttribute customerAttribute;

    @Autowired
    private CustomerRepository repo;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private DirectExchange exchange;


    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    @GetMapping("v1/Customers")
    List<CustomerAttribute> getCustomers() throws Exception {
        //SEND RabbitMQ Message
        System.out.println(" [x] Requesting getCustomers() -> GET ALL CUSTOMERS");
        //List<CustomerAttribute> allCustomers = (List<CustomerAttribute>) template.convertSendAndReceive(exchange.getName(), "rpc", 1);
        CustomerAttribute customer = new CustomerAttribute();
        customer.setFunctionCallName("getCustomers");
        String serializedCustomerObject = customerAttribute.serializeToString(customer);
        String allCustomers = (String) template.convertSendAndReceive(exchange.getName(), "rpc", serializedCustomerObject);
        //(exchange.getName(), "rpc", start++);
        System.out.println(" [.] Got '" + allCustomers + "'");
        //END RabbitMQ Message

        return (List<CustomerAttribute>) customerAttribute.deserializeFromString(allCustomers);

    }

    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    @PostMapping("v1/Customers/login")
    ResponseEntity<JwtResponse> getCustomer(@RequestParam String email, @RequestParam String password) throws IOException, ClassNotFoundException {

        CustomerAttribute customer = new CustomerAttribute();
        customer.setFunctionCallName("getCustomer");
        customer.setEmail(email);
        customer.setPasswordHash(password);

        String serializedCustomerObject = customerAttribute.serializeToString(customer);

        try {
            String serializedCustomerInfo = (String) template.convertSendAndReceive(exchange.getName(), "rpc", serializedCustomerObject);
            customer = (CustomerAttribute) customerAttribute.deserializeFromString(serializedCustomerInfo);

        //TODO check if exception is triggered if no user found

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

    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    @PostMapping("v1/Customers/register")
    ResponseEntity<Void> registerCustomer(@RequestBody CustomerAttribute newCustomer) throws IOException {

        CustomerAttribute customer = new CustomerAttribute();
        customer.setFunctionCallName("getCustomer");
        customer.setEmail(newCustomer.getEmail());
        customer.setPasswordHash(newCustomer.getPassword());

        String serializedCustomerObject = customerAttribute.serializeToString(customer);

        CustomerAttribute customerFromDB;

        try {
            String serializedCustomerInfo = (String) template.convertSendAndReceive(exchange.getName(), "rpc", serializedCustomerObject);
            customerFromDB = (CustomerAttribute) customerAttribute.deserializeFromString(serializedCustomerInfo);

            //TODO check if exception is triggered if no user found

        }catch (Exception e){
            System.out.println("No user found.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user with this email found");
        }

        //TODO check if return object is null
        if(customerFromDB.getEmail()==null) {

            newCustomer.setFunctionCallName("registerCustomer");

            CustomerAttribute newCustomerID = new CustomerAttribute();
            newCustomer.setId(newCustomerID.getId());
            newCustomer.setPassword(passwordEncoder.encode(newCustomer.getPassword()));

            String serializedCustomerObjecttoSaveToDB = customerAttribute.serializeToString(newCustomer);

            String responseFromSaveToDB = (String) template.convertSendAndReceive(exchange.getName(), "rpc", serializedCustomerObjecttoSaveToDB);

            System.out.println("Response from Save To DB" + responseFromSaveToDB);

            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        //TODO check if exceptionm is throwen when email already registered;

        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email already registered");
        }
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    @PutMapping("v1/Customers/{customerId}")
    ResponseEntity<Void> editCustomer(@PathVariable int customerId,@RequestBody CustomerAttribute editedCustomer) throws IOException, ClassNotFoundException {

        CustomerAttribute customer = new CustomerAttribute();
        customer.setId(customerId);
        customer.setFunctionCallName("customerFindById");

        String serializedCustomerObjecttoSaveToDB = customerAttribute.serializeToString(customer);
        String serializedResponseFromSaveToDB = null;
        try {
            serializedResponseFromSaveToDB = (String) template.convertSendAndReceive(exchange.getName(), "rpc", serializedCustomerObjecttoSaveToDB);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }


        System.out.println("Response from Save To DB" + serializedResponseFromSaveToDB);

        //Optional<CustomerAttribute> customerOpt = repo.findById(customerId);
        if (!(serializedResponseFromSaveToDB ==null)){
            CustomerAttribute customerToEdit = (CustomerAttribute) customerAttribute.deserializeFromString(serializedResponseFromSaveToDB);
            customerToEdit.setName(editedCustomer.getName());
            customerToEdit.setEmail(editedCustomer.getEmail());
            customerToEdit.setAddress(editedCustomer.getAddress());
            customerToEdit.setPassportNumber(editedCustomer.getPassportNumber());
            customerToEdit.setBirthdate(editedCustomer.getBirthdate());
            customerToEdit.setFunctionCallName("saveCustomer");

            String serializedCustomerObjecttoSaveToDBFinal = customerAttribute.serializeToString(customerToEdit);

            String serializedResponseFromSaveToDBFinal = null;

            try {
                serializedResponseFromSaveToDBFinal = (String) template.convertSendAndReceive(exchange.getName(), "rpc", serializedCustomerObjecttoSaveToDBFinal);
            } catch (Exception e) {
                System.out.println("Exception: " + e);
            }
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //TODO Change to RabbitMQ RPC not jet changed
    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    @GetMapping("v1/Customers/{customerId}")
    ResponseEntity<CustomerAttribute> getCustomerDetails(@PathVariable int customerId) throws IOException, ClassNotFoundException {
        System.out.println("Requested Customer " + customerId);
        CustomerAttribute customer = null;
        customer.setId(customerId);
        customer.setFunctionCallName("customerFindById");


        String serializedCustomerObjecttoSaveToDB = customerAttribute.serializeToString(customer);
        String serializedResponseFromSaveToDB = null;
        try {
            serializedResponseFromSaveToDB = (String) template.convertSendAndReceive(exchange.getName(), "rpc", serializedCustomerObjecttoSaveToDB);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }


        System.out.println("Response from Save To DB" + serializedResponseFromSaveToDB);

        customer = (CustomerAttribute) customerAttribute.deserializeFromString(serializedResponseFromSaveToDB);

        if(!(customer ==null)) {
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
