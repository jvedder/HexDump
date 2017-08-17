package hexDump;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main
{

    private static int BUFFER_SIZE = 16;
    private static String inFilename = "";
    
    private static final byte ASCII_CR = 0x0D;
    private static final byte ASCII_LF = 0x0A;
    private static final byte ASCII_TAB = 0x09;
    private static final byte ASCII_SPACE = 0x20;
    private static final byte ASCII_PERIOD = 0x2E;
    

    public static void main(String[] args) throws IOException
    {
        // parse command line arguments
        if (args.length == 1)
        {
            inFilename = args[0];
            hexDumpFile();
        }
        else if ((args.length == 2) && args[0].equals("-8"))
        {
            BUFFER_SIZE = 8;
            inFilename = args[1];
            hexDumpFile();
        }
        else if ((args.length == 2) && args[0].equals("-i"))
        {
            inFilename = args[1];
            hexInlineFile(false, false);
        }
        else if ((args.length == 2) && args[0].equals("-c"))
        {
            inFilename = args[1];
            hexInlineFile(true, false);
        }
        else if ((args.length == 2) && args[0].equals("-s"))
        {
            inFilename = args[1];
            hexInlineFile(true, true);
        }
        else
        {
            System.err.println( );
            System.err.println("HexDump.jar V1.3");
            System.err.println( );
            System.err.println("  Usage:");
            System.err.println("    HexDump.jar [-8|-i|-c] filename");
            System.err.println("  options:");
            System.err.println("    -8 = list 8 bytes/line instead of 16");
            System.err.println("    -i = inline the hex data");
            System.err.println("    -c = inline the hex data, showing [CR] and [LF]");
            System.err.println("    -s = inline the hex data, showing [CR], [LF], and spaces/tabs");
        }
        return;
    }

    private static void hexDumpFile() throws IOException
    {
        // Show complete file path
        Path inFilePath = Paths.get(inFilename);
        System.out.println();
        System.out.println("File: " + inFilePath.toAbsolutePath());
        System.out.println();

        // open file as a stream of bytes
        FileInputStream in = new FileInputStream(inFilename);

        // output header
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

        // output file contents
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

            // show file size
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

    private static void hexInlineFile(boolean showCrLf, boolean showSpaces) throws IOException
    {
        BUFFER_SIZE = 4 * 1024; // 4K buffer

        // Show complete file path
        Path inFilePath = Paths.get(inFilename);
        System.out.println();
        System.out.println("File: " + inFilePath.toAbsolutePath());
        System.out.println();

        // open file as a stream of bytes
        FileInputStream in = new FileInputStream(inFilename);

        // output file contents
        try
        {
            int bytesRead = 0;
            int totalBytesRead = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = in.read(buffer)) != -1)
            {
                totalBytesRead += bytesRead;
                for (int i = 0; i < bytesRead; i++)
                {
                    byte b = buffer[i];
                    
                    if (b == ASCII_CR) 
                    {
                        if (showCrLf) System.out.print("[CR]");
                    }
                    else if (b == ASCII_LF)
                    {
                        if (showCrLf) System.out.print("[LF]");
                        System.out.println();

                    }
                    else if (b == ASCII_TAB)
                    {
                        if (showSpaces)
                        {
                            System.out.print("[TAB]");
                        }
                        else
                        {
                            System.out.write(b);
                        }
                    }
                    else if (b == ASCII_SPACE)
                    {
                        System.out.write(showSpaces ? ASCII_PERIOD : ASCII_SPACE); 
                    }
                    else if (b == ASCII_PERIOD)
                    {
                        if (showSpaces)
                        {
                           System.out.print("[.]");
                        }
                        else
                        {
                            System.out.write(ASCII_PERIOD);
                        }                    
                    }
                    else if ((b < 0x20) || (b > 0x7e))
                    {
                        System.out.print("[");
                        System.out.print(String.format("%02X", b));
                        System.out.print("]");
                    }
                    else
                    {
                        System.out.write(b);
                    }
                }
            }

            // show file size
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
