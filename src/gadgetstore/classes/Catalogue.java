/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sarpestein;

import java.sql.ResultSet;

/**
 *
 * @author Programming
 */

public class Catalogue {
    public ResultSet GetAllItems()
    {
        DBHelper  db = new DBHelper();
        String query = "SELECT * FROM catalogue";
        
        return db.ExecuteQuery(query);
    }
    
    
    public String[] GetMatchedItemsId(String deviceType, String brandName, double ramSize, double storageSize,
            double screenSize, double minUserCost, double maxUserCost)
    {
        
        double maxPriceInCatalogue = this.GetMinCostInDB();
        double minPriceInCatalogue = this.GetMaxCostInDB();
        
        HEOM  matcher = 
                new HEOM(deviceType, brandName, ramSize, storageSize, screenSize, minUserCost, maxUserCost, maxPriceInCatalogue, minPriceInCatalogue);
        
        
        HEOMData[] output = matcher.PerformMatching();
        String[] ids = new String[output.length];
        
        for(int i = 0; i < output.length; i++)
        {
            ids[i] = output[i].id;
        }
        
        return ids;
    }
    
    
    private double GetMinCostInDB()
    {
        DBHelper db = new DBHelper();
        String query = "";
        
        double minPriceInCatalogue = 0.0;
        
        try
        {
            query = "SELECT cost FROM catalogue ORDER BY cost DESC LIMIT 1";
            
            ResultSet set = db.ExecuteQuery(query);
            
            while(set.next())
            {
                //System.out.println(set.getString("cost"));
                minPriceInCatalogue = Double.parseDouble(set.getString("cost"));
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
        return minPriceInCatalogue;
    }
    
    
    private double GetMaxCostInDB()
    {
        String query = "SELECT cost FROM catalogue ORDER BY cost ASC LIMIT 1";
        
        DBHelper db = new DBHelper();
        ResultSet set = db.ExecuteQuery(query);
        
        double maxPriceInCatalogue = 0.0;
        
        try
        {
            while(set.next())
            {
                maxPriceInCatalogue = Double.parseDouble(set.getString("cost"));
                //System.out.println(set.getString("cost"));
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
        return maxPriceInCatalogue;
    }
}

class HEOM
{
    private final String deviceType;
    private final String brandName;
    private final double ramSize;
    private final double storageSize;
    private final double screenSize;
    private final double minUserCost;
    private final double maxUserCost;
    private final double maxPriceInCatalogue;
    private final double minPriceInCatalogue;
    
    HEOM(String deviceType, String brandName, double ramSize, double storageSize,
            double screenSize, double minUserCost, double maxUserCost, double maxPriceInCatalogue, double minPriceInCatalogue)
    {
        this.deviceType = deviceType;
        this.brandName = brandName;
        this.ramSize = ramSize;
        this.storageSize = storageSize;
        this.screenSize = screenSize;
        this.minUserCost = minUserCost;
        this.maxUserCost = maxUserCost;
        this.maxPriceInCatalogue = maxPriceInCatalogue;
        this.minPriceInCatalogue = minPriceInCatalogue;
    }
    
    public HEOMData[] PerformMatching()
    {
        ResultSet set = this.GetInitialMatch();
        HEOMData[] values = new HEOMData[1000]; // [id] [heom_score] 
        HEOMData[] finalValues = null;
        try
        {
            int count = 0;
            
            while(set.next())
            {
                double score = 0.0;
                score += (deviceType.equals("")) ? 1.0 : overlap(deviceType, set.getString("type"));
                score += (brandName.equals("")) ? 1.0 : overlap(brandName, set.getString("brandName"));
                score += (ramSize == 0.0) ? 1.0 : overlap(String.valueOf(ramSize), set.getString("ram"));
                score += (storageSize == 0.0) ? 1.0 : overlap(String.valueOf(storageSize), set.getString("storageSize"));
                score += (screenSize == 0.0) ? 1.0 : overlap(String.valueOf(screenSize), set.getString("screenSize"));
                
                if((minUserCost == 0.0 ) && (maxUserCost == 0.0))
                    score += 1.0;
                else
                    score += rnDiff(minUserCost, maxUserCost, maxPriceInCatalogue, minPriceInCatalogue);
                
                HEOMData data = new HEOMData();
                data.id = set.getString("id");
                data.score = score;
                        
                values[count] = data;
                count++;
            }
            
            finalValues = new HEOMData[count];
            for(int i = 0; i < finalValues.length; i++)
            {
                finalValues[i] = values[i];
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
        return this.Sort(finalValues);
    }
    
    private HEOMData[] Sort(HEOMData[] output)
    {
        String text;
        
        for(int i = 0; i < output.length; i++)
        {
            
        }
        for (int i = 0; i < output.length; i++)
        {
            for (int j = 0; j < output.length; j++)
            {
                if( output[j].score > output[i].score)
                {
                    HEOMData temp = output[i];
                    output[i] = output[j];
                    output[j]= temp;
                }
            }
        }
        
        return output;
    }
    
    private int overlap(String x, String y)
    {
        if (x.equals(y))
            return 0;
        
        return 1;
    }
    
    private double rnDiff(double  x, double y, double max, double min)
    {
        return ( Math.abs(x - y) / (max- min) );
    }

    private ResultSet GetInitialMatch()
    {
        String[] queriesParts = new String[6];
        String[] finalQueriesParts = null;
        
        int count = 0;
        
        if (!deviceType.equals(""))
            queriesParts[count++] = String.format("type='%s'", deviceType);
        
        if (!brandName.equals(""))
        {
            queriesParts[count++] = String.format("brandName='%s'", brandName);
            
        }
        if (!(ramSize == 0.0))
            queriesParts[count++] = String.format("ram='%s'", ramSize);
        
        if (!(storageSize == 0.0))
            queriesParts[count++] = String.format("storageSize='%s'", storageSize);
        
        if (!(screenSize == 0.0))
            queriesParts[count++] = String.format("screenSize='%s'", screenSize);
        
        if (minUserCost != maxUserCost)
            queriesParts[count++] = String.format("(cost > %s  AND cost < %s)", minUserCost, maxUserCost);
        
        finalQueriesParts = new String[count];
        
        //transfer items
        for (int i = 0; i < finalQueriesParts.length; i++)
        {
            finalQueriesParts[i] = queriesParts[i];
        }
        
        
        String query = this.GenerateQuery(finalQueriesParts);
        
        DBHelper db = new DBHelper();
        
        return db.ExecuteQuery(query);
    }
    
    private String GenerateQuery(String[] queriesParts)
    {
        String query = "SELECT * FROM catalogue WHERE (";
        
        for (int i = 0; i < queriesParts.length; i++)
        {
            //System.out.println(queriesParts[i]);
            if (!queriesParts[i].equals(""))
            {
                if(i != (queriesParts.length - 1))
                    query += queriesParts[i] + " OR ";
                else query += queriesParts[i] + ") ;";
            }
            
        }
        
        //System.out.println(query);
        
        return query;
    }
}