package at.fhcampuswien.carrental.carrentalservice.restservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Session {
    private int sessionID;
    private String accountMail;

}
