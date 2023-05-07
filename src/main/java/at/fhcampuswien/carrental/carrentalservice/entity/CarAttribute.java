package at.fhcampuswien.carrental.carrentalservice.entity;

//import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//@Entity
//@Table(name = "carlist_tbl")
@Document(collection = "carlist_tbl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarAttribute {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column(name = "car_id", nullable = false)
    private int id;

    //@Column(name = "model_name")
    private String modelName;

    //@Column(name = "cylinder")
    private int cylinder;

    //@Column(name = "price_usd")
    private double priceusd;

    //@Column(name = "horsepower")
    private int horsepower;

    //@Column(name = "weight_in_lbs")
    private int weightInlbs;

    //@Column(name = "acceleration")
    private int acceleration;

    //@Column(name = "year")
    private String year;

    //@Column(name = "origin")
    private String origin;

    //@Column(name = "location")
    private String location;

    //@Column(name = "fuel")
    private String fuel;

    //@Column(name = "mileage_km")
    private int mileageKm;

    //@Column(name = "automatic")
    private boolean automatic;

    //@Column(name = "rental_id")
    private String rentalId;

}
