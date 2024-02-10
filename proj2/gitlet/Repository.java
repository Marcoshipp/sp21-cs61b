package gitlet;

import java.io.File;
import java.util.HashSet;
import java.util.Formatter;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  @author Marco
 */
public class Repository {

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The staging area directory. */
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    /** The commits' directory. */
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    /** The blobs' directory. */
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    /** The branches' directory. */
    public static final File BRANCH_DIR = join(GITLET_DIR, "branches");

    /** Add Tracker's directory */
    public static final File ADD_TRACKER_DIR = join(STAGING_DIR, "ADD");
    /** Del Tracker's directory */
    public static final File DEL_TRACKER_DIR = join(STAGING_DIR, "DEL");
    /** The HEAD (Pointer to the active branch) */
    public static final File HEAD = join(GITLET_DIR, "HEAD");

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        initDirs();
        // creates the Master branch
        File master = join(BRANCH_DIR, "master");
        // send an initial commit
        Commit initCommit = new Commit();
        File initCommitFile = join(COMMITS_DIR, initCommit.id);
        // set the HEAD pointer and the BRANCH pointer
        writeContents(master, initCommit.id);
        writeContents(HEAD, "master");
        // save the commit
        writeObject(initCommitFile, initCommit);
    }

    private static void initDirs() {
        GITLET_DIR.mkdir();
        STAGING_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        BRANCH_DIR.mkdir();
        writeObject(ADD_TRACKER_DIR, new HashSet<>());
        writeObject(DEL_TRACKER_DIR, new HashSet<>());
    }

    private static boolean isInitialized() {
        return GITLET_DIR.exists();
    }

    private static void addToAddTracker(String filename) {
        HashSet<String> addTracker = readObject(ADD_TRACKER_DIR, HashSet.class);
        addTracker.add(filename);
        writeObject(ADD_TRACKER_DIR, addTracker);
    }

    private static void addToDelTracker(String filename) {
        HashSet<String> delTracker = readObject(DEL_TRACKER_DIR, HashSet.class);
        delTracker.add(filename);
        writeObject(DEL_TRACKER_DIR, delTracker);
    }

    private static boolean inCommit(Commit curHead, File f) {
        return curHead.fileToBlobs.containsKey(f.getName());
    }

    /**
     * Gets the newest commit in the active branch
     */
    static Commit getCurHead() {
        File activeBranch = join(BRANCH_DIR, readContentsAsString(HEAD));
        String parentCommitID = readContentsAsString(activeBranch);
        return readObject(join(COMMITS_DIR, parentCommitID), Commit.class);
    }

    private static boolean sameInHeadCommit(File f) {
        Commit head = getCurHead();
        if (!inCommit(head, f)) {
            return false;
        }
        String filename = f.getName();
        String hashVal = sha1(filename, readContents(f));
        return head.fileToBlobs.get(filename).equals(hashVal);
    }

    public static void add(String filename) {
        if (!isInitialized()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        File f = join(CWD, filename);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        File copy = join(STAGING_DIR, filename);
        if (sameInHeadCommit(f)) {
            if (copy.exists()) {
                copy.delete();
            }
            return;
        }
        addToAddTracker(filename);
        writeContents(copy, readContents(f));
    }

    private static void clearStaged() {
        for (File f: STAGING_DIR.listFiles()) {
            f.delete();
        }
        writeObject(ADD_TRACKER_DIR, new HashSet<>());
        writeObject(DEL_TRACKER_DIR, new HashSet<>());
    }

    public static void commit(String message) {
        if (!isInitialized()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        Commit curHead = getCurHead();
        HashSet<String> addTrackerSet = readObject(ADD_TRACKER_DIR, HashSet.class);
        HashSet<String> delTrackerSet = readObject(DEL_TRACKER_DIR, HashSet.class);
        if (addTrackerSet.isEmpty() && delTrackerSet.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        Commit commit = new Commit(message, addTrackerSet, delTrackerSet, curHead);
        File newHead = join(COMMITS_DIR, commit.id);
        // advances the active branch pointers
        writeContents(join(BRANCH_DIR, readContentsAsString(HEAD)), commit.id);
        writeObject(newHead, commit);
        clearStaged();
    }

    /**
     * Checks if the file is tracked in the current commit.
     */
    private static boolean isTracked(String filename) {
        Commit curHead = getCurHead();
        return curHead.fileToBlobs.containsKey(filename);
    }

    /**
     * Checks if the file is tracked in the current commit.
     */
    private static boolean isStaged(String filename) {
        HashSet<String> addTracker = readObject(ADD_TRACKER_DIR, HashSet.class);
        return addTracker.contains(filename);
    }

    public static void rm(String filename) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        File f = join(CWD, filename);
        if (isStaged(filename)) {
            HashSet<String> addTracker = readObject(ADD_TRACKER_DIR, HashSet.class);
            addTracker.remove(filename);
            writeObject(ADD_TRACKER_DIR, addTracker);
            join(STAGING_DIR, filename).delete();
        } else if (isTracked(filename)) {
            addToDelTracker(filename);
            f.delete();
        } else {
            System.out.println("No reason to remove the file.");
        }
    }

    public static void log() {
        if (!isInitialized()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        // TODO: Handles merge
        Commit c = getCurHead();
        for (; c != null; c = c.parent) {
            System.out.println("===");
            System.out.printf("commit %s\n", c.id);
            Formatter formatter = new Formatter();
            formatter.format("Date: %1$ta %1$tb %1$td %1$tT %1$tY %1$tz%n", c.timestamp);
            System.out.print(formatter);
            System.out.println(c.message);
            System.out.println();
        }
    }

    public static void globalLog() {
        if (!isInitialized()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        for (String filename: plainFilenamesIn(COMMITS_DIR)) {
            if (filename.equals("HEAD")) {
                continue;
            }
            Commit c = readObject(join(COMMITS_DIR, filename), Commit.class);
            System.out.println("===");
            System.out.printf("commit %s\n", c.id);
            Formatter formatter = new Formatter();
            formatter.format("Date: %1$ta %1$tb %1$td %1$tT %1$tY %1$tz%n", c.timestamp);
            System.out.print(formatter);
            System.out.println(c.message);
            System.out.println();
        }
    }

    public static void find(String message) {
        if (!isInitialized()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        boolean found = false;
        for (String filename: plainFilenamesIn(COMMITS_DIR)) {
            Commit c = readObject(join(COMMITS_DIR, filename), Commit.class);
            if (c.message.contains(message)) {
                System.out.println(c.id);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        if (!isInitialized()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        System.out.println("=== Branches ===");
        for (String branchName: plainFilenamesIn(BRANCH_DIR)) {
            if (branchName.equals(readContentsAsString(HEAD))) {
                System.out.print("*");
            }
            System.out.println(branchName);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        HashSet<String> added = readObject(ADD_TRACKER_DIR, HashSet.class);
        for (String filename: added) {
            System.out.println(filename);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        HashSet<String> removed = readObject(DEL_TRACKER_DIR, HashSet.class);
        for (String filename: removed) {
            System.out.println(filename);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public static void branch(String branchName) {
        if (!isInitialized()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        File newBranch = join(BRANCH_DIR, branchName);
        if (newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        writeContents(newBranch, getCurHead().id);
    }

    public static void checkout(String filename, String commitID) {
        File commitFile = join(COMMITS_DIR, commitID);
        Commit commit = readObject(commitFile, Commit.class);
        if (!inCommit(commit, join(CWD, filename))) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        String blobName = commit.fileToBlobs.get(filename);
        writeContents(join(CWD, filename), readContentsAsString(join(BLOBS_DIR, blobName)));
    }

    public static void checkout(String branchName) {
        File newBranch = join(BRANCH_DIR, branchName);
        if (!newBranch.exists()) {
            System.out.println("No such branch exists.");
            return;
        }
        if (branchName.equals(readContentsAsString(HEAD))) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        String commitID = readContentsAsString(newBranch);
        Commit c = readObject(join(COMMITS_DIR, commitID), Commit.class);
        // check if any file is staged
        for (String filename: c.fileToBlobs.keySet()) {
            if (isStaged(filename)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }
        for (String filename: c.fileToBlobs.keySet()) {
            checkout(filename, commitID);
        }
        for (String filename: getCurHead().fileToBlobs.keySet()) {
            if (!c.fileToBlobs.containsKey(filename)) {
                join(CWD, filename).delete();
            }
        }
        // make head point to the given branch
        writeContents(HEAD, branchName);
        clearStaged();
    }
}
