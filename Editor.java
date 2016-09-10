package editor;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import java.lang.Math;

//scroll bar
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;

//mouse
import javafx.scene.input.MouseEvent;



/** creates a single text object using an arraylist of LinkedLists */

public class Editor extends Application {
    private int WINDOW_WIDTH = 500;
    private int WINDOW_HEIGHT = 500;

    private final Rectangle cursor;
    //creates blinky cursor

    private ArrayList<LinkedListText.Node> rowArr;
    private LinkedListText display;
    private int currPosX = 5;
    private int currPosY = 0;
    private int endX = 5;
    private int endY = 0;
    private Text currText;
    private int currTextWidth;
    private int currTextHeight;
    private int fontSize = 12;
    private String fontName = "Verdana";
    private Group textRoot = new Group();
    public static String firstParam = "";
    public static String secondParam = "";
    private OpenSave openSave;
    //add variables to track width of each row

    private ScrollBar scrollBar;
    private double scrollBarWidth;
    private double scrollOffset;

    private UndoRedoStack memory;

    public Editor() {
        rowArr = new ArrayList<>();//creates ArrayList row of LinkedListText columns
        display = new LinkedListText();//creates LinkedListText deque to store Text objects
        cursor = new Rectangle(0, 0);
        scrollBar = new ScrollBar();

    }

