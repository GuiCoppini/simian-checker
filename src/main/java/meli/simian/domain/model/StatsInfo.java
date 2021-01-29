package meli.simian.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatsInfo {
    private static final int DECIMAL_UNITS = 2;

    private Long mutantCount;

    private Long humanCount;

    private Double ratio;


    // using a build method so no other class needs to "think" about how to calculate the ratio
    public static StatsInfo build(long mutantCount, long humanCount) {
        double ratio = (double) mutantCount / humanCount;
        return new StatsInfo(mutantCount, humanCount, ratio);
    }
}
