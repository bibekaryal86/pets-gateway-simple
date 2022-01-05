package pets.gateway.app.gateway;

import lombok.extern.slf4j.Slf4j;
import pets.gateway.app.model.GatewayModel;
import pets.gateway.app.model.GatewayResponse;
import pets.gateway.app.util.RoutesUtil;
import pets.gateway.app.util.Util;

@Slf4j
public class GatewayService {

    public GatewayResponse doGatewayService(String requestUri, String trace) {
        String routeBase = RoutesUtil.getRoute(requestUri);

        if (!Util.hasText(routeBase)) {
            log.info("[ {} ] | Route Not Found for requestURI: [ {} ]", trace, requestUri);
            return GatewayResponse.builder()
                    .statusCode(422)    // unprocessable entity
                    .object(GatewayModel.builder()
                            .msg("Route Not Found! Check Request URL and Try Again!!!")
                            .build())
                    .build();
        } else {
            String outgoingUrl = routeBase.concat(requestUri);
            log.info("[ {} ] | Outgoing: [ {} ]", trace, outgoingUrl);
            return GatewayResponse.builder()
                    .statusCode(200)
                    .object(GatewayModel.builder()
                            .msg("SUCCESS")
                            .build())
                    .build();
        }
    }
}
