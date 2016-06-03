package cz.ct24.shifts.parser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shift {

    /**
     * Shift name, e.g.
     * - u
     * - t
     * - p
     * etc.
     */
    private String name;

    private LocalDate date;

}
