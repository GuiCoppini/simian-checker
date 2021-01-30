package meli.simian.rest.converter;

import meli.simian.domain.model.StatsInfo;
import meli.simian.rest.model.StatsResponse;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertNull;

class StatsConverterTest {

    StatsConverter converter = new StatsConverter();

    @Test
    void returnsNullIfRatioIsNull() {
        StatsInfo domain = new StatsInfo(999L, 999L, null);
        StatsResponse payload = converter.convert(domain);

        assertNull(payload.getRatio());
    }

}