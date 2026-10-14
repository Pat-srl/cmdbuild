/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.utils;

public interface PositionOf {

    boolean foundCard();

    long getPositionInPage();

    long getPositionInTable();

    long getPageOffset();

    long getActualOffset();
}
