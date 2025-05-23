package com.belvinard.gestionstock;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Test Endpoints", description = "Endpoints for testing and health checks")
public class HelloController {

    @GetMapping("/hello")
    @Operation(
            summary = "Simple greeting endpoint",
            description = "Returns a basic 'Hello World' message to test API connectivity"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned greeting"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public String hello() {
        return "Hello controller";
    }
}
