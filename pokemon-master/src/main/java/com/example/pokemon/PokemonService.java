package com.example.pokemon;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;



import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.Random;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@ApplicationScoped
public class PokemonService {


    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ConcurrentHashMap<Long, Boolean> entrainementEnCours = new ConcurrentHashMap<>();


    @Inject
    EntityManager em;

    @Inject
    CacheService cacheService;  // Injection du service de cache

    @Inject
    @RestClient
    UserServiceClient userServiceClient;


    private Map<Long, Pokemon> cachePokemon = new HashMap<>();

    private static final String API_URL = "https://pokeapi.co/api/v2/pokemon/";


    @Transactional
    public Pokemon creerPokemon(String nom, String description, double valeurReelle) {
        Pokemon pokemon = new Pokemon(nom, description, valeurReelle);
        em.persist(pokemon);
        return pokemon;
    }

    public Pokemon trouverPokemon(Long id) {
        return em.find(Pokemon.class, id);
    }


    public Double getMiseAPrix(Long pokemonId) {
        // Rechercher le Pokémon dans la base de données
        Pokemon pokemon = em.find(Pokemon.class, pokemonId);

        if (pokemon == null) {
            return null; // Retourne null si aucun Pokémon n'est trouvé
        }

        // Calculer ou retourner directement la mise à prix
        return pokemon.getValeurReelle(); // Exemple : utiliser la valeur réelle comme mise à prix
    }



    public List<Pokemon> listerPokemons() {
        return em.createQuery("SELECT p FROM Pokemon p", Pokemon.class).getResultList();
    }

    @Transactional
    public void supprimerPokemon(Long id) {
        Pokemon pokemon = em.find(Pokemon.class, id);
        if (pokemon != null) {
            em.remove(pokemon);
        }
    }

    private Pokemon recupererPokemonDepuisAPI(Long id) throws IOException {
        URL url = new URL(API_URL + id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(connection.getInputStream());

            // Extraire les informations du Pokémon à partir de la réponse JSON
            String nom = rootNode.get("name").asText();
            String description = "Description non disponible"; // À ajuster si disponible dans l'API

            // Extraire les types du Pokémon
            /*
            List<String> types = new ArrayList<>();
            JsonNode typesNode = rootNode.get("types");
            if (typesNode.isArray()) {
                for (JsonNode typeNode : typesNode) {
                    types.add(typeNode.get("name").asText());
                }
            }
            */
            // Construire la liste des types
            List<String> typesList = new ArrayList<>();
            JsonNode typesNode = rootNode.get("types");
            for (JsonNode type : typesNode) {
                String typeName = type.get("type").get("name").asText();
                typesList.add(typeName);
            }


            // Construire la Map des stats
            Map<String, Integer> stats = new HashMap<>();
            JsonNode statsNode = rootNode.get("stats");
            for (JsonNode stat : statsNode) {
                String statName = stat.get("stat").get("name").asText();
                int baseStat = stat.get("base_stat").asInt();
                stats.put(statName, baseStat);
            }

            /*
            // Extraire les statistiques du Pokémon et les stocker dans une map
            Map<String, Integer> stats = new HashMap<>();
            stats.put("hp", rootNode.get("stats").get("hp").asInt());
            stats.put("atk", rootNode.get("stats").get("atk").asInt());
            stats.put("def", rootNode.get("stats").get("def").asInt());
            stats.put("speAtk", rootNode.get("stats").get("spe_atk").asInt());
            stats.put("speDef", rootNode.get("stats").get("spe_def").asInt());
            stats.put("vit", rootNode.get("stats").get("vit").asInt());

            // Calculer la valeur réelle du Pokémon
            int hp = stats.get("hp");
            int atk = stats.get("atk");
            int def = stats.get("def");
            int speAtk = stats.get("speAtk");
            int speDef = stats.get("speDef");
            int vit = stats.get("vit");
            */

            // Récupérer les statistiques
            int hp = stats.getOrDefault("hp", 0);
            int atk = stats.getOrDefault("attack", 0);
            int def = stats.getOrDefault("defense", 0);
            int speAtk = stats.getOrDefault("special-attack", 0);
            int speDef = stats.getOrDefault("special-defense", 0);
            int vit = stats.getOrDefault("speed", 0);

            // Calcul de la valeur réelle du Pokémon
            double valeurReelle = (hp * 1.5) + (atk * 1.2) + (def * 1.1) + (speAtk * 1.1) + (speDef * 1.1) + (vit * 1.0);

            // Créer un objet Pokemon avec la valeur réelle calculée
            Pokemon pokemon = new Pokemon(nom, description, (int) valeurReelle);
            pokemon.setTypes(typesList);  // Associer les types
            pokemon.setStats(stats);  // Associer les statistiques

            return pokemon;
        } else {
            throw new IOException("Erreur lors de la récupération du Pokémon");
        }
    }


