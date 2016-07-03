package cz.ct24.shifts.controller;

import cz.ct24.shifts.controller.dto.JShift;
import cz.ct24.shifts.model.Employee;
import cz.ct24.shifts.parser.Roster;
import cz.ct24.shifts.service.RosterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("roster")
public class RosterController {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd");

    @Inject
    private RosterService rosterService;

    @RequestMapping(value = "team", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Collection<Employee> getTeam(
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end
    ) throws ParseException {
        if (start == null || end == null) {
            return rosterService.getTeam();
        } else {
            Date startDate = DATE_FORMAT.parse(start);
            Date endDate = DATE_FORMAT.parse(end);
            return rosterService.getTeam(startDate, endDate);
        }
    }

//    @RequestMapping(value = "/date/{year:\\d\\d\\d\\d}-{month:\\d\\d}/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public Collection<JShift> getMonthlyRoster(
//            @PathVariable("year") int year,
//            @PathVariable("month") int month,
//            @PathVariable("id") String employeeId) {
//        log.info("{}-{}: {}", year, month, employeeId);
//
//        Roster roster = rosterService.getRoster();
//
//        LocalDate date = LocalDate.of(year, month, 1);
//        LocalDate start = date.minus(14, ChronoUnit.DAYS);
//        LocalDate end = date.plus(40, ChronoUnit.DAYS);
//        String name = roster.getTeam().get(employeeId).getName();
//        List<JShift> shifts = new LinkedList<>();
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//        while (!start.equals(end)) {
//            String shift = Optional.ofNullable(roster.getDateRoster().get(start))
//                    .flatMap(m -> Optional.ofNullable(m.get(name)))
//                    .orElse(null);
//            if (shift != null) {
//                shifts.add(new JShift(shift, start.format(formatter), null));
//            }
//            start = start.plus(1, ChronoUnit.DAYS);
//
//        }
//
//        return shifts;
//    }

    @RequestMapping(value = "cal/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Collection<JShift> getCal(
            @PathVariable("id") String userId,
            @RequestParam("start") String start,
            @RequestParam("end")String end) {


        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        Roster roster = null;//rosterService.getRoster();

        String name = null;//roster.getTeam().get(userId).getName();
        List<JShift> shifts = new LinkedList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (!startDate.equals(endDate)) {
            String shift = Optional.ofNullable(roster.getDateRoster().get(startDate))
                    .flatMap(m -> Optional.ofNullable(m.get(name)))
                    .orElse(null);
            if (shift != null) {
                shifts.add(new JShift(shift, startDate.format(formatter), null));
            }
            startDate = startDate.plus(1, ChronoUnit.DAYS);

        }

        return shifts;
    }

    @ExceptionHandler({ParseException.class})
    @ResponseBody
    public ResponseEntity<?> errorHandler(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
