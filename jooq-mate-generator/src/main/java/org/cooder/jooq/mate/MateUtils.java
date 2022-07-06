package org.cooder.jooq.mate;

import java.util.ArrayList;
import java.util.List;

import org.jooq.tools.StringUtils;

public class MateUtils {
    public static String[] split(String str, String regrex) {
        List<String> ret = new ArrayList<>();
        String[] arr = str.split(regrex);
        for (String s : arr) {
            if(!StringUtils.isEmpty(s.trim())) {
                ret.add(s.trim());
            }
        }
        return ret.toArray(new String[0]);
    }

    public static String repeat(String base, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(base);
        }
        return sb.toString();
    }

    public static String firstNotEmpty(String... str) {
        for (String s : str) {
            if(!StringUtils.isEmpty(s)) {
                return s;
            }
        }
        return null;
    }
}
