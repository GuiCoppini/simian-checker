package meli.simian.rest.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import meli.simian.domain.model.StatsInfo;
import meli.simian.domain.service.StatsService;
import meli.simian.rest.converter.StatsConverter;
import meli.simian.rest.model.StatsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class StatsController {

    StatsService service;

    StatsConverter converter;

    @Autowired
    public StatsController(StatsService service, StatsConverter converter) {
        this.service = service;
        this.converter = converter;
    }

    @GetMapping("/stats")
    @ApiOperation(value = "Shows statistics from database, including the ration for mutant:human DNAs")
    public StatsResponse getStatistics() {
        log.info("Retrieving statistics");
        StatsInfo domainObject = service.getActualStats();

        return converter.convert(domainObject);
    }
}
