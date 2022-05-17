package org.NcbiParser;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
//import org.sqlite.JDBC;

public final class DataBaseManager {
    private static String path_;
    private static Connection connection_;

    private DataBaseManager(){
        path_ = "jdbc:sqlite:/";
    }

    public static void setDataBase(String path){
        path_ = "jdbc:sqlite:/" + path;
    }

    public static void connectionToDb() {
        connection_ = null;
        try{
            connection_ = DriverManager.getConnection(path_);

            if(connection_ != null){
                System.out.println("connected");
            }

        }catch(SQLException eio)
        {
            System.err.println(eio.getMessage());
        }
    }

    public static void createTableDb(String sql)/* throws ClassNotFoundException */{
        //Class.forName("org.sqlite.JDBC");
        Statement st = null;
        try{
            st = connection_.createStatement();
            st.executeUpdate(sql);
            System.out.println("table created");
        }catch (Exception ioe){
            System.out.println(ioe.getMessage());
        }

    }

    /**
     *
     * @param row ligne que l'on veut verifier
     * @param regs regions que l'on considère, on ne traite pas le cas ou il n'y a aucune région
     * @return vrai si la ligne doit être mise à jour pour les region selectionnées
     */
    public static boolean needAnUpdate(UpdateRow row,ArrayList<Region> regs){
        Statement s;
        try{
            String req = "SELECT * FROM FILES " +
                    "WHERE kingdom = \"" + row.getKingdom() + "\" AND groupe = \"" + row.getGroup() + "\" AND " +
                    "subGroup = \"" + row.getSubGroup() + "\" AND organism = \"" + row.getOrganism() + "\";";
            s = connection_.createStatement();
            ResultSet rs = s.executeQuery(req);

            if(!rs.isBeforeFirst()){ // résultat vide donc forcément pas à jour
                return true;
            }

            // fetching result to arrays
            ArrayList<String> types = new ArrayList<>();
            ArrayList<String> dates = new ArrayList<>();
            while (rs.next()) {
                types.add(rs.getString("type"));
                dates.add(rs.getString("date"));
            }

            java.util.Date newParseDate = new SimpleDateFormat("yyyy/MM/dd").parse(row.getReleaseDate());
            java.util.Date prevParseDate;
            boolean isIn;
            //pour toutes les lignes -> regarder si chaque region selectionnée est à jour
            for(var reg : regs){
                if (reg.toString().isEmpty()) continue;
                isIn = false;
                //chercher la colonne correspondant à ce type
                for (int i = 0; i < types.size(); ++i) {
                    String currentReg = types.get(i);
                    if (currentReg.equalsIgnoreCase(reg.toString())) {
                        String parseTexteDate = dates.get(i);
                        prevParseDate = new SimpleDateFormat("yyyy/MM/dd").parse(parseTexteDate);
                        isIn = true;
                        if(prevParseDate.before(newParseDate) && !prevParseDate.equals(newParseDate))
                            return true;
                    }
                }
                if(!isIn)
                    return true;
            }
            //retourne faux car on à trouver aucune région pour laquelle ce n'est pas à jour
            return false;
        }catch(Exception eio){
            System.out.println(eio.getMessage());
            return false;
        }
    }

    public static void closeDb() throws SQLException {
        try
        {
            connection_.close();
        }catch (SQLException ioe){
            System.out.println(ioe.getMessage());
        }
    }




    public static void insertFilesTable(UpdateRow ur, Region reg) {
        PreparedStatement ps;
        try{
            String compiledQuery = "INSERT OR REPLACE INTO FILES (kingdom ,groupe ,subGroup ,organism ,organelle ,gc ,type ,date)" +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
            ps = connection_.prepareStatement(compiledQuery);
            ps.setString(1,ur.getKingdom());
            ps.setString(2,ur.getGroup());
            ps.setString(3, ur.getSubGroup());
            ps.setString(4,ur.getOrganism());
            ps.setString(5,ur.getOrganelle());
            ps.setString(6,ur.getGc());
            ps.setString(7, reg.toString());
            SimpleDateFormat dtf = new SimpleDateFormat("yyyy/MM/dd");
            Calendar calendar = Calendar.getInstance();
            java.util.Date dateObj = calendar.getTime();
            ps.setString(8,dtf.format(dateObj));
            ps.executeUpdate();
        }catch(Exception eio){
            throw new RuntimeException("error inserting a parsed files");
        }
    }

    public static void multipleInsertFilesTable(UpdateRow ur, ArrayList<Region> regs) {
        PreparedStatement ps;
        try{
            String compiledQuery = "INSERT OR REPLACE INTO FILES (kingdom ,groupe ,subGroup ,organism ,organelle ,gc ,type ,date)" +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
            ps = connection_.prepareStatement(compiledQuery);
            for(var reg : regs){
                ps.setString(1,ur.getKingdom());
                ps.setString(2,ur.getGroup());
                ps.setString(3, ur.getSubGroup());
                ps.setString(4,ur.getOrganism());
                ps.setString(5,ur.getOrganelle());
                ps.setString(6,ur.getGc());
                ps.setString(7, reg.toString());
                SimpleDateFormat dtf = new SimpleDateFormat("yyyy/MM/dd");
                Calendar calendar = Calendar.getInstance();
                java.util.Date dateObj = calendar.getTime();
                ps.setString(8,dtf.format(dateObj));
                ps.addBatch();
            }
            ps.executeBatch();
        }catch(Exception eio){
            throw new RuntimeException("error inserting a parsed files");
        }
    }


    public static Connection getConnection_(){
        return connection_;
    }

}
