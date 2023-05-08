package at.fhcampuswien.carrental.carrentalservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "carlist_tbl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarAttribute {

    @Id
    private int id;

    private String modelName;

    private int cylinder;

    private double priceusd;

    private int horsepower;

    private int weightInlbs;

    private int acceleration;

    private String year;

    private String origin;

    private String location;

    private String fuel;

    private int mileageKm;

    private boolean automatic;

    private String rentalId;

}
