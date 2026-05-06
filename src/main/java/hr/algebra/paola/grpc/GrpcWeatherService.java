package hr.algebra.paola.grpc;


import hr.algebra.iisusers.grpc.WeatherProto;
import hr.algebra.iisusers.grpc.WeatherServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class GrpcWeatherService extends WeatherServiceGrpc.WeatherServiceImplBase {


    private final FetchService fetchService;
    public GrpcWeatherService(FetchService fetchService) {
        this.fetchService = fetchService;
    }

    @Override
    public void getWeather(WeatherProto.WeatherRequest request,
                           StreamObserver<WeatherProto.WeatherListResponse> result) {


        var results = fetchService.fetchWeather(request.getStation());

        WeatherProto.WeatherListResponse.Builder listBuilder = WeatherProto.WeatherListResponse.newBuilder();
        for(FetchService.WeatherData data : results){
            WeatherProto.WeatherResponse response = WeatherProto.WeatherResponse.newBuilder()
                    .setTemperature(data.temp())
                    .setDescription(data.description())
                    .setStation(data.station())
                    .build();
            listBuilder.addResults(response);
        }

        result.onNext(listBuilder.build());
        result.onCompleted();

    }


}
