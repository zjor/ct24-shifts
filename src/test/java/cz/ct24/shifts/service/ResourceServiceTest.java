package cz.ct24.shifts.service;

import com.github.jtail.jpa.util.EntityUtils;
import cz.ct24.shifts.model.Checksum;
import cz.ct24.shifts.model.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring-context**test.xml",
        "classpath*:spring-context-persistence.xml"
})
public class ResourceServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private ResourceService resourceService;

    @Test
    @Transactional
    public void shouldNotBeDuplicatedWithoutChecksum() {
        Resource res0 = resourceService.create("res0", "loc1", "text", 1);
        resourceService.persistImported(res0);

        Resource res1 = resourceService.create("res1", "loc1", "text", 1);

        Assert.assertFalse(resourceService.isDuplicated(res1));
    }

    @Test
    @Transactional
    public void shouldNotBeDuplicatedWithoutImport() {
        Resource res0 = resourceService.create("res0", "loc1", "text", 1);
        Resource res1 = resourceService.create("res1", "loc1", "text", 1);
        EntityUtils.persist(em, new Checksum(res0, "123", new Date()));
        EntityUtils.persist(em, new Checksum(res1, "123", new Date()));

        Assert.assertFalse(resourceService.isDuplicated(res1));
    }

    @Test
    @Transactional
    public void shouldBeDuplicated() {
        Resource res0 = resourceService.create("res0", "loc1", "text", 1);
        resourceService.persistImported(res0);
        Resource res1 = resourceService.create("res1", "loc1", "text", 1);
        EntityUtils.persist(em, new Checksum(res0, "123", new Date()));
        EntityUtils.persist(em, new Checksum(res1, "123", new Date()));

        Assert.assertTrue(resourceService.isDuplicated(res1));
    }


}
