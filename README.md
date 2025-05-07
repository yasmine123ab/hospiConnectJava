# HospiConnect – Application Java Desktop

## Overview

Hospiconnect a été réalisé dans le cadre du module **PIDEV 3A** à **Esprit School of Engineering** durant l’année universitaire **2024-2025**.
Il s’agit d’une application **Java Desktop** conçue pour faciliter la **gestion hospitalière**, avec une interface utilisateur développée en **JavaFX**. L'application couvre six modules principaux : gestion des utilisateurs, laboratoires, consultations, dons, matériel et stock, et opérations médicales.

---

## Features

- 🔐 Authentification sécurisée et gestion des utilisateurs
- 🧪 Gestion des laboratoires et des résultats d'analyses
- 🩺 Suivi des consultations médicales
- 🎁 Gestion des dons (matériels, financiers) et des demandes
- 🛠️ Suivi du matériel médical et du stock
- 🏥 Planification et enregistrement des opérations

---

## Tech Stack

### Frontend
- JavaFX (UI)
- FXML
- Scene Builder

### Backend
- Java 21
- JDBC
- MySQL (via XAMPP)

### Other Tools
- Git & GitHub
- IntelliJ IDEA
- phpMyAdmin


---

## Directory Structure
```
src/
├── controller/ → Contrôleurs FXML pour chaque module
├── model/ → Classes métiers (Don, Utilisateur, Consultation…)
├── service/ → Traitement logique par module
├── utils/ → Outils : DB connection, alertes, helpers
├── Main.java → Point d’entrée
resources/
├── fxml/ → Interfaces utilisateur JavaFX
├── css/ → Styles JavaFX
├── images/ → Ressources visuelles Acknowledgments
```

Ce projet a été réalisé par le groupe Zenith dans le cadre du cours PIDEV 3A encadré par Monsieur KLAI Ghassen , à Esprit School of Engineering.

