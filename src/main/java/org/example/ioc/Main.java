package org.example.ioc;

import java.util.ArrayList;

class Main {

    public static void main(String[] args) {
        ArrayList arrayList = new ArrayList();

        arrayList.add(2);
        arrayList.add("2");

        System.out.println((arrayList.get(0) instanceof Object));
        System.out.println((arrayList.get(1) instanceof String));
    }
}
