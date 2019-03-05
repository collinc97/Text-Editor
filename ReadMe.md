## Text-Editor

This project, is a text editor built from scratch. You are probably familiar with a variety of different text editors, including relatively simple text editors that allow you to edit un-styled text (e.g., pico, Notepad, and TextEdit), and also more fully-featured text editors that allow you to do add formatting, run code, and more (e.g., Microsoft Word, Google Docs, Sublime, VI, Emacs, and IntelliJ). For this project, a basic text editor can be used to open, edit, and save plain text files.

## Installation

- editor.java - This is the main file for the project and is what should be run individually in an IDE
- LinkedListText.java - Constructs the central data structure used in the editor
- OpenSave.java - Implements open and save methods of editor
- UndoRedoStack.java - Stores up to 100 editor commands in a stack to implement undo/redo function

## Features

- Cursor - The current position of the cursor marked with a flashing vertical line.
- Text input - Each time the user types a letter on the keyboard, that letter should appear on the screen after the current cursor position, and the cursor should advance to be after the last letter that was typed.
- Word wrapping - Break text into lines such that it fits the width of the text editor window without requiring the user to scroll horizontally. Editor should break lines between words rather than within words. Lines should only be broken in the middle of a word when the word does not fit on its own line.
- Newlines - When the user presses the Enter or Return key, editor should advance the cursor to the beginning of the next line.
- Backspace - Pressing the backspace key should cause the character before the current cursor position to be deleted.
- Open and save - editor should accept a single command line argument describing the location of the file to edit. If that file exists, editor should display the contents of that file. Pressing shortcut+s should save the current contents of the editor to that file.
- Arrow keys - Pressing any of the four arrow keys (up, down, left, right) should cause the cursor to move accordingly (e.g., the up key should move the cursor to be on the previous line at the horizontal position closest to the horizontal position of the cursor before the arrow was pressed).
- Mouse input - When the user clicks somewhere on the screen, the cursor should move to the place in the text closest to that location.
- Window re-sizing - When the user re-sizes the window, the text should be re-displayed so that it fits in the new window (e.g., if the new window is narrower or wider, the line breaks should be adjusted accordingly).
- Vertical scrolling - Editor should have a vertical scroll bar on the right side of the editor that allows the user to vertically navigate through the file. Moving the scroll bar should change the positioning of the file (but not the cursor position), and if the cursor is moved (e.g., using the arrow keys) so that it's not visible, the scroll bar and window position should be updated so that the cursor is visible.
- Undo and redo - Pressing shortcut+z should undo the most recent action (either inserting a character or removing a character), and pressing shortcut+y should redo. Your editor should be able to undo up to 100 actions, but no more.
- Changing font size - Pressing shortcut+"+" (the shortcut key and the "+" key at the same time) should increase the font size by 4 points and pressing shortcut+"-" should decrease the font size by 4 points.

## Tests

Printing the current position - Pressing shortcut+p should print the top left coordinate of the current cursor position.

## Motivation

This project was a project completed while enrolled in Computer Science 61B at the Univeristy of Califorina at Berkeley in the Fall of 2016.
