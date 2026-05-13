package lab6.rest;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.jsonb.JsonBindingFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class RestApplication extends ResourceConfig {
    public RestApplication() {
        register(JsonBindingFeature.class);
        register(UserResource.class);
        register(CompetitionResource.class);
    }
}