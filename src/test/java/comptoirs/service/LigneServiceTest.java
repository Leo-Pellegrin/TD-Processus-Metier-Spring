package comptoirs.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;

import comptoirs.dao.CommandeRepository;
import comptoirs.dao.ProduitRepository;
import comptoirs.entity.Commande;
import jakarta.validation.ConstraintViolationException;

@SpringBootTest
 // Ce test est basé sur le jeu de données dans "test_data.sql"
class LigneServiceTest {
    static final int NUMERO_COMMANDE_DEJA_LIVREE = 99999;
    static final int NUMERO_COMMANDE_PAS_LIVREE  = 99998;
    static final int REFERENCE_PRODUIT_DISPONIBLE_1 = 93;
    static final int REFERENCE_PRODUIT_DISPONIBLE_2 = 94;
    static final int REFERENCE_PRODUIT_DISPONIBLE_3 = 95;
    static final int REFERENCE_PRODUIT_DISPONIBLE_4 = 96;
    static final int REFERENCE_PRODUIT_INDISPONIBLE = 97;
    static final int UNITES_COMMANDEES_AVANT = 0;

    @Autowired
    LigneService service;

    @Autowired
    ProduitRepository produitDAO;

    @Test
    void onPeutAjouterDesLignesSiPasLivre() throws Exception {
        var ligne = service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_1, 1);
        assertNotNull(ligne.getId(),
        "La ligne doit être enregistrée, sa clé générée"); 
    }

    @Test
    void laQuantiteEstPositive() throws Exception  {
        assertThrows(ConstraintViolationException.class, 
            () -> service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_1, -3),
            "La quantite d'une ligne doit être positive");
    }

    @Test
    void testAjouterLigneSiDejaLivree() throws Exception {
        assertThrows(Exception.class, 
        () -> service.ajouterLigne(NUMERO_COMMANDE_DEJA_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_1, 3),
        "Ligne deja livree");
    }

    @Test
    void testAjouterLigneSiProduitPasEnStock() throws Exception{
        assertThrows(Exception.class,
        () -> service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_INDISPONIBLE, 1),
        "Quantite indisponible");
    }   

    @Test
    void testAjouterLigneMiseAJourUnitesCommandees() throws Exception{
        var produit = produitDAO.findById(REFERENCE_PRODUIT_DISPONIBLE_3).orElseThrow();
        var ligne = service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_3, 3);

        assertEquals(ligne.getProduit().getUnitesCommandees(), produit.getUnitesCommandees() + 3);
    }
}
