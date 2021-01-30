package meli.simian.rest.controller.management;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import meli.simian.domain.service.ManagementService;
import meli.simian.rest.model.management.ClearRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ManagementController {

    ManagementService service;

    public ManagementController(ManagementService service) {
        this.service = service;
    }

    // I decided to keep the logic of choosing which method of the service to call
    // because I believe the service does not need to know about the ClearRequest, it just clears

    // I could make a method on the service like "clear(boolean cache, boolean db)" but it would end up too big if I
    // added more components to clear
    @PutMapping("/mgmt/clear")
    @ApiOperation(value = "Clears the whole database or cache, or both. Use it carefully")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "There is nothing to be shown but the server completed your request."),
            @ApiResponse(code = 500, message = "An unexpected error has occurred. If this happens, contact the application administrator") })
    ResponseEntity cleanUp(@RequestBody ClearRequest request) {

        // Ugly boolean method names, thanks Lombok
        boolean clearDatabase = request.isClearDatabase();
        boolean clearCache = request.isClearCache();

        if(clearCache) {
            log.info("Calling ManagementService to clear the cache");
            service.clearCache();
        }
        if(clearDatabase) {
            log.info("Calling ManagementService to clear the cache");
            service.clearDatabase();
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
