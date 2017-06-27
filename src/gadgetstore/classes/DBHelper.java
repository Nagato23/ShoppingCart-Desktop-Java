/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sarpestein;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 *
 * @author Programming
 */
public class DBHelper {
    public void AddItemsToRlationalDB()
    {
        String filename = "catalogue.txt";
        
        try
        {
            BufferedReader br = new BufferedReader(
                    new FileReader(filename));
            
            String currentLine;
            
            String[] types = {"Laptop", "Tablet"};
            
            int count = 0;
            //Add the types
            for(String type : types)
            {
                this.ExecuteInsert(String.format("INSERT INTO type(id, name) VALUES ('{0}' , '{1}')", ++count, type));
            }
            
            while ((currentLine = br.readLine()) != null)
            {
                int brandId, supplierId, typeId;
                
                String[] values = currentLine.trim().split(";");
                
                //gets device id
                typeId = (values[0].toLowerCase().equals("laptop")) ? 1 : 2;
                
                //checks if brand exists, and add to db if not
                brandId = this.CheckIfExist(values[1], "brand", "name");
                
                //check if supplier exits, and add to db if not
                supplierId = this.CheckIfExist(values[6], "supplier", "name");
                
                //add item to catalogue table
                String query = String.format(
                        "INSERT INTO catalogue(typeId, brandId, ram, storageSpace, screenSize, cost, supplierId, model, dateAdded, dateUpdated) "
                                + "VALUES ('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}', '{7}', '{8}', '{9}')",
                        typeId, brandId, 
                        Double.parseDouble(values[2]), 
                        Double.parseDouble(values[3]),
                        Double.parseDouble(values[4]),
                        Double.parseDouble(values[5]),
                        supplierId,
                        Date.class.toString(),
                        Date.class.toString());
                
                this.ExecuteInsert(query);
            }
            
            br.close();
        }
        catch (Exception ex)
        {
            System.err.println("Error");
        }
    }
    
    
    public void AddItemsToDB()
    {
        String filename = "catalogue.txt";
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            
            String currentLine;
            
            while ((currentLine = br.readLine()) != null)
            {
                String[] specs =currentLine.split(";");
                
                String query = 
                        String.format(
                                "INSERT INTO catalogue(type, brandName, ram, storageSize, screenSize, cost, supplier) "
                                        + " VALUES('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}')",
                                specs[0], specs[1],
                                Double.parseDouble(specs[2]), Double.parseDouble(specs[3]),
                                Double.parseDouble(specs[4]), Double.parseDouble(specs[5]),
                                Double.parseDouble(specs[6])
                        );
                
                this.ExecuteInsert(query);
            }
            
            br.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    private void ExecuteInsert(String query)
    {
        try
        {
            Connection con = ((Connection) DriverManager.getConnection("jdbc:mysql://localhost/sarpestein_db", "root", ""));
            Statement st = (Statement) con.createStatement();
            
            if(query.toLowerCase().contains("insert"))
            {
                st.executeUpdate(query);
            }
            
            con.close();
        }
        catch(Exception ex)
        {
           ex.printStackTrace();
        }
    }
    
    public static int CountItems(ResultSet set)
    {
        int rows = 0;
        try
        {
            if (set.last()) 
            {     
                rows = set.getRow();
            }

            set.beforeFirst();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
        return rows;
    }
    
    
    public ResultSet ExecuteQuery(String query)
    {
        try
        {
            Connection con = ((Connection) DriverManager.getConnection("jdbc:mysql://localhost/sarpestein_db", "root", ""));
            Statement st = (Statement) con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
            if(query.toLowerCase().contains("select"))
            {
                ResultSet set = st.executeQuery(query);
                
                if( !(set.equals(null) || set.next()) )
                {
                    return set;
                }
            }
            con.close();
        }
        catch(Exception ex)
        {
           ex.printStackTrace();
        }
        
        return null;
    }
    
    private int CheckIfExist(String value, String tableName, String paramName)
    {
        //check if exist
        String query = String.format("SELECT * FROM {0} WHERE {1} = {2}", tableName, paramName, value);
        ResultSet set = this.ExecuteQuery(query);
        
        int id = -1;
        
        if(set.equals(null))
        {
            //query for id
            query = String.format("INSERT INTO {0}({1}) VALUES({2})", tableName, paramName, value);
            this.ExecuteInsert(query);
            
            
            //return Id
            query = String.format("SELECT id FROM {0} WHERE {1} = {2}", tableName, paramName, value);
            set = this.ExecuteQuery(query);
        }
        
        try
        {
            while(set.next())
            {
                id = Integer.parseInt(set.getString("id").trim());
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
        return id;
    }
}
