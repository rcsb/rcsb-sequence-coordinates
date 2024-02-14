/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.utils;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/13/24, Tuesday
 **/

public class SequenceSymbol {

    public static int getIndex(Character aa){
        if(aa == 'A'){
            return 0;
        } else if (aa == 'R') {
            return 1;
        } else if (aa == 'N') {
            return 2;
        } else if (aa == 'D') {
            return 3;
        } else if (aa == 'C') {
            return 4;
        } else if (aa == 'E') {
            return 5;
        } else if (aa == 'Q') {
            return 6;
        } else if (aa == 'G') {
            return 7;
        } else if (aa == 'H') {
            return 8;
        } else if (aa == 'I') {
            return 9;
        } else if (aa == 'L') {
            return 10;
        } else if (aa == 'K') {
            return 11;
        } else if (aa == 'M') {
            return 12;
        } else if (aa == 'F') {
            return 13;
        } else if (aa == 'P') {
            return 14;
        } else if (aa == 'S') {
            return 15;
        } else if (aa == 'T') {
            return 16;
        } else if (aa == 'W') {
            return 17;
        } else if (aa == 'Y') {
            return 18;
        } else if (aa == 'V') {
            return 19;
        } else if (aa == 'U') {
            return 20;
        } else if (aa == 'X') {
            return 21;
        } else if (aa == '-') {
            return 22;
        }
        throw new RuntimeException("Unknown sequence character " + aa);
    }

    public static Character getChar(int aa){
        if(aa == 0){
            return 'A';
        } else if (aa == 1) {
            return 'R';
        } else if (aa == 2) {
            return 'N';
        } else if (aa == 3) {
            return 'D';
        } else if (aa == 4) {
            return 'C';
        } else if (aa == 5) {
            return 'E';
        } else if (aa == 6) {
            return 'Q';
        } else if (aa == 7) {
            return 'G';
        } else if (aa == 8) {
            return 'H';
        } else if (aa == 9) {
            return 'I';
        } else if (aa == 10) {
            return 'L';
        } else if (aa == 11) {
            return 'K';
        } else if (aa == 12) {
            return 'M';
        } else if (aa == 13) {
            return 'F';
        } else if (aa == 14) {
            return 'P';
        } else if (aa == 15) {
            return 'S';
        } else if (aa == 16) {
            return 'T';
        } else if (aa == 17) {
            return 'W';
        } else if (aa == 18) {
            return 'Y';
        } else if (aa == 19) {
            return 'V';
        } else if (aa == 20) {
            return 'U';
        } else if (aa == 21) {
            return 'X';
        } else if (aa == 22) {
            return '-';
        }
        throw new RuntimeException("Unknown sequence character " + aa);
    }

    public static int getLength(){
        return 23;
    }

}

