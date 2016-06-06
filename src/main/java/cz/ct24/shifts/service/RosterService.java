package cz.ct24.shifts.service;

import cz.ct24.shifts.parser.Roster;
import cz.ct24.shifts.parser.XlsParser;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
public class RosterService {

    private Roster roster;

    public void init() {
        XlsParser parser = new XlsParser();
        InputStream in = getClass().getClassLoader().getResourceAsStream("data/shifts.xls");
        try {
            roster = parser.parse(in);
        } catch (Exception e) {
            log.error("Failed to load roster: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Roster getRoster() {
        return roster;
    }

}
