package at.fhcampuswien.carrental.carrentalservice.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.*;
import java.util.Base64;


@Document(collection = "customerlist_tbl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAttribute  implements Serializable {

    @JsonInclude()
    @Transient
    private String functionCallName;

    @JsonInclude()
    @Transient
    private String passwordHash;

    @JsonInclude()
    @Transient
    private int session;

    @Id
    private Integer id;

    private String passportNumber;

    private String name;

    private String birthdate;

    private String address;

    private String paymentMethod;

    private String email;

    private String password;

    //Deserialize object to a Base64 string
    public static Object deserializeFromString( String s ) throws IOException,
            ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode( s );
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    //Serialize object to a Base64 string
    public static String serializeToString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

}
