package cs2030.util;

import cs2030.util.Pair;

import java.util.Comparator;
import java.util.PriorityQueue;

public class PQ<T> {

    private final Comparator<? super T> cmp;
    private final PriorityQueue<T> elems;

    public PQ(Comparator<? super T> cmp) {
        this.cmp = cmp;
        this.elems = new PriorityQueue<T>(cmp);
    }

    private PQ(Comparator<? super T> cmp, PriorityQueue<? extends T> elems) {
        this.cmp = cmp;
        this.elems = new PriorityQueue<T>(elems);
    }

    public PQ<T> add(T elem) {
        PQ<T> newElems = new PQ<T>(this.cmp, this.elems);
        newElems.elems.add(elem);
        return newElems;
    }

    public PQ<T> remove(T elem) {
        PQ<T> newElems = new PQ<T>(this.cmp, this.elems);
        newElems.elems.remove(elem);
        return newElems;
    }

    public boolean isEmpty() {
        return this.elems.isEmpty();
    }

    public Pair<T, PQ<T>> poll() {
        PQ<T> newElems = new PQ<T>(this.cmp, this.elems);
        T head = newElems.elems.poll();
        Pair<T, PQ<T>> test = Pair.of(head, newElems);
        return test;
    }

    @Override
    public String toString() {
        return this.elems.toString();
    }

}
