package cz.ct24.shifts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Persists uploaded file location
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resource")
public class Resource extends Born {

    private String name;

    private String location;

    private String contentType;

    private long size;

    public Resource(String name, String location, String contentType, long size, Date born) {
        setBorn(born);
        this.name = name;
        this.location = location;
        this.contentType = contentType;
        this.size = size;
    }

}
