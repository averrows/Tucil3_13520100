package Tree;

import java.util.ArrayList;

import Commands.*;
import Puzzle.Puzzle;

public class StateTreeNode<T> extends TreeNode<T> {
    public ArrayList<StateTreeNode<T>> childs;
    public int pathCount;
    public String commandCreator;
    public ArrayList<String> commandCreators;
    ArrayList<Command<Puzzle>> commands;

    @SuppressWarnings("unchecked")
    public StateTreeNode(T p, int pathCount, String co, ArrayList<String> before) {
        super(p);
        this.pathCount = pathCount;
        commandCreator = co;
        commandCreators = (ArrayList<String>) before.clone();
        if (!commandCreator.equals("")) {
            commandCreators.add(co);
        }
        this.childs = new ArrayList<StateTreeNode<T>>();
        this.commands = new ArrayList<Command<Puzzle>>();
        commands.add(new UpCommand());
        commands.add(new DownCommand());
        commands.add(new RightCommand());
        commands.add(new LeftCommand());
    }

    private static <U> U runCommand(Command<U> commands, U v) {
        return commands.nextState(v);
    }

    public int cost() {
        if (val instanceof Puzzle) {
            Puzzle a = (Puzzle) val;
            return  g(a.getContent()) + pathCount ;
        } else {
            return 0;
        }
    }

    private int g(int[][] p) {
        int count = 0;
        for (int i = 0; i < p.length; i++) {
            count += Puzzle.shortestDistanceFromCorrectPosition(i+1, p[i]);
            if (!Puzzle.isCorrectPosition(i + 1, p[i]) && (i+1) != p.length) {
                count += 3;
            }
        }
        return count;
    }

    public void printPath(Puzzle initial){
        initial.print();
        for (int i = 0; i < commandCreators.size(); i++) {
            int j = 0;
            boolean found = false;
            while (j < commands.size() && !found) {
                if (commands.get(j).toString().equals(commandCreators.get(i))) {
                    Puzzle next = StateTreeNode.runCommand(commands.get(j), initial);
                    System.out.println("COMMAND: " + commands.get(j).toString());
                    System.out.println();
                    next.print();
                    initial = next;
                    found = true;
                }
                j++;
            }
        }
        System.out.println("GOAL REACHED");
        System.out.println();
    }

    @Override
    @SuppressWarnings("unchecked")
    public int expand() {
        if (val instanceof Puzzle) {
            int n = 0;
            Puzzle t = (Puzzle) val;
            childs.clear();
            for (int i = 0; i < commands.size(); i++) {
                if (commands.get(i).isAllowed(t) && !commands.get(i).opposite().equals(commandCreator)) {
                    childs.add((StateTreeNode<T>) new StateTreeNode<Puzzle>(
                            StateTreeNode.runCommand(commands.get(i), t), pathCount + 1,
                            commands.get(i).toString(), commandCreators));
                    n++;
                }
            }
            return n;
        }
        return 0;
    }

}
