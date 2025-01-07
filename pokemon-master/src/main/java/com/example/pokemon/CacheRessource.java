package com.example.pokemon;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.Map;

@Path("/cache")
public class CacheRessource {
    @Inject
    CacheService cacheService;

    @GET
    @Path("/afficherCaches")
    public Map<Long, Object> afficherCache() {
        return cacheService.getAllCache();
    }
}