    public Pokemon recupererPokemon(Long id) {
        // Vérifier si le Pokémon est dans le cache
        Pokemon pokemonCache = (Pokemon) cacheService.getFromCache(id);
        if (pokemonCache != null) {
            return pokemonCache; // Si oui, retourne-le depuis le cache
        }

        // Vérifier si le Pokémon est dans la base de données
        Pokemon pokemonDb = em.find(Pokemon.class, id);
        if (pokemonDb != null) {
            // Si oui, met-le dans le cache pour une prochaine fois
            cacheService.addToCache(id, pokemonDb);
            return pokemonDb;
        }

        // Si le Pokémon n'est pas dans le cache ni la base de données, récupère-le depuis l'API
        try {
            Pokemon pokemonApi = recupererPokemonDepuisAPI(id);
            // Sauvegarde le Pokémon dans la base de données
            em.persist(pokemonApi);
            em.flush(); // Assure-toi que l'objet est bien persistant
            // Mets le Pokémon dans le cache
            cacheService.addToCache(id, pokemonApi);
            return pokemonApi;
        } catch (IOException e) {
            // Gère les erreurs d'API ici
            e.printStackTrace();
        }
        return null;
    }

    // Méthode pour générer un Pokémon aléatoire
    @Transactional
    public Pokemon getRandomPokemon() {
        Random random = new Random();
        Long randomId = (long) (random.nextInt(718) + 1); // Tirage entre 1 et 718
        return recupererPokemon(randomId);
    }

    @Transactional
    public void addAuctionHistory(Long pokemonId, Enchere enchere) {
        // Récupérer le Pokémon depuis la base de données
        Pokemon pokemon = em.find(Pokemon.class, pokemonId);

        if (pokemon == null) {
            throw new IllegalArgumentException("Pokémon non trouvé pour l'ID : " + pokemonId);
        }

        // Vérifier que l'enchère appartient bien à ce Pokémon
        if (!pokemonId.equals(enchere.getPokemonId())) {
            throw new IllegalArgumentException("L'enchère ne correspond pas à ce Pokémon.");
        }

        // Ajouter ou mettre à jour l'historique des enchères
        pokemon.getHistorique_encheres().put(enchere.getUserId(), (int) enchere.getHighestBid());

        em.merge(pokemon);

        // Log pour confirmation
        System.out.println("Enchère ajoutée avec succès pour le Pokémon ID : " + pokemonId);
    }



