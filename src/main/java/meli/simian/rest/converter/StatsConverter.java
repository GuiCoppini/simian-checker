package meli.simian.rest.converter;

import meli.simian.domain.model.StatsInfo;
import meli.simian.rest.model.StatsResponse;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Service
public class StatsConverter implements GenericConverter<StatsInfo, StatsResponse> {
    public StatsResponse convert(StatsInfo domain) {
        return StatsResponse.builder()
                .humanCount(domain.getHumanCount())
                .mutantCount(domain.getMutantCount())
                // US ensures the final number will be "X.YZ" instead of "X,YZ" (using DOT instead of COMMA)
                .ratio(new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.US)).format(domain.getRatio()))
                .build();
    }
}
