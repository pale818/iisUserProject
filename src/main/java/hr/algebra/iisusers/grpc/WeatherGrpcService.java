package hr.algebra.iisusers.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

// gRPC service — extends the auto-generated base class from weather.proto
// Runs on a separate port (default 9090) from the REST API (8080)
@GrpcService
public class WeatherGrpcService extends WeatherServiceGrpc.WeatherServiceImplBase {

    private final WeatherFetchService weatherFetchService;

    public WeatherGrpcService(WeatherFetchService weatherFetchService) {
        this.weatherFetchService = weatherFetchService;
    }

    @Override
    public void getWeather(WeatherProto.WeatherRequest request,
                           StreamObserver<WeatherProto.WeatherListResponse> responseObserver) {

        //gets matches cities
        var results = weatherFetchService.fetchWeather(request.getStation());

        // Protobuf uses a builder pattern — each field must be set explicitly
        WeatherProto.WeatherListResponse.Builder listBuilder = WeatherProto.WeatherListResponse.newBuilder();

        //builds protubuf binary result, rules by .proto file
        for (WeatherFetchService.WeatherData data : results) {
            WeatherProto.WeatherResponse response = WeatherProto.WeatherResponse.newBuilder()
                    .setStation(data.station())
                    .setTemperature(data.temperature())
                    .setDescription(data.description())
                    .setTimestamp(data.timestamp())
                    .build();
            listBuilder.addResults(response);
        }

        // onNext sends the response, onCompleted signals the stream is finished
        responseObserver.onNext(listBuilder.build());
        responseObserver.onCompleted();
    }
}
