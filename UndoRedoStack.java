package editor;

import javafx.scene.text.Text;

import java.util.Stack;

public class UndoRedoStack {
    private Stack undoStackCommand;
    private Stack undoStackText;

    private Stack redoStackCommand;
    private Stack redoStackText;

    private int counter;

    public UndoRedoStack() {
        undoStackCommand = new Stack();
        undoStackText = new Stack();
        redoStackCommand = new Stack();
        redoStackText = new Stack();
        counter = 0;
    }

    public void undoPush(String command, Text text) {
        /** adds command Strings and their Text objects to memory stacks */
        if (counter <= 100) {
            undoStackCommand.push(command);
            undoStackText.push(text);
            counter++;
        } else {
            undoStackCommand.remove(0);
            undoStackText.remove(0);
            counter--;
        }
//        System.out.println(undoStackCommand.peek());

    }

    public Text undoText() {
        /** undo a command */
        Text undid = (Text) undoStackText.pop();
        redoStackText.push(undid);
        counter--;
        return undid;
    }

    public String undoString() {
        /** have to return the command string to identify the inverse of the command */
        String undid = (String) undoStackCommand.pop();
        redoStackCommand.push(undid);
        return undid;
    }

    public Text redoText() {
        /** redoes a command */
        Text redone = (Text) redoStackText.pop();
        undoStackText.push(redone);
        counter++;
        return redone;
    }

    public String redoString() {
        /** returns the string so we can redo the command */
        String redone = (String) redoStackCommand.pop();
        undoStackCommand.push(redone);
        return redone;
    }


}