package cz.ct24.shifts.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Data
@MappedSuperclass
public class Born extends Model {

    @Column(name = "born", nullable = false)
    private Date born;

}
