package at.fhcampuswien.carrental.carrentalservice.entity;


//import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//@Entity
//@Table(name = "rentallist_tbl")
@Document(collection = "rentallist_tbl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalAttribute {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column(name = "rental_id", nullable = false)
    private int id;

    //@Column(name = "car_id")
    private int carId;

    //@Column(name = "rented")
    private String rented;

    //@Column(name = "customer_id")
    private int customerId;

    //@Column(name = "rental_date")
    private String rentalDate;

    //@Column(name = "rental_duration")
    private int rentalDuration;

    //@Column(name = "return_date")
    private String returnDate;

    //@Column(name = "total_cost")
    private double totalCost;
}
