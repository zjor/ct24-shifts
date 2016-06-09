package cz.ct24.shifts.util.fn;

@FunctionalInterface
public interface XRunnable<T extends Throwable> {

    void run() throws T;

}
