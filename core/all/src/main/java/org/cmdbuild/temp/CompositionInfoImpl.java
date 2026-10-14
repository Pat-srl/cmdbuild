/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.temp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import java.util.List;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class CompositionInfoImpl implements CompositionInfo {

    private final List<Long> parts;
    private final String hash;
    private final long size;

    public CompositionInfoImpl(@JsonProperty("parts") List<Long> parts, @JsonProperty("hash") String hash, @JsonProperty("size") Long size) {
        this.parts = ImmutableList.copyOf(parts);
        this.hash = checkNotBlank(hash);
        this.size = size;
    }

    @Override
    public List<Long> getParts() {
        return parts;
    }

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public long getSize() {
        return size;
    }

}
