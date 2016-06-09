package cz.ct24.shifts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Persists md5 of file with roster to prevent import duplication
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roster_checksum")
public class RosterChecksum extends Born {

    @Column(name = "md5")
    private String md5;

    public RosterChecksum(String md5, Date born) {
        setBorn(born);
        this.md5 = md5;
    }
}
