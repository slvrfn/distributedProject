package logWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter
{
    File saveDirectory;

    public LogWriter(String folderLocation)
    {
        //each test will save its output in a different folder based on the test start time
        String folderName = "Test from "+String.valueOf(System.currentTimeMillis());
        folderLocation += "/" + folderName;
        saveDirectory = new File(folderLocation);
    }

    public void WriteToFile(String message)
    {
        BufferedWriter writer = null;
        File file = new File(saveDirectory,"log "+ System.currentTimeMillis()+".txt");
        try
        {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(message);
        }
        catch (IOException e)
        {
            ERROR("Could not create file");
        }
        finally
        {
            try
            {
                // Close the writer regardless of what happens
                writer.close();
            }
            catch (Exception e)
            {
                ERROR("Error closing buffered reader");
            }
        }
    }

    private void ERROR(String message)
    {
        System.out.println(message);
        System.exit(1);
    }
}
