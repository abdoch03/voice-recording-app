# 🎤 Voice Memo – Application Android de mémos vocaux

## 📌 Description
Voice Memo est une application Android qui permet d’enregistrer, gérer et écouter facilement des mémos vocaux.  
Elle offre une interface moderne et intuitive, avec une **visualisation en temps réel de l’onde sonore** lors de l’enregistrement.

---

## ⚡ Fonctionnalités principales
- 🎙️ **Enregistrement vocal** avec pause/reprise et suppression en cours  
- 🌊 **Visualisation de l’amplitude sonore** en temps réel (Waveform)  
- 📂 **Gestion des mémos vocaux** : liste avec nom, durée et date  
- ✏️ **Renommer et supprimer** les mémos enregistrés  
- 🔍 **Recherche en temps réel** par nom  
- 🎧 **Lecture des mémos** dans une popup avec timer, barre de progression et visualisation audio  

---

## 🏗️ Architecture du projet

- **Langages utilisés :**
  - Java (logique principale & interface)
  - Kotlin (librairie `WaveRecorder` pour la capture audio)

- **Base de données :**
  - SQLite via `VoiceMemoDBHelper` pour stocker les informations sur les mémos (nom, chemin, durée, date)

- **Composants principaux :**
  ### 📱 Activités et vues
  - `MainActivity.java` → Page d’accueil avec :
    - Liste des mémos vocaux
    - Barre de recherche temps réel
    - Navigation vers l’enregistrement
    - Lecture d’un mémo via un **dialog personnalisé**
  - `recording.java` → Page d’enregistrement avec pause/reprise, suppression, validation et visualisation d’onde
  - `WaveformView.java` → Composant personnalisé pour afficher l’amplitude sonore en temps réel

  ### 📦 Modèles & Gestion des données
  - `VoiceMemo.java` → Classe modèle représentant un mémo vocal
  - `VoiceMemoAdapter.java` → Gestion de l’affichage des mémos dans la `ListView`
  - `VoiceMemoDBHelper.java` → Gestion de la base SQLite (insertion, suppression, mise à jour)

  ### 🎵 Bibliothèque interne pour l’audio (`recorderlib/`)
  - `WaveRecorder.kt` → Gestion principale de l’enregistrement audio
  - `Calculate.kt` → Calcul des amplitudes
  - `FileWriter.kt` & `WaveHeaderWriter.kt` → Écriture des fichiers WAV
  - `RecorderState.kt` → États de l’enregistreur (en cours, pause, stop)
  - `WaveConfig.kt` → Configuration de l’enregistrement audio
  - `SilenceDetectionConfig.kt` → Gestion optionnelle de la détection de silence

---

Cette organisation rend l’application **modulaire**, avec une séparation claire entre :  
- **UI & navigation** (Activities & WaveformView)  
- **Gestion des données** (Model + Adapter + DBHelper)  
- **Enregistrement audio** (recorderlib Kotlin)

---

## 📲 Installation et exécution
1. **Cloner le projet :**
   ```bash
   git clone https://github.com/abdoch03/voice-recording-app.git
