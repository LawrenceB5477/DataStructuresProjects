public class CDList<E> implements Cloneable{
    //--------------nested node class------------
    private static class Node<E> {
        private E element;
        private Node<E> prev;
        private Node<E> next;

        public Node(E e, Node<E> p, Node<E> n) {
            element = e;
            prev = p;
            next = n;
        }
        public E getElement() { return element; }
        public Node<E> getPrev() { return prev; }
        public Node<E> getNext() { return next; }
        public void setPrev(Node<E> p) { prev = p; }
        public void setNext(Node<E> n) { next = n; }
        public String toString() {
            return "(" + prev.getElement() +"," + element + "," + next.getElement() +")";
        }
    }
    //---------------end of nested node class------------

    //Member data
    public Node<E> tail = null;
    private int size = 0;

    //Member Methods
    public CDList() {

    }

    public CDList(CDList<E> cdl) {
        //the copy constructor
        Node<E> copyHead = cdl.tail.getNext();
        for (int i = 0; i < cdl.size(); i++) {
            this.addLast(copyHead.getElement());
            copyHead = copyHead.getNext();
        }
    }

    public int size() {
        //returns the size of the list
        return size;
    }

    public boolean isEmpty() {
        //checks if the list is empty
        return size == 0;
    }
    public E first() {
        //return the first element of the list
        if (size > 0) {
            return tail.getNext().getElement();
        } else {
            return null;
        }
    }
    public E last() {
        //return the last element of the list
        if (size > 0) {
            return tail.getElement();
        } else {
            return null;
        }
    }
    public void rotate() {
        //rotate the first element to the back of the list
        tail = tail.next;
    }
    public void rotateBackward() {
        //rotate the last element to the front of the list
        tail = tail.prev;
    }
    public void addFirst(E e) {
        if (size == 0) {
            Node<E> element = new Node<>(e, null, null);
            element.setNext(element);
            element.setPrev(element);
            tail = element;
        } else {
            Node<E> element = new Node<>(e, tail, tail.next);
            tail.setNext(element);
            element.getNext().setPrev(element);
        }
        size++;
        //add element e to the front of the list
    }
    public void addLast(E e) {
        if (size == 0) {
            Node<E> element = new Node<>(e, null, null);
            element.setNext(element);
            element.setPrev(element);
            tail = element;
        } else {
            Node<E> element = new Node<>(e, tail, tail.next);
            tail.setNext(element);
            element.getNext().setPrev(element);
            tail = element;
        }
        size++;
    }
    public E removeFirst() {
        if (size == 0) {
            return null;
        }
        E element = tail.getNext().getElement();
        if (size == 1) {
            tail = null;
        } else {
            Node<E> head = tail.getNext();
            tail.setNext(head.getNext());
            head.getNext().setPrev(tail);
        }
        size--;
        return element;
    }

    public E removeLast() {
        if (size == 0) {
            return null;
        }
        E element = tail.getElement();
        if (size == 1) {
            tail = null;
        } else {
            Node<E> head = tail.getNext();
            tail.getPrev().setNext(head);
            tail = tail.getPrev();
            head.setPrev(tail);
        }
        size--;
        return element;
    }

    public CDList<E> clone() {
        //clone and return the new list(deep clone)
        CDList<E> clone = new CDList<>();
        Node<E> walk = tail.getNext();
        for (int i = 0; i < size; i++) {
            clone.addLast(walk.getElement());
            walk = walk.getNext();
        }
        return clone;
    }

    public boolean equals(Object o) {
        //Sees if both objects are of the same class before doing any logic
        if (this.getClass() != o.getClass()) {
            return false;
        }

        CDList<E> other = (CDList<E>) o;
        //Check if sizes are equal
        if (other.size() != size) {
            return false;
        }

        //Start from the originalHead head, compare each other element
        Node<E> originalHead = tail.getNext();
        Node<E> otherNode = other.tail.getNext();

        for (int i = 0; i < size; i++) {
            //See if the sequence is the same
            if (originalHead.getElement().equals(otherNode.getElement())) {
                Node<E> walking = originalHead;
                Node<E> walkingOther = otherNode;
                int match = 0;
                for (int j = 0; j < size; j++) {

                    if (!walking.getElement().equals(walkingOther.getElement())) {
                        break;
                    } else {
                        match++;
                        walking = walking.getNext();
                        walkingOther = walkingOther.getNext();
                    }
                }
                //The elements are in the same sequence
                if (match == size) {
                    return true;
                }
            }
            otherNode = otherNode.getNext();
        }

        return false;
    }

    public void attach(CDList cdl) {
        //insert cdl after the tail of the current list
        cdl = cdl.clone();

        Node<E> tempTail = tail.getNext();
        tail.setNext(cdl.tail.getNext());
        cdl.tail.getNext().setPrev(tail);
        cdl.tail.setNext(tempTail);
        tempTail.setPrev(cdl.tail);

        tail = cdl.tail;
        size += cdl.size;
    }

    private void removeElement(int position) {
        Node<E> walk = tail.getNext();
        for (int i = 0; i < position; i++) {
            walk = walk.getNext();
        }

        Node<E> precursor = walk.getPrev();
        Node<E> successor = walk.getNext();

        precursor.setNext(successor);
        successor.setPrev(precursor);

        //If the element we removed is the tail, set the new tail
        if (walk == tail) {
            tail = precursor;
        }

        size--;

    }

    public void removeDuplicates() {
        //remove all elements that happen more than once in the list. If the tail gets //deleted, the element immediately before the tail will become the new tail.
        Node<E> walk = tail.getNext();
        Node<E> otherWalk = tail.getNext().getNext();
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {

                if (walk.getElement().equals(otherWalk.getElement())) {
                    removeElement(j);
                }

                otherWalk = otherWalk.getNext();
            }
            walk = walk.getNext();
            otherWalk = walk.getNext();
        }
    }

    public void printList() {
        //prints all elements in the list
        if (size == 0)
            System.out.println("List is empty.");
        else {
            Node<E> walk = tail.getNext();

            while (!(walk.getNext().equals(tail.getNext()))) {
                System.out.print(walk.toString() + ", ");
                walk = walk.getNext();
            }
            System.out.println(walk.toString());
        }
    }
}
