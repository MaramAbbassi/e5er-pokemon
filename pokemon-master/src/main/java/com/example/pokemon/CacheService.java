package com.example.pokemon;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class CacheService {

    // Cache en mémoire pour stocker les objets
    private Map<Long, Object> cache = new HashMap<>();

    /**
     * Récupère un objet du cache par sa clé.
     * @param key La clé de l'objet.
     * @return L'objet trouvé dans le cache, ou null si non trouvé.
     */
    public Object getFromCache(Long key) {
        return cache.get(key);
    }

    /**
     * Ajoute un objet au cache.
     * @param key La clé de l'objet.
     * @param value L'objet à ajouter au cache.
     */
    public void addToCache(Long key, Object value) {
        cache.put(key, value);
    }

    /**
     * Supprime un objet du cache par sa clé.
     * @param key La clé de l'objet à supprimer.
     */
    public void removeFromCache(Long key) {
        cache.remove(key);
    }

    /**
     * Efface tout le cache.
     */
    public void clearCache() {
        cache.clear();
    }

    // Méthode pour obtenir tous les éléments du cache
    public Map<Long, Object> getAllCache() {
        return new HashMap<>(cache);
    }

}
