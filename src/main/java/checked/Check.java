package checked;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Check
{
    public static void main(String[] args)
    {
        try
        {
            FileReader reader = new FileReader("aa");
            int[] array = new int[27];
            while(true)
            {
                char charReaded = (char)reader.read();
                System.out.println(charReaded);
            }

        } catch (FileNotFoundException e)
        {
            System.out.println("file not exist");
        } catch (IOException e) {
            System.out.println("I/O exceprion");
        }

    }
}
