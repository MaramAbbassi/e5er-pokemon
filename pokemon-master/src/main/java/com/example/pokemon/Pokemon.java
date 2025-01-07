package com.example.pokemon;




import com.example.pokemon.Enchere;
import jakarta.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Pokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;
    private int miseAPrix;
    private double valeurReelle;




    @ElementCollection
    @CollectionTable(name = "pokemon_types", joinColumns = @JoinColumn(name = "pokemon_id"))
    @Column(name = "type")
    private List<String> types;


    @ElementCollection
    @CollectionTable(name = "pokemon_stats", joinColumns = @JoinColumn(name = "pokemon_id"))
    @MapKeyColumn(name = "stat_name")
    @Column(name = "stat_value")
    private Map<String, Integer> stats;


    @ElementCollection
    @CollectionTable(name = "pokemon_encheres", joinColumns = @JoinColumn(name = "pokemon_id"))
    @MapKeyColumn(name = "utilisateur_id")
    @Column(name = "montant_enchere")
    private Map<Long, Integer> historique_encheres = new HashMap<>(); // On stocke id_utilisateur et montant



    public Pokemon() {}

    public Pokemon(String nom, String description, double valeurReelle) {
        this.nom = nom;
        this.description = description;
        this.valeurReelle = valeurReelle;
        this.miseAPrix = (int) (valeurReelle * (0.6 + Math.random() * 0.8));
    }


    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMiseAPrix() {
        return miseAPrix;
    }

    public void setMiseAPrix(int miseAPrix) {
        this.miseAPrix = miseAPrix;
    }

    public double getValeurReelle() {
        return valeurReelle;
    }

    public void setValeurReelle(double valeurReelle) {
        this.valeurReelle = valeurReelle;
    }

    public Map<Long, Integer> getHistorique_encheres() {return this.historique_encheres;}



    public void ajouterEnchere(Long utilisateurId, int montant) {
        historique_encheres.put(utilisateurId, montant);
    }

    public void supprimerEnchere(Long utilisateurId) {
        historique_encheres.remove(utilisateurId);
    }

    public Integer getMontantEnchere(Long utilisateurId) {
        return historique_encheres.get(utilisateurId);
    }





    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public Map<String, Integer> getStats() {
        return stats;
    }

    public void setStats(Map<String, Integer> stats) {
        this.stats = stats;
    }

}
