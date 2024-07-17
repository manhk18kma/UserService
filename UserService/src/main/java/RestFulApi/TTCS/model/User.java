package RestFulApi.TTCS.model;

import RestFulApi.TTCS.util.Gender;
import RestFulApi.TTCS.util.UserStatus;
import RestFulApi.TTCS.util.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
//@Table(name = "tbl_user")
public class User extends AbstractEntity {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

//    @Enumerated(EnumType.STRING)
//    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
//    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", columnDefinition = "VARCHAR(255)") // Specify the SQL type here
    private Gender gender;


    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

//    @Enumerated(EnumType.STRING)
//    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
//    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "VARCHAR(255)") // Specify the SQL type here
    private UserType type;

//    @Enumerated(EnumType.STRING)
//    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
//    @Column(name = "status")
@Enumerated(EnumType.STRING)
@Column(name = "status", columnDefinition = "VARCHAR(255)") // Specify the SQL type here
    private UserStatus status;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    private Set<Address> addresses = new HashSet<>();

    public void saveAddress(Address address) {
        if (address != null) {
            if (addresses == null) {
                addresses = new HashSet<>();
            }
            addresses.add(address);
            address.setUser(this); // save user_id
        }
    }

    // https://stackoverflow.com/questions/56899986/why-infinite-loop-hibernate-when-load-data
    @JsonIgnore // Stop infinite loop
    public Set<Address> getAddresses() {
        return addresses;
    }
}
