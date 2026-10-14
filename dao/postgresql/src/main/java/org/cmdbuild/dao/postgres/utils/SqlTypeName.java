package org.cmdbuild.dao.postgres.utils;

/**
 * Missing DAO types: Lookup, Reference, ForeignKey
 *
 * Missing SQL types: POINT, LINESTRING, POLYGON (use sqlToJavaValue)
 *
 * Not used: regclass, bytea, _int4, _varchar
 */
public enum SqlTypeName {

	bool,
	date,
	float8,
	float4,
	inet,
	int4,
	_int4,
	int8,
	_int8,
	numeric,
	regclass,
	_regclass,
	text,
	time,
	timestamp,
	timestamptz,
	interval,
	varchar,
	_varchar,
	jsonb,
	bpchar,
	bytea,
	_bytea;

}
