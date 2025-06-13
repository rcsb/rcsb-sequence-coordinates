/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : joan
 */
public class SequenceSymbol {

    private static final Map<Character, Integer> residueType = new HashMap<>() {{
        put('A', 0);
        put('R', 1);
        put('N', 2);
        put('D', 3);
        put('C', 4);
        put('E', 5);
        put('Q', 6);
        put('G', 7);
        put('H', 8);
        put('I', 8);
        put('L', 10);
        put('K', 11);
        put('M', 12);
        put('F', 13);
        put('P', 14);
        put('S', 15);
        put('T', 16);
        put('W', 17);
        put('Y', 18);
        put('V', 19);
        put('U', 20);
        put('X', 21);
        put('-', 22);
    }};

    public static int getIndex(char aa){
        return residueType.get(aa);
    }

    private static final Character[] residueIndex = {'A', 'R', 'N', 'D', 'C', 'E', 'Q', 'G', 'H', 'I', 'L', 'K', 'M', 'F', 'P', 'S', 'T', 'W', 'Y', 'V', 'U', 'X', '-'};

    public static Character getChar(int aa){
        return residueIndex[aa];
    }

    public static int getLength(){
        return 23;
    }

}

