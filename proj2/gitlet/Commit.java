package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  @author Marco
 */
public class Commit implements Serializable {
    /** The message of this Commit. */
    String message;
    /** The timestamp of this Commit. */
    Date timestamp;
    /** The files I tracked in this Commit. */
    Map<String, String> fileToBlobs;
    /** The id of this Commit. */
    String id;
    /** The (1st) parent of the Commit */
    Commit parent;
    /** The (2nd) parent of the Commit */
    Commit parent2;
    /** Whether the commit node is a merge node */
    boolean merged;

    // initial commit
    public Commit() {
        this.merged = false;
        this.timestamp = new Date(0); // the epoch date
        this.id = Utils.sha1(this.timestamp.toString());
        this.parent = null;
        this.parent2 = null;
        this.message = "initial commit";
        this.fileToBlobs = new HashMap<>();
    }

    public Commit(
            String message,
            Set<String> toAdd,
            Set<String> toDelete,
            Commit parent1,
            Commit parent2
    ) {
        this.merged = (parent2 != null);
        this.timestamp = new Date();
        this.id = Utils.sha1(
                this.timestamp.toString(),
                message,
                toAdd.toString(),
                toDelete.toString()
        );
        this.message = message;
        this.fileToBlobs = new HashMap<>();
        this.parent = parent1;
        this.parent2 = parent2;
        for (String s: parent1.fileToBlobs.keySet()) {
            fileToBlobs.put(s, parent1.fileToBlobs.get(s));
        }
        // overwrites parent1's content
        for (String filename: toAdd) {
            File f = join(STAGING_DIR, filename);
            String fileContent = readContentsAsString(f);
            String sha1Hash = sha1(filename, fileContent);
            File blob = join(BLOBS_DIR, sha1Hash);
            writeContents(blob, fileContent);
            this.fileToBlobs.put(filename, sha1Hash);
        }
        for (String filename: toDelete) {
            this.fileToBlobs.remove(filename);
        }
    }

}
