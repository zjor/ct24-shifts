package cz.ct24.shifts.parser;

import cz.ct24.shifts.model.Employee;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Slf4j
@Getter
public class Roster {

    /**
     * empId -> emp
     */
    //TODO: replace by db
    @Getter
    private Map<String, Employee> team = new HashMap<>();

    /**
     * Maps employee's name to list of shifts
     */
    private Map<String, Map<LocalDate, String>> roster = new HashMap<>();

    /**
     * Date -> Employee -> Shift
     */
    private Map<LocalDate, Map<String, String>> dateRoster = new TreeMap<>();

    public void create(String employeeName) {
        if (!roster.containsKey(employeeName)) {
            String id = UUID.randomUUID().toString();
            team.put(id, new Employee(id, employeeName));
        }
        roster.putIfAbsent(employeeName, new HashMap<>());

    }

    public void addShift(String employeeName, LocalDate date, String shift) {
        try {
            roster.get(employeeName).put(date, shift);

            dateRoster.putIfAbsent(date, new TreeMap<>());
            dateRoster.get(date).put(employeeName, shift);
        } catch (Throwable t) {
            log.error("Failed to add: {}, {}, {}", employeeName, date, shift);
            throw t;
        }
    }

}
