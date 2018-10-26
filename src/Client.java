
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Graham
 */
public class Client {

    public static String filePath(){
        return "C:\\Users\\Graham\\Documents\\NetBeansProjects\\Lab109\\src\\data.txt";
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File myFile;
        Scanner scan = null;
        LinkedQueue queueFile = new LinkedQueue();
        try{
            myFile = new File(filePath());
            scan = new Scanner(myFile);
        }
        catch(FileNotFoundException e){
            
        }
        
        while(scan.hasNextLine()){
            queueFile.enqueue(scan.nextLine());
        }

        
    }
    
}
