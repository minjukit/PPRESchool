package org.tensorflow.lite.examples.classification;

import java.util.HashMap;

public class Level1 implements Level{

    @Override
    public float setFloatSize() {
        return 55.0f;
    }

    HashMap<String, String> phonicsMap = new HashMap<String, String>();

    public void setPhonicsMap() {
        phonicsMap.put("A", "에이");
        phonicsMap.put("B", "비");
        phonicsMap.put("C", "씨");
        phonicsMap.put("D", "디");
        phonicsMap.put("E", "이");
        phonicsMap.put("F", "에프");
        phonicsMap.put("G", "쥐");
        phonicsMap.put("H", "에이치");
        phonicsMap.put("I", "아이");
        phonicsMap.put("J", "제이");
        phonicsMap.put("K", "케이");
        phonicsMap.put("L", "엘");
        phonicsMap.put("M", "엠");
        phonicsMap.put("N", "엔");
        phonicsMap.put("O", "오");
        phonicsMap.put("P", "피");
        phonicsMap.put("Q", "큐");
        phonicsMap.put("R", "알");
        phonicsMap.put("S", "에스");
        phonicsMap.put("T", "티");
        phonicsMap.put("U", "유");
        phonicsMap.put("V", "브이");
        phonicsMap.put("W", "더블유");
        phonicsMap.put("X", "엑스");
        phonicsMap.put("Y", "와이");
        phonicsMap.put("Z", "지");
    }

    public HashMap<String, String> getPhonicsMap() {
        return phonicsMap;
    }
}
