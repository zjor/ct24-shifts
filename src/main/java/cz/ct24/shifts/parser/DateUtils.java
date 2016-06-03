package cz.ct24.shifts.parser;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DateUtils {

    private static Map<String, Integer> MONTHS = new HashMap<>();

    static {
        MONTHS.put("Prosinec", 12);
        MONTHS.put("Leden", 1);
        MONTHS.put("Únor", 2);
        MONTHS.put("Březen", 3);
        MONTHS.put("Duben", 4);
        MONTHS.put("Květen", 5);
        MONTHS.put("Červen", 6);
        MONTHS.put("Červenec", 7);
        MONTHS.put("Srpen", 8);
        MONTHS.put("Září", 9);
        MONTHS.put("Říjen", 10);
        MONTHS.put("Listopad", 11);
    }

    public static Pair<Integer, Integer> parseMonthYear(String sheetName) {
        String[] tokens = sheetName.split(" ");
        if (tokens.length != 2) {
            throw new IllegalArgumentException("Name should contain month and year");
        }
        Integer month = MONTHS.get(tokens[0]);
        if (month == null) {
            throw new IllegalArgumentException("Unsupported month format: " + tokens[0]);
        }

        int year = Integer.parseInt(tokens[1]);
        if (year < 2000) {
            year += 2000;
        }
        return Pair.of(month, year);
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString("sdfsdf".split(" ")));
    }


}
