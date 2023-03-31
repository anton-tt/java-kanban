package basic;

import tasks.*;

public class Node {
    public Task data;
    public Node prev;
    public Node next;


    public Node(Node prev, Task data, Node next) {
        this.prev = prev;
        this.data = data;
        this.next = next;
    }
}