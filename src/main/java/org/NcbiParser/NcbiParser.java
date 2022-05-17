package org.NcbiParser;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class NcbiParser {
    public static ArrayList<ArrayList<String>> parseFile(InputStream file, ArrayList<String> columns) throws IOException {
        return parseFile(file, columns, 0);
    }
    public static ArrayList<ArrayList<String>> parseFile(InputStream file, ArrayList<String> columns, int skip_head) throws IOException {
        try {
            var ret = new ArrayList<ArrayList<String>>();
            var br = new BufferedReader(new InputStreamReader(file));

            // get first line to extract columns
            var line = br.readLine();
            for (int i = 0; i < skip_head; ++i)
                line = br.readLine();
            var header = line.split("\t");
            var col_idx = new ArrayList<Integer>();
            for (int j = 0; j < columns.size(); ++j) {
                for (int i = 0; i < header.length; i++) {
                    if (header[i].contentEquals(columns.get(j))) {
                        col_idx.add(i);
                    }
                }
            }

            return extractColumnsFromIndex(br, col_idx);
        } catch (Throwable e) {
            throw new IOException("Bad NCBI file: " + e.getMessage());
        }
    }

    public static ArrayList<ArrayList<String>> parseFile(InputStream file, int[] columns, int skip_head) throws IOException {
        try {
            var ret = new ArrayList<ArrayList<String>>();
            var br = new BufferedReader(new InputStreamReader(file));

            // get first line to extract columns
            var line = br.readLine();
            for (int i = 0; i < skip_head; ++i)
                line = br.readLine();

            var arr = (ArrayList<Integer>) Arrays.stream(columns).boxed().collect(Collectors.toList()); // bruh
            return extractColumnsFromIndex(br, arr);
        } catch (Throwable e) {
            throw new IOException("Bad NCBI file: " + e.getMessage());
        }
    }

    private static ArrayList<ArrayList<String>> extractColumnsFromIndex(BufferedReader br, ArrayList<Integer> col_idx) throws IOException {
        String line;
        int max = Collections.max(col_idx);
        ArrayList<ArrayList<String>> ret = new ArrayList<ArrayList<String>>();
        // parse full file and extract columns
        while ((line = br.readLine()) != null) {
            var split = line.split("\t");
            var l = new ArrayList<String>();
            if (split.length <= max)
                continue;
            for (var idx : col_idx) {
                l.add(split[idx]);
            }
            ret.add(l);
        }
        return ret;
    }
}
