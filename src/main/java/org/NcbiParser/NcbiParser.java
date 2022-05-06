package org.NcbiParser;

import java.io.*;
import java.util.*;

public class NcbiParser {
    public static ArrayList<ArrayList<String>> parseFile(InputStream file, List<String> columns) throws IOException {
        try {
            var ret = new ArrayList<ArrayList<String>>();
            var br = new BufferedReader(new InputStreamReader(file));

            // get first line to extract columns
            var line = br.readLine();
            var header = line.split("\t");
            var col_idx = new ArrayList<Integer>();
            for (int j = 0; j < columns.size(); ++j) {
                for (int i = 0; i < header.length; i++) {
                    if (header[i].contentEquals(columns.get(j))) {
                        col_idx.add(i);
                    }
                }
            }

            int max = Collections.max(col_idx);

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
        } catch (Throwable e) {
            throw new IOException("Bad NCBI file: " + e.getMessage());
        }
    }

    /*public static HashMap<String, String> preparse_ncs(String raw_ncs) {
        HashMap<String, String> ret = new HashMap<String, String>();
        var split_ncs = raw_ncs.split(";");
        for (var potential_nc : split_ncs) {
            var split_nc = potential_nc.split(":");
            var split_id = split_nc[1].split("/");
            if (split_id[1].length() != 0) // id
                ret.put(split_id[1], split_id[0]);
        }
        return ret;
    }*/
}
