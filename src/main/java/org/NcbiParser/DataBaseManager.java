package org.NcbiParser;

import java.sql.*;

public final class DataBaseManager {
    private static String path_;
    private static Connection connection_;

    private DataBaseManager(){
        path_ = "jdbc:sqlite:/";
    }

    public static void setDataBase(String path){
        path_ = "jdbc:sqlite:/" + path;
    }

    public static void connectionToDb() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
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

    public static void createTableDb(String sql) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        Statement st = null;
        try{
            st = connection_.createStatement();
            st.executeUpdate(sql);
            System.out.println("table created");
        }catch (Exception ioe){
            System.out.println(ioe.getMessage());
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

    /**
     * insert ou ignore dans les table generique kingdom
     * @param nomTable nom de la table doit être (kingdom groupe subgroup)
     * @return clé de la ligne
     */
    public static int insertOrIgnoreIdNom(String nomTable,String name) throws SQLException {
        String req = "INSERT OR IGNORE INTO " + nomTable + " (nom) VALUES(?);";
        PreparedStatement ps = null;

        try{
            ps = connection_.prepareStatement(req);
            ps.setString(1,name);
            ps.executeUpdate();

            Statement s = connection_.createStatement();
            ResultSet rs = s.executeQuery("SELECT id FROM " + nomTable + " WHERE nom = \"" + name + "\" ;");
            return Integer.parseInt(rs.getString("id"));
        }catch (Exception eio){
            System.out.println(eio.getMessage());
            return -1;
        }
    }

    public static void insertOrIgnoreOrganism(int kinCle,int grouCle,int subCle,String name){
        String req = "INSERT OR IGNORE INTO ORGANISM (kingdom_id, group_id, subgroup_id, nom) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = null;

        try{
            ps = connection_.prepareStatement(req);
            ps.setInt(1,kinCle);
            ps.setInt(2,grouCle);
            ps.setInt(3,subCle);
            ps.setString(4,name);
            ps.executeUpdate();
        }catch (Exception eio){
            System.out.println(eio.getMessage());
        }
    }

    public static Connection getConnection_(){
        return connection_;
    }
}
