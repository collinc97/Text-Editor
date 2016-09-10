package editor;
import java.io.*;
import javafx.scene.Group;
import javafx.scene.text.Text;


/** A TESTING FILE FOR TESTING FILES!
 * 1. first command line argument passed to Editor must be the name of a file to edit
 * 2. If that file already exists, Editor should open it and display its contents
 * 3. otherwise, Editor should begin with an empty file
 * 4. pressing shortcut+s should save the text in your editor to the given file, replacing any existing text in the file.
 * 5. if you encounter an exception when opening or writing to the file~
 * ~your editor should exit and print an error message that includes the filename
 */
public class OpenSave {
    public String inputFilename;
    public String debugInput;
    public File inputFile;
    public FileReader reader;
    public BufferedReader bufferedReader;
    public LinkedListText display;
    public Group root;
    public Text currText;

    public OpenSave(String inputFilename, String debugInput, LinkedListText display, Group root) {
        this.inputFilename = inputFilename;
        this.debugInput = debugInput;
        this.display = display;
        this.root = root;
        if (debugInput.equals("debug")) {
            System.out.println("FACILITERATING DEBUGAMAZINGING");
        }
    }
    public void save() {
        try {

            FileWriter writer = new FileWriter(inputFilename);

            // Keep reading from the file input read() returns -1, which means the end of the file
            // was reached.
            int i = 0;
            display.setFirstRender();
            while (i < display.size()) {
                // The integer read can be cast to a char, because we're assuming ASCII.
                char charRead = display.getRenderItem().getText().charAt(0);
                writer.write(charRead);
                display.nextRender();
                i++;
            }
            System.out.println("Successfully saved file");
            writer.close();
        } catch (IOException ioException) {
            System.out.println("Error when copying; exception was: " + ioException);
        }
    }

    public void open() {
        /*if (args.length == 0) {
            System.out.println("ERROR: No filename provided. exiting...");
            System.exit(1);
        } else */
        /** Editor should accept a second optional command line argument that controls the output of your program as follows:
         If the second command line argument is blank, your program should not print any output.
         If the second command line argument is "debug", you can print any output you like to facilitate debugging. */

        try {
            inputFile = new File(inputFilename);

            if (!inputFile.exists()) {
                inputFile.createNewFile();
                //if the input file doesn't exist create a blank file.
            }
            reader = new FileReader(inputFile);
            //yay, now we can read the file

            bufferedReader = new BufferedReader(reader);
            //yay, now we can read the file

            int intRead = -1;
            // Keep reading from the file input read() returns -1, which means the end of the file
            // was reached.

            while ((intRead = bufferedReader.read()) != -1) {
                // The integer read can be cast to a char, because we're assuming ASCII.
                char charRead = (char)intRead;
                String stringRead = Character.toString(charRead);
                if (stringRead.equals("\n") || stringRead.equals("\r\n")) {
                    stringRead = "\r";
                }
                currText = new Text(0, 0, stringRead);
                display.insert(currText);
                root.getChildren().add(currText);
            }
            bufferedReader.close();

        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found! Exception was: " + fileNotFoundException);
        } catch (IOException ioException) {
            System.out.println("Error when copying; exception was: " + ioException);
        }


    }
}