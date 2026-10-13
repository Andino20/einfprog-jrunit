package plus.einfprog;

import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.util.concurrent.TimeUnit;

/**
 * Global library configuration settings.
 */
@Builder
@Data
@With
public class Settings {

    private long timeout;
    private TimeUnit timeoutUnit;

    /**
     * Absolute tolerance when comparing numbers in IOAssertions.assertOutputLoosely.
     */
    private double epsilon;

    public static Settings getDefault() {
        return Settings.builder()
                .timeout(5)
                .timeoutUnit(TimeUnit.SECONDS)
                .epsilon(1e-6)
                .build();
    }
}
