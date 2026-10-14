package org.cmdbuild.menu;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.menu.MenuItemType.ROOT;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;

public class MenuTreeNodeImpl implements MenuTreeNode {

    private final MenuItemType type;
    private final String description, target, code, targetDescription;
    private final List<MenuTreeNode> children;

    private MenuTreeNodeImpl(MenuTreeNodeImplBuilder builder) {
        this.type = checkNotNull(builder.type);
        this.description = trimToEmpty(builder.description);
        this.targetDescription = trimToEmpty(builder.targetDescription);
        this.target = trimToNull(builder.target);
        if (equal(type, ROOT)) {
            this.code = "ROOT";
        } else {
            this.code = checkNotBlank(builder.code);
        }
        this.children = ImmutableList.copyOf(checkNotNull(builder.children));
        List<String> codes = getDescendantsAndSelf().stream().map(MenuTreeNode::getCode).collect(toList());
        checkArgument(codes.size() == set(codes).size(), "duplicate menu node codes detected = %s", lazyString(() -> codes.stream().filter(c -> Collections.frequency(codes, c) > 1).collect(joining(", "))));
    }

    @Override
    public MenuItemType getType() {
        return type;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public String getTargetDescription() {
        return targetDescription;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public List<MenuTreeNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return "MenuTreeNode{type=" + type + ", description=" + description + ", objectType=" + target + ", code=" + code + '}';
    }

    public static MenuTreeNodeImplBuilder builder() {
        return new MenuTreeNodeImplBuilder();
    }

    public static MenuTreeNodeImpl buildRoot(List<MenuTreeNode> children) {
        return new MenuTreeNodeImplBuilder()
                .withType(ROOT)
                .withDescription("ROOT")
                .withChildren(children)
                .build();
    }

    public static MenuTreeNodeImplBuilder copyOf(MenuTreeNode source) {
        return new MenuTreeNodeImplBuilder()
                .withType(source.getType())
                .withDescription(source.getDescription())
                .withTarget(source.getTarget())
                .withCode(source.getCode())
                .withChildren(source.getChildren())
                .withTargetDescription(source.getTargetDescription());
    }

    public static class MenuTreeNodeImplBuilder implements Builder<MenuTreeNodeImpl, MenuTreeNodeImplBuilder> {

        private MenuItemType type;
        private String description, targetDescription;
        private String target;
        private String code;
        private List<MenuTreeNode> children = list();

        public MenuTreeNodeImplBuilder withType(MenuItemType type) {
            this.type = type;
            return this;
        }

        public MenuTreeNodeImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public MenuTreeNodeImplBuilder withTargetDescription(String targetDescription) {
            this.targetDescription = targetDescription;
            return this;
        }

        public MenuTreeNodeImplBuilder withTarget(String objectType) {
            this.target = objectType;
            return this;
        }

        public MenuTreeNodeImplBuilder withCode(String uniqueIdentifier) {
            this.code = uniqueIdentifier;
            return this;
        }

        public MenuTreeNodeImplBuilder withChildren(List<MenuTreeNode> children) {
            this.children = children;
            return this;
        }

        public MenuTreeNodeImplBuilder addChild(MenuTreeNode child) {
            children.add(child);
            return this;
        }

        @Override
        public MenuTreeNodeImpl build() {
            return new MenuTreeNodeImpl(this);
        }

    }
}
