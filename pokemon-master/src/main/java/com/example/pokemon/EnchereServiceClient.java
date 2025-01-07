package com.example.pokemon;

import jakarta.ws.rs.*;


import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@RegisterRestClient(baseUri = "http://localhost:8085/Encheres") // URL de la ressource EnchereResource
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EnchereServiceClient {

    // 1. Placer une enchère
    @GET
    @Path("/{id}/{userId}/{Bid}")
    Response placerBid(@PathParam("id") Long id, @PathParam("userId") Long userId, @PathParam("Bid") double bid);

    // 2. Récupérer une enchère par ID
    @GET
    @Path("/Enchere/{id}")
    Enchere getEnchereById(@PathParam("id") Long id);

    // 3. Récupérer toutes les enchères actives
    @GET
    List<Enchere> getAllEncheres();

    // 4. Récupérer toutes les enchères par type
    @GET
    @Path("/{type}")
    List<Enchere> getAllEncheresByType(@PathParam("type") String type);

    // 5. Ajouter un historique d'enchère pour un Pokémon
    @POST
    @Path("/{pokemonId}/addAuctionHistory")
    Response addAuctionHistory(@PathParam("pokemonId") Long pokemonId, Enchere enchere);





}
