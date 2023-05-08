package at.fhcampuswien.carrental.carrentalservice.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "rentallist_tbl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalAttribute {

    @Id
    private int id;

    private int carId;

    private String rented;

    private int customerId;

    private String rentalDate;

    private int rentalDuration;

    private String returnDate;

    private double totalCost;
}
