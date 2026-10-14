// $ANTLR 3.5.2 org/cmdbuild/cql/CQL.g 2018-05-16 09:49:31
package org.cmdbuild.cql;

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class CQLLexer extends Lexer {
	public static final int EOF=-1;
	public static final int T__140=140;
	public static final int T__141=141;
	public static final int T__142=142;
	public static final int ALL=4;
	public static final int ALLOP=5;
	public static final int AND=6;
	public static final int ANDOP=7;
	public static final int ASC=8;
	public static final int AT=9;
	public static final int ATTRIBUTE=10;
	public static final int ATTRIBUTEAS=11;
	public static final int ATTRIBUTEID=12;
	public static final int ATTRIBUTENAME=13;
	public static final int ATTRIBUTES=14;
	public static final int BGN=15;
	public static final int BGNOP=16;
	public static final int BOOLFALSE=17;
	public static final int BOOLTRUE=18;
	public static final int BTW=19;
	public static final int BTWANDOP=20;
	public static final int BTWOP=21;
	public static final int CLASS=22;
	public static final int CLASSALIAS=23;
	public static final int CLASSDOMREF=24;
	public static final int CLASSID=25;
	public static final int CLASSREF=26;
	public static final int COLON=27;
	public static final int COMMA=28;
	public static final int CONT=29;
	public static final int CONTOP=30;
	public static final int DATE=31;
	public static final int DDGT=32;
	public static final int DEFAULT=33;
	public static final int DESC=34;
	public static final int DIGIT=35;
	public static final int DOM=36;
	public static final int DOMCARDS=37;
	public static final int DOMID=38;
	public static final int DOMMETA=39;
	public static final int DOMNAME=40;
	public static final int DOMOBJS=41;
	public static final int DOMOP=42;
	public static final int DOMREF=43;
	public static final int DOMREV=44;
	public static final int DOMREVOP=45;
	public static final int DOMTYPE=46;
	public static final int DOMVALUE=47;
	public static final int DOT=48;
	public static final int DQUOTE=49;
	public static final int END=50;
	public static final int ENDOP=51;
	public static final int EQ=52;
	public static final int EQOP=53;
	public static final int EXPR=54;
	public static final int FALSE=55;
	public static final int FIELD=56;
	public static final int FIELDID=57;
	public static final int FIELDOPERATOR=58;
	public static final int FIELDVALUE=59;
	public static final int FLOAT=60;
	public static final int FROM=61;
	public static final int FROMOP=62;
	public static final int FUNCTION=63;
	public static final int GROUP=64;
	public static final int GROUPBY=65;
	public static final int GROUPBYOP=66;
	public static final int GT=67;
	public static final int GTEQ=68;
	public static final int GTEQOP=69;
	public static final int GTOP=70;
	public static final int HISTORY=71;
	public static final int HISTORYOP=72;
	public static final int IN=73;
	public static final int INOP=74;
	public static final int INPUTVAL=75;
	public static final int INT=76;
	public static final int INVERSE=77;
	public static final int ISNOTNULL=78;
	public static final int ISNULL=79;
	public static final int LETTER=80;
	public static final int LGRAPH=81;
	public static final int LIMIT=82;
	public static final int LITBOOL=83;
	public static final int LITDATE=84;
	public static final int LITERAL=85;
	public static final int LITNUM=86;
	public static final int LITSTR=87;
	public static final int LITTIMESTAMP=88;
	public static final int LMTOP=89;
	public static final int LOOKUP=90;
	public static final int LOOKUPPARENT=91;
	public static final int LROUND=92;
	public static final int LSQUARE=93;
	public static final int LT=94;
	public static final int LTEQ=95;
	public static final int LTEQOP=96;
	public static final int LTOP=97;
	public static final int META=98;
	public static final int NAME=99;
	public static final int NATIVE=100;
	public static final int NATIVEELM=101;
	public static final int NEG=102;
	public static final int NOT=103;
	public static final int NOTBGN=104;
	public static final int NOTBTW=105;
	public static final int NOTCONT=106;
	public static final int NOTDOM=107;
	public static final int NOTDOMREF=108;
	public static final int NOTDOMREV=109;
	public static final int NOTEND=110;
	public static final int NOTEQ=111;
	public static final int NOTGROUP=112;
	public static final int NOTIN=113;
	public static final int NULLOP=114;
	public static final int NUMBER=115;
	public static final int OBJECTS=116;
	public static final int OFFSET=117;
	public static final int OFFSOP=118;
	public static final int OR=119;
	public static final int ORDERBY=120;
	public static final int ORDERELM=121;
	public static final int ORDEROP=122;
	public static final int OROP=123;
	public static final int QDGT=124;
	public static final int QUOTED_CHARACTER=125;
	public static final int RGRAPH=126;
	public static final int RROUND=127;
	public static final int RSQUARE=128;
	public static final int SELECT=129;
	public static final int SELECTOP=130;
	public static final int SIGN=131;
	public static final int SLASH=132;
	public static final int SQUOTE=133;
	public static final int TILDE=134;
	public static final int TIMESTAMP=135;
	public static final int TRUE=136;
	public static final int WHERE=137;
	public static final int WHEREOP=138;
	public static final int WS=139;

	// delegates
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public CQLLexer() {} 
	public CQLLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public CQLLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "org/cmdbuild/cql/CQL.g"; }

	// $ANTLR start "T__140"
	public final void mT__140() throws RecognitionException {
		try {
			int _type = T__140;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:4:8: ( 'ASC' )
			// org/cmdbuild/cql/CQL.g:4:10: 'ASC'
			{
			match("ASC"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__140"

	// $ANTLR start "T__141"
	public final void mT__141() throws RecognitionException {
		try {
			int _type = T__141;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:5:8: ( 'DESC' )
			// org/cmdbuild/cql/CQL.g:5:10: 'DESC'
			{
			match("DESC"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__141"

	// $ANTLR start "T__142"
	public final void mT__142() throws RecognitionException {
		try {
			int _type = T__142;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:6:8: ( 'parent()' )
			// org/cmdbuild/cql/CQL.g:6:10: 'parent()'
			{
			match("parent()"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__142"

	// $ANTLR start "SELECTOP"
	public final void mSELECTOP() throws RecognitionException {
		try {
			int _type = SELECTOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:267:2: ( 'SELECT' | 'select' )
			int alt1=2;
			int LA1_0 = input.LA(1);
			if ( (LA1_0=='S') ) {
				alt1=1;
			}
			else if ( (LA1_0=='s') ) {
				alt1=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 1, 0, input);
				throw nvae;
			}

			switch (alt1) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:267:4: 'SELECT'
					{
					match("SELECT"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:267:13: 'select'
					{
					match("select"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "SELECTOP"

	// $ANTLR start "FROMOP"
	public final void mFROMOP() throws RecognitionException {
		try {
			int _type = FROMOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:270:2: ( 'FROM' | 'from' )
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0=='F') ) {
				alt2=1;
			}
			else if ( (LA2_0=='f') ) {
				alt2=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 2, 0, input);
				throw nvae;
			}

			switch (alt2) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:270:4: 'FROM'
					{
					match("FROM"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:270:11: 'from'
					{
					match("from"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "FROMOP"

	// $ANTLR start "WHEREOP"
	public final void mWHEREOP() throws RecognitionException {
		try {
			int _type = WHEREOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:272:2: ( 'WHERE' | 'where' )
			int alt3=2;
			int LA3_0 = input.LA(1);
			if ( (LA3_0=='W') ) {
				alt3=1;
			}
			else if ( (LA3_0=='w') ) {
				alt3=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 3, 0, input);
				throw nvae;
			}

			switch (alt3) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:272:4: 'WHERE'
					{
					match("WHERE"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:272:12: 'where'
					{
					match("where"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WHEREOP"

	// $ANTLR start "HISTORYOP"
	public final void mHISTORYOP() throws RecognitionException {
		try {
			int _type = HISTORYOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:275:2: ( 'HISTORY' | 'history' )
			int alt4=2;
			int LA4_0 = input.LA(1);
			if ( (LA4_0=='H') ) {
				alt4=1;
			}
			else if ( (LA4_0=='h') ) {
				alt4=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 4, 0, input);
				throw nvae;
			}

			switch (alt4) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:275:4: 'HISTORY'
					{
					match("HISTORY"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:275:14: 'history'
					{
					match("history"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "HISTORYOP"

	// $ANTLR start "GROUPBYOP"
	public final void mGROUPBYOP() throws RecognitionException {
		try {
			int _type = GROUPBYOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:278:2: ( 'GROUP BY' | 'group by' )
			int alt5=2;
			int LA5_0 = input.LA(1);
			if ( (LA5_0=='G') ) {
				alt5=1;
			}
			else if ( (LA5_0=='g') ) {
				alt5=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 5, 0, input);
				throw nvae;
			}

			switch (alt5) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:278:4: 'GROUP BY'
					{
					match("GROUP BY"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:278:15: 'group by'
					{
					match("group by"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "GROUPBYOP"

	// $ANTLR start "ORDEROP"
	public final void mORDEROP() throws RecognitionException {
		try {
			int _type = ORDEROP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:280:2: ( 'ORDER BY' | 'order by' )
			int alt6=2;
			int LA6_0 = input.LA(1);
			if ( (LA6_0=='O') ) {
				alt6=1;
			}
			else if ( (LA6_0=='o') ) {
				alt6=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 6, 0, input);
				throw nvae;
			}

			switch (alt6) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:280:4: 'ORDER BY'
					{
					match("ORDER BY"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:280:15: 'order by'
					{
					match("order by"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ORDEROP"

	// $ANTLR start "LMTOP"
	public final void mLMTOP() throws RecognitionException {
		try {
			int _type = LMTOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:282:2: ( 'LIMIT' | 'limit' )
			int alt7=2;
			int LA7_0 = input.LA(1);
			if ( (LA7_0=='L') ) {
				alt7=1;
			}
			else if ( (LA7_0=='l') ) {
				alt7=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 7, 0, input);
				throw nvae;
			}

			switch (alt7) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:282:4: 'LIMIT'
					{
					match("LIMIT"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:282:12: 'limit'
					{
					match("limit"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LMTOP"

	// $ANTLR start "OFFSOP"
	public final void mOFFSOP() throws RecognitionException {
		try {
			int _type = OFFSOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:284:2: ( 'OFFSET' | 'offset' )
			int alt8=2;
			int LA8_0 = input.LA(1);
			if ( (LA8_0=='O') ) {
				alt8=1;
			}
			else if ( (LA8_0=='o') ) {
				alt8=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 8, 0, input);
				throw nvae;
			}

			switch (alt8) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:284:4: 'OFFSET'
					{
					match("OFFSET"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:284:13: 'offset'
					{
					match("offset"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OFFSOP"

	// $ANTLR start "BTWOP"
	public final void mBTWOP() throws RecognitionException {
		try {
			int _type = BTWOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:288:2: ( 'BETWEEN' | 'between' )
			int alt9=2;
			int LA9_0 = input.LA(1);
			if ( (LA9_0=='B') ) {
				alt9=1;
			}
			else if ( (LA9_0=='b') ) {
				alt9=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 9, 0, input);
				throw nvae;
			}

			switch (alt9) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:288:4: 'BETWEEN'
					{
					match("BETWEEN"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:288:14: 'between'
					{
					match("between"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "BTWOP"

	// $ANTLR start "BTWANDOP"
	public final void mBTWANDOP() throws RecognitionException {
		try {
			int _type = BTWANDOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:290:2: ( 'AND' | 'and' )
			int alt10=2;
			int LA10_0 = input.LA(1);
			if ( (LA10_0=='A') ) {
				alt10=1;
			}
			else if ( (LA10_0=='a') ) {
				alt10=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 10, 0, input);
				throw nvae;
			}

			switch (alt10) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:290:4: 'AND'
					{
					match("AND"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:290:10: 'and'
					{
					match("and"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "BTWANDOP"

	// $ANTLR start "INOP"
	public final void mINOP() throws RecognitionException {
		try {
			int _type = INOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:291:5: ( 'IN' | 'in' )
			int alt11=2;
			int LA11_0 = input.LA(1);
			if ( (LA11_0=='I') ) {
				alt11=1;
			}
			else if ( (LA11_0=='i') ) {
				alt11=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 11, 0, input);
				throw nvae;
			}

			switch (alt11) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:291:7: 'IN'
					{
					match("IN"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:291:12: 'in'
					{
					match("in"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "INOP"

	// $ANTLR start "LTEQOP"
	public final void mLTEQOP() throws RecognitionException {
		try {
			int _type = LTEQOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:294:2: ( '<=' )
			// org/cmdbuild/cql/CQL.g:294:4: '<='
			{
			match("<="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LTEQOP"

	// $ANTLR start "GTEQOP"
	public final void mGTEQOP() throws RecognitionException {
		try {
			int _type = GTEQOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:296:2: ( '>=' )
			// org/cmdbuild/cql/CQL.g:296:4: '>='
			{
			match(">="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "GTEQOP"

	// $ANTLR start "LTOP"
	public final void mLTOP() throws RecognitionException {
		try {
			int _type = LTOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:297:5: ( '<' )
			// org/cmdbuild/cql/CQL.g:297:7: '<'
			{
			match('<'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LTOP"

	// $ANTLR start "GTOP"
	public final void mGTOP() throws RecognitionException {
		try {
			int _type = GTOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:298:5: ( '>' )
			// org/cmdbuild/cql/CQL.g:298:7: '>'
			{
			match('>'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "GTOP"

	// $ANTLR start "CONTOP"
	public final void mCONTOP() throws RecognitionException {
		try {
			int _type = CONTOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:300:2: ( 'CONTAINS' | 'contains' | ':=:' )
			int alt12=3;
			switch ( input.LA(1) ) {
			case 'C':
				{
				alt12=1;
				}
				break;
			case 'c':
				{
				alt12=2;
				}
				break;
			case ':':
				{
				alt12=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 12, 0, input);
				throw nvae;
			}
			switch (alt12) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:300:4: 'CONTAINS'
					{
					match("CONTAINS"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:300:15: 'contains'
					{
					match("contains"); 

					}
					break;
				case 3 :
					// org/cmdbuild/cql/CQL.g:300:26: ':=:'
					{
					match(":=:"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CONTOP"

	// $ANTLR start "BGNOP"
	public final void mBGNOP() throws RecognitionException {
		try {
			int _type = BGNOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:302:2: ( 'BEGIN' | 'begin' | '=:' )
			int alt13=3;
			switch ( input.LA(1) ) {
			case 'B':
				{
				alt13=1;
				}
				break;
			case 'b':
				{
				alt13=2;
				}
				break;
			case '=':
				{
				alt13=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 13, 0, input);
				throw nvae;
			}
			switch (alt13) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:302:4: 'BEGIN'
					{
					match("BEGIN"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:302:12: 'begin'
					{
					match("begin"); 

					}
					break;
				case 3 :
					// org/cmdbuild/cql/CQL.g:302:20: '=:'
					{
					match("=:"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "BGNOP"

	// $ANTLR start "ENDOP"
	public final void mENDOP() throws RecognitionException {
		try {
			int _type = ENDOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:304:2: ( 'END' | 'end' | ':=' )
			int alt14=3;
			switch ( input.LA(1) ) {
			case 'E':
				{
				alt14=1;
				}
				break;
			case 'e':
				{
				alt14=2;
				}
				break;
			case ':':
				{
				alt14=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 14, 0, input);
				throw nvae;
			}
			switch (alt14) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:304:4: 'END'
					{
					match("END"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:304:10: 'end'
					{
					match("end"); 

					}
					break;
				case 3 :
					// org/cmdbuild/cql/CQL.g:304:16: ':='
					{
					match(":="); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ENDOP"

	// $ANTLR start "EQOP"
	public final void mEQOP() throws RecognitionException {
		try {
			int _type = EQOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:305:5: ( '=' )
			// org/cmdbuild/cql/CQL.g:305:7: '='
			{
			match('='); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "EQOP"

	// $ANTLR start "NULLOP"
	public final void mNULLOP() throws RecognitionException {
		try {
			int _type = NULLOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:307:2: ( 'NULL' | 'null' )
			int alt15=2;
			int LA15_0 = input.LA(1);
			if ( (LA15_0=='N') ) {
				alt15=1;
			}
			else if ( (LA15_0=='n') ) {
				alt15=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 15, 0, input);
				throw nvae;
			}

			switch (alt15) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:307:4: 'NULL'
					{
					match("NULL"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:307:11: 'null'
					{
					match("null"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NULLOP"

	// $ANTLR start "DOMOP"
	public final void mDOMOP() throws RecognitionException {
		try {
			int _type = DOMOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:310:6: ( 'domain' )
			// org/cmdbuild/cql/CQL.g:310:8: 'domain'
			{
			match("domain"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DOMOP"

	// $ANTLR start "DOMREVOP"
	public final void mDOMREVOP() throws RecognitionException {
		try {
			int _type = DOMREVOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:312:2: ( 'domainRev' )
			// org/cmdbuild/cql/CQL.g:312:4: 'domainRev'
			{
			match("domainRev"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DOMREVOP"

	// $ANTLR start "OBJECTS"
	public final void mOBJECTS() throws RecognitionException {
		try {
			int _type = OBJECTS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:314:2: ( 'objects' | 'OBJECTS' )
			int alt16=2;
			int LA16_0 = input.LA(1);
			if ( (LA16_0=='o') ) {
				alt16=1;
			}
			else if ( (LA16_0=='O') ) {
				alt16=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 16, 0, input);
				throw nvae;
			}

			switch (alt16) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:314:4: 'objects'
					{
					match("objects"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:314:14: 'OBJECTS'
					{
					match("OBJECTS"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OBJECTS"

	// $ANTLR start "META"
	public final void mMETA() throws RecognitionException {
		try {
			int _type = META;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:316:2: ( 'meta' | 'META' )
			int alt17=2;
			int LA17_0 = input.LA(1);
			if ( (LA17_0=='m') ) {
				alt17=1;
			}
			else if ( (LA17_0=='M') ) {
				alt17=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 17, 0, input);
				throw nvae;
			}

			switch (alt17) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:316:4: 'meta'
					{
					match("meta"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:316:11: 'META'
					{
					match("META"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "META"

	// $ANTLR start "ANDOP"
	public final void mANDOP() throws RecognitionException {
		try {
			int _type = ANDOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:319:7: ( 'AND' | 'and' | '&' )
			int alt18=3;
			switch ( input.LA(1) ) {
			case 'A':
				{
				alt18=1;
				}
				break;
			case 'a':
				{
				alt18=2;
				}
				break;
			case '&':
				{
				alt18=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 18, 0, input);
				throw nvae;
			}
			switch (alt18) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:319:9: 'AND'
					{
					match("AND"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:319:17: 'and'
					{
					match("and"); 

					}
					break;
				case 3 :
					// org/cmdbuild/cql/CQL.g:319:25: '&'
					{
					match('&'); 
					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ANDOP"

	// $ANTLR start "OROP"
	public final void mOROP() throws RecognitionException {
		try {
			int _type = OROP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:320:6: ( 'OR' | 'or' | '|' )
			int alt19=3;
			switch ( input.LA(1) ) {
			case 'O':
				{
				alt19=1;
				}
				break;
			case 'o':
				{
				alt19=2;
				}
				break;
			case '|':
				{
				alt19=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 19, 0, input);
				throw nvae;
			}
			switch (alt19) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:320:8: 'OR'
					{
					match("OR"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:320:15: 'or'
					{
					match("or"); 

					}
					break;
				case 3 :
					// org/cmdbuild/cql/CQL.g:320:22: '|'
					{
					match('|'); 
					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OROP"

	// $ANTLR start "ALLOP"
	public final void mALLOP() throws RecognitionException {
		try {
			int _type = ALLOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:322:7: ( '*' )
			// org/cmdbuild/cql/CQL.g:322:9: '*'
			{
			match('*'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ALLOP"

	// $ANTLR start "BOOLTRUE"
	public final void mBOOLTRUE() throws RecognitionException {
		try {
			int _type = BOOLTRUE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:325:2: ( 'TRUE' | 'true' | 't' )
			int alt20=3;
			int LA20_0 = input.LA(1);
			if ( (LA20_0=='T') ) {
				alt20=1;
			}
			else if ( (LA20_0=='t') ) {
				int LA20_2 = input.LA(2);
				if ( (LA20_2=='r') ) {
					alt20=2;
				}

				else {
					alt20=3;
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 20, 0, input);
				throw nvae;
			}

			switch (alt20) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:325:4: 'TRUE'
					{
					match("TRUE"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:325:13: 'true'
					{
					match("true"); 

					}
					break;
				case 3 :
					// org/cmdbuild/cql/CQL.g:325:22: 't'
					{
					match('t'); 
					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "BOOLTRUE"

	// $ANTLR start "BOOLFALSE"
	public final void mBOOLFALSE() throws RecognitionException {
		try {
			int _type = BOOLFALSE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:327:2: ( 'FALSE' | 'false' | 'f' )
			int alt21=3;
			int LA21_0 = input.LA(1);
			if ( (LA21_0=='F') ) {
				alt21=1;
			}
			else if ( (LA21_0=='f') ) {
				int LA21_2 = input.LA(2);
				if ( (LA21_2=='a') ) {
					alt21=2;
				}

				else {
					alt21=3;
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 21, 0, input);
				throw nvae;
			}

			switch (alt21) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:327:4: 'FALSE'
					{
					match("FALSE"); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:327:14: 'false'
					{
					match("false"); 

					}
					break;
				case 3 :
					// org/cmdbuild/cql/CQL.g:327:24: 'f'
					{
					match('f'); 
					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "BOOLFALSE"

	// $ANTLR start "LETTER"
	public final void mLETTER() throws RecognitionException {
		try {
			// org/cmdbuild/cql/CQL.g:329:17: ( ( 'a' .. 'z' | 'A' .. 'Z' ) )
			// org/cmdbuild/cql/CQL.g:
			{
			if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LETTER"

	// $ANTLR start "SIGN"
	public final void mSIGN() throws RecognitionException {
		try {
			// org/cmdbuild/cql/CQL.g:331:15: ( '+' | '-' )
			// org/cmdbuild/cql/CQL.g:
			{
			if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "SIGN"

	// $ANTLR start "DIGIT"
	public final void mDIGIT() throws RecognitionException {
		try {
			// org/cmdbuild/cql/CQL.g:333:16: ( '0' .. '9' )
			// org/cmdbuild/cql/CQL.g:
			{
			if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DIGIT"

	// $ANTLR start "DATE"
	public final void mDATE() throws RecognitionException {
		try {
			int _type = DATE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:335:5: ( ( QDGT | DDGT ) '/' DDGT '/' DDGT )
			// org/cmdbuild/cql/CQL.g:335:7: ( QDGT | DDGT ) '/' DDGT '/' DDGT
			{
			// org/cmdbuild/cql/CQL.g:335:7: ( QDGT | DDGT )
			int alt22=2;
			int LA22_0 = input.LA(1);
			if ( ((LA22_0 >= '0' && LA22_0 <= '9')) ) {
				int LA22_1 = input.LA(2);
				if ( ((LA22_1 >= '0' && LA22_1 <= '9')) ) {
					int LA22_2 = input.LA(3);
					if ( ((LA22_2 >= '0' && LA22_2 <= '9')) ) {
						alt22=1;
					}
					else if ( (LA22_2=='/') ) {
						alt22=2;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 22, 2, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 22, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 22, 0, input);
				throw nvae;
			}

			switch (alt22) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:335:8: QDGT
					{
					mQDGT(); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:335:13: DDGT
					{
					mDDGT(); 

					}
					break;

			}

			match('/'); 
			mDDGT(); 

			match('/'); 
			mDDGT(); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DATE"

	// $ANTLR start "TIMESTAMP"
	public final void mTIMESTAMP() throws RecognitionException {
		try {
			int _type = TIMESTAMP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:337:2: ( DATE 'T' DDGT COLON DDGT COLON DDGT )
			// org/cmdbuild/cql/CQL.g:337:4: DATE 'T' DDGT COLON DDGT COLON DDGT
			{
			mDATE(); 

			match('T'); 
			mDDGT(); 

			mCOLON(); 

			mDDGT(); 

			mCOLON(); 

			mDDGT(); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "TIMESTAMP"

	// $ANTLR start "QDGT"
	public final void mQDGT() throws RecognitionException {
		try {
			// org/cmdbuild/cql/CQL.g:340:2: ( '0' .. '9' '0' .. '9' '0' .. '9' '0' .. '9' )
			// org/cmdbuild/cql/CQL.g:340:4: '0' .. '9' '0' .. '9' '0' .. '9' '0' .. '9'
			{
			matchRange('0','9'); 
			matchRange('0','9'); 
			matchRange('0','9'); 
			matchRange('0','9'); 
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "QDGT"

	// $ANTLR start "DDGT"
	public final void mDDGT() throws RecognitionException {
		try {
			// org/cmdbuild/cql/CQL.g:342:2: ( '0' .. '9' '0' .. '9' )
			// org/cmdbuild/cql/CQL.g:342:4: '0' .. '9' '0' .. '9'
			{
			matchRange('0','9'); 
			matchRange('0','9'); 
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DDGT"

	// $ANTLR start "NUMBER"
	public final void mNUMBER() throws RecognitionException {
		try {
			int _type = NUMBER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:343:8: ( INT | FLOAT )
			int alt23=2;
			alt23 = dfa23.predict(input);
			switch (alt23) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:344:2: INT
					{
					mINT(); 

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:344:8: FLOAT
					{
					mFLOAT(); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NUMBER"

	// $ANTLR start "INT"
	public final void mINT() throws RecognitionException {
		try {
			// org/cmdbuild/cql/CQL.g:345:15: ( ( SIGN )? ( DIGIT )+ )
			// org/cmdbuild/cql/CQL.g:346:2: ( SIGN )? ( DIGIT )+
			{
			// org/cmdbuild/cql/CQL.g:346:2: ( SIGN )?
			int alt24=2;
			int LA24_0 = input.LA(1);
			if ( (LA24_0=='+'||LA24_0=='-') ) {
				alt24=1;
			}
			switch (alt24) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:
					{
					if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

			}

			// org/cmdbuild/cql/CQL.g:346:8: ( DIGIT )+
			int cnt25=0;
			loop25:
			while (true) {
				int alt25=2;
				int LA25_0 = input.LA(1);
				if ( ((LA25_0 >= '0' && LA25_0 <= '9')) ) {
					alt25=1;
				}

				switch (alt25) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt25 >= 1 ) break loop25;
					EarlyExitException eee = new EarlyExitException(25, input);
					throw eee;
				}
				cnt25++;
			}

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "INT"

	// $ANTLR start "FLOAT"
	public final void mFLOAT() throws RecognitionException {
		try {
			// org/cmdbuild/cql/CQL.g:347:16: ( INT '.' ( '0' .. '9' )+ )
			// org/cmdbuild/cql/CQL.g:348:2: INT '.' ( '0' .. '9' )+
			{
			mINT(); 

			match('.'); 
			// org/cmdbuild/cql/CQL.g:348:10: ( '0' .. '9' )+
			int cnt26=0;
			loop26:
			while (true) {
				int alt26=2;
				int LA26_0 = input.LA(1);
				if ( ((LA26_0 >= '0' && LA26_0 <= '9')) ) {
					alt26=1;
				}

				switch (alt26) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt26 >= 1 ) break loop26;
					EarlyExitException eee = new EarlyExitException(26, input);
					throw eee;
				}
				cnt26++;
			}

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "FLOAT"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:351:4: ( ( ' ' | '\\t' )+ )
			// org/cmdbuild/cql/CQL.g:352:2: ( ' ' | '\\t' )+
			{
			// org/cmdbuild/cql/CQL.g:352:2: ( ' ' | '\\t' )+
			int cnt27=0;
			loop27:
			while (true) {
				int alt27=2;
				int LA27_0 = input.LA(1);
				if ( (LA27_0=='\t'||LA27_0==' ') ) {
					alt27=1;
				}

				switch (alt27) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:
					{
					if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt27 >= 1 ) break loop27;
					EarlyExitException eee = new EarlyExitException(27, input);
					throw eee;
				}
				cnt27++;
			}

			 _channel=HIDDEN; 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WS"

	// $ANTLR start "LROUND"
	public final void mLROUND() throws RecognitionException {
		try {
			int _type = LROUND;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:354:2: ( '(' )
			// org/cmdbuild/cql/CQL.g:354:4: '('
			{
			match('('); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LROUND"

	// $ANTLR start "RROUND"
	public final void mRROUND() throws RecognitionException {
		try {
			int _type = RROUND;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:356:2: ( ')' )
			// org/cmdbuild/cql/CQL.g:356:4: ')'
			{
			match(')'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RROUND"

	// $ANTLR start "LSQUARE"
	public final void mLSQUARE() throws RecognitionException {
		try {
			int _type = LSQUARE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:358:2: ( '[' )
			// org/cmdbuild/cql/CQL.g:358:4: '['
			{
			match('['); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LSQUARE"

	// $ANTLR start "RSQUARE"
	public final void mRSQUARE() throws RecognitionException {
		try {
			int _type = RSQUARE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:360:2: ( ']' )
			// org/cmdbuild/cql/CQL.g:360:4: ']'
			{
			match(']'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RSQUARE"

	// $ANTLR start "LGRAPH"
	public final void mLGRAPH() throws RecognitionException {
		try {
			int _type = LGRAPH;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:362:2: ( '{' )
			// org/cmdbuild/cql/CQL.g:362:4: '{'
			{
			match('{'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LGRAPH"

	// $ANTLR start "RGRAPH"
	public final void mRGRAPH() throws RecognitionException {
		try {
			int _type = RGRAPH;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:364:2: ( '}' )
			// org/cmdbuild/cql/CQL.g:364:4: '}'
			{
			match('}'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RGRAPH"

	// $ANTLR start "TILDE"
	public final void mTILDE() throws RecognitionException {
		try {
			int _type = TILDE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:366:2: ( '~' )
			// org/cmdbuild/cql/CQL.g:366:4: '~'
			{
			match('~'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "TILDE"

	// $ANTLR start "NEG"
	public final void mNEG() throws RecognitionException {
		try {
			int _type = NEG;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:367:5: ( '!' )
			// org/cmdbuild/cql/CQL.g:367:7: '!'
			{
			match('!'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NEG"

	// $ANTLR start "DOT"
	public final void mDOT() throws RecognitionException {
		try {
			int _type = DOT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:368:5: ( '.' )
			// org/cmdbuild/cql/CQL.g:368:7: '.'
			{
			match('.'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DOT"

	// $ANTLR start "SQUOTE"
	public final void mSQUOTE() throws RecognitionException {
		try {
			int _type = SQUOTE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:370:2: ( '\\'' )
			// org/cmdbuild/cql/CQL.g:370:4: '\\''
			{
			match('\''); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "SQUOTE"

	// $ANTLR start "DQUOTE"
	public final void mDQUOTE() throws RecognitionException {
		try {
			int _type = DQUOTE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:372:2: ( '\"' )
			// org/cmdbuild/cql/CQL.g:372:4: '\"'
			{
			match('\"'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DQUOTE"

	// $ANTLR start "COLON"
	public final void mCOLON() throws RecognitionException {
		try {
			int _type = COLON;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:374:2: ( ':' )
			// org/cmdbuild/cql/CQL.g:374:4: ':'
			{
			match(':'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "COLON"

	// $ANTLR start "COMMA"
	public final void mCOMMA() throws RecognitionException {
		try {
			int _type = COMMA;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:376:2: ( ',' )
			// org/cmdbuild/cql/CQL.g:376:4: ','
			{
			match(','); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "COMMA"

	// $ANTLR start "AT"
	public final void mAT() throws RecognitionException {
		try {
			int _type = AT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:377:4: ( '@' | 'AS' | 'as' )
			int alt28=3;
			switch ( input.LA(1) ) {
			case '@':
				{
				alt28=1;
				}
				break;
			case 'A':
				{
				alt28=2;
				}
				break;
			case 'a':
				{
				alt28=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 28, 0, input);
				throw nvae;
			}
			switch (alt28) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:377:6: '@'
					{
					match('@'); 
					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:377:10: 'AS'
					{
					match("AS"); 

					}
					break;
				case 3 :
					// org/cmdbuild/cql/CQL.g:377:15: 'as'
					{
					match("as"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "AT"

	// $ANTLR start "SLASH"
	public final void mSLASH() throws RecognitionException {
		try {
			int _type = SLASH;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:379:2: ( '/' )
			// org/cmdbuild/cql/CQL.g:379:4: '/'
			{
			match('/'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "SLASH"

	// $ANTLR start "NAME"
	public final void mNAME() throws RecognitionException {
		try {
			int _type = NAME;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:382:2: ( LETTER ( DIGIT | LETTER | '_' | '#' )* )
			// org/cmdbuild/cql/CQL.g:382:4: LETTER ( DIGIT | LETTER | '_' | '#' )*
			{
			mLETTER(); 

			// org/cmdbuild/cql/CQL.g:382:11: ( DIGIT | LETTER | '_' | '#' )*
			loop29:
			while (true) {
				int alt29=2;
				int LA29_0 = input.LA(1);
				if ( (LA29_0=='#'||(LA29_0 >= '0' && LA29_0 <= '9')||(LA29_0 >= 'A' && LA29_0 <= 'Z')||LA29_0=='_'||(LA29_0 >= 'a' && LA29_0 <= 'z')) ) {
					alt29=1;
				}

				switch (alt29) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:
					{
					if ( input.LA(1)=='#'||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop29;
				}
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NAME"

	// $ANTLR start "LITERAL"
	public final void mLITERAL() throws RecognitionException {
		try {
			int _type = LITERAL;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:384:2: ( ( '\"' ( QUOTED_CHARACTER | '\\'' )* '\"' ) | ( '\\'' ( QUOTED_CHARACTER | '\"' )* '\\'' ) )
			int alt32=2;
			int LA32_0 = input.LA(1);
			if ( (LA32_0=='\"') ) {
				alt32=1;
			}
			else if ( (LA32_0=='\'') ) {
				alt32=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 32, 0, input);
				throw nvae;
			}

			switch (alt32) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:386:3: ( '\"' ( QUOTED_CHARACTER | '\\'' )* '\"' )
					{
					// org/cmdbuild/cql/CQL.g:386:3: ( '\"' ( QUOTED_CHARACTER | '\\'' )* '\"' )
					// org/cmdbuild/cql/CQL.g:386:5: '\"' ( QUOTED_CHARACTER | '\\'' )* '\"'
					{
					match('\"'); 
					// org/cmdbuild/cql/CQL.g:386:9: ( QUOTED_CHARACTER | '\\'' )*
					loop30:
					while (true) {
						int alt30=3;
						int LA30_0 = input.LA(1);
						if ( ((LA30_0 >= '\u0000' && LA30_0 <= '\t')||(LA30_0 >= '\u000B' && LA30_0 <= '\f')||(LA30_0 >= '\u000E' && LA30_0 <= '!')||(LA30_0 >= '#' && LA30_0 <= '&')||(LA30_0 >= '(' && LA30_0 <= '\uFFFF')) ) {
							alt30=1;
						}
						else if ( (LA30_0=='\'') ) {
							alt30=2;
						}

						switch (alt30) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:386:11: QUOTED_CHARACTER
							{
							mQUOTED_CHARACTER(); 

							}
							break;
						case 2 :
							// org/cmdbuild/cql/CQL.g:386:30: '\\''
							{
							match('\''); 
							}
							break;

						default :
							break loop30;
						}
					}

					match('\"'); 
					}

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:387:4: ( '\\'' ( QUOTED_CHARACTER | '\"' )* '\\'' )
					{
					// org/cmdbuild/cql/CQL.g:387:4: ( '\\'' ( QUOTED_CHARACTER | '\"' )* '\\'' )
					// org/cmdbuild/cql/CQL.g:387:6: '\\'' ( QUOTED_CHARACTER | '\"' )* '\\''
					{
					match('\''); 
					// org/cmdbuild/cql/CQL.g:387:11: ( QUOTED_CHARACTER | '\"' )*
					loop31:
					while (true) {
						int alt31=3;
						int LA31_0 = input.LA(1);
						if ( ((LA31_0 >= '\u0000' && LA31_0 <= '\t')||(LA31_0 >= '\u000B' && LA31_0 <= '\f')||(LA31_0 >= '\u000E' && LA31_0 <= '!')||(LA31_0 >= '#' && LA31_0 <= '&')||(LA31_0 >= '(' && LA31_0 <= '\uFFFF')) ) {
							alt31=1;
						}
						else if ( (LA31_0=='\"') ) {
							alt31=2;
						}

						switch (alt31) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:387:13: QUOTED_CHARACTER
							{
							mQUOTED_CHARACTER(); 

							}
							break;
						case 2 :
							// org/cmdbuild/cql/CQL.g:387:32: '\"'
							{
							match('\"'); 
							}
							break;

						default :
							break loop31;
						}
					}

					match('\''); 
					}

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LITERAL"

	// $ANTLR start "QUOTED_CHARACTER"
	public final void mQUOTED_CHARACTER() throws RecognitionException {
		try {
			int _type = QUOTED_CHARACTER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:390:6: ( (~ ( '\\'' | '\"' | '\\r' | '\\n' | '\\\\' ) ) | '\\\\' ( ( '\\'' | '\"' | 'n' | 'r' | 't' | 'b' | 'f' | '\\\\' ) ) )
			int alt33=2;
			int LA33_0 = input.LA(1);
			if ( ((LA33_0 >= '\u0000' && LA33_0 <= '\t')||(LA33_0 >= '\u000B' && LA33_0 <= '\f')||(LA33_0 >= '\u000E' && LA33_0 <= '!')||(LA33_0 >= '#' && LA33_0 <= '&')||(LA33_0 >= '(' && LA33_0 <= '[')||(LA33_0 >= ']' && LA33_0 <= '\uFFFF')) ) {
				alt33=1;
			}
			else if ( (LA33_0=='\\') ) {
				alt33=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 33, 0, input);
				throw nvae;
			}

			switch (alt33) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:390:8: (~ ( '\\'' | '\"' | '\\r' | '\\n' | '\\\\' ) )
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '&')||(input.LA(1) >= '(' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:391:8: '\\\\' ( ( '\\'' | '\"' | 'n' | 'r' | 't' | 'b' | 'f' | '\\\\' ) )
					{
					match('\\'); 
					if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "QUOTED_CHARACTER"

	// $ANTLR start "NATIVEELM"
	public final void mNATIVEELM() throws RecognitionException {
		try {
			int _type = NATIVEELM;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org/cmdbuild/cql/CQL.g:398:2: ( '/' (~ ( '/' ) )* '/' )
			// org/cmdbuild/cql/CQL.g:398:4: '/' (~ ( '/' ) )* '/'
			{
			match('/'); 
			// org/cmdbuild/cql/CQL.g:398:8: (~ ( '/' ) )*
			loop34:
			while (true) {
				int alt34=2;
				int LA34_0 = input.LA(1);
				if ( ((LA34_0 >= '\u0000' && LA34_0 <= '.')||(LA34_0 >= '0' && LA34_0 <= '\uFFFF')) ) {
					alt34=1;
				}

				switch (alt34) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '.')||(input.LA(1) >= '0' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop34;
				}
			}

			match('/'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NATIVEELM"

	@Override
	public void mTokens() throws RecognitionException {
		// org/cmdbuild/cql/CQL.g:1:8: ( T__140 | T__141 | T__142 | SELECTOP | FROMOP | WHEREOP | HISTORYOP | GROUPBYOP | ORDEROP | LMTOP | OFFSOP | BTWOP | BTWANDOP | INOP | LTEQOP | GTEQOP | LTOP | GTOP | CONTOP | BGNOP | ENDOP | EQOP | NULLOP | DOMOP | DOMREVOP | OBJECTS | META | ANDOP | OROP | ALLOP | BOOLTRUE | BOOLFALSE | DATE | TIMESTAMP | NUMBER | WS | LROUND | RROUND | LSQUARE | RSQUARE | LGRAPH | RGRAPH | TILDE | NEG | DOT | SQUOTE | DQUOTE | COLON | COMMA | AT | SLASH | NAME | LITERAL | QUOTED_CHARACTER | NATIVEELM )
		int alt35=55;
		alt35 = dfa35.predict(input);
		switch (alt35) {
			case 1 :
				// org/cmdbuild/cql/CQL.g:1:10: T__140
				{
				mT__140(); 

				}
				break;
			case 2 :
				// org/cmdbuild/cql/CQL.g:1:17: T__141
				{
				mT__141(); 

				}
				break;
			case 3 :
				// org/cmdbuild/cql/CQL.g:1:24: T__142
				{
				mT__142(); 

				}
				break;
			case 4 :
				// org/cmdbuild/cql/CQL.g:1:31: SELECTOP
				{
				mSELECTOP(); 

				}
				break;
			case 5 :
				// org/cmdbuild/cql/CQL.g:1:40: FROMOP
				{
				mFROMOP(); 

				}
				break;
			case 6 :
				// org/cmdbuild/cql/CQL.g:1:47: WHEREOP
				{
				mWHEREOP(); 

				}
				break;
			case 7 :
				// org/cmdbuild/cql/CQL.g:1:55: HISTORYOP
				{
				mHISTORYOP(); 

				}
				break;
			case 8 :
				// org/cmdbuild/cql/CQL.g:1:65: GROUPBYOP
				{
				mGROUPBYOP(); 

				}
				break;
			case 9 :
				// org/cmdbuild/cql/CQL.g:1:75: ORDEROP
				{
				mORDEROP(); 

				}
				break;
			case 10 :
				// org/cmdbuild/cql/CQL.g:1:83: LMTOP
				{
				mLMTOP(); 

				}
				break;
			case 11 :
				// org/cmdbuild/cql/CQL.g:1:89: OFFSOP
				{
				mOFFSOP(); 

				}
				break;
			case 12 :
				// org/cmdbuild/cql/CQL.g:1:96: BTWOP
				{
				mBTWOP(); 

				}
				break;
			case 13 :
				// org/cmdbuild/cql/CQL.g:1:102: BTWANDOP
				{
				mBTWANDOP(); 

				}
				break;
			case 14 :
				// org/cmdbuild/cql/CQL.g:1:111: INOP
				{
				mINOP(); 

				}
				break;
			case 15 :
				// org/cmdbuild/cql/CQL.g:1:116: LTEQOP
				{
				mLTEQOP(); 

				}
				break;
			case 16 :
				// org/cmdbuild/cql/CQL.g:1:123: GTEQOP
				{
				mGTEQOP(); 

				}
				break;
			case 17 :
				// org/cmdbuild/cql/CQL.g:1:130: LTOP
				{
				mLTOP(); 

				}
				break;
			case 18 :
				// org/cmdbuild/cql/CQL.g:1:135: GTOP
				{
				mGTOP(); 

				}
				break;
			case 19 :
				// org/cmdbuild/cql/CQL.g:1:140: CONTOP
				{
				mCONTOP(); 

				}
				break;
			case 20 :
				// org/cmdbuild/cql/CQL.g:1:147: BGNOP
				{
				mBGNOP(); 

				}
				break;
			case 21 :
				// org/cmdbuild/cql/CQL.g:1:153: ENDOP
				{
				mENDOP(); 

				}
				break;
			case 22 :
				// org/cmdbuild/cql/CQL.g:1:159: EQOP
				{
				mEQOP(); 

				}
				break;
			case 23 :
				// org/cmdbuild/cql/CQL.g:1:164: NULLOP
				{
				mNULLOP(); 

				}
				break;
			case 24 :
				// org/cmdbuild/cql/CQL.g:1:171: DOMOP
				{
				mDOMOP(); 

				}
				break;
			case 25 :
				// org/cmdbuild/cql/CQL.g:1:177: DOMREVOP
				{
				mDOMREVOP(); 

				}
				break;
			case 26 :
				// org/cmdbuild/cql/CQL.g:1:186: OBJECTS
				{
				mOBJECTS(); 

				}
				break;
			case 27 :
				// org/cmdbuild/cql/CQL.g:1:194: META
				{
				mMETA(); 

				}
				break;
			case 28 :
				// org/cmdbuild/cql/CQL.g:1:199: ANDOP
				{
				mANDOP(); 

				}
				break;
			case 29 :
				// org/cmdbuild/cql/CQL.g:1:205: OROP
				{
				mOROP(); 

				}
				break;
			case 30 :
				// org/cmdbuild/cql/CQL.g:1:210: ALLOP
				{
				mALLOP(); 

				}
				break;
			case 31 :
				// org/cmdbuild/cql/CQL.g:1:216: BOOLTRUE
				{
				mBOOLTRUE(); 

				}
				break;
			case 32 :
				// org/cmdbuild/cql/CQL.g:1:225: BOOLFALSE
				{
				mBOOLFALSE(); 

				}
				break;
			case 33 :
				// org/cmdbuild/cql/CQL.g:1:235: DATE
				{
				mDATE(); 

				}
				break;
			case 34 :
				// org/cmdbuild/cql/CQL.g:1:240: TIMESTAMP
				{
				mTIMESTAMP(); 

				}
				break;
			case 35 :
				// org/cmdbuild/cql/CQL.g:1:250: NUMBER
				{
				mNUMBER(); 

				}
				break;
			case 36 :
				// org/cmdbuild/cql/CQL.g:1:257: WS
				{
				mWS(); 

				}
				break;
			case 37 :
				// org/cmdbuild/cql/CQL.g:1:260: LROUND
				{
				mLROUND(); 

				}
				break;
			case 38 :
				// org/cmdbuild/cql/CQL.g:1:267: RROUND
				{
				mRROUND(); 

				}
				break;
			case 39 :
				// org/cmdbuild/cql/CQL.g:1:274: LSQUARE
				{
				mLSQUARE(); 

				}
				break;
			case 40 :
				// org/cmdbuild/cql/CQL.g:1:282: RSQUARE
				{
				mRSQUARE(); 

				}
				break;
			case 41 :
				// org/cmdbuild/cql/CQL.g:1:290: LGRAPH
				{
				mLGRAPH(); 

				}
				break;
			case 42 :
				// org/cmdbuild/cql/CQL.g:1:297: RGRAPH
				{
				mRGRAPH(); 

				}
				break;
			case 43 :
				// org/cmdbuild/cql/CQL.g:1:304: TILDE
				{
				mTILDE(); 

				}
				break;
			case 44 :
				// org/cmdbuild/cql/CQL.g:1:310: NEG
				{
				mNEG(); 

				}
				break;
			case 45 :
				// org/cmdbuild/cql/CQL.g:1:314: DOT
				{
				mDOT(); 

				}
				break;
			case 46 :
				// org/cmdbuild/cql/CQL.g:1:318: SQUOTE
				{
				mSQUOTE(); 

				}
				break;
			case 47 :
				// org/cmdbuild/cql/CQL.g:1:325: DQUOTE
				{
				mDQUOTE(); 

				}
				break;
			case 48 :
				// org/cmdbuild/cql/CQL.g:1:332: COLON
				{
				mCOLON(); 

				}
				break;
			case 49 :
				// org/cmdbuild/cql/CQL.g:1:338: COMMA
				{
				mCOMMA(); 

				}
				break;
			case 50 :
				// org/cmdbuild/cql/CQL.g:1:344: AT
				{
				mAT(); 

				}
				break;
			case 51 :
				// org/cmdbuild/cql/CQL.g:1:347: SLASH
				{
				mSLASH(); 

				}
				break;
			case 52 :
				// org/cmdbuild/cql/CQL.g:1:353: NAME
				{
				mNAME(); 

				}
				break;
			case 53 :
				// org/cmdbuild/cql/CQL.g:1:358: LITERAL
				{
				mLITERAL(); 

				}
				break;
			case 54 :
				// org/cmdbuild/cql/CQL.g:1:366: QUOTED_CHARACTER
				{
				mQUOTED_CHARACTER(); 

				}
				break;
			case 55 :
				// org/cmdbuild/cql/CQL.g:1:383: NATIVEELM
				{
				mNATIVEELM(); 

				}
				break;

		}
	}


	protected DFA23 dfa23 = new DFA23(this);
	protected DFA35 dfa35 = new DFA35(this);
	static final String DFA23_eotS =
		"\2\uffff\1\3\2\uffff";
	static final String DFA23_eofS =
		"\5\uffff";
	static final String DFA23_minS =
		"\1\53\1\60\1\56\2\uffff";
	static final String DFA23_maxS =
		"\3\71\2\uffff";
	static final String DFA23_acceptS =
		"\3\uffff\1\1\1\2";
	static final String DFA23_specialS =
		"\5\uffff}>";
	static final String[] DFA23_transitionS = {
			"\1\1\1\uffff\1\1\2\uffff\12\2",
			"\12\2",
			"\1\4\1\uffff\12\2",
			"",
			""
	};

	static final short[] DFA23_eot = DFA.unpackEncodedString(DFA23_eotS);
	static final short[] DFA23_eof = DFA.unpackEncodedString(DFA23_eofS);
	static final char[] DFA23_min = DFA.unpackEncodedStringToUnsignedChars(DFA23_minS);
	static final char[] DFA23_max = DFA.unpackEncodedStringToUnsignedChars(DFA23_maxS);
	static final short[] DFA23_accept = DFA.unpackEncodedString(DFA23_acceptS);
	static final short[] DFA23_special = DFA.unpackEncodedString(DFA23_specialS);
	static final short[][] DFA23_transition;

	static {
		int numStates = DFA23_transitionS.length;
		DFA23_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA23_transition[i] = DFA.unpackEncodedString(DFA23_transitionS[i]);
		}
	}

	protected class DFA23 extends DFA {

		public DFA23(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 23;
			this.eot = DFA23_eot;
			this.eof = DFA23_eof;
			this.min = DFA23_min;
			this.max = DFA23_max;
			this.accept = DFA23_accept;
			this.special = DFA23_special;
			this.transition = DFA23_transition;
		}
		@Override
		public String getDescription() {
			return "343:1: NUMBER : ( INT | FLOAT );";
		}
	}

	static final String DFA35_eotS =
		"\1\uffff\6\76\1\107\17\76\1\135\1\137\2\76\1\143\1\145\7\76\3\uffff\1"+
		"\76\1\162\1\164\1\73\12\uffff\1\177\1\u0081\2\uffff\1\u0084\2\uffff\1"+
		"\u0083\1\76\1\uffff\10\76\1\uffff\6\76\1\156\2\76\1\156\7\76\1\u0083\2"+
		"\u00a3\4\uffff\2\76\1\u00a7\3\uffff\7\76\3\uffff\2\76\1\uffff\1\164\22"+
		"\uffff\1\u00b3\1\u00b4\32\76\1\u00b4\1\uffff\2\76\2\uffff\2\u00a7\7\76"+
		"\1\164\3\uffff\1\u00da\3\76\1\u00de\1\76\1\u00de\25\76\2\u00f5\1\76\2"+
		"\u00f7\2\162\1\164\2\uffff\3\76\1\uffff\2\107\2\u00fc\12\76\2\u0105\1"+
		"\76\1\144\1\76\1\144\2\76\1\uffff\1\76\2\uffff\1\76\2\u010d\1\uffff\2"+
		"\76\2\uffff\1\u0110\1\76\1\u0110\1\76\1\uffff\4\76\1\u0118\3\uffff\2\u011a"+
		"\1\uffff\2\u011b\2\u011c\3\76\5\uffff\2\u00a6\1\76\1\u0122\1\u0124\3\uffff";
	static final String DFA35_eofS =
		"\u0125\uffff";
	static final String DFA35_minS =
		"\1\0\1\116\1\105\1\141\1\105\1\145\1\101\1\43\1\110\1\150\1\111\1\151"+
		"\1\122\1\162\1\102\1\142\1\111\1\151\1\105\1\145\1\156\1\116\1\156\2\75"+
		"\1\117\1\157\1\75\1\72\1\116\1\156\1\125\1\165\1\157\1\145\1\105\3\uffff"+
		"\1\122\1\43\2\60\12\uffff\2\0\2\uffff\1\0\2\uffff\1\43\1\104\1\uffff\1"+
		"\123\1\162\1\114\1\154\1\117\1\114\1\157\1\154\1\uffff\1\105\1\145\1\123"+
		"\1\163\1\117\1\157\1\43\1\106\1\112\1\43\1\146\1\152\1\115\1\155\1\107"+
		"\1\147\1\144\3\43\4\uffff\1\116\1\156\1\72\3\uffff\1\104\1\144\1\114\1"+
		"\154\1\155\1\164\1\124\3\uffff\1\125\1\165\1\uffff\1\57\22\uffff\2\43"+
		"\1\103\1\145\1\105\1\145\1\115\1\123\1\155\1\163\1\122\1\162\1\124\1\164"+
		"\1\125\1\165\1\105\1\123\1\105\1\145\1\163\1\145\1\111\1\151\1\127\1\111"+
		"\1\167\1\151\1\43\1\uffff\1\124\1\164\2\uffff\2\43\1\114\1\154\2\141\1"+
		"\101\1\105\1\145\2\60\2\uffff\1\43\1\156\1\103\1\143\1\43\1\105\1\43\1"+
		"\145\1\105\1\145\1\117\1\157\1\120\1\160\1\122\1\105\1\103\1\162\1\145"+
		"\1\143\1\124\1\164\1\105\1\116\1\145\1\156\1\101\1\141\2\43\1\151\4\43"+
		"\1\57\1\60\1\uffff\1\164\1\124\1\164\1\uffff\4\43\1\122\1\162\3\40\2\124"+
		"\1\40\2\164\2\43\1\105\1\43\1\145\1\43\1\111\1\151\1\uffff\1\156\1\uffff"+
		"\1\57\1\50\2\43\1\uffff\1\131\1\171\2\uffff\1\43\1\123\1\43\1\163\1\uffff"+
		"\1\116\1\156\1\116\1\156\1\43\1\60\2\uffff\2\43\1\uffff\4\43\1\123\1\163"+
		"\1\145\1\uffff\1\60\3\uffff\2\43\1\166\1\124\1\43\3\uffff";
	static final String DFA35_maxS =
		"\1\uffff\1\123\1\105\1\141\1\105\1\145\1\122\1\172\1\110\1\150\1\111\1"+
		"\151\1\122\1\162\1\122\1\162\1\111\1\151\1\105\1\145\1\163\1\116\1\156"+
		"\2\75\1\117\1\157\1\75\1\72\1\116\1\156\1\125\1\165\1\157\1\145\1\105"+
		"\3\uffff\1\122\1\172\2\71\12\uffff\2\uffff\2\uffff\1\uffff\2\uffff\1\172"+
		"\1\104\1\uffff\1\123\1\162\1\114\1\154\1\117\1\114\1\157\1\154\1\uffff"+
		"\1\105\1\145\1\123\1\163\1\117\1\157\1\172\1\106\1\112\1\172\1\146\1\152"+
		"\1\115\1\155\1\124\1\164\1\144\3\172\4\uffff\1\116\1\156\1\72\3\uffff"+
		"\1\104\1\144\1\114\1\154\1\155\1\164\1\124\3\uffff\1\125\1\165\1\uffff"+
		"\1\71\22\uffff\2\172\1\103\1\145\1\105\1\145\1\115\1\123\1\155\1\163\1"+
		"\122\1\162\1\124\1\164\1\125\1\165\1\105\1\123\1\105\1\145\1\163\1\145"+
		"\1\111\1\151\1\127\1\111\1\167\1\151\1\172\1\uffff\1\124\1\164\2\uffff"+
		"\2\172\1\114\1\154\2\141\1\101\1\105\1\145\2\71\2\uffff\1\172\1\156\1"+
		"\103\1\143\1\172\1\105\1\172\1\145\1\105\1\145\1\117\1\157\1\120\1\160"+
		"\1\122\1\105\1\103\1\162\1\145\1\143\1\124\1\164\1\105\1\116\1\145\1\156"+
		"\1\101\1\141\2\172\1\151\4\172\1\57\1\71\1\uffff\1\164\1\124\1\164\1\uffff"+
		"\4\172\1\122\1\162\3\40\2\124\1\40\2\164\2\172\1\105\1\172\1\145\1\172"+
		"\1\111\1\151\1\uffff\1\156\1\uffff\1\57\1\50\2\172\1\uffff\1\131\1\171"+
		"\2\uffff\1\172\1\123\1\172\1\163\1\uffff\1\116\1\156\1\116\1\156\1\172"+
		"\1\71\2\uffff\2\172\1\uffff\4\172\1\123\1\163\1\145\1\uffff\1\71\3\uffff"+
		"\2\172\1\166\1\124\1\172\3\uffff";
	static final String DFA35_acceptS =
		"\44\uffff\1\34\1\35\1\36\4\uffff\1\44\1\45\1\46\1\47\1\50\1\51\1\52\1"+
		"\53\1\54\1\55\2\uffff\1\61\1\62\1\uffff\1\64\1\66\2\uffff\1\64\10\uffff"+
		"\1\40\24\uffff\1\17\1\21\1\20\1\22\3\uffff\1\60\1\24\1\26\7\uffff\1\34"+
		"\1\35\1\36\2\uffff\1\37\1\uffff\1\43\1\44\1\45\1\46\1\47\1\50\1\51\1\52"+
		"\1\53\1\54\1\55\1\56\1\65\1\57\1\61\1\62\1\63\1\67\35\uffff\1\16\2\uffff"+
		"\1\23\1\25\13\uffff\1\1\1\15\45\uffff\1\2\3\uffff\1\5\26\uffff\1\27\1"+
		"\uffff\1\33\4\uffff\1\6\2\uffff\1\10\1\11\4\uffff\1\12\6\uffff\1\3\1\4"+
		"\2\uffff\1\13\7\uffff\1\30\1\uffff\1\7\1\32\1\14\5\uffff\1\41\1\42\1\31";
	static final String DFA35_specialS =
		"\1\0\64\uffff\1\3\1\2\2\uffff\1\1\u00eb\uffff}>";
	static final String[] DFA35_transitionS = {
			"\11\73\1\53\1\uffff\2\73\1\uffff\22\73\1\53\1\63\1\66\3\73\1\44\1\65"+
			"\1\54\1\55\1\46\1\52\1\67\1\52\1\64\1\71\12\51\1\33\1\73\1\27\1\34\1"+
			"\30\1\73\1\70\1\1\1\22\1\31\1\2\1\35\1\6\1\14\1\12\1\25\2\72\1\20\1\43"+
			"\1\37\1\16\3\72\1\4\1\47\2\72\1\10\3\72\1\56\1\73\1\57\3\73\1\24\1\23"+
			"\1\32\1\41\1\36\1\7\1\15\1\13\1\26\2\72\1\21\1\42\1\40\1\17\1\3\2\72"+
			"\1\5\1\50\2\72\1\11\3\72\1\60\1\45\1\61\1\62\uff81\73",
			"\1\75\4\uffff\1\74",
			"\1\77",
			"\1\100",
			"\1\101",
			"\1\102",
			"\1\104\20\uffff\1\103",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\1\106\20\76"+
			"\1\105\10\76",
			"\1\110",
			"\1\111",
			"\1\112",
			"\1\113",
			"\1\114",
			"\1\115",
			"\1\120\3\uffff\1\117\13\uffff\1\116",
			"\1\123\3\uffff\1\122\13\uffff\1\121",
			"\1\124",
			"\1\125",
			"\1\126",
			"\1\127",
			"\1\130\4\uffff\1\131",
			"\1\132",
			"\1\133",
			"\1\134",
			"\1\136",
			"\1\140",
			"\1\141",
			"\1\142",
			"\1\144",
			"\1\146",
			"\1\147",
			"\1\150",
			"\1\151",
			"\1\152",
			"\1\153",
			"\1\154",
			"",
			"",
			"",
			"\1\160",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\21\76\1\161"+
			"\10\76",
			"\12\163",
			"\12\164",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\12\u0080\1\uffff\2\u0080\1\uffff\ufff2\u0080",
			"\12\u0080\1\uffff\2\u0080\1\uffff\ufff2\u0080",
			"",
			"",
			"\0\u0085",
			"",
			"",
			"\1\76\14\uffff\12\76\7\uffff\2\76\1\u0086\27\76\4\uffff\1\76\1\uffff"+
			"\32\76",
			"\1\u0087",
			"",
			"\1\u0088",
			"\1\u0089",
			"\1\u008a",
			"\1\u008b",
			"\1\u008c",
			"\1\u008d",
			"\1\u008e",
			"\1\u008f",
			"",
			"\1\u0090",
			"\1\u0091",
			"\1\u0092",
			"\1\u0093",
			"\1\u0094",
			"\1\u0095",
			"\1\76\14\uffff\12\76\7\uffff\3\76\1\u0096\26\76\4\uffff\1\76\1\uffff"+
			"\32\76",
			"\1\u0097",
			"\1\u0098",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\3\76\1\u0099"+
			"\26\76",
			"\1\u009a",
			"\1\u009b",
			"\1\u009c",
			"\1\u009d",
			"\1\u009f\14\uffff\1\u009e",
			"\1\u00a1\14\uffff\1\u00a0",
			"\1\u00a2",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"",
			"",
			"",
			"",
			"\1\u00a4",
			"\1\u00a5",
			"\1\u00a6",
			"",
			"",
			"",
			"\1\u00a8",
			"\1\u00a9",
			"\1\u00aa",
			"\1\u00ab",
			"\1\u00ac",
			"\1\u00ad",
			"\1\u00ae",
			"",
			"",
			"",
			"\1\u00af",
			"\1\u00b0",
			"",
			"\1\u00b2\12\u00b1",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\u00b5",
			"\1\u00b6",
			"\1\u00b7",
			"\1\u00b8",
			"\1\u00b9",
			"\1\u00ba",
			"\1\u00bb",
			"\1\u00bc",
			"\1\u00bd",
			"\1\u00be",
			"\1\u00bf",
			"\1\u00c0",
			"\1\u00c1",
			"\1\u00c2",
			"\1\u00c3",
			"\1\u00c4",
			"\1\u00c5",
			"\1\u00c6",
			"\1\u00c7",
			"\1\u00c8",
			"\1\u00c9",
			"\1\u00ca",
			"\1\u00cb",
			"\1\u00cc",
			"\1\u00cd",
			"\1\u00ce",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"",
			"\1\u00cf",
			"\1\u00d0",
			"",
			"",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\u00d1",
			"\1\u00d2",
			"\1\u00d3",
			"\1\u00d4",
			"\1\u00d5",
			"\1\u00d6",
			"\1\u00d7",
			"\12\u00d8",
			"\12\u00d9",
			"",
			"",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\u00db",
			"\1\u00dc",
			"\1\u00dd",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\u00df",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\u00e0",
			"\1\u00e1",
			"\1\u00e2",
			"\1\u00e3",
			"\1\u00e4",
			"\1\u00e5",
			"\1\u00e6",
			"\1\u00e7",
			"\1\u00e8",
			"\1\u00e9",
			"\1\u00ea",
			"\1\u00eb",
			"\1\u00ec",
			"\1\u00ed",
			"\1\u00ee",
			"\1\u00ef",
			"\1\u00f0",
			"\1\u00f1",
			"\1\u00f2",
			"\1\u00f3",
			"\1\u00f4",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\u00f6",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\u00b2",
			"\12\u00f8",
			"",
			"\1\u00f9",
			"\1\u00fa",
			"\1\u00fb",
			"",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\u00fd",
			"\1\u00fe",
			"\1\u00ff",
			"\1\u00ff",
			"\1\u0100",
			"\1\u0101",
			"\1\u0102",
			"\1\u0100",
			"\1\u0103",
			"\1\u0104",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\u0106",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\u0107",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\u0108",
			"\1\u0109",
			"",
			"\1\u010a",
			"",
			"\1\u010b",
			"\1\u010c",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"",
			"\1\u010e",
			"\1\u010f",
			"",
			"",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\u0111",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\u0112",
			"",
			"\1\u0113",
			"\1\u0114",
			"\1\u0115",
			"\1\u0116",
			"\1\76\14\uffff\12\76\7\uffff\21\76\1\u0117\10\76\4\uffff\1\76\1\uffff"+
			"\32\76",
			"\12\u0119",
			"",
			"",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\u011d",
			"\1\u011e",
			"\1\u011f",
			"",
			"\12\u0120",
			"",
			"",
			"",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"\1\u0121",
			"\1\u0123",
			"\1\76\14\uffff\12\76\7\uffff\32\76\4\uffff\1\76\1\uffff\32\76",
			"",
			"",
			""
	};

	static final short[] DFA35_eot = DFA.unpackEncodedString(DFA35_eotS);
	static final short[] DFA35_eof = DFA.unpackEncodedString(DFA35_eofS);
	static final char[] DFA35_min = DFA.unpackEncodedStringToUnsignedChars(DFA35_minS);
	static final char[] DFA35_max = DFA.unpackEncodedStringToUnsignedChars(DFA35_maxS);
	static final short[] DFA35_accept = DFA.unpackEncodedString(DFA35_acceptS);
	static final short[] DFA35_special = DFA.unpackEncodedString(DFA35_specialS);
	static final short[][] DFA35_transition;

	static {
		int numStates = DFA35_transitionS.length;
		DFA35_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA35_transition[i] = DFA.unpackEncodedString(DFA35_transitionS[i]);
		}
	}

	protected class DFA35 extends DFA {

		public DFA35(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 35;
			this.eot = DFA35_eot;
			this.eof = DFA35_eof;
			this.min = DFA35_min;
			this.max = DFA35_max;
			this.accept = DFA35_accept;
			this.special = DFA35_special;
			this.transition = DFA35_transition;
		}
		@Override
		public String getDescription() {
			return "1:1: Tokens : ( T__140 | T__141 | T__142 | SELECTOP | FROMOP | WHEREOP | HISTORYOP | GROUPBYOP | ORDEROP | LMTOP | OFFSOP | BTWOP | BTWANDOP | INOP | LTEQOP | GTEQOP | LTOP | GTOP | CONTOP | BGNOP | ENDOP | EQOP | NULLOP | DOMOP | DOMREVOP | OBJECTS | META | ANDOP | OROP | ALLOP | BOOLTRUE | BOOLFALSE | DATE | TIMESTAMP | NUMBER | WS | LROUND | RROUND | LSQUARE | RSQUARE | LGRAPH | RGRAPH | TILDE | NEG | DOT | SQUOTE | DQUOTE | COLON | COMMA | AT | SLASH | NAME | LITERAL | QUOTED_CHARACTER | NATIVEELM );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			IntStream input = _input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA35_0 = input.LA(1);
						s = -1;
						if ( (LA35_0=='A') ) {s = 1;}
						else if ( (LA35_0=='D') ) {s = 2;}
						else if ( (LA35_0=='p') ) {s = 3;}
						else if ( (LA35_0=='S') ) {s = 4;}
						else if ( (LA35_0=='s') ) {s = 5;}
						else if ( (LA35_0=='F') ) {s = 6;}
						else if ( (LA35_0=='f') ) {s = 7;}
						else if ( (LA35_0=='W') ) {s = 8;}
						else if ( (LA35_0=='w') ) {s = 9;}
						else if ( (LA35_0=='H') ) {s = 10;}
						else if ( (LA35_0=='h') ) {s = 11;}
						else if ( (LA35_0=='G') ) {s = 12;}
						else if ( (LA35_0=='g') ) {s = 13;}
						else if ( (LA35_0=='O') ) {s = 14;}
						else if ( (LA35_0=='o') ) {s = 15;}
						else if ( (LA35_0=='L') ) {s = 16;}
						else if ( (LA35_0=='l') ) {s = 17;}
						else if ( (LA35_0=='B') ) {s = 18;}
						else if ( (LA35_0=='b') ) {s = 19;}
						else if ( (LA35_0=='a') ) {s = 20;}
						else if ( (LA35_0=='I') ) {s = 21;}
						else if ( (LA35_0=='i') ) {s = 22;}
						else if ( (LA35_0=='<') ) {s = 23;}
						else if ( (LA35_0=='>') ) {s = 24;}
						else if ( (LA35_0=='C') ) {s = 25;}
						else if ( (LA35_0=='c') ) {s = 26;}
						else if ( (LA35_0==':') ) {s = 27;}
						else if ( (LA35_0=='=') ) {s = 28;}
						else if ( (LA35_0=='E') ) {s = 29;}
						else if ( (LA35_0=='e') ) {s = 30;}
						else if ( (LA35_0=='N') ) {s = 31;}
						else if ( (LA35_0=='n') ) {s = 32;}
						else if ( (LA35_0=='d') ) {s = 33;}
						else if ( (LA35_0=='m') ) {s = 34;}
						else if ( (LA35_0=='M') ) {s = 35;}
						else if ( (LA35_0=='&') ) {s = 36;}
						else if ( (LA35_0=='|') ) {s = 37;}
						else if ( (LA35_0=='*') ) {s = 38;}
						else if ( (LA35_0=='T') ) {s = 39;}
						else if ( (LA35_0=='t') ) {s = 40;}
						else if ( ((LA35_0 >= '0' && LA35_0 <= '9')) ) {s = 41;}
						else if ( (LA35_0=='+'||LA35_0=='-') ) {s = 42;}
						else if ( (LA35_0=='\t'||LA35_0==' ') ) {s = 43;}
						else if ( (LA35_0=='(') ) {s = 44;}
						else if ( (LA35_0==')') ) {s = 45;}
						else if ( (LA35_0=='[') ) {s = 46;}
						else if ( (LA35_0==']') ) {s = 47;}
						else if ( (LA35_0=='{') ) {s = 48;}
						else if ( (LA35_0=='}') ) {s = 49;}
						else if ( (LA35_0=='~') ) {s = 50;}
						else if ( (LA35_0=='!') ) {s = 51;}
						else if ( (LA35_0=='.') ) {s = 52;}
						else if ( (LA35_0=='\'') ) {s = 53;}
						else if ( (LA35_0=='\"') ) {s = 54;}
						else if ( (LA35_0==',') ) {s = 55;}
						else if ( (LA35_0=='@') ) {s = 56;}
						else if ( (LA35_0=='/') ) {s = 57;}
						else if ( ((LA35_0 >= 'J' && LA35_0 <= 'K')||(LA35_0 >= 'P' && LA35_0 <= 'R')||(LA35_0 >= 'U' && LA35_0 <= 'V')||(LA35_0 >= 'X' && LA35_0 <= 'Z')||(LA35_0 >= 'j' && LA35_0 <= 'k')||(LA35_0 >= 'q' && LA35_0 <= 'r')||(LA35_0 >= 'u' && LA35_0 <= 'v')||(LA35_0 >= 'x' && LA35_0 <= 'z')) ) {s = 58;}
						else if ( ((LA35_0 >= '\u0000' && LA35_0 <= '\b')||(LA35_0 >= '\u000B' && LA35_0 <= '\f')||(LA35_0 >= '\u000E' && LA35_0 <= '\u001F')||(LA35_0 >= '#' && LA35_0 <= '%')||LA35_0==';'||LA35_0=='?'||LA35_0=='\\'||(LA35_0 >= '^' && LA35_0 <= '`')||(LA35_0 >= '\u007F' && LA35_0 <= '\uFFFF')) ) {s = 59;}
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA35_57 = input.LA(1);
						s = -1;
						if ( ((LA35_57 >= '\u0000' && LA35_57 <= '\uFFFF')) ) {s = 133;}
						else s = 132;
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA35_54 = input.LA(1);
						s = -1;
						if ( ((LA35_54 >= '\u0000' && LA35_54 <= '\t')||(LA35_54 >= '\u000B' && LA35_54 <= '\f')||(LA35_54 >= '\u000E' && LA35_54 <= '\uFFFF')) ) {s = 128;}
						else s = 129;
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA35_53 = input.LA(1);
						s = -1;
						if ( ((LA35_53 >= '\u0000' && LA35_53 <= '\t')||(LA35_53 >= '\u000B' && LA35_53 <= '\f')||(LA35_53 >= '\u000E' && LA35_53 <= '\uFFFF')) ) {s = 128;}
						else s = 127;
						if ( s>=0 ) return s;
						break;
			}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 35, _s, input);
			error(nvae);
			throw nvae;
		}
	}

}
