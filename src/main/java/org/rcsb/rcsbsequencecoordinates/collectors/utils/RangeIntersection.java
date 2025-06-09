/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.utils;

import org.bson.Document;
import org.rcsb.utils.Range;

import java.util.List;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/7/24, Wednesday
 **/
public interface RangeIntersection {
    List<Document> getRegions(Document root);
    int getRegionBegin(Document region);
    int getRegionEnd(Document region);
    Document applyRange(Range range, Document root);
}
