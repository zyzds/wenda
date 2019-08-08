package cn.zyz.wenda.util;

import org.apache.commons.lang3.CharUtils;

import java.util.*;

public class SensitiveWord {
    private static Set<String> sensitiveWords = new HashSet<>(Arrays.asList(new String[]{
            "傻逼", "毛主席", "江泽民", "胡锦涛", "操你妈", "fuck", "motherfucker", "cocksucker"
    }));

    private static HashMap<Object, Object> initSensitiveWordsMap(Set<String> sensitiveWords) {
        if (sensitiveWords == null || sensitiveWords.isEmpty()) {
            throw new IllegalArgumentException("敏感词集合为空");
        }
        HashMap<Object, Object> sensitiveWordsMap = new HashMap<>(sensitiveWords.size());
        HashMap<Object, Object> currentMap;
        HashMap<Object, Object> subMap;
        Iterator<String> iterator = sensitiveWords.iterator();
        String currentWord;
        while (iterator.hasNext()) {
            currentWord = iterator.next();
            currentMap = sensitiveWordsMap;
            if (currentWord.length() < 2) {
                continue;
            }
            for (int i = 0; i < currentWord.length(); i++) {
                char c = currentWord.charAt(i);
                if (currentMap.containsKey(c)) {
                    currentMap = (HashMap<Object, Object>) currentMap.get(c);
                } else {
                    subMap = new HashMap<>();
                    if (i == currentWord.length() - 1) {
                        subMap.put("end", null);
                    }
                    currentMap.put(currentWord.charAt(i), subMap);
                    currentMap = subMap;
                }
            }
        }
        return sensitiveWordsMap;
    }

    public static boolean hasSensitiveWord(String s) {
        if (s == null) {
            return false;
        }
        String str = s.replaceAll("[^a-zA-Z\u4e00-\u9fa5.，,。？“”]+", "");
        if (str.length() < 2) {
            return false;
        }
        HashMap<Object, Object> map = initSensitiveWordsMap(sensitiveWords);
        HashMap<Object, Object> currentMap;
        for (int i = 0; i < str.length(); i++) {
            currentMap = map;
            for (int j = i; j < str.length(); j++) {
                char c = str.charAt(j);
                if (currentMap.containsKey(c)) {
                    currentMap = (HashMap<Object, Object>) currentMap.get(c);
                } else {
                    break;
                }
            }
            if (currentMap.containsKey("end")) {
                return true;
            }
        }
        return false;
    }

    public static String replaceSensitiveWord(String str) {
        if (str == null || str.length() < 2) {
            return str;
        }
        HashMap<Object, Object> map = initSensitiveWordsMap(sensitiveWords);
        HashMap<Object, Object> currentMap;
        char chars[] = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {
            currentMap = map;
            int j;
            for (j = i; j < str.length(); j++) {
                char c = str.charAt(j);
                if (!CharUtils.isAsciiAlphanumeric(c) && ((int) c < 0x2E80 || (int) c > 0x9FFF)) {
                    continue;
                }
                if (currentMap.containsKey(c)) {
                    currentMap = (HashMap<Object, Object>) currentMap.get(c);
                } else {
                    break;
                }
            }
            if (currentMap.containsKey("end")) {
                for (int k = i; k < j; k++) {
                    chars[k] = '*';
                }
                i = j - 1;
            }
        }
        return new String(chars);
    }
}