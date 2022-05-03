package org.NcbiParser;


/*
Classe Parser générale
 */
interface Parser {
    // Commence le parsing
    public boolean parse_into(String outDirectory);
}