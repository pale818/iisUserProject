package hr.algebra.iisusers.grpc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// REST bridge that exposes the gRPC weather data over HTTP so the browser can access it
// (browsers cannot call gRPC directly)
@RestController
@RequestMapping("/api/weather")
public class WeatherRestController {

    private final WeatherFetchService weatherFetchService;

    public WeatherRestController(WeatherFetchService weatherFetchService) {
        this.weatherFetchService = weatherFetchService;
    }

    @GetMapping
    public List<WeatherFetchService.WeatherData> getWeather(
            @RequestParam(defaultValue = "Zagreb") String station) {
        return weatherFetchService.fetchWeather(station);
    }
}