    private void textRender() {
        rowArr = new ArrayList<>(); //arrlist to keep track of rows
        display.setFirstRender(); //set render node to first text object in display

        rowArr.add(display.getRenderNode());
        //adds first object to arraylist (this will cause errors when dealing with first object)

        endX = 5;
        endY = 0;
        boolean wrapping = false;
        LinkedListText wordToWrap = new LinkedListText(); //linked list to keep track of text objects to wrap
        int i = 0;
        while (i < display.size()) {

            Text node = display.getRenderItem();

            if (wrapping) { // if we are wrapping, add the text to the linked list
                wordToWrap.addLast(node);
            }

            if (node.getText().equals(" ")) { // if the text is a space we begin wrapping
                wrapping = true;
            }

            if (wrapping && node.getText().equals(" ")) { // if there is a repeated space empty the list
                wordToWrap = new LinkedListText();
            }
            if (wrapping && node.getText().equals(" ") && endX + currTextWidth  > WINDOW_WIDTH - 5 - scrollBarWidth) {
                /** special case that keeps the cursor at the end of the window when repeated spaces */
                node.setX(node.getX() + currTextWidth);
                display.nextRender();
                i++;
                continue;
            }

            node.setFont(Font.font(fontName, fontSize));
            //sets font size and name

            node.setTextOrigin(VPos.TOP);
            //when the text is assigned a y-position is highest position across all letters

            currTextHeight = (int) Math.round(node.getLayoutBounds().getHeight());
            if (node.getText().equals("\r")) { // character \r is double height
                currTextHeight = (currTextHeight / 2) + 1;
            }
            currTextWidth = (int) Math.round(node.getLayoutBounds().getWidth());
            // Figure out the size of the current text.

            if (endX + currTextWidth + scrollBarWidth + 5 > WINDOW_WIDTH && wrapping) {
                /** if we have reached the end of the window and wrapping is true */

                if (node.getText().equals(" ")) {
                    /** fix edge case */
                } else {
                    endX = 5;
                    endY = endY + currTextHeight;
                    wordToWrap.removeLast();
                    wordToWrap.setFirstRender();
                    Text temp = wordToWrap.getRenderItem();
                    int j = 0;
                    while (j < wordToWrap.size()) {
                        /** prints out wrapped word on next line */
                        temp.setX(endX);
                        temp.setY(endY);
                        currTextWidth = (int) Math.round(temp.getLayoutBounds().getWidth());
                        currTextHeight = (int) Math.round(temp.getLayoutBounds().getHeight());
                        endX = endX + currTextWidth;
                        wordToWrap.nextRender();
                        temp = wordToWrap.getRenderItem();
                        j++;
                    }
                    wrapping = false;
                    wordToWrap = new LinkedListText();
                    rowArr.add(display.getRenderNode());
                }
            }
            if (endX + currTextWidth + scrollBarWidth + 5 > WINDOW_WIDTH) {
                /** if we have reached the end of the window */
                rowArr.add(display.getRenderNode());
                endX = 5;
                endY = endY + currTextHeight;
            }

            if (node.getText().equals("\r")){
                /** when we find an enter we go down a row */
                rowArr.add(display.getRenderNode());
                endX = 5;
                endY = endY + currTextHeight;
            }

            node.setX(endX);
            node.setY(endY);

            node.toFront();

            display.nextRender();
            currTextWidth = (int) Math.round(node.getLayoutBounds().getWidth());
            endX = endX + currTextWidth;

            i++;

            /** update scrollbar if text goes past the end of the window */
            scrollBar.setMax(Math.max(0, ((rowArr.size() * currTextHeight) - WINDOW_HEIGHT + currTextHeight)));

            scrollBar.setOrientation(Orientation.VERTICAL);
            // Set the height of the scroll bar so that it fills the whole window.
            scrollBar.setPrefHeight(WINDOW_HEIGHT);

        }
    }
    private void cursorRender() {
        if (currText != null) {
            if (currText.getText().equals(" ") && currText.getX() > WINDOW_WIDTH - 5 - scrollBarWidth) {
                /** special case where we are at the end of the window but still have spaces */
                cursor.setX(WINDOW_WIDTH - 5 - scrollBarWidth);
                cursor.setY(currText.getY());
                cursor.setHeight(currTextHeight);
                return;
            }
            // Figure out the size of the current text.
            currTextHeight = (int) Math.round(currText.getLayoutBounds().getHeight());
            if (currText.getText().equals("\r")) {
                currTextHeight = (currTextHeight / 2) + 1;
            }
            currTextWidth = (int) Math.round(currText.getLayoutBounds().getWidth());
            int currWordX = (int) Math.round(currText.getX());
            currPosX = currWordX + currTextWidth;//move cursor
            currPosY = (int) Math.round(currText.getY());

            /** Re-size and re-position the bounding box. */
            cursor.setHeight(currTextHeight);
            cursor.setWidth(1);

            /** case where we are at the beginning of the row and there is a space */
            if (currText.getText().equals(" ") && currWordX + currTextWidth + scrollBarWidth + 5 > WINDOW_WIDTH) {
                currPosX = 5;
                currPosY = currPosY + currTextHeight;
            }
            /** Set cursor to appear to the right of the typed letter */
            cursor.setX(currPosX);
            cursor.setY(currPosY);
        }
    }
    public void newLine() {
        Text enterText = new Text(currPosX, currPosY, "\r");
        display.insert(enterText);
        textRoot.getChildren().add(enterText);
        currText = display.returnText();
        textRender();
        cursorRender();
    }

    /** An EventHandler to handle keys that get pressed. */
    public class KeyEventHandler implements EventHandler<KeyEvent> {


