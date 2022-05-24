package com.github.tac.jagent.demo;

import java.util.Arrays;

/**
 * @author taccisum - liaojinfeng6938@dingtalk.com
 * @since 2022/5/24
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("------ Main#main() args: " + Arrays.toString(args));
        sayhello();
        System.out.println("------ Main's classloader: " + Main.class.getClassLoader());
    }

    private static void sayhello() {
        // this statement will be replaced by MyAgent.java
        System.out.println("hello world!");
    }
}
