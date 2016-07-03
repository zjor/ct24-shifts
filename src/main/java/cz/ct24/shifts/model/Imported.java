package cz.ct24.shifts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

/**
 * Existence of this entity means that resource was imported into database
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "imported")
public class Imported extends Born  {

    @ManyToOne
    @JoinColumn(name = "resource_id", unique = true)
    private Resource resource;

    public Imported(Resource resource, Date born) {
        setBorn(born);
        this.resource = resource;
    }
}
