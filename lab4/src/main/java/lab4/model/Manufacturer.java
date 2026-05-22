package lab4.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "manufacturer")
public class Manufacturer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "country", length = 100)
    private String country;

    public Manufacturer() {}

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
