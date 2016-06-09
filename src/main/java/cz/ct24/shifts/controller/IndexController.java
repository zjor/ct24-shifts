package cz.ct24.shifts.controller;

import cz.ct24.shifts.service.RosterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;

@Slf4j
@Controller
public class IndexController {

    @Inject
    private RosterService rosterService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("index");

//        mav.addObject("team", rosterService.getRoster().getTeam().values());

        return mav;

    }


}
