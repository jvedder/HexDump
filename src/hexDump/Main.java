package hexDump;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main
{

    private static int BUFFER_SIZE = 16;

    public static void main(String[] args) throws IOException
    {
        String inFilename = "";

        // parse command line arguments 
        if (args.length == 1)
        {
            inFilename = args[0];
        }
        else if ((args.length == 2) && args[0].equals("-8"))
        {
            BUFFER_SIZE = 8;
            inFilename = args[1];
        }
        else if ((args.length == 2) && args[1].equals("-8"))
        {
            BUFFER_SIZE = 8;
            inFilename = args[0];
        }
        else
        {
            System.err.println("Invalid arguments.  Usage:");
            System.err.println("  HexDump.jar [-8] filename");
            System.err.println("options:");
            System.err.println("  -8 = list 8 bytes/line instead of 16");
            return;
        }

        //Show complete file path
        Path inFilePath = Paths.get(inFilename);
        System.out.println();
        System.out.println("File: " + inFilePath.toAbsolutePath());
        System.out.println();
        
        //open file as a stream of bytes
        FileInputStream in = new FileInputStream(inFilename);

        //output header
        for (int row = 0; row < 2; row++)
        {
            if (row == 0)
            {
                System.out.print("    ");
            }
            else
            {
                System.out.print("----");
            }
            System.out.print(": ");
            System.out.print(headerForHex(row));
            System.out.print(" | ");
            System.out.print(headerForAscii(row));
            System.out.println("|");
        }

        //output file contents
        try
        {
            int bytesRead = 0;
            int totalBytesRead = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = in.read(buffer)) != -1)
            {
                System.out.print(String.format("%04X", totalBytesRead));
                System.out.print(": ");
                System.out.print(bufferToHex(buffer, bytesRead));
                System.out.print(" | ");
                System.out.print(bufferToAscii(buffer, bytesRead));
                System.out.println("|");

                totalBytesRead += bytesRead;
            }

            //show file size
            System.out.println();
            System.out.print("Length: ");
            System.out.print(totalBytesRead);
            System.out.print(" (0x");
            System.out.print(String.format("%04X", totalBytesRead));
            System.out.println(") Bytes.");

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (in != null) in.close();
        }
    }

    private static String bufferToHex(byte[] buffer, int size)
    {
        String hex = "";

        for (int i = 0; i < BUFFER_SIZE; i++)
        {
            if (i == 8) hex += " ";

            if (i < size)
            {
                hex += String.format("%02X", buffer[i]);
            }
            else
            {
                hex += "  ";
            }

            hex += " ";
        }

        return hex;
    }

    private static String bufferToAscii(byte[] buffer, int size)
    {
        String ascii = "";

        for (int i = 0; i < BUFFER_SIZE; i++)
        {
            if (i == 8) ascii += "  ";

            if (i < size)
            {
                if ((buffer[i] < 0x20) || (buffer[i] > 0x7E))
                {
                    ascii += ".";
                }
                else
                {
                    ascii += new String(buffer, i, 1, StandardCharsets.US_ASCII);
                }
            }
            else
            {
                ascii += " ";
            }

            ascii += " ";
        }

        return ascii;
    }

    private static String headerForHex(int row)
    {
        String hex = "";

        for (int i = 0; i < BUFFER_SIZE; i++)
        {
            if (i == 8) hex += " ";
            
            if (row == 0)
            {
                hex += String.format("%02X", i);
            }
            else
            {
                hex += "--";
            }
            hex += " ";
        }

        return hex;
    }

    private static String headerForAscii(int row)
    {
        String ascii = "";

        for (int i = 0; i < BUFFER_SIZE; i++)
        {
            if (i == 8) ascii += "  ";

            if (row == 0)
            {
                ascii += String.format("%01X", i);
            }
            else
            {
                ascii += "-";
            }

            ascii += " ";
        }

        return ascii;
    }
}
