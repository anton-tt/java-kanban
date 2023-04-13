package managers;

import java.util.*;
import tasks.Task;
import basic.Node;

public class InMemoryHistoryManager implements HistoryManager {
    final Map<Integer, Node> nodeMap = new HashMap<>();
    final CustomLinkedList viewedTasksList = new CustomLinkedList();

    @Override
    public void addViewedTask(Task task) {
        int taskId = task.getId();
        if (nodeMap.containsKey(taskId)) {
           removeRecurringTask(taskId);
        }
        Node nodeAdd = viewedTasksList.linkLast(task);
        nodeMap.put(taskId, nodeAdd);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> listTask = viewedTasksList.getViewedTasks();
        return listTask;
    }

    @Override
    public void removeRecurringTask(int id) {
        Node nodeRemove = nodeMap.get(id);
        viewedTasksList.removeNode(nodeRemove);
        nodeMap.remove(id);
    }

    public static class CustomLinkedList {
        public Node head;
        public Node tail;
        int size = 0;

        Node linkLast(Task task) {
            Node newNode = null;
            if(head == null) {
                newNode = new Node(null, task, null);
                head = newNode;
            } else if (head.data != null && head.next == null) {
                newNode = new Node(head, task, null);
                tail = newNode;
                tail.prev = head;
                head.next = tail;
            } else if (head.data != null && head.next != null) {
                Node oldTail = tail;
                newNode = new Node(oldTail, task, null);
                tail = newNode;
                tail.prev = oldTail;
                oldTail.next = tail;
            }
            size++;
            return newNode;
        }

        List<Task> getViewedTasks() {
            List<Task> viewedTasksList = new ArrayList<>();
            Node currentNode = head;
            while (currentNode != null) {
                viewedTasksList.add(currentNode.data);
                currentNode = currentNode.next;
            }
            return viewedTasksList;
        }

       void removeNode(Node node) {
           if (node.prev == null && node.next != null) {
               (node.next).prev = null;
                head = node.next;
           } else if (node.prev != null && node.next == null) {
               (node.prev).next = null;
                tail = null;
           } else if (node.prev != null && node.next != null) {
                (node.prev).next = node.next;
                (node.next).prev = node.prev;
           }
           size--;
       }
    }

}