package com.android.attendance.util;

import java.util.HashMap;
import java.util.Arrays;
import java.util.List;

public class SubjectManager {
    private static final HashMap<String, List<String>> subjectMap = new HashMap<>();
    
    static {
        // IT 2nd Year subjects
        subjectMap.put("IT_2Y", Arrays.asList(
            "M3", "DSA", "OOPS", "COA", "DSD", "DS", "DAA", "SE", "Eco", "DDC"
        ));
        
        // IT 3rd Year subjects
        subjectMap.put("IT_3Y", Arrays.asList(
            "CN", "TOC", "OS", "DAA", "AI", "DC", "DBMS", "WE", "CD", "ACN"
        ));
    }
    
    public static List<String> getSubjectsForDepartmentAndYear(String department, String year) {
        String key = department + "_" + year;
        return subjectMap.getOrDefault(key, Arrays.asList());
    }
} 