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
- **Langages :** Java (principal), Kotlin (WaveRecorder)  
- **Base de données :** SQLite via `VoiceMemoDBHelper`  
- **Composants principaux :**
  - `MainActivity` → Liste + Recherche + Navigation  
  - `RecordingActivity` → Interface d’enregistrement  
  - `Dialog Audio Player` → Lecture et visualisation d’onde  
  - `VoiceMemoAdapter` → Gestion de la liste  
  - `WaveformView` → Affichage des amplitudes  

---

## 📲 Installation et exécution
1. **Cloner le projet :**
   ```bash
   git clone https://github.com/abdoch03/voice-recording-app.git
