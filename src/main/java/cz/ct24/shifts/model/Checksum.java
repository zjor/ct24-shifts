package cz.ct24.shifts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

/**
 * Persists md5 of file with roster to prevent import duplication
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "checksum")
public class Checksum extends Born {

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

    @Column(name = "md5")
    private String md5;

    public Checksum(Resource resource, String md5, Date born) {
        setBorn(born);
        this.resource = resource;
        this.md5 = md5;
    }
}
