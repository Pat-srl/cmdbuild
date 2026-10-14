package org.cmdbuild.api.fluent;

import java.util.Optional;
import java.util.function.Supplier;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import org.cmdbuild.api.fluent.Attachment;

public class LazyAttachmentImpl implements Attachment {

    private final String name;
    private final String description;
    private final String category;
    private final Supplier<String> url;
    private final Supplier<DataSource> data;

    public LazyAttachmentImpl(String name, String description, String category, @Nullable Supplier<String> url, @Nullable Supplier<DataSource> data) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.url = url;
        this.data = data;
    }

    @Override
    @Nullable
    public String getUrl() {
        return Optional.ofNullable(url).map(Supplier::get).orElse(null);
    }

    @Override
    @Nullable
    public DataSource getData() {
        return Optional.ofNullable(data).map(Supplier::get).orElse(null);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getCategory() {
        return category;
    }

}
