package org.hospiconnect.Tests;

import org.hospiconnect.model.RendezVous;
import org.hospiconnect.service.RendezVousService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            RendezVousService service = new RendezVousService();
            // Test d'insertion
            System.out.println("Test d'insertion d'un rendez-vous");
            // Define the necessary variables for the RendezVous constructor
            String nom = "Test";
            String prenom = "User";
            String telephone = "1234567890";
            String email = "test.user@email.com";
            LocalDate date = LocalDate.now(); // e.g., "2025-04-16"
            LocalTime heure = LocalTime.now(); // e.g., "14:30"
            String type = "Opération";
            String gravite = "Moyenne";
            String commentaire = "Test rendez-vous";
            // Test d'insertion

            System.out.println("Test d'insertion d'un rendez-vous");
            RendezVous rdv = new RendezVous(nom, prenom, telephone, email, date, heure, type, gravite, commentaire);
            rdv.setDate(LocalDate.now());
            rdv.setHeure(LocalTime.now());
            rdv.setType("Opération");
            rdv.setStatut("Confirmé");
            service.insert(rdv);
            System.out.println("Rendez-vous inséré avec succès");

            // Test de récupération
            System.out.println("\nTest de récupération de tous les rendez-vous");
            List<RendezVous> rendezVous = service.findAll();
            for (RendezVous r : rendezVous) {
                System.out.println("Rendez-vous: " + r.getDate() + " " + r.getHeure() + " - " + r.getType());
            }

            // Test de modification
            System.out.println("\nTest de modification d'un rendez-vous");
            if (!rendezVous.isEmpty()) {
                RendezVous rdvToUpdate = rendezVous.get(0);
                rdvToUpdate.setStatut("Annulé");
                service.update(rdvToUpdate);
                System.out.println("Rendez-vous modifié avec succès");
            }

            // Test de suppression
            System.out.println("\nTest de suppression d'un rendez-vous");
            if (!rendezVous.isEmpty()) {
                service.delete(rendezVous.get(0));
                System.out.println("Rendez-vous supprimé avec succès");
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }
} 