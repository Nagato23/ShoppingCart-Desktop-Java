/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gadgetstore;

/**
 *
 * @author SAM
 */
public class GadgetStore {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SplashScreen ss = new SplashScreen();
        ss.setVisible(true);
        try{
            Thread.sleep(2500);
        }
        catch(InterruptedException ex){
            Thread.currentThread().interrupt();
        }
        ss.setVisible(false);
        HomePage lf = new HomePage();
        lf.setVisible(true);
    }
    
}
