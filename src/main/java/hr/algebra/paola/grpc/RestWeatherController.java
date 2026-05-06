package hr.algebra.paola.grpc;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/grpc")
public class RestWeatherController {

    private final FetchService fetchService;
    public RestWeatherController(FetchService fetchService) {
        this.fetchService = fetchService;

    }

    @GetMapping
    public List<FetchService.WeatherData> getWeather(@RequestParam(defaultValue = "Zagreb") String station) throws Exception {
        return fetchService.fetchWeather(station);
    }


}


