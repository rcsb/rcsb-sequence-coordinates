/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.utils;

import org.bson.Document;
import org.rcsb.utils.Range;

import java.util.List;

import static org.rcsb.utils.RangeMethods.intersection;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/6/24, Tuesday
 **/
public class RangeIntersectionOperator {

    private final Range range;
    private final RangeIntersection rangeIntersection;

    public RangeIntersectionOperator(List<Integer> range, RangeIntersection rangeIntersection){
        if(range == null)
            this.range = new Range(0,-1);
        else if(range.size() != 2)
            throw new RuntimeException(String.format("Illegal range of size %s", range.size()));
        else
            this.range = new Range( range.get(0), range.get(1));
        this.rangeIntersection = rangeIntersection;
    }

    public boolean isConnected(Document alignment) {
        if(range.isEmpty())
            return true;
        return rangeIntersection.getRegions(alignment).stream().anyMatch(
                region -> !intersection(
                            range,
                            new Range(rangeIntersection.getRegionBegin(region), rangeIntersection.getRegionEnd(region))
                        ).isEmpty()
        );
    }

    public Document applyRange(Document root){
        if(range.isEmpty())
            return root;
        return rangeIntersection.applyRange(range, root);
    }

}
