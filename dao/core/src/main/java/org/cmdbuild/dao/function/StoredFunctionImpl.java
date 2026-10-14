package org.cmdbuild.dao.function;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.Maps.uniqueIndex;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.AttributeMetadata;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwnerImpl;

import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.toList;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import static org.cmdbuild.dao.function.FunctionMetadata.CATEGORIES;
import static org.cmdbuild.dao.function.FunctionMetadata.MASTERTABLE;
import static org.cmdbuild.dao.function.FunctionMetadata.TAGS;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class StoredFunctionImpl implements StoredFunction {

    private final long id;
    private final String name;
    private final List<Attribute> inputParameters, outputParameters;
    private final boolean returnsSet;
    private final List<StoredFunctionCategory> categories;
    private final Map<String, Object> metadata;
    private final FunctionMetadata functionMetadata;
    private final Map<String, Attribute> attributesAsMap;

    private StoredFunctionImpl(FunctionBuilder builder) {
        this.name = checkNotBlank(builder.identifier);
        this.id = checkNotNullAndGtZero(builder.id);
        this.inputParameters = ImmutableList.copyOf(list(builder.inputParameters).map(a -> AttributeImpl.copyOf(a).withOwner(this).build()));
        this.returnsSet = builder.returnsSet;
        this.functionMetadata = checkNotNull(builder.functionMetadata);
        this.categories = toList(firstNonNull(builder.functionMetadata.getCategories(), emptyList()));
        this.metadata = ImmutableMap.of(CATEGORIES, functionMetadata.getCategories(), MASTERTABLE, functionMetadata.getMasterTable(), TAGS, functionMetadata.getTags());//TODO check this

        AtomicInteger index = new AtomicInteger(0);
        outputParameters = builder.outputParameters.stream().map(p -> {

            return (Attribute) AttributeImpl.builder()
                    .withName(p.getName())
                    .withOwner(this)
                    .withType(p.getType())
                    .withMeta(AttributeMetadataImpl.copyOf(p.getMetadata()).withIndex(index.getAndIncrement()).build())
                    .build();

        }).collect(toImmutableList());
        attributesAsMap = uniqueIndex(outputParameters, Attribute::getName);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, Attribute> getAllAttributesAsMap() {
        return attributesAsMap;
    }

    @Override
    public FunctionMetadata getMetadata() {
        return functionMetadata;
    }

    @Override
    public boolean returnsSet() {
        return returnsSet;
    }

    @Override
    public List<Attribute> getInputParameters() {
        return inputParameters;
    }

    @Override
    public List<Attribute> getOutputParameters() {
        return outputParameters;
    }

    @Override
    public Iterable<StoredFunctionCategory> getCategories() {
        return categories;
    }

    @Override
    public Map<String, Object> getMetadataExt() {
        return metadata;
    }

    @Override
    public String toString() {
        return "StoredFunction{" + "id=" + id + ", name=" + name + '}';
    }

    public static FunctionBuilder builder() {
        return new FunctionBuilder();
    }

    public static class FunctionBuilder implements Builder<StoredFunctionImpl, FunctionBuilder> {

        private String identifier;
        private Long id;
        private Boolean returnsSet;
        private FunctionMetadata functionMetadata;
        private final List<AttributeWithoutOwner> inputParameters = list();
        private final List<AttributeWithoutOwner> outputParameters = list();

        public FunctionBuilder withName(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public String getName() {
            return checkNotNull(identifier);
        }

        public FunctionBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public FunctionBuilder withReturnSet(Boolean returnsSet) {
            this.returnsSet = returnsSet;
            return this;
        }

        public FunctionBuilder withMetadata(FunctionMetadata functionMetadata) {
            this.functionMetadata = functionMetadata;
            return this;
        }

        public FunctionBuilder withInputParameter(String name, CardAttributeType<?> type) {
            inputParameters.add(AttributeWithoutOwnerImpl.builder().withName(name).withType(type).build());
            return this;
        }

        public FunctionBuilder withOutputParameter(String name, CardAttributeType<?> type, AttributeMetadata metadata) {
            outputParameters.add(AttributeWithoutOwnerImpl.builder().withName(name).withType(type).withMeta(metadata).build());
            return this;
        }

        public FunctionMetadata getFunctionMetadata() {
            return checkNotNull(functionMetadata);
        }

        @Override
        public StoredFunctionImpl build() {
            return new StoredFunctionImpl(this);
        }

    }

}
