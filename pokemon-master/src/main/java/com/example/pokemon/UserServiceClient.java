package com.example.pokemon;



import jakarta.ws.rs.*;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@RegisterRestClient(configKey = "user-service")
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface UserServiceClient {

    @GET
    List<User> getAllUsers();

    @GET
    @Path("/{id}")
    User getUserById(@PathParam("id") Long id);

    @POST
    Response addUser(User user);

    @PUT
    @Path("/{id}")
    Response updateUser(@PathParam("id") Long id, User user);

    @POST
    @Path("/{id}/deduct-coins")
    Response deductCoins(@PathParam("id") Long id, @QueryParam("amount") int amount);

    @POST
    @Path("/{id}/add-mcoins")
    Response addLimCoins(@PathParam("id") Long id, @QueryParam("amount") int amount);

    @POST
    @Path("/{id}/deduct-Limcoins")
    Boolean deductLimCoins(@PathParam("id") Long id, @QueryParam("amount") int amount);

    @POST
    @Path("/{userId}/add-pokemon")
    Response addPokemonToUser(@PathParam("userId") Long userId, Pokemon pokemon);

    @GET
    @Path("/{userId}/pokemons")
    Response getUserPokemons(@PathParam("userId") Long userId);

    @POST
    @Path("/{userId}/{enchereId}/amount/place-bid")
    Response placeBid(@PathParam("userId") Long userId, @PathParam("enchereId") Long enchereId, @QueryParam("amount") double amount);

    @GET
    @Path("/{userId}/bids")
    Response getUserEncheres(@PathParam("userId") Long userId);

    @POST
    @Path("/{userId}/sell-pokemon/{pokemonId}")
    Response sellPokemonToSystem(@PathParam("userId") Long userId, @PathParam("pokemonId") Long pokemonId);

    @GET
    @Path("/top-limcoins")
    Response getTopUsersByLimCoins();
}
