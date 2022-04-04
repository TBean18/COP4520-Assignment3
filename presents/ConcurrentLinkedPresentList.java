package presents;

import java.util.Currency;
import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * Heavily based on the Wait-Free Linked List Implementation discussed in
 * Chapter 9 of Concurrency in Action
 * 
 */
public class ConcurrentLinkedPresentList {
    Node head = new Node(-1);
    Node tailNode;

    ConcurrentLinkedPresentList() {
        tailNode = new Node(Integer.MAX_VALUE);
        head.next = new AtomicMarkableReference<ConcurrentLinkedPresentList.Node>(tailNode, false);
        tailNode.next = new AtomicMarkableReference<ConcurrentLinkedPresentList.Node>(null, false);
    }

    static class Node {
        AtomicMarkableReference<Node> next;
        int id;

        public Node(int id) {
            this.id = id;
        }
    }

    static class Window {
        public Node pred, cur;

        Window(Node myPred, Node myCur) {
            pred = myPred;
            cur = myCur;
        }
    }

    public Window find(Node head, int key) {
        Node pred = null, cur = null, succ = null;
        boolean[] marked = { false };
        boolean snip;
        retry: while (true) {
            pred = head;
            cur = pred.next.getReference();
            while (true) {
                succ = cur.next.get(marked);
                while (marked[0]) {
                    snip = pred.next.compareAndSet(cur, succ, false, false);
                    if (!snip) {
                        continue retry;
                    }
                    cur = succ;
                    succ = cur.next.get(marked);
                }
                if (cur.id >= key) {
                    return new Window(pred, cur);
                }
                pred = cur;
                cur = succ;
            }

        }
    }

    public boolean add(int id) {
        int key = id;
        while (true) {
            Window window = find(head, key);
            Node pred = window.pred, cur = window.cur;
            if (cur.id == key) {
                return false;
            } else {
                Node node = new Node(key);
                node.next = new AtomicMarkableReference<Node>(cur, false);
                if (pred.next.compareAndSet(cur, node, false, false)) {
                    return true;
                }
            }
        }

    }

    public boolean remove(int id) {
        int key = id;
        boolean snip;
        while (true) {
            Window window = find(head, key);
            Node pred = window.pred, curr = window.cur;
            if (curr.id != key) {
                return false;
            } else {
                Node succ = curr.next.getReference();
                snip = curr.next.compareAndSet(succ, succ, false, true);
                if (!snip)
                    continue;
                pred.next.compareAndSet(curr, succ, false, false);
                return true;
            }
        }
    }

    public boolean contains(int id) {
        boolean[] marked = { false };
        int key = id;
        Node curr = head;
        while (curr.id < key) {
            curr = curr.next.getReference();
            Node succ = curr.next.get(marked);
        }
        return (curr.id == key && !marked[0]);
    }

    public boolean isEmpty() {
        return head.next.compareAndSet(tailNode, tailNode, false, false);
    }
}
