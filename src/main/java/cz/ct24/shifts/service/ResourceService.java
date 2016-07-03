package cz.ct24.shifts.service;

import com.github.jtail.jpa.util.EntityUtils;
import cz.ct24.shifts.model.Checksum;
import cz.ct24.shifts.model.Checksum_;
import cz.ct24.shifts.model.Imported;
import cz.ct24.shifts.model.Imported_;
import cz.ct24.shifts.model.Resource;
import cz.ct24.shifts.model.Resource_;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@Service
public class ResourceService {

    private static final String TMP_PATH = "/tmp";

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Resource create(String name, String location, String contentType, long size) {
        return EntityUtils.persist(em, new Resource(name, location, contentType, size, new Date()));
    }

    public String getLocation() {
        return TMP_PATH + "/" + UUID.randomUUID().toString();
    }

    public void save(InputStream in, String location) throws IOException {
        IOUtils.copy(in, new FileOutputStream(location));
    }

    public InputStream open(Resource resource) throws FileNotFoundException {
        return new FileInputStream(resource.getLocation());
    }

    @Transactional
    public Checksum writeChecksum(Resource resource) throws IOException {
        try (InputStream in = open(resource)) {
            String checksum = DigestUtils.md5Hex(in);
            return EntityUtils.persist(em, new Checksum(resource, checksum, new Date()));
        }
    }

    @Transactional
    public Imported persistImported(Resource resource) {
        return EntityUtils.persist(em, new Imported(resource, new Date()));
    }

    public boolean isDuplicated(Resource resource) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Resource> q = cb.createQuery(Resource.class);
        Root<Resource> r = q.from(Resource.class);

        Subquery<Imported> iSq = q.subquery(Imported.class);
        Root<Imported> iR = iSq.from(Imported.class);
        iSq.select(iR).where(cb.equal(iR.get(Imported_.resource), r));

        Subquery<Checksum> cksSq = q.subquery(Checksum.class);
        Root<Checksum> cksR1 = cksSq.from(Checksum.class);
        Root<Checksum> cksR2 = cksSq.from(Checksum.class);
        cksSq.select(cksR1).where(
                cb.equal(cksR1.get(Checksum_.md5), cksR2.get(Checksum_.md5)),
                cb.equal(cksR1.get(Checksum_.resource), resource),
                cb.equal(cksR2.get(Checksum_.resource), r));

        q.select(r).where(
                cb.notEqual(r.get(Resource_.id), resource.getId()),
                cb.exists(iSq),
                cb.exists(cksSq));

        return !em.createQuery(q).setMaxResults(1).getResultList().isEmpty();
    }

}
