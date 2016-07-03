package cz.ct24.shifts.service;

import cz.ct24.shifts.model.Resource;
import cz.ct24.shifts.parser.Roster;
import cz.ct24.shifts.parser.XlsParser;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.Date;

@Service
public class RosterImportService {

    @Inject
    private RosterService rosterService;

    @Inject
    private ResourceService resourceService;

    public void importRoster(String name, String contentType, long size, InputStream in) throws Exception {
        String location = resourceService.getLocation();
        Resource resource = resourceService.create(name, location, contentType, size);
        resourceService.save(in, location);
        resourceService.writeChecksum(resource);

        //TODO: check if not imported
        XlsParser parser = new XlsParser();
        Roster roster = parser.parse(resourceService.open(resource));
        rosterService.persist(roster, new Date());
        resourceService.persistImported(resource);
    }

}
