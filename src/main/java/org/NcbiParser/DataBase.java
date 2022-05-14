package org.NcbiParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    - ParsingResult : Organism, Date (celle des fichiers locaux, ou bien la même que celle dans overview à ce moment là), nb_files_parsed (int)
    [[- Files : (NC ou Organism ?), Date de création)] pas franchement nécessaire]

  Vues:
    - Entrepot_view_mdr : jointure de Kingdom, Group, Subgroup, Organism, Overview et Parsing_Result
 */

public class DataBase {

    public static ArrayList<UpdateRow> getGlobalRegroupedData() {
        return globalRegroupedData;
    }

    private static ArrayList<UpdateRow> globalRegroupedData;
    //public static void updateFromIds(ArrayList<IdsData> ids_parsed) { } // pas nécessaire ?


    public static void updateFromIndexAndOverview(ArrayList<OverviewData> overview_parsed, ArrayList<AssemblyData> index_parsed){
        globalRegroupedData = new ArrayList<UpdateRow>();
        Collections.sort(overview_parsed, new Comparator<OverviewData>() {
            @Override
            public int compare(OverviewData o1, OverviewData o2) {
                return o1.compareToGroupVersion(o2);
            }
        });

        Collections.sort(index_parsed, new Comparator<AssemblyData>() {
            @Override
            public int compare(AssemblyData o1, AssemblyData o2) {
                return o1.compareTo(o2);
            }
        });
        int i = 0, j = 0;
         while(i < index_parsed.size() && j < overview_parsed.size()){
            if(index_parsed.get(i).getGroup().equalsIgnoreCase(overview_parsed.get(j).getGroup())
                    && index_parsed.get(i).getSubgroup().equalsIgnoreCase(overview_parsed.get(j).getSubgroup())){

                globalRegroupedData.add(new UpdateRow(overview_parsed.get(j).getKingdom(),index_parsed.get(i).getGroup()
                        ,index_parsed.get(i).getSubgroup(),index_parsed.get(i).getOrganism(),null,index_parsed.get(i).getGc()
                        ,index_parsed.get(i).getNcs()));
                globalRegroupedData.get(globalRegroupedData.size()-1).setModifyDate(index_parsed.get(i).getModifyDate());
                ++i;
            }else{
                //cherche le kingdom qui correspond à l'index courant
                ++j;
            }
         }

        System.out.println();
    }

    public static ArrayList<UpdateRow> allOrganismNeedingUpdate(ArrayList<OverviewData> userNeeds,ArrayList<Region> neededRegions){
        ArrayList<UpdateRow> ur = new ArrayList<UpdateRow>();
        int beg = 0;
        if(userNeeds.get(0).getKingdom() == null){ // cas ou on veut tester pour tous
            ur = new ArrayList<>(globalRegroupedData);
        }
        else{
           /* Collections.sort(userNeeds, new Comparator<OverviewData>() {
                @Override
                public int compare(OverviewData o1, OverviewData o2) {
                    return o1.compareToGroupVersion(o2);
                }
            });*/
            for(var row : userNeeds){
                if(row.getGroup() == null){
                    //recup toutes les lignes kingdom = row.kingdom
                    for(var gr : globalRegroupedData){
                        if(gr.getKingdom().equalsIgnoreCase(row.getKingdom())){
                            ur.add(gr);
                        }
                    }
                }
                else if(row.getSubgroup() == null){
                    //recup toutes les lignes pour un kingdom et un groupe
                    for(var gr : globalRegroupedData){
                        if(gr.getKingdom().equalsIgnoreCase(row.getKingdom()) && gr.getGroup().equalsIgnoreCase(row.getGroup())){
                            ur.add(gr);
                        }
                    }
                }
                else if (row.getOrganism() == null) {
                    for(var gr : globalRegroupedData){
                        if(gr.getKingdom().equalsIgnoreCase(row.getKingdom()) && gr.getGroup().equalsIgnoreCase(row.getGroup())
                                && gr.getSubGroup().equalsIgnoreCase(row.getSubgroup())){
                            ur.add(gr);
                        }
                    }
                }
                else {
                    for(var gr : globalRegroupedData){
                        if(gr.getKingdom().equalsIgnoreCase(row.getKingdom()) && gr.getGroup().equalsIgnoreCase(row.getGroup())
                                && gr.getSubGroup().equalsIgnoreCase(row.getSubgroup()) && gr.getOrganism().equalsIgnoreCase(row.getOrganism())){
                            ur.add(gr);
                        }
                    }
                }

            }
        }
        ArrayList<UpdateRow> dontNeedModif = new ArrayList<>();
        // ici on à ce que l'utilisateur VEUT, reste à prendre ce qu'on DOIT mettre a jour seulement
        for(var row : ur){
            //on retire si elle n'a pas besion d'être mise à jour
            if(!DataBaseManager.needAnUpdate(row,neededRegions)){
                dontNeedModif.add(row);
            }
        }
        ur.removeAll(dontNeedModif);

        return ur;
    }

    public static void updateFilesTable(UpdateRow ur,Region reg){
        DataBaseManager.insertFilesTable(ur,reg);
    }

    public static void updateFilesTableMutipleRegions(UpdateRow ur, ArrayList<Region> regs){
        DataBaseManager.multipleInsertFilesTable(ur, regs);
    }


    /*
        à partir de la base mise à jour, donne les ids des organismes pour
        lesquels les deux dates (celle dans overview, et la dernière maj locale) diffèrent
        normalement juste une comparaison de deux colonnes, SQL fait tout le taf

        Peut être renvoyer direct (kingdom, group, subgroup, organism) pour aller plus vite ?
     */
    public static ArrayList<Integer> allIdsNeedingUpdate() {
        return null;
        }
    //public static ArrayList<OverviewData> allOrganismNeddingUpdate() {

    /*
        Récupère la hiérarchie depuis la BDD
     */
    public static TreeNode compute_hierarchy() {return null;}

    /*
        Création de toutes les tables nécessaires
     */
    public static void createOrOpenDataBase(String path) {
        DataBaseManager.setDataBase(path);
        //Ouverture de la base ou création si inéxistante
        DataBaseManager.connectionToDb();


        DataBaseManager.createTableDb("CREATE TABLE IF NOT EXISTS FILES (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "kingdom TEXT," +
                "groupe TEXT," +
                "subGroup TEXT," +
                "organism TEXT," +
                "organelle TEXT," +
                "gc TEXT," +
                "type TEXT," +
                "date TEXT," +
                "UNIQUE (kingdom,groupe,subgroup,organism,organelle,type))");

    }

}


