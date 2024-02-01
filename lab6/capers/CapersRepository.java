package capers;

import java.io.File;
import java.io.IOException;

import static capers.Utils.*;

/** A repository for Capers 
 * @author Marco OsaOmagbon
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab6 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = Utils.join(CWD, ".capers");

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() throws IOException {
        CAPERS_FOLDER.mkdir();
        File dogs_folder = Utils.join(CAPERS_FOLDER, "dogs");
        File story = Utils.join(CAPERS_FOLDER, "story");
        story.createNewFile();
        dogs_folder.mkdir();
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        String current = Utils.readContentsAsString(Utils.join(CAPERS_FOLDER, "story"));
        Utils.writeContents(Utils.join(CAPERS_FOLDER, "story"), current, text + "\n");
        String after = Utils.readContentsAsString(Utils.join(CAPERS_FOLDER, "story"));
        System.out.println(after);
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) throws IOException {
        new Dog(name, breed, age).saveDog();
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) throws IOException {
        File dogFile = Utils.join(Utils.join(CWD, ".capers", "dogs"), name);
        Dog d = Dog.fromFile(name);
        d.haveBirthday();
        Utils.writeObject(dogFile, d);
    }
}
