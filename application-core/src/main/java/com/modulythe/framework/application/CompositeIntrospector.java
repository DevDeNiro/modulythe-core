package com.modulythe.framework.application;

/*
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import static ///application.Utils.isNotValidJwtToken;
import static ///application.Utils.isValidJwtToken;
import static java.util.Objects.nonNull;

public class CompositeIntrospector implements OpaqueTokenIntrospector {

    public static final Logger LOGGER = LoggerFactory.getLogger(CompositeIntrospector.class);
    private static final String USER_ID = "user_id";

    @Override
    @TrackExecutionTime
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        OAuth2AuthenticatedPrincipal result = null;
        if (isJwtToken(token)) {
            try {
                AuthenticatedUser authenticatedUser = userInfoService.getAuthenticatedUserFromToken(token);
                if (authenticatedUser != null) {
                    MDC.put(USER_ID, authenticatedUser.getUuid());
                    String recordedToken = cacheService.getTokenForUser(authenticatedUser.getUuid());
                    if (nonNull(recordedToken)) {
                        cacheService.evictFromCache(recordedToken);
                    }
                    cacheService.updateTokenForUser(authenticatedUser.getUuid(), token);
                    result =
                }
            } catch (EmptyUserInfoException e) {
                MDC.remove(USER_ID);
                LOGGER.error("EmptyUserInfoException in introspect", e);
            }
        } else if (isOpaqueToken(token)) {
            ...
        }
    }


    private static boolean isOpaqueToken(String token) {
        assert nonNull(token) : "token is null";
        return !token.isBlank() && isNotValidJwtToken(token);
    }

    private static boolean isJwtToken(String token) {
        assert nonNull(token) : "token is null";
        return !token.isBlank() && isValidJwtToken(token);
    }
  
}
 */

