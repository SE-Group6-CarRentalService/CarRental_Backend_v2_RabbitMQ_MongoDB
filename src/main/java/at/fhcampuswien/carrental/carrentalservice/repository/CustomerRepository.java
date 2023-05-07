package at.fhcampuswien.carrental.carrentalservice.repository;


import at.fhcampuswien.carrental.carrentalservice.entity.CustomerAttribute;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<CustomerAttribute, Integer> {

    CustomerAttribute findByEmail(String email);

}