    @Transactional
    public void entrainerPokemon(Long pokemonId, Long userId,  double pourcentage) {
        // Vérifier si un entraînement est déjà en cours pour ce Pokémon
        if (Boolean.TRUE.equals(entrainementEnCours.get(pokemonId))) {
            throw new IllegalStateException("Un entraînement est déjà en cours pour ce Pokémon.");
        }

        if (pourcentage > 10) {
            throw new IllegalArgumentException("Un Pokémon ne peut gagner plus de 10% par session.");
        }

        // Récupérer le Pokémon
        Pokemon pokemon = em.find(Pokemon.class, pokemonId);
        if (pokemon == null) {
            throw new IllegalArgumentException("Pokémon non trouvé.");
        }


        // Récupérer l'utilisateur
        /*
        User user = userServiceClient.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException(" Utilisateur non trouvé.");
        }

        // Vérifier si l'utilisateur a assez de Limcoins pour l'entraînement
        int coutEnLimcoins = calculerCoutEnLimcoins(pourcentage);
        if (user.getLimCoins() < coutEnLimcoins) {
            throw new IllegalArgumentException("L'utilisateur n'a pas assez de Limcoins.");
        }
        */


        // Calculer le délai d'attente
        int nombreTranches = (int) (pourcentage / 1); // chaque tranche de 1% = 5 minutes
        long tempsNecessaire = nombreTranches * 1L * 60 * 1000; // en millisecondes

        // Ajouter le Pokémon à la liste des entraînements en cours
        entrainementEnCours.put(pokemonId, true);

        System.out.println("Entraînement démarré pour le Pokémon : " + pokemon.getNom());

        // Planifier la mise à jour des stats
        executorService.submit(() -> {
            try {
                // Simuler l'attente du temps d'entraînement
                Thread.sleep(tempsNecessaire);

                // Appliquer les augmentations
                appliquerAugmentation(pokemonId, userId, pourcentage);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("L'entraînement a été interrompu.", e);
            } finally {
                // Retirer le Pokémon de la liste des entraînements en cours
                entrainementEnCours.remove(pokemonId);
            }
        });
    }


    private int calculerCoutEnLimcoins(double pourcentage) {
        // Chaque pourcentage coûte 250 Limcoins
        return (int) ((pourcentage / 5) * 250);
    }


    // Méthode d'entraînement : augmente les stats du Pokémon par pourcentage
    @Transactional
    public void appliquerAugmentation(Long pokemonId, Long UserId, double pourcentage) {
        Pokemon pokemon = em.find(Pokemon.class, pokemonId);
        //User user = em.find(User.class, UserId);

        // Appliquer les augmentations des stats et de la valeur réelle
        pokemon.setStats(calculStat(pokemon, pourcentage));
        pokemon.setValeurReelle(pokemon.getValeurReelle() * (1 + pourcentage / 100));

        em.merge(pokemon);

        //em.merge(user);

        // Log
        System.out.println("Entraînement terminé pour Pokémon " + pokemon.getNom());
    }


    private Map<String, Integer> calculStat(Pokemon pokemon, double pourcentage) {
        Map<String, Integer> newStats = pokemon.getStats();
        // Appliquer l'augmentation des stats
        for (Map.Entry<String, Integer> entry : newStats.entrySet()) {
            int newValue = (int) (entry.getValue() * (1 + pourcentage / 100.0));
            newStats.put(entry.getKey(), newValue);
        }

        return newStats;
    }


    public List<Pokemon> findMostExpensivePokemons() {
        // Récupérer tous les Pokémon
        List<Pokemon> pokemons = em.createQuery("SELECT p FROM Pokemon p", Pokemon.class).getResultList();

        // Trier les Pokémon en fonction de la plus haute enchère dans historique_encheres
        return pokemons.stream()
                .filter(pokemon -> !pokemon.getHistorique_encheres().isEmpty()) // Filtrer ceux ayant un historique
                .sorted((p1, p2) -> {
                    // Trouver la plus haute enchère dans l'historique pour chaque Pokémon
                    int maxBid1 = p1.getHistorique_encheres().values().stream()
                            .max(Integer::compare) // Trouver la valeur la plus haute
                            .orElse(0); // Si pas d'enchères, la valeur est 0
                    int maxBid2 = p2.getHistorique_encheres().values().stream()
                            .max(Integer::compare)
                            .orElse(0);

                    // Comparer les enchères de manière décroissante
                    return Integer.compare(maxBid2, maxBid1);
                })
                .limit(5) // Limiter aux 5 Pokémon les plus chers
                .collect(Collectors.toList());
    }



}
