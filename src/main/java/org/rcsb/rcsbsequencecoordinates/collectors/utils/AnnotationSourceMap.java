/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.utils;

import org.bson.Document;

import java.util.*;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/14/24, Wednesday
 **/
public class AnnotationSourceMap extends HashMap<String, AnnotationSourceMap.AnnotationTypeMap> {

    private final Map<String, Map<String, Map<String, Set<String>>>> targetMap = new HashMap<>();
    public List<Document> get(String source, String type, String name, String targetId){
        addTarget(source, type, name, targetId);
        this.putIfAbsent(source, new AnnotationTypeMap(targetMap.get(source)));
        this.get(source).putIfAbsent(type, new AnnotationTypeMap.AnnotationNameMap(targetMap.get(source).get(type)));
        this.get(source).get(type).putIfAbsent(name, new ArrayList<>());
        return  this.get(source).get(type).get(name);
    }

    private void addTarget(String source, String type, String name, String targetId){
        targetMap.putIfAbsent(source, new HashMap<>());
        targetMap.get(source).putIfAbsent(type, new HashMap<>());
        targetMap.get(source).get(type).putIfAbsent(name, new HashSet<>());
        targetMap.get(source).get(type).get(name).add(targetId);
    }

    public static class AnnotationTypeMap extends HashMap<String, AnnotationTypeMap.AnnotationNameMap> {
        private final Map<String, Map<String, Set<String>>> targetMap;
        private AnnotationTypeMap(Map<String, Map<String, Set<String>>> targetMap){
            this.targetMap = targetMap;
        }
        public static class AnnotationNameMap extends HashMap<String, List<Document>> {
            private final Map<String, Set<String>> targetMap;
            private AnnotationNameMap(Map<String, Set<String>> targetMap){
                this.targetMap = targetMap;
            }
            public Set<String> getTargets(String name){
                return targetMap.get(name);
            }
        }
    }

}
