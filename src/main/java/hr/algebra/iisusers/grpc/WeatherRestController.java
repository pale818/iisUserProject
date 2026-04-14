package hr.algebra.iisusers.grpc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST wrapper around the weather fetch logic so the gRPC service can be demoed
 * without a gRPC client. The actual gRPC service is still running on port 9090.
 * Returns all stations whose name matches the given string (partial, case-insensitive).
 */
@RestController
@RequestMapping("/api/weather")
public class WeatherRestController {

    private final WeatherFetchService weatherFetchService;

    public WeatherRestController(WeatherFetchService weatherFetchService) {
        this.weatherFetchService = weatherFetchService;
    }

    // GET /api/weather?station=Zagreb — returns all matching stations
    @GetMapping
    public List<WeatherFetchService.WeatherData> getWeather(
            @RequestParam(defaultValue = "Zagreb") String station) {
        return weatherFetchService.fetchWeather(station);
    }
}
