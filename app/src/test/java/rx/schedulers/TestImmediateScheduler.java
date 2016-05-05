package rx.schedulers;

public class TestImmediateScheduler {

    public static ImmediateScheduler immediate() {
        return ImmediateScheduler.instance();
    }

    public static TrampolineScheduler trampolineScheduler() {
        return TrampolineScheduler.instance();
    }

}
