package cz.ct24.shifts.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JShift {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd");

    private String title;

    private String start;

    private String color;

}
