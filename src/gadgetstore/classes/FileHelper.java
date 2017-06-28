/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gadgetstore.classes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Formatter;

/**
 *
 * @author Programming
 */
public class FileHelper {
    
    public static Boolean FileExist(String filename)
    {
        File file = new File(filename);
        return file.exists();
    }
    
    public static Boolean CreateFile(String filename)
    {
        try
        {
            Formatter newFile = new Formatter(filename);
            return true;
        }
        catch (Exception ex)
        {
            System.err.println("You have an error");
            return false;
        }
    }
    
    public static String[] ReadData(String filename)
    {
        try
        {
            String totalLine = "";
            
            BufferedReader br = new BufferedReader(
                    new FileReader(filename));
            
            String currentLine;
            
            while ((currentLine = br.readLine()) != null)
            {
                totalLine += currentLine + "a" ;
            }
            
            br.close();
            
            return totalLine.split("a");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.err.println("Error");
        }
        return null;
    }
    
    public static Boolean WriteData(String filename, String[] textLines, Boolean append)
    {
        try
        {
            BufferedWriter br = new BufferedWriter(
                    new FileWriter(filename, append));
            
            for(String line : textLines)
                br.write(line); br.newLine();
            
            br.close();
            
            return true;
        }
        catch (Exception ex)
        {
            System.err.println("Error");
            return false;
        }
    }
    
    
    
    public static int CountChar(char character, String filename)
    {
        int sum = 0;
        
        try
        {
            BufferedReader br = new BufferedReader(
                    new FileReader(filename));
            
            String currentLine;
            
            while ((currentLine = br.readLine()) != null)
            {
                for (char ch : currentLine.toCharArray())
                {
                    if (ch == character)
                        sum++;
                }
            }
            
            br.close();
        }
        catch (Exception ex)
        {
            System.err.println("Error");
        }
        return sum;
    }
    
    public static Boolean ReplaceLine(String filename, String partString, String textToReplaceWith)
    {
        try
        {
            String[] lines = ReadData(filename);
            String[] newLines;
            newLines = new String[lines.length];
            
            for (int i = 0; i < lines.length; i++)
            {
                if(lines[i].toLowerCase().contains(partString.toLowerCase())) 
                {
                    
                    newLines[i] = textToReplaceWith + "\n";
                }
                else
                    newLines[i] = lines[i] + "\n";
            }
            
            return WriteData(filename, newLines, false);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static String GetData(String filename, String text)
    {
        try
        {
            try (BufferedReader br = new BufferedReader( new FileReader(filename))) {
                String currentLine;
                
                while ((currentLine = br.readLine()) != null)
                {
                    if(currentLine.contains(text)) return currentLine;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "";
    }
}