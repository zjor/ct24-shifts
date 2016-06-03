package cz.ct24.shifts;

import cz.ct24.shifts.parser.Roster;
import cz.ct24.shifts.parser.XlsParser;

import java.io.FileInputStream;

public class Main {

    public static void main(String[] args) throws Exception {

        XlsParser parser = new XlsParser();
        FileInputStream in = new FileInputStream("data/shifts.xls");

//        POIFSFileSystem fs = new POIFSFileSystem(in);
//        HSSFWorkbook wb = new HSSFWorkbook(fs);
//        parser.parseSheet(wb.getSheetAt(1), new Roster());

        Roster roster = parser.parse(in);
        roster.getDateRoster().keySet().stream().forEach(date -> {
            System.out.println(date + ": " + roster.getDateRoster().get(date));
        });



    }

}
