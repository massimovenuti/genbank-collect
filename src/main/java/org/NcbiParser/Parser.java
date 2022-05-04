package org.NcbiParser;


import org.biojava.nbio.core.exceptions.CompoundNotFoundException;

import java.io.IOException;

/*
Classe Parser générale
 */
interface Parser {
    // Commence le parsing
    public boolean parse_into(String outDirectory);
}