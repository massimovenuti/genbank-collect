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
                for (var data : overview_parsed) {
                    int indKindom = DataBaseManager.insertOrIgnoreIdNom("KINGDOM", data.getKingdom());
                    int indGroup = DataBaseManager.insertOrIgnoreIdNom("GROUPE", data.getGroup());
                    int indSubgroup = DataBaseManager.insertOrIgnoreIdNom("SUBGROUP", data.getSubgroup());
                    DataBaseManager.insertOrIgnoreOrganism(indKindom,indGroup,indSubgroup,data.getOrganism());
                }

            }catch(Exception eio){

            }
        }
    }
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

    /*
        Création de toutes les tables nécessaires
     */
    public static void createOrOpenDataBase(String path) throws ClassNotFoundException {
        DataBaseManager.setDataBase(path);
        //Ouverture de la base ou création si inéxistante
        DataBaseManager.connectionToDb();

        DataBaseManager.createTableDb("CREATE TABLE IF NOT EXISTS KINGDOM (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nom TEXT UNIQUE)");

        DataBaseManager.createTableDb("CREATE TABLE IF NOT EXISTS GROUPE (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nom TEXT UNIQUE)");

        DataBaseManager.createTableDb("CREATE TABLE IF NOT EXISTS SUBGROUP (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nom TEXT UNIQUE)");

        DataBaseManager.createTableDb("CREATE TABLE IF NOT EXISTS ORGANELLE (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nom TEXT UNIQUE," +
                "organism_id INTEGER," +
                "FOREIGN KEY (organism_id) REFERENCES ORGANISM(id)" +
                "   ON DELETE CASCADE ON UPDATE CASCADE)");

        DataBaseManager.createTableDb("CREATE TABLE IF NOT EXISTS ORGANISM (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "kingdom_id INTEGER," +
                "group_id INTEGER," +
                "subgroup_id INTEGER," +
                "nom TEXT UNIQUE," +
                "UNIQUE(kingdom_id,group_id,subgroup_id,nom)," +
                "FOREIGN KEY (kingdom_id) REFERENCES KINGDOM(id)" +
                "   ON DELETE CASCADE ON UPDATE CASCADE," +
                "FOREIGN KEY (group_id) REFERENCES GROUPE(id)" +
                "   ON DELETE CASCADE ON UPDATE CASCADE," +
                "FOREIGN KEY (subgroup_id) REFERENCES SUBGROUP(id)" +
                "   ON DELETE CASCADE ON UPDATE CASCADE)");

        DataBaseManager.createTableDb("CREATE TABLE IF NOT EXISTS HIERARCHYDATE (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "organism_id INTEGER," +
                "date_id INTEGER," +
                "FOREIGN KEY (organism_id) REFERENCES ORGANISM(id)" +
                "   ON DELETE CASCADE ON UPDATE CASCADE," +
                "FOREIGN KEY (date_id) REFERENCES DATE(id)" +
                "   ON DELETE CASCADE ON UPDATE CASCADE)");


        DataBaseManager.createTableDb("CREATE TABLE IF NOT EXISTS PARSINGRESULTS (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "organism_id INTEGER," +
                "date_id INTEGER," +
                "nb_files_parsed INTEGER," +
                "FOREIGN KEY (organism_id) REFERENCES ORGANISM(id)" +
                "   ON DELETE CASCADE ON UPDATE CASCADE," +
                "FOREIGN KEY (date_id) REFERENCES DATE(id)" +
                "   ON DELETE CASCADE ON UPDATE CASCADE)");

    }

}


