package com.keysight.include.content.helpers;

import java.util.Stack;

/**
 * A simple ThreadLocal stack to prevent circular content includes.
 */
public class SharedBlockIncludeStack
{
    private static ThreadLocal threadLocal = new ThreadLocal();

    public static String pop()
    {
        return (String) getStack().pop();
    }

    public static void push(String s)
    {
        getStack().push(s);
    }

    public static boolean contains(String s)
    {
        return getStack().contains(s);
    }

    public static String peek()
    {
        Stack stack = getStack();
        if (stack.size() == 0)
            return null;
        
        return (String) getStack().peek();
    }

    private static Stack getStack()
    {
        Stack stack = (Stack)threadLocal.get();

        if (stack == null)
        {
            stack = new Stack();
            threadLocal.set(stack);
        }

        return stack;
    }

    public static boolean isEmpty()
    {
        return getStack().isEmpty();
    }
}
