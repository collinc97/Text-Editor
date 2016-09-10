package editor;

import javafx.scene.text.Text;

public class LinkedListText {

    public class Node {
        private Text item;
        private Node next;
        private Node prev;

        public Node(Node p, Text i, Node n) {
            item = i;
            next = n;
            prev = p;
        }
    }


    private Node sentinel = new Node(null, null, null);
    private Node currentPos;
    private Node renderNode;
    private int size;

    public LinkedListText() {
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        currentPos = sentinel;
        renderNode = sentinel;
        size = 0;
    }

    /*
    public LinkedListDeque(Item x) {
        size = 1;
        Node nextNode = new Node(sentinel, x, sentinel);
        sentinel.next = nextNode;
        sentinel.prev = nextNode;

    } */
    public void setFirstRender(){
        renderNode = sentinel.next;
    }

    public void nextRender(){
        renderNode = renderNode.next;
    }

    public Text getRenderItem(){
        return renderNode.item;
    }
    public Node getRenderNode(){
        return renderNode;
    }

    public void moveForward() {
        if (currentPos.next != null) {
            currentPos = currentPos.next;
        }
    }

    public void moveBackward() {
        if (currentPos.prev != null) {
            currentPos = currentPos.prev;
        }
    }

    public Text setCurrentPos(Node rowNode, int currPosX) {
        int linePos = 5;
        int min = 1000;
        Node minText = new Node(null, null, null);
        currentPos = rowNode;
        if (rowNode.item.getText().equals("\r")) {
            /** special case for first row */
            currentPos = currentPos.next;
        }
        int charWidth = (int) Math.round(currentPos.item.getLayoutBounds().getWidth());
        int charX = (int) Math.round(currentPos.item.getX());

        while (!currentPos.item.getText().equals("\r") && currentPos.next.item != null ) {

            if (Math.abs(currPosX - charX - charWidth) < min) {
                min = Math.abs(currPosX - charX - charWidth);
                minText = currentPos;
            }
            linePos = linePos + charWidth;
            currentPos = currentPos.next;
            charWidth = (int) Math.round(currentPos.item.getLayoutBounds().getWidth());
            charX = (int) Math.round(currentPos.item.getX());
        }
        currentPos = minText;
        return minText.item;

    }

    public Text returnText() {
        return currentPos.item;
    }

    public Node returnNode() {
        return currentPos;
    }


    public void addLast(Text x) {
        Node nextNode = new Node(sentinel.prev, x, sentinel);
        sentinel.prev.next = nextNode;
        sentinel.prev = nextNode;
        currentPos = nextNode;
        size++;
    }

    public void insert(Text x) {
        if (currentPos.next == null) {
            addLast(x);
        } else {
            Node newNode = new Node(currentPos, x, currentPos.next);
            currentPos.next.prev = newNode;
            currentPos.next = newNode;
            currentPos = newNode;
            size++;
        }
    }

    public Text remove() {
        if (currentPos.prev == null) {
            return removeFirst();
        } else if (currentPos.next == null) {
            return removeLast();
        } else {
            Text returnItem = currentPos.item;
            currentPos.prev.next = currentPos.next;
            currentPos.next.prev = currentPos.prev;
            currentPos = currentPos.prev;
            size--;
            return returnItem;
        }
    }

    public boolean isEmpty() {
        if (size == 0) {
            return true;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public Text removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Text returnThing = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size--;
        return returnThing;

    }

    public Text removeLast() {
        Text returnThing = sentinel.prev.item;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        size--;
        return returnThing;

    }

}
