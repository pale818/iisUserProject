package hr.algebra.iisusers.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * gRPC server endpoint for weather data.
 * Runs on port 9090 (configured via grpc.server.port in application.properties).
 *
 * The WeatherServiceGrpc base class and WeatherProto message classes are generated
 * from src/main/proto/weather.proto by the protobuf-maven-plugin during the build.
 */
@GrpcService
public class WeatherGrpcService extends WeatherServiceGrpc.WeatherServiceImplBase {

    private final WeatherFetchService weatherFetchService;

    public WeatherGrpcService(WeatherFetchService weatherFetchService) {
        this.weatherFetchService = weatherFetchService;
    }

    @Override
    public void getWeather(WeatherProto.WeatherRequest request,
                           StreamObserver<WeatherProto.WeatherListResponse> responseObserver) {

        var results = weatherFetchService.fetchWeather(request.getStation());

        WeatherProto.WeatherListResponse.Builder listBuilder = WeatherProto.WeatherListResponse.newBuilder();

        for (WeatherFetchService.WeatherData data : results) {
            WeatherProto.WeatherResponse response = WeatherProto.WeatherResponse.newBuilder()
                    .setStation(data.station())
                    .setTemperature(data.temperature())
                    .setDescription(data.description())
                    .setTimestamp(data.timestamp())
                    .build();
            listBuilder.addResults(response);
        }

        responseObserver.onNext(listBuilder.build());
        responseObserver.onCompleted();
    }
}
