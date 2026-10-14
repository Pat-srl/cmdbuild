// $ANTLR 3.5.2 org/cmdbuild/cql/CQL.g 2018-05-16 09:49:31
package org.cmdbuild.cql;

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;


@SuppressWarnings("all")
public class CQLParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALL", "ALLOP", "AND", "ANDOP", 
		"ASC", "AT", "ATTRIBUTE", "ATTRIBUTEAS", "ATTRIBUTEID", "ATTRIBUTENAME", 
		"ATTRIBUTES", "BGN", "BGNOP", "BOOLFALSE", "BOOLTRUE", "BTW", "BTWANDOP", 
		"BTWOP", "CLASS", "CLASSALIAS", "CLASSDOMREF", "CLASSID", "CLASSREF", 
		"COLON", "COMMA", "CONT", "CONTOP", "DATE", "DDGT", "DEFAULT", "DESC", 
		"DIGIT", "DOM", "DOMCARDS", "DOMID", "DOMMETA", "DOMNAME", "DOMOBJS", 
		"DOMOP", "DOMREF", "DOMREV", "DOMREVOP", "DOMTYPE", "DOMVALUE", "DOT", 
		"DQUOTE", "END", "ENDOP", "EQ", "EQOP", "EXPR", "FALSE", "FIELD", "FIELDID", 
		"FIELDOPERATOR", "FIELDVALUE", "FLOAT", "FROM", "FROMOP", "FUNCTION", 
		"GROUP", "GROUPBY", "GROUPBYOP", "GT", "GTEQ", "GTEQOP", "GTOP", "HISTORY", 
		"HISTORYOP", "IN", "INOP", "INPUTVAL", "INT", "INVERSE", "ISNOTNULL", 
		"ISNULL", "LETTER", "LGRAPH", "LIMIT", "LITBOOL", "LITDATE", "LITERAL", 
		"LITNUM", "LITSTR", "LITTIMESTAMP", "LMTOP", "LOOKUP", "LOOKUPPARENT", 
		"LROUND", "LSQUARE", "LT", "LTEQ", "LTEQOP", "LTOP", "META", "NAME", "NATIVE", 
		"NATIVEELM", "NEG", "NOT", "NOTBGN", "NOTBTW", "NOTCONT", "NOTDOM", "NOTDOMREF", 
		"NOTDOMREV", "NOTEND", "NOTEQ", "NOTGROUP", "NOTIN", "NULLOP", "NUMBER", 
		"OBJECTS", "OFFSET", "OFFSOP", "OR", "ORDERBY", "ORDERELM", "ORDEROP", 
		"OROP", "QDGT", "QUOTED_CHARACTER", "RGRAPH", "RROUND", "RSQUARE", "SELECT", 
		"SELECTOP", "SIGN", "SLASH", "SQUOTE", "TILDE", "TIMESTAMP", "TRUE", "WHERE", 
		"WHEREOP", "WS", "'ASC'", "'DESC'", "'parent()'"
	};
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
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public CQLParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public CQLParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}
	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}
	@Override public String[] getTokenNames() { return CQLParser.tokenNames; }
	@Override public String getGrammarFileName() { return "org/cmdbuild/cql/CQL.g"; }


		//return true if every parameter is null
		boolean isnull(Object... objs){
			if(objs == null){return true;}
			for(Object o : objs){if(o!=null){return false;}}
			return true;
		}


	public static class expr_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "expr"
	// org/cmdbuild/cql/CQL.g:74:1: expr : ( LROUND )? ( SELECTOP select ( COMMA select )* )? FROMOP (history= HISTORYOP )? fromref ( COMMA fromref )* ( WHEREOP fields )? ( GROUPBYOP groupby= selattrs )? ( ORDEROP order ( COMMA order )* )? ( LMTOP litlmt= NUMBER | ( LGRAPH inlmt= NAME RGRAPH ) )? ( OFFSOP litoff= NUMBER | ( LGRAPH inoff= NAME RGRAPH ) )? ( RROUND )? -> ^( EXPR ( ^( HISTORY $history) )? ^( FROM ( fromref )+ ) ( ^( SELECT ( select )+ ) )? ( ^( WHERE fields ) )? ( ^( GROUPBY $groupby) )? ( ^( ORDERBY ( order )+ ) )? ( ^( LIMIT ( ^( LITNUM $litlmt) )? ( ^( INPUTVAL $inlmt) )? ) )? ( ^( OFFSET ( ^( LITNUM $litoff) )? ( ^( INPUTVAL $inoff) )? ) )? ) ;
	public final CQLParser.expr_return expr() throws RecognitionException {
		CQLParser.expr_return retval = new CQLParser.expr_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token history=null;
		Token litlmt=null;
		Token inlmt=null;
		Token litoff=null;
		Token inoff=null;
		Token LROUND1=null;
		Token SELECTOP2=null;
		Token COMMA4=null;
		Token FROMOP6=null;
		Token COMMA8=null;
		Token WHEREOP10=null;
		Token GROUPBYOP12=null;
		Token ORDEROP13=null;
		Token COMMA15=null;
		Token LMTOP17=null;
		Token LGRAPH18=null;
		Token RGRAPH19=null;
		Token OFFSOP20=null;
		Token LGRAPH21=null;
		Token RGRAPH22=null;
		Token RROUND23=null;
		ParserRuleReturnScope groupby =null;
		ParserRuleReturnScope select3 =null;
		ParserRuleReturnScope select5 =null;
		ParserRuleReturnScope fromref7 =null;
		ParserRuleReturnScope fromref9 =null;
		ParserRuleReturnScope fields11 =null;
		ParserRuleReturnScope order14 =null;
		ParserRuleReturnScope order16 =null;

		Object history_tree=null;
		Object litlmt_tree=null;
		Object inlmt_tree=null;
		Object litoff_tree=null;
		Object inoff_tree=null;
		Object LROUND1_tree=null;
		Object SELECTOP2_tree=null;
		Object COMMA4_tree=null;
		Object FROMOP6_tree=null;
		Object COMMA8_tree=null;
		Object WHEREOP10_tree=null;
		Object GROUPBYOP12_tree=null;
		Object ORDEROP13_tree=null;
		Object COMMA15_tree=null;
		Object LMTOP17_tree=null;
		Object LGRAPH18_tree=null;
		Object RGRAPH19_tree=null;
		Object OFFSOP20_tree=null;
		Object LGRAPH21_tree=null;
		Object RGRAPH22_tree=null;
		Object RROUND23_tree=null;
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleTokenStream stream_RGRAPH=new RewriteRuleTokenStream(adaptor,"token RGRAPH");
		RewriteRuleTokenStream stream_NUMBER=new RewriteRuleTokenStream(adaptor,"token NUMBER");
		RewriteRuleTokenStream stream_HISTORYOP=new RewriteRuleTokenStream(adaptor,"token HISTORYOP");
		RewriteRuleTokenStream stream_WHEREOP=new RewriteRuleTokenStream(adaptor,"token WHEREOP");
		RewriteRuleTokenStream stream_LMTOP=new RewriteRuleTokenStream(adaptor,"token LMTOP");
		RewriteRuleTokenStream stream_RROUND=new RewriteRuleTokenStream(adaptor,"token RROUND");
		RewriteRuleTokenStream stream_GROUPBYOP=new RewriteRuleTokenStream(adaptor,"token GROUPBYOP");
		RewriteRuleTokenStream stream_NAME=new RewriteRuleTokenStream(adaptor,"token NAME");
		RewriteRuleTokenStream stream_OFFSOP=new RewriteRuleTokenStream(adaptor,"token OFFSOP");
		RewriteRuleTokenStream stream_FROMOP=new RewriteRuleTokenStream(adaptor,"token FROMOP");
		RewriteRuleTokenStream stream_SELECTOP=new RewriteRuleTokenStream(adaptor,"token SELECTOP");
		RewriteRuleTokenStream stream_ORDEROP=new RewriteRuleTokenStream(adaptor,"token ORDEROP");
		RewriteRuleTokenStream stream_LROUND=new RewriteRuleTokenStream(adaptor,"token LROUND");
		RewriteRuleTokenStream stream_LGRAPH=new RewriteRuleTokenStream(adaptor,"token LGRAPH");
		RewriteRuleSubtreeStream stream_select=new RewriteRuleSubtreeStream(adaptor,"rule select");
		RewriteRuleSubtreeStream stream_fromref=new RewriteRuleSubtreeStream(adaptor,"rule fromref");
		RewriteRuleSubtreeStream stream_fields=new RewriteRuleSubtreeStream(adaptor,"rule fields");
		RewriteRuleSubtreeStream stream_selattrs=new RewriteRuleSubtreeStream(adaptor,"rule selattrs");
		RewriteRuleSubtreeStream stream_order=new RewriteRuleSubtreeStream(adaptor,"rule order");

		try {
			// org/cmdbuild/cql/CQL.g:74:5: ( ( LROUND )? ( SELECTOP select ( COMMA select )* )? FROMOP (history= HISTORYOP )? fromref ( COMMA fromref )* ( WHEREOP fields )? ( GROUPBYOP groupby= selattrs )? ( ORDEROP order ( COMMA order )* )? ( LMTOP litlmt= NUMBER | ( LGRAPH inlmt= NAME RGRAPH ) )? ( OFFSOP litoff= NUMBER | ( LGRAPH inoff= NAME RGRAPH ) )? ( RROUND )? -> ^( EXPR ( ^( HISTORY $history) )? ^( FROM ( fromref )+ ) ( ^( SELECT ( select )+ ) )? ( ^( WHERE fields ) )? ( ^( GROUPBY $groupby) )? ( ^( ORDERBY ( order )+ ) )? ( ^( LIMIT ( ^( LITNUM $litlmt) )? ( ^( INPUTVAL $inlmt) )? ) )? ( ^( OFFSET ( ^( LITNUM $litoff) )? ( ^( INPUTVAL $inoff) )? ) )? ) )
			// org/cmdbuild/cql/CQL.g:74:7: ( LROUND )? ( SELECTOP select ( COMMA select )* )? FROMOP (history= HISTORYOP )? fromref ( COMMA fromref )* ( WHEREOP fields )? ( GROUPBYOP groupby= selattrs )? ( ORDEROP order ( COMMA order )* )? ( LMTOP litlmt= NUMBER | ( LGRAPH inlmt= NAME RGRAPH ) )? ( OFFSOP litoff= NUMBER | ( LGRAPH inoff= NAME RGRAPH ) )? ( RROUND )?
			{
			// org/cmdbuild/cql/CQL.g:74:7: ( LROUND )?
			int alt1=2;
			int LA1_0 = input.LA(1);
			if ( (LA1_0==LROUND) ) {
				alt1=1;
			}
			switch (alt1) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:74:7: LROUND
					{
					LROUND1=(Token)match(input,LROUND,FOLLOW_LROUND_in_expr349); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LROUND.add(LROUND1);

					}
					break;

			}

			// org/cmdbuild/cql/CQL.g:75:3: ( SELECTOP select ( COMMA select )* )?
			int alt3=2;
			int LA3_0 = input.LA(1);
			if ( (LA3_0==SELECTOP) ) {
				alt3=1;
			}
			switch (alt3) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:75:4: SELECTOP select ( COMMA select )*
					{
					SELECTOP2=(Token)match(input,SELECTOP,FOLLOW_SELECTOP_in_expr355); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_SELECTOP.add(SELECTOP2);

					pushFollow(FOLLOW_select_in_expr357);
					select3=select();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_select.add(select3.getTree());
					// org/cmdbuild/cql/CQL.g:75:20: ( COMMA select )*
					loop2:
					while (true) {
						int alt2=2;
						int LA2_0 = input.LA(1);
						if ( (LA2_0==COMMA) ) {
							alt2=1;
						}

						switch (alt2) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:75:21: COMMA select
							{
							COMMA4=(Token)match(input,COMMA,FOLLOW_COMMA_in_expr360); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_COMMA.add(COMMA4);

							pushFollow(FOLLOW_select_in_expr362);
							select5=select();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_select.add(select5.getTree());
							}
							break;

						default :
							break loop2;
						}
					}

					}
					break;

			}

			FROMOP6=(Token)match(input,FROMOP,FOLLOW_FROMOP_in_expr371); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_FROMOP.add(FROMOP6);

			// org/cmdbuild/cql/CQL.g:76:17: (history= HISTORYOP )?
			int alt4=2;
			int LA4_0 = input.LA(1);
			if ( (LA4_0==HISTORYOP) ) {
				alt4=1;
			}
			switch (alt4) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:76:17: history= HISTORYOP
					{
					history=(Token)match(input,HISTORYOP,FOLLOW_HISTORYOP_in_expr375); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_HISTORYOP.add(history);

					}
					break;

			}

			pushFollow(FOLLOW_fromref_in_expr378);
			fromref7=fromref();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_fromref.add(fromref7.getTree());
			// org/cmdbuild/cql/CQL.g:76:37: ( COMMA fromref )*
			loop5:
			while (true) {
				int alt5=2;
				int LA5_0 = input.LA(1);
				if ( (LA5_0==COMMA) ) {
					int LA5_13 = input.LA(2);
					if ( (LA5_13==NUMBER) ) {
						int LA5_21 = input.LA(3);
						if ( (synpred5_CQL()) ) {
							alt5=1;
						}

					}
					else if ( (LA5_13==DOMOP||LA5_13==DOMREVOP||LA5_13==DOT||LA5_13==LSQUARE||LA5_13==NAME||LA5_13==TILDE) ) {
						alt5=1;
					}

				}

				switch (alt5) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:76:38: COMMA fromref
					{
					COMMA8=(Token)match(input,COMMA,FOLLOW_COMMA_in_expr381); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COMMA.add(COMMA8);

					pushFollow(FOLLOW_fromref_in_expr383);
					fromref9=fromref();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_fromref.add(fromref9.getTree());
					}
					break;

				default :
					break loop5;
				}
			}

			// org/cmdbuild/cql/CQL.g:77:3: ( WHEREOP fields )?
			int alt6=2;
			int LA6_0 = input.LA(1);
			if ( (LA6_0==WHEREOP) ) {
				alt6=1;
			}
			switch (alt6) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:77:4: WHEREOP fields
					{
					WHEREOP10=(Token)match(input,WHEREOP,FOLLOW_WHEREOP_in_expr391); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_WHEREOP.add(WHEREOP10);

					pushFollow(FOLLOW_fields_in_expr393);
					fields11=fields();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_fields.add(fields11.getTree());
					}
					break;

			}

			// org/cmdbuild/cql/CQL.g:78:3: ( GROUPBYOP groupby= selattrs )?
			int alt7=2;
			int LA7_0 = input.LA(1);
			if ( (LA7_0==GROUPBYOP) ) {
				int LA7_1 = input.LA(2);
				if ( (synpred7_CQL()) ) {
					alt7=1;
				}
			}
			switch (alt7) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:78:4: GROUPBYOP groupby= selattrs
					{
					GROUPBYOP12=(Token)match(input,GROUPBYOP,FOLLOW_GROUPBYOP_in_expr401); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_GROUPBYOP.add(GROUPBYOP12);

					pushFollow(FOLLOW_selattrs_in_expr405);
					groupby=selattrs();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_selattrs.add(groupby.getTree());
					}
					break;

			}

			// org/cmdbuild/cql/CQL.g:79:3: ( ORDEROP order ( COMMA order )* )?
			int alt9=2;
			int LA9_0 = input.LA(1);
			if ( (LA9_0==ORDEROP) ) {
				int LA9_1 = input.LA(2);
				if ( (synpred9_CQL()) ) {
					alt9=1;
				}
			}
			switch (alt9) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:79:4: ORDEROP order ( COMMA order )*
					{
					ORDEROP13=(Token)match(input,ORDEROP,FOLLOW_ORDEROP_in_expr413); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ORDEROP.add(ORDEROP13);

					pushFollow(FOLLOW_order_in_expr415);
					order14=order();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_order.add(order14.getTree());
					// org/cmdbuild/cql/CQL.g:79:18: ( COMMA order )*
					loop8:
					while (true) {
						int alt8=2;
						int LA8_0 = input.LA(1);
						if ( (LA8_0==COMMA) ) {
							int LA8_12 = input.LA(2);
							if ( (LA8_12==NAME) ) {
								alt8=1;
							}

						}

						switch (alt8) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:79:19: COMMA order
							{
							COMMA15=(Token)match(input,COMMA,FOLLOW_COMMA_in_expr418); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_COMMA.add(COMMA15);

							pushFollow(FOLLOW_order_in_expr420);
							order16=order();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_order.add(order16.getTree());
							}
							break;

						default :
							break loop8;
						}
					}

					}
					break;

			}

			// org/cmdbuild/cql/CQL.g:80:3: ( LMTOP litlmt= NUMBER | ( LGRAPH inlmt= NAME RGRAPH ) )?
			int alt10=3;
			int LA10_0 = input.LA(1);
			if ( (LA10_0==LMTOP) ) {
				int LA10_1 = input.LA(2);
				if ( (synpred10_CQL()) ) {
					alt10=1;
				}
			}
			else if ( (LA10_0==LGRAPH) ) {
				int LA10_2 = input.LA(2);
				if ( (synpred11_CQL()) ) {
					alt10=2;
				}
			}
			switch (alt10) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:80:4: LMTOP litlmt= NUMBER
					{
					LMTOP17=(Token)match(input,LMTOP,FOLLOW_LMTOP_in_expr429); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LMTOP.add(LMTOP17);

					litlmt=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_expr434); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NUMBER.add(litlmt);

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:80:25: ( LGRAPH inlmt= NAME RGRAPH )
					{
					// org/cmdbuild/cql/CQL.g:80:25: ( LGRAPH inlmt= NAME RGRAPH )
					// org/cmdbuild/cql/CQL.g:80:26: LGRAPH inlmt= NAME RGRAPH
					{
					LGRAPH18=(Token)match(input,LGRAPH,FOLLOW_LGRAPH_in_expr437); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LGRAPH.add(LGRAPH18);

					inlmt=(Token)match(input,NAME,FOLLOW_NAME_in_expr441); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NAME.add(inlmt);

					RGRAPH19=(Token)match(input,RGRAPH,FOLLOW_RGRAPH_in_expr443); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RGRAPH.add(RGRAPH19);

					}

					}
					break;

			}

			// org/cmdbuild/cql/CQL.g:81:3: ( OFFSOP litoff= NUMBER | ( LGRAPH inoff= NAME RGRAPH ) )?
			int alt11=3;
			int LA11_0 = input.LA(1);
			if ( (LA11_0==OFFSOP) ) {
				int LA11_1 = input.LA(2);
				if ( (synpred12_CQL()) ) {
					alt11=1;
				}
			}
			else if ( (LA11_0==LGRAPH) ) {
				int LA11_2 = input.LA(2);
				if ( (synpred13_CQL()) ) {
					alt11=2;
				}
			}
			switch (alt11) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:81:4: OFFSOP litoff= NUMBER
					{
					OFFSOP20=(Token)match(input,OFFSOP,FOLLOW_OFFSOP_in_expr452); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_OFFSOP.add(OFFSOP20);

					litoff=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_expr456); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NUMBER.add(litoff);

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:81:25: ( LGRAPH inoff= NAME RGRAPH )
					{
					// org/cmdbuild/cql/CQL.g:81:25: ( LGRAPH inoff= NAME RGRAPH )
					// org/cmdbuild/cql/CQL.g:81:26: LGRAPH inoff= NAME RGRAPH
					{
					LGRAPH21=(Token)match(input,LGRAPH,FOLLOW_LGRAPH_in_expr459); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LGRAPH.add(LGRAPH21);

					inoff=(Token)match(input,NAME,FOLLOW_NAME_in_expr463); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NAME.add(inoff);

					RGRAPH22=(Token)match(input,RGRAPH,FOLLOW_RGRAPH_in_expr465); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RGRAPH.add(RGRAPH22);

					}

					}
					break;

			}

			// org/cmdbuild/cql/CQL.g:82:3: ( RROUND )?
			int alt12=2;
			int LA12_0 = input.LA(1);
			if ( (LA12_0==RROUND) ) {
				int LA12_1 = input.LA(2);
				if ( (synpred14_CQL()) ) {
					alt12=1;
				}
			}
			switch (alt12) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:82:3: RROUND
					{
					RROUND23=(Token)match(input,RROUND,FOLLOW_RROUND_in_expr472); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RROUND.add(RROUND23);

					}
					break;

			}

			// AST REWRITE
			// elements: inoff, groupby, litlmt, order, litoff, select, inlmt, history, fromref, fields
			// token labels: inoff, litoff, litlmt, inlmt, history
			// rule labels: groupby, retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleTokenStream stream_inoff=new RewriteRuleTokenStream(adaptor,"token inoff",inoff);
			RewriteRuleTokenStream stream_litoff=new RewriteRuleTokenStream(adaptor,"token litoff",litoff);
			RewriteRuleTokenStream stream_litlmt=new RewriteRuleTokenStream(adaptor,"token litlmt",litlmt);
			RewriteRuleTokenStream stream_inlmt=new RewriteRuleTokenStream(adaptor,"token inlmt",inlmt);
			RewriteRuleTokenStream stream_history=new RewriteRuleTokenStream(adaptor,"token history",history);
			RewriteRuleSubtreeStream stream_groupby=new RewriteRuleSubtreeStream(adaptor,"rule groupby",groupby!=null?groupby.getTree():null);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 83:3: -> ^( EXPR ( ^( HISTORY $history) )? ^( FROM ( fromref )+ ) ( ^( SELECT ( select )+ ) )? ( ^( WHERE fields ) )? ( ^( GROUPBY $groupby) )? ( ^( ORDERBY ( order )+ ) )? ( ^( LIMIT ( ^( LITNUM $litlmt) )? ( ^( INPUTVAL $inlmt) )? ) )? ( ^( OFFSET ( ^( LITNUM $litoff) )? ( ^( INPUTVAL $inoff) )? ) )? )
			{
				// org/cmdbuild/cql/CQL.g:83:6: ^( EXPR ( ^( HISTORY $history) )? ^( FROM ( fromref )+ ) ( ^( SELECT ( select )+ ) )? ( ^( WHERE fields ) )? ( ^( GROUPBY $groupby) )? ( ^( ORDERBY ( order )+ ) )? ( ^( LIMIT ( ^( LITNUM $litlmt) )? ( ^( INPUTVAL $inlmt) )? ) )? ( ^( OFFSET ( ^( LITNUM $litoff) )? ( ^( INPUTVAL $inoff) )? ) )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(EXPR, "EXPR"), root_1);
				// org/cmdbuild/cql/CQL.g:83:13: ( ^( HISTORY $history) )?
				if ( stream_history.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:83:13: ^( HISTORY $history)
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(HISTORY, "HISTORY"), root_2);
					adaptor.addChild(root_2, stream_history.nextNode());
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_history.reset();

				// org/cmdbuild/cql/CQL.g:84:4: ^( FROM ( fromref )+ )
				{
				Object root_2 = (Object)adaptor.nil();
				root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FROM, "FROM"), root_2);
				if ( !(stream_fromref.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_fromref.hasNext() ) {
					adaptor.addChild(root_2, stream_fromref.nextTree());
				}
				stream_fromref.reset();

				adaptor.addChild(root_1, root_2);
				}

				// org/cmdbuild/cql/CQL.g:85:4: ( ^( SELECT ( select )+ ) )?
				if ( stream_select.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:85:4: ^( SELECT ( select )+ )
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(SELECT, "SELECT"), root_2);
					if ( !(stream_select.hasNext()) ) {
						throw new RewriteEarlyExitException();
					}
					while ( stream_select.hasNext() ) {
						adaptor.addChild(root_2, stream_select.nextTree());
					}
					stream_select.reset();

					adaptor.addChild(root_1, root_2);
					}

				}
				stream_select.reset();

				// org/cmdbuild/cql/CQL.g:86:4: ( ^( WHERE fields ) )?
				if ( stream_fields.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:86:4: ^( WHERE fields )
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(WHERE, "WHERE"), root_2);
					adaptor.addChild(root_2, stream_fields.nextTree());
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_fields.reset();

				// org/cmdbuild/cql/CQL.g:87:4: ( ^( GROUPBY $groupby) )?
				if ( stream_groupby.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:87:4: ^( GROUPBY $groupby)
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(GROUPBY, "GROUPBY"), root_2);
					adaptor.addChild(root_2, stream_groupby.nextTree());
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_groupby.reset();

				// org/cmdbuild/cql/CQL.g:88:4: ( ^( ORDERBY ( order )+ ) )?
				if ( stream_order.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:88:4: ^( ORDERBY ( order )+ )
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(ORDERBY, "ORDERBY"), root_2);
					if ( !(stream_order.hasNext()) ) {
						throw new RewriteEarlyExitException();
					}
					while ( stream_order.hasNext() ) {
						adaptor.addChild(root_2, stream_order.nextTree());
					}
					stream_order.reset();

					adaptor.addChild(root_1, root_2);
					}

				}
				stream_order.reset();

				// org/cmdbuild/cql/CQL.g:89:4: ( ^( LIMIT ( ^( LITNUM $litlmt) )? ( ^( INPUTVAL $inlmt) )? ) )?
				if ( stream_litlmt.hasNext()||stream_inlmt.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:89:4: ^( LIMIT ( ^( LITNUM $litlmt) )? ( ^( INPUTVAL $inlmt) )? )
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIMIT, "LIMIT"), root_2);
					// org/cmdbuild/cql/CQL.g:89:12: ( ^( LITNUM $litlmt) )?
					if ( stream_litlmt.hasNext() ) {
						// org/cmdbuild/cql/CQL.g:89:12: ^( LITNUM $litlmt)
						{
						Object root_3 = (Object)adaptor.nil();
						root_3 = (Object)adaptor.becomeRoot((Object)adaptor.create(LITNUM, "LITNUM"), root_3);
						adaptor.addChild(root_3, stream_litlmt.nextNode());
						adaptor.addChild(root_2, root_3);
						}

					}
					stream_litlmt.reset();

					// org/cmdbuild/cql/CQL.g:89:31: ( ^( INPUTVAL $inlmt) )?
					if ( stream_inlmt.hasNext() ) {
						// org/cmdbuild/cql/CQL.g:89:31: ^( INPUTVAL $inlmt)
						{
						Object root_3 = (Object)adaptor.nil();
						root_3 = (Object)adaptor.becomeRoot((Object)adaptor.create(INPUTVAL, "INPUTVAL"), root_3);
						adaptor.addChild(root_3, stream_inlmt.nextNode());
						adaptor.addChild(root_2, root_3);
						}

					}
					stream_inlmt.reset();

					adaptor.addChild(root_1, root_2);
					}

				}
				stream_litlmt.reset();
				stream_inlmt.reset();

				// org/cmdbuild/cql/CQL.g:90:4: ( ^( OFFSET ( ^( LITNUM $litoff) )? ( ^( INPUTVAL $inoff) )? ) )?
				if ( stream_inoff.hasNext()||stream_litoff.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:90:4: ^( OFFSET ( ^( LITNUM $litoff) )? ( ^( INPUTVAL $inoff) )? )
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(OFFSET, "OFFSET"), root_2);
					// org/cmdbuild/cql/CQL.g:90:13: ( ^( LITNUM $litoff) )?
					if ( stream_litoff.hasNext() ) {
						// org/cmdbuild/cql/CQL.g:90:13: ^( LITNUM $litoff)
						{
						Object root_3 = (Object)adaptor.nil();
						root_3 = (Object)adaptor.becomeRoot((Object)adaptor.create(LITNUM, "LITNUM"), root_3);
						adaptor.addChild(root_3, stream_litoff.nextNode());
						adaptor.addChild(root_2, root_3);
						}

					}
					stream_litoff.reset();

					// org/cmdbuild/cql/CQL.g:90:32: ( ^( INPUTVAL $inoff) )?
					if ( stream_inoff.hasNext() ) {
						// org/cmdbuild/cql/CQL.g:90:32: ^( INPUTVAL $inoff)
						{
						Object root_3 = (Object)adaptor.nil();
						root_3 = (Object)adaptor.becomeRoot((Object)adaptor.create(INPUTVAL, "INPUTVAL"), root_3);
						adaptor.addChild(root_3, stream_inoff.nextNode());
						adaptor.addChild(root_2, root_3);
						}

					}
					stream_inoff.reset();

					adaptor.addChild(root_1, root_2);
					}

				}
				stream_inoff.reset();
				stream_litoff.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "expr"


	public static class order_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "order"
	// org/cmdbuild/cql/CQL.g:97:1: order : (cdomscope= NAME DOT )? attr= NAME (asc= 'ASC' |desc= 'DESC' )? -> ^( ORDERELM $attr ( ^( ASC $asc) )? ( ^( DESC $desc) )? ( ^( CLASSDOMREF $cdomscope) )? ) ;
	public final CQLParser.order_return order() throws RecognitionException {
		CQLParser.order_return retval = new CQLParser.order_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token cdomscope=null;
		Token attr=null;
		Token asc=null;
		Token desc=null;
		Token DOT24=null;

		Object cdomscope_tree=null;
		Object attr_tree=null;
		Object asc_tree=null;
		Object desc_tree=null;
		Object DOT24_tree=null;
		RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
		RewriteRuleTokenStream stream_140=new RewriteRuleTokenStream(adaptor,"token 140");
		RewriteRuleTokenStream stream_141=new RewriteRuleTokenStream(adaptor,"token 141");
		RewriteRuleTokenStream stream_NAME=new RewriteRuleTokenStream(adaptor,"token NAME");

		try {
			// org/cmdbuild/cql/CQL.g:98:2: ( (cdomscope= NAME DOT )? attr= NAME (asc= 'ASC' |desc= 'DESC' )? -> ^( ORDERELM $attr ( ^( ASC $asc) )? ( ^( DESC $desc) )? ( ^( CLASSDOMREF $cdomscope) )? ) )
			// org/cmdbuild/cql/CQL.g:98:4: (cdomscope= NAME DOT )? attr= NAME (asc= 'ASC' |desc= 'DESC' )?
			{
			// org/cmdbuild/cql/CQL.g:98:4: (cdomscope= NAME DOT )?
			int alt13=2;
			int LA13_0 = input.LA(1);
			if ( (LA13_0==NAME) ) {
				int LA13_1 = input.LA(2);
				if ( (LA13_1==DOT) ) {
					alt13=1;
				}
			}
			switch (alt13) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:98:5: cdomscope= NAME DOT
					{
					cdomscope=(Token)match(input,NAME,FOLLOW_NAME_in_order616); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NAME.add(cdomscope);

					DOT24=(Token)match(input,DOT,FOLLOW_DOT_in_order618); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_DOT.add(DOT24);

					}
					break;

			}

			attr=(Token)match(input,NAME,FOLLOW_NAME_in_order624); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_NAME.add(attr);

			// org/cmdbuild/cql/CQL.g:98:36: (asc= 'ASC' |desc= 'DESC' )?
			int alt14=3;
			int LA14_0 = input.LA(1);
			if ( (LA14_0==140) ) {
				alt14=1;
			}
			else if ( (LA14_0==141) ) {
				alt14=2;
			}
			switch (alt14) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:98:37: asc= 'ASC'
					{
					asc=(Token)match(input,140,FOLLOW_140_in_order629); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_140.add(asc);

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:98:47: desc= 'DESC'
					{
					desc=(Token)match(input,141,FOLLOW_141_in_order633); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_141.add(desc);

					}
					break;

			}

			// AST REWRITE
			// elements: asc, cdomscope, attr, desc
			// token labels: asc, cdomscope, attr, desc
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleTokenStream stream_asc=new RewriteRuleTokenStream(adaptor,"token asc",asc);
			RewriteRuleTokenStream stream_cdomscope=new RewriteRuleTokenStream(adaptor,"token cdomscope",cdomscope);
			RewriteRuleTokenStream stream_attr=new RewriteRuleTokenStream(adaptor,"token attr",attr);
			RewriteRuleTokenStream stream_desc=new RewriteRuleTokenStream(adaptor,"token desc",desc);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 98:61: -> ^( ORDERELM $attr ( ^( ASC $asc) )? ( ^( DESC $desc) )? ( ^( CLASSDOMREF $cdomscope) )? )
			{
				// org/cmdbuild/cql/CQL.g:98:64: ^( ORDERELM $attr ( ^( ASC $asc) )? ( ^( DESC $desc) )? ( ^( CLASSDOMREF $cdomscope) )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ORDERELM, "ORDERELM"), root_1);
				adaptor.addChild(root_1, stream_attr.nextNode());
				// org/cmdbuild/cql/CQL.g:98:81: ( ^( ASC $asc) )?
				if ( stream_asc.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:98:81: ^( ASC $asc)
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(ASC, "ASC"), root_2);
					adaptor.addChild(root_2, stream_asc.nextNode());
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_asc.reset();

				// org/cmdbuild/cql/CQL.g:98:94: ( ^( DESC $desc) )?
				if ( stream_desc.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:98:94: ^( DESC $desc)
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DESC, "DESC"), root_2);
					adaptor.addChild(root_2, stream_desc.nextNode());
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_desc.reset();

				// org/cmdbuild/cql/CQL.g:98:109: ( ^( CLASSDOMREF $cdomscope) )?
				if ( stream_cdomscope.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:98:109: ^( CLASSDOMREF $cdomscope)
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(CLASSDOMREF, "CLASSDOMREF"), root_2);
					adaptor.addChild(root_2, stream_cdomscope.nextNode());
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_cdomscope.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "order"


	public static class select_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "select"
	// org/cmdbuild/cql/CQL.g:106:1: select : ( ALLOP -> ^( ALL ) |alias= NAME COLON COLON LROUND selattr ( COMMA selattr )* RROUND -> ^( CLASSREF $alias ^( ATTRIBUTES ( selattr )+ ) ) |alias= NAME COLON COLON ( META LROUND meta= selattrs RROUND )? ( OBJECTS LROUND objs= selattrs RROUND )? -> ^( DOMREF $alias ( ^( DOMMETA $meta) )? ( ^( DOMOBJS $objs) )? ) | selattr );
	public final CQLParser.select_return select() throws RecognitionException {
		CQLParser.select_return retval = new CQLParser.select_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token alias=null;
		Token ALLOP25=null;
		Token COLON26=null;
		Token COLON27=null;
		Token LROUND28=null;
		Token COMMA30=null;
		Token RROUND32=null;
		Token COLON33=null;
		Token COLON34=null;
		Token META35=null;
		Token LROUND36=null;
		Token RROUND37=null;
		Token OBJECTS38=null;
		Token LROUND39=null;
		Token RROUND40=null;
		ParserRuleReturnScope meta =null;
		ParserRuleReturnScope objs =null;
		ParserRuleReturnScope selattr29 =null;
		ParserRuleReturnScope selattr31 =null;
		ParserRuleReturnScope selattr41 =null;

		Object alias_tree=null;
		Object ALLOP25_tree=null;
		Object COLON26_tree=null;
		Object COLON27_tree=null;
		Object LROUND28_tree=null;
		Object COMMA30_tree=null;
		Object RROUND32_tree=null;
		Object COLON33_tree=null;
		Object COLON34_tree=null;
		Object META35_tree=null;
		Object LROUND36_tree=null;
		Object RROUND37_tree=null;
		Object OBJECTS38_tree=null;
		Object LROUND39_tree=null;
		Object RROUND40_tree=null;
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleTokenStream stream_META=new RewriteRuleTokenStream(adaptor,"token META");
		RewriteRuleTokenStream stream_ALLOP=new RewriteRuleTokenStream(adaptor,"token ALLOP");
		RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
		RewriteRuleTokenStream stream_RROUND=new RewriteRuleTokenStream(adaptor,"token RROUND");
		RewriteRuleTokenStream stream_LROUND=new RewriteRuleTokenStream(adaptor,"token LROUND");
		RewriteRuleTokenStream stream_OBJECTS=new RewriteRuleTokenStream(adaptor,"token OBJECTS");
		RewriteRuleTokenStream stream_NAME=new RewriteRuleTokenStream(adaptor,"token NAME");
		RewriteRuleSubtreeStream stream_selattrs=new RewriteRuleSubtreeStream(adaptor,"rule selattrs");
		RewriteRuleSubtreeStream stream_selattr=new RewriteRuleSubtreeStream(adaptor,"rule selattr");

		try {
			// org/cmdbuild/cql/CQL.g:107:2: ( ALLOP -> ^( ALL ) |alias= NAME COLON COLON LROUND selattr ( COMMA selattr )* RROUND -> ^( CLASSREF $alias ^( ATTRIBUTES ( selattr )+ ) ) |alias= NAME COLON COLON ( META LROUND meta= selattrs RROUND )? ( OBJECTS LROUND objs= selattrs RROUND )? -> ^( DOMREF $alias ( ^( DOMMETA $meta) )? ( ^( DOMOBJS $objs) )? ) | selattr )
			int alt18=4;
			int LA18_0 = input.LA(1);
			if ( (LA18_0==ALLOP) ) {
				alt18=1;
			}
			else if ( (LA18_0==NAME) ) {
				int LA18_2 = input.LA(2);
				if ( (LA18_2==COLON) ) {
					int LA18_3 = input.LA(3);
					if ( (LA18_3==COLON) ) {
						int LA18_10 = input.LA(4);
						if ( (synpred20_CQL()) ) {
							alt18=2;
						}
						else if ( (synpred23_CQL()) ) {
							alt18=3;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 18, 10, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 18, 3, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}
				else if ( (LA18_2==EOF||LA18_2==AT||LA18_2==COMMA||LA18_2==DOT||LA18_2==FROMOP||LA18_2==LROUND) ) {
					alt18=4;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 18, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 18, 0, input);
				throw nvae;
			}

			switch (alt18) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:107:4: ALLOP
					{
					ALLOP25=(Token)match(input,ALLOP,FOLLOW_ALLOP_in_select681); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ALLOP.add(ALLOP25);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 107:10: -> ^( ALL )
					{
						// org/cmdbuild/cql/CQL.g:107:13: ^( ALL )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ALL, "ALL"), root_1);
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:108:4: alias= NAME COLON COLON LROUND selattr ( COMMA selattr )* RROUND
					{
					alias=(Token)match(input,NAME,FOLLOW_NAME_in_select694); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NAME.add(alias);

					COLON26=(Token)match(input,COLON,FOLLOW_COLON_in_select696); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COLON.add(COLON26);

					COLON27=(Token)match(input,COLON,FOLLOW_COLON_in_select698); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COLON.add(COLON27);

					LROUND28=(Token)match(input,LROUND,FOLLOW_LROUND_in_select700); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LROUND.add(LROUND28);

					pushFollow(FOLLOW_selattr_in_select702);
					selattr29=selattr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_selattr.add(selattr29.getTree());
					// org/cmdbuild/cql/CQL.g:108:42: ( COMMA selattr )*
					loop15:
					while (true) {
						int alt15=2;
						int LA15_0 = input.LA(1);
						if ( (LA15_0==COMMA) ) {
							alt15=1;
						}

						switch (alt15) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:108:43: COMMA selattr
							{
							COMMA30=(Token)match(input,COMMA,FOLLOW_COMMA_in_select705); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_COMMA.add(COMMA30);

							pushFollow(FOLLOW_selattr_in_select707);
							selattr31=selattr();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_selattr.add(selattr31.getTree());
							}
							break;

						default :
							break loop15;
						}
					}

					RROUND32=(Token)match(input,RROUND,FOLLOW_RROUND_in_select711); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RROUND.add(RROUND32);

					// AST REWRITE
					// elements: selattr, alias
					// token labels: alias
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleTokenStream stream_alias=new RewriteRuleTokenStream(adaptor,"token alias",alias);
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 109:3: -> ^( CLASSREF $alias ^( ATTRIBUTES ( selattr )+ ) )
					{
						// org/cmdbuild/cql/CQL.g:109:6: ^( CLASSREF $alias ^( ATTRIBUTES ( selattr )+ ) )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CLASSREF, "CLASSREF"), root_1);
						adaptor.addChild(root_1, stream_alias.nextNode());
						// org/cmdbuild/cql/CQL.g:109:24: ^( ATTRIBUTES ( selattr )+ )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATTRIBUTES, "ATTRIBUTES"), root_2);
						if ( !(stream_selattr.hasNext()) ) {
							throw new RewriteEarlyExitException();
						}
						while ( stream_selattr.hasNext() ) {
							adaptor.addChild(root_2, stream_selattr.nextTree());
						}
						stream_selattr.reset();

						adaptor.addChild(root_1, root_2);
						}

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// org/cmdbuild/cql/CQL.g:110:4: alias= NAME COLON COLON ( META LROUND meta= selattrs RROUND )? ( OBJECTS LROUND objs= selattrs RROUND )?
					{
					alias=(Token)match(input,NAME,FOLLOW_NAME_in_select738); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NAME.add(alias);

					COLON33=(Token)match(input,COLON,FOLLOW_COLON_in_select740); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COLON.add(COLON33);

					COLON34=(Token)match(input,COLON,FOLLOW_COLON_in_select742); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COLON.add(COLON34);

					// org/cmdbuild/cql/CQL.g:110:27: ( META LROUND meta= selattrs RROUND )?
					int alt16=2;
					int LA16_0 = input.LA(1);
					if ( (LA16_0==META) ) {
						alt16=1;
					}
					switch (alt16) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:110:28: META LROUND meta= selattrs RROUND
							{
							META35=(Token)match(input,META,FOLLOW_META_in_select745); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_META.add(META35);

							LROUND36=(Token)match(input,LROUND,FOLLOW_LROUND_in_select747); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_LROUND.add(LROUND36);

							pushFollow(FOLLOW_selattrs_in_select751);
							meta=selattrs();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_selattrs.add(meta.getTree());
							RROUND37=(Token)match(input,RROUND,FOLLOW_RROUND_in_select753); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_RROUND.add(RROUND37);

							}
							break;

					}

					// org/cmdbuild/cql/CQL.g:110:63: ( OBJECTS LROUND objs= selattrs RROUND )?
					int alt17=2;
					int LA17_0 = input.LA(1);
					if ( (LA17_0==OBJECTS) ) {
						alt17=1;
					}
					switch (alt17) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:110:64: OBJECTS LROUND objs= selattrs RROUND
							{
							OBJECTS38=(Token)match(input,OBJECTS,FOLLOW_OBJECTS_in_select758); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_OBJECTS.add(OBJECTS38);

							LROUND39=(Token)match(input,LROUND,FOLLOW_LROUND_in_select760); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_LROUND.add(LROUND39);

							pushFollow(FOLLOW_selattrs_in_select764);
							objs=selattrs();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_selattrs.add(objs.getTree());
							RROUND40=(Token)match(input,RROUND,FOLLOW_RROUND_in_select766); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_RROUND.add(RROUND40);

							}
							break;

					}

					// AST REWRITE
					// elements: objs, meta, alias
					// token labels: alias
					// rule labels: meta, retval, objs
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleTokenStream stream_alias=new RewriteRuleTokenStream(adaptor,"token alias",alias);
					RewriteRuleSubtreeStream stream_meta=new RewriteRuleSubtreeStream(adaptor,"rule meta",meta!=null?meta.getTree():null);
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);
					RewriteRuleSubtreeStream stream_objs=new RewriteRuleSubtreeStream(adaptor,"rule objs",objs!=null?objs.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 111:3: -> ^( DOMREF $alias ( ^( DOMMETA $meta) )? ( ^( DOMOBJS $objs) )? )
					{
						// org/cmdbuild/cql/CQL.g:111:6: ^( DOMREF $alias ( ^( DOMMETA $meta) )? ( ^( DOMOBJS $objs) )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMREF, "DOMREF"), root_1);
						adaptor.addChild(root_1, stream_alias.nextNode());
						// org/cmdbuild/cql/CQL.g:111:22: ( ^( DOMMETA $meta) )?
						if ( stream_meta.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:111:22: ^( DOMMETA $meta)
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMMETA, "DOMMETA"), root_2);
							adaptor.addChild(root_2, stream_meta.nextTree());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_meta.reset();

						// org/cmdbuild/cql/CQL.g:111:40: ( ^( DOMOBJS $objs) )?
						if ( stream_objs.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:111:40: ^( DOMOBJS $objs)
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMOBJS, "DOMOBJS"), root_2);
							adaptor.addChild(root_2, stream_objs.nextTree());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_objs.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 4 :
					// org/cmdbuild/cql/CQL.g:112:4: selattr
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_selattr_in_select801);
					selattr41=selattr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, selattr41.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "select"


	public static class selattrs_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "selattrs"
	// org/cmdbuild/cql/CQL.g:114:1: selattrs : selattr ( COMMA selattr )* -> ( selattr )+ ;
	public final CQLParser.selattrs_return selattrs() throws RecognitionException {
		CQLParser.selattrs_return retval = new CQLParser.selattrs_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token COMMA43=null;
		ParserRuleReturnScope selattr42 =null;
		ParserRuleReturnScope selattr44 =null;

		Object COMMA43_tree=null;
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleSubtreeStream stream_selattr=new RewriteRuleSubtreeStream(adaptor,"rule selattr");

		try {
			// org/cmdbuild/cql/CQL.g:115:2: ( selattr ( COMMA selattr )* -> ( selattr )+ )
			// org/cmdbuild/cql/CQL.g:115:4: selattr ( COMMA selattr )*
			{
			pushFollow(FOLLOW_selattr_in_selattrs811);
			selattr42=selattr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_selattr.add(selattr42.getTree());
			// org/cmdbuild/cql/CQL.g:115:12: ( COMMA selattr )*
			loop19:
			while (true) {
				int alt19=2;
				int LA19_0 = input.LA(1);
				if ( (LA19_0==COMMA) ) {
					int LA19_12 = input.LA(2);
					if ( (LA19_12==NAME) ) {
						alt19=1;
					}

				}

				switch (alt19) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:115:13: COMMA selattr
					{
					COMMA43=(Token)match(input,COMMA,FOLLOW_COMMA_in_selattrs814); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COMMA.add(COMMA43);

					pushFollow(FOLLOW_selattr_in_selattrs816);
					selattr44=selattr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_selattr.add(selattr44.getTree());
					}
					break;

				default :
					break loop19;
				}
			}

			// AST REWRITE
			// elements: selattr
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 115:29: -> ( selattr )+
			{
				if ( !(stream_selattr.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_selattr.hasNext() ) {
					adaptor.addChild(root_0, stream_selattr.nextTree());
				}
				stream_selattr.reset();

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "selattrs"


	public static class selattr_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "selattr"
	// org/cmdbuild/cql/CQL.g:121:1: selattr : (fname= NAME LROUND selattrs RROUND ( AT as= NAME )? -> ^( FUNCTION $fname ^( ATTRIBUTES selattrs ) ( ^( ATTRIBUTEAS $as) )? ) | (cname= NAME DOT )? NAME ( AT as= NAME )? -> ^( ATTRIBUTE ^( ATTRIBUTENAME NAME ) ( ^( ATTRIBUTEAS $as) )? ( ^( CLASSDOMREF $cname) )? ) );
	public final CQLParser.selattr_return selattr() throws RecognitionException {
		CQLParser.selattr_return retval = new CQLParser.selattr_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token fname=null;
		Token as=null;
		Token cname=null;
		Token LROUND45=null;
		Token RROUND47=null;
		Token AT48=null;
		Token DOT49=null;
		Token NAME50=null;
		Token AT51=null;
		ParserRuleReturnScope selattrs46 =null;

		Object fname_tree=null;
		Object as_tree=null;
		Object cname_tree=null;
		Object LROUND45_tree=null;
		Object RROUND47_tree=null;
		Object AT48_tree=null;
		Object DOT49_tree=null;
		Object NAME50_tree=null;
		Object AT51_tree=null;
		RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
		RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
		RewriteRuleTokenStream stream_RROUND=new RewriteRuleTokenStream(adaptor,"token RROUND");
		RewriteRuleTokenStream stream_LROUND=new RewriteRuleTokenStream(adaptor,"token LROUND");
		RewriteRuleTokenStream stream_NAME=new RewriteRuleTokenStream(adaptor,"token NAME");
		RewriteRuleSubtreeStream stream_selattrs=new RewriteRuleSubtreeStream(adaptor,"rule selattrs");

		try {
			// org/cmdbuild/cql/CQL.g:122:2: (fname= NAME LROUND selattrs RROUND ( AT as= NAME )? -> ^( FUNCTION $fname ^( ATTRIBUTES selattrs ) ( ^( ATTRIBUTEAS $as) )? ) | (cname= NAME DOT )? NAME ( AT as= NAME )? -> ^( ATTRIBUTE ^( ATTRIBUTENAME NAME ) ( ^( ATTRIBUTEAS $as) )? ( ^( CLASSDOMREF $cname) )? ) )
			int alt23=2;
			int LA23_0 = input.LA(1);
			if ( (LA23_0==NAME) ) {
				int LA23_1 = input.LA(2);
				if ( (LA23_1==LROUND) ) {
					alt23=1;
				}
				else if ( (LA23_1==EOF||LA23_1==ANDOP||LA23_1==AT||LA23_1==BTWANDOP||(LA23_1 >= COLON && LA23_1 <= COMMA)||LA23_1==DOT||LA23_1==FROMOP||LA23_1==GROUPBYOP||LA23_1==LGRAPH||LA23_1==LMTOP||LA23_1==OFFSOP||(LA23_1 >= ORDEROP && LA23_1 <= OROP)||LA23_1==RROUND) ) {
					alt23=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 23, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 23, 0, input);
				throw nvae;
			}

			switch (alt23) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:122:4: fname= NAME LROUND selattrs RROUND ( AT as= NAME )?
					{
					fname=(Token)match(input,NAME,FOLLOW_NAME_in_selattr836); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NAME.add(fname);

					LROUND45=(Token)match(input,LROUND,FOLLOW_LROUND_in_selattr838); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LROUND.add(LROUND45);

					pushFollow(FOLLOW_selattrs_in_selattr840);
					selattrs46=selattrs();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_selattrs.add(selattrs46.getTree());
					RROUND47=(Token)match(input,RROUND,FOLLOW_RROUND_in_selattr842); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RROUND.add(RROUND47);

					// org/cmdbuild/cql/CQL.g:122:38: ( AT as= NAME )?
					int alt20=2;
					int LA20_0 = input.LA(1);
					if ( (LA20_0==AT) ) {
						alt20=1;
					}
					switch (alt20) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:122:39: AT as= NAME
							{
							AT48=(Token)match(input,AT,FOLLOW_AT_in_selattr845); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_AT.add(AT48);

							as=(Token)match(input,NAME,FOLLOW_NAME_in_selattr849); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NAME.add(as);

							}
							break;

					}

					// AST REWRITE
					// elements: selattrs, fname, as
					// token labels: fname, as
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleTokenStream stream_fname=new RewriteRuleTokenStream(adaptor,"token fname",fname);
					RewriteRuleTokenStream stream_as=new RewriteRuleTokenStream(adaptor,"token as",as);
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 122:52: -> ^( FUNCTION $fname ^( ATTRIBUTES selattrs ) ( ^( ATTRIBUTEAS $as) )? )
					{
						// org/cmdbuild/cql/CQL.g:122:55: ^( FUNCTION $fname ^( ATTRIBUTES selattrs ) ( ^( ATTRIBUTEAS $as) )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FUNCTION, "FUNCTION"), root_1);
						adaptor.addChild(root_1, stream_fname.nextNode());
						// org/cmdbuild/cql/CQL.g:122:73: ^( ATTRIBUTES selattrs )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATTRIBUTES, "ATTRIBUTES"), root_2);
						adaptor.addChild(root_2, stream_selattrs.nextTree());
						adaptor.addChild(root_1, root_2);
						}

						// org/cmdbuild/cql/CQL.g:122:96: ( ^( ATTRIBUTEAS $as) )?
						if ( stream_as.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:122:96: ^( ATTRIBUTEAS $as)
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATTRIBUTEAS, "ATTRIBUTEAS"), root_2);
							adaptor.addChild(root_2, stream_as.nextNode());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_as.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:123:4: (cname= NAME DOT )? NAME ( AT as= NAME )?
					{
					// org/cmdbuild/cql/CQL.g:123:4: (cname= NAME DOT )?
					int alt21=2;
					int LA21_0 = input.LA(1);
					if ( (LA21_0==NAME) ) {
						int LA21_1 = input.LA(2);
						if ( (LA21_1==DOT) ) {
							alt21=1;
						}
					}
					switch (alt21) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:123:5: cname= NAME DOT
							{
							cname=(Token)match(input,NAME,FOLLOW_NAME_in_selattr882); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NAME.add(cname);

							DOT49=(Token)match(input,DOT,FOLLOW_DOT_in_selattr884); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_DOT.add(DOT49);

							}
							break;

					}

					NAME50=(Token)match(input,NAME,FOLLOW_NAME_in_selattr888); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NAME.add(NAME50);

					// org/cmdbuild/cql/CQL.g:123:27: ( AT as= NAME )?
					int alt22=2;
					int LA22_0 = input.LA(1);
					if ( (LA22_0==AT) ) {
						alt22=1;
					}
					switch (alt22) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:123:28: AT as= NAME
							{
							AT51=(Token)match(input,AT,FOLLOW_AT_in_selattr891); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_AT.add(AT51);

							as=(Token)match(input,NAME,FOLLOW_NAME_in_selattr895); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NAME.add(as);

							}
							break;

					}

					// AST REWRITE
					// elements: cname, NAME, as
					// token labels: as, cname
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleTokenStream stream_as=new RewriteRuleTokenStream(adaptor,"token as",as);
					RewriteRuleTokenStream stream_cname=new RewriteRuleTokenStream(adaptor,"token cname",cname);
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 123:43: -> ^( ATTRIBUTE ^( ATTRIBUTENAME NAME ) ( ^( ATTRIBUTEAS $as) )? ( ^( CLASSDOMREF $cname) )? )
					{
						// org/cmdbuild/cql/CQL.g:123:46: ^( ATTRIBUTE ^( ATTRIBUTENAME NAME ) ( ^( ATTRIBUTEAS $as) )? ( ^( CLASSDOMREF $cname) )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATTRIBUTE, "ATTRIBUTE"), root_1);
						// org/cmdbuild/cql/CQL.g:123:58: ^( ATTRIBUTENAME NAME )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATTRIBUTENAME, "ATTRIBUTENAME"), root_2);
						adaptor.addChild(root_2, stream_NAME.nextNode());
						adaptor.addChild(root_1, root_2);
						}

						// org/cmdbuild/cql/CQL.g:123:80: ( ^( ATTRIBUTEAS $as) )?
						if ( stream_as.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:123:80: ^( ATTRIBUTEAS $as)
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATTRIBUTEAS, "ATTRIBUTEAS"), root_2);
							adaptor.addChild(root_2, stream_as.nextNode());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_as.reset();

						// org/cmdbuild/cql/CQL.g:123:100: ( ^( CLASSDOMREF $cname) )?
						if ( stream_cname.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:123:100: ^( CLASSDOMREF $cname)
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(CLASSDOMREF, "CLASSDOMREF"), root_2);
							adaptor.addChild(root_2, stream_cname.nextNode());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_cname.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "selattr"


	public static class fromref_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "fromref"
	// org/cmdbuild/cql/CQL.g:129:1: fromref : ( (name= NAME | NUMBER ) ( AT alias= NAME )? -> ^( CLASSREF ( ^( CLASS $name) )? ( ^( CLASSID NUMBER ) )? ( ^( CLASSALIAS $alias) )? ) | domaindecl );
	public final CQLParser.fromref_return fromref() throws RecognitionException {
		CQLParser.fromref_return retval = new CQLParser.fromref_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token name=null;
		Token alias=null;
		Token NUMBER52=null;
		Token AT53=null;
		ParserRuleReturnScope domaindecl54 =null;

		Object name_tree=null;
		Object alias_tree=null;
		Object NUMBER52_tree=null;
		Object AT53_tree=null;
		RewriteRuleTokenStream stream_NUMBER=new RewriteRuleTokenStream(adaptor,"token NUMBER");
		RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
		RewriteRuleTokenStream stream_NAME=new RewriteRuleTokenStream(adaptor,"token NAME");

		try {
			// org/cmdbuild/cql/CQL.g:130:2: ( (name= NAME | NUMBER ) ( AT alias= NAME )? -> ^( CLASSREF ( ^( CLASS $name) )? ( ^( CLASSID NUMBER ) )? ( ^( CLASSALIAS $alias) )? ) | domaindecl )
			int alt26=2;
			switch ( input.LA(1) ) {
			case NAME:
				{
				int LA26_1 = input.LA(2);
				if ( (LA26_1==EOF||LA26_1==ANDOP||LA26_1==AT||LA26_1==BTWANDOP||(LA26_1 >= COLON && LA26_1 <= COMMA)||LA26_1==GROUPBYOP||LA26_1==LGRAPH||LA26_1==LMTOP||LA26_1==OFFSOP||(LA26_1 >= ORDEROP && LA26_1 <= OROP)||LA26_1==RROUND||LA26_1==WHEREOP) ) {
					alt26=1;
				}
				else if ( (LA26_1==DOMOP||LA26_1==DOMREVOP||LA26_1==DOT||LA26_1==LSQUARE||LA26_1==TILDE) ) {
					alt26=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 26, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case NUMBER:
				{
				alt26=1;
				}
				break;
			case DOMOP:
			case DOMREVOP:
			case DOT:
			case LSQUARE:
			case TILDE:
				{
				alt26=2;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 26, 0, input);
				throw nvae;
			}
			switch (alt26) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:130:4: (name= NAME | NUMBER ) ( AT alias= NAME )?
					{
					// org/cmdbuild/cql/CQL.g:130:4: (name= NAME | NUMBER )
					int alt24=2;
					int LA24_0 = input.LA(1);
					if ( (LA24_0==NAME) ) {
						alt24=1;
					}
					else if ( (LA24_0==NUMBER) ) {
						alt24=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 24, 0, input);
						throw nvae;
					}

					switch (alt24) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:130:5: name= NAME
							{
							name=(Token)match(input,NAME,FOLLOW_NAME_in_fromref943); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NAME.add(name);

							}
							break;
						case 2 :
							// org/cmdbuild/cql/CQL.g:130:15: NUMBER
							{
							NUMBER52=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_fromref945); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NUMBER.add(NUMBER52);

							}
							break;

					}

					// org/cmdbuild/cql/CQL.g:130:23: ( AT alias= NAME )?
					int alt25=2;
					int LA25_0 = input.LA(1);
					if ( (LA25_0==AT) ) {
						alt25=1;
					}
					switch (alt25) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:130:24: AT alias= NAME
							{
							AT53=(Token)match(input,AT,FOLLOW_AT_in_fromref949); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_AT.add(AT53);

							alias=(Token)match(input,NAME,FOLLOW_NAME_in_fromref953); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NAME.add(alias);

							}
							break;

					}

					// AST REWRITE
					// elements: NUMBER, alias, name
					// token labels: name, alias
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
					RewriteRuleTokenStream stream_alias=new RewriteRuleTokenStream(adaptor,"token alias",alias);
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 130:41: -> ^( CLASSREF ( ^( CLASS $name) )? ( ^( CLASSID NUMBER ) )? ( ^( CLASSALIAS $alias) )? )
					{
						// org/cmdbuild/cql/CQL.g:130:44: ^( CLASSREF ( ^( CLASS $name) )? ( ^( CLASSID NUMBER ) )? ( ^( CLASSALIAS $alias) )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CLASSREF, "CLASSREF"), root_1);
						// org/cmdbuild/cql/CQL.g:130:55: ( ^( CLASS $name) )?
						if ( stream_name.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:130:55: ^( CLASS $name)
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(CLASS, "CLASS"), root_2);
							adaptor.addChild(root_2, stream_name.nextNode());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_name.reset();

						// org/cmdbuild/cql/CQL.g:130:71: ( ^( CLASSID NUMBER ) )?
						if ( stream_NUMBER.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:130:71: ^( CLASSID NUMBER )
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(CLASSID, "CLASSID"), root_2);
							adaptor.addChild(root_2, stream_NUMBER.nextNode());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_NUMBER.reset();

						// org/cmdbuild/cql/CQL.g:130:90: ( ^( CLASSALIAS $alias) )?
						if ( stream_alias.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:130:90: ^( CLASSALIAS $alias)
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(CLASSALIAS, "CLASSALIAS"), root_2);
							adaptor.addChild(root_2, stream_alias.nextNode());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_alias.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:131:4: domaindecl
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_domaindecl_in_fromref990);
					domaindecl54=domaindecl();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, domaindecl54.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "fromref"


	public static class domaindecl_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "domaindecl"
	// org/cmdbuild/cql/CQL.g:136:1: domaindecl : (cscope= NAME )? ( ( ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND ) | (t= TILDE )? LSQUARE ) ( NUMBER |domname= NAME ) ( RROUND | RSQUARE ) ( AT domref= NAME )? ( domaindecl )? -> {isnull($rev,$t)}? ^( DOM ( ^( CLASSDOMREF $cscope) )? ^( DOMTYPE DEFAULT ) ( ^( DOMNAME $domname) )? ( ^( DOMID NUMBER ) )? ( ^( DOMREF $domref) )? ( ^( DOMCARDS domaindecl ) )? ) -> ^( DOM ( ^( CLASSDOMREF $cscope) )? ^( DOMTYPE INVERSE ) ( ^( DOMNAME $domname) )? ( ^( DOMID NUMBER ) )? ( ^( DOMREF $domref) )? ( ^( DOMCARDS domaindecl ) )? ) ;
	public final CQLParser.domaindecl_return domaindecl() throws RecognitionException {
		CQLParser.domaindecl_return retval = new CQLParser.domaindecl_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token cscope=null;
		Token rev=null;
		Token t=null;
		Token domname=null;
		Token domref=null;
		Token DOT55=null;
		Token DOMOP56=null;
		Token LROUND57=null;
		Token LSQUARE58=null;
		Token NUMBER59=null;
		Token RROUND60=null;
		Token RSQUARE61=null;
		Token AT62=null;
		ParserRuleReturnScope domaindecl63 =null;

		Object cscope_tree=null;
		Object rev_tree=null;
		Object t_tree=null;
		Object domname_tree=null;
		Object domref_tree=null;
		Object DOT55_tree=null;
		Object DOMOP56_tree=null;
		Object LROUND57_tree=null;
		Object LSQUARE58_tree=null;
		Object NUMBER59_tree=null;
		Object RROUND60_tree=null;
		Object RSQUARE61_tree=null;
		Object AT62_tree=null;
		RewriteRuleTokenStream stream_DOMOP=new RewriteRuleTokenStream(adaptor,"token DOMOP");
		RewriteRuleTokenStream stream_LSQUARE=new RewriteRuleTokenStream(adaptor,"token LSQUARE");
		RewriteRuleTokenStream stream_RSQUARE=new RewriteRuleTokenStream(adaptor,"token RSQUARE");
		RewriteRuleTokenStream stream_NUMBER=new RewriteRuleTokenStream(adaptor,"token NUMBER");
		RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
		RewriteRuleTokenStream stream_DOMREVOP=new RewriteRuleTokenStream(adaptor,"token DOMREVOP");
		RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
		RewriteRuleTokenStream stream_TILDE=new RewriteRuleTokenStream(adaptor,"token TILDE");
		RewriteRuleTokenStream stream_RROUND=new RewriteRuleTokenStream(adaptor,"token RROUND");
		RewriteRuleTokenStream stream_LROUND=new RewriteRuleTokenStream(adaptor,"token LROUND");
		RewriteRuleTokenStream stream_NAME=new RewriteRuleTokenStream(adaptor,"token NAME");
		RewriteRuleSubtreeStream stream_domaindecl=new RewriteRuleSubtreeStream(adaptor,"rule domaindecl");

		try {
			// org/cmdbuild/cql/CQL.g:137:2: ( (cscope= NAME )? ( ( ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND ) | (t= TILDE )? LSQUARE ) ( NUMBER |domname= NAME ) ( RROUND | RSQUARE ) ( AT domref= NAME )? ( domaindecl )? -> {isnull($rev,$t)}? ^( DOM ( ^( CLASSDOMREF $cscope) )? ^( DOMTYPE DEFAULT ) ( ^( DOMNAME $domname) )? ( ^( DOMID NUMBER ) )? ( ^( DOMREF $domref) )? ( ^( DOMCARDS domaindecl ) )? ) -> ^( DOM ( ^( CLASSDOMREF $cscope) )? ^( DOMTYPE INVERSE ) ( ^( DOMNAME $domname) )? ( ^( DOMID NUMBER ) )? ( ^( DOMREF $domref) )? ( ^( DOMCARDS domaindecl ) )? ) )
			// org/cmdbuild/cql/CQL.g:137:4: (cscope= NAME )? ( ( ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND ) | (t= TILDE )? LSQUARE ) ( NUMBER |domname= NAME ) ( RROUND | RSQUARE ) ( AT domref= NAME )? ( domaindecl )?
			{
			// org/cmdbuild/cql/CQL.g:137:10: (cscope= NAME )?
			int alt27=2;
			int LA27_0 = input.LA(1);
			if ( (LA27_0==NAME) ) {
				alt27=1;
			}
			switch (alt27) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:137:10: cscope= NAME
					{
					cscope=(Token)match(input,NAME,FOLLOW_NAME_in_domaindecl1004); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NAME.add(cscope);

					}
					break;

			}

			// org/cmdbuild/cql/CQL.g:138:3: ( ( ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND ) | (t= TILDE )? LSQUARE )
			int alt31=2;
			int LA31_0 = input.LA(1);
			if ( (LA31_0==DOMOP||LA31_0==DOMREVOP||LA31_0==DOT) ) {
				alt31=1;
			}
			else if ( (LA31_0==LSQUARE||LA31_0==TILDE) ) {
				alt31=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 31, 0, input);
				throw nvae;
			}

			switch (alt31) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:138:4: ( ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND )
					{
					// org/cmdbuild/cql/CQL.g:138:4: ( ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND )
					// org/cmdbuild/cql/CQL.g:138:5: ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND
					{
					// org/cmdbuild/cql/CQL.g:138:5: ( DOT )?
					int alt28=2;
					int LA28_0 = input.LA(1);
					if ( (LA28_0==DOT) ) {
						alt28=1;
					}
					switch (alt28) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:138:5: DOT
							{
							DOT55=(Token)match(input,DOT,FOLLOW_DOT_in_domaindecl1011); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_DOT.add(DOT55);

							}
							break;

					}

					// org/cmdbuild/cql/CQL.g:138:10: ( DOMOP |rev= DOMREVOP )
					int alt29=2;
					int LA29_0 = input.LA(1);
					if ( (LA29_0==DOMOP) ) {
						alt29=1;
					}
					else if ( (LA29_0==DOMREVOP) ) {
						alt29=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 29, 0, input);
						throw nvae;
					}

					switch (alt29) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:138:11: DOMOP
							{
							DOMOP56=(Token)match(input,DOMOP,FOLLOW_DOMOP_in_domaindecl1015); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_DOMOP.add(DOMOP56);

							}
							break;
						case 2 :
							// org/cmdbuild/cql/CQL.g:138:17: rev= DOMREVOP
							{
							rev=(Token)match(input,DOMREVOP,FOLLOW_DOMREVOP_in_domaindecl1019); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_DOMREVOP.add(rev);

							}
							break;

					}

					LROUND57=(Token)match(input,LROUND,FOLLOW_LROUND_in_domaindecl1022); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LROUND.add(LROUND57);

					}

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:138:40: (t= TILDE )? LSQUARE
					{
					// org/cmdbuild/cql/CQL.g:138:41: (t= TILDE )?
					int alt30=2;
					int LA30_0 = input.LA(1);
					if ( (LA30_0==TILDE) ) {
						alt30=1;
					}
					switch (alt30) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:138:41: t= TILDE
							{
							t=(Token)match(input,TILDE,FOLLOW_TILDE_in_domaindecl1028); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_TILDE.add(t);

							}
							break;

					}

					LSQUARE58=(Token)match(input,LSQUARE,FOLLOW_LSQUARE_in_domaindecl1031); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LSQUARE.add(LSQUARE58);

					}
					break;

			}

			// org/cmdbuild/cql/CQL.g:138:58: ( NUMBER |domname= NAME )
			int alt32=2;
			int LA32_0 = input.LA(1);
			if ( (LA32_0==NUMBER) ) {
				alt32=1;
			}
			else if ( (LA32_0==NAME) ) {
				alt32=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 32, 0, input);
				throw nvae;
			}

			switch (alt32) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:138:59: NUMBER
					{
					NUMBER59=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_domaindecl1035); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NUMBER.add(NUMBER59);

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:138:66: domname= NAME
					{
					domname=(Token)match(input,NAME,FOLLOW_NAME_in_domaindecl1039); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NAME.add(domname);

					}
					break;

			}

			// org/cmdbuild/cql/CQL.g:138:80: ( RROUND | RSQUARE )
			int alt33=2;
			int LA33_0 = input.LA(1);
			if ( (LA33_0==RROUND) ) {
				alt33=1;
			}
			else if ( (LA33_0==RSQUARE) ) {
				alt33=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 33, 0, input);
				throw nvae;
			}

			switch (alt33) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:138:81: RROUND
					{
					RROUND60=(Token)match(input,RROUND,FOLLOW_RROUND_in_domaindecl1043); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RROUND.add(RROUND60);

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:138:88: RSQUARE
					{
					RSQUARE61=(Token)match(input,RSQUARE,FOLLOW_RSQUARE_in_domaindecl1045); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RSQUARE.add(RSQUARE61);

					}
					break;

			}

			// org/cmdbuild/cql/CQL.g:139:3: ( AT domref= NAME )?
			int alt34=2;
			int LA34_0 = input.LA(1);
			if ( (LA34_0==AT) ) {
				alt34=1;
			}
			switch (alt34) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:139:4: AT domref= NAME
					{
					AT62=(Token)match(input,AT,FOLLOW_AT_in_domaindecl1052); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_AT.add(AT62);

					domref=(Token)match(input,NAME,FOLLOW_NAME_in_domaindecl1056); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NAME.add(domref);

					}
					break;

			}

			// org/cmdbuild/cql/CQL.g:139:21: ( domaindecl )?
			int alt35=2;
			int LA35_0 = input.LA(1);
			if ( (LA35_0==DOMOP||LA35_0==DOMREVOP||LA35_0==DOT||LA35_0==LSQUARE||LA35_0==NAME||LA35_0==TILDE) ) {
				alt35=1;
			}
			switch (alt35) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:139:22: domaindecl
					{
					pushFollow(FOLLOW_domaindecl_in_domaindecl1061);
					domaindecl63=domaindecl();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_domaindecl.add(domaindecl63.getTree());
					}
					break;

			}

			// AST REWRITE
			// elements: domaindecl, cscope, cscope, NUMBER, domname, domref, domaindecl, domref, domname, NUMBER
			// token labels: domname, cscope, domref
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleTokenStream stream_domname=new RewriteRuleTokenStream(adaptor,"token domname",domname);
			RewriteRuleTokenStream stream_cscope=new RewriteRuleTokenStream(adaptor,"token cscope",cscope);
			RewriteRuleTokenStream stream_domref=new RewriteRuleTokenStream(adaptor,"token domref",domref);
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 140:3: -> {isnull($rev,$t)}? ^( DOM ( ^( CLASSDOMREF $cscope) )? ^( DOMTYPE DEFAULT ) ( ^( DOMNAME $domname) )? ( ^( DOMID NUMBER ) )? ( ^( DOMREF $domref) )? ( ^( DOMCARDS domaindecl ) )? )
			if (isnull(rev,t)) {
				// org/cmdbuild/cql/CQL.g:140:25: ^( DOM ( ^( CLASSDOMREF $cscope) )? ^( DOMTYPE DEFAULT ) ( ^( DOMNAME $domname) )? ( ^( DOMID NUMBER ) )? ( ^( DOMREF $domref) )? ( ^( DOMCARDS domaindecl ) )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOM, "DOM"), root_1);
				// org/cmdbuild/cql/CQL.g:140:31: ( ^( CLASSDOMREF $cscope) )?
				if ( stream_cscope.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:140:31: ^( CLASSDOMREF $cscope)
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(CLASSDOMREF, "CLASSDOMREF"), root_2);
					adaptor.addChild(root_2, stream_cscope.nextNode());
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_cscope.reset();

				// org/cmdbuild/cql/CQL.g:140:55: ^( DOMTYPE DEFAULT )
				{
				Object root_2 = (Object)adaptor.nil();
				root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMTYPE, "DOMTYPE"), root_2);
				adaptor.addChild(root_2, (Object)adaptor.create(DEFAULT, "DEFAULT"));
				adaptor.addChild(root_1, root_2);
				}

				// org/cmdbuild/cql/CQL.g:141:9: ( ^( DOMNAME $domname) )?
				if ( stream_domname.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:141:9: ^( DOMNAME $domname)
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMNAME, "DOMNAME"), root_2);
					adaptor.addChild(root_2, stream_domname.nextNode());
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_domname.reset();

				// org/cmdbuild/cql/CQL.g:141:30: ( ^( DOMID NUMBER ) )?
				if ( stream_NUMBER.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:141:30: ^( DOMID NUMBER )
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMID, "DOMID"), root_2);
					adaptor.addChild(root_2, stream_NUMBER.nextNode());
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_NUMBER.reset();

				// org/cmdbuild/cql/CQL.g:142:9: ( ^( DOMREF $domref) )?
				if ( stream_domref.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:142:9: ^( DOMREF $domref)
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMREF, "DOMREF"), root_2);
					adaptor.addChild(root_2, stream_domref.nextNode());
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_domref.reset();

				// org/cmdbuild/cql/CQL.g:142:28: ( ^( DOMCARDS domaindecl ) )?
				if ( stream_domaindecl.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:142:28: ^( DOMCARDS domaindecl )
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMCARDS, "DOMCARDS"), root_2);
					adaptor.addChild(root_2, stream_domaindecl.nextTree());
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_domaindecl.reset();

				adaptor.addChild(root_0, root_1);
				}

			}

			else // 143:8: -> ^( DOM ( ^( CLASSDOMREF $cscope) )? ^( DOMTYPE INVERSE ) ( ^( DOMNAME $domname) )? ( ^( DOMID NUMBER ) )? ( ^( DOMREF $domref) )? ( ^( DOMCARDS domaindecl ) )? )
			{
				// org/cmdbuild/cql/CQL.g:143:11: ^( DOM ( ^( CLASSDOMREF $cscope) )? ^( DOMTYPE INVERSE ) ( ^( DOMNAME $domname) )? ( ^( DOMID NUMBER ) )? ( ^( DOMREF $domref) )? ( ^( DOMCARDS domaindecl ) )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOM, "DOM"), root_1);
				// org/cmdbuild/cql/CQL.g:143:17: ( ^( CLASSDOMREF $cscope) )?
				if ( stream_cscope.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:143:17: ^( CLASSDOMREF $cscope)
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(CLASSDOMREF, "CLASSDOMREF"), root_2);
					adaptor.addChild(root_2, stream_cscope.nextNode());
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_cscope.reset();

				// org/cmdbuild/cql/CQL.g:143:41: ^( DOMTYPE INVERSE )
				{
				Object root_2 = (Object)adaptor.nil();
				root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMTYPE, "DOMTYPE"), root_2);
				adaptor.addChild(root_2, (Object)adaptor.create(INVERSE, "INVERSE"));
				adaptor.addChild(root_1, root_2);
				}

				// org/cmdbuild/cql/CQL.g:144:9: ( ^( DOMNAME $domname) )?
				if ( stream_domname.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:144:9: ^( DOMNAME $domname)
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMNAME, "DOMNAME"), root_2);
					adaptor.addChild(root_2, stream_domname.nextNode());
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_domname.reset();

				// org/cmdbuild/cql/CQL.g:144:30: ( ^( DOMID NUMBER ) )?
				if ( stream_NUMBER.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:144:30: ^( DOMID NUMBER )
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMID, "DOMID"), root_2);
					adaptor.addChild(root_2, stream_NUMBER.nextNode());
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_NUMBER.reset();

				// org/cmdbuild/cql/CQL.g:145:9: ( ^( DOMREF $domref) )?
				if ( stream_domref.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:145:9: ^( DOMREF $domref)
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMREF, "DOMREF"), root_2);
					adaptor.addChild(root_2, stream_domref.nextNode());
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_domref.reset();

				// org/cmdbuild/cql/CQL.g:145:28: ( ^( DOMCARDS domaindecl ) )?
				if ( stream_domaindecl.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:145:28: ^( DOMCARDS domaindecl )
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMCARDS, "DOMCARDS"), root_2);
					adaptor.addChild(root_2, stream_domaindecl.nextTree());
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_domaindecl.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "domaindecl"


	public static class fields_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "fields"
	// org/cmdbuild/cql/CQL.g:148:1: fields : fieldgrpdom ( ( and | or )* ) ;
	public final CQLParser.fields_return fields() throws RecognitionException {
		CQLParser.fields_return retval = new CQLParser.fields_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope fieldgrpdom64 =null;
		ParserRuleReturnScope and65 =null;
		ParserRuleReturnScope or66 =null;


		try {
			// org/cmdbuild/cql/CQL.g:149:2: ( fieldgrpdom ( ( and | or )* ) )
			// org/cmdbuild/cql/CQL.g:149:4: fieldgrpdom ( ( and | or )* )
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_fieldgrpdom_in_fields1222);
			fieldgrpdom64=fieldgrpdom();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, fieldgrpdom64.getTree());

			// org/cmdbuild/cql/CQL.g:149:16: ( ( and | or )* )
			// org/cmdbuild/cql/CQL.g:149:17: ( and | or )*
			{
			// org/cmdbuild/cql/CQL.g:149:17: ( and | or )*
			loop36:
			while (true) {
				int alt36=3;
				int LA36_0 = input.LA(1);
				if ( (LA36_0==ANDOP) ) {
					int LA36_7 = input.LA(2);
					if ( (synpred41_CQL()) ) {
						alt36=1;
					}

				}
				else if ( (LA36_0==OROP) ) {
					int LA36_8 = input.LA(2);
					if ( (synpred42_CQL()) ) {
						alt36=2;
					}

				}

				switch (alt36) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:149:18: and
					{
					pushFollow(FOLLOW_and_in_fields1226);
					and65=and();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, and65.getTree());

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:149:22: or
					{
					pushFollow(FOLLOW_or_in_fields1228);
					or66=or();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, or66.getTree());

					}
					break;

				default :
					break loop36;
				}
			}

			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "fields"


	public static class and_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "and"
	// org/cmdbuild/cql/CQL.g:152:1: and : ANDOP fieldgrpdom -> ^( AND fieldgrpdom ) ;
	public final CQLParser.and_return and() throws RecognitionException {
		CQLParser.and_return retval = new CQLParser.and_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token ANDOP67=null;
		ParserRuleReturnScope fieldgrpdom68 =null;

		Object ANDOP67_tree=null;
		RewriteRuleTokenStream stream_ANDOP=new RewriteRuleTokenStream(adaptor,"token ANDOP");
		RewriteRuleSubtreeStream stream_fieldgrpdom=new RewriteRuleSubtreeStream(adaptor,"rule fieldgrpdom");

		try {
			// org/cmdbuild/cql/CQL.g:152:5: ( ANDOP fieldgrpdom -> ^( AND fieldgrpdom ) )
			// org/cmdbuild/cql/CQL.g:153:2: ANDOP fieldgrpdom
			{
			ANDOP67=(Token)match(input,ANDOP,FOLLOW_ANDOP_in_and1242); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_ANDOP.add(ANDOP67);

			pushFollow(FOLLOW_fieldgrpdom_in_and1244);
			fieldgrpdom68=fieldgrpdom();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_fieldgrpdom.add(fieldgrpdom68.getTree());
			// AST REWRITE
			// elements: fieldgrpdom
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 153:20: -> ^( AND fieldgrpdom )
			{
				// org/cmdbuild/cql/CQL.g:153:23: ^( AND fieldgrpdom )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(AND, "AND"), root_1);
				adaptor.addChild(root_1, stream_fieldgrpdom.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "and"


	public static class or_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "or"
	// org/cmdbuild/cql/CQL.g:154:1: or : OROP fieldgrpdom -> ^( OR fieldgrpdom ) ;
	public final CQLParser.or_return or() throws RecognitionException {
		CQLParser.or_return retval = new CQLParser.or_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token OROP69=null;
		ParserRuleReturnScope fieldgrpdom70 =null;

		Object OROP69_tree=null;
		RewriteRuleTokenStream stream_OROP=new RewriteRuleTokenStream(adaptor,"token OROP");
		RewriteRuleSubtreeStream stream_fieldgrpdom=new RewriteRuleSubtreeStream(adaptor,"rule fieldgrpdom");

		try {
			// org/cmdbuild/cql/CQL.g:154:4: ( OROP fieldgrpdom -> ^( OR fieldgrpdom ) )
			// org/cmdbuild/cql/CQL.g:155:2: OROP fieldgrpdom
			{
			OROP69=(Token)match(input,OROP,FOLLOW_OROP_in_or1260); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_OROP.add(OROP69);

			pushFollow(FOLLOW_fieldgrpdom_in_or1262);
			fieldgrpdom70=fieldgrpdom();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_fieldgrpdom.add(fieldgrpdom70.getTree());
			// AST REWRITE
			// elements: fieldgrpdom
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 155:19: -> ^( OR fieldgrpdom )
			{
				// org/cmdbuild/cql/CQL.g:155:22: ^( OR fieldgrpdom )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(OR, "OR"), root_1);
				adaptor.addChild(root_1, stream_fieldgrpdom.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "or"


	public static class fieldgrpdom_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "fieldgrpdom"
	// org/cmdbuild/cql/CQL.g:157:10: fragment fieldgrpdom : ( field | group | domain );
	public final CQLParser.fieldgrpdom_return fieldgrpdom() throws RecognitionException {
		CQLParser.fieldgrpdom_return retval = new CQLParser.fieldgrpdom_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope field71 =null;
		ParserRuleReturnScope group72 =null;
		ParserRuleReturnScope domain73 =null;


		try {
			// org/cmdbuild/cql/CQL.g:158:2: ( field | group | domain )
			int alt37=3;
			switch ( input.LA(1) ) {
			case NAME:
				{
				switch ( input.LA(2) ) {
				case DOT:
					{
					int LA37_9 = input.LA(3);
					if ( (LA37_9==NAME||LA37_9==142) ) {
						alt37=1;
					}
					else if ( (LA37_9==DOMOP||LA37_9==DOMREVOP||LA37_9==META||LA37_9==OBJECTS) ) {
						alt37=3;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 37, 9, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case BGNOP:
				case BTWOP:
				case CONTOP:
				case ENDOP:
				case EQOP:
				case GTEQOP:
				case GTOP:
				case INOP:
				case LROUND:
				case LTEQOP:
				case LTOP:
				case NEG:
				case NULLOP:
					{
					alt37=1;
					}
					break;
				case EOF:
				case ANDOP:
				case BTWANDOP:
				case COLON:
				case COMMA:
				case DOMOP:
				case DOMREVOP:
				case GROUPBYOP:
				case LGRAPH:
				case LMTOP:
				case LSQUARE:
				case OFFSOP:
				case ORDEROP:
				case OROP:
				case RROUND:
				case TILDE:
					{
					alt37=3;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 37, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case NEG:
				{
				int LA37_2 = input.LA(2);
				if ( (LA37_2==LROUND) ) {
					alt37=2;
				}
				else if ( (LA37_2==DOMOP||LA37_2==DOMREVOP||LA37_2==DOT||LA37_2==LSQUARE||LA37_2==NAME||LA37_2==TILDE) ) {
					alt37=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 37, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case LROUND:
				{
				alt37=2;
				}
				break;
			case DOMOP:
			case DOMREVOP:
			case DOT:
			case LSQUARE:
			case TILDE:
				{
				alt37=3;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 37, 0, input);
				throw nvae;
			}
			switch (alt37) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:158:4: field
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_field_in_fieldgrpdom1281);
					field71=field();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, field71.getTree());

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:159:4: group
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_group_in_fieldgrpdom1286);
					group72=group();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, group72.getTree());

					}
					break;
				case 3 :
					// org/cmdbuild/cql/CQL.g:160:4: domain
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_domain_in_fieldgrpdom1291);
					domain73=domain();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, domain73.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "fieldgrpdom"


	public static class group_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "group"
	// org/cmdbuild/cql/CQL.g:166:1: group : (n= NEG )? LROUND fields RROUND -> {$n!=null}? ^( NOTGROUP fields ) -> ^( GROUP fields ) ;
	public final CQLParser.group_return group() throws RecognitionException {
		CQLParser.group_return retval = new CQLParser.group_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token n=null;
		Token LROUND74=null;
		Token RROUND76=null;
		ParserRuleReturnScope fields75 =null;

		Object n_tree=null;
		Object LROUND74_tree=null;
		Object RROUND76_tree=null;
		RewriteRuleTokenStream stream_NEG=new RewriteRuleTokenStream(adaptor,"token NEG");
		RewriteRuleTokenStream stream_RROUND=new RewriteRuleTokenStream(adaptor,"token RROUND");
		RewriteRuleTokenStream stream_LROUND=new RewriteRuleTokenStream(adaptor,"token LROUND");
		RewriteRuleSubtreeStream stream_fields=new RewriteRuleSubtreeStream(adaptor,"rule fields");

		try {
			// org/cmdbuild/cql/CQL.g:167:2: ( (n= NEG )? LROUND fields RROUND -> {$n!=null}? ^( NOTGROUP fields ) -> ^( GROUP fields ) )
			// org/cmdbuild/cql/CQL.g:167:4: (n= NEG )? LROUND fields RROUND
			{
			// org/cmdbuild/cql/CQL.g:167:5: (n= NEG )?
			int alt38=2;
			int LA38_0 = input.LA(1);
			if ( (LA38_0==NEG) ) {
				alt38=1;
			}
			switch (alt38) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:167:5: n= NEG
					{
					n=(Token)match(input,NEG,FOLLOW_NEG_in_group1306); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NEG.add(n);

					}
					break;

			}

			LROUND74=(Token)match(input,LROUND,FOLLOW_LROUND_in_group1309); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LROUND.add(LROUND74);

			pushFollow(FOLLOW_fields_in_group1311);
			fields75=fields();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_fields.add(fields75.getTree());
			RROUND76=(Token)match(input,RROUND,FOLLOW_RROUND_in_group1313); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_RROUND.add(RROUND76);

			// AST REWRITE
			// elements: fields, fields
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 168:3: -> {$n!=null}? ^( NOTGROUP fields )
			if (n!=null) {
				// org/cmdbuild/cql/CQL.g:168:19: ^( NOTGROUP fields )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NOTGROUP, "NOTGROUP"), root_1);
				adaptor.addChild(root_1, stream_fields.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}

			else // 169:3: -> ^( GROUP fields )
			{
				// org/cmdbuild/cql/CQL.g:169:9: ^( GROUP fields )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(GROUP, "GROUP"), root_1);
				adaptor.addChild(root_1, stream_fields.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "group"


	public static class domain_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "domain"
	// org/cmdbuild/cql/CQL.g:179:1: domain : ( (n= NEG )? (cscope= NAME )? ( ( ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND ) | (t= TILDE )? LSQUARE ) ( NAME | NUMBER ) ( RROUND | RSQUARE ) ( DOT META LROUND meta= fields RROUND )? ( ( DOT OBJECTS LROUND cards= fields ) |subdom= domain )? -> {isnull($rev,$t)}? ^( DOM ( ^( CLASSDOMREF $cscope) )? ^( DOMTYPE DEFAULT ( ^( NOT $n) )? ) ( ^( DOMNAME NAME ) )? ( ^( DOMID NUMBER ) )? ( ^( DOMVALUE $meta) )? ( ^( DOMCARDS $cards) )? ( ^( DOMCARDS $subdom) )? ) -> ^( DOM ( ^( CLASSDOMREF $cscope) )? ^( DOMTYPE INVERSE ( ^( NOT $n) )? ) ( ^( DOMNAME NAME ) )? ( ^( DOMID NUMBER ) )? ( ^( DOMVALUE $meta) )? ( ^( DOMCARDS $cards) )? ( ^( DOMCARDS $subdom) )? ) | (n= NEG )? NAME ( DOT META LROUND meta= fields RROUND )? ( DOT OBJECTS LROUND cards= fields RROUND )? -> ^( DOMREF ^( DOMTYPE ( ^( NOT $n) )? ) ^( DOMNAME NAME ) ( ^( DOMVALUE $meta) )? ( ^( DOMCARDS $cards) )? ) );
	public final CQLParser.domain_return domain() throws RecognitionException {
		CQLParser.domain_return retval = new CQLParser.domain_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token n=null;
		Token cscope=null;
		Token rev=null;
		Token t=null;
		Token DOT77=null;
		Token DOMOP78=null;
		Token LROUND79=null;
		Token LSQUARE80=null;
		Token NAME81=null;
		Token NUMBER82=null;
		Token RROUND83=null;
		Token RSQUARE84=null;
		Token DOT85=null;
		Token META86=null;
		Token LROUND87=null;
		Token RROUND88=null;
		Token DOT89=null;
		Token OBJECTS90=null;
		Token LROUND91=null;
		Token NAME92=null;
		Token DOT93=null;
		Token META94=null;
		Token LROUND95=null;
		Token RROUND96=null;
		Token DOT97=null;
		Token OBJECTS98=null;
		Token LROUND99=null;
		Token RROUND100=null;
		ParserRuleReturnScope meta =null;
		ParserRuleReturnScope cards =null;
		ParserRuleReturnScope subdom =null;

		Object n_tree=null;
		Object cscope_tree=null;
		Object rev_tree=null;
		Object t_tree=null;
		Object DOT77_tree=null;
		Object DOMOP78_tree=null;
		Object LROUND79_tree=null;
		Object LSQUARE80_tree=null;
		Object NAME81_tree=null;
		Object NUMBER82_tree=null;
		Object RROUND83_tree=null;
		Object RSQUARE84_tree=null;
		Object DOT85_tree=null;
		Object META86_tree=null;
		Object LROUND87_tree=null;
		Object RROUND88_tree=null;
		Object DOT89_tree=null;
		Object OBJECTS90_tree=null;
		Object LROUND91_tree=null;
		Object NAME92_tree=null;
		Object DOT93_tree=null;
		Object META94_tree=null;
		Object LROUND95_tree=null;
		Object RROUND96_tree=null;
		Object DOT97_tree=null;
		Object OBJECTS98_tree=null;
		Object LROUND99_tree=null;
		Object RROUND100_tree=null;
		RewriteRuleTokenStream stream_DOMOP=new RewriteRuleTokenStream(adaptor,"token DOMOP");
		RewriteRuleTokenStream stream_META=new RewriteRuleTokenStream(adaptor,"token META");
		RewriteRuleTokenStream stream_NUMBER=new RewriteRuleTokenStream(adaptor,"token NUMBER");
		RewriteRuleTokenStream stream_DOMREVOP=new RewriteRuleTokenStream(adaptor,"token DOMREVOP");
		RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
		RewriteRuleTokenStream stream_RROUND=new RewriteRuleTokenStream(adaptor,"token RROUND");
		RewriteRuleTokenStream stream_OBJECTS=new RewriteRuleTokenStream(adaptor,"token OBJECTS");
		RewriteRuleTokenStream stream_NAME=new RewriteRuleTokenStream(adaptor,"token NAME");
		RewriteRuleTokenStream stream_NEG=new RewriteRuleTokenStream(adaptor,"token NEG");
		RewriteRuleTokenStream stream_LSQUARE=new RewriteRuleTokenStream(adaptor,"token LSQUARE");
		RewriteRuleTokenStream stream_RSQUARE=new RewriteRuleTokenStream(adaptor,"token RSQUARE");
		RewriteRuleTokenStream stream_TILDE=new RewriteRuleTokenStream(adaptor,"token TILDE");
		RewriteRuleTokenStream stream_LROUND=new RewriteRuleTokenStream(adaptor,"token LROUND");
		RewriteRuleSubtreeStream stream_domain=new RewriteRuleSubtreeStream(adaptor,"rule domain");
		RewriteRuleSubtreeStream stream_fields=new RewriteRuleSubtreeStream(adaptor,"rule fields");

		try {
			// org/cmdbuild/cql/CQL.g:180:2: ( (n= NEG )? (cscope= NAME )? ( ( ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND ) | (t= TILDE )? LSQUARE ) ( NAME | NUMBER ) ( RROUND | RSQUARE ) ( DOT META LROUND meta= fields RROUND )? ( ( DOT OBJECTS LROUND cards= fields ) |subdom= domain )? -> {isnull($rev,$t)}? ^( DOM ( ^( CLASSDOMREF $cscope) )? ^( DOMTYPE DEFAULT ( ^( NOT $n) )? ) ( ^( DOMNAME NAME ) )? ( ^( DOMID NUMBER ) )? ( ^( DOMVALUE $meta) )? ( ^( DOMCARDS $cards) )? ( ^( DOMCARDS $subdom) )? ) -> ^( DOM ( ^( CLASSDOMREF $cscope) )? ^( DOMTYPE INVERSE ( ^( NOT $n) )? ) ( ^( DOMNAME NAME ) )? ( ^( DOMID NUMBER ) )? ( ^( DOMVALUE $meta) )? ( ^( DOMCARDS $cards) )? ( ^( DOMCARDS $subdom) )? ) | (n= NEG )? NAME ( DOT META LROUND meta= fields RROUND )? ( DOT OBJECTS LROUND cards= fields RROUND )? -> ^( DOMREF ^( DOMTYPE ( ^( NOT $n) )? ) ^( DOMNAME NAME ) ( ^( DOMVALUE $meta) )? ( ^( DOMCARDS $cards) )? ) )
			int alt52=2;
			switch ( input.LA(1) ) {
			case NEG:
				{
				int LA52_1 = input.LA(2);
				if ( (LA52_1==NAME) ) {
					switch ( input.LA(3) ) {
					case DOT:
						{
						int LA52_31 = input.LA(4);
						if ( (synpred57_CQL()) ) {
							alt52=1;
						}
						else if ( (true) ) {
							alt52=2;
						}

						}
						break;
					case DOMOP:
					case DOMREVOP:
					case LSQUARE:
					case TILDE:
						{
						alt52=1;
						}
						break;
					case EOF:
					case ANDOP:
					case BTWANDOP:
					case COLON:
					case COMMA:
					case GROUPBYOP:
					case LGRAPH:
					case LMTOP:
					case OFFSOP:
					case ORDEROP:
					case OROP:
					case RROUND:
						{
						alt52=2;
						}
						break;
					default:
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 52, 8, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}
				else if ( (LA52_1==DOMOP||LA52_1==DOMREVOP||LA52_1==DOT||LA52_1==LSQUARE||LA52_1==TILDE) ) {
					alt52=1;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 52, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case NAME:
				{
				switch ( input.LA(2) ) {
				case DOT:
					{
					int LA52_14 = input.LA(3);
					if ( (LA52_14==META||LA52_14==OBJECTS) ) {
						alt52=2;
					}
					else if ( (LA52_14==DOMOP||LA52_14==DOMREVOP) ) {
						alt52=1;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 52, 14, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case DOMOP:
				case DOMREVOP:
				case LSQUARE:
				case TILDE:
					{
					alt52=1;
					}
					break;
				case EOF:
				case ANDOP:
				case BTWANDOP:
				case COLON:
				case COMMA:
				case GROUPBYOP:
				case LGRAPH:
				case LMTOP:
				case OFFSOP:
				case ORDEROP:
				case OROP:
				case RROUND:
					{
					alt52=2;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 52, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case DOMOP:
			case DOMREVOP:
			case DOT:
			case LSQUARE:
			case TILDE:
				{
				alt52=1;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 52, 0, input);
				throw nvae;
			}
			switch (alt52) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:180:4: (n= NEG )? (cscope= NAME )? ( ( ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND ) | (t= TILDE )? LSQUARE ) ( NAME | NUMBER ) ( RROUND | RSQUARE ) ( DOT META LROUND meta= fields RROUND )? ( ( DOT OBJECTS LROUND cards= fields ) |subdom= domain )?
					{
					// org/cmdbuild/cql/CQL.g:180:5: (n= NEG )?
					int alt39=2;
					int LA39_0 = input.LA(1);
					if ( (LA39_0==NEG) ) {
						alt39=1;
					}
					switch (alt39) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:180:5: n= NEG
							{
							n=(Token)match(input,NEG,FOLLOW_NEG_in_domain1356); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NEG.add(n);

							}
							break;

					}

					// org/cmdbuild/cql/CQL.g:181:9: (cscope= NAME )?
					int alt40=2;
					int LA40_0 = input.LA(1);
					if ( (LA40_0==NAME) ) {
						alt40=1;
					}
					switch (alt40) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:181:9: cscope= NAME
							{
							cscope=(Token)match(input,NAME,FOLLOW_NAME_in_domain1364); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NAME.add(cscope);

							}
							break;

					}

					// org/cmdbuild/cql/CQL.g:182:3: ( ( ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND ) | (t= TILDE )? LSQUARE )
					int alt44=2;
					int LA44_0 = input.LA(1);
					if ( (LA44_0==DOMOP||LA44_0==DOMREVOP||LA44_0==DOT) ) {
						alt44=1;
					}
					else if ( (LA44_0==LSQUARE||LA44_0==TILDE) ) {
						alt44=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 44, 0, input);
						throw nvae;
					}

					switch (alt44) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:182:4: ( ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND )
							{
							// org/cmdbuild/cql/CQL.g:182:4: ( ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND )
							// org/cmdbuild/cql/CQL.g:182:5: ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND
							{
							// org/cmdbuild/cql/CQL.g:182:5: ( DOT )?
							int alt41=2;
							int LA41_0 = input.LA(1);
							if ( (LA41_0==DOT) ) {
								alt41=1;
							}
							switch (alt41) {
								case 1 :
									// org/cmdbuild/cql/CQL.g:182:5: DOT
									{
									DOT77=(Token)match(input,DOT,FOLLOW_DOT_in_domain1372); if (state.failed) return retval; 
									if ( state.backtracking==0 ) stream_DOT.add(DOT77);

									}
									break;

							}

							// org/cmdbuild/cql/CQL.g:182:10: ( DOMOP |rev= DOMREVOP )
							int alt42=2;
							int LA42_0 = input.LA(1);
							if ( (LA42_0==DOMOP) ) {
								alt42=1;
							}
							else if ( (LA42_0==DOMREVOP) ) {
								alt42=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								NoViableAltException nvae =
									new NoViableAltException("", 42, 0, input);
								throw nvae;
							}

							switch (alt42) {
								case 1 :
									// org/cmdbuild/cql/CQL.g:182:11: DOMOP
									{
									DOMOP78=(Token)match(input,DOMOP,FOLLOW_DOMOP_in_domain1376); if (state.failed) return retval; 
									if ( state.backtracking==0 ) stream_DOMOP.add(DOMOP78);

									}
									break;
								case 2 :
									// org/cmdbuild/cql/CQL.g:182:17: rev= DOMREVOP
									{
									rev=(Token)match(input,DOMREVOP,FOLLOW_DOMREVOP_in_domain1380); if (state.failed) return retval; 
									if ( state.backtracking==0 ) stream_DOMREVOP.add(rev);

									}
									break;

							}

							LROUND79=(Token)match(input,LROUND,FOLLOW_LROUND_in_domain1383); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_LROUND.add(LROUND79);

							}

							}
							break;
						case 2 :
							// org/cmdbuild/cql/CQL.g:182:40: (t= TILDE )? LSQUARE
							{
							// org/cmdbuild/cql/CQL.g:182:41: (t= TILDE )?
							int alt43=2;
							int LA43_0 = input.LA(1);
							if ( (LA43_0==TILDE) ) {
								alt43=1;
							}
							switch (alt43) {
								case 1 :
									// org/cmdbuild/cql/CQL.g:182:41: t= TILDE
									{
									t=(Token)match(input,TILDE,FOLLOW_TILDE_in_domain1389); if (state.failed) return retval; 
									if ( state.backtracking==0 ) stream_TILDE.add(t);

									}
									break;

							}

							LSQUARE80=(Token)match(input,LSQUARE,FOLLOW_LSQUARE_in_domain1392); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_LSQUARE.add(LSQUARE80);

							}
							break;

					}

					// org/cmdbuild/cql/CQL.g:182:58: ( NAME | NUMBER )
					int alt45=2;
					int LA45_0 = input.LA(1);
					if ( (LA45_0==NAME) ) {
						alt45=1;
					}
					else if ( (LA45_0==NUMBER) ) {
						alt45=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 45, 0, input);
						throw nvae;
					}

					switch (alt45) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:182:59: NAME
							{
							NAME81=(Token)match(input,NAME,FOLLOW_NAME_in_domain1396); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NAME.add(NAME81);

							}
							break;
						case 2 :
							// org/cmdbuild/cql/CQL.g:182:64: NUMBER
							{
							NUMBER82=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_domain1398); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NUMBER.add(NUMBER82);

							}
							break;

					}

					// org/cmdbuild/cql/CQL.g:182:72: ( RROUND | RSQUARE )
					int alt46=2;
					int LA46_0 = input.LA(1);
					if ( (LA46_0==RROUND) ) {
						alt46=1;
					}
					else if ( (LA46_0==RSQUARE) ) {
						alt46=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 46, 0, input);
						throw nvae;
					}

					switch (alt46) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:182:73: RROUND
							{
							RROUND83=(Token)match(input,RROUND,FOLLOW_RROUND_in_domain1402); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_RROUND.add(RROUND83);

							}
							break;
						case 2 :
							// org/cmdbuild/cql/CQL.g:182:80: RSQUARE
							{
							RSQUARE84=(Token)match(input,RSQUARE,FOLLOW_RSQUARE_in_domain1404); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_RSQUARE.add(RSQUARE84);

							}
							break;

					}

					// org/cmdbuild/cql/CQL.g:183:3: ( DOT META LROUND meta= fields RROUND )?
					int alt47=2;
					int LA47_0 = input.LA(1);
					if ( (LA47_0==DOT) ) {
						int LA47_1 = input.LA(2);
						if ( (LA47_1==META) ) {
							alt47=1;
						}
					}
					switch (alt47) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:183:4: DOT META LROUND meta= fields RROUND
							{
							DOT85=(Token)match(input,DOT,FOLLOW_DOT_in_domain1411); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_DOT.add(DOT85);

							META86=(Token)match(input,META,FOLLOW_META_in_domain1413); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_META.add(META86);

							LROUND87=(Token)match(input,LROUND,FOLLOW_LROUND_in_domain1415); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_LROUND.add(LROUND87);

							pushFollow(FOLLOW_fields_in_domain1419);
							meta=fields();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_fields.add(meta.getTree());
							RROUND88=(Token)match(input,RROUND,FOLLOW_RROUND_in_domain1421); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_RROUND.add(RROUND88);

							}
							break;

					}

					// org/cmdbuild/cql/CQL.g:183:41: ( ( DOT OBJECTS LROUND cards= fields ) |subdom= domain )?
					int alt48=3;
					int LA48_0 = input.LA(1);
					if ( (LA48_0==DOT) ) {
						int LA48_1 = input.LA(2);
						if ( (LA48_1==OBJECTS) ) {
							alt48=1;
						}
						else if ( (LA48_1==DOMOP||LA48_1==DOMREVOP) ) {
							alt48=2;
						}
					}
					else if ( (LA48_0==DOMOP||LA48_0==DOMREVOP||LA48_0==LSQUARE||LA48_0==NAME||LA48_0==NEG||LA48_0==TILDE) ) {
						alt48=2;
					}
					switch (alt48) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:183:42: ( DOT OBJECTS LROUND cards= fields )
							{
							// org/cmdbuild/cql/CQL.g:183:42: ( DOT OBJECTS LROUND cards= fields )
							// org/cmdbuild/cql/CQL.g:183:43: DOT OBJECTS LROUND cards= fields
							{
							DOT89=(Token)match(input,DOT,FOLLOW_DOT_in_domain1427); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_DOT.add(DOT89);

							OBJECTS90=(Token)match(input,OBJECTS,FOLLOW_OBJECTS_in_domain1429); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_OBJECTS.add(OBJECTS90);

							LROUND91=(Token)match(input,LROUND,FOLLOW_LROUND_in_domain1431); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_LROUND.add(LROUND91);

							pushFollow(FOLLOW_fields_in_domain1435);
							cards=fields();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_fields.add(cards.getTree());
							}

							}
							break;
						case 2 :
							// org/cmdbuild/cql/CQL.g:183:76: subdom= domain
							{
							pushFollow(FOLLOW_domain_in_domain1440);
							subdom=domain();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_domain.add(subdom.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: cscope, cards, cards, cscope, meta, meta, NUMBER, NUMBER, subdom, n, subdom, NAME, n, NAME
					// token labels: cscope, n
					// rule labels: cards, meta, subdom, retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleTokenStream stream_cscope=new RewriteRuleTokenStream(adaptor,"token cscope",cscope);
					RewriteRuleTokenStream stream_n=new RewriteRuleTokenStream(adaptor,"token n",n);
					RewriteRuleSubtreeStream stream_cards=new RewriteRuleSubtreeStream(adaptor,"rule cards",cards!=null?cards.getTree():null);
					RewriteRuleSubtreeStream stream_meta=new RewriteRuleSubtreeStream(adaptor,"rule meta",meta!=null?meta.getTree():null);
					RewriteRuleSubtreeStream stream_subdom=new RewriteRuleSubtreeStream(adaptor,"rule subdom",subdom!=null?subdom.getTree():null);
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 184:3: -> {isnull($rev,$t)}? ^( DOM ( ^( CLASSDOMREF $cscope) )? ^( DOMTYPE DEFAULT ( ^( NOT $n) )? ) ( ^( DOMNAME NAME ) )? ( ^( DOMID NUMBER ) )? ( ^( DOMVALUE $meta) )? ( ^( DOMCARDS $cards) )? ( ^( DOMCARDS $subdom) )? )
					if (isnull(rev,t)) {
						// org/cmdbuild/cql/CQL.g:185:4: ^( DOM ( ^( CLASSDOMREF $cscope) )? ^( DOMTYPE DEFAULT ( ^( NOT $n) )? ) ( ^( DOMNAME NAME ) )? ( ^( DOMID NUMBER ) )? ( ^( DOMVALUE $meta) )? ( ^( DOMCARDS $cards) )? ( ^( DOMCARDS $subdom) )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOM, "DOM"), root_1);
						// org/cmdbuild/cql/CQL.g:186:4: ( ^( CLASSDOMREF $cscope) )?
						if ( stream_cscope.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:186:4: ^( CLASSDOMREF $cscope)
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(CLASSDOMREF, "CLASSDOMREF"), root_2);
							adaptor.addChild(root_2, stream_cscope.nextNode());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_cscope.reset();

						// org/cmdbuild/cql/CQL.g:187:4: ^( DOMTYPE DEFAULT ( ^( NOT $n) )? )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMTYPE, "DOMTYPE"), root_2);
						adaptor.addChild(root_2, (Object)adaptor.create(DEFAULT, "DEFAULT"));
						// org/cmdbuild/cql/CQL.g:187:22: ( ^( NOT $n) )?
						if ( stream_n.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:187:22: ^( NOT $n)
							{
							Object root_3 = (Object)adaptor.nil();
							root_3 = (Object)adaptor.becomeRoot((Object)adaptor.create(NOT, "NOT"), root_3);
							adaptor.addChild(root_3, stream_n.nextNode());
							adaptor.addChild(root_2, root_3);
							}

						}
						stream_n.reset();

						adaptor.addChild(root_1, root_2);
						}

						// org/cmdbuild/cql/CQL.g:188:4: ( ^( DOMNAME NAME ) )?
						if ( stream_NAME.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:188:4: ^( DOMNAME NAME )
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMNAME, "DOMNAME"), root_2);
							adaptor.addChild(root_2, stream_NAME.nextNode());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_NAME.reset();

						// org/cmdbuild/cql/CQL.g:188:21: ( ^( DOMID NUMBER ) )?
						if ( stream_NUMBER.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:188:21: ^( DOMID NUMBER )
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMID, "DOMID"), root_2);
							adaptor.addChild(root_2, stream_NUMBER.nextNode());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_NUMBER.reset();

						// org/cmdbuild/cql/CQL.g:188:38: ( ^( DOMVALUE $meta) )?
						if ( stream_meta.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:188:38: ^( DOMVALUE $meta)
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMVALUE, "DOMVALUE"), root_2);
							adaptor.addChild(root_2, stream_meta.nextTree());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_meta.reset();

						// org/cmdbuild/cql/CQL.g:188:57: ( ^( DOMCARDS $cards) )?
						if ( stream_cards.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:188:57: ^( DOMCARDS $cards)
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMCARDS, "DOMCARDS"), root_2);
							adaptor.addChild(root_2, stream_cards.nextTree());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_cards.reset();

						// org/cmdbuild/cql/CQL.g:188:77: ( ^( DOMCARDS $subdom) )?
						if ( stream_subdom.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:188:77: ^( DOMCARDS $subdom)
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMCARDS, "DOMCARDS"), root_2);
							adaptor.addChild(root_2, stream_subdom.nextTree());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_subdom.reset();

						adaptor.addChild(root_0, root_1);
						}

					}

					else // 189:3: -> ^( DOM ( ^( CLASSDOMREF $cscope) )? ^( DOMTYPE INVERSE ( ^( NOT $n) )? ) ( ^( DOMNAME NAME ) )? ( ^( DOMID NUMBER ) )? ( ^( DOMVALUE $meta) )? ( ^( DOMCARDS $cards) )? ( ^( DOMCARDS $subdom) )? )
					{
						// org/cmdbuild/cql/CQL.g:189:6: ^( DOM ( ^( CLASSDOMREF $cscope) )? ^( DOMTYPE INVERSE ( ^( NOT $n) )? ) ( ^( DOMNAME NAME ) )? ( ^( DOMID NUMBER ) )? ( ^( DOMVALUE $meta) )? ( ^( DOMCARDS $cards) )? ( ^( DOMCARDS $subdom) )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOM, "DOM"), root_1);
						// org/cmdbuild/cql/CQL.g:190:4: ( ^( CLASSDOMREF $cscope) )?
						if ( stream_cscope.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:190:4: ^( CLASSDOMREF $cscope)
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(CLASSDOMREF, "CLASSDOMREF"), root_2);
							adaptor.addChild(root_2, stream_cscope.nextNode());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_cscope.reset();

						// org/cmdbuild/cql/CQL.g:191:4: ^( DOMTYPE INVERSE ( ^( NOT $n) )? )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMTYPE, "DOMTYPE"), root_2);
						adaptor.addChild(root_2, (Object)adaptor.create(INVERSE, "INVERSE"));
						// org/cmdbuild/cql/CQL.g:191:22: ( ^( NOT $n) )?
						if ( stream_n.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:191:22: ^( NOT $n)
							{
							Object root_3 = (Object)adaptor.nil();
							root_3 = (Object)adaptor.becomeRoot((Object)adaptor.create(NOT, "NOT"), root_3);
							adaptor.addChild(root_3, stream_n.nextNode());
							adaptor.addChild(root_2, root_3);
							}

						}
						stream_n.reset();

						adaptor.addChild(root_1, root_2);
						}

						// org/cmdbuild/cql/CQL.g:192:4: ( ^( DOMNAME NAME ) )?
						if ( stream_NAME.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:192:4: ^( DOMNAME NAME )
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMNAME, "DOMNAME"), root_2);
							adaptor.addChild(root_2, stream_NAME.nextNode());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_NAME.reset();

						// org/cmdbuild/cql/CQL.g:192:21: ( ^( DOMID NUMBER ) )?
						if ( stream_NUMBER.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:192:21: ^( DOMID NUMBER )
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMID, "DOMID"), root_2);
							adaptor.addChild(root_2, stream_NUMBER.nextNode());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_NUMBER.reset();

						// org/cmdbuild/cql/CQL.g:192:38: ( ^( DOMVALUE $meta) )?
						if ( stream_meta.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:192:38: ^( DOMVALUE $meta)
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMVALUE, "DOMVALUE"), root_2);
							adaptor.addChild(root_2, stream_meta.nextTree());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_meta.reset();

						// org/cmdbuild/cql/CQL.g:192:57: ( ^( DOMCARDS $cards) )?
						if ( stream_cards.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:192:57: ^( DOMCARDS $cards)
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMCARDS, "DOMCARDS"), root_2);
							adaptor.addChild(root_2, stream_cards.nextTree());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_cards.reset();

						// org/cmdbuild/cql/CQL.g:192:77: ( ^( DOMCARDS $subdom) )?
						if ( stream_subdom.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:192:77: ^( DOMCARDS $subdom)
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMCARDS, "DOMCARDS"), root_2);
							adaptor.addChild(root_2, stream_subdom.nextTree());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_subdom.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:194:4: (n= NEG )? NAME ( DOT META LROUND meta= fields RROUND )? ( DOT OBJECTS LROUND cards= fields RROUND )?
					{
					// org/cmdbuild/cql/CQL.g:194:5: (n= NEG )?
					int alt49=2;
					int LA49_0 = input.LA(1);
					if ( (LA49_0==NEG) ) {
						alt49=1;
					}
					switch (alt49) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:194:5: n= NEG
							{
							n=(Token)match(input,NEG,FOLLOW_NEG_in_domain1617); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NEG.add(n);

							}
							break;

					}

					NAME92=(Token)match(input,NAME,FOLLOW_NAME_in_domain1620); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NAME.add(NAME92);

					// org/cmdbuild/cql/CQL.g:194:16: ( DOT META LROUND meta= fields RROUND )?
					int alt50=2;
					int LA50_0 = input.LA(1);
					if ( (LA50_0==DOT) ) {
						int LA50_1 = input.LA(2);
						if ( (LA50_1==META) ) {
							alt50=1;
						}
					}
					switch (alt50) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:194:17: DOT META LROUND meta= fields RROUND
							{
							DOT93=(Token)match(input,DOT,FOLLOW_DOT_in_domain1623); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_DOT.add(DOT93);

							META94=(Token)match(input,META,FOLLOW_META_in_domain1625); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_META.add(META94);

							LROUND95=(Token)match(input,LROUND,FOLLOW_LROUND_in_domain1627); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_LROUND.add(LROUND95);

							pushFollow(FOLLOW_fields_in_domain1631);
							meta=fields();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_fields.add(meta.getTree());
							RROUND96=(Token)match(input,RROUND,FOLLOW_RROUND_in_domain1633); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_RROUND.add(RROUND96);

							}
							break;

					}

					// org/cmdbuild/cql/CQL.g:194:54: ( DOT OBJECTS LROUND cards= fields RROUND )?
					int alt51=2;
					int LA51_0 = input.LA(1);
					if ( (LA51_0==DOT) ) {
						alt51=1;
					}
					switch (alt51) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:194:55: DOT OBJECTS LROUND cards= fields RROUND
							{
							DOT97=(Token)match(input,DOT,FOLLOW_DOT_in_domain1638); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_DOT.add(DOT97);

							OBJECTS98=(Token)match(input,OBJECTS,FOLLOW_OBJECTS_in_domain1640); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_OBJECTS.add(OBJECTS98);

							LROUND99=(Token)match(input,LROUND,FOLLOW_LROUND_in_domain1642); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_LROUND.add(LROUND99);

							pushFollow(FOLLOW_fields_in_domain1646);
							cards=fields();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_fields.add(cards.getTree());
							RROUND100=(Token)match(input,RROUND,FOLLOW_RROUND_in_domain1648); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_RROUND.add(RROUND100);

							}
							break;

					}

					// AST REWRITE
					// elements: NAME, meta, n, cards
					// token labels: n
					// rule labels: cards, meta, retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleTokenStream stream_n=new RewriteRuleTokenStream(adaptor,"token n",n);
					RewriteRuleSubtreeStream stream_cards=new RewriteRuleSubtreeStream(adaptor,"rule cards",cards!=null?cards.getTree():null);
					RewriteRuleSubtreeStream stream_meta=new RewriteRuleSubtreeStream(adaptor,"rule meta",meta!=null?meta.getTree():null);
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 195:3: -> ^( DOMREF ^( DOMTYPE ( ^( NOT $n) )? ) ^( DOMNAME NAME ) ( ^( DOMVALUE $meta) )? ( ^( DOMCARDS $cards) )? )
					{
						// org/cmdbuild/cql/CQL.g:195:6: ^( DOMREF ^( DOMTYPE ( ^( NOT $n) )? ) ^( DOMNAME NAME ) ( ^( DOMVALUE $meta) )? ( ^( DOMCARDS $cards) )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMREF, "DOMREF"), root_1);
						// org/cmdbuild/cql/CQL.g:196:4: ^( DOMTYPE ( ^( NOT $n) )? )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMTYPE, "DOMTYPE"), root_2);
						// org/cmdbuild/cql/CQL.g:196:14: ( ^( NOT $n) )?
						if ( stream_n.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:196:14: ^( NOT $n)
							{
							Object root_3 = (Object)adaptor.nil();
							root_3 = (Object)adaptor.becomeRoot((Object)adaptor.create(NOT, "NOT"), root_3);
							adaptor.addChild(root_3, stream_n.nextNode());
							adaptor.addChild(root_2, root_3);
							}

						}
						stream_n.reset();

						adaptor.addChild(root_1, root_2);
						}

						// org/cmdbuild/cql/CQL.g:197:4: ^( DOMNAME NAME )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMNAME, "DOMNAME"), root_2);
						adaptor.addChild(root_2, stream_NAME.nextNode());
						adaptor.addChild(root_1, root_2);
						}

						// org/cmdbuild/cql/CQL.g:197:20: ( ^( DOMVALUE $meta) )?
						if ( stream_meta.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:197:20: ^( DOMVALUE $meta)
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMVALUE, "DOMVALUE"), root_2);
							adaptor.addChild(root_2, stream_meta.nextTree());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_meta.reset();

						// org/cmdbuild/cql/CQL.g:197:39: ( ^( DOMCARDS $cards) )?
						if ( stream_cards.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:197:39: ^( DOMCARDS $cards)
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(DOMCARDS, "DOMCARDS"), root_2);
							adaptor.addChild(root_2, stream_cards.nextTree());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_cards.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "domain"


	public static class field_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "field"
	// org/cmdbuild/cql/CQL.g:205:1: field : ( id operator ( val )? -> ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR operator ) ( ^( FIELDVALUE val ) )? ) | id (n= NEG )? ( BTWOP | LROUND ) val ( BTWANDOP | COLON ) val ( RROUND )? -> {$n!=null}? ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR NOTBTW ) ^( FIELDVALUE val val ) ) -> ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR BTW ) ^( FIELDVALUE val val ) ) | id (n= NEG )? ( INOP )? LROUND val ( COMMA val )* RROUND -> {$n!=null}? ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR NOTIN ) ^( FIELDVALUE ( val )+ ) ) -> ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR IN ) ^( FIELDVALUE ( val )+ ) ) );
	public final CQLParser.field_return field() throws RecognitionException {
		CQLParser.field_return retval = new CQLParser.field_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token n=null;
		Token BTWOP105=null;
		Token LROUND106=null;
		Token BTWANDOP108=null;
		Token COLON109=null;
		Token RROUND111=null;
		Token INOP113=null;
		Token LROUND114=null;
		Token COMMA116=null;
		Token RROUND118=null;
		ParserRuleReturnScope id101 =null;
		ParserRuleReturnScope operator102 =null;
		ParserRuleReturnScope val103 =null;
		ParserRuleReturnScope id104 =null;
		ParserRuleReturnScope val107 =null;
		ParserRuleReturnScope val110 =null;
		ParserRuleReturnScope id112 =null;
		ParserRuleReturnScope val115 =null;
		ParserRuleReturnScope val117 =null;

		Object n_tree=null;
		Object BTWOP105_tree=null;
		Object LROUND106_tree=null;
		Object BTWANDOP108_tree=null;
		Object COLON109_tree=null;
		Object RROUND111_tree=null;
		Object INOP113_tree=null;
		Object LROUND114_tree=null;
		Object COMMA116_tree=null;
		Object RROUND118_tree=null;
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleTokenStream stream_NEG=new RewriteRuleTokenStream(adaptor,"token NEG");
		RewriteRuleTokenStream stream_BTWANDOP=new RewriteRuleTokenStream(adaptor,"token BTWANDOP");
		RewriteRuleTokenStream stream_INOP=new RewriteRuleTokenStream(adaptor,"token INOP");
		RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
		RewriteRuleTokenStream stream_RROUND=new RewriteRuleTokenStream(adaptor,"token RROUND");
		RewriteRuleTokenStream stream_LROUND=new RewriteRuleTokenStream(adaptor,"token LROUND");
		RewriteRuleTokenStream stream_BTWOP=new RewriteRuleTokenStream(adaptor,"token BTWOP");
		RewriteRuleSubtreeStream stream_val=new RewriteRuleSubtreeStream(adaptor,"rule val");
		RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
		RewriteRuleSubtreeStream stream_operator=new RewriteRuleSubtreeStream(adaptor,"rule operator");

		try {
			// org/cmdbuild/cql/CQL.g:206:2: ( id operator ( val )? -> ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR operator ) ( ^( FIELDVALUE val ) )? ) | id (n= NEG )? ( BTWOP | LROUND ) val ( BTWANDOP | COLON ) val ( RROUND )? -> {$n!=null}? ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR NOTBTW ) ^( FIELDVALUE val val ) ) -> ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR BTW ) ^( FIELDVALUE val val ) ) | id (n= NEG )? ( INOP )? LROUND val ( COMMA val )* RROUND -> {$n!=null}? ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR NOTIN ) ^( FIELDVALUE ( val )+ ) ) -> ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR IN ) ^( FIELDVALUE ( val )+ ) ) )
			int alt61=3;
			int LA61_0 = input.LA(1);
			if ( (LA61_0==NAME) ) {
				switch ( input.LA(2) ) {
				case DOT:
					{
					int LA61_2 = input.LA(3);
					if ( (LA61_2==142) ) {
						int LA61_16 = input.LA(4);
						if ( (synpred62_CQL()) ) {
							alt61=1;
						}
						else if ( (synpred67_CQL()) ) {
							alt61=2;
						}
						else if ( (true) ) {
							alt61=3;
						}

					}
					else if ( (LA61_2==NAME) ) {
						int LA61_17 = input.LA(4);
						if ( (synpred62_CQL()) ) {
							alt61=1;
						}
						else if ( (synpred67_CQL()) ) {
							alt61=2;
						}
						else if ( (true) ) {
							alt61=3;
						}

					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 61, 2, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case BGNOP:
				case CONTOP:
				case ENDOP:
				case EQOP:
				case GTEQOP:
				case GTOP:
				case LTEQOP:
				case LTOP:
				case NULLOP:
					{
					alt61=1;
					}
					break;
				case NEG:
					{
					switch ( input.LA(3) ) {
					case BGNOP:
					case CONTOP:
					case ENDOP:
					case EQOP:
					case NULLOP:
						{
						alt61=1;
						}
						break;
					case BTWOP:
						{
						alt61=2;
						}
						break;
					case LROUND:
						{
						int LA61_24 = input.LA(4);
						if ( (synpred67_CQL()) ) {
							alt61=2;
						}
						else if ( (true) ) {
							alt61=3;
						}

						}
						break;
					case INOP:
						{
						alt61=3;
						}
						break;
					default:
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 61, 7, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case BTWOP:
					{
					alt61=2;
					}
					break;
				case LROUND:
					{
					switch ( input.LA(3) ) {
					case BOOLTRUE:
						{
						int LA61_26 = input.LA(4);
						if ( (synpred67_CQL()) ) {
							alt61=2;
						}
						else if ( (true) ) {
							alt61=3;
						}

						}
						break;
					case BOOLFALSE:
						{
						int LA61_27 = input.LA(4);
						if ( (synpred67_CQL()) ) {
							alt61=2;
						}
						else if ( (true) ) {
							alt61=3;
						}

						}
						break;
					case DATE:
						{
						int LA61_28 = input.LA(4);
						if ( (synpred67_CQL()) ) {
							alt61=2;
						}
						else if ( (true) ) {
							alt61=3;
						}

						}
						break;
					case TIMESTAMP:
						{
						int LA61_29 = input.LA(4);
						if ( (synpred67_CQL()) ) {
							alt61=2;
						}
						else if ( (true) ) {
							alt61=3;
						}

						}
						break;
					case LGRAPH:
						{
						int LA61_30 = input.LA(4);
						if ( (synpred67_CQL()) ) {
							alt61=2;
						}
						else if ( (true) ) {
							alt61=3;
						}

						}
						break;
					case NATIVEELM:
						{
						int LA61_31 = input.LA(4);
						if ( (synpred67_CQL()) ) {
							alt61=2;
						}
						else if ( (true) ) {
							alt61=3;
						}

						}
						break;
					case LITERAL:
						{
						int LA61_32 = input.LA(4);
						if ( (synpred67_CQL()) ) {
							alt61=2;
						}
						else if ( (true) ) {
							alt61=3;
						}

						}
						break;
					case NUMBER:
						{
						int LA61_33 = input.LA(4);
						if ( (synpred67_CQL()) ) {
							alt61=2;
						}
						else if ( (true) ) {
							alt61=3;
						}

						}
						break;
					case LROUND:
						{
						int LA61_34 = input.LA(4);
						if ( (synpred67_CQL()) ) {
							alt61=2;
						}
						else if ( (true) ) {
							alt61=3;
						}

						}
						break;
					case SELECTOP:
						{
						int LA61_35 = input.LA(4);
						if ( (synpred67_CQL()) ) {
							alt61=2;
						}
						else if ( (true) ) {
							alt61=3;
						}

						}
						break;
					case FROMOP:
						{
						int LA61_36 = input.LA(4);
						if ( (synpred67_CQL()) ) {
							alt61=2;
						}
						else if ( (true) ) {
							alt61=3;
						}

						}
						break;
					default:
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 61, 14, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case INOP:
					{
					alt61=3;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 61, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 61, 0, input);
				throw nvae;
			}

			switch (alt61) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:206:4: id operator ( val )?
					{
					pushFollow(FOLLOW_id_in_field1713);
					id101=id();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_id.add(id101.getTree());
					pushFollow(FOLLOW_operator_in_field1715);
					operator102=operator();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_operator.add(operator102.getTree());
					// org/cmdbuild/cql/CQL.g:206:16: ( val )?
					int alt53=2;
					int LA53_0 = input.LA(1);
					if ( ((LA53_0 >= BOOLFALSE && LA53_0 <= BOOLTRUE)||LA53_0==DATE||LA53_0==FROMOP||LA53_0==LITERAL||LA53_0==LROUND||LA53_0==NATIVEELM||LA53_0==NUMBER||LA53_0==SELECTOP||LA53_0==TIMESTAMP) ) {
						alt53=1;
					}
					else if ( (LA53_0==LGRAPH) ) {
						int LA53_5 = input.LA(2);
						if ( (LA53_5==NAME) ) {
							int LA53_23 = input.LA(3);
							if ( (LA53_23==RGRAPH) ) {
								int LA53_24 = input.LA(4);
								if ( (synpred61_CQL()) ) {
									alt53=1;
								}
							}
						}
					}
					switch (alt53) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:206:16: val
							{
							pushFollow(FOLLOW_val_in_field1717);
							val103=val();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_val.add(val103.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: val, operator, id
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 206:21: -> ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR operator ) ( ^( FIELDVALUE val ) )? )
					{
						// org/cmdbuild/cql/CQL.g:206:24: ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR operator ) ( ^( FIELDVALUE val ) )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD, "FIELD"), root_1);
						// org/cmdbuild/cql/CQL.g:206:32: ^( FIELDID id )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELDID, "FIELDID"), root_2);
						adaptor.addChild(root_2, stream_id.nextTree());
						adaptor.addChild(root_1, root_2);
						}

						// org/cmdbuild/cql/CQL.g:206:46: ^( FIELDOPERATOR operator )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELDOPERATOR, "FIELDOPERATOR"), root_2);
						adaptor.addChild(root_2, stream_operator.nextTree());
						adaptor.addChild(root_1, root_2);
						}

						// org/cmdbuild/cql/CQL.g:206:72: ( ^( FIELDVALUE val ) )?
						if ( stream_val.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:206:72: ^( FIELDVALUE val )
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELDVALUE, "FIELDVALUE"), root_2);
							adaptor.addChild(root_2, stream_val.nextTree());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_val.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:207:4: id (n= NEG )? ( BTWOP | LROUND ) val ( BTWANDOP | COLON ) val ( RROUND )?
					{
					pushFollow(FOLLOW_id_in_field1748);
					id104=id();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_id.add(id104.getTree());
					// org/cmdbuild/cql/CQL.g:207:8: (n= NEG )?
					int alt54=2;
					int LA54_0 = input.LA(1);
					if ( (LA54_0==NEG) ) {
						alt54=1;
					}
					switch (alt54) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:207:8: n= NEG
							{
							n=(Token)match(input,NEG,FOLLOW_NEG_in_field1752); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NEG.add(n);

							}
							break;

					}

					// org/cmdbuild/cql/CQL.g:207:14: ( BTWOP | LROUND )
					int alt55=2;
					int LA55_0 = input.LA(1);
					if ( (LA55_0==BTWOP) ) {
						alt55=1;
					}
					else if ( (LA55_0==LROUND) ) {
						alt55=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 55, 0, input);
						throw nvae;
					}

					switch (alt55) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:207:15: BTWOP
							{
							BTWOP105=(Token)match(input,BTWOP,FOLLOW_BTWOP_in_field1756); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_BTWOP.add(BTWOP105);

							}
							break;
						case 2 :
							// org/cmdbuild/cql/CQL.g:207:21: LROUND
							{
							LROUND106=(Token)match(input,LROUND,FOLLOW_LROUND_in_field1758); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_LROUND.add(LROUND106);

							}
							break;

					}

					pushFollow(FOLLOW_val_in_field1761);
					val107=val();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_val.add(val107.getTree());
					// org/cmdbuild/cql/CQL.g:207:33: ( BTWANDOP | COLON )
					int alt56=2;
					int LA56_0 = input.LA(1);
					if ( (LA56_0==BTWANDOP) ) {
						alt56=1;
					}
					else if ( (LA56_0==COLON) ) {
						alt56=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 56, 0, input);
						throw nvae;
					}

					switch (alt56) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:207:34: BTWANDOP
							{
							BTWANDOP108=(Token)match(input,BTWANDOP,FOLLOW_BTWANDOP_in_field1764); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_BTWANDOP.add(BTWANDOP108);

							}
							break;
						case 2 :
							// org/cmdbuild/cql/CQL.g:207:43: COLON
							{
							COLON109=(Token)match(input,COLON,FOLLOW_COLON_in_field1766); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_COLON.add(COLON109);

							}
							break;

					}

					pushFollow(FOLLOW_val_in_field1769);
					val110=val();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_val.add(val110.getTree());
					// org/cmdbuild/cql/CQL.g:207:54: ( RROUND )?
					int alt57=2;
					int LA57_0 = input.LA(1);
					if ( (LA57_0==RROUND) ) {
						int LA57_1 = input.LA(2);
						if ( (synpred66_CQL()) ) {
							alt57=1;
						}
					}
					switch (alt57) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:207:55: RROUND
							{
							RROUND111=(Token)match(input,RROUND,FOLLOW_RROUND_in_field1772); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_RROUND.add(RROUND111);

							}
							break;

					}

					// AST REWRITE
					// elements: val, id, val, val, val, id
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 208:8: -> {$n!=null}? ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR NOTBTW ) ^( FIELDVALUE val val ) )
					if (n!=null) {
						// org/cmdbuild/cql/CQL.g:208:23: ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR NOTBTW ) ^( FIELDVALUE val val ) )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD, "FIELD"), root_1);
						// org/cmdbuild/cql/CQL.g:208:31: ^( FIELDID id )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELDID, "FIELDID"), root_2);
						adaptor.addChild(root_2, stream_id.nextTree());
						adaptor.addChild(root_1, root_2);
						}

						// org/cmdbuild/cql/CQL.g:208:45: ^( FIELDOPERATOR NOTBTW )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELDOPERATOR, "FIELDOPERATOR"), root_2);
						adaptor.addChild(root_2, (Object)adaptor.create(NOTBTW, "NOTBTW"));
						adaptor.addChild(root_1, root_2);
						}

						// org/cmdbuild/cql/CQL.g:208:69: ^( FIELDVALUE val val )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELDVALUE, "FIELDVALUE"), root_2);
						adaptor.addChild(root_2, stream_val.nextTree());
						adaptor.addChild(root_2, stream_val.nextTree());
						adaptor.addChild(root_1, root_2);
						}

						adaptor.addChild(root_0, root_1);
						}

					}

					else // 209:8: -> ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR BTW ) ^( FIELDVALUE val val ) )
					{
						// org/cmdbuild/cql/CQL.g:209:14: ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR BTW ) ^( FIELDVALUE val val ) )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD, "FIELD"), root_1);
						// org/cmdbuild/cql/CQL.g:209:22: ^( FIELDID id )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELDID, "FIELDID"), root_2);
						adaptor.addChild(root_2, stream_id.nextTree());
						adaptor.addChild(root_1, root_2);
						}

						// org/cmdbuild/cql/CQL.g:209:36: ^( FIELDOPERATOR BTW )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELDOPERATOR, "FIELDOPERATOR"), root_2);
						adaptor.addChild(root_2, (Object)adaptor.create(BTW, "BTW"));
						adaptor.addChild(root_1, root_2);
						}

						// org/cmdbuild/cql/CQL.g:209:57: ^( FIELDVALUE val val )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELDVALUE, "FIELDVALUE"), root_2);
						adaptor.addChild(root_2, stream_val.nextTree());
						adaptor.addChild(root_2, stream_val.nextTree());
						adaptor.addChild(root_1, root_2);
						}

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// org/cmdbuild/cql/CQL.g:210:4: id (n= NEG )? ( INOP )? LROUND val ( COMMA val )* RROUND
					{
					pushFollow(FOLLOW_id_in_field1850);
					id112=id();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_id.add(id112.getTree());
					// org/cmdbuild/cql/CQL.g:210:8: (n= NEG )?
					int alt58=2;
					int LA58_0 = input.LA(1);
					if ( (LA58_0==NEG) ) {
						alt58=1;
					}
					switch (alt58) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:210:8: n= NEG
							{
							n=(Token)match(input,NEG,FOLLOW_NEG_in_field1854); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NEG.add(n);

							}
							break;

					}

					// org/cmdbuild/cql/CQL.g:210:14: ( INOP )?
					int alt59=2;
					int LA59_0 = input.LA(1);
					if ( (LA59_0==INOP) ) {
						alt59=1;
					}
					switch (alt59) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:210:14: INOP
							{
							INOP113=(Token)match(input,INOP,FOLLOW_INOP_in_field1857); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_INOP.add(INOP113);

							}
							break;

					}

					LROUND114=(Token)match(input,LROUND,FOLLOW_LROUND_in_field1860); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LROUND.add(LROUND114);

					pushFollow(FOLLOW_val_in_field1862);
					val115=val();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_val.add(val115.getTree());
					// org/cmdbuild/cql/CQL.g:210:31: ( COMMA val )*
					loop60:
					while (true) {
						int alt60=2;
						int LA60_0 = input.LA(1);
						if ( (LA60_0==COMMA) ) {
							alt60=1;
						}

						switch (alt60) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:210:32: COMMA val
							{
							COMMA116=(Token)match(input,COMMA,FOLLOW_COMMA_in_field1865); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_COMMA.add(COMMA116);

							pushFollow(FOLLOW_val_in_field1867);
							val117=val();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_val.add(val117.getTree());
							}
							break;

						default :
							break loop60;
						}
					}

					RROUND118=(Token)match(input,RROUND,FOLLOW_RROUND_in_field1871); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RROUND.add(RROUND118);

					// AST REWRITE
					// elements: val, val, id, id
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 211:8: -> {$n!=null}? ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR NOTIN ) ^( FIELDVALUE ( val )+ ) )
					if (n!=null) {
						// org/cmdbuild/cql/CQL.g:211:23: ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR NOTIN ) ^( FIELDVALUE ( val )+ ) )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD, "FIELD"), root_1);
						// org/cmdbuild/cql/CQL.g:211:31: ^( FIELDID id )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELDID, "FIELDID"), root_2);
						adaptor.addChild(root_2, stream_id.nextTree());
						adaptor.addChild(root_1, root_2);
						}

						// org/cmdbuild/cql/CQL.g:211:45: ^( FIELDOPERATOR NOTIN )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELDOPERATOR, "FIELDOPERATOR"), root_2);
						adaptor.addChild(root_2, (Object)adaptor.create(NOTIN, "NOTIN"));
						adaptor.addChild(root_1, root_2);
						}

						// org/cmdbuild/cql/CQL.g:211:68: ^( FIELDVALUE ( val )+ )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELDVALUE, "FIELDVALUE"), root_2);
						if ( !(stream_val.hasNext()) ) {
							throw new RewriteEarlyExitException();
						}
						while ( stream_val.hasNext() ) {
							adaptor.addChild(root_2, stream_val.nextTree());
						}
						stream_val.reset();

						adaptor.addChild(root_1, root_2);
						}

						adaptor.addChild(root_0, root_1);
						}

					}

					else // 212:8: -> ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR IN ) ^( FIELDVALUE ( val )+ ) )
					{
						// org/cmdbuild/cql/CQL.g:212:14: ^( FIELD ^( FIELDID id ) ^( FIELDOPERATOR IN ) ^( FIELDVALUE ( val )+ ) )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD, "FIELD"), root_1);
						// org/cmdbuild/cql/CQL.g:212:22: ^( FIELDID id )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELDID, "FIELDID"), root_2);
						adaptor.addChild(root_2, stream_id.nextTree());
						adaptor.addChild(root_1, root_2);
						}

						// org/cmdbuild/cql/CQL.g:212:36: ^( FIELDOPERATOR IN )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELDOPERATOR, "FIELDOPERATOR"), root_2);
						adaptor.addChild(root_2, (Object)adaptor.create(IN, "IN"));
						adaptor.addChild(root_1, root_2);
						}

						// org/cmdbuild/cql/CQL.g:212:56: ^( FIELDVALUE ( val )+ )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELDVALUE, "FIELDVALUE"), root_2);
						if ( !(stream_val.hasNext()) ) {
							throw new RewriteEarlyExitException();
						}
						while ( stream_val.hasNext() ) {
							adaptor.addChild(root_2, stream_val.nextTree());
						}
						stream_val.reset();

						adaptor.addChild(root_1, root_2);
						}

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "field"


	public static class operator_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "operator"
	// org/cmdbuild/cql/CQL.g:215:1: operator : ( LTEQOP -> ^( LTEQ ) | GTEQOP -> ^( GTEQ ) | LTOP -> ^( LT ) | GTOP -> ^( GT ) | (n= NEG )? CONTOP -> {$n!=null}? ^( NOTCONT ) -> ^( CONT ) | (n= NEG )? BGNOP -> {$n!=null}? ^( NOTBGN ) -> ^( BGN ) | (n= NEG )? ENDOP -> {$n!=null}? ^( NOTEND ) -> ^( END ) | (n= NEG )? EQOP -> {$n!=null}? ^( NOTEQ ) -> ^( EQ ) | (n= NEG )? NULLOP -> {$n!=null}? ^( ISNOTNULL ) -> ^( ISNULL ) );
	public final CQLParser.operator_return operator() throws RecognitionException {
		CQLParser.operator_return retval = new CQLParser.operator_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token n=null;
		Token LTEQOP119=null;
		Token GTEQOP120=null;
		Token LTOP121=null;
		Token GTOP122=null;
		Token CONTOP123=null;
		Token BGNOP124=null;
		Token ENDOP125=null;
		Token EQOP126=null;
		Token NULLOP127=null;

		Object n_tree=null;
		Object LTEQOP119_tree=null;
		Object GTEQOP120_tree=null;
		Object LTOP121_tree=null;
		Object GTOP122_tree=null;
		Object CONTOP123_tree=null;
		Object BGNOP124_tree=null;
		Object ENDOP125_tree=null;
		Object EQOP126_tree=null;
		Object NULLOP127_tree=null;
		RewriteRuleTokenStream stream_NEG=new RewriteRuleTokenStream(adaptor,"token NEG");
		RewriteRuleTokenStream stream_CONTOP=new RewriteRuleTokenStream(adaptor,"token CONTOP");
		RewriteRuleTokenStream stream_GTEQOP=new RewriteRuleTokenStream(adaptor,"token GTEQOP");
		RewriteRuleTokenStream stream_NULLOP=new RewriteRuleTokenStream(adaptor,"token NULLOP");
		RewriteRuleTokenStream stream_LTEQOP=new RewriteRuleTokenStream(adaptor,"token LTEQOP");
		RewriteRuleTokenStream stream_BGNOP=new RewriteRuleTokenStream(adaptor,"token BGNOP");
		RewriteRuleTokenStream stream_LTOP=new RewriteRuleTokenStream(adaptor,"token LTOP");
		RewriteRuleTokenStream stream_ENDOP=new RewriteRuleTokenStream(adaptor,"token ENDOP");
		RewriteRuleTokenStream stream_EQOP=new RewriteRuleTokenStream(adaptor,"token EQOP");
		RewriteRuleTokenStream stream_GTOP=new RewriteRuleTokenStream(adaptor,"token GTOP");

		try {
			// org/cmdbuild/cql/CQL.g:216:2: ( LTEQOP -> ^( LTEQ ) | GTEQOP -> ^( GTEQ ) | LTOP -> ^( LT ) | GTOP -> ^( GT ) | (n= NEG )? CONTOP -> {$n!=null}? ^( NOTCONT ) -> ^( CONT ) | (n= NEG )? BGNOP -> {$n!=null}? ^( NOTBGN ) -> ^( BGN ) | (n= NEG )? ENDOP -> {$n!=null}? ^( NOTEND ) -> ^( END ) | (n= NEG )? EQOP -> {$n!=null}? ^( NOTEQ ) -> ^( EQ ) | (n= NEG )? NULLOP -> {$n!=null}? ^( ISNOTNULL ) -> ^( ISNULL ) )
			int alt67=9;
			switch ( input.LA(1) ) {
			case LTEQOP:
				{
				alt67=1;
				}
				break;
			case GTEQOP:
				{
				alt67=2;
				}
				break;
			case LTOP:
				{
				alt67=3;
				}
				break;
			case GTOP:
				{
				alt67=4;
				}
				break;
			case NEG:
				{
				switch ( input.LA(2) ) {
				case CONTOP:
					{
					alt67=5;
					}
					break;
				case BGNOP:
					{
					alt67=6;
					}
					break;
				case ENDOP:
					{
					alt67=7;
					}
					break;
				case EQOP:
					{
					alt67=8;
					}
					break;
				case NULLOP:
					{
					alt67=9;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 67, 5, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case CONTOP:
				{
				alt67=5;
				}
				break;
			case BGNOP:
				{
				alt67=6;
				}
				break;
			case ENDOP:
				{
				alt67=7;
				}
				break;
			case EQOP:
				{
				alt67=8;
				}
				break;
			case NULLOP:
				{
				alt67=9;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 67, 0, input);
				throw nvae;
			}
			switch (alt67) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:216:4: LTEQOP
					{
					LTEQOP119=(Token)match(input,LTEQOP,FOLLOW_LTEQOP_in_operator1951); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LTEQOP.add(LTEQOP119);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 216:14: -> ^( LTEQ )
					{
						// org/cmdbuild/cql/CQL.g:216:17: ^( LTEQ )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LTEQ, "LTEQ"), root_1);
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:217:4: GTEQOP
					{
					GTEQOP120=(Token)match(input,GTEQOP,FOLLOW_GTEQOP_in_operator1965); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_GTEQOP.add(GTEQOP120);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 217:14: -> ^( GTEQ )
					{
						// org/cmdbuild/cql/CQL.g:217:17: ^( GTEQ )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(GTEQ, "GTEQ"), root_1);
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// org/cmdbuild/cql/CQL.g:218:4: LTOP
					{
					LTOP121=(Token)match(input,LTOP,FOLLOW_LTOP_in_operator1979); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LTOP.add(LTOP121);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 218:12: -> ^( LT )
					{
						// org/cmdbuild/cql/CQL.g:218:15: ^( LT )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LT, "LT"), root_1);
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 4 :
					// org/cmdbuild/cql/CQL.g:219:4: GTOP
					{
					GTOP122=(Token)match(input,GTOP,FOLLOW_GTOP_in_operator1993); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_GTOP.add(GTOP122);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 219:12: -> ^( GT )
					{
						// org/cmdbuild/cql/CQL.g:219:15: ^( GT )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(GT, "GT"), root_1);
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 5 :
					// org/cmdbuild/cql/CQL.g:220:4: (n= NEG )? CONTOP
					{
					// org/cmdbuild/cql/CQL.g:220:5: (n= NEG )?
					int alt62=2;
					int LA62_0 = input.LA(1);
					if ( (LA62_0==NEG) ) {
						alt62=1;
					}
					switch (alt62) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:220:5: n= NEG
							{
							n=(Token)match(input,NEG,FOLLOW_NEG_in_operator2009); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NEG.add(n);

							}
							break;

					}

					CONTOP123=(Token)match(input,CONTOP,FOLLOW_CONTOP_in_operator2012); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_CONTOP.add(CONTOP123);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 220:19: -> {$n!=null}? ^( NOTCONT )
					if (n!=null) {
						// org/cmdbuild/cql/CQL.g:220:34: ^( NOTCONT )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NOTCONT, "NOTCONT"), root_1);
						adaptor.addChild(root_0, root_1);
						}

					}

					else // 220:46: -> ^( CONT )
					{
						// org/cmdbuild/cql/CQL.g:220:49: ^( CONT )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CONT, "CONT"), root_1);
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 6 :
					// org/cmdbuild/cql/CQL.g:221:4: (n= NEG )? BGNOP
					{
					// org/cmdbuild/cql/CQL.g:221:5: (n= NEG )?
					int alt63=2;
					int LA63_0 = input.LA(1);
					if ( (LA63_0==NEG) ) {
						alt63=1;
					}
					switch (alt63) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:221:5: n= NEG
							{
							n=(Token)match(input,NEG,FOLLOW_NEG_in_operator2035); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NEG.add(n);

							}
							break;

					}

					BGNOP124=(Token)match(input,BGNOP,FOLLOW_BGNOP_in_operator2038); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_BGNOP.add(BGNOP124);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 221:18: -> {$n!=null}? ^( NOTBGN )
					if (n!=null) {
						// org/cmdbuild/cql/CQL.g:221:33: ^( NOTBGN )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NOTBGN, "NOTBGN"), root_1);
						adaptor.addChild(root_0, root_1);
						}

					}

					else // 221:44: -> ^( BGN )
					{
						// org/cmdbuild/cql/CQL.g:221:47: ^( BGN )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BGN, "BGN"), root_1);
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 7 :
					// org/cmdbuild/cql/CQL.g:222:4: (n= NEG )? ENDOP
					{
					// org/cmdbuild/cql/CQL.g:222:5: (n= NEG )?
					int alt64=2;
					int LA64_0 = input.LA(1);
					if ( (LA64_0==NEG) ) {
						alt64=1;
					}
					switch (alt64) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:222:5: n= NEG
							{
							n=(Token)match(input,NEG,FOLLOW_NEG_in_operator2061); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NEG.add(n);

							}
							break;

					}

					ENDOP125=(Token)match(input,ENDOP,FOLLOW_ENDOP_in_operator2064); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ENDOP.add(ENDOP125);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 222:18: -> {$n!=null}? ^( NOTEND )
					if (n!=null) {
						// org/cmdbuild/cql/CQL.g:222:33: ^( NOTEND )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NOTEND, "NOTEND"), root_1);
						adaptor.addChild(root_0, root_1);
						}

					}

					else // 222:44: -> ^( END )
					{
						// org/cmdbuild/cql/CQL.g:222:47: ^( END )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(END, "END"), root_1);
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 8 :
					// org/cmdbuild/cql/CQL.g:223:4: (n= NEG )? EQOP
					{
					// org/cmdbuild/cql/CQL.g:223:5: (n= NEG )?
					int alt65=2;
					int LA65_0 = input.LA(1);
					if ( (LA65_0==NEG) ) {
						alt65=1;
					}
					switch (alt65) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:223:5: n= NEG
							{
							n=(Token)match(input,NEG,FOLLOW_NEG_in_operator2087); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NEG.add(n);

							}
							break;

					}

					EQOP126=(Token)match(input,EQOP,FOLLOW_EQOP_in_operator2090); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_EQOP.add(EQOP126);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 223:18: -> {$n!=null}? ^( NOTEQ )
					if (n!=null) {
						// org/cmdbuild/cql/CQL.g:223:33: ^( NOTEQ )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NOTEQ, "NOTEQ"), root_1);
						adaptor.addChild(root_0, root_1);
						}

					}

					else // 223:44: -> ^( EQ )
					{
						// org/cmdbuild/cql/CQL.g:223:47: ^( EQ )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(EQ, "EQ"), root_1);
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 9 :
					// org/cmdbuild/cql/CQL.g:224:4: (n= NEG )? NULLOP
					{
					// org/cmdbuild/cql/CQL.g:224:5: (n= NEG )?
					int alt66=2;
					int LA66_0 = input.LA(1);
					if ( (LA66_0==NEG) ) {
						alt66=1;
					}
					switch (alt66) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:224:5: n= NEG
							{
							n=(Token)match(input,NEG,FOLLOW_NEG_in_operator2115); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NEG.add(n);

							}
							break;

					}

					NULLOP127=(Token)match(input,NULLOP,FOLLOW_NULLOP_in_operator2118); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NULLOP.add(NULLOP127);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 224:19: -> {$n!=null}? ^( ISNOTNULL )
					if (n!=null) {
						// org/cmdbuild/cql/CQL.g:224:34: ^( ISNOTNULL )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ISNOTNULL, "ISNOTNULL"), root_1);
						adaptor.addChild(root_0, root_1);
						}

					}

					else // 224:48: -> ^( ISNULL )
					{
						// org/cmdbuild/cql/CQL.g:224:51: ^( ISNULL )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ISNULL, "ISNULL"), root_1);
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "operator"


	public static class val_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "val"
	// org/cmdbuild/cql/CQL.g:236:1: val : ( BOOLTRUE -> ^( LITBOOL TRUE ) | BOOLFALSE -> ^( LITBOOL FALSE ) | DATE -> ^( LITDATE DATE ) | TIMESTAMP -> ^( LITTIMESTAMP TIMESTAMP ) | LGRAPH NAME RGRAPH -> ^( INPUTVAL NAME ) | NATIVEELM -> ^( NATIVE NATIVEELM ) | LITERAL -> ^( LITSTR LITERAL ) | NUMBER -> ^( LITNUM NUMBER ) | expr );
	public final CQLParser.val_return val() throws RecognitionException {
		CQLParser.val_return retval = new CQLParser.val_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token BOOLTRUE128=null;
		Token BOOLFALSE129=null;
		Token DATE130=null;
		Token TIMESTAMP131=null;
		Token LGRAPH132=null;
		Token NAME133=null;
		Token RGRAPH134=null;
		Token NATIVEELM135=null;
		Token LITERAL136=null;
		Token NUMBER137=null;
		ParserRuleReturnScope expr138 =null;

		Object BOOLTRUE128_tree=null;
		Object BOOLFALSE129_tree=null;
		Object DATE130_tree=null;
		Object TIMESTAMP131_tree=null;
		Object LGRAPH132_tree=null;
		Object NAME133_tree=null;
		Object RGRAPH134_tree=null;
		Object NATIVEELM135_tree=null;
		Object LITERAL136_tree=null;
		Object NUMBER137_tree=null;
		RewriteRuleTokenStream stream_RGRAPH=new RewriteRuleTokenStream(adaptor,"token RGRAPH");
		RewriteRuleTokenStream stream_NATIVEELM=new RewriteRuleTokenStream(adaptor,"token NATIVEELM");
		RewriteRuleTokenStream stream_DATE=new RewriteRuleTokenStream(adaptor,"token DATE");
		RewriteRuleTokenStream stream_NUMBER=new RewriteRuleTokenStream(adaptor,"token NUMBER");
		RewriteRuleTokenStream stream_LITERAL=new RewriteRuleTokenStream(adaptor,"token LITERAL");
		RewriteRuleTokenStream stream_BOOLTRUE=new RewriteRuleTokenStream(adaptor,"token BOOLTRUE");
		RewriteRuleTokenStream stream_TIMESTAMP=new RewriteRuleTokenStream(adaptor,"token TIMESTAMP");
		RewriteRuleTokenStream stream_LGRAPH=new RewriteRuleTokenStream(adaptor,"token LGRAPH");
		RewriteRuleTokenStream stream_BOOLFALSE=new RewriteRuleTokenStream(adaptor,"token BOOLFALSE");
		RewriteRuleTokenStream stream_NAME=new RewriteRuleTokenStream(adaptor,"token NAME");

		try {
			// org/cmdbuild/cql/CQL.g:237:2: ( BOOLTRUE -> ^( LITBOOL TRUE ) | BOOLFALSE -> ^( LITBOOL FALSE ) | DATE -> ^( LITDATE DATE ) | TIMESTAMP -> ^( LITTIMESTAMP TIMESTAMP ) | LGRAPH NAME RGRAPH -> ^( INPUTVAL NAME ) | NATIVEELM -> ^( NATIVE NATIVEELM ) | LITERAL -> ^( LITSTR LITERAL ) | NUMBER -> ^( LITNUM NUMBER ) | expr )
			int alt68=9;
			switch ( input.LA(1) ) {
			case BOOLTRUE:
				{
				alt68=1;
				}
				break;
			case BOOLFALSE:
				{
				alt68=2;
				}
				break;
			case DATE:
				{
				alt68=3;
				}
				break;
			case TIMESTAMP:
				{
				alt68=4;
				}
				break;
			case LGRAPH:
				{
				alt68=5;
				}
				break;
			case NATIVEELM:
				{
				alt68=6;
				}
				break;
			case LITERAL:
				{
				alt68=7;
				}
				break;
			case NUMBER:
				{
				alt68=8;
				}
				break;
			case FROMOP:
			case LROUND:
			case SELECTOP:
				{
				alt68=9;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 68, 0, input);
				throw nvae;
			}
			switch (alt68) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:237:4: BOOLTRUE
					{
					BOOLTRUE128=(Token)match(input,BOOLTRUE,FOLLOW_BOOLTRUE_in_val2147); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_BOOLTRUE.add(BOOLTRUE128);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 237:16: -> ^( LITBOOL TRUE )
					{
						// org/cmdbuild/cql/CQL.g:237:19: ^( LITBOOL TRUE )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LITBOOL, "LITBOOL"), root_1);
						adaptor.addChild(root_1, (Object)adaptor.create(TRUE, "TRUE"));
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:238:4: BOOLFALSE
					{
					BOOLFALSE129=(Token)match(input,BOOLFALSE,FOLLOW_BOOLFALSE_in_val2163); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_BOOLFALSE.add(BOOLFALSE129);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 238:17: -> ^( LITBOOL FALSE )
					{
						// org/cmdbuild/cql/CQL.g:238:20: ^( LITBOOL FALSE )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LITBOOL, "LITBOOL"), root_1);
						adaptor.addChild(root_1, (Object)adaptor.create(FALSE, "FALSE"));
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// org/cmdbuild/cql/CQL.g:239:4: DATE
					{
					DATE130=(Token)match(input,DATE,FOLLOW_DATE_in_val2179); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_DATE.add(DATE130);

					// AST REWRITE
					// elements: DATE
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 239:13: -> ^( LITDATE DATE )
					{
						// org/cmdbuild/cql/CQL.g:239:16: ^( LITDATE DATE )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LITDATE, "LITDATE"), root_1);
						adaptor.addChild(root_1, stream_DATE.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 4 :
					// org/cmdbuild/cql/CQL.g:240:4: TIMESTAMP
					{
					TIMESTAMP131=(Token)match(input,TIMESTAMP,FOLLOW_TIMESTAMP_in_val2196); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_TIMESTAMP.add(TIMESTAMP131);

					// AST REWRITE
					// elements: TIMESTAMP
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 240:17: -> ^( LITTIMESTAMP TIMESTAMP )
					{
						// org/cmdbuild/cql/CQL.g:240:20: ^( LITTIMESTAMP TIMESTAMP )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LITTIMESTAMP, "LITTIMESTAMP"), root_1);
						adaptor.addChild(root_1, stream_TIMESTAMP.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 5 :
					// org/cmdbuild/cql/CQL.g:241:4: LGRAPH NAME RGRAPH
					{
					LGRAPH132=(Token)match(input,LGRAPH,FOLLOW_LGRAPH_in_val2212); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LGRAPH.add(LGRAPH132);

					NAME133=(Token)match(input,NAME,FOLLOW_NAME_in_val2214); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NAME.add(NAME133);

					RGRAPH134=(Token)match(input,RGRAPH,FOLLOW_RGRAPH_in_val2216); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RGRAPH.add(RGRAPH134);

					// AST REWRITE
					// elements: NAME
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 241:24: -> ^( INPUTVAL NAME )
					{
						// org/cmdbuild/cql/CQL.g:241:27: ^( INPUTVAL NAME )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(INPUTVAL, "INPUTVAL"), root_1);
						adaptor.addChild(root_1, stream_NAME.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 6 :
					// org/cmdbuild/cql/CQL.g:242:4: NATIVEELM
					{
					NATIVEELM135=(Token)match(input,NATIVEELM,FOLLOW_NATIVEELM_in_val2230); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NATIVEELM.add(NATIVEELM135);

					// AST REWRITE
					// elements: NATIVEELM
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 242:17: -> ^( NATIVE NATIVEELM )
					{
						// org/cmdbuild/cql/CQL.g:242:20: ^( NATIVE NATIVEELM )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NATIVE, "NATIVE"), root_1);
						adaptor.addChild(root_1, stream_NATIVEELM.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 7 :
					// org/cmdbuild/cql/CQL.g:243:4: LITERAL
					{
					LITERAL136=(Token)match(input,LITERAL,FOLLOW_LITERAL_in_val2246); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LITERAL.add(LITERAL136);

					// AST REWRITE
					// elements: LITERAL
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 243:16: -> ^( LITSTR LITERAL )
					{
						// org/cmdbuild/cql/CQL.g:243:19: ^( LITSTR LITERAL )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LITSTR, "LITSTR"), root_1);
						adaptor.addChild(root_1, stream_LITERAL.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 8 :
					// org/cmdbuild/cql/CQL.g:244:4: NUMBER
					{
					NUMBER137=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_val2263); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NUMBER.add(NUMBER137);

					// AST REWRITE
					// elements: NUMBER
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 244:15: -> ^( LITNUM NUMBER )
					{
						// org/cmdbuild/cql/CQL.g:244:18: ^( LITNUM NUMBER )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LITNUM, "LITNUM"), root_1);
						adaptor.addChild(root_1, stream_NUMBER.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 9 :
					// org/cmdbuild/cql/CQL.g:245:4: expr
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_expr_in_val2280);
					expr138=expr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, expr138.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "val"


	public static class id_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "id"
	// org/cmdbuild/cql/CQL.g:256:1: id : ( NAME -> ^( ATTRIBUTEID NAME ) | (cdref= NAME DOT )? attr= NAME lookupOp -> ^( ATTRIBUTEID $attr ^( LOOKUP lookupOp ) ( ^( CLASSDOMREF $cdref) )? ) | NAME ( DOT NAME )+ -> ^( ATTRIBUTEID ( NAME )+ ) );
	public final CQLParser.id_return id() throws RecognitionException {
		CQLParser.id_return retval = new CQLParser.id_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token cdref=null;
		Token attr=null;
		Token NAME139=null;
		Token DOT140=null;
		Token NAME142=null;
		Token DOT143=null;
		Token NAME144=null;
		ParserRuleReturnScope lookupOp141 =null;

		Object cdref_tree=null;
		Object attr_tree=null;
		Object NAME139_tree=null;
		Object DOT140_tree=null;
		Object NAME142_tree=null;
		Object DOT143_tree=null;
		Object NAME144_tree=null;
		RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
		RewriteRuleTokenStream stream_NAME=new RewriteRuleTokenStream(adaptor,"token NAME");
		RewriteRuleSubtreeStream stream_lookupOp=new RewriteRuleSubtreeStream(adaptor,"rule lookupOp");

		try {
			// org/cmdbuild/cql/CQL.g:256:4: ( NAME -> ^( ATTRIBUTEID NAME ) | (cdref= NAME DOT )? attr= NAME lookupOp -> ^( ATTRIBUTEID $attr ^( LOOKUP lookupOp ) ( ^( CLASSDOMREF $cdref) )? ) | NAME ( DOT NAME )+ -> ^( ATTRIBUTEID ( NAME )+ ) )
			int alt71=3;
			int LA71_0 = input.LA(1);
			if ( (LA71_0==NAME) ) {
				int LA71_1 = input.LA(2);
				if ( (LA71_1==DOT) ) {
					int LA71_2 = input.LA(3);
					if ( (LA71_2==142) ) {
						alt71=2;
					}
					else if ( (LA71_2==NAME) ) {
						int LA71_17 = input.LA(4);
						if ( (synpred94_CQL()) ) {
							alt71=2;
						}
						else if ( (true) ) {
							alt71=3;
						}

					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 71, 2, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}
				else if ( (LA71_1==BGNOP||LA71_1==BTWOP||LA71_1==CONTOP||LA71_1==ENDOP||LA71_1==EQOP||(LA71_1 >= GTEQOP && LA71_1 <= GTOP)||LA71_1==INOP||LA71_1==LROUND||(LA71_1 >= LTEQOP && LA71_1 <= LTOP)||LA71_1==NEG||LA71_1==NULLOP) ) {
					alt71=1;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 71, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 71, 0, input);
				throw nvae;
			}

			switch (alt71) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:256:6: NAME
					{
					NAME139=(Token)match(input,NAME,FOLLOW_NAME_in_id2291); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NAME.add(NAME139);

					// AST REWRITE
					// elements: NAME
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 256:18: -> ^( ATTRIBUTEID NAME )
					{
						// org/cmdbuild/cql/CQL.g:256:21: ^( ATTRIBUTEID NAME )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATTRIBUTEID, "ATTRIBUTEID"), root_1);
						adaptor.addChild(root_1, stream_NAME.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// org/cmdbuild/cql/CQL.g:257:4: (cdref= NAME DOT )? attr= NAME lookupOp
					{
					// org/cmdbuild/cql/CQL.g:257:4: (cdref= NAME DOT )?
					int alt69=2;
					int LA69_0 = input.LA(1);
					if ( (LA69_0==NAME) ) {
						int LA69_1 = input.LA(2);
						if ( (LA69_1==DOT) ) {
							int LA69_2 = input.LA(3);
							if ( (LA69_2==NAME) ) {
								alt69=1;
							}
						}
					}
					switch (alt69) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:257:5: cdref= NAME DOT
							{
							cdref=(Token)match(input,NAME,FOLLOW_NAME_in_id2314); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NAME.add(cdref);

							DOT140=(Token)match(input,DOT,FOLLOW_DOT_in_id2316); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_DOT.add(DOT140);

							}
							break;

					}

					attr=(Token)match(input,NAME,FOLLOW_NAME_in_id2322); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NAME.add(attr);

					pushFollow(FOLLOW_lookupOp_in_id2324);
					lookupOp141=lookupOp();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_lookupOp.add(lookupOp141.getTree());
					// AST REWRITE
					// elements: attr, lookupOp, cdref
					// token labels: cdref, attr
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleTokenStream stream_cdref=new RewriteRuleTokenStream(adaptor,"token cdref",cdref);
					RewriteRuleTokenStream stream_attr=new RewriteRuleTokenStream(adaptor,"token attr",attr);
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 257:42: -> ^( ATTRIBUTEID $attr ^( LOOKUP lookupOp ) ( ^( CLASSDOMREF $cdref) )? )
					{
						// org/cmdbuild/cql/CQL.g:257:45: ^( ATTRIBUTEID $attr ^( LOOKUP lookupOp ) ( ^( CLASSDOMREF $cdref) )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATTRIBUTEID, "ATTRIBUTEID"), root_1);
						adaptor.addChild(root_1, stream_attr.nextNode());
						// org/cmdbuild/cql/CQL.g:257:65: ^( LOOKUP lookupOp )
						{
						Object root_2 = (Object)adaptor.nil();
						root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(LOOKUP, "LOOKUP"), root_2);
						adaptor.addChild(root_2, stream_lookupOp.nextTree());
						adaptor.addChild(root_1, root_2);
						}

						// org/cmdbuild/cql/CQL.g:257:84: ( ^( CLASSDOMREF $cdref) )?
						if ( stream_cdref.hasNext() ) {
							// org/cmdbuild/cql/CQL.g:257:84: ^( CLASSDOMREF $cdref)
							{
							Object root_2 = (Object)adaptor.nil();
							root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(CLASSDOMREF, "CLASSDOMREF"), root_2);
							adaptor.addChild(root_2, stream_cdref.nextNode());
							adaptor.addChild(root_1, root_2);
							}

						}
						stream_cdref.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// org/cmdbuild/cql/CQL.g:258:4: NAME ( DOT NAME )+
					{
					NAME142=(Token)match(input,NAME,FOLLOW_NAME_in_id2353); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NAME.add(NAME142);

					// org/cmdbuild/cql/CQL.g:258:9: ( DOT NAME )+
					int cnt70=0;
					loop70:
					while (true) {
						int alt70=2;
						int LA70_0 = input.LA(1);
						if ( (LA70_0==DOT) ) {
							alt70=1;
						}

						switch (alt70) {
						case 1 :
							// org/cmdbuild/cql/CQL.g:258:10: DOT NAME
							{
							DOT143=(Token)match(input,DOT,FOLLOW_DOT_in_id2356); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_DOT.add(DOT143);

							NAME144=(Token)match(input,NAME,FOLLOW_NAME_in_id2358); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NAME.add(NAME144);

							}
							break;

						default :
							if ( cnt70 >= 1 ) break loop70;
							if (state.backtracking>0) {state.failed=true; return retval;}
							EarlyExitException eee = new EarlyExitException(70, input);
							throw eee;
						}
						cnt70++;
					}

					// AST REWRITE
					// elements: NAME
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 258:26: -> ^( ATTRIBUTEID ( NAME )+ )
					{
						// org/cmdbuild/cql/CQL.g:258:29: ^( ATTRIBUTEID ( NAME )+ )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATTRIBUTEID, "ATTRIBUTEID"), root_1);
						if ( !(stream_NAME.hasNext()) ) {
							throw new RewriteEarlyExitException();
						}
						while ( stream_NAME.hasNext() ) {
							adaptor.addChild(root_1, stream_NAME.nextNode());
						}
						stream_NAME.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "id"


	public static class lookupOp_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "lookupOp"
	// org/cmdbuild/cql/CQL.g:260:1: lookupOp : DOT 'parent()' ( lookupOp )? ( DOT NAME )? -> ^( LOOKUPPARENT ( ^( lookupOp ) )? ( ^( ATTRIBUTE NAME ) )? ) ;
	public final CQLParser.lookupOp_return lookupOp() throws RecognitionException {
		CQLParser.lookupOp_return retval = new CQLParser.lookupOp_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token DOT145=null;
		Token string_literal146=null;
		Token DOT148=null;
		Token NAME149=null;
		ParserRuleReturnScope lookupOp147 =null;

		Object DOT145_tree=null;
		Object string_literal146_tree=null;
		Object DOT148_tree=null;
		Object NAME149_tree=null;
		RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
		RewriteRuleTokenStream stream_142=new RewriteRuleTokenStream(adaptor,"token 142");
		RewriteRuleTokenStream stream_NAME=new RewriteRuleTokenStream(adaptor,"token NAME");
		RewriteRuleSubtreeStream stream_lookupOp=new RewriteRuleSubtreeStream(adaptor,"rule lookupOp");

		try {
			// org/cmdbuild/cql/CQL.g:261:2: ( DOT 'parent()' ( lookupOp )? ( DOT NAME )? -> ^( LOOKUPPARENT ( ^( lookupOp ) )? ( ^( ATTRIBUTE NAME ) )? ) )
			// org/cmdbuild/cql/CQL.g:261:4: DOT 'parent()' ( lookupOp )? ( DOT NAME )?
			{
			DOT145=(Token)match(input,DOT,FOLLOW_DOT_in_lookupOp2384); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_DOT.add(DOT145);

			string_literal146=(Token)match(input,142,FOLLOW_142_in_lookupOp2386); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_142.add(string_literal146);

			// org/cmdbuild/cql/CQL.g:261:19: ( lookupOp )?
			int alt72=2;
			int LA72_0 = input.LA(1);
			if ( (LA72_0==DOT) ) {
				int LA72_1 = input.LA(2);
				if ( (LA72_1==142) ) {
					alt72=1;
				}
			}
			switch (alt72) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:261:20: lookupOp
					{
					pushFollow(FOLLOW_lookupOp_in_lookupOp2389);
					lookupOp147=lookupOp();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_lookupOp.add(lookupOp147.getTree());
					}
					break;

			}

			// org/cmdbuild/cql/CQL.g:261:31: ( DOT NAME )?
			int alt73=2;
			int LA73_0 = input.LA(1);
			if ( (LA73_0==DOT) ) {
				int LA73_1 = input.LA(2);
				if ( (synpred97_CQL()) ) {
					alt73=1;
				}
			}
			switch (alt73) {
				case 1 :
					// org/cmdbuild/cql/CQL.g:261:32: DOT NAME
					{
					DOT148=(Token)match(input,DOT,FOLLOW_DOT_in_lookupOp2394); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_DOT.add(DOT148);

					NAME149=(Token)match(input,NAME,FOLLOW_NAME_in_lookupOp2396); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NAME.add(NAME149);

					}
					break;

			}

			// AST REWRITE
			// elements: lookupOp, NAME
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 261:44: -> ^( LOOKUPPARENT ( ^( lookupOp ) )? ( ^( ATTRIBUTE NAME ) )? )
			{
				// org/cmdbuild/cql/CQL.g:261:47: ^( LOOKUPPARENT ( ^( lookupOp ) )? ( ^( ATTRIBUTE NAME ) )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LOOKUPPARENT, "LOOKUPPARENT"), root_1);
				// org/cmdbuild/cql/CQL.g:261:62: ( ^( lookupOp ) )?
				if ( stream_lookupOp.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:261:62: ^( lookupOp )
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot(stream_lookupOp.nextNode(), root_2);
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_lookupOp.reset();

				// org/cmdbuild/cql/CQL.g:261:75: ( ^( ATTRIBUTE NAME ) )?
				if ( stream_NAME.hasNext() ) {
					// org/cmdbuild/cql/CQL.g:261:75: ^( ATTRIBUTE NAME )
					{
					Object root_2 = (Object)adaptor.nil();
					root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATTRIBUTE, "ATTRIBUTE"), root_2);
					adaptor.addChild(root_2, stream_NAME.nextNode());
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_NAME.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "lookupOp"

	// $ANTLR start synpred5_CQL
	public final void synpred5_CQL_fragment() throws RecognitionException {
		// org/cmdbuild/cql/CQL.g:76:38: ( COMMA fromref )
		// org/cmdbuild/cql/CQL.g:76:38: COMMA fromref
		{
		match(input,COMMA,FOLLOW_COMMA_in_synpred5_CQL381); if (state.failed) return;

		pushFollow(FOLLOW_fromref_in_synpred5_CQL383);
		fromref();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred5_CQL

	// $ANTLR start synpred7_CQL
	public final void synpred7_CQL_fragment() throws RecognitionException {
		ParserRuleReturnScope groupby =null;


		// org/cmdbuild/cql/CQL.g:78:4: ( GROUPBYOP groupby= selattrs )
		// org/cmdbuild/cql/CQL.g:78:4: GROUPBYOP groupby= selattrs
		{
		match(input,GROUPBYOP,FOLLOW_GROUPBYOP_in_synpred7_CQL401); if (state.failed) return;

		pushFollow(FOLLOW_selattrs_in_synpred7_CQL405);
		groupby=selattrs();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred7_CQL

	// $ANTLR start synpred9_CQL
	public final void synpred9_CQL_fragment() throws RecognitionException {
		// org/cmdbuild/cql/CQL.g:79:4: ( ORDEROP order ( COMMA order )* )
		// org/cmdbuild/cql/CQL.g:79:4: ORDEROP order ( COMMA order )*
		{
		match(input,ORDEROP,FOLLOW_ORDEROP_in_synpred9_CQL413); if (state.failed) return;

		pushFollow(FOLLOW_order_in_synpred9_CQL415);
		order();
		state._fsp--;
		if (state.failed) return;

		// org/cmdbuild/cql/CQL.g:79:18: ( COMMA order )*
		loop75:
		while (true) {
			int alt75=2;
			int LA75_0 = input.LA(1);
			if ( (LA75_0==COMMA) ) {
				alt75=1;
			}

			switch (alt75) {
			case 1 :
				// org/cmdbuild/cql/CQL.g:79:19: COMMA order
				{
				match(input,COMMA,FOLLOW_COMMA_in_synpred9_CQL418); if (state.failed) return;

				pushFollow(FOLLOW_order_in_synpred9_CQL420);
				order();
				state._fsp--;
				if (state.failed) return;

				}
				break;

			default :
				break loop75;
			}
		}

		}

	}
	// $ANTLR end synpred9_CQL

	// $ANTLR start synpred10_CQL
	public final void synpred10_CQL_fragment() throws RecognitionException {
		Token litlmt=null;


		// org/cmdbuild/cql/CQL.g:80:4: ( LMTOP litlmt= NUMBER )
		// org/cmdbuild/cql/CQL.g:80:4: LMTOP litlmt= NUMBER
		{
		match(input,LMTOP,FOLLOW_LMTOP_in_synpred10_CQL429); if (state.failed) return;

		litlmt=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_synpred10_CQL434); if (state.failed) return;

		}

	}
	// $ANTLR end synpred10_CQL

	// $ANTLR start synpred11_CQL
	public final void synpred11_CQL_fragment() throws RecognitionException {
		Token inlmt=null;


		// org/cmdbuild/cql/CQL.g:80:25: ( ( LGRAPH inlmt= NAME RGRAPH ) )
		// org/cmdbuild/cql/CQL.g:80:25: ( LGRAPH inlmt= NAME RGRAPH )
		{
		// org/cmdbuild/cql/CQL.g:80:25: ( LGRAPH inlmt= NAME RGRAPH )
		// org/cmdbuild/cql/CQL.g:80:26: LGRAPH inlmt= NAME RGRAPH
		{
		match(input,LGRAPH,FOLLOW_LGRAPH_in_synpred11_CQL437); if (state.failed) return;

		inlmt=(Token)match(input,NAME,FOLLOW_NAME_in_synpred11_CQL441); if (state.failed) return;

		match(input,RGRAPH,FOLLOW_RGRAPH_in_synpred11_CQL443); if (state.failed) return;

		}

		}

	}
	// $ANTLR end synpred11_CQL

	// $ANTLR start synpred12_CQL
	public final void synpred12_CQL_fragment() throws RecognitionException {
		Token litoff=null;


		// org/cmdbuild/cql/CQL.g:81:4: ( OFFSOP litoff= NUMBER )
		// org/cmdbuild/cql/CQL.g:81:4: OFFSOP litoff= NUMBER
		{
		match(input,OFFSOP,FOLLOW_OFFSOP_in_synpred12_CQL452); if (state.failed) return;

		litoff=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_synpred12_CQL456); if (state.failed) return;

		}

	}
	// $ANTLR end synpred12_CQL

	// $ANTLR start synpred13_CQL
	public final void synpred13_CQL_fragment() throws RecognitionException {
		Token inoff=null;


		// org/cmdbuild/cql/CQL.g:81:25: ( ( LGRAPH inoff= NAME RGRAPH ) )
		// org/cmdbuild/cql/CQL.g:81:25: ( LGRAPH inoff= NAME RGRAPH )
		{
		// org/cmdbuild/cql/CQL.g:81:25: ( LGRAPH inoff= NAME RGRAPH )
		// org/cmdbuild/cql/CQL.g:81:26: LGRAPH inoff= NAME RGRAPH
		{
		match(input,LGRAPH,FOLLOW_LGRAPH_in_synpred13_CQL459); if (state.failed) return;

		inoff=(Token)match(input,NAME,FOLLOW_NAME_in_synpred13_CQL463); if (state.failed) return;

		match(input,RGRAPH,FOLLOW_RGRAPH_in_synpred13_CQL465); if (state.failed) return;

		}

		}

	}
	// $ANTLR end synpred13_CQL

	// $ANTLR start synpred14_CQL
	public final void synpred14_CQL_fragment() throws RecognitionException {
		// org/cmdbuild/cql/CQL.g:82:3: ( RROUND )
		// org/cmdbuild/cql/CQL.g:82:3: RROUND
		{
		match(input,RROUND,FOLLOW_RROUND_in_synpred14_CQL472); if (state.failed) return;

		}

	}
	// $ANTLR end synpred14_CQL

	// $ANTLR start synpred20_CQL
	public final void synpred20_CQL_fragment() throws RecognitionException {
		Token alias=null;


		// org/cmdbuild/cql/CQL.g:108:4: (alias= NAME COLON COLON LROUND selattr ( COMMA selattr )* RROUND )
		// org/cmdbuild/cql/CQL.g:108:4: alias= NAME COLON COLON LROUND selattr ( COMMA selattr )* RROUND
		{
		alias=(Token)match(input,NAME,FOLLOW_NAME_in_synpred20_CQL694); if (state.failed) return;

		match(input,COLON,FOLLOW_COLON_in_synpred20_CQL696); if (state.failed) return;

		match(input,COLON,FOLLOW_COLON_in_synpred20_CQL698); if (state.failed) return;

		match(input,LROUND,FOLLOW_LROUND_in_synpred20_CQL700); if (state.failed) return;

		pushFollow(FOLLOW_selattr_in_synpred20_CQL702);
		selattr();
		state._fsp--;
		if (state.failed) return;

		// org/cmdbuild/cql/CQL.g:108:42: ( COMMA selattr )*
		loop76:
		while (true) {
			int alt76=2;
			int LA76_0 = input.LA(1);
			if ( (LA76_0==COMMA) ) {
				alt76=1;
			}

			switch (alt76) {
			case 1 :
				// org/cmdbuild/cql/CQL.g:108:43: COMMA selattr
				{
				match(input,COMMA,FOLLOW_COMMA_in_synpred20_CQL705); if (state.failed) return;

				pushFollow(FOLLOW_selattr_in_synpred20_CQL707);
				selattr();
				state._fsp--;
				if (state.failed) return;

				}
				break;

			default :
				break loop76;
			}
		}

		match(input,RROUND,FOLLOW_RROUND_in_synpred20_CQL711); if (state.failed) return;

		}

	}
	// $ANTLR end synpred20_CQL

	// $ANTLR start synpred23_CQL
	public final void synpred23_CQL_fragment() throws RecognitionException {
		Token alias=null;
		ParserRuleReturnScope meta =null;
		ParserRuleReturnScope objs =null;


		// org/cmdbuild/cql/CQL.g:110:4: (alias= NAME COLON COLON ( META LROUND meta= selattrs RROUND )? ( OBJECTS LROUND objs= selattrs RROUND )? )
		// org/cmdbuild/cql/CQL.g:110:4: alias= NAME COLON COLON ( META LROUND meta= selattrs RROUND )? ( OBJECTS LROUND objs= selattrs RROUND )?
		{
		alias=(Token)match(input,NAME,FOLLOW_NAME_in_synpred23_CQL738); if (state.failed) return;

		match(input,COLON,FOLLOW_COLON_in_synpred23_CQL740); if (state.failed) return;

		match(input,COLON,FOLLOW_COLON_in_synpred23_CQL742); if (state.failed) return;

		// org/cmdbuild/cql/CQL.g:110:27: ( META LROUND meta= selattrs RROUND )?
		int alt77=2;
		int LA77_0 = input.LA(1);
		if ( (LA77_0==META) ) {
			alt77=1;
		}
		switch (alt77) {
			case 1 :
				// org/cmdbuild/cql/CQL.g:110:28: META LROUND meta= selattrs RROUND
				{
				match(input,META,FOLLOW_META_in_synpred23_CQL745); if (state.failed) return;

				match(input,LROUND,FOLLOW_LROUND_in_synpred23_CQL747); if (state.failed) return;

				pushFollow(FOLLOW_selattrs_in_synpred23_CQL751);
				meta=selattrs();
				state._fsp--;
				if (state.failed) return;

				match(input,RROUND,FOLLOW_RROUND_in_synpred23_CQL753); if (state.failed) return;

				}
				break;

		}

		// org/cmdbuild/cql/CQL.g:110:63: ( OBJECTS LROUND objs= selattrs RROUND )?
		int alt78=2;
		int LA78_0 = input.LA(1);
		if ( (LA78_0==OBJECTS) ) {
			alt78=1;
		}
		switch (alt78) {
			case 1 :
				// org/cmdbuild/cql/CQL.g:110:64: OBJECTS LROUND objs= selattrs RROUND
				{
				match(input,OBJECTS,FOLLOW_OBJECTS_in_synpred23_CQL758); if (state.failed) return;

				match(input,LROUND,FOLLOW_LROUND_in_synpred23_CQL760); if (state.failed) return;

				pushFollow(FOLLOW_selattrs_in_synpred23_CQL764);
				objs=selattrs();
				state._fsp--;
				if (state.failed) return;

				match(input,RROUND,FOLLOW_RROUND_in_synpred23_CQL766); if (state.failed) return;

				}
				break;

		}

		}

	}
	// $ANTLR end synpred23_CQL

	// $ANTLR start synpred41_CQL
	public final void synpred41_CQL_fragment() throws RecognitionException {
		// org/cmdbuild/cql/CQL.g:149:18: ( and )
		// org/cmdbuild/cql/CQL.g:149:18: and
		{
		pushFollow(FOLLOW_and_in_synpred41_CQL1226);
		and();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred41_CQL

	// $ANTLR start synpred42_CQL
	public final void synpred42_CQL_fragment() throws RecognitionException {
		// org/cmdbuild/cql/CQL.g:149:22: ( or )
		// org/cmdbuild/cql/CQL.g:149:22: or
		{
		pushFollow(FOLLOW_or_in_synpred42_CQL1228);
		or();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred42_CQL

	// $ANTLR start synpred57_CQL
	public final void synpred57_CQL_fragment() throws RecognitionException {
		Token n=null;
		Token cscope=null;
		Token rev=null;
		Token t=null;
		ParserRuleReturnScope meta =null;
		ParserRuleReturnScope cards =null;
		ParserRuleReturnScope subdom =null;


		// org/cmdbuild/cql/CQL.g:180:4: ( (n= NEG )? (cscope= NAME )? ( ( ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND ) | (t= TILDE )? LSQUARE ) ( NAME | NUMBER ) ( RROUND | RSQUARE ) ( DOT META LROUND meta= fields RROUND )? ( ( DOT OBJECTS LROUND cards= fields ) |subdom= domain )? )
		// org/cmdbuild/cql/CQL.g:180:4: (n= NEG )? (cscope= NAME )? ( ( ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND ) | (t= TILDE )? LSQUARE ) ( NAME | NUMBER ) ( RROUND | RSQUARE ) ( DOT META LROUND meta= fields RROUND )? ( ( DOT OBJECTS LROUND cards= fields ) |subdom= domain )?
		{
		// org/cmdbuild/cql/CQL.g:180:5: (n= NEG )?
		int alt86=2;
		int LA86_0 = input.LA(1);
		if ( (LA86_0==NEG) ) {
			alt86=1;
		}
		switch (alt86) {
			case 1 :
				// org/cmdbuild/cql/CQL.g:180:5: n= NEG
				{
				n=(Token)match(input,NEG,FOLLOW_NEG_in_synpred57_CQL1356); if (state.failed) return;

				}
				break;

		}

		// org/cmdbuild/cql/CQL.g:181:9: (cscope= NAME )?
		int alt87=2;
		int LA87_0 = input.LA(1);
		if ( (LA87_0==NAME) ) {
			alt87=1;
		}
		switch (alt87) {
			case 1 :
				// org/cmdbuild/cql/CQL.g:181:9: cscope= NAME
				{
				cscope=(Token)match(input,NAME,FOLLOW_NAME_in_synpred57_CQL1364); if (state.failed) return;

				}
				break;

		}

		// org/cmdbuild/cql/CQL.g:182:3: ( ( ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND ) | (t= TILDE )? LSQUARE )
		int alt91=2;
		int LA91_0 = input.LA(1);
		if ( (LA91_0==DOMOP||LA91_0==DOMREVOP||LA91_0==DOT) ) {
			alt91=1;
		}
		else if ( (LA91_0==LSQUARE||LA91_0==TILDE) ) {
			alt91=2;
		}

		else {
			if (state.backtracking>0) {state.failed=true; return;}
			NoViableAltException nvae =
				new NoViableAltException("", 91, 0, input);
			throw nvae;
		}

		switch (alt91) {
			case 1 :
				// org/cmdbuild/cql/CQL.g:182:4: ( ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND )
				{
				// org/cmdbuild/cql/CQL.g:182:4: ( ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND )
				// org/cmdbuild/cql/CQL.g:182:5: ( DOT )? ( DOMOP |rev= DOMREVOP ) LROUND
				{
				// org/cmdbuild/cql/CQL.g:182:5: ( DOT )?
				int alt88=2;
				int LA88_0 = input.LA(1);
				if ( (LA88_0==DOT) ) {
					alt88=1;
				}
				switch (alt88) {
					case 1 :
						// org/cmdbuild/cql/CQL.g:182:5: DOT
						{
						match(input,DOT,FOLLOW_DOT_in_synpred57_CQL1372); if (state.failed) return;

						}
						break;

				}

				// org/cmdbuild/cql/CQL.g:182:10: ( DOMOP |rev= DOMREVOP )
				int alt89=2;
				int LA89_0 = input.LA(1);
				if ( (LA89_0==DOMOP) ) {
					alt89=1;
				}
				else if ( (LA89_0==DOMREVOP) ) {
					alt89=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					NoViableAltException nvae =
						new NoViableAltException("", 89, 0, input);
					throw nvae;
				}

				switch (alt89) {
					case 1 :
						// org/cmdbuild/cql/CQL.g:182:11: DOMOP
						{
						match(input,DOMOP,FOLLOW_DOMOP_in_synpred57_CQL1376); if (state.failed) return;

						}
						break;
					case 2 :
						// org/cmdbuild/cql/CQL.g:182:17: rev= DOMREVOP
						{
						rev=(Token)match(input,DOMREVOP,FOLLOW_DOMREVOP_in_synpred57_CQL1380); if (state.failed) return;

						}
						break;

				}

				match(input,LROUND,FOLLOW_LROUND_in_synpred57_CQL1383); if (state.failed) return;

				}

				}
				break;
			case 2 :
				// org/cmdbuild/cql/CQL.g:182:40: (t= TILDE )? LSQUARE
				{
				// org/cmdbuild/cql/CQL.g:182:41: (t= TILDE )?
				int alt90=2;
				int LA90_0 = input.LA(1);
				if ( (LA90_0==TILDE) ) {
					alt90=1;
				}
				switch (alt90) {
					case 1 :
						// org/cmdbuild/cql/CQL.g:182:41: t= TILDE
						{
						t=(Token)match(input,TILDE,FOLLOW_TILDE_in_synpred57_CQL1389); if (state.failed) return;

						}
						break;

				}

				match(input,LSQUARE,FOLLOW_LSQUARE_in_synpred57_CQL1392); if (state.failed) return;

				}
				break;

		}

		if ( input.LA(1)==NAME||input.LA(1)==NUMBER ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		if ( (input.LA(1) >= RROUND && input.LA(1) <= RSQUARE) ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		// org/cmdbuild/cql/CQL.g:183:3: ( DOT META LROUND meta= fields RROUND )?
		int alt92=2;
		int LA92_0 = input.LA(1);
		if ( (LA92_0==DOT) ) {
			int LA92_1 = input.LA(2);
			if ( (LA92_1==META) ) {
				alt92=1;
			}
		}
		switch (alt92) {
			case 1 :
				// org/cmdbuild/cql/CQL.g:183:4: DOT META LROUND meta= fields RROUND
				{
				match(input,DOT,FOLLOW_DOT_in_synpred57_CQL1411); if (state.failed) return;

				match(input,META,FOLLOW_META_in_synpred57_CQL1413); if (state.failed) return;

				match(input,LROUND,FOLLOW_LROUND_in_synpred57_CQL1415); if (state.failed) return;

				pushFollow(FOLLOW_fields_in_synpred57_CQL1419);
				meta=fields();
				state._fsp--;
				if (state.failed) return;

				match(input,RROUND,FOLLOW_RROUND_in_synpred57_CQL1421); if (state.failed) return;

				}
				break;

		}

		// org/cmdbuild/cql/CQL.g:183:41: ( ( DOT OBJECTS LROUND cards= fields ) |subdom= domain )?
		int alt93=3;
		int LA93_0 = input.LA(1);
		if ( (LA93_0==DOT) ) {
			int LA93_1 = input.LA(2);
			if ( (LA93_1==OBJECTS) ) {
				alt93=1;
			}
			else if ( (LA93_1==DOMOP||LA93_1==DOMREVOP) ) {
				alt93=2;
			}
		}
		else if ( (LA93_0==DOMOP||LA93_0==DOMREVOP||LA93_0==LSQUARE||LA93_0==NAME||LA93_0==NEG||LA93_0==TILDE) ) {
			alt93=2;
		}
		switch (alt93) {
			case 1 :
				// org/cmdbuild/cql/CQL.g:183:42: ( DOT OBJECTS LROUND cards= fields )
				{
				// org/cmdbuild/cql/CQL.g:183:42: ( DOT OBJECTS LROUND cards= fields )
				// org/cmdbuild/cql/CQL.g:183:43: DOT OBJECTS LROUND cards= fields
				{
				match(input,DOT,FOLLOW_DOT_in_synpred57_CQL1427); if (state.failed) return;

				match(input,OBJECTS,FOLLOW_OBJECTS_in_synpred57_CQL1429); if (state.failed) return;

				match(input,LROUND,FOLLOW_LROUND_in_synpred57_CQL1431); if (state.failed) return;

				pushFollow(FOLLOW_fields_in_synpred57_CQL1435);
				cards=fields();
				state._fsp--;
				if (state.failed) return;

				}

				}
				break;
			case 2 :
				// org/cmdbuild/cql/CQL.g:183:76: subdom= domain
				{
				pushFollow(FOLLOW_domain_in_synpred57_CQL1440);
				subdom=domain();
				state._fsp--;
				if (state.failed) return;

				}
				break;

		}

		}

	}
	// $ANTLR end synpred57_CQL

	// $ANTLR start synpred61_CQL
	public final void synpred61_CQL_fragment() throws RecognitionException {
		// org/cmdbuild/cql/CQL.g:206:16: ( val )
		// org/cmdbuild/cql/CQL.g:206:16: val
		{
		pushFollow(FOLLOW_val_in_synpred61_CQL1717);
		val();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred61_CQL

	// $ANTLR start synpred62_CQL
	public final void synpred62_CQL_fragment() throws RecognitionException {
		// org/cmdbuild/cql/CQL.g:206:4: ( id operator ( val )? )
		// org/cmdbuild/cql/CQL.g:206:4: id operator ( val )?
		{
		pushFollow(FOLLOW_id_in_synpred62_CQL1713);
		id();
		state._fsp--;
		if (state.failed) return;

		pushFollow(FOLLOW_operator_in_synpred62_CQL1715);
		operator();
		state._fsp--;
		if (state.failed) return;

		// org/cmdbuild/cql/CQL.g:206:16: ( val )?
		int alt94=2;
		int LA94_0 = input.LA(1);
		if ( ((LA94_0 >= BOOLFALSE && LA94_0 <= BOOLTRUE)||LA94_0==DATE||LA94_0==FROMOP||LA94_0==LGRAPH||LA94_0==LITERAL||LA94_0==LROUND||LA94_0==NATIVEELM||LA94_0==NUMBER||LA94_0==SELECTOP||LA94_0==TIMESTAMP) ) {
			alt94=1;
		}
		switch (alt94) {
			case 1 :
				// org/cmdbuild/cql/CQL.g:206:16: val
				{
				pushFollow(FOLLOW_val_in_synpred62_CQL1717);
				val();
				state._fsp--;
				if (state.failed) return;

				}
				break;

		}

		}

	}
	// $ANTLR end synpred62_CQL

	// $ANTLR start synpred66_CQL
	public final void synpred66_CQL_fragment() throws RecognitionException {
		// org/cmdbuild/cql/CQL.g:207:55: ( RROUND )
		// org/cmdbuild/cql/CQL.g:207:55: RROUND
		{
		match(input,RROUND,FOLLOW_RROUND_in_synpred66_CQL1772); if (state.failed) return;

		}

	}
	// $ANTLR end synpred66_CQL

	// $ANTLR start synpred67_CQL
	public final void synpred67_CQL_fragment() throws RecognitionException {
		Token n=null;


		// org/cmdbuild/cql/CQL.g:207:4: ( id (n= NEG )? ( BTWOP | LROUND ) val ( BTWANDOP | COLON ) val ( RROUND )? )
		// org/cmdbuild/cql/CQL.g:207:4: id (n= NEG )? ( BTWOP | LROUND ) val ( BTWANDOP | COLON ) val ( RROUND )?
		{
		pushFollow(FOLLOW_id_in_synpred67_CQL1748);
		id();
		state._fsp--;
		if (state.failed) return;

		// org/cmdbuild/cql/CQL.g:207:8: (n= NEG )?
		int alt95=2;
		int LA95_0 = input.LA(1);
		if ( (LA95_0==NEG) ) {
			alt95=1;
		}
		switch (alt95) {
			case 1 :
				// org/cmdbuild/cql/CQL.g:207:8: n= NEG
				{
				n=(Token)match(input,NEG,FOLLOW_NEG_in_synpred67_CQL1752); if (state.failed) return;

				}
				break;

		}

		if ( input.LA(1)==BTWOP||input.LA(1)==LROUND ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_val_in_synpred67_CQL1761);
		val();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==BTWANDOP||input.LA(1)==COLON ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_val_in_synpred67_CQL1769);
		val();
		state._fsp--;
		if (state.failed) return;

		// org/cmdbuild/cql/CQL.g:207:54: ( RROUND )?
		int alt96=2;
		int LA96_0 = input.LA(1);
		if ( (LA96_0==RROUND) ) {
			alt96=1;
		}
		switch (alt96) {
			case 1 :
				// org/cmdbuild/cql/CQL.g:207:55: RROUND
				{
				match(input,RROUND,FOLLOW_RROUND_in_synpred67_CQL1772); if (state.failed) return;

				}
				break;

		}

		}

	}
	// $ANTLR end synpred67_CQL

	// $ANTLR start synpred94_CQL
	public final void synpred94_CQL_fragment() throws RecognitionException {
		Token cdref=null;
		Token attr=null;


		// org/cmdbuild/cql/CQL.g:257:4: ( (cdref= NAME DOT )? attr= NAME lookupOp )
		// org/cmdbuild/cql/CQL.g:257:4: (cdref= NAME DOT )? attr= NAME lookupOp
		{
		// org/cmdbuild/cql/CQL.g:257:4: (cdref= NAME DOT )?
		int alt101=2;
		int LA101_0 = input.LA(1);
		if ( (LA101_0==NAME) ) {
			int LA101_1 = input.LA(2);
			if ( (LA101_1==DOT) ) {
				int LA101_2 = input.LA(3);
				if ( (LA101_2==NAME) ) {
					alt101=1;
				}
			}
		}
		switch (alt101) {
			case 1 :
				// org/cmdbuild/cql/CQL.g:257:5: cdref= NAME DOT
				{
				cdref=(Token)match(input,NAME,FOLLOW_NAME_in_synpred94_CQL2314); if (state.failed) return;

				match(input,DOT,FOLLOW_DOT_in_synpred94_CQL2316); if (state.failed) return;

				}
				break;

		}

		attr=(Token)match(input,NAME,FOLLOW_NAME_in_synpred94_CQL2322); if (state.failed) return;

		pushFollow(FOLLOW_lookupOp_in_synpred94_CQL2324);
		lookupOp();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred94_CQL

	// $ANTLR start synpred97_CQL
	public final void synpred97_CQL_fragment() throws RecognitionException {
		// org/cmdbuild/cql/CQL.g:261:32: ( DOT NAME )
		// org/cmdbuild/cql/CQL.g:261:32: DOT NAME
		{
		match(input,DOT,FOLLOW_DOT_in_synpred97_CQL2394); if (state.failed) return;

		match(input,NAME,FOLLOW_NAME_in_synpred97_CQL2396); if (state.failed) return;

		}

	}
	// $ANTLR end synpred97_CQL

	// Delegated rules

	public final boolean synpred10_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred10_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred94_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred94_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred42_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred42_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred12_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred12_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred66_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred66_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred23_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred23_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred62_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred62_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred5_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred5_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred14_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred14_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred7_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred7_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred41_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred41_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred57_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred57_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred97_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred97_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred67_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred67_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred9_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred9_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred13_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred13_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred11_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred11_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred20_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred20_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred61_CQL() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred61_CQL_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}



	public static final BitSet FOLLOW_LROUND_in_expr349 = new BitSet(new long[]{0x4000000000000000L,0x0000000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_SELECTOP_in_expr355 = new BitSet(new long[]{0x0000000000000020L,0x0000000800000000L});
	public static final BitSet FOLLOW_select_in_expr357 = new BitSet(new long[]{0x4000000010000000L});
	public static final BitSet FOLLOW_COMMA_in_expr360 = new BitSet(new long[]{0x0000000000000020L,0x0000000800000000L});
	public static final BitSet FOLLOW_select_in_expr362 = new BitSet(new long[]{0x4000000010000000L});
	public static final BitSet FOLLOW_FROMOP_in_expr371 = new BitSet(new long[]{0x0001240000000000L,0x0008000820000100L,0x0000000000000040L});
	public static final BitSet FOLLOW_HISTORYOP_in_expr375 = new BitSet(new long[]{0x0001240000000000L,0x0008000820000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_fromref_in_expr378 = new BitSet(new long[]{0x0000000010000002L,0x8440000002020004L,0x0000000000000400L});
	public static final BitSet FOLLOW_COMMA_in_expr381 = new BitSet(new long[]{0x0001240000000000L,0x0008000820000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_fromref_in_expr383 = new BitSet(new long[]{0x0000000010000002L,0x8440000002020004L,0x0000000000000400L});
	public static final BitSet FOLLOW_WHEREOP_in_expr391 = new BitSet(new long[]{0x0001240000000000L,0x0000004830000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_fields_in_expr393 = new BitSet(new long[]{0x0000000000000002L,0x8440000002020004L});
	public static final BitSet FOLLOW_GROUPBYOP_in_expr401 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_selattrs_in_expr405 = new BitSet(new long[]{0x0000000000000002L,0x8440000002020000L});
	public static final BitSet FOLLOW_ORDEROP_in_expr413 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_order_in_expr415 = new BitSet(new long[]{0x0000000010000002L,0x8040000002020000L});
	public static final BitSet FOLLOW_COMMA_in_expr418 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_order_in_expr420 = new BitSet(new long[]{0x0000000010000002L,0x8040000002020000L});
	public static final BitSet FOLLOW_LMTOP_in_expr429 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
	public static final BitSet FOLLOW_NUMBER_in_expr434 = new BitSet(new long[]{0x0000000000000002L,0x8040000000020000L});
	public static final BitSet FOLLOW_LGRAPH_in_expr437 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_NAME_in_expr441 = new BitSet(new long[]{0x0000000000000000L,0x4000000000000000L});
	public static final BitSet FOLLOW_RGRAPH_in_expr443 = new BitSet(new long[]{0x0000000000000002L,0x8040000000020000L});
	public static final BitSet FOLLOW_OFFSOP_in_expr452 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
	public static final BitSet FOLLOW_NUMBER_in_expr456 = new BitSet(new long[]{0x0000000000000002L,0x8000000000000000L});
	public static final BitSet FOLLOW_LGRAPH_in_expr459 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_NAME_in_expr463 = new BitSet(new long[]{0x0000000000000000L,0x4000000000000000L});
	public static final BitSet FOLLOW_RGRAPH_in_expr465 = new BitSet(new long[]{0x0000000000000002L,0x8000000000000000L});
	public static final BitSet FOLLOW_RROUND_in_expr472 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NAME_in_order616 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_DOT_in_order618 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_NAME_in_order624 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000003000L});
	public static final BitSet FOLLOW_140_in_order629 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_141_in_order633 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ALLOP_in_select681 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NAME_in_select694 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_COLON_in_select696 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_COLON_in_select698 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_LROUND_in_select700 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_selattr_in_select702 = new BitSet(new long[]{0x0000000010000000L,0x8000000000000000L});
	public static final BitSet FOLLOW_COMMA_in_select705 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_selattr_in_select707 = new BitSet(new long[]{0x0000000010000000L,0x8000000000000000L});
	public static final BitSet FOLLOW_RROUND_in_select711 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NAME_in_select738 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_COLON_in_select740 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_COLON_in_select742 = new BitSet(new long[]{0x0000000000000002L,0x0010000400000000L});
	public static final BitSet FOLLOW_META_in_select745 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_LROUND_in_select747 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_selattrs_in_select751 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L});
	public static final BitSet FOLLOW_RROUND_in_select753 = new BitSet(new long[]{0x0000000000000002L,0x0010000000000000L});
	public static final BitSet FOLLOW_OBJECTS_in_select758 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_LROUND_in_select760 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_selattrs_in_select764 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L});
	public static final BitSet FOLLOW_RROUND_in_select766 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_selattr_in_select801 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_selattr_in_selattrs811 = new BitSet(new long[]{0x0000000010000002L});
	public static final BitSet FOLLOW_COMMA_in_selattrs814 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_selattr_in_selattrs816 = new BitSet(new long[]{0x0000000010000002L});
	public static final BitSet FOLLOW_NAME_in_selattr836 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_LROUND_in_selattr838 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_selattrs_in_selattr840 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L});
	public static final BitSet FOLLOW_RROUND_in_selattr842 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_AT_in_selattr845 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_NAME_in_selattr849 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NAME_in_selattr882 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_DOT_in_selattr884 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_NAME_in_selattr888 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_AT_in_selattr891 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_NAME_in_selattr895 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NAME_in_fromref943 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_NUMBER_in_fromref945 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_AT_in_fromref949 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_NAME_in_fromref953 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_domaindecl_in_fromref990 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NAME_in_domaindecl1004 = new BitSet(new long[]{0x0001240000000000L,0x0000000020000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_DOT_in_domaindecl1011 = new BitSet(new long[]{0x0000240000000000L});
	public static final BitSet FOLLOW_DOMOP_in_domaindecl1015 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_DOMREVOP_in_domaindecl1019 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_LROUND_in_domaindecl1022 = new BitSet(new long[]{0x0000000000000000L,0x0008000800000000L});
	public static final BitSet FOLLOW_TILDE_in_domaindecl1028 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
	public static final BitSet FOLLOW_LSQUARE_in_domaindecl1031 = new BitSet(new long[]{0x0000000000000000L,0x0008000800000000L});
	public static final BitSet FOLLOW_NUMBER_in_domaindecl1035 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L,0x0000000000000001L});
	public static final BitSet FOLLOW_NAME_in_domaindecl1039 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L,0x0000000000000001L});
	public static final BitSet FOLLOW_RROUND_in_domaindecl1043 = new BitSet(new long[]{0x0001240000000202L,0x0000000820000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_RSQUARE_in_domaindecl1045 = new BitSet(new long[]{0x0001240000000202L,0x0000000820000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_AT_in_domaindecl1052 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_NAME_in_domaindecl1056 = new BitSet(new long[]{0x0001240000000002L,0x0000000820000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_domaindecl_in_domaindecl1061 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fieldgrpdom_in_fields1222 = new BitSet(new long[]{0x0000000000000082L,0x0800000000000000L});
	public static final BitSet FOLLOW_and_in_fields1226 = new BitSet(new long[]{0x0000000000000082L,0x0800000000000000L});
	public static final BitSet FOLLOW_or_in_fields1228 = new BitSet(new long[]{0x0000000000000082L,0x0800000000000000L});
	public static final BitSet FOLLOW_ANDOP_in_and1242 = new BitSet(new long[]{0x0001240000000000L,0x0000004830000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_fieldgrpdom_in_and1244 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OROP_in_or1260 = new BitSet(new long[]{0x0001240000000000L,0x0000004830000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_fieldgrpdom_in_or1262 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_field_in_fieldgrpdom1281 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_group_in_fieldgrpdom1286 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_domain_in_fieldgrpdom1291 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEG_in_group1306 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_LROUND_in_group1309 = new BitSet(new long[]{0x0001240000000000L,0x0000004830000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_fields_in_group1311 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L});
	public static final BitSet FOLLOW_RROUND_in_group1313 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEG_in_domain1356 = new BitSet(new long[]{0x0001240000000000L,0x0000000820000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_NAME_in_domain1364 = new BitSet(new long[]{0x0001240000000000L,0x0000000020000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_DOT_in_domain1372 = new BitSet(new long[]{0x0000240000000000L});
	public static final BitSet FOLLOW_DOMOP_in_domain1376 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_DOMREVOP_in_domain1380 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_LROUND_in_domain1383 = new BitSet(new long[]{0x0000000000000000L,0x0008000800000000L});
	public static final BitSet FOLLOW_TILDE_in_domain1389 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
	public static final BitSet FOLLOW_LSQUARE_in_domain1392 = new BitSet(new long[]{0x0000000000000000L,0x0008000800000000L});
	public static final BitSet FOLLOW_NAME_in_domain1396 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L,0x0000000000000001L});
	public static final BitSet FOLLOW_NUMBER_in_domain1398 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L,0x0000000000000001L});
	public static final BitSet FOLLOW_RROUND_in_domain1402 = new BitSet(new long[]{0x0001240000000002L,0x0000004820000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_RSQUARE_in_domain1404 = new BitSet(new long[]{0x0001240000000002L,0x0000004820000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_DOT_in_domain1411 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_META_in_domain1413 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_LROUND_in_domain1415 = new BitSet(new long[]{0x0001240000000000L,0x0000004830000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_fields_in_domain1419 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L});
	public static final BitSet FOLLOW_RROUND_in_domain1421 = new BitSet(new long[]{0x0001240000000002L,0x0000004820000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_DOT_in_domain1427 = new BitSet(new long[]{0x0000000000000000L,0x0010000000000000L});
	public static final BitSet FOLLOW_OBJECTS_in_domain1429 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_LROUND_in_domain1431 = new BitSet(new long[]{0x0001240000000000L,0x0000004830000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_fields_in_domain1435 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_domain_in_domain1440 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEG_in_domain1617 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_NAME_in_domain1620 = new BitSet(new long[]{0x0001000000000002L});
	public static final BitSet FOLLOW_DOT_in_domain1623 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_META_in_domain1625 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_LROUND_in_domain1627 = new BitSet(new long[]{0x0001240000000000L,0x0000004830000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_fields_in_domain1631 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L});
	public static final BitSet FOLLOW_RROUND_in_domain1633 = new BitSet(new long[]{0x0001000000000002L});
	public static final BitSet FOLLOW_DOT_in_domain1638 = new BitSet(new long[]{0x0000000000000000L,0x0010000000000000L});
	public static final BitSet FOLLOW_OBJECTS_in_domain1640 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_LROUND_in_domain1642 = new BitSet(new long[]{0x0001240000000000L,0x0000004830000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_fields_in_domain1646 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L});
	public static final BitSet FOLLOW_RROUND_in_domain1648 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_id_in_field1713 = new BitSet(new long[]{0x0028000040010000L,0x0004004300000060L});
	public static final BitSet FOLLOW_operator_in_field1715 = new BitSet(new long[]{0x4000000080060002L,0x0008002010220000L,0x0000000000000084L});
	public static final BitSet FOLLOW_val_in_field1717 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_id_in_field1748 = new BitSet(new long[]{0x0000000000200000L,0x0000004010000000L});
	public static final BitSet FOLLOW_NEG_in_field1752 = new BitSet(new long[]{0x0000000000200000L,0x0000000010000000L});
	public static final BitSet FOLLOW_BTWOP_in_field1756 = new BitSet(new long[]{0x4000000080060000L,0x0008002010220000L,0x0000000000000084L});
	public static final BitSet FOLLOW_LROUND_in_field1758 = new BitSet(new long[]{0x4000000080060000L,0x0008002010220000L,0x0000000000000084L});
	public static final BitSet FOLLOW_val_in_field1761 = new BitSet(new long[]{0x0000000008100000L});
	public static final BitSet FOLLOW_BTWANDOP_in_field1764 = new BitSet(new long[]{0x4000000080060000L,0x0008002010220000L,0x0000000000000084L});
	public static final BitSet FOLLOW_COLON_in_field1766 = new BitSet(new long[]{0x4000000080060000L,0x0008002010220000L,0x0000000000000084L});
	public static final BitSet FOLLOW_val_in_field1769 = new BitSet(new long[]{0x0000000000000002L,0x8000000000000000L});
	public static final BitSet FOLLOW_RROUND_in_field1772 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_id_in_field1850 = new BitSet(new long[]{0x0000000000000000L,0x0000004010000400L});
	public static final BitSet FOLLOW_NEG_in_field1854 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000400L});
	public static final BitSet FOLLOW_INOP_in_field1857 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_LROUND_in_field1860 = new BitSet(new long[]{0x4000000080060000L,0x0008002010220000L,0x0000000000000084L});
	public static final BitSet FOLLOW_val_in_field1862 = new BitSet(new long[]{0x0000000010000000L,0x8000000000000000L});
	public static final BitSet FOLLOW_COMMA_in_field1865 = new BitSet(new long[]{0x4000000080060000L,0x0008002010220000L,0x0000000000000084L});
	public static final BitSet FOLLOW_val_in_field1867 = new BitSet(new long[]{0x0000000010000000L,0x8000000000000000L});
	public static final BitSet FOLLOW_RROUND_in_field1871 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LTEQOP_in_operator1951 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GTEQOP_in_operator1965 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LTOP_in_operator1979 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GTOP_in_operator1993 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEG_in_operator2009 = new BitSet(new long[]{0x0000000040000000L});
	public static final BitSet FOLLOW_CONTOP_in_operator2012 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEG_in_operator2035 = new BitSet(new long[]{0x0000000000010000L});
	public static final BitSet FOLLOW_BGNOP_in_operator2038 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEG_in_operator2061 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_ENDOP_in_operator2064 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEG_in_operator2087 = new BitSet(new long[]{0x0020000000000000L});
	public static final BitSet FOLLOW_EQOP_in_operator2090 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEG_in_operator2115 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
	public static final BitSet FOLLOW_NULLOP_in_operator2118 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BOOLTRUE_in_val2147 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BOOLFALSE_in_val2163 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DATE_in_val2179 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TIMESTAMP_in_val2196 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LGRAPH_in_val2212 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_NAME_in_val2214 = new BitSet(new long[]{0x0000000000000000L,0x4000000000000000L});
	public static final BitSet FOLLOW_RGRAPH_in_val2216 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NATIVEELM_in_val2230 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LITERAL_in_val2246 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NUMBER_in_val2263 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_val2280 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NAME_in_id2291 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NAME_in_id2314 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_DOT_in_id2316 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_NAME_in_id2322 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_lookupOp_in_id2324 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NAME_in_id2353 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_DOT_in_id2356 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_NAME_in_id2358 = new BitSet(new long[]{0x0001000000000002L});
	public static final BitSet FOLLOW_DOT_in_lookupOp2384 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000004000L});
	public static final BitSet FOLLOW_142_in_lookupOp2386 = new BitSet(new long[]{0x0001000000000002L});
	public static final BitSet FOLLOW_lookupOp_in_lookupOp2389 = new BitSet(new long[]{0x0001000000000002L});
	public static final BitSet FOLLOW_DOT_in_lookupOp2394 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_NAME_in_lookupOp2396 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_COMMA_in_synpred5_CQL381 = new BitSet(new long[]{0x0001240000000000L,0x0008000820000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_fromref_in_synpred5_CQL383 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GROUPBYOP_in_synpred7_CQL401 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_selattrs_in_synpred7_CQL405 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ORDEROP_in_synpred9_CQL413 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_order_in_synpred9_CQL415 = new BitSet(new long[]{0x0000000010000002L});
	public static final BitSet FOLLOW_COMMA_in_synpred9_CQL418 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_order_in_synpred9_CQL420 = new BitSet(new long[]{0x0000000010000002L});
	public static final BitSet FOLLOW_LMTOP_in_synpred10_CQL429 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
	public static final BitSet FOLLOW_NUMBER_in_synpred10_CQL434 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LGRAPH_in_synpred11_CQL437 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_NAME_in_synpred11_CQL441 = new BitSet(new long[]{0x0000000000000000L,0x4000000000000000L});
	public static final BitSet FOLLOW_RGRAPH_in_synpred11_CQL443 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OFFSOP_in_synpred12_CQL452 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
	public static final BitSet FOLLOW_NUMBER_in_synpred12_CQL456 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LGRAPH_in_synpred13_CQL459 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_NAME_in_synpred13_CQL463 = new BitSet(new long[]{0x0000000000000000L,0x4000000000000000L});
	public static final BitSet FOLLOW_RGRAPH_in_synpred13_CQL465 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RROUND_in_synpred14_CQL472 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NAME_in_synpred20_CQL694 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_COLON_in_synpred20_CQL696 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_COLON_in_synpred20_CQL698 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_LROUND_in_synpred20_CQL700 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_selattr_in_synpred20_CQL702 = new BitSet(new long[]{0x0000000010000000L,0x8000000000000000L});
	public static final BitSet FOLLOW_COMMA_in_synpred20_CQL705 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_selattr_in_synpred20_CQL707 = new BitSet(new long[]{0x0000000010000000L,0x8000000000000000L});
	public static final BitSet FOLLOW_RROUND_in_synpred20_CQL711 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NAME_in_synpred23_CQL738 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_COLON_in_synpred23_CQL740 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_COLON_in_synpred23_CQL742 = new BitSet(new long[]{0x0000000000000002L,0x0010000400000000L});
	public static final BitSet FOLLOW_META_in_synpred23_CQL745 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_LROUND_in_synpred23_CQL747 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_selattrs_in_synpred23_CQL751 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L});
	public static final BitSet FOLLOW_RROUND_in_synpred23_CQL753 = new BitSet(new long[]{0x0000000000000002L,0x0010000000000000L});
	public static final BitSet FOLLOW_OBJECTS_in_synpred23_CQL758 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_LROUND_in_synpred23_CQL760 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_selattrs_in_synpred23_CQL764 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L});
	public static final BitSet FOLLOW_RROUND_in_synpred23_CQL766 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_and_in_synpred41_CQL1226 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_or_in_synpred42_CQL1228 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEG_in_synpred57_CQL1356 = new BitSet(new long[]{0x0001240000000000L,0x0000000820000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_NAME_in_synpred57_CQL1364 = new BitSet(new long[]{0x0001240000000000L,0x0000000020000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_DOT_in_synpred57_CQL1372 = new BitSet(new long[]{0x0000240000000000L});
	public static final BitSet FOLLOW_DOMOP_in_synpred57_CQL1376 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_DOMREVOP_in_synpred57_CQL1380 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_LROUND_in_synpred57_CQL1383 = new BitSet(new long[]{0x0000000000000000L,0x0008000800000000L});
	public static final BitSet FOLLOW_TILDE_in_synpred57_CQL1389 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
	public static final BitSet FOLLOW_LSQUARE_in_synpred57_CQL1392 = new BitSet(new long[]{0x0000000000000000L,0x0008000800000000L});
	public static final BitSet FOLLOW_set_in_synpred57_CQL1395 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L,0x0000000000000001L});
	public static final BitSet FOLLOW_set_in_synpred57_CQL1401 = new BitSet(new long[]{0x0001240000000002L,0x0000004820000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_DOT_in_synpred57_CQL1411 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_META_in_synpred57_CQL1413 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_LROUND_in_synpred57_CQL1415 = new BitSet(new long[]{0x0001240000000000L,0x0000004830000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_fields_in_synpred57_CQL1419 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L});
	public static final BitSet FOLLOW_RROUND_in_synpred57_CQL1421 = new BitSet(new long[]{0x0001240000000002L,0x0000004820000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_DOT_in_synpred57_CQL1427 = new BitSet(new long[]{0x0000000000000000L,0x0010000000000000L});
	public static final BitSet FOLLOW_OBJECTS_in_synpred57_CQL1429 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_LROUND_in_synpred57_CQL1431 = new BitSet(new long[]{0x0001240000000000L,0x0000004830000000L,0x0000000000000040L});
	public static final BitSet FOLLOW_fields_in_synpred57_CQL1435 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_domain_in_synpred57_CQL1440 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_val_in_synpred61_CQL1717 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_id_in_synpred62_CQL1713 = new BitSet(new long[]{0x0028000040010000L,0x0004004300000060L});
	public static final BitSet FOLLOW_operator_in_synpred62_CQL1715 = new BitSet(new long[]{0x4000000080060002L,0x0008002010220000L,0x0000000000000084L});
	public static final BitSet FOLLOW_val_in_synpred62_CQL1717 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RROUND_in_synpred66_CQL1772 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_id_in_synpred67_CQL1748 = new BitSet(new long[]{0x0000000000200000L,0x0000004010000000L});
	public static final BitSet FOLLOW_NEG_in_synpred67_CQL1752 = new BitSet(new long[]{0x0000000000200000L,0x0000000010000000L});
	public static final BitSet FOLLOW_set_in_synpred67_CQL1755 = new BitSet(new long[]{0x4000000080060000L,0x0008002010220000L,0x0000000000000084L});
	public static final BitSet FOLLOW_val_in_synpred67_CQL1761 = new BitSet(new long[]{0x0000000008100000L});
	public static final BitSet FOLLOW_set_in_synpred67_CQL1763 = new BitSet(new long[]{0x4000000080060000L,0x0008002010220000L,0x0000000000000084L});
	public static final BitSet FOLLOW_val_in_synpred67_CQL1769 = new BitSet(new long[]{0x0000000000000002L,0x8000000000000000L});
	public static final BitSet FOLLOW_RROUND_in_synpred67_CQL1772 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NAME_in_synpred94_CQL2314 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_DOT_in_synpred94_CQL2316 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_NAME_in_synpred94_CQL2322 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_lookupOp_in_synpred94_CQL2324 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_synpred97_CQL2394 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
	public static final BitSet FOLLOW_NAME_in_synpred97_CQL2396 = new BitSet(new long[]{0x0000000000000002L});
}
