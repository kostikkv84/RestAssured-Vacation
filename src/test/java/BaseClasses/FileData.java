package BaseClasses;

import java.io.*;
import java.util.Scanner;

public class FileData {
    public void saveID(String id){
        try(FileWriter writer = new FileWriter("newId.txt")) {
            writer.write(id);
        }
        catch(IOException e){
            // Handle the exception
        }
    }
    public String openAndRead() throws IOException {
        File file = new File("newId.txt");
        Scanner scanner = new Scanner(file);
        String id = scanner.nextLine();
        return id;
    }
}