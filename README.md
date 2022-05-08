# Projet Algorithmes du texte

------

**Auteurs :** *Cheneau Léo*, *Venuti Massimo*, *Damoi Neiko*, *Bangard Jules*

## Points importants :
- Ne pas supprimer les fichiers à la main car nous utilisons la base de données pour la mise à jour de ces derniers.
- Veillez à bien sélectionner des kingdom, groupe, sous-groupe et/ou organisme dans la hiérarchie.
- De même pour les régions à parser (CDS, Introns, etc.). 
- L'interface graphique permet de configurer plusieurs choses dont le nombre de thread à partir du menu config situé en haut à droite. On peut également choisir de prioriser au choix les parsings ou les téléchargements, nous conseillons de garder la priorité au parsing (cf. Config).
- Cocher uniquement la case la plus générale dans la hiérarchie selectionnera tous les organismes qui sont en dessous de cette case. 

--------

## Fonctionnalités

#### Base de données 
Nous utilisons une base de données embarquée SQLite pour repérer les fichiers qui ont besoin d'être mis à jour. Cela offre un gain de temps considérable car nous n'avons pas à parcourir l'intégralité des fichiers déja parsés. 

#### Interface graphique
Notre interface est très pratique car une fois reliée à notre base de données elle permet de voir quels fichiers nécessitent une mise à jour en un coup d'oeil.

#### Téléchargement des données
S'effectue par FTP fusionné avec un parsing des fichiers overview.txt, eukaryotes.txt, etc.

#### Threads
Permet de paralléliser le téléchargement et le parsing des différents fichiers.

#### Config
La configuration permet de selectionner le niveau de priorité accordé aux téléchargements et aux parsings. Au début les threads partagent les téléchargements et le parsing de manière équitable mais après plusieurs itérations ce processus s'optimise automatiquement de sorte à avoir un partage optimal des tâches.
