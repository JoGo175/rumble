package org.rumbledb.types;

import org.rumbledb.api.Item;
import org.rumbledb.context.DynamicContext;
import org.rumbledb.context.Name;
import org.rumbledb.context.StaticContext;
import org.rumbledb.exceptions.ExceptionMetadata;
import org.rumbledb.exceptions.InvalidSchemaException;
import org.rumbledb.exceptions.OurBadException;

import java.util.*;

public class ArrayItemType implements ItemType {

    private static final long serialVersionUID = 1L;

    final static Set<FacetTypes> allowedFacets = new HashSet<>(
            Arrays.asList(
                FacetTypes.ENUMERATION,
                FacetTypes.CONTENT,
                FacetTypes.MINLENGTH,
                FacetTypes.MAXLENGTH
            )
    );

    final private Name name;
    final private ItemType baseType;
    private int typeTreeDepth;

    private ItemType content;
    private List<Item> enumeration;
    private Integer minLength, maxLength;

    ArrayItemType(
            Name name,
            ItemType baseType,
            ItemType content,
            Integer minLength,
            Integer maxLength,
            List<Item> enumeration
    ) {
        this.name = name;
        if (baseType == null) {
            throw new OurBadException("Unexpected error: baseType is null.");
        }
        this.baseType = baseType;
        this.content = content;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.enumeration = enumeration;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ItemType)) {
            return false;
        }
        return isEqualTo((ItemType) other);
    }

    @Override
    public boolean isArrayItemType() {
        return true;
    }

    @Override
    public boolean hasName() {
        return this.name != null;
    }

    @Override
    public Name getName() {
        return this.name;
    }

    @Override
    public int getTypeTreeDepth() {
        return this.typeTreeDepth;
    }

    @Override
    public boolean isUserDefined() {
        return !(this.equals(BuiltinTypesCatalogue.arrayItem));
    }

    @Override
    public boolean isPrimitive() {
        return this.equals(BuiltinTypesCatalogue.arrayItem);
    }

    @Override
    public ItemType getPrimitiveType() {
        return BuiltinTypesCatalogue.arrayItem;
    }

    @Override
    public ItemType getBaseType() {
        return this.baseType;
    }

    @Override
    public Set<FacetTypes> getAllowedFacets() {
        return allowedFacets;
    }

    @Override
    public List<Item> getEnumerationFacet() {
        return this.enumeration;
    }

    @Override
    public Integer getMinLengthFacet() {
        return this.minLength;
    }

    @Override
    public Integer getMaxLengthFacet() {
        return this.maxLength;
    }

    @Override
    public ItemType getArrayContentFacet() {
        return this.content;
    }

    @Override
    public String getIdentifierString() {
        if (this.hasName()) {
            return this.name.toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("#anonymous-array-base{");
        sb.append(this.baseType.getIdentifierString());
        sb.append("}");
        if (this.content != null) {
            sb.append("-content{");
            sb.append(this.content.getIdentifierString());
            sb.append("}");
        }
        if (this.enumeration != null) {
            sb.append("-enum{");
            String comma = "";
            for (Item item : this.enumeration) {
                sb.append(comma);
                sb.append(item.serialize());
                comma = ",";
            }
            sb.append("}");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        // consider add content and various stuff
        return ((this.name == null) ? "<anonymous>" : this.name.toString())
            + "(array of "
            + this.getArrayContentFacet()
            + ")";
    }

    @Override
    public boolean isDataFrameType() {
        return this.content.isDataFrameType();
    }

    @Override
    public void resolve(DynamicContext context, ExceptionMetadata metadata) {
        if (isResolved()) {
            return;
        }
        this.baseType.resolve(context, metadata);
        if (this.baseType.isArrayItemType()) {
            this.typeTreeDepth = baseType.getTypeTreeDepth() + 1;
            if (this.content == null) {
                this.content = this.baseType.getArrayContentFacet();
            } else {
                this.content.resolve(context, metadata);
            }
            if (this.minLength == null) {
                this.minLength = this.baseType.getMinLengthFacet();
            }
            if (this.maxLength == null) {
                this.maxLength = this.baseType.getMaxLengthFacet();
            }
            if (this.enumeration == null) {
                this.enumeration = this.baseType.getEnumerationFacet();
            }
        }
        checkSubtypeConsistency();
    }

    @Override
    public void resolve(StaticContext context, ExceptionMetadata metadata) {
        if (isResolved()) {
            return;
        }
        this.baseType.resolve(context, metadata);
        if (this.baseType.isArrayItemType()) {
            this.typeTreeDepth = baseType.getTypeTreeDepth() + 1;
            if (this.content == null) {
                this.content = this.baseType.getArrayContentFacet();
            } else {
                this.content.resolve(context, metadata);
            }
            if (this.minLength == null) {
                this.minLength = this.baseType.getMinLengthFacet();
            }
            if (this.maxLength == null) {
                this.maxLength = this.baseType.getMaxLengthFacet();
            }
            if (this.enumeration == null) {
                this.enumeration = this.baseType.getEnumerationFacet();
            }
        }
        checkSubtypeConsistency();
    }

    @Override
    public boolean isResolved() {
        return this.baseType.isResolved() && this.content != null && this.content.isResolved();
    }

    public void checkSubtypeConsistency() {
        if (!this.baseType.isArrayItemType()) {
            if (!this.equals(BuiltinTypesCatalogue.arrayItem)) {
                throw new InvalidSchemaException(
                        "Any user-defined array type must have an array type as its base type.",
                        null
                );
            }
            return;
        }
        if (!this.content.isSubtypeOf(this.baseType.getArrayContentFacet())) {
            throw new InvalidSchemaException(
                    "The content of an array subtype must be a subtype of the content of its base type.",
                    null
            );
        }
        if (this.baseType.getMinLengthFacet() != null && this.getMinLengthFacet() < this.baseType.getMinLengthFacet()) {
            throw new InvalidSchemaException(
                    "The minLength facet of an array subtype must be greater or equal to that of its base type.",
                    null
            );
        }
        if (this.baseType.getMaxLengthFacet() != null && this.getMaxLengthFacet() > this.baseType.getMaxLengthFacet()) {
            throw new InvalidSchemaException(
                    "The maxLength facet of an array subtype must be lesser or equal to that of its base type.",
                    null
            );
        }
    }

    @Override
    public boolean isCompatibleWithDataFrames() {
        return this.content.isCompatibleWithDataFrames();
    }

}
