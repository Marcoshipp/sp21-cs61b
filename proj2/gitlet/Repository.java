package gitlet;

import java.io.File;
import java.util.HashSet;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.Queue;

import static gitlet.Utils.*;
import static gitlet.Utils.readContentsAsString;

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
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
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

    private static boolean inCommit(Commit c, String filename) {
        return c.fileToBlobs.containsKey(filename);
    }

    private static void error(String message) {
        System.out.println(message);
        System.exit(0);
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
        if (!inCommit(head, f.getName())) {
            return false;
        }
        String filename = f.getName();
        String hashVal = sha1(filename, readContentsAsString(f));
        return head.fileToBlobs.get(filename).equals(hashVal);
    }

    public static void add(File f) {
        if (!f.exists()) {
            error("File does not exist.");
        }
        HashSet<String> delTracker = readObject(DEL_TRACKER_DIR, HashSet.class);
        File copy = join(STAGING_DIR, f.getName());
        if (sameInHeadCommit(f)) {
            if (copy.exists()) {
                copy.delete();
            }
            if (delTracker.contains(f.getName())) {
                delTracker.remove(f.getName());
                writeObject(DEL_TRACKER_DIR, delTracker);
            }
            return;
        }
        addToAddTracker(f.getName());
        writeContents(copy, readContentsAsString(f));
    }

    private static void clearStaged() {
        for (File f: STAGING_DIR.listFiles()) {
            f.delete();
        }
        writeObject(ADD_TRACKER_DIR, new HashSet<>());
        writeObject(DEL_TRACKER_DIR, new HashSet<>());
    }

    public static void commit(String message, Commit other) {
        Commit curHead = getCurHead();
        HashSet<String> addTrackerSet = readObject(ADD_TRACKER_DIR, HashSet.class);
        HashSet<String> delTrackerSet = readObject(DEL_TRACKER_DIR, HashSet.class);
        if (addTrackerSet.isEmpty() && delTrackerSet.isEmpty()) {
            error("No changes added to the commit.");
        }
        Commit commit = new Commit(message, addTrackerSet, delTrackerSet, curHead, other);
        File newHead = join(COMMITS_DIR, commit.id);
        // advances the active branch pointers
        writeContents(join(BRANCH_DIR, readContentsAsString(HEAD)), commit.id);
        writeObject(newHead, commit);
        clearStaged();
    }

    /**
     * Checks if the file is tracked in the current commit.
     */
    private static boolean isStaged(String filename) {
        HashSet<String> addTracker = readObject(ADD_TRACKER_DIR, HashSet.class);
        return addTracker.contains(filename);
    }

    public static void rm(File f) {
        String filename = f.getName();
        if (isStaged(filename)) {
            HashSet<String> addTracker = readObject(ADD_TRACKER_DIR, HashSet.class);
            addTracker.remove(filename);
            writeObject(ADD_TRACKER_DIR, addTracker);
            join(STAGING_DIR, filename).delete();
        } else if (inCommit(getCurHead(), filename)) {
            addToDelTracker(filename);
            f.delete();
        } else {
            System.out.println("No reason to remove the file.");
        }
    }

    private static void printCommit(Commit c) {
        System.out.println("===");
        System.out.printf("commit %s\n", c.id);
        if (c.merged) {
            System.out.printf("Merge: %s %s\n",
                    c.parent.id.substring(0, 7),
                    c.parent2.id.substring(0, 7));
        }
        Formatter formatter = new Formatter();
        formatter.format("Date: %1$ta %1$tb %1$td %1$tT %1$tY %1$tz%n", c.timestamp);
        System.out.print(formatter);
        System.out.println(c.message);
        System.out.println();
    }

    public static void log() {
        Commit c = getCurHead();
        for (; c != null; c = c.parent) {
            printCommit(c);
        }
    }

    public static void globalLog() {
        for (File f: COMMITS_DIR.listFiles()) {
            Commit c = readObject(f, Commit.class);
            printCommit(c);
        }
    }

    public static void find(String message) {
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
        File newBranch = join(BRANCH_DIR, branchName);
        if (newBranch.exists()) {
            error("A branch with that name already exists.");
        }
        writeContents(newBranch, getCurHead().id);
    }

    public static void checkout(String filename, String commitID) {
        File commitFile = join(COMMITS_DIR, commitID);
        if (!commitFile.exists()) {
            error("No commit with that id exists.");
        }
        Commit commit = readObject(commitFile, Commit.class);
        if (!inCommit(commit, filename)) {
            error("File does not exist in that commit.");
        }
        String blobName = commit.fileToBlobs.get(filename);
        writeContents(join(CWD, filename),
                readContentsAsString(join(BLOBS_DIR, blobName)));
    }

    private static void checkoutHelper(String commitID) {
        Commit c = readObject(join(COMMITS_DIR, commitID), Commit.class);
        for (String filename: c.fileToBlobs.keySet()) {
            File f = join(CWD, filename);
            if (f.exists()
                && !sameInHeadCommit(f)
                && !isStaged(filename)) {
                error("There is an untracked file in the way;"
                       + " delete it, or add and commit it first.");
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
    }

    public static void checkout(String branchName) {
        File newBranch = join(BRANCH_DIR, branchName);
        if (!newBranch.exists()) {
            error("No such branch exists.");
        }
        if (branchName.equals(readContentsAsString(HEAD))) {
            error("No need to checkout the current branch.");
        }
        String commitID = readContentsAsString(newBranch);
        checkoutHelper(commitID);
        // make head point to the given branch
        writeContents(HEAD, branchName);
        clearStaged();
    }

    public static void rmBranch(String branchName) {
        File branchFile = join(BRANCH_DIR, branchName);
        if (!branchFile.exists()) {
            error("A branch with that name does not exist.");
        }
        if (branchName.equals(readContentsAsString(HEAD))) {
            error("Cannot remove the current branch.");
        }
        branchFile.delete();
    }

    public static void reset(String commitID) {
        File commit = join(COMMITS_DIR, commitID);
        if (!commit.exists()) {
            error("No commit with that id exists.");
        }
        checkoutHelper(commitID);
        writeContents(join(COMMITS_DIR, readContentsAsString(HEAD)), commitID);
    }

    private static Commit findSplit(Commit head, Commit other) {
        HashSet<String> set = new HashSet<>();
        Queue<Commit> fringe = new LinkedList<>();
        Commit splitPoint = null;
        fringe.add(head);
        while (!fringe.isEmpty()) {
            Commit c = fringe.remove();
            set.add(c.id);
            // if c is not null
            if (c.parent != null) {
                fringe.add(c.parent);
            }
            // if is merge Node
            if (c.merged) {
                fringe.add(c.parent2);
            }
        }
        fringe.add(other);
        while (!fringe.isEmpty()) {
            Commit c = fringe.remove();
            if (set.contains(c.id)) {
                splitPoint = other;
                break;
            }
            // if c is not null
            if (c.parent != null) {
                fringe.add(c.parent);
            }
            // if is merge Node
            if (c.merged) {
                fringe.add(c.parent2);
            }
        }
        return splitPoint;
    }

    private static HashSet collectFiles(Commit head, Commit other, Commit split) {
        HashSet<String> files = new HashSet<>();
        for (String name: head.fileToBlobs.keySet()) {
            files.add(name);
        }
        for (String name: other.fileToBlobs.keySet()) {
            files.add(name);
        }
        for (String name: split.fileToBlobs.keySet()) {
            files.add(name);
        }
        return files;
    }

    private static void handleConflict(String filename, Commit current, Commit given) {
        StringBuilder content = new StringBuilder();
        content.append("<<<<<<< HEAD\n");
        content.append(readContentsAsString(join(BLOBS_DIR, current.fileToBlobs.get(filename))));
        content.append("=======\n");
        content.append(readContentsAsString(join(BLOBS_DIR, given.fileToBlobs.get(filename))));
        content.append(">>>>>>>\n");
        File f = join(CWD, filename);
        writeContents(f, content);
        add(f);
    }

    public static void merge(String otherBranchName) {
        if (!join(BRANCH_DIR, otherBranchName).exists()) {
            error("A branch with that name does not exist");
        }
        if (otherBranchName.equals(readContentsAsString(HEAD))) {
            error("Cannot merge a branch with itself.");
        }
        Commit head = readObject(join(BRANCH_DIR, readContentsAsString(HEAD)), Commit.class);
        Commit other = readObject(join(BRANCH_DIR, otherBranchName), Commit.class);
        Commit split = findSplit(head, other);
        if (split.id.equals(other.id)) {
            error("Given branch is an ancestor of the current branch.");
        }
        if (split.id.equals(head.id)) {
            checkout(otherBranchName);
            error("Current branch fast-forwarded.");
        }
        HashSet<String> files = collectFiles(head, other, split);
        HashSet<String> addTracker = readObject(ADD_TRACKER_DIR, HashSet.class);
        HashSet<String> delTracker = readObject(DEL_TRACKER_DIR, HashSet.class);
        if (!addTracker.isEmpty() || !delTracker.isEmpty()) {
            error("You have uncommitted changes.");
        }
        for (String filename: head.fileToBlobs.keySet()) {
            File f = join(CWD, filename);
            if (f.exists()
                && !sameInHeadCommit(f)
                && !isStaged(filename)) {
                error("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
            }
        }
        boolean conflict = false;
        for (String filename: files) {
            boolean inSplit = inCommit(split, filename);
            boolean inOther = inCommit(other, filename);
            boolean inHead = inCommit(head, filename);
            String splitBlob = inSplit ? split.fileToBlobs.get(filename) : "";
            String headBlob = inHead ? head.fileToBlobs.get(filename) : "";
            String otherBlob = inOther ? other.fileToBlobs.get(filename) : "";
            // An empty string blob means the file DNE.
            if (otherBlob.equals(headBlob)) {
                continue;
            }
            if (inOther && inHead) {
                handleConflict(filename, head, other);
                conflict = true;
                continue;
            }
            if (!splitBlob.equals(headBlob) && splitBlob.equals(otherBlob)) {
                continue;
            }
            if (splitBlob.equals(headBlob)) {
                checkout(filename, other.id);
                add(join(CWD, filename));
            }
            if (!inSplit) {
                checkout(filename, other.id);
                add(join(CWD, filename));
            } else {
                if (splitBlob.equals(headBlob) && !inOther) {
                    rm(join(CWD, filename));
                }
            }
        }
        commit(
                String.format("Merged %s into %s.",
                otherBranchName,
                readContentsAsString(HEAD)),
                other
        );
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }
}
