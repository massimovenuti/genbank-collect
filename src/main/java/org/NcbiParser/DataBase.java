package org.NcbiParser;

import java.util.ArrayList;

/* Tables (id non écrits mais présents dabs chaque table, quand une table est mentionnée à droite de ":", c'est une CE
    sur cette table)
    - Kingdom : nom
    - Group : nom
    - Subgroup : nom
    - Organism : Kingdom, Group, Subgroup, nom
    - Date : Year, Month, Day, timestamp
    - Overview : Organism, Date (celle dans overview)
    [- NC : Organism, NC (String)] pas nécessaire ?
    [- SeqType : nom (CDS, Codon, etc.) ] nécessaire ?]
    - ParsingResult : Organism, Date (celle des fichiers locaux, ou bien la même que celle dans overview à ce moment là), nb_files_parsed (int)
    [[- Files : (NC ou Organism ?), Date de création)] pas franchement nécessaire]

  Vues:
    - Entrepot_view_mdr : jointure de Kingdom, Group, Subgroup, Organism, Overview et Parsing_Result
 */

public class DataBase {
    /*
        Met à jour la base avec les dernières données d'overview, viruses.txt, x.ids, etc.
     */
    public static void updateFromOverview(ArrayList<OverviewData> overview_parsed) { }
    //public static void updateFromIds(ArrayList<IdsData> ids_parsed) { } // pas nécessaire ?
    public static void updateFromIndexFile(ArrayList<IndexData> index_parsed) { }

    /*
        à partir de la base mise à jour, donne les ids des organismes pour
        lesquels les deux dates (celle dans overview, et la dernière maj locale) diffèrent
        normalement juste une comparaison de deux colonnes, SQL fait tout le taf

        Peut être renvoyer direct (kingdom, group, subgroup, organism) pour aller plus vite ?
     */
    public static ArrayList<Integer> allIdsNeedingUpdate() { return null;}
    //public static ArrayList<OverviewData> allOrganismNeddingUpdate() {

    /*
        Récupère la hiérarchie depuis la BDD
     */
    public static TreeNode compute_hierarchy() {return null;}

}


