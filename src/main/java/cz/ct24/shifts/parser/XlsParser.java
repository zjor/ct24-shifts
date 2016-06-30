package cz.ct24.shifts.parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class XlsParser {

    private static final Pattern DAY_REGEX = Pattern.compile("(\\d+)\\.");

    public Roster parse(InputStream in) throws Exception {
        Roster roster = new Roster();

        POIFSFileSystem fs = new POIFSFileSystem(in);
        HSSFWorkbook wb = new HSSFWorkbook(fs);

        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            parseSheet(wb.getSheetAt(i), roster);
        }

        return roster;
    }

    public void parseSheet(HSSFSheet sheet, Roster roster) throws Exception {
        log.info("Parsing sheet: {}", sheet.getSheetName());
        Pair<Integer, Integer> monthYear = DateUtils.parseMonthYear(sheet.getSheetName());
        int month = monthYear.getLeft();

        int rows = sheet.getPhysicalNumberOfRows();
        int cols = getColumnsCount(sheet, rows);
        int rightBound = getRightBound(sheet);

        log.info("Loading shifts roster: (cols: {}; rows: {})", cols, rows);

        Pair<String, String[]> teamRow = parseTeam(sheet);
        String[] team = teamRow.getRight();
        log.info("Month: {}, Team({}): {}", teamRow.getLeft(), Arrays.toString(team));

        Arrays.stream(team).filter(t -> t != null).forEach(roster::create);

        int startRow = getBeginningOfMonthRow(sheet);
        for (int r = startRow; r < rows; r++) {
            HSSFRow row = sheet.getRow(r);
            if (row == null || !isRoster(row)) {
                continue;
            }

            HSSFRow prevRow = null;
            if (r > startRow) {
                prevRow = sheet.getRow(r - 1);
            }
            LocalDate date = inferDate(row, prevRow, monthYear.getRight(), month);
            month = date.getMonthValue();
            log.info("Parsing row: {}", rowToString(row, ", "));
            for (int c = 1; c < rightBound - 1; c++) {
                HSSFCell cell = row.getCell(c);
                if (cell == null || StringUtils.isEmpty(cell.getStringCellValue())) {
                    roster.addShift(team[c], date, null);
                } else {
                    roster.addShift(team[c], date, cell.getStringCellValue());
                }
            }
        }
    }


    private String rowToString(HSSFRow row, String delimiter) {
        StringBuilder builder = new StringBuilder();
        row.forEach(c -> builder.append(StringUtils.defaultString(c.getStringCellValue())).append(delimiter));
        builder.setLength(builder.length() - delimiter.length());
        return builder.toString();
    }

    private int getRightBound(HSSFSheet sheet) {
        HSSFRow firstRow = sheet.getRow(0);
        HSSFRow row = sheet.getRow(1);
        String a = row.getCell(0).getStringCellValue();
        int r = 1;
        while (r < 1000) {
            HSSFCell cell = row.getCell(r);
            if (cell != null && a.equals(cell.getStringCellValue())) { /* left cell equals right cell => all content goes in between */
                break;
            }

            HSSFCell cellAbove = firstRow.getCell(r);
            if (cellAbove == null || cellAbove.getStringCellValue() == null) { /* cell above is empty, e.i. current column doesn't correspond to data */
                break;
            }

            r++;
        }
        return r;
    }

    private int getBeginningOfMonthRow(HSSFSheet sheet) {
        int r = 0;
        HSSFRow row;
        while (r < 1000 && (row = sheet.getRow(r)) != null) {
            HSSFCell a = row.getCell(0);
            if (a != null && StringUtils.defaultString(a.getStringCellValue()).startsWith("1.")) {
                return r;
            }
            r++;
        }
        log.warn("Sheet '{}' doesn't contain row starting with '1.'", sheet.getSheetName());
        return 1;
    }


    private int getColumnsCount(HSSFSheet sheet, int rows) {
        int cols = 0;
        for (int i = 0; i < 10 || i < rows; i++) {
            HSSFRow row = sheet.getRow(i);
            if (row != null) {
                int tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                if (tmp > cols) cols = tmp;
            }
        }
        return cols;
    }

    /**
     * First line of the document contains month, team[].
     * Month might be null for old versions
     *
     * @param sheet
     * @return
     */
    private Pair<String, String[]> parseTeam(HSSFSheet sheet) {
        int rightBound = getRightBound(sheet);

        HSSFRow row = sheet.getRow(0);
        log.info("Parsing team row: {}", rowToString(row, ", "));
        String month = Optional.ofNullable(row.getCell(0)).map(c -> c.getStringCellValue()).orElse(null);
        String[] team = new String[rightBound + 1];

        for (int i = 1; i < rightBound; i++) {
            team[i] = row.getCell(i).getStringCellValue();
        }
        return Pair.of(month, team);
    }

    /**
     * Verifies if the row contains shifts, e.i. starts with a day
     *
     * @param row
     * @return
     */
    private boolean isRoster(HSSFRow row) {
        HSSFCell cell = row.getCell(0);
        if (cell == null) {
            return false;
        }

        String val = StringUtils.defaultString(cell.getStringCellValue());
        return val.matches("\\d+\\..*");
    }

    private int extractDay(String val) {
        Matcher m = DAY_REGEX.matcher(val);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        } else {
            throw new IllegalArgumentException(val + " is not a day");
        }
    }

    private LocalDate inferDate(HSSFRow current, HSSFRow previous, int year, int month) {
        int today = extractDay(current.getCell(0).getStringCellValue());
        LocalDate date = LocalDate.of(year, month, today);

        if (previous != null && isRoster(previous)) {
            int yesterday = extractDay(previous.getCell(0).getStringCellValue());
            if (yesterday > today) {
                return date.plus(1, ChronoUnit.MONTHS);
            }
        }

        return date;
    }


    public static void main(String[] args) {
        System.out.println("".matches("\\d+\\."));
    }

}
