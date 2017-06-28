/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gadgetstore.classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
            
            String[] brands = {"acerlap", "acertab",
                    "hplap", "hptab",
                    "asuslap", "asustab",
                    "lenovolap", "lenovotab",
                    "delllap", "delltab",
                    "samsunglap", "samsungtab",
                    "sonylap", "sonytab",
                    "tecnotab"
                };
                int[] brandscount = {63, 11,
                    63, 10,
                    10, 10,
                    10, 10,
                    61, 10,
                    13, 51,
                    11, 10,
                    44
                };
            
            while ((currentLine = br.readLine()) != null)
            {
                String[] specs =currentLine.split(";");
            
                int[] counters = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                String imgPath = "";
                String brandType = specs[1].toLowerCase() + specs[0].substring(0, 4).toLowerCase();
                for (int i = 0; i < brands.length; i++) {
                    if (specs[0].equals(brands[i])) {
                        int count = counters[i];
                        imgPath = brands[i].substring(0, brands[i].length() - 3).toLowerCase() + count + ".jpg";
                        counters[i] = (counters[i] + 1) % brandscount[i];
                    }

                }
                
                
                String query = 
                        String.format(
                                "INSERT INTO catalogue(type, brandName, ram, storageSize, screenSize, cost, supplier, picturePath) "
                                        + " VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                                specs[0], specs[1],
                                Double.parseDouble(specs[2]), Double.parseDouble(specs[3]),
                                Double.parseDouble(specs[4]), Double.parseDouble(specs[5]),
                                Double.parseDouble(specs[6]),
                                imgPath
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
    
    public static int CountItems(ResultSet resultSet)
    {
//        int count = 0;
//        try
//        {
//            while(resultSet.next())
//            {
//                count++;
//            }
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//        }
//        System.out.println(count);
//        return count;
        
        if (resultSet == null) {
            return 0;
        }
        try {
            resultSet.last();
            return resultSet.getRow();
        } catch (SQLException exp) {
            exp.printStackTrace();
        } finally {
            try {
                resultSet.beforeFirst();
            } catch (SQLException exp) {
                exp.printStackTrace();
            }
        }
        return 0;
    }
    
    
    public ResultSet ExecuteQuery(String query)
    {
        try
        {
            Connection con = ((Connection) DriverManager.getConnection("jdbc:mysql://localhost/sarpestein_db", "root", ""));
            Statement st = (Statement) con.createStatement();
            
            if(query.toLowerCase().contains("select"))
            {
                ResultSet set = st.executeQuery(query);
                
                if( !set.equals(null) )
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
