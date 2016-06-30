package cz.ct24.shifts.parser;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

public class XlsParserTest {

    private XlsParser parser = new XlsParser();

    @Test
    public void shouldParseShiftsXls() throws Exception {
        Roster roster = parser.parse(getResource("shifts.xls"));
        Assert.assertFalse("Roster shouldn't be empty", roster.getRoster().isEmpty());
    }

    @Test
    public void shouldParseShifts2016Xls() throws Exception {
        Roster roster = parser.parse(getResource("shifts_2016.xls"));
        Assert.assertFalse("Roster shouldn't be empty", roster.getRoster().isEmpty());
    }


    private InputStream getResource(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }


}
