package meli.simian.rest.converter;

import meli.simian.domain.model.StatsInfo;
import meli.simian.rest.model.StatsResponse;
import org.springframework.stereotype.Service;

@Service
public class StatsConverter implements GenericConverter<StatsInfo, StatsResponse> {
    public StatsResponse convert(StatsInfo domain) {
        return StatsResponse.builder()
                .humanCount(domain.getHumanCount())
                .mutantCount(domain.getMutantCount())
                .ratio(domain.getRatio())
                .build();
    }
}
