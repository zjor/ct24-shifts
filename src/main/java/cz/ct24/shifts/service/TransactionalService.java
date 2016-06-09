package cz.ct24.shifts.service;

import cz.ct24.shifts.util.fn.XRunnable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionalService {

    @Transactional
    public <T extends Throwable> void tx(XRunnable<T> runnable) throws T {
        runnable.run();
    }

}
