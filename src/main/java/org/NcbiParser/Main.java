package org.NcbiParser;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Ncbi ncbi = null;
        boolean error = false;
        try {
            ncbi = new Ncbi();
            var ov = ncbi.overview();
            for (var line : ov) {
                for (var col : line) {
                    System.out.printf("%20s ", col);
                }
                System.out.printf("\n");
            }
        } catch (Exception e) {
            error = true;
            e.printStackTrace();
        } finally {
            System.exit(error ? 1 : 0);
        }
    }
}
