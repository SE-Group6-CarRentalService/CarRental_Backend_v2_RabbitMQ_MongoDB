package at.fhcampuswien.carrental.carrentalservice.repository;


import at.fhcampuswien.carrental.carrentalservice.entity.CustomerAttribute;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<CustomerAttribute, Integer> {

    CustomerAttribute findByEmail(String email);

}
