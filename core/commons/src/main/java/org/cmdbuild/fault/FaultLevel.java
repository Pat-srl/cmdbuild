/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.fault;

public enum FaultLevel {
    FL_INFO(3), FL_WARNING(2), FL_ERROR(1), FL_NEVER(0);

    private final int level;

    private FaultLevel(int level) {
        this.level = level;
    }

    public int getIndex() {
        return level;
    }

    public boolean isWorseOrEqualTo(FaultLevel other) {
        return this.getIndex() <= other.getIndex();
    }

}
