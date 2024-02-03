package org.rcsb.utils;

public class Range {

    private final int x;
    private final int y;
    private final boolean empty;

    public Range(int x, int y){
        this.x = x;
        this.y = y;
        empty = x > y;
    }

    public int top(){
        return y;
    }

    public int bottom(){
        return x;
    }

    public boolean isEmpty(){
        return empty;
    }

    public boolean contains(int n){
        return n >= x && n <= y;
    }

    public int size(){
        return y - x + 1;
    }
}
