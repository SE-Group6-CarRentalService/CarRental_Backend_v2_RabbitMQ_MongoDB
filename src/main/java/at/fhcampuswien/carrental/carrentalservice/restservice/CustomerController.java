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
import java.util.Objects;
import java.util.Optional;


@RestController
@CrossOrigin(origins = "*")
public class CustomerController {

    private CustomerAttribute customerAttribute;

    //@Autowired
    //private CustomerRepository repo;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private DirectExchange exchange;


    //TESTED working
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

    //TESTED working
    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    @PostMapping("v1/Customers/login")
    ResponseEntity<JwtResponse> getCustomer(@RequestParam String email, @RequestParam String password) throws IOException, ClassNotFoundException {

        System.out.println(" [x] Requesting getCustomer() -> LOGIN A CUSTOMER");

        CustomerAttribute customer = new CustomerAttribute();
        //TODO change getCustomer to LoginCustomer in microservice and in comment below
        customer.setFunctionCallName("getCustomer");
        customer.setEmail(email);
        customer.setPassword(password);

        String serializedCustomerObject = customerAttribute.serializeToString(customer);

        String responseAMQP;
        CustomerAttribute customerFoundInDB;
        String passwordFromDBforRequestedUser;
        try {
            responseAMQP = (String) template.convertSendAndReceive(exchange.getName(), "rpc", serializedCustomerObject);
            customerFoundInDB = (CustomerAttribute) customerAttribute.deserializeFromString(responseAMQP);

            //TODO check if exception is triggered if no user found

        } catch (Exception e) {
            System.out.println("No user with this email found " + email);
            System.out.println("Exception: " + e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user with this email found");
        }


        System.out.println("Email equals Email? " +responseAMQP);
        System.out.println("CustomerFoundInDB? " +customerFoundInDB.getEmail());
        if (Objects.equals(customerFoundInDB.getEmail(), email)) {
            System.out.println("PW from DB? " +customerFoundInDB.getPassword());
            System.out.println("PW from PW? " +password);
            System.out.println("Email from DB? " +customerFoundInDB.getEmail());

            if (passwordEncoder.matches(password, customerFoundInDB.getPassword())) {
                System.out.println("1");
                final String token = jwtTokenUtil.generateToken(email);
                System.out.println("2");
                final JwtResponse response = new JwtResponse(token, customerFoundInDB.getId());
                System.out.println("3");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }


    }

    //TESTED working -> see TODO in function below
    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    @PostMapping("v1/Customers/register")
    ResponseEntity<Void> registerCustomer(@RequestBody CustomerAttribute newCustomer) throws IOException {

        System.out.println(" [x] Requesting registerCustomer() -> REGISTER A CUSTOMER");

        CustomerAttribute newCustomerId = new CustomerAttribute();
        newCustomer.setFunctionCallName("getCustomer");
        //newCustomer.setId(newCustomerId.getId());
        //TODO check why when registering with passportNumber filled: Error: response status is 400. Takes the passportnumber as JWT token why -> thats why passportNumber set to null
        newCustomer.setPassportNumber(null);

        //newCustomer.setPassword(passwordEncoder.encode(newCustomer.getPassword()));

        System.out.println("Encoded Password 1" + newCustomer.getPassword());

        String serializedCustomerObject = customerAttribute.serializeToString(newCustomer);

        System.out.println("Encoded Password 2" + newCustomer.getPassword());
        List<CustomerAttribute> customerFromDB;
        String deSerializedStringResponseFromGetCustomer = "";

        String serializedCustomerInfo;
        try {
            serializedCustomerInfo = (String) template.convertSendAndReceive(exchange.getName(), "rpc", serializedCustomerObject);
            //deSerializedStringResponseFromGetCustomer = (String) customerAttribute.deserializeFromString(serializedCustomerInfo);


        } catch (Exception e) {
            System.out.println("No user found.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user with this email found");
        }

        if (Objects.equals(serializedCustomerInfo, "noUserFound")) {

            newCustomer.setFunctionCallName("registerCustomer");

            CustomerAttribute newCustomerID = new CustomerAttribute();
            newCustomer.setId(newCustomerID.getId());
            newCustomer.setPassword(passwordEncoder.encode(newCustomer.getPassword()));

            String serializedCustomerObjecttoSaveToDB = customerAttribute.serializeToString(newCustomer);

            String responseFromSaveToDB = (String) template.convertSendAndReceive(exchange.getName(), "rpc", serializedCustomerObjecttoSaveToDB);

            System.out.println("Response from Save To DB" + responseFromSaveToDB);

            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, " email already registered");
        }
    }

    //TESTED working
    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    @PutMapping("v1/Customers/{customerId}")
    ResponseEntity<Void> editCustomer(@PathVariable int customerId,@RequestBody CustomerAttribute editedCustomer) throws IOException, ClassNotFoundException {

        System.out.println(" [x] Requesting editCustomer() -> EDIT A CUSTOMER");

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

    //TESTED working
    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    @GetMapping("v1/Customers/{customerId}")
    ResponseEntity<CustomerAttribute> getCustomerDetails(@PathVariable int customerId) throws IOException, ClassNotFoundException {

        System.out.println(" [x] Requesting getCustomerDetails() -> GET CUSTOMER DETAILS");

        System.out.println("Requested Customer " + customerId);
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
