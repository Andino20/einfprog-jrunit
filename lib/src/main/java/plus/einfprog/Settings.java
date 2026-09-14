package plus.einfprog;

import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.util.concurrent.TimeUnit;

@Builder
@Data
@With
public class Settings {

    private long timeout;
    private TimeUnit timeoutUnit;

    public static Settings getDefault() {
        return Settings.builder()
                .timeout(5)
                .timeoutUnit(TimeUnit.SECONDS)
                .build();
    }
}
