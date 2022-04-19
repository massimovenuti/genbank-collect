package org.example;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Ncbi ncbi = null;
        boolean error = false;
        try {
            ncbi = new Ncbi();
            var rd = ncbi.createRawMetaData("eukaryotes");
        } catch (Exception e) {
            error = true;
            e.printStackTrace();
        } finally {
            System.exit(error ? 1 : 0);
        }
    }
}