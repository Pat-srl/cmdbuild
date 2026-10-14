package org.cmdbuild.dao.utils;

import org.cmdbuild.common.Constants;
import static org.cmdbuild.utils.date.CmDateUtils.toJavaDate;
import org.cmdbuild.common.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.entrytype.attributetype.BooleanAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ByteArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.CMAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.CharAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DecimalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DoubleAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.RegclassAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IntegerAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IpAddressAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TextAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TimeAttributeType;
import org.joda.time.DateTime;
import static org.joda.time.format.DateTimeFormat.forPattern;
import org.joda.time.format.DateTimeFormatter;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LongAttributeType;

public class WsAttributeValueConvertingVisitor implements CMAttributeTypeVisitor {

	public static final DateTimeFormatter DATE_TIME_FORMATTER = forPattern(Constants.DATETIME_PRINTING_PATTERN);
	public static final DateTimeFormatter TIME_FORMATTER = forPattern(Constants.TIME_PRINTING_PATTERN);
	public static final DateTimeFormatter DATE_FORMATTER = forPattern(Constants.DATE_PRINTING_PATTERN);

	protected final Object value;
	protected final CardAttributeType<?> type;
//	protected final TranslationFacade translationFacade;
	protected Object convertedValue;
//	protected final LookupSerializer lookupSerializer;

	public WsAttributeValueConvertingVisitor(CardAttributeType<?> type, Object value) {
		this.value = value;
		this.type = type;
		this.convertedValue = null;
//		this.translationFacade = translationFacade;
//		this.lookupSerializer = lookupSerializer;
	}

	@Override
	public void visit(RegclassAttributeType attributeType) {
		throw new UnsupportedOperationException("regclasses not supported");
	}

	@Override
	public void visit(LookupAttributeType attributeType) {
		throw new UnsupportedOperationException("lookups not supported");
	}

	@Override
	public void visit(ReferenceAttributeType attributeType) {
		throw new UnsupportedOperationException("references not supported");
	}

	@Override
	public void visit(BooleanAttributeType attributeType) {
		convertedValue = value;
	}

	@Override
	public void visit(LongAttributeType attributeType) {
		convertedValue = value;
	}

	@Override
	public void visit(CharAttributeType attributeType) {
		convertedValue = value;
	}

	@Override
	public void visit(DateTimeAttributeType attributeType) {
		if (value != null) {
			convertedValue = DATE_TIME_FORMATTER.print(new DateTime(toJavaDate(value)));
		}
	}

	@Override
	public void visit(DateAttributeType attributeType) {
		if (value != null) {
			convertedValue = DATE_FORMATTER.print(new DateTime(toJavaDate(value)));
		}
	}

	@Override
	public void visit(TimeAttributeType attributeType) {
		if (value != null) {
			convertedValue = TIME_FORMATTER.print(new DateTime(toJavaDate(value)));
		}
	}

	@Override
	public void visit(DecimalAttributeType attributeType) {
		convertedValue = value;
	}

	@Override
	public void visit(DoubleAttributeType attributeType) {
		convertedValue = value;
	}

	@Override
	public void visit(ForeignKeyAttributeType attributeType) {
		if (value instanceof IdAndDescriptionImpl) {
			IdAndDescriptionImpl cardReference = IdAndDescriptionImpl.class.cast(value);
			convertedValue = cardReference.getDescription();
		} else {
			convertedValue = value;
		}
	}

	@Override
	public void visit(IntegerAttributeType attributeType) {
		convertedValue = value;
	}

	@Override
	public void visit(IpAddressAttributeType attributeType) {
		convertedValue = value;
	}

	@Override
	public void visit(StringArrayAttributeType attributeType) {
		convertedValue = value;
	}

	@Override
	public void visit(StringAttributeType attributeType) {
		convertedValue = value;
	}

	@Override
	public void visit(TextAttributeType attributeType) {
		convertedValue = value;
	}

	@Override
	public void visit(ByteArrayAttributeType attributeType) {
		convertedValue = value;
	}

	public Object convertValue() {
		type.accept(this);
		return convertedValue;
	}

}
