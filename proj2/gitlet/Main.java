package gitlet;

import static gitlet.Repository.getCurHead;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Marco
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String command = args[0];
        switch (command) {
            case "init":
                Repository.init();
                break;
            case "add":
                Repository.add(args[1]);
                break;
            case "commit":
                if (args.length != 2 || args[1].equals("")) {
                    System.out.println("Please enter a commit message.");
                    return;
                }
                Repository.commit(args[1]);
                break;
            case "rm":
                Repository.rm(args[1]);
                break;
            case "log":
                Repository.log();
                break;
            case "global-log":
                Repository.globalLog();
                break;
            case "find":
                Repository.find(args[1]);
                break;
            case "status":
                Repository.status();
                break;
            case "branch":
                Repository.branch(args[1]);
                break;
            case "checkout":
                if (args.length == 2) {
                    Repository.checkout(args[1]);
                } else if (args.length == 3) {
                    Repository.checkout(args[2], getCurHead().id);
                } else if (args.length == 4) {
                    Repository.checkout(args[3], args[1]);
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }
}