        public KeyEventHandler() {

        }

        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.isShortcutDown()) {
                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.P) {
                    /** when the user presses shortcut+p,
                     * you should print the top left coordinate of the current cursor position.
                     * The cursor position should be printed in the format "x, y"
                     * where the x and y positions describe the cursor position relative to
                     * the top left corner of the window
                     */
                    System.out.println((int)cursor.getX() + ", " + (int)cursor.getY());
                } else if (code == KeyCode.S) {
                    /** command S writes to file */
                    openSave.save();

                } else if (code == KeyCode.PLUS || code == KeyCode.EQUALS) {
                    /** inc font size */
                    fontSize += 4;
                    textRender();
                    cursorRender();

                }else if (code == KeyCode.MINUS) {
                    /** dec font size */
                    fontSize = Math.max(0, fontSize - 4);
                    textRender();
                    cursorRender();
                } else  if (code == KeyCode.Z) {
                    /** CONTROLZZZ */
                    Text retrievedText = memory.undoText();
                    String retrievedString = memory.undoString();

                    if (retrievedString.equals("added")) {
                        currText = retrievedText;
                        cursorRender();
                        int rowIndex = (currPosY / currTextHeight);
                        display.setCurrentPos(rowArr.get(rowIndex), currPosX);
                        display.remove();
                        textRoot.getChildren().remove(retrievedText);
                        currText = display.returnText();//set the currText to the previous character
                        cursorRender();
                    } else if (retrievedString.equals("deleted")) {
                        display.insert(retrievedText);
                        textRoot.getChildren().add(retrievedText);
                        currText = display.returnText();
                        cursorRender();

                    }

                } else  if (code == KeyCode.Y) {
                    /** REDOOOO */
                    Text retrievedText = memory.redoText();
                    String retrievedString = memory.redoString();

                    if (retrievedString.equals("added")) {
                        display.insert(retrievedText);
                        textRoot.getChildren().add(retrievedText);
                        currText = display.returnText();
                        cursorRender();
                    } else if (retrievedString.equals("deleted")) {
                        currText = retrievedText;
                        cursorRender();
                        int rowIndex = (currPosY / currTextHeight);
                        display.setCurrentPos(rowArr.get(rowIndex), currPosX);
                        display.remove();
                        textRoot.getChildren().remove(retrievedText);
                        currText = display.returnText();//set the currText to the previous character
                        cursorRender();
                    }
                }

            } else if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
                // Use the KEY_TYPED event rather than KEY_PRESSED for letter keys, because with
                // the KEY_TYPED event, javafx handles the "Shift" key and associated
                // capitalization.
                String characterTyped = keyEvent.getCharacter();
                if (characterTyped.equals("\r")){
                    /** if enter is pressed, create a new line, move the text/cursor */
                    newLine();

                } else if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {
                    // Ignore control keys, which have non-zero length, as well as the backspace key, which is
                    // represented as a character of value = 8 on Windows.

                    currText = new Text(currPosX, currPosY, Character.toString(characterTyped.charAt(0)));
                    /** Text input Each time the user types a letter on the keyboard,
                     * that letter should appear on the screen after the current cursor position,
                     * and the cursor should advance to be after the last letter that was typed.
                     */

                    display.insert(currText);//adds object to linked list
                    textRoot.getChildren().add(currText);

                    /** push to memory */
                    memory.undoPush("added", currText);

                    textRender();//update text
                    cursorRender();//move cursor

                    if (currPosY + currTextHeight > WINDOW_HEIGHT - textRoot.getLayoutY()) {
                        int maxScroll = (int)scrollBar.getMax();
                        scrollBar.setValue(maxScroll);
                    }

                }
            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                // Arrow keys should be processed using the KEY_PRESSED event, because KEY_PRESSED
                // events have a code that we can check (KEY_TYPED events don't have an associated
                // KeyCode).
                KeyCode code = keyEvent.getCode();
                /** Arrow keys Pressing any of the four arrow keys (up, down, left, right)
                 * should cause the cursor to move accordingly */
                if (code == KeyCode.UP) {
                    if (currPosY > 0) {

                        int rowIndex = (currPosY / currTextHeight) - 1;

                        currText = display.setCurrentPos(rowArr.get(rowIndex), currPosX);
                        cursorRender();
                    }
                } else if (code == KeyCode.DOWN) {
                    if (currPosY + currTextHeight < rowArr.size() * currTextHeight) {

                        int rowIndex = (currPosY / currTextHeight) + 1;

                        currText = display.setCurrentPos(rowArr.get(rowIndex), currPosX);
                        cursorRender();
                    }
                    textRender();
                } else if (code == KeyCode.LEFT) {

                    /** if the previous character exists,
                     * decrement currRowIndex
                     * reassign currText and currPosX
                     * render text
                     * render cursor
                     */
                    display.moveBackward();
                    currText = display.returnText();
                    cursorRender();

                } else if (code == KeyCode.RIGHT) {
                    /** Arrow keys Pressing any of the four arrow keys (up, down, left, right)
                     * should cause the cursor to move accordingly
                     */
//                    if (currText.getY() < endY) {
                    /** if the next character exists,
                     * increment currRowIndex
                     * reassign currText and currPosX
                     * render text
                     * render cursor
                     */
                    display.moveForward();
                    currText = display.returnText();
                    cursorRender();
                    //}
                } else if (code == KeyCode.BACK_SPACE) {
                    /** Backspace Pressing the backspace key should cause the character
                     * before the current cursor position to be deleted.
                     */
                    if (display.size() - 1 > 0) {
                        /** if there is text in the List,
                         * remove the character from the list.
                         * decrement the currPosX by the removed char width.
                         * decrement the row position.
                         * reassign currText
                         */

                        Text removed = display.remove();//remove and get the element to be removed
                        memory.undoPush("deleted", removed);
                        currText = display.returnText();//set the currText to the previous character
                        textRoot.getChildren().remove(removed);
                        textRender();
                        cursorRender();
                        int textSize = rowArr.size() * currTextHeight;
                        if (currPosY - currTextHeight < textSize && textSize < WINDOW_HEIGHT) {
                            int maxScroll = (int) scrollBar.getMax();
                            scrollBar.setValue(maxScroll);
                        }
                    } else if (display.size() - 1 == 0) {
                        /** we've backspaced to the beginning of the line */
                        textRoot.getChildren().remove(display.remove()); //removes text from both root and list
                        currText = null;
                        currPosX = 5;
                        textRender();
                        cursorRender();
                    }
                }
            }
            keyEvent.consume();
        }
    }

    /** An EventHandler to handle changing the color of the rectangle. */
    public class RectangleBlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 0;
        private Color[] boxColors =
                {Color.BLACK, Color.WHITE};

        RectangleBlinkEventHandler() {
            // Set the color to be the first color in the list.
            changeColor();
        }

        private void changeColor() {
            cursor.setFill(boxColors[currentColorIndex]);
            currentColorIndex = (currentColorIndex + 1) % boxColors.length;
        }

        @Override
        public void handle(ActionEvent event) {
            changeColor();
        }
    }
    /** Makes the cursor box blink periodically. */
    public void makeRectangleColorChange() {
        // Create a Timeline that will call the "handle" function of RectangleBlinkEventHandler
        // every 0.5 seconds.
        final Timeline timeline = new Timeline();
        // The rectangle should continue blinking forever.
        timeline.setCycleCount(Timeline.INDEFINITE);
        RectangleBlinkEventHandler cursorChange = new RectangleBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    /** An event handler that displays the current position of the mouse whenever it is clicked. */
    private class MouseClickEventHandler implements EventHandler<MouseEvent> {
        /** A Text object that will be used to print the current mouse position. */
        MouseClickEventHandler() {
            /*// For now, since there's no mouse position yet, just create an empty Text object.
            positionText = new Text("");
            // We want the text to show up immediately above the position, so set the origin to be
            // VPos.BOTTOM (so the x-position we assign will be the position of the bottom of the
            // text).
            positionText.setTextOrigin(VPos.BOTTOM);

            // Add the positionText to root, so that it will be displayed on the screen.
            root.getChildren().add(positionText);*/
        }


        @Override
        public void handle(MouseEvent mouseEvent) {
            // Because we registered this EventHandler using setOnMouseClicked, it will only called
            // with mouse events of type MouseEvent.MOUSE_CLICKED.  A mouse clicked event is
            // generated anytime the mouse is pressed and released on the same JavaFX node.
            int clickX = (int) Math.round(mouseEvent.getX());
            int clickY = (int) Math.round(mouseEvent.getY());


            int rowIndex = (int) Math.floor((clickY + scrollOffset) / currTextHeight);
            currText = display.setCurrentPos(rowArr.get(rowIndex), clickX);

            textRender();
            cursorRender();

        }
    }

    @Override
    public void start(Stage primaryStage) {
        // Create a Node that will be the parent of all things displayed on the screen.
        Group root = new Group();
        root.getChildren().add(textRoot);

        // The Scene represents the window: its height and width will be the height and width
        // of the window displayed.
        int windowWidth = 500;
        int windowHeight = 500;
        Scene scene = new Scene(root, windowWidth, windowHeight, Color.WHITE);

        // To get information about what keys the user is pressing, create an EventHandler.
        // EventHandler subclasses must override the "handle" function, which will be called
        // by javafx.
        EventHandler<KeyEvent> keyEventHandler =
                new KeyEventHandler();
        // Register the event handler to be called for all KEY_PRESSED and KEY_TYPED events.
        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);

        /** CURSORAMAZING */
        // cursor needs to be added to the root in order to be displayed.
        textRoot.getChildren().add(cursor);
        makeRectangleColorChange();

        /** SCROLLICIOUS */
        scrollBarWidth = scrollBar.getLayoutBounds().getWidth();
        // Make a vertical scroll bar on the right side of the screen.
        scrollBar.setOrientation(Orientation.VERTICAL);
        // Set the height of the scroll bar so that it fills the whole window.
        scrollBar.setPrefHeight(WINDOW_HEIGHT);

        // Set the range of the scroll bar.
        scrollBar.setMin(0);
        scrollBar.setMax(0);

        double usableScreenWidth = WINDOW_WIDTH - scrollBar.getLayoutBounds().getWidth();
        scrollBar.setLayoutX(usableScreenWidth);

        // Add the scroll bar to the scene graph, so that it appears on the screen.
        root.getChildren().add(scrollBar);
        scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {

                // newValue describes the value of the new position of the scroll bar. The numerical
                // value of the position is based on the position of the scroll bar, and on the min
                // and max we set above. For example, if the scroll bar is exactly in the middle of
                // the scroll area, the position will be:
                //      scroll minimum + (scroll maximum - scroll minimum) / 2
                // Here, we can directly use the value of the scroll bar to set the height of Josh,
                // because of how we set the minimum and maximum above.

                int newVal = newValue.intValue() - 5;
                textRoot.setLayoutY(-newVal);
                scrollOffset = (double) newValue;

            }
        });

        /** reSIZE */
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenWidth,
                    Number newScreenWidth) {
                WINDOW_WIDTH = Math.round(newScreenWidth.intValue());
                textRender();
                cursorRender();
                scrollBar.setLayoutX(WINDOW_WIDTH - scrollBar.getLayoutBounds().getWidth());

            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenHeight,
                    Number newScreenHeight) {
                WINDOW_HEIGHT = Math.round(newScreenHeight.intValue());
                textRender();
                cursorRender();
                scrollBar.setPrefHeight(WINDOW_HEIGHT);
            }
        });

        /** UNDO/REDO */
        memory = new UndoRedoStack();


        /** MICKEY MOUSE */
        scene.setOnMouseClicked(new MouseClickEventHandler());


        primaryStage.setTitle("SUBLIMEVIMPICOTEXTEDIT");

        /** OPENININE SAVINGGN */
        openSave = new OpenSave(firstParam, secondParam, display, textRoot);
        openSave.open();
        display.setFirstRender();
        currText = display.getRenderItem();

        textRender();
        cursorRender();
        // This is boilerplate, necessary to setup the window where things are displayed.
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            firstParam = args[0];
        } else if (args.length > 1) {
            firstParam = args[0];
            secondParam = args[1];
        }
        launch(args);
    }
}
