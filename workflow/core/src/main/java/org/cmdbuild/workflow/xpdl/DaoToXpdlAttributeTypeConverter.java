/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.xpdl;

import org.cmdbuild.dao.entrytype.attributetype.BooleanAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.CMAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.CharAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DecimalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DoubleAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.FileAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.FormulaAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IntegerAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IpAddressAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.JsonAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LinkAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LongAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.RegclassAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TextAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TimeAttributeType;

public class DaoToXpdlAttributeTypeConverter implements CMAttributeTypeVisitor {

    private StandardAndCustomTypes xpdlType;

    public StandardAndCustomTypes convertType(CardAttributeType<?> type) {
        type.accept(this);
        return xpdlType;
    }

    @Override
    public void visit(BooleanAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.BOOLEAN;
    }

    @Override
    public void visit(CharAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.STRING;
    }

    @Override
    public void visit(DateTimeAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.DATETIME;
    }

    @Override
    public void visit(DateAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.DATETIME;
    }

    @Override
    public void visit(DecimalAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.FLOAT;
    }

    @Override
    public void visit(DoubleAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.FLOAT;
    }

    @Override
    public void visit(RegclassAttributeType attributeType) {
        xpdlType = null;
    }

    @Override
    public void visit(ForeignKeyAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.REFERENCE;
    }

    @Override
    public void visit(IntegerAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.INTEGER;
    }

    @Override
    public void visit(LongAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.INTEGER;
    }

    @Override
    public void visit(IpAddressAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.STRING;
    }

    @Override
    public void visit(LookupAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.LOOKUP;
    }

    @Override
    public void visit(ReferenceAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.REFERENCE;
    }

    @Override
    public void visit(StringAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.STRING;
    }

    @Override
    public void visit(JsonAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.STRING;
    }

    @Override
    public void visit(TextAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.STRING;
    }

    @Override
    public void visit(TimeAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.DATETIME;
    }

    @Override
    public void visit(StringArrayAttributeType stringArrayAttributeType) {
        xpdlType = null; // TODO verity this
    }

    @Override
    public void visit(FormulaAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.STRING;
    }

    @Override
    public void visit(LookupArrayAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.LOOKUPARRAY;
    }

    @Override
    public void visit(FileAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.REFERENCE;
    }

    @Override
    public void visit(LinkAttributeType attributeType) {
        xpdlType = StandardAndCustomTypes.STRING;
    }

}
