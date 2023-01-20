package comptoirs.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import comptoirs.entity.Produit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
 // Ce test est basé sur le jeu de données dans "test_data.sql"
class CommandeServiceTest {
    private static final String ID_PETIT_CLIENT = "0COM";
    private static final String ID_GROS_CLIENT = "2COM";
    private static final String VILLE_PETIT_CLIENT = "Berlin";
    private static final BigDecimal REMISE_POUR_GROS_CLIENT = new BigDecimal("0.15");

    @Autowired
    private CommandeService service;
    @Test
    void testCreerCommandePourGrosClient() {
        var commande = service.creerCommande(ID_GROS_CLIENT);
        assertNotNull(commande.getNumero(), "On doit avoir la clé de la commande");
        assertEquals(REMISE_POUR_GROS_CLIENT, commande.getRemise(),
            "Une remise de 15% doit être appliquée pour les gros clients");
    }

    @Test
    void testCreerCommandePourPetitClient() {
        var commande = service.creerCommande(ID_PETIT_CLIENT);
        assertNotNull(commande.getNumero());
        assertEquals(BigDecimal.ZERO, commande.getRemise(),
            "Aucune remise ne doit être appliquée pour les petits clients");
    }

    @Test
    void testCreerCommandeInitialiseAdresseLivraison() {
        var commande = service.creerCommande(ID_PETIT_CLIENT);
        assertEquals(VILLE_PETIT_CLIENT, commande.getAdresseLivraison().getVille(),
            "On doit recopier l'adresse du client dans l'adresse de livraison");
    }   

    @Test
    void testEnregistrerExpeditionPourGrosClient(){
        // On crée une commande
        var commande = service.creerCommande(ID_GROS_CLIENT);

        //On récupère les unités en stocks pour chaque produit de la commande avant la mise à jour
        var lignesBefore = commande.getLignes();
        var unitesEnStockBefore = new ArrayList<Integer>();
        for(int i = 0; i < lignesBefore.size(); i++){
            unitesEnStockBefore.add(lignesBefore.get(i).getProduit().getUnitesEnStock());
        }

        // On vérifie que la date d'éxpédition a été renseigné au bon jour
        commande = service.enregistreExpédition(commande.getNumero());
        assertEquals(LocalDate.now(), commande.getEnvoyeele());

        //On récupère les unités en stocks pour chaque produit de la commande après la mise à jour et la quantité pour chaque produit
        var lignesAfter = commande.getLignes();
        var unitesEnStockAfter = new ArrayList<Integer>();
        var quantites = new ArrayList<Integer>();
        for(int i = 0; i < lignesAfter.size(); i++){
            unitesEnStockAfter.add(lignesAfter.get(i).getProduit().getUnitesEnStock());
            quantites.add(lignesAfter.get(i).getQuantite());
        }
        
        // On teste que la mise à jour des stocks à bien été réalisé 
        if(unitesEnStockAfter.size() == unitesEnStockBefore.size()){
            for(int i = 0; i < unitesEnStockBefore.size(); i++){
                assertEquals(unitesEnStockBefore.get(i), unitesEnStockAfter.get(i) + quantites.get(i));
            }
        }
    }

    void testEnregistrerExpeditionPourPetitClient(){
        // On crée une commande
        var commande = service.creerCommande(ID_PETIT_CLIENT);

        //On récupère les unités en stocks pour chaque produit de la commande avant la mise à jour
        var lignesBefore = commande.getLignes();
        var unitesEnStockBefore = new ArrayList<Integer>();
        for(int i = 0; i < lignesBefore.size(); i++){
            unitesEnStockBefore.add(lignesBefore.get(i).getProduit().getUnitesEnStock());
        }

        // On vérifie que la date d'éxpédition a été renseigné au bon jour
        commande = service.enregistreExpédition(commande.getNumero());
        assertEquals(LocalDate.now(), commande.getEnvoyeele());

        //On récupère les unités en stocks pour chaque produit de la commande après la mise à jour et la quantité pour chaque produit
        var lignesAfter = commande.getLignes();
        var unitesEnStockAfter = new ArrayList<Integer>();
        var quantites = new ArrayList<Integer>();
        for(int i = 0; i < lignesAfter.size(); i++){
            unitesEnStockAfter.add(lignesAfter.get(i).getProduit().getUnitesEnStock());
            quantites.add(lignesAfter.get(i).getQuantite());
        }
        
        // On teste que la mise à jour des stocks à bien été réalisé 
        if(unitesEnStockAfter.size() == unitesEnStockBefore.size()){
            for(int i = 0; i < unitesEnStockBefore.size(); i++){
                assertEquals(unitesEnStockBefore.get(i), unitesEnStockAfter.get(i) + quantites.get(i));
            }
        }
    }
}
