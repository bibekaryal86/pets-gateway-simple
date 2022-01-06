package pets.gateway.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pets.gateway.app.model.GatewayModel;
import pets.gateway.app.model.GatewayResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectorUtil {

    private static HttpClient getHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5L))
                .build();
    }

    private static URI getUri(String endpoint) {
        return URI.create(endpoint);
    }

    private static HttpRequest.BodyPublisher getPOST(Object object) {
        return HttpRequest.BodyPublishers.ofString(Util.getGson().toJson(object));
    }

    private static HttpRequest getHttpRequestBuilder(String endpoint,
                                                     String httpMethod,
                                                     Object bodyObject,
                                                     Map<String, String> headers) {
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                .uri(getUri(endpoint))
                .header("Content-Type", "application/json");

        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpRequestBuilder = httpRequestBuilder.header(entry.getKey(), entry.getValue());
            }
        }

        switch (httpMethod) {
            case "POST":
                httpRequestBuilder = httpRequestBuilder.POST(getPOST(bodyObject));
                break;
            case "PUT":
                httpRequestBuilder = httpRequestBuilder.PUT(getPOST(bodyObject));
                break;
            case "DELETE":
                httpRequestBuilder = httpRequestBuilder.DELETE();
                break;
            case "GET":
                httpRequestBuilder = httpRequestBuilder.GET();
                break;
            default:
                break;
        }

        return httpRequestBuilder.build();
    }

    private static HttpResponse<String> sendHttpRequest(HttpRequest httpRequest) throws IOException, InterruptedException {
        return getHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    public static GatewayResponse sendHttpRequest(String endpoint,
                                                  String httpMethod,
                                                  Object bodyObject,
                                                  Map<String, String> headers,
                                                  String trace) {
        try {
            log.info("[ {} ] HTTP Request Sent::: Endpoint: [ {} ], Method: [ {} ], Headers: [ {} ], Body: [ {} ]",
                    trace,
                    endpoint,
                    httpMethod,
                    headers == null ? 0 : headers.size(),
                    bodyObject == null ? null : bodyObject.getClass().getName());

            HttpRequest httpRequest = getHttpRequestBuilder(endpoint, httpMethod, bodyObject, headers);
            HttpResponse<String> httpResponse = sendHttpRequest(httpRequest);

            log.info("[ {} ] HTTP Response Received::: Endpoint: [ {} ], Status: [ {} ], Body: [ {} ]",
                    trace,
                    endpoint,
                    httpResponse.statusCode(),
                    httpResponse.body() == null ? null : httpResponse.body().length());

            Object object = Util.getGson().fromJson(httpResponse.body(), Object.class);

            return GatewayResponse.builder()
                    .statusCode(httpResponse.statusCode())
                    .object(object)
                    .build();
        } catch (InterruptedException ex) {
            log.error("[ {} ] Error in HttpClient Send: [ {} ] | [ {} ] | [ {} ]",
                    trace, endpoint, httpMethod, ex.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            log.error("[ {} ] Error in HttpClient Send: [ {} ] | [ {} ] | [ {} ]",
                    trace, endpoint, httpMethod, ex.getMessage());
        }

        return GatewayResponse.builder()
                .statusCode(503)
                .object(GatewayModel.builder()
                        .errMsg("Error! Something Went Wrong!! Please Try Again!!!")
                        .build())
                .build();
    }
}
