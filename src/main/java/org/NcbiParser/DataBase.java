package org.NcbiParser;

import java.sql.SQLException;
import java.util.ArrayList;

/* Tables (id non écrits mais présents dabs chaque table, quand une table est mentionnée à droite de ":", c'est une CE
    sur cette table)
    - Kingdom : nom k
    - Group : nom k
    - Subgroup : nom k
    - Organelle : nom k
    - Organism : + clé Kingdom, Group, Subgroup, nom k
    - ? : Organism, Organelle ?
    - Date : Year, Month, Day, timestamp k
    - Overview : Organism, Date (celle dans overview + viruses + les autres)
    - ParsingResult : Organism, Date (celle des fichiers locaux, ou bien la même que celle dans overview à ce moment là), nb_files_parsed (int)
    [- NC : Organism, NC (String)] pas nécessaire ?
    [- SeqType : nom (CDS, Codon, etc.) ] nécessaire ?]
    [[- Files : (NC ou Organism ?), Date de création)] pas franchement nécessaire]

  Vues:
    - Entrepot_view_mdr : jointure de Kingdom, Group, Subgroup, Organism, Overview et Parsing_Result
 */

public class DataBase {
    /*
        Met à jour la base avec les dernières données d'overview, viruses.txt, x.ids, etc.
     */
    public static void updateFromOverview(ArrayList<OverviewData> overview_parsed) throws SQLException {
        if(DataBaseManager.getConnection_() != null){
            try{
                ArrayList<String> kingdomNames = new ArrayList<String>();
                ArrayList<String> groupNames = new ArrayList<String>();
                ArrayList<String> subgroupNames = new ArrayList<String>();
                ArrayList<String> organismNames = new ArrayList<String>();

                for (var data : overview_parsed) {
                    kingdomNames.add(data.getKingdom());
                    groupNames.add(data.getGroup());
                    subgroupNames.add(data.getSubgroup());
                    organismNames.add(data.getOrganism());
                }
                DataBaseManager.insertOverviewTable(kingdomNames,groupNames,subgroupNames,organismNames);

            }catch(Exception eio){

            }
        }
    }
    //public static void updateFromIds(ArrayList<IdsData> ids_parsed) { } // pas nécessaire ?
    public static void updateFromIndexFile(ArrayList<IndexData> index_parsed) {
        if(DataBaseManager.getConnection_() != null){
            try{
                ArrayList<String> groupNames = new ArrayList<String>();
                ArrayList<String> subgroupNames = new ArrayList<String>();
                ArrayList<String> organismNames = new ArrayList<String>();
                ArrayList<String> GC = new ArrayList<String>();
                ArrayList<String> lastModify = new ArrayList<String>();

                for (var data : index_parsed) {
                    groupNames.add(data.getGroup());
                    subgroupNames.add(data.getSubgroup());
                    organismNames.add(data.getOrganism());
                    GC.add(data.getGc());
                    lastModify.add(data.getModifyDate());
                }
                DataBaseManager.insertIndexesTables(groupNames,subgroupNames,organismNames,GC,lastModify);

            }catch(Exception eio){

            }
        }

    }

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

    /*
        Création de toutes les tables nécessaires
     */
    public static void createOrOpenDataBase(String path) throws ClassNotFoundException {
        DataBaseManager.setDataBase(path);
        //Ouverture de la base ou création si inéxistante
        DataBaseManager.connectionToDb();

        DataBaseManager.createTableDb("CREATE TABLE IF NOT EXISTS OVERVIEW (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "kingdom TEXT," +
                "groupe TEXT," +
                "subgroup TEXT," +
                "organisme TEXT," +
                "organelle TEXT)");

        DataBaseManager.createTableDb("CREATE TABLE IF NOT EXISTS INDEXES (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "groupe TEXT," +
                "subgroup TEXT," +
                "organisme TEXT," +
                "GC TEXT," +
                "last_modify TEXT)");

        DataBaseManager.createTableDb("CREATE TABLE IF NOT EXISTS FILES (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Type TEXT," +
                "indexes_id INTEGER," +
                "date TEXT)");


    }

}


