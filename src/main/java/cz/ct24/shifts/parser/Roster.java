package cz.ct24.shifts.parser;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Getter
public class Roster {

    /**
     * Maps employee's name to list of shifts
     */
    private Map<String, Map<LocalDate, String>> roster = new HashMap<>();

    /**
     * Date -> Employee -> Shift
     */
    private Map<LocalDate, Map<String, String>> dateRoster = new TreeMap<>();

    public void create(String employeeName) {
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
