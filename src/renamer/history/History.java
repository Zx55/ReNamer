/*
 * @author Zx55, mcy
 * @project ReNamer
 * @file History.java
 * @date 2019/6/1 21:54
 * @version 1.0
 * Copyright (c) Zx55. All rights reserved.
 */

package renamer.history;

import java.util.ArrayList;
import java.util.LinkedList;

public final class History {
    private static History history = new History();
    private LinkedList<ArrayList<String>> stack;

    private History() {
        stack = new LinkedList<>();
    }

    public static void clear() {
        history.stack.clear();
    }

    public static ArrayList<String> top() {
        return history.stack.getFirst();
    }

    public static void pop() {
        history.stack.removeFirst();
    }

    public static ArrayList<String> topAndPop() {
        return history.stack.removeFirst();
    }

    public static void push(ArrayList<String> item) {
        history.stack.addFirst(item);
    }
}
