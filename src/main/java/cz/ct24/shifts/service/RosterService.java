package cz.ct24.shifts.service;

import com.github.jtail.jpa.util.EntityUtils;
import cz.ct24.shifts.model.Born_;
import cz.ct24.shifts.model.Employee;
import cz.ct24.shifts.model.Employee_;
import cz.ct24.shifts.model.Shift;
import cz.ct24.shifts.model.Shift_;
import cz.ct24.shifts.parser.Roster;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RosterService {

    @PersistenceContext
    private EntityManager em;

    private Employee ensureEmployee(String name) {
        return EntityUtils.find(em, Employee.class)
                .has(Employee_.name, name)
                .first()
                .orElseGet(() -> EntityUtils.persist(em, new Employee(name)));
    }

    @Transactional
    public void persist(Roster r, Date now) {
        r.getRoster().keySet().stream().forEach(name -> {
            Employee employee = ensureEmployee(name);
            Map<LocalDate, String> shifts = r.getRoster().get(name);
            shifts.entrySet().stream()
                    .filter(entry -> entry.getValue() != null)
                    .forEach(entry -> {
                        Date date = Date.from(entry.getKey().atStartOfDay(ZoneId.systemDefault()).toInstant());
                        em.merge(new Shift(employee, entry.getValue(), date, now));
                    });
        });
    }

    /**
     * Returns all employees ever created
     * @return
     */
    public List<Employee> getTeam() {
        return EntityUtils.find(em, Employee.class).desc(Employee_.name).list();
    }

    /**
     * Returns employees having shifts within given dates
     * @param start
     * @param end
     * @return
     */
    public List<Employee> getTeam(Date start, Date end) {
        return EntityUtils.find(em, Employee.class).bySubquery(Shift.class, (cb, employeeRoot, subquery) -> {
            Root<Shift> shiftRoot = subquery.from(Shift.class);
            return cb.exists(
                    subquery.select(shiftRoot)
                            .where(
                                    cb.equal(shiftRoot.get(Shift_.employee), employeeRoot),
                                    cb.greaterThanOrEqualTo(shiftRoot.get(Born_.born), start),
                                    cb.lessThanOrEqualTo(shiftRoot.get(Born_.born), end)));

        }).desc(Employee_.name).list();
    }

}
