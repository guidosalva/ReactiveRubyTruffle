// created by jay 1.0.2 (c) 2002-2004 ats@cs.rit.edu
// skeleton Java 1.0 (c) 2002 ats@cs.rit.edu

// line 2 "RubyParser.y"
/***** BEGIN LICENSE BLOCK *****
 * Version: EPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2008-2009 Thomas E Enebo <enebo@acm.org>
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the EPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the EPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/
package org.jruby.parser;

import java.io.IOException;

import org.jruby.ast.ArgsNode;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.ArrayNode;
import org.jruby.ast.AssignableNode;
import org.jruby.ast.BackRefNode;
import org.jruby.ast.BeginNode;
import org.jruby.ast.BlockAcceptingNode;
import org.jruby.ast.BlockArgNode;
import org.jruby.ast.BlockNode;
import org.jruby.ast.BlockPassNode;
import org.jruby.ast.BreakNode;
import org.jruby.ast.CallNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.Colon3Node;
import org.jruby.ast.ConstNode;
import org.jruby.ast.ConstDeclNode;
import org.jruby.ast.DStrNode;
import org.jruby.ast.DSymbolNode;
import org.jruby.ast.DXStrNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.DotNode;
import org.jruby.ast.EncodingNode;
import org.jruby.ast.EnsureNode;
import org.jruby.ast.EvStrNode;
import org.jruby.ast.FalseNode;
import org.jruby.ast.FileNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.FixnumNode;
import org.jruby.ast.FloatNode;
import org.jruby.ast.ForNode;
import org.jruby.ast.GlobalAsgnNode;
import org.jruby.ast.GlobalVarNode;
import org.jruby.ast.HashNode;
import org.jruby.ast.IfNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.IterNode;
import org.jruby.ast.LambdaNode;
import org.jruby.ast.ListNode;
import org.jruby.ast.LiteralNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.MultipleAsgn19Node;
import org.jruby.ast.NextNode;
import org.jruby.ast.NilImplicitNode;
import org.jruby.ast.NilNode;
import org.jruby.ast.Node;
import org.jruby.ast.NonLocalControlFlowNode;
import org.jruby.ast.NumericNode;
import org.jruby.ast.OpAsgnAndNode;
import org.jruby.ast.OpAsgnNode;
import org.jruby.ast.OpAsgnOrNode;
import org.jruby.ast.OptArgNode;
import org.jruby.ast.PostExeNode;
import org.jruby.ast.PreExe19Node;
import org.jruby.ast.RationalNode;
import org.jruby.ast.RedoNode;
import org.jruby.ast.RegexpNode;
import org.jruby.ast.RequiredKeywordArgumentValueNode;
import org.jruby.ast.RescueBodyNode;
import org.jruby.ast.RescueNode;
import org.jruby.ast.RestArgNode;
import org.jruby.ast.RetryNode;
import org.jruby.ast.ReturnNode;
import org.jruby.ast.SClassNode;
import org.jruby.ast.SelfNode;
import org.jruby.ast.StarNode;
import org.jruby.ast.StrNode;
import org.jruby.ast.SymbolNode;
import org.jruby.ast.TrueNode;
import org.jruby.ast.UnnamedRestArgNode;
import org.jruby.ast.UntilNode;
import org.jruby.ast.VAliasNode;
import org.jruby.ast.WhileNode;
import org.jruby.ast.XStrNode;
import org.jruby.ast.YieldNode;
import org.jruby.ast.ZArrayNode;
import org.jruby.ast.ZSuperNode;
import org.jruby.ast.ZYieldNode;
import org.jruby.ast.types.ILiteralNode;
import org.jruby.common.IRubyWarnings;
import org.jruby.common.IRubyWarnings.ID;
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.lexer.yacc.ISourcePositionHolder;
import org.jruby.lexer.yacc.LexerSource;
import org.jruby.lexer.yacc.RubyLexer;
import org.jruby.lexer.yacc.RubyLexer.LexState;
import org.jruby.lexer.yacc.StrTerm;
import org.jruby.lexer.yacc.SyntaxException;
import org.jruby.lexer.yacc.SyntaxException.PID;
import org.jruby.util.ByteList;
import org.jruby.util.KeyValuePair;
import org.jruby.util.cli.Options;

public class RubyParser {
    protected ParserSupport support;
    protected RubyLexer lexer;

    public RubyParser() {
        this(new ParserSupport());
    }

    public RubyParser(ParserSupport support) {
        this.support = support;
        lexer = new RubyLexer();
        lexer.setParserSupport(support);
        support.setLexer(lexer);
    }

    public void setWarnings(IRubyWarnings warnings) {
        support.setWarnings(warnings);
        lexer.setWarnings(warnings);
    }
    // line 152 "-"
    // %token constants
    public static final int kCLASS = 257;
    public static final int kMODULE = 258;
    public static final int kDEF = 259;
    public static final int kUNDEF = 260;
    public static final int kBEGIN = 261;
    public static final int kRESCUE = 262;
    public static final int kENSURE = 263;
    public static final int kEND = 264;
    public static final int kIF = 265;
    public static final int kUNLESS = 266;
    public static final int kTHEN = 267;
    public static final int kELSIF = 268;
    public static final int kELSE = 269;
    public static final int kCASE = 270;
    public static final int kWHEN = 271;
    public static final int kWHILE = 272;
    public static final int kUNTIL = 273;
    public static final int kFOR = 274;
    public static final int kBREAK = 275;
    public static final int kNEXT = 276;
    public static final int kREDO = 277;
    public static final int kRETRY = 278;
    public static final int kIN = 279;
    public static final int kDO = 280;
    public static final int kDO_COND = 281;
    public static final int kDO_BLOCK = 282;
    public static final int kRETURN = 283;
    public static final int kYIELD = 284;
    public static final int kSUPER = 285;
    public static final int kSELF = 286;
    public static final int kNIL = 287;
    public static final int kTRUE = 288;
    public static final int kFALSE = 289;
    public static final int kAND = 290;
    public static final int kOR = 291;
    public static final int kNOT = 292;
    public static final int kIF_MOD = 293;
    public static final int kUNLESS_MOD = 294;
    public static final int kWHILE_MOD = 295;
    public static final int kUNTIL_MOD = 296;
    public static final int kRESCUE_MOD = 297;
    public static final int kALIAS = 298;
    public static final int kDEFINED = 299;
    public static final int klBEGIN = 300;
    public static final int klEND = 301;
    public static final int k__LINE__ = 302;
    public static final int k__FILE__ = 303;
    public static final int k__ENCODING__ = 304;
    public static final int kDO_LAMBDA = 305;
    public static final int kSIGNAL = 306;
    public static final int tIDENTIFIER = 307;
    public static final int tFID = 308;
    public static final int tGVAR = 309;
    public static final int tIVAR = 310;
    public static final int tCONSTANT = 311;
    public static final int tCVAR = 312;
    public static final int tLABEL = 313;
    public static final int tCHAR = 314;
    public static final int tUPLUS = 315;
    public static final int tUMINUS = 316;
    public static final int tUMINUS_NUM = 317;
    public static final int tPOW = 318;
    public static final int tCMP = 319;
    public static final int tEQ = 320;
    public static final int tEQQ = 321;
    public static final int tNEQ = 322;
    public static final int tGEQ = 323;
    public static final int tLEQ = 324;
    public static final int tANDOP = 325;
    public static final int tOROP = 326;
    public static final int tMATCH = 327;
    public static final int tNMATCH = 328;
    public static final int tDOT = 329;
    public static final int tDOT2 = 330;
    public static final int tDOT3 = 331;
    public static final int tAREF = 332;
    public static final int tASET = 333;
    public static final int tLSHFT = 334;
    public static final int tRSHFT = 335;
    public static final int tCOLON2 = 336;
    public static final int tCOLON3 = 337;
    public static final int tOP_ASGN = 338;
    public static final int tASSOC = 339;
    public static final int tLPAREN = 340;
    public static final int tLPAREN2 = 341;
    public static final int tRPAREN = 342;
    public static final int tLPAREN_ARG = 343;
    public static final int tLBRACK = 344;
    public static final int tRBRACK = 345;
    public static final int tLBRACE = 346;
    public static final int tLBRACE_ARG = 347;
    public static final int tSTAR = 348;
    public static final int tSTAR2 = 349;
    public static final int tAMPER = 350;
    public static final int tAMPER2 = 351;
    public static final int tTILDE = 352;
    public static final int tPERCENT = 353;
    public static final int tDIVIDE = 354;
    public static final int tPLUS = 355;
    public static final int tMINUS = 356;
    public static final int tLT = 357;
    public static final int tGT = 358;
    public static final int tPIPE = 359;
    public static final int tBANG = 360;
    public static final int tCARET = 361;
    public static final int tLCURLY = 362;
    public static final int tRCURLY = 363;
    public static final int tBACK_REF2 = 364;
    public static final int tSYMBEG = 365;
    public static final int tSTRING_BEG = 366;
    public static final int tXSTRING_BEG = 367;
    public static final int tREGEXP_BEG = 368;
    public static final int tWORDS_BEG = 369;
    public static final int tQWORDS_BEG = 370;
    public static final int tSTRING_DBEG = 371;
    public static final int tSTRING_DVAR = 372;
    public static final int tSTRING_END = 373;
    public static final int tLAMBDA = 374;
    public static final int tLAMBEG = 375;
    public static final int tNTH_REF = 376;
    public static final int tBACK_REF = 377;
    public static final int tSTRING_CONTENT = 378;
    public static final int tINTEGER = 379;
    public static final int tIMAGINARY = 380;
    public static final int tFLOAT = 381;
    public static final int tRATIONAL = 382;
    public static final int tREGEXP_END = 383;
    public static final int tSYMBOLS_BEG = 384;
    public static final int tQSYMBOLS_BEG = 385;
    public static final int tDSTAR = 386;
    public static final int tSTRING_DEND = 387;
    public static final int tLABEL_END = 388;
    public static final int tLOWEST = 389;
    public static final int yyErrorCode = 256;

    /** number of final state.
     */
    protected static final int yyFinal = 1;

    /** parser tables.
     Order is mandated by <i>jay</i>.
     */
    protected static final short[] yyLhs = {
//yyLhs 637
            -1,   140,     0,   133,   134,   134,   134,   134,   135,   143,
            135,    37,    36,    38,    38,    38,    38,    44,   144,    44,
            145,    39,    39,    39,    39,    39,    39,    39,    39,    39,
            39,    39,    39,    39,    39,    39,    39,    39,    39,    39,
            39,    39,    39,    39,    31,    31,    40,    40,    40,    40,
            40,    40,    45,    32,    32,    59,    59,   147,   110,   139,
            43,    43,    43,    43,    43,    43,    43,    43,    43,    43,
            43,   111,   111,   122,   122,   112,   112,   112,   112,   112,
            112,   112,   112,   112,   112,    71,    71,   100,   100,   101,
            101,    72,    72,    72,    72,    72,    72,    72,    72,    72,
            72,    72,    72,    72,    72,    72,    72,    72,    72,    72,
            77,    77,    77,    77,    77,    77,    77,    77,    77,    77,
            77,    77,    77,    77,    77,    77,    77,    77,    77,     6,
            6,    30,    30,    30,     7,     7,     7,     7,     7,   115,
            115,   116,   116,    61,   148,    61,     8,     8,     8,     8,
            8,     8,     8,     8,     8,     8,     8,     8,     8,     8,
            8,     8,     8,     8,     8,     8,     8,     8,     8,     8,
            8,     8,     8,     8,     8,     8,   131,   131,   131,   131,
            131,   131,   131,   131,   131,   131,   131,   131,   131,   131,
            131,   131,   131,   131,   131,   131,   131,   131,   131,   131,
            131,   131,   131,   131,   131,   131,   131,   131,   131,   131,
            131,   131,   131,   131,   131,   131,   131,   131,   131,    41,
            41,    41,    41,    41,    41,    41,    41,    41,    41,    41,
            41,    41,    41,    41,    41,    41,    41,    41,    41,    41,
            41,    41,    41,    41,    41,    41,    41,    41,    41,    41,
            41,    41,    41,    41,    41,    41,    41,    41,    41,    41,
            41,    41,    41,    73,    76,    76,    76,    76,    53,    57,
            57,   125,   125,   125,   125,   125,    51,    51,    51,    51,
            51,   150,    55,   104,   103,   103,    79,    79,    79,    79,
            35,    35,    70,    70,    70,    42,    42,    42,    42,    42,
            42,    42,    42,    42,    42,    42,    42,   151,    42,   152,
            42,    42,    42,    42,    42,    42,    42,    42,    42,    42,
            42,    42,    42,    42,    42,    42,    42,    42,    42,   154,
            156,    42,   157,   158,    42,    42,    42,   159,   160,    42,
            161,    42,   163,   164,    42,   165,    42,   166,    42,   167,
            168,    42,    42,    42,    42,    42,    42,    46,   153,   153,
            153,   155,   155,    49,    49,    47,    47,   124,   124,   126,
            126,    84,    84,   127,   127,   127,   127,   127,   127,   127,
            127,   127,    91,    91,    91,    91,    90,    90,    66,    66,
            66,    66,    66,    66,    66,    66,    66,    66,    66,    66,
            66,    66,    66,    68,    68,    67,    67,    67,   119,   119,
            118,   118,   128,   128,   169,   121,    65,    65,   120,   120,
            170,   109,    58,    58,    58,    58,    22,    22,    22,    22,
            22,    22,    22,    22,    22,   171,   108,   172,   108,    74,
            48,    48,   113,   113,    75,    75,    75,    50,    50,    52,
            52,    28,    28,    28,    15,    16,    16,    16,    17,    18,
            19,    25,    25,    81,    81,    27,    27,    87,    87,    85,
            85,    26,    26,    88,    88,    80,    80,    86,    86,    20,
            20,    21,    21,    24,    24,    23,   173,    23,   174,   175,
            23,    62,    62,    62,    62,     2,     1,     1,     1,     1,
            29,    33,    33,    34,    34,    34,    34,    56,    56,    56,
            56,    56,    56,    56,    56,    56,    56,    56,    56,   114,
            114,   114,   114,   114,   114,   114,   114,   114,   114,   114,
            114,    63,    63,    54,   176,    54,    54,    69,   177,    69,
            92,    92,    92,    92,    89,    89,    64,    64,    64,    64,
            64,    64,    64,    64,    64,    64,    64,    64,    64,    64,
            64,   132,   132,   132,   132,     9,     9,   117,   117,    82,
            82,   138,    93,    93,    94,    94,    95,    95,    96,    96,
            136,   136,   137,   137,    60,   123,   102,   102,    83,    83,
            11,    11,    13,    13,    12,    12,   107,   106,   106,    14,
            178,    14,    97,    97,    98,    98,    99,    99,    99,    99,
            3,     3,     3,     4,     4,     4,     4,     5,     5,     5,
            10,    10,   141,   141,   146,   146,   129,   130,   149,   149,
            149,   162,   162,   142,   142,    78,   105,
    }, yyLen = {
//yyLen 637
            2,     0,     2,     2,     1,     1,     3,     2,     1,     0,
            5,     4,     2,     1,     1,     3,     2,     1,     0,     5,
            0,     4,     3,     3,     3,     2,     3,     3,     3,     3,
            3,     4,     1,     3,     3,     6,     5,     5,     5,     5,
            3,     3,     3,     1,     3,     3,     1,     3,     3,     3,
            2,     1,     1,     1,     1,     1,     4,     0,     5,     1,
            2,     3,     4,     5,     4,     5,     2,     2,     2,     2,
            2,     1,     3,     1,     3,     1,     2,     3,     5,     2,
            4,     2,     4,     1,     3,     1,     3,     2,     3,     1,
            3,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     4,     3,     3,     3,     3,     2,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     4,     3,     3,     3,     3,     2,     1,     1,
            1,     2,     1,     3,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     0,     4,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     3,
            5,     3,     5,     6,     5,     5,     5,     5,     4,     3,
            3,     3,     3,     3,     3,     3,     3,     3,     4,     2,
            2,     3,     3,     3,     3,     3,     3,     3,     3,     3,
            3,     3,     3,     3,     2,     2,     3,     3,     3,     3,
            3,     6,     1,     1,     1,     2,     4,     2,     3,     1,
            1,     1,     1,     2,     4,     2,     1,     2,     2,     4,
            1,     0,     2,     2,     2,     1,     1,     2,     3,     4,
            1,     1,     3,     4,     2,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     3,     0,     3,     0,
            4,     3,     3,     2,     3,     3,     1,     4,     3,     1,
            5,     4,     3,     2,     1,     2,     2,     6,     6,     0,
            0,     7,     0,     0,     7,     5,     4,     0,     0,     9,
            0,     6,     0,     0,     8,     0,     5,     0,     6,     0,
            0,     9,     1,     1,     1,     1,     3,     1,     1,     1,
            2,     1,     1,     1,     5,     1,     2,     1,     1,     1,
            3,     1,     3,     1,     4,     6,     3,     5,     2,     4,
            1,     3,     4,     2,     2,     1,     2,     0,     6,     8,
            4,     6,     4,     2,     6,     2,     4,     6,     2,     4,
            2,     4,     1,     1,     1,     3,     1,     4,     1,     4,
            1,     3,     1,     1,     0,     3,     4,     1,     3,     3,
            0,     5,     2,     4,     5,     5,     2,     4,     4,     3,
            3,     3,     2,     1,     4,     0,     5,     0,     5,     5,
            1,     1,     6,     0,     1,     1,     1,     2,     1,     2,
            1,     1,     1,     1,     1,     1,     1,     2,     3,     3,
            3,     3,     3,     0,     3,     1,     2,     3,     3,     0,
            3,     3,     3,     3,     3,     0,     3,     0,     3,     0,
            2,     0,     2,     0,     2,     1,     0,     3,     0,     0,
            5,     1,     1,     1,     1,     2,     1,     1,     1,     1,
            3,     1,     2,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     0,     4,     2,     3,     0,     3,
            4,     2,     2,     1,     2,     0,     6,     8,     4,     6,
            4,     6,     2,     4,     6,     2,     4,     2,     4,     1,
            0,     1,     1,     1,     1,     1,     1,     1,     3,     1,
            3,     1,     2,     1,     2,     1,     1,     3,     1,     3,
            1,     1,     2,     1,     3,     3,     1,     3,     1,     3,
            1,     1,     2,     1,     1,     1,     2,     2,     0,     1,
            0,     4,     1,     2,     1,     3,     3,     2,     4,     2,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     0,     1,     0,     1,     2,     2,     0,     1,
            1,     1,     1,     1,     2,     0,     0,
    }, yyDefRed = {
//yyDefRed 1091
            1,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,   329,   332,     0,     0,     0,   354,   355,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     9,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,   455,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,   479,   481,   483,     0,     0,   414,
            531,   532,   503,   506,   504,   505,     0,     0,   452,    59,
            296,     0,   456,   297,   298,     0,   299,   300,   295,   453,
            32,    46,   451,   501,     0,     0,     0,     0,     0,     0,
            303,     0,    54,     0,     0,    85,     0,     4,   301,   302,
            0,     0,    71,     0,     2,     0,     5,     0,     7,   352,
            353,   316,     0,     0,   513,   512,   514,   515,     0,     0,
            517,   516,   518,     0,   509,   508,     0,   511,     0,     0,
            0,     0,   132,     0,   357,     0,   304,     0,   345,   186,
            197,   187,   210,   183,   203,   193,   192,   208,   191,   190,
            185,   211,   195,   184,   198,   202,   204,   196,   189,   205,
            212,   207,     0,     0,     0,     0,   182,   201,   200,   213,
            214,   216,   217,   218,   181,   188,   179,   180,     0,     0,
            0,   215,     0,   136,     0,   171,   172,   168,   149,   150,
            151,   158,   155,   157,   152,   153,   173,   174,   159,   160,
            600,   165,   164,   148,   170,   167,   166,   162,   163,   156,
            154,   146,   169,   147,   175,   161,   347,   137,     0,   599,
            138,   206,   199,   209,   194,   176,   177,   178,   134,   135,
            140,   139,   142,     0,   141,   143,     0,     0,     0,     0,
            0,     0,    14,    13,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,   631,   632,     0,     0,     0,   633,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,   367,   368,     0,
            0,     0,     0,     0,   479,     0,     0,   276,    69,     0,
            0,     0,   604,   280,    70,    68,     0,    67,     0,     0,
            432,    66,     0,   625,     0,     0,    20,     0,     0,     0,
            0,   239,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,   264,     0,     0,     0,   602,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,   255,    50,   254,
            498,   497,   499,   495,   496,     0,     0,     0,     0,     0,
            0,     0,     0,   326,     0,     0,     0,     0,     0,   457,
            437,   435,   325,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,   420,   422,     0,     0,
            0,   620,   621,     0,     0,    87,     0,     0,     0,     0,
            0,     0,     3,     0,   426,     0,   323,     0,   502,     0,
            129,     0,   131,     0,   534,   340,   533,     0,     0,     0,
            0,     0,     0,   349,   144,     0,     0,     0,     0,   306,
            12,     0,     0,   359,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,   634,     0,     0,     0,
            0,     0,     0,   337,   607,   287,   283,     0,   609,     0,
            0,   277,   285,     0,   278,     0,   318,     0,   282,   272,
            271,     0,     0,     0,     0,   322,    49,    22,    24,    23,
            0,     0,     0,     0,     0,   356,     0,     0,     0,     0,
            0,   311,     0,     0,   308,   314,     0,   629,   265,     0,
            267,   315,   603,     0,    89,     0,     0,     0,     0,     0,
            488,   486,   500,   485,   482,   458,   480,   459,   460,   484,
            461,   462,   465,     0,   471,   472,     0,   566,   563,   562,
            561,   564,   571,   580,     0,     0,   591,   590,   595,   594,
            581,     0,     0,     0,     0,   588,   417,     0,     0,     0,
            559,   578,     0,   543,   569,   565,     0,     0,     0,   467,
            468,     0,   473,   474,     0,     0,     0,    26,    27,    28,
            29,    30,    47,    48,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,   615,     0,     0,   616,   430,     0,     0,     0,
            0,   429,     0,   431,     0,   613,   614,     0,    40,     0,
            0,    45,    44,     0,    41,   286,     0,     0,     0,     0,
            0,    88,    33,    42,   290,     0,    34,     0,     6,    57,
            61,     0,   536,     0,     0,     0,     0,     0,     0,   133,
            0,     0,     0,     0,     0,     0,     0,     0,     0,   445,
            0,     0,   446,     0,     0,   365,    15,     0,   360,     0,
            0,     0,     0,     0,     0,     0,     0,     0,   336,   362,
            330,   361,   333,     0,     0,     0,     0,     0,     0,     0,
            606,     0,     0,     0,   284,   605,   317,   626,     0,     0,
            268,   321,    21,     0,     0,    31,     0,     0,     0,   310,
            0,     0,     0,     0,     0,     0,     0,     0,   489,     0,
            464,   466,   476,     0,     0,   369,     0,   371,     0,     0,
            0,   592,   596,     0,   557,     0,     0,   415,     0,   552,
            0,   555,     0,   541,   582,     0,   542,   572,   470,   478,
            406,     0,   404,     0,   403,     0,     0,     0,     0,     0,
            270,     0,   427,   269,     0,     0,   428,     0,     0,     0,
            0,     0,     0,     0,     0,     0,    86,     0,     0,     0,
            0,   343,     0,     0,   434,   346,   601,     0,     0,     0,
            350,   145,     0,     0,     0,   448,   366,     0,    11,   450,
            0,   363,     0,     0,     0,     0,     0,     0,     0,   335,
            0,     0,     0,     0,     0,     0,   608,   289,   279,     0,
            320,    10,   266,    90,     0,     0,   491,   492,   493,   487,
            494,     0,     0,     0,     0,   568,     0,     0,   584,   567,
            0,   544,     0,     0,     0,     0,   570,     0,   589,     0,
            579,   597,     0,     0,     0,     0,     0,   402,   576,     0,
            0,   385,     0,   586,     0,     0,     0,     0,     0,     0,
            36,     0,    37,     0,    63,    39,     0,    38,     0,    65,
            0,   627,   425,   424,     0,     0,     0,     0,     0,     0,
            0,   535,   341,   537,   348,   539,     0,    19,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,   447,     0,   449,     0,   327,     0,   328,
            288,     0,     0,     0,   338,     0,     0,   370,     0,     0,
            0,   372,   416,     0,     0,   558,   419,   418,     0,   550,
            0,   548,     0,   553,   556,   540,     0,     0,   400,     0,
            0,   395,     0,   383,     0,   398,   405,   384,     0,     0,
            0,     0,   438,   436,     0,   421,    35,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,   440,   439,
            441,   331,   334,     0,   490,     0,     0,     0,     0,   412,
            0,   410,   413,     0,     0,     0,     0,     0,     0,   386,
            407,     0,     0,   577,     0,     0,     0,   587,   313,     0,
            58,   344,     0,     0,     0,     0,     0,     0,   442,     0,
            0,     0,     0,     0,   409,   551,     0,   546,   549,   554,
            0,   401,     0,   392,     0,   390,   382,     0,   396,   399,
            0,     0,   351,     0,   364,   339,     0,   411,     0,     0,
            0,     0,     0,   547,   394,     0,   388,   391,   397,     0,
            389,
    }, yyDgoto = {
//yyDgoto 179
            1,   363,    68,    69,   678,   641,   132,   231,   635,   869,
            423,   572,   573,   574,   218,    70,    71,    72,    73,    74,
            366,   365,    75,   544,   368,    76,    77,   553,    78,    79,
            133,    80,    81,    82,    83,   663,   238,   239,   240,   241,
            85,    86,    87,    88,   242,   258,   322,   831,  1009,   832,
            824,   499,   828,   643,   445,   307,    90,   792,    91,    92,
            575,   233,   859,   260,   576,   577,   885,   782,   783,   684,
            654,    94,    95,   299,   475,   691,   332,   261,   243,   501,
            372,   370,   578,   579,   756,   376,   378,    98,    99,   764,
            978,  1029,   871,   581,   888,   889,   582,   338,   502,   302,
            100,   535,   890,   491,   303,   492,   773,   583,   436,   417,
            670,   101,   102,   458,   262,   234,   235,   584,  1020,   866,
            767,   373,   329,   893,   289,   503,   757,   758,  1021,   496,
            798,   220,   585,   104,   105,   106,   586,   587,   588,   137,
            2,   267,   268,   318,   456,   510,   497,   810,   687,   528,
            308,   331,   523,   464,   270,   710,   842,   271,   843,   718,
            1013,   674,   465,   671,   920,   450,   452,   686,   926,   374,
            630,   596,   595,   749,   748,   855,   673,   685,   451,
    }, yySindex = {
//yySindex 1091
            0,     0, 17976, 19275,  5855, 21210, 17409, 17748, 18106, 20436,
            20436,  8013,     0,     0, 20952, 18365, 18365,     0,     0, 18365,
            -176,  -161,     0,     0,     0,     0,    70, 17635,   192,     0,
            -113,     0,     0,     0, 18106,     0,     0,     0,     0,     0,
            0,     0, 20565, 20565,   968,   -14, 18236, 20436, 18755, 19145,
            16891, 20565, 20694, 17522,     0,     0,     0,   251,   315,     0,
            0,     0,     0,     0,     0,     0,   329,   341,     0,     0,
            0,   -36,     0,     0,     0,  -140,     0,     0,     0,     0,
            0,     0,     0,     0,  1618,   -31,  5289,     0,    99,   -13,
            0,   -40,     0,    95,   357,     0,   416,     0,     0,     0,
            21081,   440,     0,   125,     0,   158,     0,   -98,     0,     0,
            0,     0,  -176,  -161,     0,     0,     0,     0,   266,   192,
            0,     0,     0,     0,     0,     0,     0,     0,   968, 20436,
            401, 18106,     0,   110,     0,   306,     0,   -98,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,   -40,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,   580,     0,     0, 19404, 18106,   364,   377,
            158,  1618,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,   332,   -31,   193,   373,
            320,   583,   330,   193,     0,     0,   158,   399,   618,     0,
            20436, 20436,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,   374,   536,     0,     0,     0,   408,
            20565, 20565, 20565, 20565,     0, 20565,  5289,     0,     0,   367,
            667,   685,     0,     0,     0,     0,  6356,     0, 18365, 18365,
            0,     0,  6766,     0, 20436,   -95,     0, 19533,   372, 18106,
            473,     0,   555,   425,   429,   417, 18236,   414,     0,   192,
            -31,   192,   434,     0,    81,   150,   367,     0,   418,   150,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,   476, 21339,   571,     0,   746,     0,     0,     0,
            0,     0,     0,     0,     0,   405,   736,   915,   774,   419,
            923,   421,   -56,     0,  2305,   422,  1035,   433,   -34,     0,
            0,     0,     0, 20436, 20436, 20436, 20436, 19404, 20436, 20436,
            20565, 20565, 20565, 20565, 20565, 20565, 20565, 20565, 20565, 20565,
            20565, 20565, 20565, 20565, 20565, 20565, 20565, 20565, 20565, 20565,
            20565, 20565, 20565, 20565, 20565, 20565,     0,     0, 21676, 21731,
            18365,     0,     0, 22391, 20694,     0, 19662, 18236, 17021,   766,
            19662, 20694,     0, 17151,     0,   467,     0,   470,     0,   -31,
            0,     0,     0,   158,     0,     0,     0, 21786, 21841, 18365,
            18106, 20436,   477,     0,     0,  1618,   459, 19791,   559,     0,
            0, 17280,   417,     0, 18106,   563, 21896, 21951, 18365, 20565,
            20565, 20565, 18106,   399, 19920,   576,     0,    79,    79,     0,
            22006, 22061, 18365,     0,     0,     0,     0,   666,     0, 20565,
            18495,     0,     0, 18885,     0,   192,     0,   500,     0,     0,
            0,   806,   813,   192,    50,     0,     0,     0,     0,     0,
            17748, 20436,  5289, 17976,   505,     0, 21896, 21951, 20565, 20565,
            192,     0,     0,   192,     0,     0, 19015,     0,     0, 19145,
            0,     0,     0,     0,     0,   833, 22116, 22171, 18365, 21339,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,   172,     0,     0,   846,     0,     0,     0,
            0,     0,     0,     0,  1584,  2761,     0,     0,     0,     0,
            0,   828,   581,   587,   851,     0,     0,  -163,   853,   855,
            0,     0,   857,     0,     0,     0,   597,   865, 20565,     0,
            0,   203,     0,     0,   881,  -120,  -120,     0,     0,     0,
            0,     0,     0,     0,   425,  2779,  2779,  2779,  2779,  2323,
            2323,  3360,  3257,  2779,  2779,  2858,  2858,   386,   386,   425,
            2142,   425,   425,   420,   420,  2323,  2323,  2334,  2334,  5709,
            -120,   577,     0,   578,  -161,     0,     0,   579,     0,   588,
            -161,     0,     0,     0,   192,     0,     0,  -161,     0,  5289,
            20565,     0,     0,  4283,     0,     0,   871,   901,   192, 21339,
            902,     0,     0,     0,     0,     0,     0,  4786,     0,     0,
            0,   158,     0, 20436, 18106,  -161,     0,     0,  -161,     0,
            192,   686,    50,  2761, 18106,  2761, 17861, 17748, 17976,     0,
            0,   613,     0, 18106,   688,     0,     0,   102,     0,   616,
            626,   628,   630,   192,  4283,   559,   703,   689,     0,     0,
            0,     0,     0,     0,     0,     0,     0,   192, 20436, 20565,
            0, 20565,   367,   685,     0,     0,     0,     0, 18495, 18885,
            0,     0,     0,    50,   611,     0,   425,  5289,     0,     0,
            150, 21339,     0,     0,     0,     0,   192,   833,     0,   -19,
            0,     0,     0,  1584,  1028,     0,   931,     0,   192,   192,
            20565,     0,     0,  2206,     0, 18106, 18106,     0,  2761,     0,
            2761,     0,  1004,     0,     0,    34,     0,     0,     0,     0,
            0,   928,     0, 18106,     0, 18106,   919, 18106, 20694, 20694,
            0,   467,     0,     0, 20694, 20694,     0,   467,   642,   639,
            99,  -140,     0, 20565, 20694, 20049,     0,   833, 21339, 20565,
            -120,     0,   158,   717,     0,     0,     0,   192,   721,   158,
            0,     0,   635, 21468,   193,     0,     0, 18106,     0,     0,
            20436,     0,   727, 20565, 20565, 20565, 20565,   669,   747,     0,
            20178, 18106, 18106, 18106,     0,    79,     0,     0,     0,   960,
            0,     0,     0,     0,     0, 18106,     0,     0,     0,     0,
            0,   192,  1783,   966,  1777,     0,   670,   956,     0,     0,
            970,     0,   763,   665,   986,   987,     0,   997,     0,   970,
            0,     0,   865,   982,  1006,   192,  1012,     0,     0,  1017,
            1020,     0,   708,     0,   865, 21597,   784,   705, 20565,   807,
            0,  5289,     0,  5289,     0,     0,  5289,     0,  5289,     0,
            20694,     0,     0,     0,  5289, 20565,     0,   833,  5289, 18106,
            18106,     0,     0,     0,     0,     0,   477,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            767,   619,     0,     0, 18106,     0,   193,     0, 20565,     0,
            0,    62,   815,   823,     0, 18885,   725,     0,  1046,  1783,
            1082,     0,     0,  1324,  2206,     0,     0,     0,  2206,     0,
            2761,     0,  2206,     0,     0,     0, 21597,  2206,     0,   734,
            2808,     0,  1004,     0,  2808,     0,     0,     0,     0,     0,
            785,   636,     0,     0,  5289,     0,     0,  5289,     0,   737,
            840, 18106,     0, 22226, 22281, 18365,   364, 18106,     0,     0,
            0,     0,     0, 18106,     0,  1783,  1046,  1783,  1053,     0,
            174,     0,     0,   970,  1066,   970,   970,   636,  1068,     0,
            0,  1072,  1080,     0,   865,  1083,  1068,     0,     0, 22336,
            0,     0,   868,     0,     0,     0,     0,   192,     0,   102,
            878,  1046,  1783,  1324,     0,     0,  2206,     0,     0,     0,
            2206,     0,  2206,     0,  2808,     0,     0,  2206,     0,     0,
            0,     0,     0,     0,     0,     0,  1046,     0,   970,  1068,
            1085,  1068,  1068,     0,     0,  2206,     0,     0,     0,  1068,
            0,
    }, yyRindex = {
//yyRindex 1091
            0,     0,   189,     0,     0,     0,     0,     0,   789,     0,
            0,   873,     0,     0,     0, 14736, 14841,     0,     0, 14947,
            4988,  4485, 15236, 15313, 15419, 15539, 20823,     0, 20307,     0,
            0, 15616, 15720, 15842,   370,  5118,  3609, 15968, 16084,  5491,
            16194,     0,     0,     0,     0,     0,    71,    54,   819,   788,
            171,     0,     0,  1070,     0,     0,     0,  1148,    -6,     0,
            0,     0,     0,     0,     0,     0,  1359,   287,     0,     0,
            0, 10190,     0,     0,     0, 10365,     0,     0,     0,     0,
            0,     0,     0,     0,    40,  1773,  1498, 10496, 16391,     0,
            0, 16759,     0, 16271,     0,     0,     0,     0,     0,     0,
            260,     0,     0,     0,     0,    67,     0, 18625,     0,     0,
            0,     0, 10606,  8308,     0,     0,     0,     0,     0,   824,
            0,     0,     0,  6939,     0,     0,  7069,     0,     0,     0,
            0,    71,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,  1016,  1259,  1515,  1569,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,  1654,  2194,
            2712,     0,  3215,     0,  3718,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0, 16846,     0,     0,     0,    87,   574,     0,
            1217,    56,     0,     0,  8418,  8549,  8724,  8855,  8965,  9096,
            9271,  2470,  9402,  9512,  2603,  9643,     0,  9768,     0,     0,
            9949,     0,     0,     0,     0,     0,   873,     0,   895,     0,
            0,     0,   517,   740,   744,   827,  1040,  1118,  1174,   918,
            1506,  1568,  2242,  1593,     0,     0,  1644,     0,     0,     0,
            0,     0,     0,     0,     0,     0, 14497,     0,     0, 15069,
            16544, 16544,     0,     0,     0,     0,   830,     0,     0,    58,
            0,     0,   830,     0,     0,     0,     0,     0,     0,    52,
            0,     0,     0, 10846, 10737, 16373,    71,     0,   162,   830,
            116,   830,     0,     0,   825,   825,     0,     0,     0,   820,
            1170,  1793,  1801,  2381,  5121,  5624,  6127,   812,  7061,  8072,
            1655,  8185,     0,     0,     0,  8316,   298,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,  -147,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            63,     0,     0,     0,     0,     0,     0,    71,   384,   485,
            0,     0,     0,    69,     0,  1169,     0,     0,     0,   236,
            0,  7483,     0,     0,     0,     0,     0,     0,     0,    63,
            789,     0,   810,     0,     0,    74,     0,   557,   350,     0,
            0,  2184, 10059,     0,    84,  7613,     0,     0,    63,     0,
            0,     0,   709,     0,     0,     0,     0,     0,     0,  1679,
            0,     0,    63,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,   830,     0,     0,     0,     0,
            0,    33,    33,   830,   830,     0,     0,     0,     0,     0,
            0,     0, 13592,    52,     0,     0,     0,     0,     0,     0,
            830,     0,   180,   830,     0,     0,   831,     0,     0,   -91,
            0,     0,     0,  8362,     0,   510,     0,     0,    63,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,   137,     0,     0,     0,     0,
            0,   167,   240,     0,    60,     0,     0,     0,    60,    60,
            0,     0,   100,     0,     0,     0,   687,   100,   209,     0,
            0,     0,     0,     0,     0,  7744,  7882,     0,     0,     0,
            0,     0,     0,     0, 10966, 12807, 12893, 12984, 13072, 12372,
            12469, 13158, 13418, 13244, 13332,  1412, 13504, 11785, 11916, 11075,
            12026, 11206, 11316, 11556, 11676, 12586, 12710, 12131, 12252,  1115,
            7744,  5621,     0,  5994,  4615,     0,     0,  6124,  3982,  6497,
            18625,     0,  4112,     0,   843,     0,     0,  6627,     0, 13678,
            0,     0,     0, 16809,     0,     0,     0,     0,   830,     0,
            600,     0,     0,     0,     0, 10315,     0, 14586,     0,     0,
            0,     0,     0,     0,   789,  9818,  7211,  7341,     0,     0,
            843,     0,   830,   177,   789,   305,     0,     0,    52,     0,
            657,   226,     0,   612,   926,     0,     0,   926,     0,  2976,
            3106,  3479,  2053,   843, 14648,   926,     0,     0,     0,     0,
            0,     0,     0,  2813,  3315,  3818,   870,   843,     0,     0,
            0,     0, 16482, 16544,     0,     0,     0,     0,    41,   241,
            0,     0,     0,   830,     0,     0, 11435, 13764,   131,     0,
            825,     0,  1927,  1937,  9207,  1326,   843,   629,     0,     0,
            0,     0,     0,     0,   210,     0,   211,     0,   830,    13,
            0,     0,     0,     0,     0,   370,    52,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,   -27,     0,   370,     0,    52,     0,   370,     0,     0,
            0,  1930,     0,     0,     0,     0,     0, 16646, 15130,     0,
            16795, 16706,  1682,     0,     0,     0,     0,   644,     0,     0,
            7882,     0,     0,     0,     0,     0,     0,   830,     0,     0,
            0,     0,     0,     0,     0,     0,     0,   370,     0,     0,
            0,     0,     0,     0,     0,     0,     0,  8177,     0,     0,
            0,   641,   370,   370,  1262,     0,     0,     0,     0,    33,
            0,     0,     0,     0,  1541,    52,     0,     0,     0,     0,
            0,   830,     0,   215,     0,     0,     0,  -166,     0,     0,
            60,     0,     0,     0,    60,    60,     0,    60,     0,    60,
            0,     0,   100,   119,    75,   -27,    75,     0,     0,    80,
            75,     0,     0,     0,    80,   151,     0,     0,     0,     0,
            0, 13852,     0, 13938,     0,     0, 14024,     0, 14112,     0,
            0,     0,     0,     0, 14198,     0,  8674,   664, 14284,    52,
            789,     0,     0,     0,     0,     0,   810,     0,   888,  1224,
            1320,  1915,  2295,  3217,  3720,  1194,  4223,  4726,  1282,  5229,
            0,     0,  6235,     0,   789,     0,     0,     0,     0,     0,
            0,   926,     0,     0,     0,   246,     0,     0,   223,     0,
            227,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            111,     0,     0,     0,     0,     0,     0,     0,   464,   654,
            0,   156,     0,     0, 14372,     0,     0, 14458,  9221,     0,
            0,   789,  7127,     0,     0,    63,   574,    84,     0,     0,
            0,     0,     0,   370,     0,     0,   237,     0,   247,     0,
            -73,     0,     0,    60,    60,    60,    60,   157,    75,     0,
            0,    75,    75,     0,    80,    75,    75,     0,     0,     0,
            0,     0,     0,  1386,  1923,  2222,  1809,   843,     0,   926,
            0,   248,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            1022,  1039,     0,   753,     0,     0,   255,     0,    60,    75,
            75,    75,    75,     0,     0,     0,     0,     0,     0,    75,
            0,
    }, yyGindex = {
//yyGindex 179
            0,     0,    27,     0,  -331,     0,   -72,     2,    -5,   537,
            975,     0,     0,   163,     0,     0,     0,  1123,     0,     0,
            909,  1140,     0,   449,     0,     0,     0,   829,     0,    30,
            1202,  -417,    66,     0,   112,     0,   402,  -429,     0,     8,
            264,  1765,    24,    20,   749,     6,    98,  -413,     0,   159,
            0,   614,     0,    -9,     0,    44,  1207,   569,     0,     0,
            -699,     0,     0,   955,  -382,     0,     0,     0,  -422,   295,
            -266,   -86,    -1,   130,  -448,     0,     0,   799,    -2,   138,
            0,     0, 11901,   462,  -786,     0,     0,     0,     0,  -274,
            1554,   455,   244,   478,   269,     0,     0,     0,     4,  -466,
            0,  -462,   262,  -295,  -449,     0,  -580,  8181,   -71,   448,
            -491,  1238,   -28,   249,  1264,     0,   -12,  -686,     0,  -676,
            0,     0,  -189,  -822,     0,  -407,  -781,   503,   205,  -141,
            -624,     0,  -840,  -427,     0,    28,     0,  1645,  2022,   751,
            0,    47,   -10,     0,     0,     0,   -23,     0,     0,  -309,
            0,     0,     0,  -239,     0,  -419,     0,     0,     0,     0,
            0,     0,    22,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,
    };
    protected static final short[] yyTable = YyTables.yyTable();
    protected static final short[] yyCheck = YyTables.yyCheck();

    /** maps symbol value to printable name.
     @see #yyExpecting
     */
    protected static final String[] yyNames = {
            "end-of-file",null,null,null,null,null,null,null,null,null,"'\\n'",
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,"' '",null,null,null,null,null,
            null,null,null,null,null,null,"','",null,null,null,null,null,null,
            null,null,null,null,null,null,null,"':'","';'",null,"'='",null,"'?'",
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,
            "'['",null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,
            "kCLASS","kMODULE","kDEF","kUNDEF","kBEGIN","kRESCUE","kENSURE",
            "kEND","kIF","kUNLESS","kTHEN","kELSIF","kELSE","kCASE","kWHEN",
            "kWHILE","kUNTIL","kFOR","kBREAK","kNEXT","kREDO","kRETRY","kIN",
            "kDO","kDO_COND","kDO_BLOCK","kRETURN","kYIELD","kSUPER","kSELF",
            "kNIL","kTRUE","kFALSE","kAND","kOR","kNOT","kIF_MOD","kUNLESS_MOD",
            "kWHILE_MOD","kUNTIL_MOD","kRESCUE_MOD","kALIAS","kDEFINED","klBEGIN",
            "klEND","k__LINE__","k__FILE__","k__ENCODING__","kDO_LAMBDA",
            "kSIGNAL","tIDENTIFIER","tFID","tGVAR","tIVAR","tCONSTANT","tCVAR",
            "tLABEL","tCHAR","tUPLUS","tUMINUS","tUMINUS_NUM","tPOW","tCMP","tEQ",
            "tEQQ","tNEQ","tGEQ","tLEQ","tANDOP","tOROP","tMATCH","tNMATCH",
            "tDOT","tDOT2","tDOT3","tAREF","tASET","tLSHFT","tRSHFT","tCOLON2",
            "tCOLON3","tOP_ASGN","tASSOC","tLPAREN","tLPAREN2","tRPAREN",
            "tLPAREN_ARG","tLBRACK","tRBRACK","tLBRACE","tLBRACE_ARG","tSTAR",
            "tSTAR2","tAMPER","tAMPER2","tTILDE","tPERCENT","tDIVIDE","tPLUS",
            "tMINUS","tLT","tGT","tPIPE","tBANG","tCARET","tLCURLY","tRCURLY",
            "tBACK_REF2","tSYMBEG","tSTRING_BEG","tXSTRING_BEG","tREGEXP_BEG",
            "tWORDS_BEG","tQWORDS_BEG","tSTRING_DBEG","tSTRING_DVAR",
            "tSTRING_END","tLAMBDA","tLAMBEG","tNTH_REF","tBACK_REF",
            "tSTRING_CONTENT","tINTEGER","tIMAGINARY","tFLOAT","tRATIONAL",
            "tREGEXP_END","tSYMBOLS_BEG","tQSYMBOLS_BEG","tDSTAR","tSTRING_DEND",
            "tLABEL_END","tLOWEST",
    };

    /** printable rules for debugging.
     */
    protected static final String [] yyRule = {
            "$accept : program",
            "$$1 :",
            "program : $$1 top_compstmt",
            "top_compstmt : top_stmts opt_terms",
            "top_stmts : none",
            "top_stmts : top_stmt",
            "top_stmts : top_stmts terms top_stmt",
            "top_stmts : error top_stmt",
            "top_stmt : stmt",
            "$$2 :",
            "top_stmt : klBEGIN $$2 tLCURLY top_compstmt tRCURLY",
            "bodystmt : compstmt opt_rescue opt_else opt_ensure",
            "compstmt : stmts opt_terms",
            "stmts : none",
            "stmts : stmt_or_begin",
            "stmts : stmts terms stmt_or_begin",
            "stmts : error stmt",
            "stmt_or_begin : stmt",
            "$$3 :",
            "stmt_or_begin : kBEGIN $$3 tLCURLY top_compstmt tRCURLY",
            "$$4 :",
            "stmt : kALIAS fitem $$4 fitem",
            "stmt : kALIAS tGVAR tGVAR",
            "stmt : kALIAS tGVAR tBACK_REF",
            "stmt : kALIAS tGVAR tNTH_REF",
            "stmt : kUNDEF undef_list",
            "stmt : stmt kIF_MOD expr_value",
            "stmt : stmt kUNLESS_MOD expr_value",
            "stmt : stmt kWHILE_MOD expr_value",
            "stmt : stmt kUNTIL_MOD expr_value",
            "stmt : stmt kRESCUE_MOD stmt",
            "stmt : klEND tLCURLY compstmt tRCURLY",
            "stmt : command_asgn",
            "stmt : mlhs '=' command_call",
            "stmt : var_lhs tOP_ASGN command_call",
            "stmt : primary_value '[' opt_call_args rbracket tOP_ASGN command_call",
            "stmt : primary_value tDOT tIDENTIFIER tOP_ASGN command_call",
            "stmt : primary_value tDOT tCONSTANT tOP_ASGN command_call",
            "stmt : primary_value tCOLON2 tCONSTANT tOP_ASGN command_call",
            "stmt : primary_value tCOLON2 tIDENTIFIER tOP_ASGN command_call",
            "stmt : backref tOP_ASGN command_call",
            "stmt : lhs '=' mrhs",
            "stmt : mlhs '=' mrhs_arg",
            "stmt : expr",
            "command_asgn : lhs '=' command_call",
            "command_asgn : lhs '=' command_asgn",
            "expr : command_call",
            "expr : expr kAND expr",
            "expr : expr kOR expr",
            "expr : kNOT opt_nl expr",
            "expr : tBANG command_call",
            "expr : arg",
            "expr_value : expr",
            "command_call : command",
            "command_call : block_command",
            "block_command : block_call",
            "block_command : block_call dot_or_colon operation2 command_args",
            "$$5 :",
            "cmd_brace_block : tLBRACE_ARG $$5 opt_block_param compstmt tRCURLY",
            "fcall : operation",
            "command : fcall command_args",
            "command : fcall command_args cmd_brace_block",
            "command : primary_value tDOT operation2 command_args",
            "command : primary_value tDOT operation2 command_args cmd_brace_block",
            "command : primary_value tCOLON2 operation2 command_args",
            "command : primary_value tCOLON2 operation2 command_args cmd_brace_block",
            "command : kSUPER command_args",
            "command : kYIELD command_args",
            "command : kRETURN call_args",
            "command : kBREAK call_args",
            "command : kNEXT call_args",
            "mlhs : mlhs_basic",
            "mlhs : tLPAREN mlhs_inner rparen",
            "mlhs_inner : mlhs_basic",
            "mlhs_inner : tLPAREN mlhs_inner rparen",
            "mlhs_basic : mlhs_head",
            "mlhs_basic : mlhs_head mlhs_item",
            "mlhs_basic : mlhs_head tSTAR mlhs_node",
            "mlhs_basic : mlhs_head tSTAR mlhs_node ',' mlhs_post",
            "mlhs_basic : mlhs_head tSTAR",
            "mlhs_basic : mlhs_head tSTAR ',' mlhs_post",
            "mlhs_basic : tSTAR mlhs_node",
            "mlhs_basic : tSTAR mlhs_node ',' mlhs_post",
            "mlhs_basic : tSTAR",
            "mlhs_basic : tSTAR ',' mlhs_post",
            "mlhs_item : mlhs_node",
            "mlhs_item : tLPAREN mlhs_inner rparen",
            "mlhs_head : mlhs_item ','",
            "mlhs_head : mlhs_head mlhs_item ','",
            "mlhs_post : mlhs_item",
            "mlhs_post : mlhs_post ',' mlhs_item",
            "mlhs_node : tIDENTIFIER",
            "mlhs_node : tIVAR",
            "mlhs_node : tGVAR",
            "mlhs_node : tCONSTANT",
            "mlhs_node : tCVAR",
            "mlhs_node : kNIL",
            "mlhs_node : kSELF",
            "mlhs_node : kTRUE",
            "mlhs_node : kFALSE",
            "mlhs_node : k__FILE__",
            "mlhs_node : k__LINE__",
            "mlhs_node : k__ENCODING__",
            "mlhs_node : primary_value '[' opt_call_args rbracket",
            "mlhs_node : primary_value tDOT tIDENTIFIER",
            "mlhs_node : primary_value tCOLON2 tIDENTIFIER",
            "mlhs_node : primary_value tDOT tCONSTANT",
            "mlhs_node : primary_value tCOLON2 tCONSTANT",
            "mlhs_node : tCOLON3 tCONSTANT",
            "mlhs_node : backref",
            "lhs : tIDENTIFIER",
            "lhs : tIVAR",
            "lhs : tGVAR",
            "lhs : tCONSTANT",
            "lhs : tCVAR",
            "lhs : kNIL",
            "lhs : kSELF",
            "lhs : kTRUE",
            "lhs : kFALSE",
            "lhs : k__FILE__",
            "lhs : k__LINE__",
            "lhs : k__ENCODING__",
            "lhs : primary_value '[' opt_call_args rbracket",
            "lhs : primary_value tDOT tIDENTIFIER",
            "lhs : primary_value tCOLON2 tIDENTIFIER",
            "lhs : primary_value tDOT tCONSTANT",
            "lhs : primary_value tCOLON2 tCONSTANT",
            "lhs : tCOLON3 tCONSTANT",
            "lhs : backref",
            "cname : tIDENTIFIER",
            "cname : tCONSTANT",
            "cpath : tCOLON3 cname",
            "cpath : cname",
            "cpath : primary_value tCOLON2 cname",
            "fname : tIDENTIFIER",
            "fname : tCONSTANT",
            "fname : tFID",
            "fname : op",
            "fname : reswords",
            "fsym : fname",
            "fsym : symbol",
            "fitem : fsym",
            "fitem : dsym",
            "undef_list : fitem",
            "$$6 :",
            "undef_list : undef_list ',' $$6 fitem",
            "op : tPIPE",
            "op : tCARET",
            "op : tAMPER2",
            "op : tCMP",
            "op : tEQ",
            "op : tEQQ",
            "op : tMATCH",
            "op : tNMATCH",
            "op : tGT",
            "op : tGEQ",
            "op : tLT",
            "op : tLEQ",
            "op : tNEQ",
            "op : tLSHFT",
            "op : tRSHFT",
            "op : tDSTAR",
            "op : tPLUS",
            "op : tMINUS",
            "op : tSTAR2",
            "op : tSTAR",
            "op : tDIVIDE",
            "op : tPERCENT",
            "op : tPOW",
            "op : tBANG",
            "op : tTILDE",
            "op : tUPLUS",
            "op : tUMINUS",
            "op : tAREF",
            "op : tASET",
            "op : tBACK_REF2",
            "reswords : k__LINE__",
            "reswords : k__FILE__",
            "reswords : k__ENCODING__",
            "reswords : klBEGIN",
            "reswords : klEND",
            "reswords : kALIAS",
            "reswords : kAND",
            "reswords : kBEGIN",
            "reswords : kBREAK",
            "reswords : kCASE",
            "reswords : kCLASS",
            "reswords : kDEF",
            "reswords : kDEFINED",
            "reswords : kDO",
            "reswords : kELSE",
            "reswords : kELSIF",
            "reswords : kEND",
            "reswords : kENSURE",
            "reswords : kFALSE",
            "reswords : kFOR",
            "reswords : kIN",
            "reswords : kMODULE",
            "reswords : kNEXT",
            "reswords : kNIL",
            "reswords : kNOT",
            "reswords : kOR",
            "reswords : kREDO",
            "reswords : kRESCUE",
            "reswords : kRETRY",
            "reswords : kRETURN",
            "reswords : kSELF",
            "reswords : kSUPER",
            "reswords : kTHEN",
            "reswords : kTRUE",
            "reswords : kUNDEF",
            "reswords : kWHEN",
            "reswords : kYIELD",
            "reswords : kIF_MOD",
            "reswords : kUNLESS_MOD",
            "reswords : kSIGNAL",
            "reswords : kWHILE_MOD",
            "reswords : kUNTIL_MOD",
            "reswords : kRESCUE_MOD",
            "arg : lhs '=' arg",
            "arg : lhs '=' arg kRESCUE_MOD arg",
            "arg : var_lhs tOP_ASGN arg",
            "arg : var_lhs tOP_ASGN arg kRESCUE_MOD arg",
            "arg : primary_value '[' opt_call_args rbracket tOP_ASGN arg",
            "arg : primary_value tDOT tIDENTIFIER tOP_ASGN arg",
            "arg : primary_value tDOT tCONSTANT tOP_ASGN arg",
            "arg : primary_value tCOLON2 tIDENTIFIER tOP_ASGN arg",
            "arg : primary_value tCOLON2 tCONSTANT tOP_ASGN arg",
            "arg : tCOLON3 tCONSTANT tOP_ASGN arg",
            "arg : backref tOP_ASGN arg",
            "arg : arg tDOT2 arg",
            "arg : arg tDOT3 arg",
            "arg : arg tPLUS arg",
            "arg : arg tMINUS arg",
            "arg : arg tSTAR2 arg",
            "arg : arg tDIVIDE arg",
            "arg : arg tPERCENT arg",
            "arg : arg tPOW arg",
            "arg : tUMINUS_NUM simple_numeric tPOW arg",
            "arg : tUPLUS arg",
            "arg : tUMINUS arg",
            "arg : arg tPIPE arg",
            "arg : arg tCARET arg",
            "arg : arg tAMPER2 arg",
            "arg : arg tCMP arg",
            "arg : arg tGT arg",
            "arg : arg tGEQ arg",
            "arg : arg tLT arg",
            "arg : arg tLEQ arg",
            "arg : arg tEQ arg",
            "arg : arg tEQQ arg",
            "arg : arg tNEQ arg",
            "arg : arg tMATCH arg",
            "arg : arg tNMATCH arg",
            "arg : tBANG arg",
            "arg : tTILDE arg",
            "arg : arg tLSHFT arg",
            "arg : arg tRSHFT arg",
            "arg : arg tANDOP arg",
            "arg : arg tOROP arg",
            "arg : kDEFINED opt_nl arg",
            "arg : arg '?' arg opt_nl ':' arg",
            "arg : primary",
            "arg_value : arg",
            "aref_args : none",
            "aref_args : args trailer",
            "aref_args : args ',' assocs trailer",
            "aref_args : assocs trailer",
            "paren_args : tLPAREN2 opt_call_args rparen",
            "opt_paren_args : none",
            "opt_paren_args : paren_args",
            "opt_call_args : none",
            "opt_call_args : call_args",
            "opt_call_args : args ','",
            "opt_call_args : args ',' assocs ','",
            "opt_call_args : assocs ','",
            "call_args : command",
            "call_args : args opt_block_arg",
            "call_args : assocs opt_block_arg",
            "call_args : args ',' assocs opt_block_arg",
            "call_args : block_arg",
            "$$7 :",
            "command_args : $$7 call_args",
            "block_arg : tAMPER arg_value",
            "opt_block_arg : ',' block_arg",
            "opt_block_arg : none_block_pass",
            "args : arg_value",
            "args : tSTAR arg_value",
            "args : args ',' arg_value",
            "args : args ',' tSTAR arg_value",
            "mrhs_arg : mrhs",
            "mrhs_arg : arg_value",
            "mrhs : args ',' arg_value",
            "mrhs : args ',' tSTAR arg_value",
            "mrhs : tSTAR arg_value",
            "primary : literal",
            "primary : strings",
            "primary : xstring",
            "primary : regexp",
            "primary : words",
            "primary : qwords",
            "primary : symbols",
            "primary : qsymbols",
            "primary : var_ref",
            "primary : backref",
            "primary : tFID",
            "primary : kBEGIN bodystmt kEND",
            "$$8 :",
            "primary : tLPAREN_ARG $$8 rparen",
            "$$9 :",
            "primary : tLPAREN_ARG expr $$9 rparen",
            "primary : tLPAREN compstmt tRPAREN",
            "primary : primary_value tCOLON2 tCONSTANT",
            "primary : tCOLON3 tCONSTANT",
            "primary : tLBRACK aref_args tRBRACK",
            "primary : tLBRACE assoc_list tRCURLY",
            "primary : kRETURN",
            "primary : kYIELD tLPAREN2 call_args rparen",
            "primary : kYIELD tLPAREN2 rparen",
            "primary : kYIELD",
            "primary : kDEFINED opt_nl tLPAREN2 expr rparen",
            "primary : kNOT tLPAREN2 expr rparen",
            "primary : kNOT tLPAREN2 rparen",
            "primary : fcall brace_block",
            "primary : method_call",
            "primary : method_call brace_block",
            "primary : tLAMBDA lambda",
            "primary : kIF expr_value then compstmt if_tail kEND",
            "primary : kUNLESS expr_value then compstmt opt_else kEND",
            "$$10 :",
            "$$11 :",
            "primary : kWHILE $$10 expr_value do $$11 compstmt kEND",
            "$$12 :",
            "$$13 :",
            "primary : kUNTIL $$12 expr_value do $$13 compstmt kEND",
            "primary : kCASE expr_value opt_terms case_body kEND",
            "primary : kCASE opt_terms case_body kEND",
            "$$14 :",
            "$$15 :",
            "primary : kFOR for_var kIN $$14 expr_value do $$15 compstmt kEND",
            "$$16 :",
            "primary : kCLASS cpath superclass $$16 bodystmt kEND",
            "$$17 :",
            "$$18 :",
            "primary : kCLASS tLSHFT expr $$17 term $$18 bodystmt kEND",
            "$$19 :",
            "primary : kMODULE cpath $$19 bodystmt kEND",
            "$$20 :",
            "primary : kDEF fname $$20 f_arglist bodystmt kEND",
            "$$21 :",
            "$$22 :",
            "primary : kDEF singleton dot_or_colon $$21 fname $$22 f_arglist bodystmt kEND",
            "primary : kBREAK",
            "primary : kNEXT",
            "primary : kREDO",
            "primary : kRETRY",
            "primary : kSIGNAL compstmt kEND",
            "primary_value : primary",
            "then : term",
            "then : kTHEN",
            "then : term kTHEN",
            "do : term",
            "do : kDO_COND",
            "if_tail : opt_else",
            "if_tail : kELSIF expr_value then compstmt if_tail",
            "opt_else : none",
            "opt_else : kELSE compstmt",
            "for_var : lhs",
            "for_var : mlhs",
            "f_marg : f_norm_arg",
            "f_marg : tLPAREN f_margs rparen",
            "f_marg_list : f_marg",
            "f_marg_list : f_marg_list ',' f_marg",
            "f_margs : f_marg_list",
            "f_margs : f_marg_list ',' tSTAR f_norm_arg",
            "f_margs : f_marg_list ',' tSTAR f_norm_arg ',' f_marg_list",
            "f_margs : f_marg_list ',' tSTAR",
            "f_margs : f_marg_list ',' tSTAR ',' f_marg_list",
            "f_margs : tSTAR f_norm_arg",
            "f_margs : tSTAR f_norm_arg ',' f_marg_list",
            "f_margs : tSTAR",
            "f_margs : tSTAR ',' f_marg_list",
            "block_args_tail : f_block_kwarg ',' f_kwrest opt_f_block_arg",
            "block_args_tail : f_block_kwarg opt_f_block_arg",
            "block_args_tail : f_kwrest opt_f_block_arg",
            "block_args_tail : f_block_arg",
            "opt_block_args_tail : ',' block_args_tail",
            "opt_block_args_tail :",
            "block_param : f_arg ',' f_block_optarg ',' f_rest_arg opt_block_args_tail",
            "block_param : f_arg ',' f_block_optarg ',' f_rest_arg ',' f_arg opt_block_args_tail",
            "block_param : f_arg ',' f_block_optarg opt_block_args_tail",
            "block_param : f_arg ',' f_block_optarg ',' f_arg opt_block_args_tail",
            "block_param : f_arg ',' f_rest_arg opt_block_args_tail",
            "block_param : f_arg ','",
            "block_param : f_arg ',' f_rest_arg ',' f_arg opt_block_args_tail",
            "block_param : f_arg opt_block_args_tail",
            "block_param : f_block_optarg ',' f_rest_arg opt_block_args_tail",
            "block_param : f_block_optarg ',' f_rest_arg ',' f_arg opt_block_args_tail",
            "block_param : f_block_optarg opt_block_args_tail",
            "block_param : f_block_optarg ',' f_arg opt_block_args_tail",
            "block_param : f_rest_arg opt_block_args_tail",
            "block_param : f_rest_arg ',' f_arg opt_block_args_tail",
            "block_param : block_args_tail",
            "opt_block_param : none",
            "opt_block_param : block_param_def",
            "block_param_def : tPIPE opt_bv_decl tPIPE",
            "block_param_def : tOROP",
            "block_param_def : tPIPE block_param opt_bv_decl tPIPE",
            "opt_bv_decl : opt_nl",
            "opt_bv_decl : opt_nl ';' bv_decls opt_nl",
            "bv_decls : bvar",
            "bv_decls : bv_decls ',' bvar",
            "bvar : tIDENTIFIER",
            "bvar : f_bad_arg",
            "$$23 :",
            "lambda : $$23 f_larglist lambda_body",
            "f_larglist : tLPAREN2 f_args opt_bv_decl tRPAREN",
            "f_larglist : f_args",
            "lambda_body : tLAMBEG compstmt tRCURLY",
            "lambda_body : kDO_LAMBDA compstmt kEND",
            "$$24 :",
            "do_block : kDO_BLOCK $$24 opt_block_param compstmt kEND",
            "block_call : command do_block",
            "block_call : block_call dot_or_colon operation2 opt_paren_args",
            "block_call : block_call dot_or_colon operation2 opt_paren_args brace_block",
            "block_call : block_call dot_or_colon operation2 command_args do_block",
            "method_call : fcall paren_args",
            "method_call : primary_value tDOT operation2 opt_paren_args",
            "method_call : primary_value tCOLON2 operation2 paren_args",
            "method_call : primary_value tCOLON2 operation3",
            "method_call : primary_value tDOT paren_args",
            "method_call : primary_value tCOLON2 paren_args",
            "method_call : kSUPER paren_args",
            "method_call : kSUPER",
            "method_call : primary_value '[' opt_call_args rbracket",
            "$$25 :",
            "brace_block : tLCURLY $$25 opt_block_param compstmt tRCURLY",
            "$$26 :",
            "brace_block : kDO $$26 opt_block_param compstmt kEND",
            "case_body : kWHEN args then compstmt cases",
            "cases : opt_else",
            "cases : case_body",
            "opt_rescue : kRESCUE exc_list exc_var then compstmt opt_rescue",
            "opt_rescue :",
            "exc_list : arg_value",
            "exc_list : mrhs",
            "exc_list : none",
            "exc_var : tASSOC lhs",
            "exc_var : none",
            "opt_ensure : kENSURE compstmt",
            "opt_ensure : none",
            "literal : numeric",
            "literal : symbol",
            "literal : dsym",
            "strings : string",
            "string : tCHAR",
            "string : string1",
            "string : string string1",
            "string1 : tSTRING_BEG string_contents tSTRING_END",
            "xstring : tXSTRING_BEG xstring_contents tSTRING_END",
            "regexp : tREGEXP_BEG regexp_contents tREGEXP_END",
            "words : tWORDS_BEG ' ' tSTRING_END",
            "words : tWORDS_BEG word_list tSTRING_END",
            "word_list :",
            "word_list : word_list word ' '",
            "word : string_content",
            "word : word string_content",
            "symbols : tSYMBOLS_BEG ' ' tSTRING_END",
            "symbols : tSYMBOLS_BEG symbol_list tSTRING_END",
            "symbol_list :",
            "symbol_list : symbol_list word ' '",
            "qwords : tQWORDS_BEG ' ' tSTRING_END",
            "qwords : tQWORDS_BEG qword_list tSTRING_END",
            "qsymbols : tQSYMBOLS_BEG ' ' tSTRING_END",
            "qsymbols : tQSYMBOLS_BEG qsym_list tSTRING_END",
            "qword_list :",
            "qword_list : qword_list tSTRING_CONTENT ' '",
            "qsym_list :",
            "qsym_list : qsym_list tSTRING_CONTENT ' '",
            "string_contents :",
            "string_contents : string_contents string_content",
            "xstring_contents :",
            "xstring_contents : xstring_contents string_content",
            "regexp_contents :",
            "regexp_contents : regexp_contents string_content",
            "string_content : tSTRING_CONTENT",
            "$$27 :",
            "string_content : tSTRING_DVAR $$27 string_dvar",
            "$$28 :",
            "$$29 :",
            "string_content : tSTRING_DBEG $$28 $$29 compstmt tRCURLY",
            "string_dvar : tGVAR",
            "string_dvar : tIVAR",
            "string_dvar : tCVAR",
            "string_dvar : backref",
            "symbol : tSYMBEG sym",
            "sym : fname",
            "sym : tIVAR",
            "sym : tGVAR",
            "sym : tCVAR",
            "dsym : tSYMBEG xstring_contents tSTRING_END",
            "numeric : simple_numeric",
            "numeric : tUMINUS_NUM simple_numeric",
            "simple_numeric : tINTEGER",
            "simple_numeric : tFLOAT",
            "simple_numeric : tRATIONAL",
            "simple_numeric : tIMAGINARY",
            "var_ref : tIDENTIFIER",
            "var_ref : tIVAR",
            "var_ref : tGVAR",
            "var_ref : tCONSTANT",
            "var_ref : tCVAR",
            "var_ref : kNIL",
            "var_ref : kSELF",
            "var_ref : kTRUE",
            "var_ref : kFALSE",
            "var_ref : k__FILE__",
            "var_ref : k__LINE__",
            "var_ref : k__ENCODING__",
            "var_lhs : tIDENTIFIER",
            "var_lhs : tIVAR",
            "var_lhs : tGVAR",
            "var_lhs : tCONSTANT",
            "var_lhs : tCVAR",
            "var_lhs : kNIL",
            "var_lhs : kSELF",
            "var_lhs : kTRUE",
            "var_lhs : kFALSE",
            "var_lhs : k__FILE__",
            "var_lhs : k__LINE__",
            "var_lhs : k__ENCODING__",
            "backref : tNTH_REF",
            "backref : tBACK_REF",
            "superclass : term",
            "$$30 :",
            "superclass : tLT $$30 expr_value term",
            "superclass : error term",
            "f_arglist : tLPAREN2 f_args rparen",
            "$$31 :",
            "f_arglist : $$31 f_args term",
            "args_tail : f_kwarg ',' f_kwrest opt_f_block_arg",
            "args_tail : f_kwarg opt_f_block_arg",
            "args_tail : f_kwrest opt_f_block_arg",
            "args_tail : f_block_arg",
            "opt_args_tail : ',' args_tail",
            "opt_args_tail :",
            "f_args : f_arg ',' f_optarg ',' f_rest_arg opt_args_tail",
            "f_args : f_arg ',' f_optarg ',' f_rest_arg ',' f_arg opt_args_tail",
            "f_args : f_arg ',' f_optarg opt_args_tail",
            "f_args : f_arg ',' f_optarg ',' f_arg opt_args_tail",
            "f_args : f_arg ',' f_rest_arg opt_args_tail",
            "f_args : f_arg ',' f_rest_arg ',' f_arg opt_args_tail",
            "f_args : f_arg opt_args_tail",
            "f_args : f_optarg ',' f_rest_arg opt_args_tail",
            "f_args : f_optarg ',' f_rest_arg ',' f_arg opt_args_tail",
            "f_args : f_optarg opt_args_tail",
            "f_args : f_optarg ',' f_arg opt_args_tail",
            "f_args : f_rest_arg opt_args_tail",
            "f_args : f_rest_arg ',' f_arg opt_args_tail",
            "f_args : args_tail",
            "f_args :",
            "f_bad_arg : tCONSTANT",
            "f_bad_arg : tIVAR",
            "f_bad_arg : tGVAR",
            "f_bad_arg : tCVAR",
            "f_norm_arg : f_bad_arg",
            "f_norm_arg : tIDENTIFIER",
            "f_arg_item : f_norm_arg",
            "f_arg_item : tLPAREN f_margs rparen",
            "f_arg : f_arg_item",
            "f_arg : f_arg ',' f_arg_item",
            "f_label : tLABEL",
            "f_kw : f_label arg_value",
            "f_kw : f_label",
            "f_block_kw : f_label primary_value",
            "f_block_kw : f_label",
            "f_block_kwarg : f_block_kw",
            "f_block_kwarg : f_block_kwarg ',' f_block_kw",
            "f_kwarg : f_kw",
            "f_kwarg : f_kwarg ',' f_kw",
            "kwrest_mark : tPOW",
            "kwrest_mark : tDSTAR",
            "f_kwrest : kwrest_mark tIDENTIFIER",
            "f_kwrest : kwrest_mark",
            "f_opt : f_norm_arg '=' arg_value",
            "f_block_opt : tIDENTIFIER '=' primary_value",
            "f_block_optarg : f_block_opt",
            "f_block_optarg : f_block_optarg ',' f_block_opt",
            "f_optarg : f_opt",
            "f_optarg : f_optarg ',' f_opt",
            "restarg_mark : tSTAR2",
            "restarg_mark : tSTAR",
            "f_rest_arg : restarg_mark tIDENTIFIER",
            "f_rest_arg : restarg_mark",
            "blkarg_mark : tAMPER2",
            "blkarg_mark : tAMPER",
            "f_block_arg : blkarg_mark tIDENTIFIER",
            "opt_f_block_arg : ',' f_block_arg",
            "opt_f_block_arg :",
            "singleton : var_ref",
            "$$32 :",
            "singleton : tLPAREN2 $$32 expr rparen",
            "assoc_list : none",
            "assoc_list : assocs trailer",
            "assocs : assoc",
            "assocs : assocs ',' assoc",
            "assoc : arg_value tASSOC arg_value",
            "assoc : tLABEL arg_value",
            "assoc : tSTRING_BEG string_contents tLABEL_END arg_value",
            "assoc : tDSTAR arg_value",
            "operation : tIDENTIFIER",
            "operation : tCONSTANT",
            "operation : tFID",
            "operation2 : tIDENTIFIER",
            "operation2 : tCONSTANT",
            "operation2 : tFID",
            "operation2 : op",
            "operation3 : tIDENTIFIER",
            "operation3 : tFID",
            "operation3 : op",
            "dot_or_colon : tDOT",
            "dot_or_colon : tCOLON2",
            "opt_terms :",
            "opt_terms : terms",
            "opt_nl :",
            "opt_nl : '\\n'",
            "rparen : opt_nl tRPAREN",
            "rbracket : opt_nl tRBRACK",
            "trailer :",
            "trailer : '\\n'",
            "trailer : ','",
            "term : ';'",
            "term : '\\n'",
            "terms : term",
            "terms : terms ';'",
            "none :",
            "none_block_pass :",
    };

    protected org.jruby.parser.YYDebug yydebug;

    /** index-checked interface to {@link #yyNames}.
     @param token single character or <tt>%token</tt> value.
     @return token name or <tt>[illegal]</tt> or <tt>[unknown]</tt>.
     */
    public static String yyName (int token) {
        if (token < 0 || token > yyNames.length) return "[illegal]";
        String name;
        if ((name = yyNames[token]) != null) return name;
        return "[unknown]";
    }


    /** computes list of expected tokens on error by tracing the tables.
     @param state for which to compute the list.
     @return list of token names.
     */
    protected String[] yyExpecting (int state) {
        int token, n, len = 0;
        boolean[] ok = new boolean[yyNames.length];

        if ((n = yySindex[state]) != 0)
            for (token = n < 0 ? -n : 0;
                 token < yyNames.length && n+token < yyTable.length; ++ token)
                if (yyCheck[n+token] == token && !ok[token] && yyNames[token] != null) {
                    ++ len;
                    ok[token] = true;
                }
        if ((n = yyRindex[state]) != 0)
            for (token = n < 0 ? -n : 0;
                 token < yyNames.length && n+token < yyTable.length; ++ token)
                if (yyCheck[n+token] == token && !ok[token] && yyNames[token] != null) {
                    ++ len;
                    ok[token] = true;
                }

        String result[] = new String[len];
        for (n = token = 0; n < len;  ++ token)
            if (ok[token]) result[n++] = yyNames[token];
        return result;
    }

    /** the generated parser, with debugging messages.
     Maintains a dynamic state and value stack.
     @param yyLex scanner.
     @param yydebug debug message writer implementing <tt>yyDebug</tt>, or <tt>null</tt>.
     @return result of the last reduction, if any.
     */
    public Object yyparse (RubyLexer yyLex, Object ayydebug)
            throws java.io.IOException {
        this.yydebug = (org.jruby.parser.YYDebug) ayydebug;
        return yyparse(yyLex);
    }

    /** initial size and increment of the state/value stack [default 256].
     This is not final so that it can be overwritten outside of invocations
     of {@link #yyparse}.
     */
    protected int yyMax;

    /** executed at the beginning of a reduce action.
     Used as <tt>$$ = yyDefault($1)</tt>, prior to the user-specified action, if any.
     Can be overwritten to provide deep copy, etc.
     @param first value for <tt>$1</tt>, or <tt>null</tt>.
     @return first.
     */
    protected Object yyDefault (Object first) {
        return first;
    }

    /** the generated parser.
     Maintains a dynamic state and value stack.
     @param yyLex scanner.
     @return result of the last reduction, if any.
     */
    public Object yyparse (RubyLexer yyLex) throws java.io.IOException {
        if (yyMax <= 0) yyMax = 256;			// initial size
        int yyState = 0, yyStates[] = new int[yyMax];	// state stack
        Object yyVal = null, yyVals[] = new Object[yyMax];	// value stack
        int yyToken = -1;					// current input
        int yyErrorFlag = 0;				// #tokens to shift

        yyLoop: for (int yyTop = 0;; ++ yyTop) {
            if (yyTop >= yyStates.length) {			// dynamically increase
                int[] i = new int[yyStates.length+yyMax];
                System.arraycopy(yyStates, 0, i, 0, yyStates.length);
                yyStates = i;
                Object[] o = new Object[yyVals.length+yyMax];
                System.arraycopy(yyVals, 0, o, 0, yyVals.length);
                yyVals = o;
            }
            yyStates[yyTop] = yyState;
            yyVals[yyTop] = yyVal;
            if (yydebug != null) yydebug.push(yyState, yyVal);

            yyDiscarded: for (;;) {	// discarding a token does not change stack
                int yyN;
                if ((yyN = yyDefRed[yyState]) == 0) {	// else [default] reduce (yyN)
                    if (yyToken < 0) {
//            yyToken = yyLex.advance() ? yyLex.token() : 0;
                        yyToken = yyLex.nextToken();
                        if (yydebug != null)
                            yydebug.lex(yyState, yyToken, yyName(yyToken), yyLex.value());
                    }
                    if ((yyN = yySindex[yyState]) != 0 && (yyN += yyToken) >= 0
                            && yyN < yyTable.length && yyCheck[yyN] == yyToken) {
                        if (yydebug != null)
                            yydebug.shift(yyState, yyTable[yyN], yyErrorFlag-1);
                        yyState = yyTable[yyN];		// shift to yyN
                        yyVal = yyLex.value();
                        yyToken = -1;
                        if (yyErrorFlag > 0) -- yyErrorFlag;
                        continue yyLoop;
                    }
                    if ((yyN = yyRindex[yyState]) != 0 && (yyN += yyToken) >= 0
                            && yyN < yyTable.length && yyCheck[yyN] == yyToken)
                        yyN = yyTable[yyN];			// reduce (yyN)
                    else
                        switch (yyErrorFlag) {

                            case 0:
                                support.yyerror("syntax error", yyExpecting(yyState), yyNames[yyToken]);
                                if (yydebug != null) yydebug.error("syntax error");

                            case 1: case 2:
                                yyErrorFlag = 3;
                                do {
                                    if ((yyN = yySindex[yyStates[yyTop]]) != 0
                                            && (yyN += yyErrorCode) >= 0 && yyN < yyTable.length
                                            && yyCheck[yyN] == yyErrorCode) {
                                        if (yydebug != null)
                                            yydebug.shift(yyStates[yyTop], yyTable[yyN], 3);
                                        yyState = yyTable[yyN];
                                        yyVal = yyLex.value();
                                        continue yyLoop;
                                    }
                                    if (yydebug != null) yydebug.pop(yyStates[yyTop]);
                                } while (-- yyTop >= 0);
                                if (yydebug != null) yydebug.reject();
                                support.yyerror("irrecoverable syntax error");

                            case 3:
                                if (yyToken == 0) {
                                    if (yydebug != null) yydebug.reject();
                                    support.yyerror("irrecoverable syntax error at end-of-file");
                                }
                                if (yydebug != null)
                                    yydebug.discard(yyState, yyToken, yyName(yyToken),
                                            yyLex.value());
                                yyToken = -1;
                                continue yyDiscarded;		// leave stack alone
                        }
                }
                int yyV = yyTop + 1-yyLen[yyN];
                if (yydebug != null)
                    yydebug.reduce(yyState, yyStates[yyV-1], yyN, yyRule[yyN], yyLen[yyN]);
                switch (yyN) {
// ACTIONS_BEGIN
                    case 1:
                        // line 310 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_BEG);
                        support.initTopLocalVariables();
                    }
                    break;
                    case 2:
                        // line 313 "RubyParser.y"
                    {
  /* ENEBO: Removed !compile_for_eval which probably is to reduce warnings*/
                        if (((Node)yyVals[0+yyTop]) != null) {
                      /* last expression should not be void */
                            if (((Node)yyVals[0+yyTop]) instanceof BlockNode) {
                                support.checkUselessStatement(((BlockNode)yyVals[0+yyTop]).getLast());
                            } else {
                                support.checkUselessStatement(((Node)yyVals[0+yyTop]));
                            }
                        }
                        support.getResult().setAST(support.addRootNode(((Node)yyVals[0+yyTop])));
                    }
                    break;
                    case 3:
                        // line 326 "RubyParser.y"
                    {
                        if (((Node)yyVals[-1+yyTop]) instanceof BlockNode) {
                            support.checkUselessStatements(((BlockNode)yyVals[-1+yyTop]));
                        }
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 5:
                        // line 334 "RubyParser.y"
                    {
                        yyVal = support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop])));
                    }
                    break;
                    case 6:
                        // line 337 "RubyParser.y"
                    {
                        yyVal = support.appendToBlock(((Node)yyVals[-2+yyTop]), support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop]))));
                    }
                    break;
                    case 7:
                        // line 340 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 9:
                        // line 345 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) {
                            support.yyerror("BEGIN in method");
                        }
                    }
                    break;
                    case 10:
                        // line 349 "RubyParser.y"
                    {
                        support.getResult().addBeginNode(new PreExe19Node(((ISourcePosition)yyVals[-4+yyTop]), support.getCurrentScope(), ((Node)yyVals[-1+yyTop])));
                        yyVal = null;
                    }
                    break;
                    case 11:
                        // line 354 "RubyParser.y"
                    {
                        Node node = ((Node)yyVals[-3+yyTop]);

                        if (((RescueBodyNode)yyVals[-2+yyTop]) != null) {
                            node = new RescueNode(support.getPosition(((Node)yyVals[-3+yyTop])), ((Node)yyVals[-3+yyTop]), ((RescueBodyNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]));
                        } else if (((Node)yyVals[-1+yyTop]) != null) {
                            support.warn(ID.ELSE_WITHOUT_RESCUE, support.getPosition(((Node)yyVals[-3+yyTop])), "else without rescue is useless");
                            node = support.appendToBlock(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                        }
                        if (((Node)yyVals[0+yyTop]) != null) {
                            if (node == null) node = NilImplicitNode.NIL;
                            node = new EnsureNode(support.getPosition(((Node)yyVals[-3+yyTop])), node, ((Node)yyVals[0+yyTop]));
                        }

                        support.fixpos(node, ((Node)yyVals[-3+yyTop]));
                        yyVal = node;
                    }
                    break;
                    case 12:
                        // line 372 "RubyParser.y"
                    {
                        if (((Node)yyVals[-1+yyTop]) instanceof BlockNode) {
                            support.checkUselessStatements(((BlockNode)yyVals[-1+yyTop]));
                        }
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 14:
                        // line 380 "RubyParser.y"
                    {
                        yyVal = support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop])));
                    }
                    break;
                    case 15:
                        // line 383 "RubyParser.y"
                    {
                        yyVal = support.appendToBlock(((Node)yyVals[-2+yyTop]), support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop]))));
                    }
                    break;
                    case 16:
                        // line 386 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 17:
                        // line 390 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 18:
                        // line 394 "RubyParser.y"
                    {
                        support.yyerror("BEGIN is permitted only at toplevel");
                    }
                    break;
                    case 19:
                        // line 396 "RubyParser.y"
                    {
                        yyVal = new BeginNode(((ISourcePosition)yyVals[-4+yyTop]), ((Node)yyVals[-3+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-3+yyTop]));
                    }
                    break;
                    case 20:
                        // line 400 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_FNAME);
                    }
                    break;
                    case 21:
                        // line 402 "RubyParser.y"
                    {
                        yyVal = support.newAlias(((ISourcePosition)yyVals[-3+yyTop]), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 22:
                        // line 405 "RubyParser.y"
                    {
                        yyVal = new VAliasNode(((ISourcePosition)yyVals[-2+yyTop]), ((String)yyVals[-1+yyTop]), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 23:
                        // line 408 "RubyParser.y"
                    {
                        yyVal = new VAliasNode(((ISourcePosition)yyVals[-2+yyTop]), ((String)yyVals[-1+yyTop]), "$" + ((BackRefNode)yyVals[0+yyTop]).getType());
                    }
                    break;
                    case 24:
                        // line 411 "RubyParser.y"
                    {
                        support.yyerror("can't make alias for the number variables");
                    }
                    break;
                    case 25:
                        // line 414 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 26:
                        // line 417 "RubyParser.y"
                    {
                        yyVal = new IfNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), null);
                        support.fixpos(((Node)yyVal), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 27:
                        // line 421 "RubyParser.y"
                    {
                        yyVal = new IfNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), null, ((Node)yyVals[-2+yyTop]));
                        support.fixpos(((Node)yyVal), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 28:
                        // line 425 "RubyParser.y"
                    {
                        if (((Node)yyVals[-2+yyTop]) != null && ((Node)yyVals[-2+yyTop]) instanceof BeginNode) {
                            yyVal = new WhileNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((BeginNode)yyVals[-2+yyTop]).getBodyNode(), false);
                        } else {
                            yyVal = new WhileNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), true);
                        }
                    }
                    break;
                    case 29:
                        // line 432 "RubyParser.y"
                    {
                        if (((Node)yyVals[-2+yyTop]) != null && ((Node)yyVals[-2+yyTop]) instanceof BeginNode) {
                            yyVal = new UntilNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((BeginNode)yyVals[-2+yyTop]).getBodyNode(), false);
                        } else {
                            yyVal = new UntilNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), true);
                        }
                    }
                    break;
                    case 30:
                        // line 439 "RubyParser.y"
                    {
                        Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
                        yyVal = new RescueNode(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), new RescueBodyNode(support.getPosition(((Node)yyVals[-2+yyTop])), null, body, null), null);
                    }
                    break;
                    case 31:
                        // line 443 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) {
                            support.warn(ID.END_IN_METHOD, ((ISourcePosition)yyVals[-3+yyTop]), "END in method; use at_exit");
                        }
                        yyVal = new PostExeNode(((ISourcePosition)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 33:
                        // line 450 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[0+yyTop]));
                        ((MultipleAsgn19Node)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                        yyVal = ((MultipleAsgn19Node)yyVals[-2+yyTop]);
                    }
                    break;
                    case 34:
                        // line 455 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[0+yyTop]));

                        ISourcePosition pos = ((AssignableNode)yyVals[-2+yyTop]).getPosition();
                        String asgnOp = ((String)yyVals[-1+yyTop]);
                        if (asgnOp.equals("||")) {
                            ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                            yyVal = new OpAsgnOrNode(pos, support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
                        } else if (asgnOp.equals("&&")) {
                            ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                            yyVal = new OpAsgnAndNode(pos, support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
                        } else {
                            ((AssignableNode)yyVals[-2+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableNode)yyVals[-2+yyTop])), asgnOp, ((Node)yyVals[0+yyTop])));
                            ((AssignableNode)yyVals[-2+yyTop]).setPosition(pos);
                            yyVal = ((AssignableNode)yyVals[-2+yyTop]);
                        }
                    }
                    break;
                    case 35:
                        // line 472 "RubyParser.y"
                    {
  /* FIXME: arg_concat logic missing for opt_call_args*/
                        yyVal = support.new_opElementAsgnNode(((Node)yyVals[-5+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 36:
                        // line 476 "RubyParser.y"
                    {
                        yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), ((String)yyVals[-2+yyTop]), ((String)yyVals[-1+yyTop]));
                    }
                    break;
                    case 37:
                        // line 479 "RubyParser.y"
                    {
                        yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), ((String)yyVals[-2+yyTop]), ((String)yyVals[-1+yyTop]));
                    }
                    break;
                    case 38:
                        // line 482 "RubyParser.y"
                    {
                        support.yyerror("can't make alias for the number variables");
                        yyVal = null;
                    }
                    break;
                    case 39:
                        // line 487 "RubyParser.y"
                    {
                        yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), ((String)yyVals[-2+yyTop]), ((String)yyVals[-1+yyTop]));
                    }
                    break;
                    case 40:
                        // line 490 "RubyParser.y"
                    {
                        support.backrefAssignError(((Node)yyVals[-2+yyTop]));
                    }
                    break;
                    case 41:
                        // line 493 "RubyParser.y"
                    {
                        yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 42:
                        // line 496 "RubyParser.y"
                    {
                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                        yyVal = ((MultipleAsgn19Node)yyVals[-2+yyTop]);
                        ((MultipleAsgn19Node)yyVals[-2+yyTop]).setPosition(support.getPosition(((MultipleAsgn19Node)yyVals[-2+yyTop])));
                    }
                    break;
                    case 44:
                        // line 508 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[0+yyTop]));
                        yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 45:
                        // line 512 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[0+yyTop]));
                        yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 47:
                        // line 519 "RubyParser.y"
                    {
                        yyVal = support.newAndNode(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 48:
                        // line 522 "RubyParser.y"
                    {
                        yyVal = support.newOrNode(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 49:
                        // line 525 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[0+yyTop])), "!");
                    }
                    break;
                    case 50:
                        // line 528 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[0+yyTop])), "!");
                    }
                    break;
                    case 52:
                        // line 533 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 56:
                        // line 543 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 57:
                        // line 548 "RubyParser.y"
                    {
                        support.pushBlockScope();
                    }
                    break;
                    case 58:
                        // line 550 "RubyParser.y"
                    {
                        yyVal = new IterNode(((ISourcePosition)yyVals[-4+yyTop]), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                        support.popCurrentScope();
                    }
                    break;
                    case 59:
                        // line 555 "RubyParser.y"
                    {
                        yyVal = support.new_fcall(((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 60:
                        // line 560 "RubyParser.y"
                    {
                        support.frobnicate_fcall_args(((FCallNode)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                        yyVal = ((FCallNode)yyVals[-1+yyTop]);
                    }
                    break;
                    case 61:
                        // line 564 "RubyParser.y"
                    {
                        support.frobnicate_fcall_args(((FCallNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
                        yyVal = ((FCallNode)yyVals[-2+yyTop]);
                    }
                    break;
                    case 62:
                        // line 568 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 63:
                        // line 571 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((String)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 64:
                        // line 574 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 65:
                        // line 577 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((String)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 66:
                        // line 580 "RubyParser.y"
                    {
                        yyVal = support.new_super(((ISourcePosition)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 67:
                        // line 583 "RubyParser.y"
                    {
                        yyVal = support.new_yield(((ISourcePosition)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 68:
                        // line 586 "RubyParser.y"
                    {
                        yyVal = new ReturnNode(((ISourcePosition)yyVals[-1+yyTop]), support.ret_args(((Node)yyVals[0+yyTop]), ((ISourcePosition)yyVals[-1+yyTop])));
                    }
                    break;
                    case 69:
                        // line 589 "RubyParser.y"
                    {
                        yyVal = new BreakNode(((ISourcePosition)yyVals[-1+yyTop]), support.ret_args(((Node)yyVals[0+yyTop]), ((ISourcePosition)yyVals[-1+yyTop])));
                    }
                    break;
                    case 70:
                        // line 592 "RubyParser.y"
                    {
                        yyVal = new NextNode(((ISourcePosition)yyVals[-1+yyTop]), support.ret_args(((Node)yyVals[0+yyTop]), ((ISourcePosition)yyVals[-1+yyTop])));
                    }
                    break;
                    case 72:
                        // line 598 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 73:
                        // line 603 "RubyParser.y"
                    {
                        yyVal = ((MultipleAsgn19Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 74:
                        // line 606 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ISourcePosition)yyVals[-2+yyTop]), support.newArrayNode(((ISourcePosition)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop])), null, null);
                    }
                    break;
                    case 75:
                        // line 611 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[0+yyTop]).getPosition(), ((ListNode)yyVals[0+yyTop]), null, null);
                    }
                    break;
                    case 76:
                        // line 614 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]).add(((Node)yyVals[0+yyTop])), null, null);
                    }
                    break;
                    case 77:
                        // line 617 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-2+yyTop]).getPosition(), ((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]), (ListNode) null);
                    }
                    break;
                    case 78:
                        // line 620 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-4+yyTop]).getPosition(), ((ListNode)yyVals[-4+yyTop]), ((Node)yyVals[-2+yyTop]), ((ListNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 79:
                        // line 623 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), new StarNode(lexer.getPosition()), null);
                    }
                    break;
                    case 80:
                        // line 626 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), new StarNode(lexer.getPosition()), ((ListNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 81:
                        // line 629 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((Node)yyVals[0+yyTop]).getPosition(), null, ((Node)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 82:
                        // line 632 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((Node)yyVals[-2+yyTop]).getPosition(), null, ((Node)yyVals[-2+yyTop]), ((ListNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 83:
                        // line 635 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(lexer.getPosition(), null, new StarNode(lexer.getPosition()), null);
                    }
                    break;
                    case 84:
                        // line 638 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(lexer.getPosition(), null, new StarNode(lexer.getPosition()), ((ListNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 86:
                        // line 643 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 87:
                        // line 648 "RubyParser.y"
                    {
                        yyVal = support.newArrayNode(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 88:
                        // line 651 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 89:
                        // line 656 "RubyParser.y"
                    {
                        yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 90:
                        // line 659 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 91:
                        // line 663 "RubyParser.y"
                    {
                        yyVal = support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 92:
                        // line 666 "RubyParser.y"
                    {
                        yyVal = new InstAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 93:
                        // line 669 "RubyParser.y"
                    {
                        yyVal = new GlobalAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 94:
                        // line 672 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) support.compile_error("dynamic constant assignment");

                        yyVal = new ConstDeclNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), null, NilImplicitNode.NIL);
                    }
                    break;
                    case 95:
                        // line 677 "RubyParser.y"
                    {
                        yyVal = new ClassVarAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 96:
                        // line 680 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to nil");
                        yyVal = null;
                    }
                    break;
                    case 97:
                        // line 684 "RubyParser.y"
                    {
                        support.compile_error("Can't change the value of self");
                        yyVal = null;
                    }
                    break;
                    case 98:
                        // line 688 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to true");
                        yyVal = null;
                    }
                    break;
                    case 99:
                        // line 692 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to false");
                        yyVal = null;
                    }
                    break;
                    case 100:
                        // line 696 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to __FILE__");
                        yyVal = null;
                    }
                    break;
                    case 101:
                        // line 700 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to __LINE__");
                        yyVal = null;
                    }
                    break;
                    case 102:
                        // line 704 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to __ENCODING__");
                        yyVal = null;
                    }
                    break;
                    case 103:
                        // line 708 "RubyParser.y"
                    {
                        yyVal = support.aryset(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 104:
                        // line 711 "RubyParser.y"
                    {
                        yyVal = support.attrset(((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 105:
                        // line 714 "RubyParser.y"
                    {
                        yyVal = support.attrset(((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 106:
                        // line 717 "RubyParser.y"
                    {
                        yyVal = support.attrset(((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 107:
                        // line 720 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) {
                            support.yyerror("dynamic constant assignment");
                        }

                        ISourcePosition position = support.getPosition(((Node)yyVals[-2+yyTop]));

                        yyVal = new ConstDeclNode(position, null, support.new_colon2(position, ((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop])), NilImplicitNode.NIL);
                    }
                    break;
                    case 108:
                        // line 729 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) {
                            support.yyerror("dynamic constant assignment");
                        }

                        ISourcePosition position = lexer.getPosition();

                        yyVal = new ConstDeclNode(position, null, support.new_colon3(position, ((String)yyVals[0+yyTop])), NilImplicitNode.NIL);
                    }
                    break;
                    case 109:
                        // line 738 "RubyParser.y"
                    {
                        support.backrefAssignError(((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 110:
                        // line 742 "RubyParser.y"
                    {
                        yyVal = support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 111:
                        // line 745 "RubyParser.y"
                    {
                        yyVal = new InstAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 112:
                        // line 748 "RubyParser.y"
                    {
                        yyVal = new GlobalAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 113:
                        // line 751 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) support.compile_error("dynamic constant assignment");

                        yyVal = new ConstDeclNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), null, NilImplicitNode.NIL);
                    }
                    break;
                    case 114:
                        // line 756 "RubyParser.y"
                    {
                        yyVal = new ClassVarAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 115:
                        // line 759 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to nil");
                        yyVal = null;
                    }
                    break;
                    case 116:
                        // line 763 "RubyParser.y"
                    {
                        support.compile_error("Can't change the value of self");
                        yyVal = null;
                    }
                    break;
                    case 117:
                        // line 767 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to true");
                        yyVal = null;
                    }
                    break;
                    case 118:
                        // line 771 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to false");
                        yyVal = null;
                    }
                    break;
                    case 119:
                        // line 775 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to __FILE__");
                        yyVal = null;
                    }
                    break;
                    case 120:
                        // line 779 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to __LINE__");
                        yyVal = null;
                    }
                    break;
                    case 121:
                        // line 783 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to __ENCODING__");
                        yyVal = null;
                    }
                    break;
                    case 122:
                        // line 787 "RubyParser.y"
                    {
                        yyVal = support.aryset(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 123:
                        // line 790 "RubyParser.y"
                    {
                        yyVal = support.attrset(((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 124:
                        // line 793 "RubyParser.y"
                    {
                        yyVal = support.attrset(((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 125:
                        // line 796 "RubyParser.y"
                    {
                        yyVal = support.attrset(((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 126:
                        // line 799 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) {
                            support.yyerror("dynamic constant assignment");
                        }

                        ISourcePosition position = support.getPosition(((Node)yyVals[-2+yyTop]));

                        yyVal = new ConstDeclNode(position, null, support.new_colon2(position, ((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop])), NilImplicitNode.NIL);
                    }
                    break;
                    case 127:
                        // line 808 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) {
                            support.yyerror("dynamic constant assignment");
                        }

                        ISourcePosition position = lexer.getPosition();

                        yyVal = new ConstDeclNode(position, null, support.new_colon3(position, ((String)yyVals[0+yyTop])), NilImplicitNode.NIL);
                    }
                    break;
                    case 128:
                        // line 817 "RubyParser.y"
                    {
                        support.backrefAssignError(((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 129:
                        // line 821 "RubyParser.y"
                    {
                        support.yyerror("class/module name must be CONSTANT");
                    }
                    break;
                    case 131:
                        // line 826 "RubyParser.y"
                    {
                        yyVal = support.new_colon3(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 132:
                        // line 829 "RubyParser.y"
                    {
                        yyVal = support.new_colon2(lexer.getPosition(), null, ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 133:
                        // line 832 "RubyParser.y"
                    {
                        yyVal = support.new_colon2(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 137:
                        // line 838 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_ENDFN);
                        yyVal = ((String)yyVals[0+yyTop]);
                    }
                    break;
                    case 138:
                        // line 842 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_ENDFN);
                        yyVal = ((String)yyVals[0+yyTop]);
                    }
                    break;
                    case 139:
                        // line 848 "RubyParser.y"
                    {
                        yyVal = new LiteralNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 140:
                        // line 851 "RubyParser.y"
                    {
                        yyVal = new LiteralNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 141:
                        // line 856 "RubyParser.y"
                    {
                        yyVal = ((LiteralNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 142:
                        // line 859 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 143:
                        // line 863 "RubyParser.y"
                    {
                        yyVal = support.newUndef(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 144:
                        // line 866 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_FNAME);
                    }
                    break;
                    case 145:
                        // line 868 "RubyParser.y"
                    {
                        yyVal = support.appendToBlock(((Node)yyVals[-3+yyTop]), support.newUndef(((Node)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[0+yyTop])));
                    }
                    break;
                    case 176:
                        // line 880 "RubyParser.y"
                    {
                        yyVal = "__LINE__";
                    }
                    break;
                    case 177:
                        // line 883 "RubyParser.y"
                    {
                        yyVal = "__FILE__";
                    }
                    break;
                    case 178:
                        // line 886 "RubyParser.y"
                    {
                        yyVal = "__ENCODING__";
                    }
                    break;
                    case 179:
                        // line 889 "RubyParser.y"
                    {
                        yyVal = "BEGIN";
                    }
                    break;
                    case 180:
                        // line 892 "RubyParser.y"
                    {
                        yyVal = "END";
                    }
                    break;
                    case 181:
                        // line 895 "RubyParser.y"
                    {
                        yyVal = "alias";
                    }
                    break;
                    case 182:
                        // line 898 "RubyParser.y"
                    {
                        yyVal = "and";
                    }
                    break;
                    case 183:
                        // line 901 "RubyParser.y"
                    {
                        yyVal = "begin";
                    }
                    break;
                    case 184:
                        // line 904 "RubyParser.y"
                    {
                        yyVal = "break";
                    }
                    break;
                    case 185:
                        // line 907 "RubyParser.y"
                    {
                        yyVal = "case";
                    }
                    break;
                    case 186:
                        // line 910 "RubyParser.y"
                    {
                        yyVal = "class";
                    }
                    break;
                    case 187:
                        // line 913 "RubyParser.y"
                    {
                        yyVal = "def";
                    }
                    break;
                    case 188:
                        // line 916 "RubyParser.y"
                    {
                        yyVal = "defined?";
                    }
                    break;
                    case 189:
                        // line 919 "RubyParser.y"
                    {
                        yyVal = "do";
                    }
                    break;
                    case 190:
                        // line 922 "RubyParser.y"
                    {
                        yyVal = "else";
                    }
                    break;
                    case 191:
                        // line 925 "RubyParser.y"
                    {
                        yyVal = "elsif";
                    }
                    break;
                    case 192:
                        // line 928 "RubyParser.y"
                    {
                        yyVal = "end";
                    }
                    break;
                    case 193:
                        // line 931 "RubyParser.y"
                    {
                        yyVal = "ensure";
                    }
                    break;
                    case 194:
                        // line 934 "RubyParser.y"
                    {
                        yyVal = "false";
                    }
                    break;
                    case 195:
                        // line 937 "RubyParser.y"
                    {
                        yyVal = "for";
                    }
                    break;
                    case 196:
                        // line 940 "RubyParser.y"
                    {
                        yyVal = "in";
                    }
                    break;
                    case 197:
                        // line 943 "RubyParser.y"
                    {
                        yyVal = "module";
                    }
                    break;
                    case 198:
                        // line 946 "RubyParser.y"
                    {
                        yyVal = "next";
                    }
                    break;
                    case 199:
                        // line 949 "RubyParser.y"
                    {
                        yyVal = "nil";
                    }
                    break;
                    case 200:
                        // line 952 "RubyParser.y"
                    {
                        yyVal = "not";
                    }
                    break;
                    case 201:
                        // line 955 "RubyParser.y"
                    {
                        yyVal = "or";
                    }
                    break;
                    case 202:
                        // line 958 "RubyParser.y"
                    {
                        yyVal = "redo";
                    }
                    break;
                    case 203:
                        // line 961 "RubyParser.y"
                    {
                        yyVal = "rescue";
                    }
                    break;
                    case 204:
                        // line 964 "RubyParser.y"
                    {
                        yyVal = "retry";
                    }
                    break;
                    case 205:
                        // line 967 "RubyParser.y"
                    {
                        yyVal = "return";
                    }
                    break;
                    case 206:
                        // line 970 "RubyParser.y"
                    {
                        yyVal = "self";
                    }
                    break;
                    case 207:
                        // line 973 "RubyParser.y"
                    {
                        yyVal = "super";
                    }
                    break;
                    case 208:
                        // line 976 "RubyParser.y"
                    {
                        yyVal = "then";
                    }
                    break;
                    case 209:
                        // line 979 "RubyParser.y"
                    {
                        yyVal = "true";
                    }
                    break;
                    case 210:
                        // line 982 "RubyParser.y"
                    {
                        yyVal = "undef";
                    }
                    break;
                    case 211:
                        // line 985 "RubyParser.y"
                    {
                        yyVal = "when";
                    }
                    break;
                    case 212:
                        // line 988 "RubyParser.y"
                    {
                        yyVal = "yield";
                    }
                    break;
                    case 213:
                        // line 991 "RubyParser.y"
                    {
                        yyVal = "if";
                    }
                    break;
                    case 214:
                        // line 994 "RubyParser.y"
                    {
                        yyVal = "unless";
                    }
                    break;
                    case 215:
                        // line 997 "RubyParser.y"
                    {
                        yyVal = "signal";
                    }
                    break;
                    case 216:
                        // line 1000 "RubyParser.y"
                    {
                        yyVal = "while";
                    }
                    break;
                    case 217:
                        // line 1003 "RubyParser.y"
                    {
                        yyVal = "until";
                    }
                    break;
                    case 218:
                        // line 1006 "RubyParser.y"
                    {
                        yyVal = "rescue";
                    }
                    break;
                    case 219:
                        // line 1010 "RubyParser.y"
                    {
                        yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    /* FIXME: Consider fixing node_assign itself rather than single case*/
                        ((Node)yyVal).setPosition(support.getPosition(((Node)yyVals[-2+yyTop])));
                    }
                    break;
                    case 220:
                        // line 1015 "RubyParser.y"
                    {
                        ISourcePosition position = support.getPosition(((Node)yyVals[-4+yyTop]));
                        Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
                        yyVal = support.node_assign(((Node)yyVals[-4+yyTop]), new RescueNode(position, ((Node)yyVals[-2+yyTop]), new RescueBodyNode(position, null, body, null), null));
                    }
                    break;
                    case 221:
                        // line 1020 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[0+yyTop]));

                        ISourcePosition pos = ((AssignableNode)yyVals[-2+yyTop]).getPosition();
                        String asgnOp = ((String)yyVals[-1+yyTop]);
                        if (asgnOp.equals("||")) {
                            ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                            yyVal = new OpAsgnOrNode(pos, support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
                        } else if (asgnOp.equals("&&")) {
                            ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                            yyVal = new OpAsgnAndNode(pos, support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
                        } else {
                            ((AssignableNode)yyVals[-2+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableNode)yyVals[-2+yyTop])), asgnOp, ((Node)yyVals[0+yyTop])));
                            ((AssignableNode)yyVals[-2+yyTop]).setPosition(pos);
                            yyVal = ((AssignableNode)yyVals[-2+yyTop]);
                        }
                    }
                    break;
                    case 222:
                        // line 1037 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[-2+yyTop]));
                        ISourcePosition pos = support.getPosition(((Node)yyVals[0+yyTop]));
                        Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
                        Node rescue = new RescueNode(pos, ((Node)yyVals[-2+yyTop]), new RescueBodyNode(support.getPosition(((Node)yyVals[-2+yyTop])), null, body, null), null);

                        pos = ((AssignableNode)yyVals[-4+yyTop]).getPosition();
                        String asgnOp = ((String)yyVals[-3+yyTop]);
                        if (asgnOp.equals("||")) {
                            ((AssignableNode)yyVals[-4+yyTop]).setValueNode(rescue);
                            yyVal = new OpAsgnOrNode(pos, support.gettable2(((AssignableNode)yyVals[-4+yyTop])), ((AssignableNode)yyVals[-4+yyTop]));
                        } else if (asgnOp.equals("&&")) {
                            ((AssignableNode)yyVals[-4+yyTop]).setValueNode(rescue);
                            yyVal = new OpAsgnAndNode(pos, support.gettable2(((AssignableNode)yyVals[-4+yyTop])), ((AssignableNode)yyVals[-4+yyTop]));
                        } else {
                            ((AssignableNode)yyVals[-4+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableNode)yyVals[-4+yyTop])), asgnOp, rescue));
                            ((AssignableNode)yyVals[-4+yyTop]).setPosition(pos);
                            yyVal = ((AssignableNode)yyVals[-4+yyTop]);
                        }
                    }
                    break;
                    case 223:
                        // line 1057 "RubyParser.y"
                    {
  /* FIXME: arg_concat missing for opt_call_args*/
                        yyVal = support.new_opElementAsgnNode(((Node)yyVals[-5+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 224:
                        // line 1061 "RubyParser.y"
                    {
                        yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), ((String)yyVals[-2+yyTop]), ((String)yyVals[-1+yyTop]));
                    }
                    break;
                    case 225:
                        // line 1064 "RubyParser.y"
                    {
                        yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), ((String)yyVals[-2+yyTop]), ((String)yyVals[-1+yyTop]));
                    }
                    break;
                    case 226:
                        // line 1067 "RubyParser.y"
                    {
                        yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), ((String)yyVals[-2+yyTop]), ((String)yyVals[-1+yyTop]));
                    }
                    break;
                    case 227:
                        // line 1070 "RubyParser.y"
                    {
                        support.yyerror("constant re-assignment");
                    }
                    break;
                    case 228:
                        // line 1073 "RubyParser.y"
                    {
                        support.yyerror("constant re-assignment");
                    }
                    break;
                    case 229:
                        // line 1076 "RubyParser.y"
                    {
                        support.backrefAssignError(((Node)yyVals[-2+yyTop]));
                    }
                    break;
                    case 230:
                        // line 1079 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[-2+yyTop]));
                        support.checkExpression(((Node)yyVals[0+yyTop]));

                        boolean isLiteral = ((Node)yyVals[-2+yyTop]) instanceof FixnumNode && ((Node)yyVals[0+yyTop]) instanceof FixnumNode;
                        yyVal = new DotNode(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]), false, isLiteral);
                    }
                    break;
                    case 231:
                        // line 1086 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[-2+yyTop]));
                        support.checkExpression(((Node)yyVals[0+yyTop]));

                        boolean isLiteral = ((Node)yyVals[-2+yyTop]) instanceof FixnumNode && ((Node)yyVals[0+yyTop]) instanceof FixnumNode;
                        yyVal = new DotNode(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]), true, isLiteral);
                    }
                    break;
                    case 232:
                        // line 1093 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "+", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 233:
                        // line 1096 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "-", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 234:
                        // line 1099 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "*", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 235:
                        // line 1102 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "/", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 236:
                        // line 1105 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "%", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 237:
                        // line 1108 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 238:
                        // line 1111 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(support.getOperatorCallNode(((NumericNode)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), lexer.getPosition()), "-@");
                    }
                    break;
                    case 239:
                        // line 1114 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "+@");
                    }
                    break;
                    case 240:
                        // line 1117 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "-@");
                    }
                    break;
                    case 241:
                        // line 1120 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "|", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 242:
                        // line 1123 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "^", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 243:
                        // line 1126 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "&", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 244:
                        // line 1129 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<=>", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 245:
                        // line 1132 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 246:
                        // line 1135 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">=", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 247:
                        // line 1138 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 248:
                        // line 1141 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<=", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 249:
                        // line 1144 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "==", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 250:
                        // line 1147 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "===", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 251:
                        // line 1150 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "!=", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 252:
                        // line 1153 "RubyParser.y"
                    {
                        yyVal = support.getMatchNode(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                  /* ENEBO
                        $$ = match_op($1, $3);
                        if (nd_type($1) == NODE_LIT && TYPE($1->nd_lit) == T_REGEXP) {
                            $$ = reg_named_capture_assign($1->nd_lit, $$);
                        }
                  */
                    }
                    break;
                    case 253:
                        // line 1162 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "!~", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 254:
                        // line 1165 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[0+yyTop])), "!");
                    }
                    break;
                    case 255:
                        // line 1168 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "~");
                    }
                    break;
                    case 256:
                        // line 1171 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<<", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 257:
                        // line 1174 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">>", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 258:
                        // line 1177 "RubyParser.y"
                    {
                        yyVal = support.newAndNode(((Node)yyVals[-2+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 259:
                        // line 1180 "RubyParser.y"
                    {
                        yyVal = support.newOrNode(((Node)yyVals[-2+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 260:
                        // line 1183 "RubyParser.y"
                    {
                        yyVal = support.new_defined(((ISourcePosition)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 261:
                        // line 1186 "RubyParser.y"
                    {
                        yyVal = new IfNode(support.getPosition(((Node)yyVals[-5+yyTop])), support.getConditionNode(((Node)yyVals[-5+yyTop])), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 262:
                        // line 1189 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 263:
                        // line 1193 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[0+yyTop]));
                        yyVal = ((Node)yyVals[0+yyTop]) != null ? ((Node)yyVals[0+yyTop]) : NilImplicitNode.NIL;
                    }
                    break;
                    case 265:
                        // line 1199 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 266:
                        // line 1202 "RubyParser.y"
                    {
                        yyVal = support.arg_append(((Node)yyVals[-3+yyTop]), ((HashNode)yyVals[-1+yyTop]));
                    }
                    break;
                    case 267:
                        // line 1205 "RubyParser.y"
                    {
                        yyVal = support.newArrayNode(((HashNode)yyVals[-1+yyTop]).getPosition(), ((HashNode)yyVals[-1+yyTop]));
                    }
                    break;
                    case 268:
                        // line 1209 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                        if (yyVal != null) ((Node)yyVal).setPosition(((ISourcePosition)yyVals[-2+yyTop]));
                    }
                    break;
                    case 273:
                        // line 1218 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 274:
                        // line 1221 "RubyParser.y"
                    {
                        yyVal = support.arg_append(((Node)yyVals[-3+yyTop]), ((HashNode)yyVals[-1+yyTop]));
                    }
                    break;
                    case 275:
                        // line 1224 "RubyParser.y"
                    {
                        yyVal = support.newArrayNode(((HashNode)yyVals[-1+yyTop]).getPosition(), ((HashNode)yyVals[-1+yyTop]));
                    }
                    break;
                    case 276:
                        // line 1230 "RubyParser.y"
                    {
                        yyVal = support.newArrayNode(support.getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 277:
                        // line 1233 "RubyParser.y"
                    {
                        yyVal = support.arg_blk_pass(((Node)yyVals[-1+yyTop]), ((BlockPassNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 278:
                        // line 1236 "RubyParser.y"
                    {
                        yyVal = support.newArrayNode(((HashNode)yyVals[-1+yyTop]).getPosition(), ((HashNode)yyVals[-1+yyTop]));
                        yyVal = support.arg_blk_pass((Node)yyVal, ((BlockPassNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 279:
                        // line 1240 "RubyParser.y"
                    {
                        yyVal = support.arg_append(((Node)yyVals[-3+yyTop]), ((HashNode)yyVals[-1+yyTop]));
                        yyVal = support.arg_blk_pass((Node)yyVal, ((BlockPassNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 280:
                        // line 1244 "RubyParser.y"
                    {
                    }
                    break;
                    case 281:
                        // line 1247 "RubyParser.y"
                    {
                        yyVal = Long.valueOf(lexer.getCmdArgumentState().begin());
                    }
                    break;
                    case 282:
                        // line 1249 "RubyParser.y"
                    {
                        lexer.getCmdArgumentState().reset(((Long)yyVals[-1+yyTop]).longValue());
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 283:
                        // line 1254 "RubyParser.y"
                    {
                        yyVal = new BlockPassNode(support.getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 284:
                        // line 1258 "RubyParser.y"
                    {
                        yyVal = ((BlockPassNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 286:
                        // line 1264 "RubyParser.y"
                    { /* ArrayNode*/
                        ISourcePosition pos = ((Node)yyVals[0+yyTop]) == null ? lexer.getPosition() : ((Node)yyVals[0+yyTop]).getPosition();
                        yyVal = support.newArrayNode(pos, ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 287:
                        // line 1268 "RubyParser.y"
                    { /* SplatNode*/
                        yyVal = support.newSplatNode(support.getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 288:
                        // line 1271 "RubyParser.y"
                    { /* ArgsCatNode, SplatNode, ArrayNode*/
                        Node node = support.splat_array(((Node)yyVals[-2+yyTop]));

                        if (node != null) {
                            yyVal = support.list_append(node, ((Node)yyVals[0+yyTop]));
                        } else {
                            yyVal = support.arg_append(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                        }
                    }
                    break;
                    case 289:
                        // line 1280 "RubyParser.y"
                    { /* ArgsCatNode, SplatNode, ArrayNode*/
                        Node node = null;

                    /* FIXME: lose syntactical elements here (and others like this)*/
                        if (((Node)yyVals[0+yyTop]) instanceof ArrayNode &&
                                (node = support.splat_array(((Node)yyVals[-3+yyTop]))) != null) {
                            yyVal = support.list_concat(node, ((Node)yyVals[0+yyTop]));
                        } else {
                            yyVal = support.arg_concat(support.getPosition(((Node)yyVals[-3+yyTop])), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                        }
                    }
                    break;
                    case 290:
                        // line 1292 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 291:
                        // line 1295 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 292:
                        // line 1300 "RubyParser.y"
                    {
                        Node node = support.splat_array(((Node)yyVals[-2+yyTop]));

                        if (node != null) {
                            yyVal = support.list_append(node, ((Node)yyVals[0+yyTop]));
                        } else {
                            yyVal = support.arg_append(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                        }
                    }
                    break;
                    case 293:
                        // line 1309 "RubyParser.y"
                    {
                        Node node = null;

                        if (((Node)yyVals[0+yyTop]) instanceof ArrayNode &&
                                (node = support.splat_array(((Node)yyVals[-3+yyTop]))) != null) {
                            yyVal = support.list_concat(node, ((Node)yyVals[0+yyTop]));
                        } else {
                            yyVal = support.arg_concat(((Node)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                        }
                    }
                    break;
                    case 294:
                        // line 1319 "RubyParser.y"
                    {
                        yyVal = support.newSplatNode(support.getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 301:
                        // line 1329 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[0+yyTop]); /* FIXME: Why complaining without $$ = $1;*/
                    }
                    break;
                    case 302:
                        // line 1332 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[0+yyTop]); /* FIXME: Why complaining without $$ = $1;*/
                    }
                    break;
                    case 305:
                        // line 1337 "RubyParser.y"
                    {
                        yyVal = support.new_fcall(((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 306:
                        // line 1340 "RubyParser.y"
                    {
                        yyVal = new BeginNode(((ISourcePosition)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 307:
                        // line 1343 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_ENDARG);
                    }
                    break;
                    case 308:
                        // line 1345 "RubyParser.y"
                    {
                        yyVal = null; /*FIXME: Should be implicit nil?*/
                    }
                    break;
                    case 309:
                        // line 1348 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_ENDARG);
                    }
                    break;
                    case 310:
                        // line 1350 "RubyParser.y"
                    {
                        if (Options.PARSER_WARN_GROUPED_EXPRESSIONS.load()) {
                            support.warning(ID.GROUPED_EXPRESSION, ((ISourcePosition)yyVals[-3+yyTop]), "(...) interpreted as grouped expression");
                        }
                        yyVal = ((Node)yyVals[-2+yyTop]);
                    }
                    break;
                    case 311:
                        // line 1356 "RubyParser.y"
                    {
                        if (((Node)yyVals[-1+yyTop]) != null) {
                        /* compstmt position includes both parens around it*/
                            ((ISourcePositionHolder) ((Node)yyVals[-1+yyTop])).setPosition(((ISourcePosition)yyVals[-2+yyTop]));
                            yyVal = ((Node)yyVals[-1+yyTop]);
                        } else {
                            yyVal = new NilNode(((ISourcePosition)yyVals[-2+yyTop]));
                        }
                    }
                    break;
                    case 312:
                        // line 1365 "RubyParser.y"
                    {
                        yyVal = support.new_colon2(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 313:
                        // line 1368 "RubyParser.y"
                    {
                        yyVal = support.new_colon3(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 314:
                        // line 1371 "RubyParser.y"
                    {
                        ISourcePosition position = support.getPosition(((Node)yyVals[-1+yyTop]));
                        if (((Node)yyVals[-1+yyTop]) == null) {
                            yyVal = new ZArrayNode(position); /* zero length array */
                        } else {
                            yyVal = ((Node)yyVals[-1+yyTop]);
                        }
                    }
                    break;
                    case 315:
                        // line 1379 "RubyParser.y"
                    {
                        yyVal = ((HashNode)yyVals[-1+yyTop]);
                    }
                    break;
                    case 316:
                        // line 1382 "RubyParser.y"
                    {
                        yyVal = new ReturnNode(((ISourcePosition)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 317:
                        // line 1385 "RubyParser.y"
                    {
                        yyVal = support.new_yield(((ISourcePosition)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 318:
                        // line 1388 "RubyParser.y"
                    {
                        yyVal = new ZYieldNode(((ISourcePosition)yyVals[-2+yyTop]));
                    }
                    break;
                    case 319:
                        // line 1391 "RubyParser.y"
                    {
                        yyVal = new ZYieldNode(((ISourcePosition)yyVals[0+yyTop]));
                    }
                    break;
                    case 320:
                        // line 1394 "RubyParser.y"
                    {
                        yyVal = support.new_defined(((ISourcePosition)yyVals[-4+yyTop]), ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 321:
                        // line 1397 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[-1+yyTop])), "!");
                    }
                    break;
                    case 322:
                        // line 1400 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(NilImplicitNode.NIL, "!");
                    }
                    break;
                    case 323:
                        // line 1403 "RubyParser.y"
                    {
                        support.frobnicate_fcall_args(((FCallNode)yyVals[-1+yyTop]), null, ((IterNode)yyVals[0+yyTop]));
                        yyVal = ((FCallNode)yyVals[-1+yyTop]);
                    }
                    break;
                    case 325:
                        // line 1408 "RubyParser.y"
                    {
                        if (((Node)yyVals[-1+yyTop]) != null &&
                                ((BlockAcceptingNode)yyVals[-1+yyTop]).getIterNode() instanceof BlockPassNode) {
                            throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, ((Node)yyVals[-1+yyTop]).getPosition(), lexer.getCurrentLine(), "Both block arg and actual block given.");
                        }
                        yyVal = ((BlockAcceptingNode)yyVals[-1+yyTop]).setIterNode(((IterNode)yyVals[0+yyTop]));
                        ((Node)yyVal).setPosition(((Node)yyVals[-1+yyTop]).getPosition());
                    }
                    break;
                    case 326:
                        // line 1416 "RubyParser.y"
                    {
                        yyVal = ((LambdaNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 327:
                        // line 1419 "RubyParser.y"
                    {
                        yyVal = new IfNode(((ISourcePosition)yyVals[-5+yyTop]), support.getConditionNode(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 328:
                        // line 1422 "RubyParser.y"
                    {
                        yyVal = new IfNode(((ISourcePosition)yyVals[-5+yyTop]), support.getConditionNode(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[-2+yyTop]));
                    }
                    break;
                    case 329:
                        // line 1425 "RubyParser.y"
                    {
                        lexer.getConditionState().begin();
                    }
                    break;
                    case 330:
                        // line 1427 "RubyParser.y"
                    {
                        lexer.getConditionState().end();
                    }
                    break;
                    case 331:
                        // line 1429 "RubyParser.y"
                    {
                        Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
                        yyVal = new WhileNode(((ISourcePosition)yyVals[-6+yyTop]), support.getConditionNode(((Node)yyVals[-4+yyTop])), body);
                    }
                    break;
                    case 332:
                        // line 1433 "RubyParser.y"
                    {
                        lexer.getConditionState().begin();
                    }
                    break;
                    case 333:
                        // line 1435 "RubyParser.y"
                    {
                        lexer.getConditionState().end();
                    }
                    break;
                    case 334:
                        // line 1437 "RubyParser.y"
                    {
                        Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
                        yyVal = new UntilNode(((ISourcePosition)yyVals[-6+yyTop]), support.getConditionNode(((Node)yyVals[-4+yyTop])), body);
                    }
                    break;
                    case 335:
                        // line 1441 "RubyParser.y"
                    {
                        yyVal = support.newCaseNode(((ISourcePosition)yyVals[-4+yyTop]), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 336:
                        // line 1444 "RubyParser.y"
                    {
                        yyVal = support.newCaseNode(((ISourcePosition)yyVals[-3+yyTop]), null, ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 337:
                        // line 1447 "RubyParser.y"
                    {
                        lexer.getConditionState().begin();
                    }
                    break;
                    case 338:
                        // line 1449 "RubyParser.y"
                    {
                        lexer.getConditionState().end();
                    }
                    break;
                    case 339:
                        // line 1451 "RubyParser.y"
                    {
                      /* ENEBO: Lots of optz in 1.9 parser here*/
                        yyVal = new ForNode(((ISourcePosition)yyVals[-8+yyTop]), ((Node)yyVals[-7+yyTop]), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[-4+yyTop]), support.getCurrentScope());
                    }
                    break;
                    case 340:
                        // line 1455 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) {
                            support.yyerror("class definition in method body");
                        }
                        support.pushLocalScope();
                    }
                    break;
                    case 341:
                        // line 1460 "RubyParser.y"
                    {
                        Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);

                        yyVal = new ClassNode(((ISourcePosition)yyVals[-5+yyTop]), ((Colon3Node)yyVals[-4+yyTop]), support.getCurrentScope(), body, ((Node)yyVals[-3+yyTop]));
                        support.popCurrentScope();
                    }
                    break;
                    case 342:
                        // line 1466 "RubyParser.y"
                    {
                        yyVal = Boolean.valueOf(support.isInDef());
                        support.setInDef(false);
                    }
                    break;
                    case 343:
                        // line 1469 "RubyParser.y"
                    {
                        yyVal = Integer.valueOf(support.getInSingle());
                        support.setInSingle(0);
                        support.pushLocalScope();
                    }
                    break;
                    case 344:
                        // line 1473 "RubyParser.y"
                    {
                        Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);

                        yyVal = new SClassNode(((ISourcePosition)yyVals[-7+yyTop]), ((Node)yyVals[-5+yyTop]), support.getCurrentScope(), body);
                        support.popCurrentScope();
                        support.setInDef(((Boolean)yyVals[-4+yyTop]).booleanValue());
                        support.setInSingle(((Integer)yyVals[-2+yyTop]).intValue());
                    }
                    break;
                    case 345:
                        // line 1481 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) {
                            support.yyerror("module definition in method body");
                        }
                        support.pushLocalScope();
                    }
                    break;
                    case 346:
                        // line 1486 "RubyParser.y"
                    {
                        Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);

                        yyVal = new ModuleNode(((ISourcePosition)yyVals[-4+yyTop]), ((Colon3Node)yyVals[-3+yyTop]), support.getCurrentScope(), body);
                        support.popCurrentScope();
                    }
                    break;
                    case 347:
                        // line 1492 "RubyParser.y"
                    {
                        support.setInDef(true);
                        support.pushLocalScope();
                    }
                    break;
                    case 348:
                        // line 1495 "RubyParser.y"
                    {
                        Node body = ((Node)yyVals[-1+yyTop]);
                        if (body == null) body = NilImplicitNode.NIL;

                        yyVal = new DefnNode(((ISourcePosition)yyVals[-5+yyTop]), new ArgumentNode(((ISourcePosition)yyVals[-5+yyTop]), ((String)yyVals[-4+yyTop])), (ArgsNode) yyVals[-2+yyTop], support.getCurrentScope(), body);
                        support.popCurrentScope();
                        support.setInDef(false);
                    }
                    break;
                    case 349:
                        // line 1503 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_FNAME);
                    }
                    break;
                    case 350:
                        // line 1505 "RubyParser.y"
                    {
                        support.setInSingle(support.getInSingle() + 1);
                        support.pushLocalScope();
                        lexer.setState(LexState.EXPR_ENDFN); /* force for args */
                    }
                    break;
                    case 351:
                        // line 1509 "RubyParser.y"
                    {
                        Node body = ((Node)yyVals[-1+yyTop]);
                        if (body == null) body = NilImplicitNode.NIL;

                        yyVal = new DefsNode(((ISourcePosition)yyVals[-8+yyTop]), ((Node)yyVals[-7+yyTop]), new ArgumentNode(((ISourcePosition)yyVals[-8+yyTop]), ((String)yyVals[-4+yyTop])), (ArgsNode) yyVals[-2+yyTop], support.getCurrentScope(), body);
                        support.popCurrentScope();
                        support.setInSingle(support.getInSingle() - 1);
                    }
                    break;
                    case 352:
                        // line 1517 "RubyParser.y"
                    {
                        yyVal = new BreakNode(((ISourcePosition)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 353:
                        // line 1520 "RubyParser.y"
                    {
                        yyVal = new NextNode(((ISourcePosition)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 354:
                        // line 1523 "RubyParser.y"
                    {
                        yyVal = new RedoNode(((ISourcePosition)yyVals[0+yyTop]));
                    }
                    break;
                    case 355:
                        // line 1526 "RubyParser.y"
                    {
                        yyVal = new RetryNode(((ISourcePosition)yyVals[0+yyTop]));
                    }
                    break;
                    case 356:
                        // line 1529 "RubyParser.y"
                    {
                        yyVal = support.signal_assign(((ISourcePosition)yyVals[-2+yyTop]),((ISourcePosition)yyVals[-2+yyTop]));
                    }
                    break;
                    case 357:
                        // line 1540 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[0+yyTop]));
                        yyVal = ((Node)yyVals[0+yyTop]);
                        if (yyVal == null) yyVal = NilImplicitNode.NIL;
                    }
                    break;
                    case 364:
                        // line 1554 "RubyParser.y"
                    {
                        yyVal = new IfNode(((ISourcePosition)yyVals[-4+yyTop]), support.getConditionNode(((Node)yyVals[-3+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 366:
                        // line 1559 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 368:
                        // line 1564 "RubyParser.y"
                    {
                    }
                    break;
                    case 369:
                        // line 1567 "RubyParser.y"
                    {
                        yyVal = support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 370:
                        // line 1570 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 371:
                        // line 1575 "RubyParser.y"
                    {
                        yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 372:
                        // line 1578 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 373:
                        // line 1582 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[0+yyTop]).getPosition(), ((ListNode)yyVals[0+yyTop]), null, null);
                    }
                    break;
                    case 374:
                        // line 1585 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), null), null);
                    }
                    break;
                    case 375:
                        // line 1588 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), support.assignableLabelOrIdentifier(((String)yyVals[-2+yyTop]), null), ((ListNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 376:
                        // line 1591 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-2+yyTop]).getPosition(), ((ListNode)yyVals[-2+yyTop]), new StarNode(lexer.getPosition()), null);
                    }
                    break;
                    case 377:
                        // line 1594 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-4+yyTop]).getPosition(), ((ListNode)yyVals[-4+yyTop]), new StarNode(lexer.getPosition()), ((ListNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 378:
                        // line 1597 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(lexer.getPosition(), null, support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), null), null);
                    }
                    break;
                    case 379:
                        // line 1600 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(lexer.getPosition(), null, support.assignableLabelOrIdentifier(((String)yyVals[-2+yyTop]), null), ((ListNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 380:
                        // line 1603 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(lexer.getPosition(), null, new StarNode(lexer.getPosition()), null);
                    }
                    break;
                    case 381:
                        // line 1606 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(support.getPosition(((ListNode)yyVals[0+yyTop])), null, null, ((ListNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 382:
                        // line 1610 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 383:
                        // line 1613 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 384:
                        // line 1616 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(lexer.getPosition(), null, ((String)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 385:
                        // line 1619 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(((BlockArgNode)yyVals[0+yyTop]).getPosition(), null, null, ((BlockArgNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 386:
                        // line 1623 "RubyParser.y"
                    {
                        yyVal = ((ArgsTailHolder)yyVals[0+yyTop]);
                    }
                    break;
                    case 387:
                        // line 1626 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(lexer.getPosition(), null, null, null);
                    }
                    break;
                    case 388:
                        // line 1631 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 389:
                        // line 1634 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-7+yyTop]).getPosition(), ((ListNode)yyVals[-7+yyTop]), ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 390:
                        // line 1637 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 391:
                        // line 1640 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 392:
                        // line 1643 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), null, ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 393:
                        // line 1646 "RubyParser.y"
                    {
                        RestArgNode rest = new UnnamedRestArgNode(((ListNode)yyVals[-1+yyTop]).getPosition(), null, support.getCurrentScope().addVariable("*"));
                        yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, rest, null, (ArgsTailHolder) null);
                    }
                    break;
                    case 394:
                        // line 1650 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 395:
                        // line 1653 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 396:
                        // line 1656 "RubyParser.y"
                    {
                        yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-3+yyTop])), null, ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 397:
                        // line 1659 "RubyParser.y"
                    {
                        yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-5+yyTop])), null, ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 398:
                        // line 1662 "RubyParser.y"
                    {
                        yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-1+yyTop])), null, ((ListNode)yyVals[-1+yyTop]), null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 399:
                        // line 1665 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 400:
                        // line 1668 "RubyParser.y"
                    {
                        yyVal = support.new_args(((RestArgNode)yyVals[-1+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 401:
                        // line 1671 "RubyParser.y"
                    {
                        yyVal = support.new_args(((RestArgNode)yyVals[-3+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 402:
                        // line 1674 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ArgsTailHolder)yyVals[0+yyTop]).getPosition(), null, null, null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 403:
                        // line 1678 "RubyParser.y"
                    {
    /* was $$ = null;*/
                        yyVal = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
                    }
                    break;
                    case 404:
                        // line 1682 "RubyParser.y"
                    {
                        lexer.commandStart = true;
                        yyVal = ((ArgsNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 405:
                        // line 1687 "RubyParser.y"
                    {
                        yyVal = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
                    }
                    break;
                    case 406:
                        // line 1690 "RubyParser.y"
                    {
                        yyVal = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
                    }
                    break;
                    case 407:
                        // line 1693 "RubyParser.y"
                    {
                        yyVal = ((ArgsNode)yyVals[-2+yyTop]);
                    }
                    break;
                    case 408:
                        // line 1698 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 409:
                        // line 1701 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 410:
                        // line 1706 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 411:
                        // line 1709 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 412:
                        // line 1713 "RubyParser.y"
                    {
                        support.new_bv(((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 413:
                        // line 1716 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 414:
                        // line 1720 "RubyParser.y"
                    {
                        support.pushBlockScope();
                        yyVal = lexer.getLeftParenBegin();
                        lexer.setLeftParenBegin(lexer.incrementParenNest());
                    }
                    break;
                    case 415:
                        // line 1724 "RubyParser.y"
                    {
                        yyVal = new LambdaNode(((ArgsNode)yyVals[-1+yyTop]).getPosition(), ((ArgsNode)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), support.getCurrentScope());
                        support.popCurrentScope();
                        lexer.setLeftParenBegin(((Integer)yyVals[-2+yyTop]));
                    }
                    break;
                    case 416:
                        // line 1730 "RubyParser.y"
                    {
                        yyVal = ((ArgsNode)yyVals[-2+yyTop]);
                    }
                    break;
                    case 417:
                        // line 1733 "RubyParser.y"
                    {
                        yyVal = ((ArgsNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 418:
                        // line 1737 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 419:
                        // line 1740 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 420:
                        // line 1744 "RubyParser.y"
                    {
                        support.pushBlockScope();
                    }
                    break;
                    case 421:
                        // line 1746 "RubyParser.y"
                    {
                        yyVal = new IterNode(((ISourcePosition)yyVals[-4+yyTop]), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                        support.popCurrentScope();
                    }
                    break;
                    case 422:
                        // line 1755 "RubyParser.y"
                    {
                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
                        if (((Node)yyVals[-1+yyTop]) instanceof YieldNode) {
                            throw new SyntaxException(PID.BLOCK_GIVEN_TO_YIELD, ((Node)yyVals[-1+yyTop]).getPosition(), lexer.getCurrentLine(), "block given to yield");
                        }
                        if (((Node)yyVals[-1+yyTop]) instanceof BlockAcceptingNode && ((BlockAcceptingNode)yyVals[-1+yyTop]).getIterNode() instanceof BlockPassNode) {
                            throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, ((Node)yyVals[-1+yyTop]).getPosition(), lexer.getCurrentLine(), "Both block arg and actual block given.");
                        }
                        if (((Node)yyVals[-1+yyTop]) instanceof NonLocalControlFlowNode) {
                            ((BlockAcceptingNode) ((NonLocalControlFlowNode)yyVals[-1+yyTop]).getValueNode()).setIterNode(((IterNode)yyVals[0+yyTop]));
                        } else {
                            ((BlockAcceptingNode)yyVals[-1+yyTop]).setIterNode(((IterNode)yyVals[0+yyTop]));
                        }
                        yyVal = ((Node)yyVals[-1+yyTop]);
                        ((Node)yyVal).setPosition(((Node)yyVals[-1+yyTop]).getPosition());
                    }
                    break;
                    case 423:
                        // line 1771 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 424:
                        // line 1774 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((String)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 425:
                        // line 1777 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((String)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 426:
                        // line 1782 "RubyParser.y"
                    {
                        support.frobnicate_fcall_args(((FCallNode)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                        yyVal = ((FCallNode)yyVals[-1+yyTop]);
                    }
                    break;
                    case 427:
                        // line 1786 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 428:
                        // line 1789 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 429:
                        // line 1792 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]), null, null);
                    }
                    break;
                    case 430:
                        // line 1795 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-2+yyTop]), "call", ((Node)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 431:
                        // line 1798 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-2+yyTop]), "call", ((Node)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 432:
                        // line 1801 "RubyParser.y"
                    {
                        yyVal = support.new_super(((ISourcePosition)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 433:
                        // line 1804 "RubyParser.y"
                    {
                        yyVal = new ZSuperNode(((ISourcePosition)yyVals[0+yyTop]));
                    }
                    break;
                    case 434:
                        // line 1807 "RubyParser.y"
                    {
                        if (((Node)yyVals[-3+yyTop]) instanceof SelfNode) {
                            yyVal = support.new_fcall("[]");
                            support.frobnicate_fcall_args(((FCallNode)yyVal), ((Node)yyVals[-1+yyTop]), null);
                        } else {
                            yyVal = support.new_call(((Node)yyVals[-3+yyTop]), "[]", ((Node)yyVals[-1+yyTop]), null);
                        }
                    }
                    break;
                    case 435:
                        // line 1816 "RubyParser.y"
                    {
                        support.pushBlockScope();
                    }
                    break;
                    case 436:
                        // line 1818 "RubyParser.y"
                    {
                        yyVal = new IterNode(((ISourcePosition)yyVals[-4+yyTop]), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                        support.popCurrentScope();
                    }
                    break;
                    case 437:
                        // line 1822 "RubyParser.y"
                    {
                        support.pushBlockScope();
                    }
                    break;
                    case 438:
                        // line 1824 "RubyParser.y"
                    {
                        yyVal = new IterNode(((ISourcePosition)yyVals[-4+yyTop]), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                        support.popCurrentScope();
                    }
                    break;
                    case 439:
                        // line 1829 "RubyParser.y"
                    {
                        yyVal = support.newWhenNode(((ISourcePosition)yyVals[-4+yyTop]), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 442:
                        // line 1835 "RubyParser.y"
                    {
                        Node node;
                        if (((Node)yyVals[-3+yyTop]) != null) {
                            node = support.appendToBlock(support.node_assign(((Node)yyVals[-3+yyTop]), new GlobalVarNode(((ISourcePosition)yyVals[-5+yyTop]), "$!")), ((Node)yyVals[-1+yyTop]));
                            if (((Node)yyVals[-1+yyTop]) != null) {
                                node.setPosition(((ISourcePosition)yyVals[-5+yyTop]));
                            }
                        } else {
                            node = ((Node)yyVals[-1+yyTop]);
                        }
                        Node body = node == null ? NilImplicitNode.NIL : node;
                        yyVal = new RescueBodyNode(((ISourcePosition)yyVals[-5+yyTop]), ((Node)yyVals[-4+yyTop]), body, ((RescueBodyNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 443:
                        // line 1848 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 444:
                        // line 1852 "RubyParser.y"
                    {
                        yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 445:
                        // line 1855 "RubyParser.y"
                    {
                        yyVal = support.splat_array(((Node)yyVals[0+yyTop]));
                        if (yyVal == null) yyVal = ((Node)yyVals[0+yyTop]); /* ArgsCat or ArgsPush*/
                    }
                    break;
                    case 447:
                        // line 1861 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 449:
                        // line 1866 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 451:
                        // line 1871 "RubyParser.y"
                    {
                        yyVal = ((NumericNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 452:
                        // line 1874 "RubyParser.y"
                    {
                        yyVal = new SymbolNode(lexer.getPosition(), new ByteList(((String)yyVals[0+yyTop]).getBytes(), lexer.getEncoding()));
                    }
                    break;
                    case 454:
                        // line 1879 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]) instanceof EvStrNode ? new DStrNode(((Node)yyVals[0+yyTop]).getPosition(), lexer.getEncoding()).add(((Node)yyVals[0+yyTop])) : ((Node)yyVals[0+yyTop]);
                    /*
                    NODE *node = $1;
                    if (!node) {
                        node = NEW_STR(STR_NEW0());
                    } else {
                        node = evstr2dstr(node);
                    }
                    $$ = node;
                    */
                    }
                    break;
                    case 455:
                        // line 1893 "RubyParser.y"
                    {
                        yyVal = ((StrNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 456:
                        // line 1896 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 457:
                        // line 1899 "RubyParser.y"
                    {
                        yyVal = support.literal_concat(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 458:
                        // line 1903 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 459:
                        // line 1907 "RubyParser.y"
                    {
                        ISourcePosition position = support.getPosition(((Node)yyVals[-1+yyTop]));

                        if (((Node)yyVals[-1+yyTop]) == null) {
                            yyVal = new XStrNode(position, null);
                        } else if (((Node)yyVals[-1+yyTop]) instanceof StrNode) {
                            yyVal = new XStrNode(position, (ByteList) ((StrNode)yyVals[-1+yyTop]).getValue().clone());
                        } else if (((Node)yyVals[-1+yyTop]) instanceof DStrNode) {
                            yyVal = new DXStrNode(position, ((DStrNode)yyVals[-1+yyTop]));

                            ((Node)yyVal).setPosition(position);
                        } else {
                            yyVal = new DXStrNode(position).add(((Node)yyVals[-1+yyTop]));
                        }
                    }
                    break;
                    case 460:
                        // line 1923 "RubyParser.y"
                    {
                        yyVal = support.newRegexpNode(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), (RegexpNode) ((RegexpNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 461:
                        // line 1927 "RubyParser.y"
                    {
                        yyVal = new ZArrayNode(lexer.getPosition());
                    }
                    break;
                    case 462:
                        // line 1930 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-1+yyTop]);
                    }
                    break;
                    case 463:
                        // line 1934 "RubyParser.y"
                    {
                        yyVal = new ArrayNode(lexer.getPosition());
                    }
                    break;
                    case 464:
                        // line 1937 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]) instanceof EvStrNode ? new DStrNode(((ListNode)yyVals[-2+yyTop]).getPosition(), lexer.getEncoding()).add(((Node)yyVals[-1+yyTop])) : ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 465:
                        // line 1941 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 466:
                        // line 1944 "RubyParser.y"
                    {
                        yyVal = support.literal_concat(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 467:
                        // line 1948 "RubyParser.y"
                    {
                        yyVal = new ArrayNode(lexer.getPosition());
                    }
                    break;
                    case 468:
                        // line 1951 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-1+yyTop]);
                    }
                    break;
                    case 469:
                        // line 1955 "RubyParser.y"
                    {
                        yyVal = new ArrayNode(lexer.getPosition());
                    }
                    break;
                    case 470:
                        // line 1958 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]) instanceof EvStrNode ? new DSymbolNode(((ListNode)yyVals[-2+yyTop]).getPosition()).add(((Node)yyVals[-1+yyTop])) : support.asSymbol(((ListNode)yyVals[-2+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop])));
                    }
                    break;
                    case 471:
                        // line 1962 "RubyParser.y"
                    {
                        yyVal = new ZArrayNode(lexer.getPosition());
                    }
                    break;
                    case 472:
                        // line 1965 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-1+yyTop]);
                    }
                    break;
                    case 473:
                        // line 1969 "RubyParser.y"
                    {
                        yyVal = new ZArrayNode(lexer.getPosition());
                    }
                    break;
                    case 474:
                        // line 1972 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-1+yyTop]);
                    }
                    break;
                    case 475:
                        // line 1977 "RubyParser.y"
                    {
                        yyVal = new ArrayNode(lexer.getPosition());
                    }
                    break;
                    case 476:
                        // line 1980 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 477:
                        // line 1984 "RubyParser.y"
                    {
                        yyVal = new ArrayNode(lexer.getPosition());
                    }
                    break;
                    case 478:
                        // line 1987 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-2+yyTop]).add(support.asSymbol(((ListNode)yyVals[-2+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop])));
                    }
                    break;
                    case 479:
                        // line 1991 "RubyParser.y"
                    {
                        ByteList aChar = ByteList.create("");
                        aChar.setEncoding(lexer.getEncoding());
                        yyVal = lexer.createStrNode(lexer.getPosition(), aChar, 0);
                    }
                    break;
                    case 480:
                        // line 1996 "RubyParser.y"
                    {
                        yyVal = support.literal_concat(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 481:
                        // line 2000 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 482:
                        // line 2003 "RubyParser.y"
                    {
                        yyVal = support.literal_concat(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 483:
                        // line 2007 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 484:
                        // line 2010 "RubyParser.y"
                    {
    /* FIXME: mri is different here.*/
                        yyVal = support.literal_concat(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 485:
                        // line 2015 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 486:
                        // line 2018 "RubyParser.y"
                    {
                        yyVal = lexer.getStrTerm();
                        lexer.setStrTerm(null);
                        lexer.setState(LexState.EXPR_BEG);
                    }
                    break;
                    case 487:
                        // line 2022 "RubyParser.y"
                    {
                        lexer.setStrTerm(((StrTerm)yyVals[-1+yyTop]));
                        yyVal = new EvStrNode(support.getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 488:
                        // line 2026 "RubyParser.y"
                    {
                        yyVal = lexer.getStrTerm();
                        lexer.setStrTerm(null);
                        lexer.getConditionState().stop();
                        lexer.getCmdArgumentState().stop();
                    }
                    break;
                    case 489:
                        // line 2031 "RubyParser.y"
                    {
                        yyVal = lexer.getState();
                        lexer.setState(LexState.EXPR_BEG);
                    }
                    break;
                    case 490:
                        // line 2034 "RubyParser.y"
                    {
                        lexer.getConditionState().restart();
                        lexer.getCmdArgumentState().restart();
                        lexer.setStrTerm(((StrTerm)yyVals[-3+yyTop]));
                        lexer.setState(((LexState)yyVals[-2+yyTop]));

                        yyVal = support.newEvStrNode(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 491:
                        // line 2043 "RubyParser.y"
                    {
                        yyVal = new GlobalVarNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 492:
                        // line 2046 "RubyParser.y"
                    {
                        yyVal = new InstVarNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 493:
                        // line 2049 "RubyParser.y"
                    {
                        yyVal = new ClassVarNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 495:
                        // line 2055 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_END);
                        yyVal = ((String)yyVals[0+yyTop]);
                    }
                    break;
                    case 500:
                        // line 2063 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_END);

                     /* DStrNode: :"some text #{some expression}"*/
                     /* StrNode: :"some text"*/
                     /* EvStrNode :"#{some expression}"*/
                     /* Ruby 1.9 allows empty strings as symbols*/
                        if (((Node)yyVals[-1+yyTop]) == null) {
                            yyVal = new SymbolNode(lexer.getPosition(), new ByteList(new byte[0], lexer.getEncoding()));
                        } else if (((Node)yyVals[-1+yyTop]) instanceof DStrNode) {
                            yyVal = new DSymbolNode(((Node)yyVals[-1+yyTop]).getPosition(), ((DStrNode)yyVals[-1+yyTop]));
                        } else if (((Node)yyVals[-1+yyTop]) instanceof StrNode) {
                            yyVal = new SymbolNode(((Node)yyVals[-1+yyTop]).getPosition(), ((StrNode)yyVals[-1+yyTop]).getValue());
                        } else {
                            yyVal = new DSymbolNode(((Node)yyVals[-1+yyTop]).getPosition());
                            ((DSymbolNode)yyVal).add(((Node)yyVals[-1+yyTop]));
                        }
                    }
                    break;
                    case 501:
                        // line 2082 "RubyParser.y"
                    {
                        yyVal = ((NumericNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 502:
                        // line 2085 "RubyParser.y"
                    {
                        yyVal = support.negateNumeric(((NumericNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 503:
                        // line 2089 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 504:
                        // line 2092 "RubyParser.y"
                    {
                        yyVal = ((FloatNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 505:
                        // line 2095 "RubyParser.y"
                    {
                        yyVal = ((RationalNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 506:
                        // line 2098 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 507:
                        // line 2103 "RubyParser.y"
                    {
                        yyVal = support.declareIdentifier(((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 508:
                        // line 2106 "RubyParser.y"
                    {
                        yyVal = new InstVarNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 509:
                        // line 2109 "RubyParser.y"
                    {
                        yyVal = new GlobalVarNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 510:
                        // line 2112 "RubyParser.y"
                    {
                        yyVal = new ConstNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 511:
                        // line 2115 "RubyParser.y"
                    {
                        yyVal = new ClassVarNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 512:
                        // line 2118 "RubyParser.y"
                    {
                        yyVal = new NilNode(lexer.getPosition());
                    }
                    break;
                    case 513:
                        // line 2121 "RubyParser.y"
                    {
                        yyVal = new SelfNode(lexer.getPosition());
                    }
                    break;
                    case 514:
                        // line 2124 "RubyParser.y"
                    {
                        yyVal = new TrueNode(lexer.getPosition());
                    }
                    break;
                    case 515:
                        // line 2127 "RubyParser.y"
                    {
                        yyVal = new FalseNode(lexer.getPosition());
                    }
                    break;
                    case 516:
                        // line 2130 "RubyParser.y"
                    {
                        yyVal = new FileNode(lexer.getPosition(), new ByteList(lexer.getPosition().getFile().getBytes(),
                                support.getConfiguration().getRuntime().getEncodingService().getLocaleEncoding()));
                    }
                    break;
                    case 517:
                        // line 2134 "RubyParser.y"
                    {
                        yyVal = new FixnumNode(lexer.getPosition(), lexer.tokline.getLine()+1);
                    }
                    break;
                    case 518:
                        // line 2137 "RubyParser.y"
                    {
                        yyVal = new EncodingNode(lexer.getPosition(), lexer.getEncoding());
                    }
                    break;
                    case 519:
                        // line 2142 "RubyParser.y"
                    {
                        yyVal = support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 520:
                        // line 2145 "RubyParser.y"
                    {
                        yyVal = new InstAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 521:
                        // line 2148 "RubyParser.y"
                    {
                        yyVal = new GlobalAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 522:
                        // line 2151 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) support.compile_error("dynamic constant assignment");

                        yyVal = new ConstDeclNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), null, NilImplicitNode.NIL);
                    }
                    break;
                    case 523:
                        // line 2156 "RubyParser.y"
                    {
                        yyVal = new ClassVarAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 524:
                        // line 2159 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to nil");
                        yyVal = null;
                    }
                    break;
                    case 525:
                        // line 2163 "RubyParser.y"
                    {
                        support.compile_error("Can't change the value of self");
                        yyVal = null;
                    }
                    break;
                    case 526:
                        // line 2167 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to true");
                        yyVal = null;
                    }
                    break;
                    case 527:
                        // line 2171 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to false");
                        yyVal = null;
                    }
                    break;
                    case 528:
                        // line 2175 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to __FILE__");
                        yyVal = null;
                    }
                    break;
                    case 529:
                        // line 2179 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to __LINE__");
                        yyVal = null;
                    }
                    break;
                    case 530:
                        // line 2183 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to __ENCODING__");
                        yyVal = null;
                    }
                    break;
                    case 531:
                        // line 2189 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 532:
                        // line 2192 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 533:
                        // line 2196 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 534:
                        // line 2199 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_BEG);
                    }
                    break;
                    case 535:
                        // line 2201 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 536:
                        // line 2204 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 537:
                        // line 2210 "RubyParser.y"
                    {
                        yyVal = ((ArgsNode)yyVals[-1+yyTop]);
                        lexer.setState(LexState.EXPR_BEG);
                        lexer.commandStart = true;
                    }
                    break;
                    case 538:
                        // line 2215 "RubyParser.y"
                    {
                        yyVal = lexer.inKwarg;
                        lexer.inKwarg = true;
                    }
                    break;
                    case 539:
                        // line 2218 "RubyParser.y"
                    {
                        lexer.inKwarg = ((Boolean)yyVals[-2+yyTop]);
                        yyVal = ((ArgsNode)yyVals[-1+yyTop]);
                        lexer.setState(LexState.EXPR_BEG);
                        lexer.commandStart = true;
                    }
                    break;
                    case 540:
                        // line 2226 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 541:
                        // line 2229 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 542:
                        // line 2232 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(lexer.getPosition(), null, ((String)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 543:
                        // line 2235 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(((BlockArgNode)yyVals[0+yyTop]).getPosition(), null, null, ((BlockArgNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 544:
                        // line 2239 "RubyParser.y"
                    {
                        yyVal = ((ArgsTailHolder)yyVals[0+yyTop]);
                    }
                    break;
                    case 545:
                        // line 2242 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(lexer.getPosition(), null, null, null);
                    }
                    break;
                    case 546:
                        // line 2247 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 547:
                        // line 2250 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-7+yyTop]).getPosition(), ((ListNode)yyVals[-7+yyTop]), ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 548:
                        // line 2253 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 549:
                        // line 2256 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 550:
                        // line 2259 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), null, ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 551:
                        // line 2262 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 552:
                        // line 2265 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 553:
                        // line 2268 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 554:
                        // line 2271 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), null, ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 555:
                        // line 2274 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), null, ((ListNode)yyVals[-1+yyTop]), null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 556:
                        // line 2277 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 557:
                        // line 2280 "RubyParser.y"
                    {
                        yyVal = support.new_args(((RestArgNode)yyVals[-1+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 558:
                        // line 2283 "RubyParser.y"
                    {
                        yyVal = support.new_args(((RestArgNode)yyVals[-3+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 559:
                        // line 2286 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ArgsTailHolder)yyVals[0+yyTop]).getPosition(), null, null, null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 560:
                        // line 2289 "RubyParser.y"
                    {
                        yyVal = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
                    }
                    break;
                    case 561:
                        // line 2293 "RubyParser.y"
                    {
                        support.yyerror("formal argument cannot be a constant");
                    }
                    break;
                    case 562:
                        // line 2296 "RubyParser.y"
                    {
                        support.yyerror("formal argument cannot be an instance variable");
                    }
                    break;
                    case 563:
                        // line 2299 "RubyParser.y"
                    {
                        support.yyerror("formal argument cannot be a global variable");
                    }
                    break;
                    case 564:
                        // line 2302 "RubyParser.y"
                    {
                        support.yyerror("formal argument cannot be a class variable");
                    }
                    break;
                    case 566:
                        // line 2308 "RubyParser.y"
                    {
                        yyVal = support.formal_argument(((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 567:
                        // line 2312 "RubyParser.y"
                    {
                        yyVal = support.arg_var(((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 568:
                        // line 2315 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    /*            {
            ID tid = internal_id();
            arg_var(tid);
            if (dyna_in_block()) {
                $2->nd_value = NEW_DVAR(tid);
            }
            else {
                $2->nd_value = NEW_LVAR(tid);
            }
            $$ = NEW_ARGS_AUX(tid, 1);
            $$->nd_next = $2;*/
                    }
                    break;
                    case 569:
                        // line 2331 "RubyParser.y"
                    {
                        yyVal = new ArrayNode(lexer.getPosition(), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 570:
                        // line 2334 "RubyParser.y"
                    {
                        ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
                        yyVal = ((ListNode)yyVals[-2+yyTop]);
                    }
                    break;
                    case 571:
                        // line 2339 "RubyParser.y"
                    {
                        support.arg_var(support.formal_argument(((String)yyVals[0+yyTop])));
                        yyVal = ((String)yyVals[0+yyTop]);
                    }
                    break;
                    case 572:
                        // line 2344 "RubyParser.y"
                    {
                        yyVal = support.keyword_arg(((Node)yyVals[0+yyTop]).getPosition(), support.assignableLabelOrIdentifier(((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])));
                    }
                    break;
                    case 573:
                        // line 2347 "RubyParser.y"
                    {
                        yyVal = support.keyword_arg(lexer.getPosition(), support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), new RequiredKeywordArgumentValueNode()));
                    }
                    break;
                    case 574:
                        // line 2351 "RubyParser.y"
                    {
                        yyVal = support.keyword_arg(support.getPosition(((Node)yyVals[0+yyTop])), support.assignableLabelOrIdentifier(((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])));
                    }
                    break;
                    case 575:
                        // line 2354 "RubyParser.y"
                    {
                        yyVal = support.keyword_arg(lexer.getPosition(), support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), new RequiredKeywordArgumentValueNode()));
                    }
                    break;
                    case 576:
                        // line 2359 "RubyParser.y"
                    {
                        yyVal = new ArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 577:
                        // line 2362 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 578:
                        // line 2366 "RubyParser.y"
                    {
                        yyVal = new ArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 579:
                        // line 2369 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 580:
                        // line 2373 "RubyParser.y"
                    {
                        yyVal = ((String)yyVals[0+yyTop]);
                    }
                    break;
                    case 581:
                        // line 2376 "RubyParser.y"
                    {
                        yyVal = ((String)yyVals[0+yyTop]);
                    }
                    break;
                    case 582:
                        // line 2380 "RubyParser.y"
                    {
                        support.shadowing_lvar(((String)yyVals[0+yyTop]));
                        yyVal = ((String)yyVals[0+yyTop]);
                    }
                    break;
                    case 583:
                        // line 2384 "RubyParser.y"
                    {
                        yyVal = support.internalId();
                    }
                    break;
                    case 584:
                        // line 2388 "RubyParser.y"
                    {
                        support.arg_var(((String)yyVals[-2+yyTop]));
                        yyVal = new OptArgNode(support.getPosition(((Node)yyVals[0+yyTop])), support.assignableLabelOrIdentifier(((String)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
                    }
                    break;
                    case 585:
                        // line 2393 "RubyParser.y"
                    {
                        support.arg_var(support.formal_argument(((String)yyVals[-2+yyTop])));
                        yyVal = new OptArgNode(support.getPosition(((Node)yyVals[0+yyTop])), support.assignableLabelOrIdentifier(((String)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
                    }
                    break;
                    case 586:
                        // line 2398 "RubyParser.y"
                    {
                        yyVal = new BlockNode(((Node)yyVals[0+yyTop]).getPosition()).add(((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 587:
                        // line 2401 "RubyParser.y"
                    {
                        yyVal = support.appendToBlock(((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 588:
                        // line 2405 "RubyParser.y"
                    {
                        yyVal = new BlockNode(((Node)yyVals[0+yyTop]).getPosition()).add(((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 589:
                        // line 2408 "RubyParser.y"
                    {
                        yyVal = support.appendToBlock(((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 592:
                        // line 2415 "RubyParser.y"
                    {
                        if (!support.is_local_id(((String)yyVals[0+yyTop]))) {
                            support.yyerror("rest argument must be local variable");
                        }

                        yyVal = new RestArgNode(support.arg_var(support.shadowing_lvar(((String)yyVals[0+yyTop]))));
                    }
                    break;
                    case 593:
                        // line 2422 "RubyParser.y"
                    {
                        yyVal = new UnnamedRestArgNode(lexer.getPosition(), "", support.getCurrentScope().addVariable("*"));
                    }
                    break;
                    case 596:
                        // line 2430 "RubyParser.y"
                    {
                        if (!support.is_local_id(((String)yyVals[0+yyTop]))) {
                            support.yyerror("block argument must be local variable");
                        }

                        yyVal = new BlockArgNode(support.arg_var(support.shadowing_lvar(((String)yyVals[0+yyTop]))));
                    }
                    break;
                    case 597:
                        // line 2438 "RubyParser.y"
                    {
                        yyVal = ((BlockArgNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 598:
                        // line 2441 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 599:
                        // line 2445 "RubyParser.y"
                    {
                        if (!(((Node)yyVals[0+yyTop]) instanceof SelfNode)) {
                            support.checkExpression(((Node)yyVals[0+yyTop]));
                        }
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 600:
                        // line 2451 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_BEG);
                    }
                    break;
                    case 601:
                        // line 2453 "RubyParser.y"
                    {
                        if (((Node)yyVals[-1+yyTop]) == null) {
                            support.yyerror("can't define single method for ().");
                        } else if (((Node)yyVals[-1+yyTop]) instanceof ILiteralNode) {
                            support.yyerror("can't define single method for literals.");
                        }
                        support.checkExpression(((Node)yyVals[-1+yyTop]));
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 602:
                        // line 2464 "RubyParser.y"
                    {
                        yyVal = new HashNode(lexer.getPosition());
                    }
                    break;
                    case 603:
                        // line 2467 "RubyParser.y"
                    {
                        yyVal = ((HashNode)yyVals[-1+yyTop]);
                    }
                    break;
                    case 604:
                        // line 2472 "RubyParser.y"
                    {
                        yyVal = new HashNode(lexer.getPosition(), ((KeyValuePair)yyVals[0+yyTop]));
                    }
                    break;
                    case 605:
                        // line 2475 "RubyParser.y"
                    {
                        yyVal = ((HashNode)yyVals[-2+yyTop]).add(((KeyValuePair)yyVals[0+yyTop]));
                    }
                    break;
                    case 606:
                        // line 2480 "RubyParser.y"
                    {
                        yyVal = new KeyValuePair<Node,Node>(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 607:
                        // line 2483 "RubyParser.y"
                    {
                        SymbolNode label = new SymbolNode(support.getPosition(((Node)yyVals[0+yyTop])), new ByteList(((String)yyVals[-1+yyTop]).getBytes(), lexer.getEncoding()));
                        yyVal = new KeyValuePair<Node,Node>(label, ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 608:
                        // line 2487 "RubyParser.y"
                    {
                        if (((Node)yyVals[-2+yyTop]) instanceof StrNode) {
                            DStrNode dnode = new DStrNode(support.getPosition(((Node)yyVals[-2+yyTop])), lexer.getEncoding());
                            dnode.add(((Node)yyVals[-2+yyTop]));
                            yyVal = new KeyValuePair<Node,Node>(new DSymbolNode(support.getPosition(((Node)yyVals[-2+yyTop])), dnode), ((Node)yyVals[0+yyTop]));
                        } else if (((Node)yyVals[-2+yyTop]) instanceof DStrNode) {
                            yyVal = new KeyValuePair<Node,Node>(new DSymbolNode(support.getPosition(((Node)yyVals[-2+yyTop])), ((DStrNode)yyVals[-2+yyTop])), ((Node)yyVals[0+yyTop]));
                        } else {
                            support.compile_error("Uknown type for assoc in strings: " + ((Node)yyVals[-2+yyTop]));
                        }

                    }
                    break;
                    case 609:
                        // line 2500 "RubyParser.y"
                    {
                        yyVal = new KeyValuePair<Node,Node>(null, ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 626:
                        // line 2510 "RubyParser.y"
                    {
                        yyVal = ((String)yyVals[0+yyTop]);
                    }
                    break;
                    case 627:
                        // line 2513 "RubyParser.y"
                    {
                        yyVal = ((String)yyVals[0+yyTop]);
                    }
                    break;
                    case 635:
                        // line 2524 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 636:
                        // line 2528 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    // line 9790 "-"
// ACTIONS_END
                }
                yyTop -= yyLen[yyN];
                yyState = yyStates[yyTop];
                int yyM = yyLhs[yyN];
                if (yyState == 0 && yyM == 0) {
                    if (yydebug != null) yydebug.shift(0, yyFinal);
                    yyState = yyFinal;
                    if (yyToken < 0) {
                        yyToken = yyLex.nextToken();
//            yyToken = yyLex.advance() ? yyLex.token() : 0;
                        if (yydebug != null)
                            yydebug.lex(yyState, yyToken,yyName(yyToken), yyLex.value());
                    }
                    if (yyToken == 0) {
                        if (yydebug != null) yydebug.accept(yyVal);
                        return yyVal;
                    }
                    continue yyLoop;
                }
                if ((yyN = yyGindex[yyM]) != 0 && (yyN += yyState) >= 0
                        && yyN < yyTable.length && yyCheck[yyN] == yyState)
                    yyState = yyTable[yyN];
                else
                    yyState = yyDgoto[yyM];
                if (yydebug != null) yydebug.shift(yyStates[yyTop], yyState);
                continue yyLoop;
            }
        }
    }

// ACTION_BODIES
    // line 2533 "RubyParser.y"

    /** The parse method use an lexer stream and parse it to an AST node 
     * structure
     */
    public RubyParserResult parse(ParserConfiguration configuration, LexerSource source) throws IOException {
        support.reset();
        support.setConfiguration(configuration);
        support.setResult(new RubyParserResult());

        lexer.reset();
        lexer.setSource(source);
        lexer.setEncoding(configuration.getDefaultEncoding());

        yyparse(lexer, configuration.isDebug() ? new YYDebug() : null);

        return support.getResult();
    }
}
// line 9842 "-"
