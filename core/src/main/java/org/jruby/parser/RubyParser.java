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
    public static final int kEMIT = 307;
    public static final int tIDENTIFIER = 308;
    public static final int tFID = 309;
    public static final int tGVAR = 310;
    public static final int tIVAR = 311;
    public static final int tCONSTANT = 312;
    public static final int tCVAR = 313;
    public static final int tLABEL = 314;
    public static final int tCHAR = 315;
    public static final int tUPLUS = 316;
    public static final int tUMINUS = 317;
    public static final int tUMINUS_NUM = 318;
    public static final int tPOW = 319;
    public static final int tCMP = 320;
    public static final int tEQ = 321;
    public static final int tEQQ = 322;
    public static final int tNEQ = 323;
    public static final int tGEQ = 324;
    public static final int tLEQ = 325;
    public static final int tANDOP = 326;
    public static final int tOROP = 327;
    public static final int tMATCH = 328;
    public static final int tNMATCH = 329;
    public static final int tDOT = 330;
    public static final int tDOT2 = 331;
    public static final int tDOT3 = 332;
    public static final int tAREF = 333;
    public static final int tASET = 334;
    public static final int tLSHFT = 335;
    public static final int tRSHFT = 336;
    public static final int tCOLON2 = 337;
    public static final int tCOLON3 = 338;
    public static final int tOP_ASGN = 339;
    public static final int tASSOC = 340;
    public static final int tLPAREN = 341;
    public static final int tLPAREN2 = 342;
    public static final int tRPAREN = 343;
    public static final int tLPAREN_ARG = 344;
    public static final int tLBRACK = 345;
    public static final int tRBRACK = 346;
    public static final int tLBRACE = 347;
    public static final int tLBRACE_ARG = 348;
    public static final int tSTAR = 349;
    public static final int tSTAR2 = 350;
    public static final int tAMPER = 351;
    public static final int tAMPER2 = 352;
    public static final int tTILDE = 353;
    public static final int tPERCENT = 354;
    public static final int tDIVIDE = 355;
    public static final int tPLUS = 356;
    public static final int tMINUS = 357;
    public static final int tLT = 358;
    public static final int tGT = 359;
    public static final int tPIPE = 360;
    public static final int tBANG = 361;
    public static final int tCARET = 362;
    public static final int tLCURLY = 363;
    public static final int tRCURLY = 364;
    public static final int tBACK_REF2 = 365;
    public static final int tSYMBEG = 366;
    public static final int tSTRING_BEG = 367;
    public static final int tXSTRING_BEG = 368;
    public static final int tREGEXP_BEG = 369;
    public static final int tWORDS_BEG = 370;
    public static final int tQWORDS_BEG = 371;
    public static final int tSTRING_DBEG = 372;
    public static final int tSTRING_DVAR = 373;
    public static final int tSTRING_END = 374;
    public static final int tLAMBDA = 375;
    public static final int tLAMBEG = 376;
    public static final int tNTH_REF = 377;
    public static final int tBACK_REF = 378;
    public static final int tSTRING_CONTENT = 379;
    public static final int tINTEGER = 380;
    public static final int tIMAGINARY = 381;
    public static final int tFLOAT = 382;
    public static final int tRATIONAL = 383;
    public static final int tREGEXP_END = 384;
    public static final int tSYMBOLS_BEG = 385;
    public static final int tQSYMBOLS_BEG = 386;
    public static final int tDSTAR = 387;
    public static final int tSTRING_DEND = 388;
    public static final int tLABEL_END = 389;
    public static final int tLOWEST = 390;
    public static final int yyErrorCode = 256;

    /** number of final state.
     */
    protected static final int yyFinal = 1;

    /** parser tables.
     Order is mandated by <i>jay</i>.
     */
    protected static final short[] yyLhs = {
//yyLhs 641
            -1,   141,     0,   133,   134,   134,   134,   134,   135,   144,
            135,   140,    37,    36,    38,    38,    38,    38,    44,   145,
            44,   146,    39,    39,    39,    39,    39,    39,    39,    39,
            39,    39,    39,    39,    39,    39,    39,    39,    39,    39,
            39,    39,    39,    39,    39,    31,    31,    40,    40,    40,
            40,    40,    40,    45,    32,    32,    59,    59,   148,   110,
            139,    43,    43,    43,    43,    43,    43,    43,    43,    43,
            43,    43,   111,   111,   122,   122,   112,   112,   112,   112,
            112,   112,   112,   112,   112,   112,    71,    71,   100,   100,
            101,   101,    72,    72,    72,    72,    72,    72,    72,    72,
            72,    72,    72,    72,    72,    72,    72,    72,    72,    72,
            72,    77,    77,    77,    77,    77,    77,    77,    77,    77,
            77,    77,    77,    77,    77,    77,    77,    77,    77,    77,
            6,     6,    30,    30,    30,     7,     7,     7,     7,     7,
            115,   115,   116,   116,    61,   149,    61,     8,     8,     8,
            8,     8,     8,     8,     8,     8,     8,     8,     8,     8,
            8,     8,     8,     8,     8,     8,     8,     8,     8,     8,
            8,     8,     8,     8,     8,     8,     8,   131,   131,   131,
            131,   131,   131,   131,   131,   131,   131,   131,   131,   131,
            131,   131,   131,   131,   131,   131,   131,   131,   131,   131,
            131,   131,   131,   131,   131,   131,   131,   131,   131,   131,
            131,   131,   131,   131,   131,   131,   131,   131,   131,   131,
            131,    41,    41,    41,    41,    41,    41,    41,    41,    41,
            41,    41,    41,    41,    41,    41,    41,    41,    41,    41,
            41,    41,    41,    41,    41,    41,    41,    41,    41,    41,
            41,    41,    41,    41,    41,    41,    41,    41,    41,    41,
            41,    41,    41,    41,    41,    73,    76,    76,    76,    76,
            53,    57,    57,   125,   125,   125,   125,   125,    51,    51,
            51,    51,    51,   151,    55,   104,   103,   103,    79,    79,
            79,    79,    35,    35,    70,    70,    70,    42,    42,    42,
            42,    42,    42,    42,    42,    42,    42,    42,    42,   152,
            42,   153,    42,    42,    42,    42,    42,    42,    42,    42,
            42,    42,    42,    42,    42,    42,    42,    42,    42,    42,
            42,   155,   157,    42,   158,   159,    42,    42,    42,   160,
            161,    42,   162,    42,   164,   165,    42,   166,    42,   167,
            42,   168,   169,    42,    42,    42,    42,    42,   170,    42,
            42,    46,   154,   154,   154,   156,   156,    49,    49,    47,
            47,   124,   124,   126,   126,    84,    84,   127,   127,   127,
            127,   127,   127,   127,   127,   127,    91,    91,    91,    91,
            90,    90,    66,    66,    66,    66,    66,    66,    66,    66,
            66,    66,    66,    66,    66,    66,    66,    68,    68,    67,
            67,    67,   119,   119,   118,   118,   128,   128,   171,   121,
            65,    65,   120,   120,   172,   109,    58,    58,    58,    58,
            22,    22,    22,    22,    22,    22,    22,    22,    22,   173,
            108,   174,   108,    74,    48,    48,   113,   113,    75,    75,
            75,    50,    50,    52,    52,    28,    28,    28,    15,    16,
            16,    16,    17,    18,    19,    25,    25,    81,    81,    27,
            27,    87,    87,    85,    85,    26,    26,    88,    88,    80,
            80,    86,    86,    20,    20,    21,    21,    24,    24,    23,
            175,    23,   176,   177,    23,    62,    62,    62,    62,     2,
            1,     1,     1,     1,    29,    33,    33,    34,    34,    34,
            34,    56,    56,    56,    56,    56,    56,    56,    56,    56,
            56,    56,    56,   114,   114,   114,   114,   114,   114,   114,
            114,   114,   114,   114,   114,    63,    63,    54,   178,    54,
            54,    69,   179,    69,    92,    92,    92,    92,    89,    89,
            64,    64,    64,    64,    64,    64,    64,    64,    64,    64,
            64,    64,    64,    64,    64,   132,   132,   132,   132,     9,
            9,   117,   117,    82,    82,   138,    93,    93,    94,    94,
            95,    95,    96,    96,   136,   136,   137,   137,    60,   123,
            102,   102,    83,    83,    11,    11,    13,    13,    12,    12,
            107,   106,   106,    14,   180,    14,    97,    97,    98,    98,
            99,    99,    99,    99,     3,     3,     3,     4,     4,     4,
            4,     5,     5,     5,    10,    10,   142,   142,   147,   147,
            129,   130,   150,   150,   150,   163,   163,   143,   143,    78,
            105,
    }, yyLen = {
//yyLen 641
            2,     0,     2,     2,     1,     1,     3,     2,     1,     0,
            5,     1,     4,     2,     1,     1,     3,     2,     1,     0,
            5,     0,     4,     3,     3,     3,     2,     3,     3,     3,
            3,     3,     4,     1,     3,     3,     6,     5,     5,     5,
            5,     3,     3,     3,     1,     3,     3,     1,     3,     3,
            3,     2,     1,     1,     1,     1,     1,     4,     0,     5,
            1,     2,     3,     4,     5,     4,     5,     2,     2,     2,
            2,     2,     1,     3,     1,     3,     1,     2,     3,     5,
            2,     4,     2,     4,     1,     3,     1,     3,     2,     3,
            1,     3,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     4,     3,     3,     3,     3,     2,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     4,     3,     3,     3,     3,     2,     1,
            1,     1,     2,     1,     3,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     0,     4,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     3,     5,     3,     5,     6,     5,     5,     5,     5,
            4,     3,     3,     3,     3,     3,     3,     3,     3,     3,
            4,     2,     2,     3,     3,     3,     3,     3,     3,     3,
            3,     3,     3,     3,     3,     3,     2,     2,     3,     3,
            3,     3,     3,     6,     1,     1,     1,     2,     4,     2,
            3,     1,     1,     1,     1,     2,     4,     2,     1,     2,
            2,     4,     1,     0,     2,     2,     2,     1,     1,     2,
            3,     4,     1,     1,     3,     4,     2,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     3,     0,
            3,     0,     4,     3,     3,     2,     3,     3,     1,     4,
            3,     1,     5,     4,     3,     2,     1,     2,     2,     6,
            6,     0,     0,     7,     0,     0,     7,     5,     4,     0,
            0,     9,     0,     6,     0,     0,     8,     0,     5,     0,
            6,     0,     0,     9,     1,     1,     1,     1,     0,     4,
            3,     1,     1,     1,     2,     1,     1,     1,     5,     1,
            2,     1,     1,     1,     3,     1,     3,     1,     4,     6,
            3,     5,     2,     4,     1,     3,     4,     2,     2,     1,
            2,     0,     6,     8,     4,     6,     4,     2,     6,     2,
            4,     6,     2,     4,     2,     4,     1,     1,     1,     3,
            1,     4,     1,     4,     1,     3,     1,     1,     0,     3,
            4,     1,     3,     3,     0,     5,     2,     4,     5,     5,
            2,     4,     4,     3,     3,     3,     2,     1,     4,     0,
            5,     0,     5,     5,     1,     1,     6,     0,     1,     1,
            1,     2,     1,     2,     1,     1,     1,     1,     1,     1,
            1,     2,     3,     3,     3,     3,     3,     0,     3,     1,
            2,     3,     3,     0,     3,     3,     3,     3,     3,     0,
            3,     0,     3,     0,     2,     0,     2,     0,     2,     1,
            0,     3,     0,     0,     5,     1,     1,     1,     1,     2,
            1,     1,     1,     1,     3,     1,     2,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     1,     1,     0,     4,
            2,     3,     0,     3,     4,     2,     2,     1,     2,     0,
            6,     8,     4,     6,     4,     6,     2,     4,     6,     2,
            4,     2,     4,     1,     0,     1,     1,     1,     1,     1,
            1,     1,     3,     1,     3,     1,     2,     1,     2,     1,
            1,     3,     1,     3,     1,     1,     2,     1,     3,     3,
            1,     3,     1,     3,     1,     1,     2,     1,     1,     1,
            2,     2,     0,     1,     0,     4,     1,     2,     1,     3,
            3,     2,     4,     2,     1,     1,     1,     1,     1,     1,
            1,     1,     1,     1,     1,     1,     0,     1,     0,     1,
            2,     2,     0,     1,     1,     1,     1,     1,     2,     0,
            0,
    }, yyDefRed = {
//yyDefRed 1097
            1,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,   331,   334,     0,     0,     0,   356,   357,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     9,
            0,     0,     0,     0,   358,     0,     0,     0,     0,     0,
            0,     0,   459,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,   483,   485,   487,     0,     0,
            418,   535,   536,   507,   510,   508,   509,     0,     0,   456,
            60,   298,     0,   460,   299,   300,     0,   301,   302,   297,
            457,    33,    47,   455,   505,     0,     0,     0,     0,     0,
            0,   305,     0,    55,     0,     0,    86,     0,     4,   303,
            304,     0,     0,    72,     0,     2,     0,     5,     0,     7,
            354,   355,   318,     0,     0,   517,   516,   518,   519,     0,
            0,   521,   520,   522,     0,   513,   512,     0,   515,     0,
            0,     0,     0,   133,     0,   361,     0,   306,     0,   347,
            187,   198,   188,   211,   184,   204,   194,   193,   209,   192,
            191,   186,   212,   196,   185,   199,   203,   205,   197,   190,
            206,   213,   208,     0,     0,     0,     0,   183,   202,   201,
            214,   215,   218,   219,   220,   182,   189,   180,   181,     0,
            0,     0,   216,   217,     0,   137,     0,   172,   173,   169,
            150,   151,   152,   159,   156,   158,   153,   154,   174,   175,
            160,   161,   604,   166,   165,   149,   171,   168,   167,   163,
            164,   157,   155,   147,   170,   148,   176,   162,   349,   138,
            0,   603,   139,   207,   200,   210,   195,   177,   178,   179,
            135,   136,   141,   140,   143,     0,   142,   144,     0,     0,
            0,     0,     0,     0,    15,    14,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,   635,   636,     0,     0,
            0,   637,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,   371,
            372,     0,     0,     0,     0,     0,   483,     0,     0,   278,
            70,     0,     0,     0,   608,   282,    71,    69,     0,    68,
            0,     0,   436,    67,     0,   629,     0,     0,    21,     0,
            0,     0,     0,     0,   241,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,   266,     0,     0,     0,
            606,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            257,    51,   256,   502,   501,   503,   499,   500,     0,     0,
            0,     0,     0,     0,     0,     0,   328,     0,     0,     0,
            0,     0,   461,   441,   439,   327,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,   424,
            426,     0,     0,     0,   624,   625,     0,     0,    88,     0,
            0,     0,     0,     0,     0,     3,     0,   430,     0,   325,
            0,   506,     0,   130,     0,   132,     0,   538,   342,   537,
            0,     0,     0,     0,     0,     0,   351,   145,     0,     0,
            0,     0,   308,    13,     0,     0,   363,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,   638,
            0,     0,     0,     0,     0,     0,   339,   611,   289,   285,
            0,   613,     0,     0,   279,   287,     0,   280,     0,   320,
            0,   284,   274,   273,     0,     0,     0,     0,   324,    50,
            23,    25,    24,     0,     0,     0,     0,     0,    11,     0,
            360,     0,     0,     0,     0,     0,   313,     0,     0,   310,
            316,     0,   633,   267,     0,   269,   317,   607,     0,    90,
            0,     0,     0,     0,     0,   492,   490,   504,   489,   486,
            462,   484,   463,   464,   488,   465,   466,   469,     0,   475,
            476,     0,   570,   567,   566,   565,   568,   575,   584,     0,
            0,   595,   594,   599,   598,   585,     0,     0,     0,     0,
            592,   421,     0,     0,     0,   563,   582,     0,   547,   573,
            569,     0,     0,     0,   471,   472,     0,   477,   478,     0,
            0,     0,    27,    28,    29,    30,    31,    48,    49,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,   619,     0,     0,
            620,   434,     0,     0,     0,     0,   433,     0,   435,     0,
            617,   618,     0,    41,     0,     0,    46,    45,     0,    42,
            288,     0,     0,     0,     0,     0,    89,    34,    43,   292,
            0,    35,     0,     6,    58,    62,     0,   540,     0,     0,
            0,     0,     0,     0,   134,     0,     0,     0,     0,     0,
            0,     0,     0,     0,   449,     0,     0,   450,     0,     0,
            369,    16,     0,   364,     0,     0,     0,     0,     0,     0,
            0,     0,     0,   338,   366,   332,   365,   335,     0,     0,
            0,     0,     0,     0,     0,   610,     0,     0,     0,   286,
            609,   319,   630,     0,     0,   270,   323,    22,     0,     0,
            32,   359,     0,     0,     0,   312,     0,     0,     0,     0,
            0,     0,     0,     0,   493,     0,   468,   470,   480,     0,
            0,   373,     0,   375,     0,     0,     0,   596,   600,     0,
            561,     0,     0,   419,     0,   556,     0,   559,     0,   545,
            586,     0,   546,   576,   474,   482,   410,     0,   408,     0,
            407,     0,     0,     0,     0,     0,   272,     0,   431,   271,
            0,     0,   432,     0,     0,     0,     0,     0,     0,     0,
            0,     0,    87,     0,     0,     0,     0,   345,     0,     0,
            438,   348,   605,     0,     0,     0,   352,   146,     0,     0,
            0,   452,   370,     0,    12,   454,     0,   367,     0,     0,
            0,     0,     0,     0,     0,   337,     0,     0,     0,     0,
            0,     0,   612,   291,   281,     0,   322,    10,   268,    91,
            0,     0,   495,   496,   497,   491,   498,     0,     0,     0,
            0,   572,     0,     0,   588,   571,     0,   548,     0,     0,
            0,     0,   574,     0,   593,     0,   583,   601,     0,     0,
            0,     0,     0,   406,   580,     0,     0,   389,     0,   590,
            0,     0,     0,     0,     0,     0,    37,     0,    38,     0,
            64,    40,     0,    39,     0,    66,     0,   631,   429,   428,
            0,     0,     0,     0,     0,     0,     0,   539,   343,   541,
            350,   543,     0,    20,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,   451,
            0,   453,     0,   329,     0,   330,   290,     0,     0,     0,
            340,     0,     0,   374,     0,     0,     0,   376,   420,     0,
            0,   562,   423,   422,     0,   554,     0,   552,     0,   557,
            560,   544,     0,     0,   404,     0,     0,   399,     0,   387,
            0,   402,   409,   388,     0,     0,     0,     0,   442,   440,
            0,   425,    36,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,   444,   443,   445,   333,   336,     0,
            494,     0,     0,     0,     0,   416,     0,   414,   417,     0,
            0,     0,     0,     0,     0,   390,   411,     0,     0,   581,
            0,     0,     0,   591,   315,     0,    59,   346,     0,     0,
            0,     0,     0,     0,   446,     0,     0,     0,     0,     0,
            413,   555,     0,   550,   553,   558,     0,   405,     0,   396,
            0,   394,   386,     0,   400,   403,     0,     0,   353,     0,
            368,   341,     0,   415,     0,     0,     0,     0,     0,   551,
            398,     0,   392,   395,   401,     0,   393,
    }, yyDgoto = {
//yyDgoto 181
            1,   366,    69,    70,   683,   646,   133,   233,   640,   875,
            426,   577,   578,   579,   220,    71,    72,    73,    74,    75,
            369,   368,    76,   549,   371,    77,    78,   558,    79,    80,
            134,    81,    82,    83,    84,   668,   240,   241,   242,   243,
            86,    87,    88,    89,   244,   260,   325,   837,  1015,   838,
            830,   502,   834,   648,   448,   309,    91,   798,    92,    93,
            580,   235,   865,   262,   581,   582,   891,   788,   789,   689,
            659,    95,    96,   301,   478,   696,   335,   263,   245,   504,
            375,   373,   583,   584,   762,   379,   381,    99,   100,   770,
            984,  1035,   877,   586,   894,   895,   587,   341,   505,   304,
            101,   540,   896,   494,   305,   495,   779,   588,   439,   420,
            675,   102,   103,   461,   264,   236,   237,   589,  1026,   872,
            773,   376,   332,   899,   291,   506,   763,   764,  1027,   499,
            804,   222,   590,   105,   106,   107,   591,   592,   593,   108,
            519,     2,   269,   270,   320,   459,   513,   500,   816,   692,
            533,   310,   334,   528,   467,   272,   715,   848,   273,   849,
            723,  1019,   679,   468,   676,   926,   453,   455,   691,   932,
            322,   377,   635,   601,   600,   755,   754,   861,   678,   690,
            454,
    }, yySindex = {
//yySindex 1097
            0,     0, 18211, 19520, 21210, 21600, 17639, 17981, 18342, 20690,
            20690,  7883,     0,     0, 21340, 18603, 18603,     0,     0, 18603,
            -149,  -137,     0,     0,     0,     0,    71, 17867,   221,     0,
            -114,     0,     0,     0,     0, 20690,     0,     0,     0,     0,
            0,     0,     0, 20820, 20820,   636,   -60, 18473, 20690, 18996,
            19389, 17119, 20820, 20950, 17753,     0,     0,     0,   266,   284,
            0,     0,     0,     0,     0,     0,     0,   299,   303,     0,
            0,     0,  -106,     0,     0,     0,  -116,     0,     0,     0,
            0,     0,     0,     0,     0,  1954,    99,  3969,     0,    87,
            3,     0,   -94,     0,   -32,   404,     0,   311,     0,     0,
            0, 21470,   350,     0,    20,     0,   164,     0,  -117,     0,
            0,     0,     0,  -149,  -137,     0,     0,     0,     0,    34,
            221,     0,     0,     0,     0,     0,     0,     0,     0,   636,
            20690,   315, 18342,     0,   200,     0,   445,     0,  -117,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            -94,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,   480,     0,     0, 19650, 18342,
            331,   297,   164,  1954,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,   251,    99,
            232,   522,   304,   591,   320,   232,     0,     0,   164,   364,
            608,     0, 20690, 20690,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,   358,   585,     0,     0,
            0,   400, 20820, 20820, 20820, 20820,     0, 20820,  3969,     0,
            0,   341,   661,   667,     0,     0,     0,     0,  6452,     0,
            18603, 18603,     0,     0,  8020,     0, 20690,   -81,     0, 19780,
            351, 18342, 18342,   -69,     0,   676,   412,   413,   408, 18473,
            417,     0,   221,    99,   221,   397,     0,   182,   194,   341,
            0,   399,   194,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,   444, 21730,   678,     0,   740,
            0,     0,     0,     0,     0,     0,     0,     0,  1414,  1550,
            1606,   -29,   418,  1694,   419,   226,     0,  2428,   425,  1796,
            427,   328,     0,     0,     0,     0, 20690, 20690, 20690, 20690,
            19650, 20690, 20690, 20820, 20820, 20820, 20820, 20820, 20820, 20820,
            20820, 20820, 20820, 20820, 20820, 20820, 20820, 20820, 20820, 20820,
            20820, 20820, 20820, 20820, 20820, 20820, 20820, 20820, 20820,     0,
            0,  4481,  4975, 18603,     0,     0, 22454, 20950,     0, 19910,
            18473, 17249,   750, 19910, 20950,     0, 17379,     0,   462,     0,
            467,     0,    99,     0,     0,     0,   164,     0,     0,     0,
            5394,  5899, 18603, 18342, 20690,   474,     0,     0,  1954,   460,
            20040,   556,     0,     0, 17509,   408,     0, 18342,   562,  5981,
            6534, 18603, 20820, 20820, 20820, 18342,   364, 20170,   578,     0,
            142,   142,     0, 22069, 22124, 18603,     0,     0,     0,     0,
            301,     0, 20820, 18734,     0,     0, 19127,     0,   221,     0,
            501,     0,     0,     0,   803,   804,   221,   319,     0,     0,
            0,     0,     0, 17981, 20690,  3969, 18211,   490,     0,   592,
            0,  5981,  6534, 20820, 20820,   221,     0,     0,   221,     0,
            0, 19258,     0,     0, 19389,     0,     0,     0,     0,     0,
            811, 22179, 22234, 18603, 21730,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     9,     0,
            0,   826,     0,     0,     0,     0,     0,     0,     0,  1502,
            2441,     0,     0,     0,     0,     0,   802,   557,   559,   820,
            0,     0,  -125,   827,   831,     0,     0,   842,     0,     0,
            0,   582,   857, 20820,     0,     0,   187,     0,     0,   870,
            -148,  -148,     0,     0,     0,     0,     0,     0,     0,   412,
            2502,  2502,  2502,  2502,  1999,  1999,  2880,  3416,  2502,  2502,
            3043,  3043,  1047,  1047,   412,   484,   412,   412,   -84,   -84,
            1999,  1999,  2546,  2546,  4919,  -148,   568,     0,   572,  -137,
            0,     0,   575,     0,   587,  -137,     0,     0,     0,   221,
            0,     0,  -137,     0,  3969, 20820,     0,     0,  2959,     0,
            0,   848,   873,   221, 21730,   880,     0,     0,     0,     0,
            0,     0,  3464,     0,     0,     0,   164,     0, 20690, 18342,
            -137,     0,     0,  -137,     0,   221,   666,   319,  2441, 18342,
            2441, 18095, 17981, 18211,     0,     0,   598,     0, 18342,   662,
            0,     0,   387,     0,   594,   600,   602,   611,   221,  2959,
            556,   668,   713,     0,     0,     0,     0,     0,     0,     0,
            0,     0,   221, 20690, 20820,     0, 20820,   341,   667,     0,
            0,     0,     0, 18734, 19127,     0,     0,     0,   319,   590,
            0,     0,   412,  3969,     0,     0,   194, 21730,     0,     0,
            0,     0,   221,   811,     0,  1022,     0,     0,     0,  1502,
            405,     0,   917,     0,   221,   221, 20820,     0,     0,  2955,
            0, 18342, 18342,     0,  2441,     0,  2441,     0,  1250,     0,
            0,   332,     0,     0,     0,     0,     0,   817,     0, 18342,
            0, 18342,   905, 18342, 20950, 20950,     0,   462,     0,     0,
            20950, 20950,     0,   462,   626,   629,    87,  -116,     0, 20820,
            20950, 20300,     0,   811, 21730, 20820,  -148,     0,   164,   717,
            0,     0,     0,   221,   726,   164,     0,     0,   613, 21860,
            232,     0,     0, 18342,     0,     0, 20690,     0,   732, 20820,
            20820, 20820, 20820,   686,   737,     0, 20430, 18342, 18342, 18342,
            0,   142,     0,     0,     0,   982,     0,     0,     0,     0,
            0, 18342,     0,     0,     0,     0,     0,   221,  1843,   991,
            1600,     0,   694,   980,     0,     0,  1002,     0,   783,   690,
            1011,  1012,     0,  1013,     0,  1002,     0,     0,   857,   998,
            1016,   221,  1023,     0,     0,  1026,  1027,     0,   714,     0,
            857, 21990,   819,   736, 20820,   821,     0,  3969,     0,  3969,
            0,     0,  3969,     0,  3969,     0, 20950,     0,     0,     0,
            3969, 20820,     0,   811,  3969, 18342, 18342,     0,     0,     0,
            0,     0,   474,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,   784,   706,     0,     0,
            18342,     0,   232,     0, 20820,     0,     0,    77,   846,   851,
            0, 19127,   748,     0,  1060,  1843,   721,     0,     0,  1183,
            2955,     0,     0,     0,  2955,     0,  2441,     0,  2955,     0,
            0,     0, 21990,  2955,     0,   756,  2454,     0,  1250,     0,
            2454,     0,     0,     0,     0,     0,   806,   735,     0,     0,
            3969,     0,     0,  3969,     0,   755,   860, 18342,     0, 22289,
            22344, 18603,   331, 18342,     0,     0,     0,     0,     0, 18342,
            0,  1843,  1060,  1843,  1089,     0,   248,     0,     0,  1002,
            1093,  1002,  1002,   735,  1095,     0,     0,  1098,  1101,     0,
            857,  1102,  1095,     0,     0, 22399,     0,     0,   886,     0,
            0,     0,     0,   221,     0,   387,   893,  1060,  1843,  1183,
            0,     0,  2955,     0,     0,     0,  2955,     0,  2955,     0,
            2454,     0,     0,  2955,     0,     0,     0,     0,     0,     0,
            0,     0,  1060,     0,  1002,  1095,  1128,  1095,  1095,     0,
            0,  2955,     0,     0,     0,  1095,     0,
    }, yyRindex = {
//yyRindex 1097
            0,     0,   198,     0,     0,     0,     0,     0,  1085,     0,
            0,   909,     0,     0,     0, 14724, 14836,     0,     0, 14939,
            4808,  4303, 15290, 15368, 15594, 15672, 21080,     0, 20560,     0,
            0, 15750, 15898, 15976,     0,     0,  5171,  3293, 16054, 16202,
            5302, 16280,     0,     0,     0,     0,     0,   127,    50,   835,
            822,   243,     0,     0,  1800,     0,     0,     0,  1813,   376,
            0,     0,     0,     0,     0,     0,     0,  1828,   392,     0,
            0,     0, 10130,     0,     0,     0, 10248,     0,     0,     0,
            0,     0,     0,     0,     0,    80,  1185, 13748, 10373, 16794,
            0,     0, 16857,     0, 16358,     0,     0,     0,     0,     0,
            0,   244,     0,     0,     0,     0,    38,     0, 18865,     0,
            0,     0,     0, 10491,  8360,     0,     0,     0,     0,     0,
            845,     0,     0,     0,  6807,     0,     0,  6944,     0,     0,
            0,     0,   127,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,   841,  1553,  1620,  1690,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,  1965,
            2372,  2877,     0,     0,  3382,     0,  3887,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,  1750,     0,     0,     0,    63,
            104,     0,  1506,   720,     0,     0,  8483,  8608,  8790,  8915,
            9033,  9158,  9276,  2152,  9401,  9519,  2283,  9644,     0, 17045,
            0,     0,  9887,     0,     0,     0,     0,     0,   909,     0,
            913,     0,     0,     0,  1079,  1281,  1589,  1608,  1754,  1830,
            3721,   885,  4226,  4491,  1262,  4731,     0,     0,  5428,     0,
            0,     0,     0,     0,     0,     0,     0,     0, 14449,     0,
            0, 15476, 16650, 16650,     0,     0,     0,     0,   854,     0,
            0,   203,     0,     0,   854,     0,     0,     0,     0,     0,
            0,    83,   174,     0,     0,     0, 10734, 10616, 16506,   127,
            0,   201,   854,   206,   854,     0,     0,   843,   843,     0,
            0,     0,   836,  1082,  1193,  1283,  1755,  1899,  1944,  2043,
            465,  5174,  5305,   494,  5810,     0,     0,     0,  5867,   291,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,  -120,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,    69,     0,     0,     0,     0,     0,     0,
            127,   296,   305,     0,     0,     0,    39,     0,  1848,     0,
            0,     0,   165,     0,  7345,     0,     0,     0,     0,     0,
            0,     0,    69,  1085,     0,   569,     0,     0,  1239,     0,
            280,   461,     0,     0,  1956, 10005,     0,   948,  7482,     0,
            0,    69,     0,     0,     0,   161,     0,     0,     0,     0,
            0,     0,  5492,     0,     0,    69,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,   854,     0,
            0,     0,     0,     0,    32,    32,   854,   854,     0,     0,
            0,     0,     0,     0,     0, 13620,    83,     0,     0,     0,
            0,     0,     0,     0,     0,   854,     0,    23,   854,     0,
            0,   855,     0,     0,  -170,     0,     0,     0,  6315,     0,
            318,     0,     0,    69,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            158,     0,     0,     0,     0,     0,    81,    79,     0,    18,
            0,     0,     0,    18,    18,     0,     0,    75,     0,     0,
            0,   159,    75,   137,     0,     0,     0,     0,     0,     0,
            7614,  7751,     0,     0,     0,     0,     0,     0,     0, 10856,
            12736, 12823, 12912, 13002, 12286, 12397, 13089, 13354, 13178, 13267,
            13444, 13533, 11699, 11819, 10977, 11939, 11097, 11217, 11458, 11578,
            12515, 12637, 12060, 12180,  1144,  7614,  5676,     0,  5807,  4677,
            0,     0,  6181,  3667,  6312, 18865,     0,  3798,     0,   859,
            0,     0,  6675,     0, 13709,     0,     0,     0,  1433,     0,
            0,     0,     0,   854,     0,   367,     0,     0,     0,     0,
            1088,     0, 14536,     0,     0,     0,     0,     0,     0,  1085,
            9762,  7076,  7213,     0,     0,   859,     0,   854,   224,  1085,
            173,     0,     0,    83,     0,   702,   606,     0,   729,   942,
            0,     0,   942,     0,  2657,  2788,  3162,  4172,   859, 14625,
            942,     0,     0,     0,     0,     0,     0,     0,  2492,  2997,
            3502,  1208,   859,     0,     0,     0,     0, 16614, 16650,     0,
            0,     0,     0,    64,    86,     0,     0,     0,   854,     0,
            0,     0, 11338, 13799,   117,     0,   843,     0,   896,  1247,
            1279,   771,   859,   457,     0,     0,     0,     0,     0,     0,
            229,     0,   231,     0,   854,    13,     0,     0,     0,     0,
            0,   174,    83,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,   -19,     0,   174,
            0,    83,     0,   174,     0,     0,     0,  8733,     0,     0,
            0,     0,     0, 14571, 15187,     0, 16893, 16739, 15077,     0,
            0,     0,     0,   570,     0,     0,  7751,     0,     0,     0,
            0,     0,     0,   854,     0,     0,     0,     0,     0,     0,
            0,     0,     0,   174,     0,     0,     0,     0,     0,     0,
            0,     0,     0,  8257,     0,     0,     0,    94,   174,   174,
            448,     0,     0,     0,     0,    32,     0,     0,     0,     0,
            605,    83,     0,     0,     0,     0,     0,   854,     0,   238,
            0,     0,     0,   -49,     0,     0,    18,     0,     0,     0,
            18,    18,     0,    18,     0,    18,     0,     0,    75,   114,
            56,   -19,    56,     0,     0,   101,    56,     0,     0,     0,
            101,   118,     0,     0,     0,     0,     0, 13862,     0, 13949,
            0,     0, 13988,     0, 14076,     0,     0,     0,     0,     0,
            14136,     0, 16956,   593, 14258,    83,  1085,     0,     0,     0,
            0,     0,   569,     0,   630,   894,  1455,  1474,  1699,  1791,
            1795,   586,  1868,  1915,   731,  1932,     0,     0,  2027,     0,
            1085,     0,     0,     0,     0,     0,     0,   942,     0,     0,
            0,   199,     0,     0,   258,     0,   264,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,   156,     0,     0,     0,
            0,     0,     0,     0,  1097,  1966,     0,   138,     0,     0,
            14350,     0,     0, 14410, 17009,     0,     0,  1085,  2046,     0,
            0,    69,   104,   948,     0,     0,     0,     0,     0,   174,
            0,     0,   274,     0,   275,     0,   -48,     0,     0,    18,
            18,    18,    18,   147,    56,     0,     0,    56,    56,     0,
            101,    56,    56,     0,     0,     0,     0,     0,     0,  8092,
            8129,  8144,  1246,   859,     0,   942,     0,   276,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,  2006,   739,     0,  1237,
            0,     0,   279,     0,    18,    56,    56,    56,    56,     0,
            0,     0,     0,     0,     0,    56,     0,
    }, yyGindex = {
//yyGindex 181
            0,     0,    27,     0,  -351,     0,   -68,     7,    -5,   403,
            988,     0,     0,   420,     0,     0,     0,  1148,     0,     0,
            925,  1169,     0,   840,     0,     0,     0,   850,     0,    30,
            1225,  -406,   -42,     0,   111,     0,   673,  -429,     0,    41,
            1259,  1260,     4,    11,   768,    10,    98,  -447,     0,   179,
            0,   298,     0,    12,     0,     8,  1229,   588,     0,     0,
            -646,     0,     0,   273,  -488,     0,     0,     0,  -506,   309,
            -196,   -76,   -36,   785,  -466,     0,     0,   622,    -2,    43,
            0,     0,  5936,   468,  -758,     0,     0,     0,     0,  -440,
            1782,   456,  -326,   473,   262,     0,     0,     0,    37,  -446,
            0,  -476,   260,  -291,  -461,     0,  -546,  1302,   -72,   452,
            -165,  1251,   -30,   249,   574,     0,   -20,   -92,     0,  -704,
            0,     0,  -175,  -801,     0,  -407,  -794,   505,   207,   472,
            -587,     0,  -814,  -375,     0,    16,     0,  3759,  1744,  1424,
            0,     0,    66,    61,     0,     0,     0,   -23,     0,     0,
            -273,     0,     0,     0,  -259,     0,  -450,     0,     0,     0,
            0,     0,     0,    25,     0,     0,     0,     0,     0,     0,
            0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
            0,
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
            "kSIGNAL","kEMIT","tIDENTIFIER","tFID","tGVAR","tIVAR","tCONSTANT",
            "tCVAR","tLABEL","tCHAR","tUPLUS","tUMINUS","tUMINUS_NUM","tPOW",
            "tCMP","tEQ","tEQQ","tNEQ","tGEQ","tLEQ","tANDOP","tOROP","tMATCH",
            "tNMATCH","tDOT","tDOT2","tDOT3","tAREF","tASET","tLSHFT","tRSHFT",
            "tCOLON2","tCOLON3","tOP_ASGN","tASSOC","tLPAREN","tLPAREN2",
            "tRPAREN","tLPAREN_ARG","tLBRACK","tRBRACK","tLBRACE","tLBRACE_ARG",
            "tSTAR","tSTAR2","tAMPER","tAMPER2","tTILDE","tPERCENT","tDIVIDE",
            "tPLUS","tMINUS","tLT","tGT","tPIPE","tBANG","tCARET","tLCURLY",
            "tRCURLY","tBACK_REF2","tSYMBEG","tSTRING_BEG","tXSTRING_BEG",
            "tREGEXP_BEG","tWORDS_BEG","tQWORDS_BEG","tSTRING_DBEG",
            "tSTRING_DVAR","tSTRING_END","tLAMBDA","tLAMBEG","tNTH_REF",
            "tBACK_REF","tSTRING_CONTENT","tINTEGER","tIMAGINARY","tFLOAT",
            "tRATIONAL","tREGEXP_END","tSYMBOLS_BEG","tQSYMBOLS_BEG","tDSTAR",
            "tSTRING_DEND","tLABEL_END","tLOWEST",
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
            "signalbodystmt : compstmt",
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
            "reswords : kEMIT",
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
            "$$23 :",
            "primary : kSIGNAL $$23 signalbodystmt kEND",
            "primary : kEMIT expr kEND",
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
            "$$24 :",
            "lambda : $$24 f_larglist lambda_body",
            "f_larglist : tLPAREN2 f_args opt_bv_decl tRPAREN",
            "f_larglist : f_args",
            "lambda_body : tLAMBEG compstmt tRCURLY",
            "lambda_body : kDO_LAMBDA compstmt kEND",
            "$$25 :",
            "do_block : kDO_BLOCK $$25 opt_block_param compstmt kEND",
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
            "$$26 :",
            "brace_block : tLCURLY $$26 opt_block_param compstmt tRCURLY",
            "$$27 :",
            "brace_block : kDO $$27 opt_block_param compstmt kEND",
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
            "$$28 :",
            "string_content : tSTRING_DVAR $$28 string_dvar",
            "$$29 :",
            "$$30 :",
            "string_content : tSTRING_DBEG $$29 $$30 compstmt tRCURLY",
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
            "$$31 :",
            "superclass : tLT $$31 expr_value term",
            "superclass : error term",
            "f_arglist : tLPAREN2 f_args rparen",
            "$$32 :",
            "f_arglist : $$32 f_args term",
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
            "$$33 :",
            "singleton : tLPAREN2 $$33 expr rparen",
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
                        // line 312 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_BEG);
                        support.initTopLocalVariables();
                    }
                    break;
                    case 2:
                        // line 315 "RubyParser.y"
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
                        // line 328 "RubyParser.y"
                    {
                        if (((Node)yyVals[-1+yyTop]) instanceof BlockNode) {
                            support.checkUselessStatements(((BlockNode)yyVals[-1+yyTop]));
                        }
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 5:
                        // line 336 "RubyParser.y"
                    {
                        yyVal = support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop])));
                    }
                    break;
                    case 6:
                        // line 339 "RubyParser.y"
                    {
                        yyVal = support.appendToBlock(((Node)yyVals[-2+yyTop]), support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop]))));
                    }
                    break;
                    case 7:
                        // line 342 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 9:
                        // line 347 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) {
                            support.yyerror("BEGIN in method");
                        }
                    }
                    break;
                    case 10:
                        // line 351 "RubyParser.y"
                    {
                        support.getResult().addBeginNode(new PreExe19Node(((ISourcePosition)yyVals[-4+yyTop]), support.getCurrentScope(), ((Node)yyVals[-1+yyTop])));
                        yyVal = null;
                    }
                    break;
                    case 11:
                        // line 355 "RubyParser.y"
                    {
                        Node node = ((Node)yyVals[0+yyTop]);
                        yyVal = support.signalBodyNode(node);
                    }
                    break;
                    case 12:
                        // line 361 "RubyParser.y"
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
                    case 13:
                        // line 379 "RubyParser.y"
                    {
                        if (((Node)yyVals[-1+yyTop]) instanceof BlockNode) {
                            support.checkUselessStatements(((BlockNode)yyVals[-1+yyTop]));
                        }
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 15:
                        // line 387 "RubyParser.y"
                    {
                        yyVal = support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop])));
                    }
                    break;
                    case 16:
                        // line 390 "RubyParser.y"
                    {
                        yyVal = support.appendToBlock(((Node)yyVals[-2+yyTop]), support.newline_node(((Node)yyVals[0+yyTop]), support.getPosition(((Node)yyVals[0+yyTop]))));
                    }
                    break;
                    case 17:
                        // line 393 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 18:
                        // line 397 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 19:
                        // line 401 "RubyParser.y"
                    {
                        support.yyerror("BEGIN is permitted only at toplevel");
                    }
                    break;
                    case 20:
                        // line 403 "RubyParser.y"
                    {
                        yyVal = new BeginNode(((ISourcePosition)yyVals[-4+yyTop]), ((Node)yyVals[-3+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-3+yyTop]));
                    }
                    break;
                    case 21:
                        // line 407 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_FNAME);
                    }
                    break;
                    case 22:
                        // line 409 "RubyParser.y"
                    {
                        yyVal = support.newAlias(((ISourcePosition)yyVals[-3+yyTop]), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 23:
                        // line 412 "RubyParser.y"
                    {
                        yyVal = new VAliasNode(((ISourcePosition)yyVals[-2+yyTop]), ((String)yyVals[-1+yyTop]), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 24:
                        // line 415 "RubyParser.y"
                    {
                        yyVal = new VAliasNode(((ISourcePosition)yyVals[-2+yyTop]), ((String)yyVals[-1+yyTop]), "$" + ((BackRefNode)yyVals[0+yyTop]).getType());
                    }
                    break;
                    case 25:
                        // line 418 "RubyParser.y"
                    {
                        support.yyerror("can't make alias for the number variables");
                    }
                    break;
                    case 26:
                        // line 421 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 27:
                        // line 424 "RubyParser.y"
                    {
                        yyVal = new IfNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), null);
                        support.fixpos(((Node)yyVal), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 28:
                        // line 428 "RubyParser.y"
                    {
                        yyVal = new IfNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), null, ((Node)yyVals[-2+yyTop]));
                        support.fixpos(((Node)yyVal), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 29:
                        // line 432 "RubyParser.y"
                    {
                        if (((Node)yyVals[-2+yyTop]) != null && ((Node)yyVals[-2+yyTop]) instanceof BeginNode) {
                            yyVal = new WhileNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((BeginNode)yyVals[-2+yyTop]).getBodyNode(), false);
                        } else {
                            yyVal = new WhileNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), true);
                        }
                    }
                    break;
                    case 30:
                        // line 439 "RubyParser.y"
                    {
                        if (((Node)yyVals[-2+yyTop]) != null && ((Node)yyVals[-2+yyTop]) instanceof BeginNode) {
                            yyVal = new UntilNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((BeginNode)yyVals[-2+yyTop]).getBodyNode(), false);
                        } else {
                            yyVal = new UntilNode(support.getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), true);
                        }
                    }
                    break;
                    case 31:
                        // line 446 "RubyParser.y"
                    {
                        Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
                        yyVal = new RescueNode(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), new RescueBodyNode(support.getPosition(((Node)yyVals[-2+yyTop])), null, body, null), null);
                    }
                    break;
                    case 32:
                        // line 450 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) {
                            support.warn(ID.END_IN_METHOD, ((ISourcePosition)yyVals[-3+yyTop]), "END in method; use at_exit");
                        }
                        yyVal = new PostExeNode(((ISourcePosition)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 34:
                        // line 457 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[0+yyTop]));
                        ((MultipleAsgn19Node)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                        yyVal = ((MultipleAsgn19Node)yyVals[-2+yyTop]);
                    }
                    break;
                    case 35:
                        // line 462 "RubyParser.y"
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
                    case 36:
                        // line 479 "RubyParser.y"
                    {
  /* FIXME: arg_concat logic missing for opt_call_args*/
                        yyVal = support.new_opElementAsgnNode(((Node)yyVals[-5+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 37:
                        // line 483 "RubyParser.y"
                    {
                        yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), ((String)yyVals[-2+yyTop]), ((String)yyVals[-1+yyTop]));
                    }
                    break;
                    case 38:
                        // line 486 "RubyParser.y"
                    {
                        yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), ((String)yyVals[-2+yyTop]), ((String)yyVals[-1+yyTop]));
                    }
                    break;
                    case 39:
                        // line 489 "RubyParser.y"
                    {
                        support.yyerror("can't make alias for the number variables");
                        yyVal = null;
                    }
                    break;
                    case 40:
                        // line 494 "RubyParser.y"
                    {
                        yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), ((String)yyVals[-2+yyTop]), ((String)yyVals[-1+yyTop]));
                    }
                    break;
                    case 41:
                        // line 497 "RubyParser.y"
                    {
                        support.backrefAssignError(((Node)yyVals[-2+yyTop]));
                    }
                    break;
                    case 42:
                        // line 500 "RubyParser.y"
                    {
                        yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 43:
                        // line 503 "RubyParser.y"
                    {
                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                        yyVal = ((MultipleAsgn19Node)yyVals[-2+yyTop]);
                        ((MultipleAsgn19Node)yyVals[-2+yyTop]).setPosition(support.getPosition(((MultipleAsgn19Node)yyVals[-2+yyTop])));
                    }
                    break;
                    case 45:
                        // line 515 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[0+yyTop]));
                        yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 46:
                        // line 519 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[0+yyTop]));
                        yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 48:
                        // line 526 "RubyParser.y"
                    {
                        yyVal = support.newAndNode(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 49:
                        // line 529 "RubyParser.y"
                    {
                        yyVal = support.newOrNode(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 50:
                        // line 532 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[0+yyTop])), "!");
                    }
                    break;
                    case 51:
                        // line 535 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[0+yyTop])), "!");
                    }
                    break;
                    case 53:
                        // line 540 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 57:
                        // line 550 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 58:
                        // line 555 "RubyParser.y"
                    {
                        support.pushBlockScope();
                    }
                    break;
                    case 59:
                        // line 557 "RubyParser.y"
                    {
                        yyVal = new IterNode(((ISourcePosition)yyVals[-4+yyTop]), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                        support.popCurrentScope();
                    }
                    break;
                    case 60:
                        // line 562 "RubyParser.y"
                    {
                        yyVal = support.new_fcall(((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 61:
                        // line 567 "RubyParser.y"
                    {
                        support.frobnicate_fcall_args(((FCallNode)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                        yyVal = ((FCallNode)yyVals[-1+yyTop]);
                    }
                    break;
                    case 62:
                        // line 571 "RubyParser.y"
                    {
                        support.frobnicate_fcall_args(((FCallNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
                        yyVal = ((FCallNode)yyVals[-2+yyTop]);
                    }
                    break;
                    case 63:
                        // line 575 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 64:
                        // line 578 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((String)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 65:
                        // line 581 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 66:
                        // line 584 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((String)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 67:
                        // line 587 "RubyParser.y"
                    {
                        yyVal = support.new_super(((ISourcePosition)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 68:
                        // line 590 "RubyParser.y"
                    {
                        yyVal = support.new_yield(((ISourcePosition)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 69:
                        // line 593 "RubyParser.y"
                    {
                        yyVal = new ReturnNode(((ISourcePosition)yyVals[-1+yyTop]), support.ret_args(((Node)yyVals[0+yyTop]), ((ISourcePosition)yyVals[-1+yyTop])));
                    }
                    break;
                    case 70:
                        // line 596 "RubyParser.y"
                    {
                        yyVal = new BreakNode(((ISourcePosition)yyVals[-1+yyTop]), support.ret_args(((Node)yyVals[0+yyTop]), ((ISourcePosition)yyVals[-1+yyTop])));
                    }
                    break;
                    case 71:
                        // line 599 "RubyParser.y"
                    {
                        yyVal = new NextNode(((ISourcePosition)yyVals[-1+yyTop]), support.ret_args(((Node)yyVals[0+yyTop]), ((ISourcePosition)yyVals[-1+yyTop])));
                    }
                    break;
                    case 73:
                        // line 605 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 74:
                        // line 610 "RubyParser.y"
                    {
                        yyVal = ((MultipleAsgn19Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 75:
                        // line 613 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ISourcePosition)yyVals[-2+yyTop]), support.newArrayNode(((ISourcePosition)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop])), null, null);
                    }
                    break;
                    case 76:
                        // line 618 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[0+yyTop]).getPosition(), ((ListNode)yyVals[0+yyTop]), null, null);
                    }
                    break;
                    case 77:
                        // line 621 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]).add(((Node)yyVals[0+yyTop])), null, null);
                    }
                    break;
                    case 78:
                        // line 624 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-2+yyTop]).getPosition(), ((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]), (ListNode) null);
                    }
                    break;
                    case 79:
                        // line 627 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-4+yyTop]).getPosition(), ((ListNode)yyVals[-4+yyTop]), ((Node)yyVals[-2+yyTop]), ((ListNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 80:
                        // line 630 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), new StarNode(lexer.getPosition()), null);
                    }
                    break;
                    case 81:
                        // line 633 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), new StarNode(lexer.getPosition()), ((ListNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 82:
                        // line 636 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((Node)yyVals[0+yyTop]).getPosition(), null, ((Node)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 83:
                        // line 639 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((Node)yyVals[-2+yyTop]).getPosition(), null, ((Node)yyVals[-2+yyTop]), ((ListNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 84:
                        // line 642 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(lexer.getPosition(), null, new StarNode(lexer.getPosition()), null);
                    }
                    break;
                    case 85:
                        // line 645 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(lexer.getPosition(), null, new StarNode(lexer.getPosition()), ((ListNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 87:
                        // line 650 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 88:
                        // line 655 "RubyParser.y"
                    {
                        yyVal = support.newArrayNode(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 89:
                        // line 658 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 90:
                        // line 663 "RubyParser.y"
                    {
                        yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 91:
                        // line 666 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 92:
                        // line 670 "RubyParser.y"
                    {
                        yyVal = support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 93:
                        // line 673 "RubyParser.y"
                    {
                        yyVal = new InstAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 94:
                        // line 676 "RubyParser.y"
                    {
                        yyVal = new GlobalAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 95:
                        // line 679 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) support.compile_error("dynamic constant assignment");

                        yyVal = new ConstDeclNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), null, NilImplicitNode.NIL);
                    }
                    break;
                    case 96:
                        // line 684 "RubyParser.y"
                    {
                        yyVal = new ClassVarAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 97:
                        // line 687 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to nil");
                        yyVal = null;
                    }
                    break;
                    case 98:
                        // line 691 "RubyParser.y"
                    {
                        support.compile_error("Can't change the value of self");
                        yyVal = null;
                    }
                    break;
                    case 99:
                        // line 695 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to true");
                        yyVal = null;
                    }
                    break;
                    case 100:
                        // line 699 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to false");
                        yyVal = null;
                    }
                    break;
                    case 101:
                        // line 703 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to __FILE__");
                        yyVal = null;
                    }
                    break;
                    case 102:
                        // line 707 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to __LINE__");
                        yyVal = null;
                    }
                    break;
                    case 103:
                        // line 711 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to __ENCODING__");
                        yyVal = null;
                    }
                    break;
                    case 104:
                        // line 715 "RubyParser.y"
                    {
                        yyVal = support.aryset(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 105:
                        // line 718 "RubyParser.y"
                    {
                        yyVal = support.attrset(((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 106:
                        // line 721 "RubyParser.y"
                    {
                        yyVal = support.attrset(((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 107:
                        // line 724 "RubyParser.y"
                    {
                        yyVal = support.attrset(((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 108:
                        // line 727 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) {
                            support.yyerror("dynamic constant assignment");
                        }

                        ISourcePosition position = support.getPosition(((Node)yyVals[-2+yyTop]));

                        yyVal = new ConstDeclNode(position, null, support.new_colon2(position, ((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop])), NilImplicitNode.NIL);
                    }
                    break;
                    case 109:
                        // line 736 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) {
                            support.yyerror("dynamic constant assignment");
                        }

                        ISourcePosition position = lexer.getPosition();

                        yyVal = new ConstDeclNode(position, null, support.new_colon3(position, ((String)yyVals[0+yyTop])), NilImplicitNode.NIL);
                    }
                    break;
                    case 110:
                        // line 745 "RubyParser.y"
                    {
                        support.backrefAssignError(((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 111:
                        // line 749 "RubyParser.y"
                    {
                        yyVal = support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 112:
                        // line 752 "RubyParser.y"
                    {
                        yyVal = new InstAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 113:
                        // line 755 "RubyParser.y"
                    {
                        yyVal = new GlobalAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 114:
                        // line 758 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) support.compile_error("dynamic constant assignment");

                        yyVal = new ConstDeclNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), null, NilImplicitNode.NIL);
                    }
                    break;
                    case 115:
                        // line 763 "RubyParser.y"
                    {
                        yyVal = new ClassVarAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 116:
                        // line 766 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to nil");
                        yyVal = null;
                    }
                    break;
                    case 117:
                        // line 770 "RubyParser.y"
                    {
                        support.compile_error("Can't change the value of self");
                        yyVal = null;
                    }
                    break;
                    case 118:
                        // line 774 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to true");
                        yyVal = null;
                    }
                    break;
                    case 119:
                        // line 778 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to false");
                        yyVal = null;
                    }
                    break;
                    case 120:
                        // line 782 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to __FILE__");
                        yyVal = null;
                    }
                    break;
                    case 121:
                        // line 786 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to __LINE__");
                        yyVal = null;
                    }
                    break;
                    case 122:
                        // line 790 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to __ENCODING__");
                        yyVal = null;
                    }
                    break;
                    case 123:
                        // line 794 "RubyParser.y"
                    {
                        yyVal = support.aryset(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 124:
                        // line 797 "RubyParser.y"
                    {
                        yyVal = support.attrset(((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 125:
                        // line 800 "RubyParser.y"
                    {
                        yyVal = support.attrset(((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 126:
                        // line 803 "RubyParser.y"
                    {
                        yyVal = support.attrset(((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 127:
                        // line 806 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) {
                            support.yyerror("dynamic constant assignment");
                        }

                        ISourcePosition position = support.getPosition(((Node)yyVals[-2+yyTop]));

                        yyVal = new ConstDeclNode(position, null, support.new_colon2(position, ((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop])), NilImplicitNode.NIL);
                    }
                    break;
                    case 128:
                        // line 815 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) {
                            support.yyerror("dynamic constant assignment");
                        }

                        ISourcePosition position = lexer.getPosition();

                        yyVal = new ConstDeclNode(position, null, support.new_colon3(position, ((String)yyVals[0+yyTop])), NilImplicitNode.NIL);
                    }
                    break;
                    case 129:
                        // line 824 "RubyParser.y"
                    {
                        support.backrefAssignError(((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 130:
                        // line 828 "RubyParser.y"
                    {
                        support.yyerror("class/module name must be CONSTANT");
                    }
                    break;
                    case 132:
                        // line 833 "RubyParser.y"
                    {
                        yyVal = support.new_colon3(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 133:
                        // line 836 "RubyParser.y"
                    {
                        yyVal = support.new_colon2(lexer.getPosition(), null, ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 134:
                        // line 839 "RubyParser.y"
                    {
                        yyVal = support.new_colon2(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 138:
                        // line 845 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_ENDFN);
                        yyVal = ((String)yyVals[0+yyTop]);
                    }
                    break;
                    case 139:
                        // line 849 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_ENDFN);
                        yyVal = ((String)yyVals[0+yyTop]);
                    }
                    break;
                    case 140:
                        // line 855 "RubyParser.y"
                    {
                        yyVal = new LiteralNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 141:
                        // line 858 "RubyParser.y"
                    {
                        yyVal = new LiteralNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 142:
                        // line 863 "RubyParser.y"
                    {
                        yyVal = ((LiteralNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 143:
                        // line 866 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 144:
                        // line 870 "RubyParser.y"
                    {
                        yyVal = support.newUndef(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 145:
                        // line 873 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_FNAME);
                    }
                    break;
                    case 146:
                        // line 875 "RubyParser.y"
                    {
                        yyVal = support.appendToBlock(((Node)yyVals[-3+yyTop]), support.newUndef(((Node)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[0+yyTop])));
                    }
                    break;
                    case 177:
                        // line 887 "RubyParser.y"
                    {
                        yyVal = "__LINE__";
                    }
                    break;
                    case 178:
                        // line 890 "RubyParser.y"
                    {
                        yyVal = "__FILE__";
                    }
                    break;
                    case 179:
                        // line 893 "RubyParser.y"
                    {
                        yyVal = "__ENCODING__";
                    }
                    break;
                    case 180:
                        // line 896 "RubyParser.y"
                    {
                        yyVal = "BEGIN";
                    }
                    break;
                    case 181:
                        // line 899 "RubyParser.y"
                    {
                        yyVal = "END";
                    }
                    break;
                    case 182:
                        // line 902 "RubyParser.y"
                    {
                        yyVal = "alias";
                    }
                    break;
                    case 183:
                        // line 905 "RubyParser.y"
                    {
                        yyVal = "and";
                    }
                    break;
                    case 184:
                        // line 908 "RubyParser.y"
                    {
                        yyVal = "begin";
                    }
                    break;
                    case 185:
                        // line 911 "RubyParser.y"
                    {
                        yyVal = "break";
                    }
                    break;
                    case 186:
                        // line 914 "RubyParser.y"
                    {
                        yyVal = "case";
                    }
                    break;
                    case 187:
                        // line 917 "RubyParser.y"
                    {
                        yyVal = "class";
                    }
                    break;
                    case 188:
                        // line 920 "RubyParser.y"
                    {
                        yyVal = "def";
                    }
                    break;
                    case 189:
                        // line 923 "RubyParser.y"
                    {
                        yyVal = "defined?";
                    }
                    break;
                    case 190:
                        // line 926 "RubyParser.y"
                    {
                        yyVal = "do";
                    }
                    break;
                    case 191:
                        // line 929 "RubyParser.y"
                    {
                        yyVal = "else";
                    }
                    break;
                    case 192:
                        // line 932 "RubyParser.y"
                    {
                        yyVal = "elsif";
                    }
                    break;
                    case 193:
                        // line 935 "RubyParser.y"
                    {
                        yyVal = "end";
                    }
                    break;
                    case 194:
                        // line 938 "RubyParser.y"
                    {
                        yyVal = "ensure";
                    }
                    break;
                    case 195:
                        // line 941 "RubyParser.y"
                    {
                        yyVal = "false";
                    }
                    break;
                    case 196:
                        // line 944 "RubyParser.y"
                    {
                        yyVal = "for";
                    }
                    break;
                    case 197:
                        // line 947 "RubyParser.y"
                    {
                        yyVal = "in";
                    }
                    break;
                    case 198:
                        // line 950 "RubyParser.y"
                    {
                        yyVal = "module";
                    }
                    break;
                    case 199:
                        // line 953 "RubyParser.y"
                    {
                        yyVal = "next";
                    }
                    break;
                    case 200:
                        // line 956 "RubyParser.y"
                    {
                        yyVal = "nil";
                    }
                    break;
                    case 201:
                        // line 959 "RubyParser.y"
                    {
                        yyVal = "not";
                    }
                    break;
                    case 202:
                        // line 962 "RubyParser.y"
                    {
                        yyVal = "or";
                    }
                    break;
                    case 203:
                        // line 965 "RubyParser.y"
                    {
                        yyVal = "redo";
                    }
                    break;
                    case 204:
                        // line 968 "RubyParser.y"
                    {
                        yyVal = "rescue";
                    }
                    break;
                    case 205:
                        // line 971 "RubyParser.y"
                    {
                        yyVal = "retry";
                    }
                    break;
                    case 206:
                        // line 974 "RubyParser.y"
                    {
                        yyVal = "return";
                    }
                    break;
                    case 207:
                        // line 977 "RubyParser.y"
                    {
                        yyVal = "self";
                    }
                    break;
                    case 208:
                        // line 980 "RubyParser.y"
                    {
                        yyVal = "super";
                    }
                    break;
                    case 209:
                        // line 983 "RubyParser.y"
                    {
                        yyVal = "then";
                    }
                    break;
                    case 210:
                        // line 986 "RubyParser.y"
                    {
                        yyVal = "true";
                    }
                    break;
                    case 211:
                        // line 989 "RubyParser.y"
                    {
                        yyVal = "undef";
                    }
                    break;
                    case 212:
                        // line 992 "RubyParser.y"
                    {
                        yyVal = "when";
                    }
                    break;
                    case 213:
                        // line 995 "RubyParser.y"
                    {
                        yyVal = "yield";
                    }
                    break;
                    case 214:
                        // line 998 "RubyParser.y"
                    {
                        yyVal = "if";
                    }
                    break;
                    case 215:
                        // line 1001 "RubyParser.y"
                    {
                        yyVal = "unless";
                    }
                    break;
                    case 216:
                        // line 1004 "RubyParser.y"
                    {
                        yyVal = "signal";
                    }
                    break;
                    case 217:
                        // line 1007 "RubyParser.y"
                    {
                        yyVal = "signal emmit";
                    }
                    break;
                    case 218:
                        // line 1010 "RubyParser.y"
                    {
                        yyVal = "while";
                    }
                    break;
                    case 219:
                        // line 1013 "RubyParser.y"
                    {
                        yyVal = "until";
                    }
                    break;
                    case 220:
                        // line 1016 "RubyParser.y"
                    {
                        yyVal = "rescue";
                    }
                    break;
                    case 221:
                        // line 1020 "RubyParser.y"
                    {
                        yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    /* FIXME: Consider fixing node_assign itself rather than single case*/
                        ((Node)yyVal).setPosition(support.getPosition(((Node)yyVals[-2+yyTop])));
                    }
                    break;
                    case 222:
                        // line 1025 "RubyParser.y"
                    {
                        ISourcePosition position = support.getPosition(((Node)yyVals[-4+yyTop]));
                        Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
                        yyVal = support.node_assign(((Node)yyVals[-4+yyTop]), new RescueNode(position, ((Node)yyVals[-2+yyTop]), new RescueBodyNode(position, null, body, null), null));
                    }
                    break;
                    case 223:
                        // line 1030 "RubyParser.y"
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
                    case 224:
                        // line 1047 "RubyParser.y"
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
                    case 225:
                        // line 1067 "RubyParser.y"
                    {
  /* FIXME: arg_concat missing for opt_call_args*/
                        yyVal = support.new_opElementAsgnNode(((Node)yyVals[-5+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 226:
                        // line 1071 "RubyParser.y"
                    {
                        yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), ((String)yyVals[-2+yyTop]), ((String)yyVals[-1+yyTop]));
                    }
                    break;
                    case 227:
                        // line 1074 "RubyParser.y"
                    {
                        yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), ((String)yyVals[-2+yyTop]), ((String)yyVals[-1+yyTop]));
                    }
                    break;
                    case 228:
                        // line 1077 "RubyParser.y"
                    {
                        yyVal = new OpAsgnNode(support.getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), ((String)yyVals[-2+yyTop]), ((String)yyVals[-1+yyTop]));
                    }
                    break;
                    case 229:
                        // line 1080 "RubyParser.y"
                    {
                        support.yyerror("constant re-assignment");
                    }
                    break;
                    case 230:
                        // line 1083 "RubyParser.y"
                    {
                        support.yyerror("constant re-assignment");
                    }
                    break;
                    case 231:
                        // line 1086 "RubyParser.y"
                    {
                        support.backrefAssignError(((Node)yyVals[-2+yyTop]));
                    }
                    break;
                    case 232:
                        // line 1089 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[-2+yyTop]));
                        support.checkExpression(((Node)yyVals[0+yyTop]));

                        boolean isLiteral = ((Node)yyVals[-2+yyTop]) instanceof FixnumNode && ((Node)yyVals[0+yyTop]) instanceof FixnumNode;
                        yyVal = new DotNode(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]), false, isLiteral);
                    }
                    break;
                    case 233:
                        // line 1096 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[-2+yyTop]));
                        support.checkExpression(((Node)yyVals[0+yyTop]));

                        boolean isLiteral = ((Node)yyVals[-2+yyTop]) instanceof FixnumNode && ((Node)yyVals[0+yyTop]) instanceof FixnumNode;
                        yyVal = new DotNode(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]), true, isLiteral);
                    }
                    break;
                    case 234:
                        // line 1103 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "+", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 235:
                        // line 1106 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "-", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 236:
                        // line 1109 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "*", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 237:
                        // line 1112 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "/", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 238:
                        // line 1115 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "%", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 239:
                        // line 1118 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 240:
                        // line 1121 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(support.getOperatorCallNode(((NumericNode)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), lexer.getPosition()), "-@");
                    }
                    break;
                    case 241:
                        // line 1124 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "+@");
                    }
                    break;
                    case 242:
                        // line 1127 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "-@");
                    }
                    break;
                    case 243:
                        // line 1130 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "|", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 244:
                        // line 1133 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "^", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 245:
                        // line 1136 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "&", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 246:
                        // line 1139 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<=>", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 247:
                        // line 1142 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 248:
                        // line 1145 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">=", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 249:
                        // line 1148 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 250:
                        // line 1151 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<=", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 251:
                        // line 1154 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "==", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 252:
                        // line 1157 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "===", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 253:
                        // line 1160 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "!=", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 254:
                        // line 1163 "RubyParser.y"
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
                    case 255:
                        // line 1172 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "!~", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 256:
                        // line 1175 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[0+yyTop])), "!");
                    }
                    break;
                    case 257:
                        // line 1178 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "~");
                    }
                    break;
                    case 258:
                        // line 1181 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<<", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 259:
                        // line 1184 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">>", ((Node)yyVals[0+yyTop]), lexer.getPosition());
                    }
                    break;
                    case 260:
                        // line 1187 "RubyParser.y"
                    {
                        yyVal = support.newAndNode(((Node)yyVals[-2+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 261:
                        // line 1190 "RubyParser.y"
                    {
                        yyVal = support.newOrNode(((Node)yyVals[-2+yyTop]).getPosition(), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 262:
                        // line 1193 "RubyParser.y"
                    {
                        yyVal = support.new_defined(((ISourcePosition)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 263:
                        // line 1196 "RubyParser.y"
                    {
                        yyVal = new IfNode(support.getPosition(((Node)yyVals[-5+yyTop])), support.getConditionNode(((Node)yyVals[-5+yyTop])), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 264:
                        // line 1199 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 265:
                        // line 1203 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[0+yyTop]));
                        yyVal = ((Node)yyVals[0+yyTop]) != null ? ((Node)yyVals[0+yyTop]) : NilImplicitNode.NIL;
                    }
                    break;
                    case 267:
                        // line 1209 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 268:
                        // line 1212 "RubyParser.y"
                    {
                        yyVal = support.arg_append(((Node)yyVals[-3+yyTop]), ((HashNode)yyVals[-1+yyTop]));
                    }
                    break;
                    case 269:
                        // line 1215 "RubyParser.y"
                    {
                        yyVal = support.newArrayNode(((HashNode)yyVals[-1+yyTop]).getPosition(), ((HashNode)yyVals[-1+yyTop]));
                    }
                    break;
                    case 270:
                        // line 1219 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                        if (yyVal != null) ((Node)yyVal).setPosition(((ISourcePosition)yyVals[-2+yyTop]));
                    }
                    break;
                    case 275:
                        // line 1228 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 276:
                        // line 1231 "RubyParser.y"
                    {
                        yyVal = support.arg_append(((Node)yyVals[-3+yyTop]), ((HashNode)yyVals[-1+yyTop]));
                    }
                    break;
                    case 277:
                        // line 1234 "RubyParser.y"
                    {
                        yyVal = support.newArrayNode(((HashNode)yyVals[-1+yyTop]).getPosition(), ((HashNode)yyVals[-1+yyTop]));
                    }
                    break;
                    case 278:
                        // line 1240 "RubyParser.y"
                    {
                        yyVal = support.newArrayNode(support.getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 279:
                        // line 1243 "RubyParser.y"
                    {
                        yyVal = support.arg_blk_pass(((Node)yyVals[-1+yyTop]), ((BlockPassNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 280:
                        // line 1246 "RubyParser.y"
                    {
                        yyVal = support.newArrayNode(((HashNode)yyVals[-1+yyTop]).getPosition(), ((HashNode)yyVals[-1+yyTop]));
                        yyVal = support.arg_blk_pass((Node)yyVal, ((BlockPassNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 281:
                        // line 1250 "RubyParser.y"
                    {
                        yyVal = support.arg_append(((Node)yyVals[-3+yyTop]), ((HashNode)yyVals[-1+yyTop]));
                        yyVal = support.arg_blk_pass((Node)yyVal, ((BlockPassNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 282:
                        // line 1254 "RubyParser.y"
                    {
                    }
                    break;
                    case 283:
                        // line 1257 "RubyParser.y"
                    {
                        yyVal = Long.valueOf(lexer.getCmdArgumentState().begin());
                    }
                    break;
                    case 284:
                        // line 1259 "RubyParser.y"
                    {
                        lexer.getCmdArgumentState().reset(((Long)yyVals[-1+yyTop]).longValue());
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 285:
                        // line 1264 "RubyParser.y"
                    {
                        yyVal = new BlockPassNode(support.getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 286:
                        // line 1268 "RubyParser.y"
                    {
                        yyVal = ((BlockPassNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 288:
                        // line 1274 "RubyParser.y"
                    { /* ArrayNode*/
                        ISourcePosition pos = ((Node)yyVals[0+yyTop]) == null ? lexer.getPosition() : ((Node)yyVals[0+yyTop]).getPosition();
                        yyVal = support.newArrayNode(pos, ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 289:
                        // line 1278 "RubyParser.y"
                    { /* SplatNode*/
                        yyVal = support.newSplatNode(support.getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 290:
                        // line 1281 "RubyParser.y"
                    { /* ArgsCatNode, SplatNode, ArrayNode*/
                        Node node = support.splat_array(((Node)yyVals[-2+yyTop]));

                        if (node != null) {
                            yyVal = support.list_append(node, ((Node)yyVals[0+yyTop]));
                        } else {
                            yyVal = support.arg_append(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                        }
                    }
                    break;
                    case 291:
                        // line 1290 "RubyParser.y"
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
                    case 292:
                        // line 1302 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 293:
                        // line 1305 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 294:
                        // line 1310 "RubyParser.y"
                    {
                        Node node = support.splat_array(((Node)yyVals[-2+yyTop]));

                        if (node != null) {
                            yyVal = support.list_append(node, ((Node)yyVals[0+yyTop]));
                        } else {
                            yyVal = support.arg_append(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                        }
                    }
                    break;
                    case 295:
                        // line 1319 "RubyParser.y"
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
                    case 296:
                        // line 1329 "RubyParser.y"
                    {
                        yyVal = support.newSplatNode(support.getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 303:
                        // line 1339 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[0+yyTop]); /* FIXME: Why complaining without $$ = $1;*/
                    }
                    break;
                    case 304:
                        // line 1342 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[0+yyTop]); /* FIXME: Why complaining without $$ = $1;*/
                    }
                    break;
                    case 307:
                        // line 1347 "RubyParser.y"
                    {
                        yyVal = support.new_fcall(((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 308:
                        // line 1350 "RubyParser.y"
                    {
                        yyVal = new BeginNode(((ISourcePosition)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 309:
                        // line 1353 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_ENDARG);
                    }
                    break;
                    case 310:
                        // line 1355 "RubyParser.y"
                    {
                        yyVal = null; /*FIXME: Should be implicit nil?*/
                    }
                    break;
                    case 311:
                        // line 1358 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_ENDARG);
                    }
                    break;
                    case 312:
                        // line 1360 "RubyParser.y"
                    {
                        if (Options.PARSER_WARN_GROUPED_EXPRESSIONS.load()) {
                            support.warning(ID.GROUPED_EXPRESSION, ((ISourcePosition)yyVals[-3+yyTop]), "(...) interpreted as grouped expression");
                        }
                        yyVal = ((Node)yyVals[-2+yyTop]);
                    }
                    break;
                    case 313:
                        // line 1366 "RubyParser.y"
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
                    case 314:
                        // line 1375 "RubyParser.y"
                    {
                        yyVal = support.new_colon2(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 315:
                        // line 1378 "RubyParser.y"
                    {
                        yyVal = support.new_colon3(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 316:
                        // line 1381 "RubyParser.y"
                    {
                        ISourcePosition position = support.getPosition(((Node)yyVals[-1+yyTop]));
                        if (((Node)yyVals[-1+yyTop]) == null) {
                            yyVal = new ZArrayNode(position); /* zero length array */
                        } else {
                            yyVal = ((Node)yyVals[-1+yyTop]);
                        }
                    }
                    break;
                    case 317:
                        // line 1389 "RubyParser.y"
                    {
                        yyVal = ((HashNode)yyVals[-1+yyTop]);
                    }
                    break;
                    case 318:
                        // line 1392 "RubyParser.y"
                    {
                        yyVal = new ReturnNode(((ISourcePosition)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 319:
                        // line 1395 "RubyParser.y"
                    {
                        yyVal = support.new_yield(((ISourcePosition)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 320:
                        // line 1398 "RubyParser.y"
                    {
                        yyVal = new ZYieldNode(((ISourcePosition)yyVals[-2+yyTop]));
                    }
                    break;
                    case 321:
                        // line 1401 "RubyParser.y"
                    {
                        yyVal = new ZYieldNode(((ISourcePosition)yyVals[0+yyTop]));
                    }
                    break;
                    case 322:
                        // line 1404 "RubyParser.y"
                    {
                        yyVal = support.new_defined(((ISourcePosition)yyVals[-4+yyTop]), ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 323:
                        // line 1407 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[-1+yyTop])), "!");
                    }
                    break;
                    case 324:
                        // line 1410 "RubyParser.y"
                    {
                        yyVal = support.getOperatorCallNode(NilImplicitNode.NIL, "!");
                    }
                    break;
                    case 325:
                        // line 1413 "RubyParser.y"
                    {
                        support.frobnicate_fcall_args(((FCallNode)yyVals[-1+yyTop]), null, ((IterNode)yyVals[0+yyTop]));
                        yyVal = ((FCallNode)yyVals[-1+yyTop]);
                    }
                    break;
                    case 327:
                        // line 1418 "RubyParser.y"
                    {
                        if (((Node)yyVals[-1+yyTop]) != null &&
                                ((BlockAcceptingNode)yyVals[-1+yyTop]).getIterNode() instanceof BlockPassNode) {
                            throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, ((Node)yyVals[-1+yyTop]).getPosition(), lexer.getCurrentLine(), "Both block arg and actual block given.");
                        }
                        yyVal = ((BlockAcceptingNode)yyVals[-1+yyTop]).setIterNode(((IterNode)yyVals[0+yyTop]));
                        ((Node)yyVal).setPosition(((Node)yyVals[-1+yyTop]).getPosition());
                    }
                    break;
                    case 328:
                        // line 1426 "RubyParser.y"
                    {
                        yyVal = ((LambdaNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 329:
                        // line 1429 "RubyParser.y"
                    {
                        yyVal = new IfNode(((ISourcePosition)yyVals[-5+yyTop]), support.getConditionNode(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 330:
                        // line 1432 "RubyParser.y"
                    {
                        yyVal = new IfNode(((ISourcePosition)yyVals[-5+yyTop]), support.getConditionNode(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[-2+yyTop]));
                    }
                    break;
                    case 331:
                        // line 1435 "RubyParser.y"
                    {
                        lexer.getConditionState().begin();
                    }
                    break;
                    case 332:
                        // line 1437 "RubyParser.y"
                    {
                        lexer.getConditionState().end();
                    }
                    break;
                    case 333:
                        // line 1439 "RubyParser.y"
                    {
                        Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
                        yyVal = new WhileNode(((ISourcePosition)yyVals[-6+yyTop]), support.getConditionNode(((Node)yyVals[-4+yyTop])), body);
                    }
                    break;
                    case 334:
                        // line 1443 "RubyParser.y"
                    {
                        lexer.getConditionState().begin();
                    }
                    break;
                    case 335:
                        // line 1445 "RubyParser.y"
                    {
                        lexer.getConditionState().end();
                    }
                    break;
                    case 336:
                        // line 1447 "RubyParser.y"
                    {
                        Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
                        yyVal = new UntilNode(((ISourcePosition)yyVals[-6+yyTop]), support.getConditionNode(((Node)yyVals[-4+yyTop])), body);
                    }
                    break;
                    case 337:
                        // line 1451 "RubyParser.y"
                    {
                        yyVal = support.newCaseNode(((ISourcePosition)yyVals[-4+yyTop]), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 338:
                        // line 1454 "RubyParser.y"
                    {
                        yyVal = support.newCaseNode(((ISourcePosition)yyVals[-3+yyTop]), null, ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 339:
                        // line 1457 "RubyParser.y"
                    {
                        lexer.getConditionState().begin();
                    }
                    break;
                    case 340:
                        // line 1459 "RubyParser.y"
                    {
                        lexer.getConditionState().end();
                    }
                    break;
                    case 341:
                        // line 1461 "RubyParser.y"
                    {
                      /* ENEBO: Lots of optz in 1.9 parser here*/
                        yyVal = new ForNode(((ISourcePosition)yyVals[-8+yyTop]), ((Node)yyVals[-7+yyTop]), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[-4+yyTop]), support.getCurrentScope());
                    }
                    break;
                    case 342:
                        // line 1465 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) {
                            support.yyerror("class definition in method body");
                        }
                        support.pushLocalScope();
                    }
                    break;
                    case 343:
                        // line 1470 "RubyParser.y"
                    {
                        Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);

                        yyVal = new ClassNode(((ISourcePosition)yyVals[-5+yyTop]), ((Colon3Node)yyVals[-4+yyTop]), support.getCurrentScope(), body, ((Node)yyVals[-3+yyTop]));
                        support.popCurrentScope();
                    }
                    break;
                    case 344:
                        // line 1476 "RubyParser.y"
                    {
                        yyVal = Boolean.valueOf(support.isInDef());
                        support.setInDef(false);
                    }
                    break;
                    case 345:
                        // line 1479 "RubyParser.y"
                    {
                        yyVal = Integer.valueOf(support.getInSingle());
                        support.setInSingle(0);
                        support.pushLocalScope();
                    }
                    break;
                    case 346:
                        // line 1483 "RubyParser.y"
                    {
                        Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);

                        yyVal = new SClassNode(((ISourcePosition)yyVals[-7+yyTop]), ((Node)yyVals[-5+yyTop]), support.getCurrentScope(), body);
                        support.popCurrentScope();
                        support.setInDef(((Boolean)yyVals[-4+yyTop]).booleanValue());
                        support.setInSingle(((Integer)yyVals[-2+yyTop]).intValue());
                    }
                    break;
                    case 347:
                        // line 1491 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) {
                            support.yyerror("module definition in method body");
                        }
                        support.pushLocalScope();
                    }
                    break;
                    case 348:
                        // line 1496 "RubyParser.y"
                    {
                        Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);

                        yyVal = new ModuleNode(((ISourcePosition)yyVals[-4+yyTop]), ((Colon3Node)yyVals[-3+yyTop]), support.getCurrentScope(), body);
                        support.popCurrentScope();
                    }
                    break;
                    case 349:
                        // line 1502 "RubyParser.y"
                    {
                        support.setInDef(true);
                        support.pushLocalScope();
                    }
                    break;
                    case 350:
                        // line 1505 "RubyParser.y"
                    {
                        Node body = ((Node)yyVals[-1+yyTop]);
                        if (body == null) body = NilImplicitNode.NIL;

                        yyVal = new DefnNode(((ISourcePosition)yyVals[-5+yyTop]), new ArgumentNode(((ISourcePosition)yyVals[-5+yyTop]), ((String)yyVals[-4+yyTop])), (ArgsNode) yyVals[-2+yyTop], support.getCurrentScope(), body);
                        support.popCurrentScope();
                        support.setInDef(false);
                    }
                    break;
                    case 351:
                        // line 1513 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_FNAME);
                    }
                    break;
                    case 352:
                        // line 1515 "RubyParser.y"
                    {
                        support.setInSingle(support.getInSingle() + 1);
                        support.pushLocalScope();
                        lexer.setState(LexState.EXPR_ENDFN); /* force for args */
                    }
                    break;
                    case 353:
                        // line 1519 "RubyParser.y"
                    {
                        Node body = ((Node)yyVals[-1+yyTop]);
                        if (body == null) body = NilImplicitNode.NIL;

                        yyVal = new DefsNode(((ISourcePosition)yyVals[-8+yyTop]), ((Node)yyVals[-7+yyTop]), new ArgumentNode(((ISourcePosition)yyVals[-8+yyTop]), ((String)yyVals[-4+yyTop])), (ArgsNode) yyVals[-2+yyTop], support.getCurrentScope(), body);
                        support.popCurrentScope();
                        support.setInSingle(support.getInSingle() - 1);
                    }
                    break;
                    case 354:
                        // line 1527 "RubyParser.y"
                    {
                        yyVal = new BreakNode(((ISourcePosition)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 355:
                        // line 1530 "RubyParser.y"
                    {
                        yyVal = new NextNode(((ISourcePosition)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 356:
                        // line 1533 "RubyParser.y"
                    {
                        yyVal = new RedoNode(((ISourcePosition)yyVals[0+yyTop]));
                    }
                    break;
                    case 357:
                        // line 1536 "RubyParser.y"
                    {
                        yyVal = new RetryNode(((ISourcePosition)yyVals[0+yyTop]));
                    }
                    break;
                    case 358:
                        // line 1539 "RubyParser.y"
                    {
                        support.pushBlockScope();
                    }
                    break;
                    case 359:
                        // line 1541 "RubyParser.y"
                    {
                        yyVal = support.signal_assign(((ISourcePosition)yyVals[-3+yyTop]),((Node)yyVals[-1+yyTop]));
                        support.popCurrentScope();
                    }
                    break;
                    case 360:
                        // line 1545 "RubyParser.y"
                    {
                        yyVal = support.signal_emit(((ISourcePosition)yyVals[-2+yyTop]),((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 361:
                        // line 1556 "RubyParser.y"
                    {
                        support.checkExpression(((Node)yyVals[0+yyTop]));
                        yyVal = ((Node)yyVals[0+yyTop]);
                        if (yyVal == null) yyVal = NilImplicitNode.NIL;
                    }
                    break;
                    case 368:
                        // line 1570 "RubyParser.y"
                    {
                        yyVal = new IfNode(((ISourcePosition)yyVals[-4+yyTop]), support.getConditionNode(((Node)yyVals[-3+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 370:
                        // line 1575 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 372:
                        // line 1580 "RubyParser.y"
                    {
                    }
                    break;
                    case 373:
                        // line 1583 "RubyParser.y"
                    {
                        yyVal = support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 374:
                        // line 1586 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 375:
                        // line 1591 "RubyParser.y"
                    {
                        yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 376:
                        // line 1594 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 377:
                        // line 1598 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[0+yyTop]).getPosition(), ((ListNode)yyVals[0+yyTop]), null, null);
                    }
                    break;
                    case 378:
                        // line 1601 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), null), null);
                    }
                    break;
                    case 379:
                        // line 1604 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), support.assignableLabelOrIdentifier(((String)yyVals[-2+yyTop]), null), ((ListNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 380:
                        // line 1607 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-2+yyTop]).getPosition(), ((ListNode)yyVals[-2+yyTop]), new StarNode(lexer.getPosition()), null);
                    }
                    break;
                    case 381:
                        // line 1610 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-4+yyTop]).getPosition(), ((ListNode)yyVals[-4+yyTop]), new StarNode(lexer.getPosition()), ((ListNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 382:
                        // line 1613 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(lexer.getPosition(), null, support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), null), null);
                    }
                    break;
                    case 383:
                        // line 1616 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(lexer.getPosition(), null, support.assignableLabelOrIdentifier(((String)yyVals[-2+yyTop]), null), ((ListNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 384:
                        // line 1619 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(lexer.getPosition(), null, new StarNode(lexer.getPosition()), null);
                    }
                    break;
                    case 385:
                        // line 1622 "RubyParser.y"
                    {
                        yyVal = new MultipleAsgn19Node(support.getPosition(((ListNode)yyVals[0+yyTop])), null, null, ((ListNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 386:
                        // line 1626 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 387:
                        // line 1629 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 388:
                        // line 1632 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(lexer.getPosition(), null, ((String)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 389:
                        // line 1635 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(((BlockArgNode)yyVals[0+yyTop]).getPosition(), null, null, ((BlockArgNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 390:
                        // line 1639 "RubyParser.y"
                    {
                        yyVal = ((ArgsTailHolder)yyVals[0+yyTop]);
                    }
                    break;
                    case 391:
                        // line 1642 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(lexer.getPosition(), null, null, null);
                    }
                    break;
                    case 392:
                        // line 1647 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 393:
                        // line 1650 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-7+yyTop]).getPosition(), ((ListNode)yyVals[-7+yyTop]), ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 394:
                        // line 1653 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 395:
                        // line 1656 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 396:
                        // line 1659 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), null, ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 397:
                        // line 1662 "RubyParser.y"
                    {
                        RestArgNode rest = new UnnamedRestArgNode(((ListNode)yyVals[-1+yyTop]).getPosition(), null, support.getCurrentScope().addVariable("*"));
                        yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, rest, null, (ArgsTailHolder) null);
                    }
                    break;
                    case 398:
                        // line 1666 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 399:
                        // line 1669 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 400:
                        // line 1672 "RubyParser.y"
                    {
                        yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-3+yyTop])), null, ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 401:
                        // line 1675 "RubyParser.y"
                    {
                        yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-5+yyTop])), null, ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 402:
                        // line 1678 "RubyParser.y"
                    {
                        yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-1+yyTop])), null, ((ListNode)yyVals[-1+yyTop]), null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 403:
                        // line 1681 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 404:
                        // line 1684 "RubyParser.y"
                    {
                        yyVal = support.new_args(((RestArgNode)yyVals[-1+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 405:
                        // line 1687 "RubyParser.y"
                    {
                        yyVal = support.new_args(((RestArgNode)yyVals[-3+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 406:
                        // line 1690 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ArgsTailHolder)yyVals[0+yyTop]).getPosition(), null, null, null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 407:
                        // line 1694 "RubyParser.y"
                    {
    /* was $$ = null;*/
                        yyVal = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
                    }
                    break;
                    case 408:
                        // line 1698 "RubyParser.y"
                    {
                        lexer.commandStart = true;
                        yyVal = ((ArgsNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 409:
                        // line 1703 "RubyParser.y"
                    {
                        yyVal = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
                    }
                    break;
                    case 410:
                        // line 1706 "RubyParser.y"
                    {
                        yyVal = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
                    }
                    break;
                    case 411:
                        // line 1709 "RubyParser.y"
                    {
                        yyVal = ((ArgsNode)yyVals[-2+yyTop]);
                    }
                    break;
                    case 412:
                        // line 1714 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 413:
                        // line 1717 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 414:
                        // line 1722 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 415:
                        // line 1725 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 416:
                        // line 1729 "RubyParser.y"
                    {
                        support.new_bv(((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 417:
                        // line 1732 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 418:
                        // line 1736 "RubyParser.y"
                    {
                        support.pushBlockScope();
                        yyVal = lexer.getLeftParenBegin();
                        lexer.setLeftParenBegin(lexer.incrementParenNest());
                    }
                    break;
                    case 419:
                        // line 1740 "RubyParser.y"
                    {
                        yyVal = new LambdaNode(((ArgsNode)yyVals[-1+yyTop]).getPosition(), ((ArgsNode)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), support.getCurrentScope());
                        support.popCurrentScope();
                        lexer.setLeftParenBegin(((Integer)yyVals[-2+yyTop]));
                    }
                    break;
                    case 420:
                        // line 1746 "RubyParser.y"
                    {
                        yyVal = ((ArgsNode)yyVals[-2+yyTop]);
                    }
                    break;
                    case 421:
                        // line 1749 "RubyParser.y"
                    {
                        yyVal = ((ArgsNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 422:
                        // line 1753 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 423:
                        // line 1756 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 424:
                        // line 1760 "RubyParser.y"
                    {
                        support.pushBlockScope();
                    }
                    break;
                    case 425:
                        // line 1762 "RubyParser.y"
                    {
                        yyVal = new IterNode(((ISourcePosition)yyVals[-4+yyTop]), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                        support.popCurrentScope();
                    }
                    break;
                    case 426:
                        // line 1771 "RubyParser.y"
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
                    case 427:
                        // line 1787 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 428:
                        // line 1790 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((String)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 429:
                        // line 1793 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((String)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 430:
                        // line 1798 "RubyParser.y"
                    {
                        support.frobnicate_fcall_args(((FCallNode)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                        yyVal = ((FCallNode)yyVals[-1+yyTop]);
                    }
                    break;
                    case 431:
                        // line 1802 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 432:
                        // line 1805 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 433:
                        // line 1808 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]), null, null);
                    }
                    break;
                    case 434:
                        // line 1811 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-2+yyTop]), "call", ((Node)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 435:
                        // line 1814 "RubyParser.y"
                    {
                        yyVal = support.new_call(((Node)yyVals[-2+yyTop]), "call", ((Node)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 436:
                        // line 1817 "RubyParser.y"
                    {
                        yyVal = support.new_super(((ISourcePosition)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 437:
                        // line 1820 "RubyParser.y"
                    {
                        yyVal = new ZSuperNode(((ISourcePosition)yyVals[0+yyTop]));
                    }
                    break;
                    case 438:
                        // line 1823 "RubyParser.y"
                    {
                        if (((Node)yyVals[-3+yyTop]) instanceof SelfNode) {
                            yyVal = support.new_fcall("[]");
                            support.frobnicate_fcall_args(((FCallNode)yyVal), ((Node)yyVals[-1+yyTop]), null);
                        } else {
                            yyVal = support.new_call(((Node)yyVals[-3+yyTop]), "[]", ((Node)yyVals[-1+yyTop]), null);
                        }
                    }
                    break;
                    case 439:
                        // line 1832 "RubyParser.y"
                    {
                        support.pushBlockScope();
                    }
                    break;
                    case 440:
                        // line 1834 "RubyParser.y"
                    {
                        yyVal = new IterNode(((ISourcePosition)yyVals[-4+yyTop]), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                        support.popCurrentScope();
                    }
                    break;
                    case 441:
                        // line 1838 "RubyParser.y"
                    {
                        support.pushBlockScope();
                    }
                    break;
                    case 442:
                        // line 1840 "RubyParser.y"
                    {
                        yyVal = new IterNode(((ISourcePosition)yyVals[-4+yyTop]), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                        support.popCurrentScope();
                    }
                    break;
                    case 443:
                        // line 1845 "RubyParser.y"
                    {
                        yyVal = support.newWhenNode(((ISourcePosition)yyVals[-4+yyTop]), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 446:
                        // line 1851 "RubyParser.y"
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
                    case 447:
                        // line 1864 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 448:
                        // line 1868 "RubyParser.y"
                    {
                        yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 449:
                        // line 1871 "RubyParser.y"
                    {
                        yyVal = support.splat_array(((Node)yyVals[0+yyTop]));
                        if (yyVal == null) yyVal = ((Node)yyVals[0+yyTop]); /* ArgsCat or ArgsPush*/
                    }
                    break;
                    case 451:
                        // line 1877 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 453:
                        // line 1882 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 455:
                        // line 1887 "RubyParser.y"
                    {
                        yyVal = ((NumericNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 456:
                        // line 1890 "RubyParser.y"
                    {
                        yyVal = new SymbolNode(lexer.getPosition(), new ByteList(((String)yyVals[0+yyTop]).getBytes(), lexer.getEncoding()));
                    }
                    break;
                    case 458:
                        // line 1895 "RubyParser.y"
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
                    case 459:
                        // line 1909 "RubyParser.y"
                    {
                        yyVal = ((StrNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 460:
                        // line 1912 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 461:
                        // line 1915 "RubyParser.y"
                    {
                        yyVal = support.literal_concat(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 462:
                        // line 1919 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 463:
                        // line 1923 "RubyParser.y"
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
                    case 464:
                        // line 1939 "RubyParser.y"
                    {
                        yyVal = support.newRegexpNode(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), (RegexpNode) ((RegexpNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 465:
                        // line 1943 "RubyParser.y"
                    {
                        yyVal = new ZArrayNode(lexer.getPosition());
                    }
                    break;
                    case 466:
                        // line 1946 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-1+yyTop]);
                    }
                    break;
                    case 467:
                        // line 1950 "RubyParser.y"
                    {
                        yyVal = new ArrayNode(lexer.getPosition());
                    }
                    break;
                    case 468:
                        // line 1953 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]) instanceof EvStrNode ? new DStrNode(((ListNode)yyVals[-2+yyTop]).getPosition(), lexer.getEncoding()).add(((Node)yyVals[-1+yyTop])) : ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 469:
                        // line 1957 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 470:
                        // line 1960 "RubyParser.y"
                    {
                        yyVal = support.literal_concat(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 471:
                        // line 1964 "RubyParser.y"
                    {
                        yyVal = new ArrayNode(lexer.getPosition());
                    }
                    break;
                    case 472:
                        // line 1967 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-1+yyTop]);
                    }
                    break;
                    case 473:
                        // line 1971 "RubyParser.y"
                    {
                        yyVal = new ArrayNode(lexer.getPosition());
                    }
                    break;
                    case 474:
                        // line 1974 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]) instanceof EvStrNode ? new DSymbolNode(((ListNode)yyVals[-2+yyTop]).getPosition()).add(((Node)yyVals[-1+yyTop])) : support.asSymbol(((ListNode)yyVals[-2+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop])));
                    }
                    break;
                    case 475:
                        // line 1978 "RubyParser.y"
                    {
                        yyVal = new ZArrayNode(lexer.getPosition());
                    }
                    break;
                    case 476:
                        // line 1981 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-1+yyTop]);
                    }
                    break;
                    case 477:
                        // line 1985 "RubyParser.y"
                    {
                        yyVal = new ZArrayNode(lexer.getPosition());
                    }
                    break;
                    case 478:
                        // line 1988 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-1+yyTop]);
                    }
                    break;
                    case 479:
                        // line 1993 "RubyParser.y"
                    {
                        yyVal = new ArrayNode(lexer.getPosition());
                    }
                    break;
                    case 480:
                        // line 1996 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 481:
                        // line 2000 "RubyParser.y"
                    {
                        yyVal = new ArrayNode(lexer.getPosition());
                    }
                    break;
                    case 482:
                        // line 2003 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-2+yyTop]).add(support.asSymbol(((ListNode)yyVals[-2+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop])));
                    }
                    break;
                    case 483:
                        // line 2007 "RubyParser.y"
                    {
                        ByteList aChar = ByteList.create("");
                        aChar.setEncoding(lexer.getEncoding());
                        yyVal = lexer.createStrNode(lexer.getPosition(), aChar, 0);
                    }
                    break;
                    case 484:
                        // line 2012 "RubyParser.y"
                    {
                        yyVal = support.literal_concat(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 485:
                        // line 2016 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 486:
                        // line 2019 "RubyParser.y"
                    {
                        yyVal = support.literal_concat(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 487:
                        // line 2023 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 488:
                        // line 2026 "RubyParser.y"
                    {
    /* FIXME: mri is different here.*/
                        yyVal = support.literal_concat(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 489:
                        // line 2031 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 490:
                        // line 2034 "RubyParser.y"
                    {
                        yyVal = lexer.getStrTerm();
                        lexer.setStrTerm(null);
                        lexer.setState(LexState.EXPR_BEG);
                    }
                    break;
                    case 491:
                        // line 2038 "RubyParser.y"
                    {
                        lexer.setStrTerm(((StrTerm)yyVals[-1+yyTop]));
                        yyVal = new EvStrNode(support.getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 492:
                        // line 2042 "RubyParser.y"
                    {
                        yyVal = lexer.getStrTerm();
                        lexer.setStrTerm(null);
                        lexer.getConditionState().stop();
                        lexer.getCmdArgumentState().stop();
                    }
                    break;
                    case 493:
                        // line 2047 "RubyParser.y"
                    {
                        yyVal = lexer.getState();
                        lexer.setState(LexState.EXPR_BEG);
                    }
                    break;
                    case 494:
                        // line 2050 "RubyParser.y"
                    {
                        lexer.getConditionState().restart();
                        lexer.getCmdArgumentState().restart();
                        lexer.setStrTerm(((StrTerm)yyVals[-3+yyTop]));
                        lexer.setState(((LexState)yyVals[-2+yyTop]));

                        yyVal = support.newEvStrNode(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]));
                    }
                    break;
                    case 495:
                        // line 2059 "RubyParser.y"
                    {
                        yyVal = new GlobalVarNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 496:
                        // line 2062 "RubyParser.y"
                    {
                        yyVal = new InstVarNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 497:
                        // line 2065 "RubyParser.y"
                    {
                        yyVal = new ClassVarNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 499:
                        // line 2071 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_END);
                        yyVal = ((String)yyVals[0+yyTop]);
                    }
                    break;
                    case 504:
                        // line 2079 "RubyParser.y"
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
                    case 505:
                        // line 2098 "RubyParser.y"
                    {
                        yyVal = ((NumericNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 506:
                        // line 2101 "RubyParser.y"
                    {
                        yyVal = support.negateNumeric(((NumericNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 507:
                        // line 2105 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 508:
                        // line 2108 "RubyParser.y"
                    {
                        yyVal = ((FloatNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 509:
                        // line 2111 "RubyParser.y"
                    {
                        yyVal = ((RationalNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 510:
                        // line 2114 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 511:
                        // line 2119 "RubyParser.y"
                    {
                        yyVal = support.declareIdentifier(((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 512:
                        // line 2122 "RubyParser.y"
                    {
                        yyVal = new InstVarNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 513:
                        // line 2125 "RubyParser.y"
                    {
                        yyVal = new GlobalVarNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 514:
                        // line 2128 "RubyParser.y"
                    {
                        yyVal = new ConstNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 515:
                        // line 2131 "RubyParser.y"
                    {
                        yyVal = new ClassVarNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 516:
                        // line 2134 "RubyParser.y"
                    {
                        yyVal = new NilNode(lexer.getPosition());
                    }
                    break;
                    case 517:
                        // line 2137 "RubyParser.y"
                    {
                        yyVal = new SelfNode(lexer.getPosition());
                    }
                    break;
                    case 518:
                        // line 2140 "RubyParser.y"
                    {
                        yyVal = new TrueNode(lexer.getPosition());
                    }
                    break;
                    case 519:
                        // line 2143 "RubyParser.y"
                    {
                        yyVal = new FalseNode(lexer.getPosition());
                    }
                    break;
                    case 520:
                        // line 2146 "RubyParser.y"
                    {
                        yyVal = new FileNode(lexer.getPosition(), new ByteList(lexer.getPosition().getFile().getBytes(),
                                support.getConfiguration().getRuntime().getEncodingService().getLocaleEncoding()));
                    }
                    break;
                    case 521:
                        // line 2150 "RubyParser.y"
                    {
                        yyVal = new FixnumNode(lexer.getPosition(), lexer.tokline.getLine()+1);
                    }
                    break;
                    case 522:
                        // line 2153 "RubyParser.y"
                    {
                        yyVal = new EncodingNode(lexer.getPosition(), lexer.getEncoding());
                    }
                    break;
                    case 523:
                        // line 2158 "RubyParser.y"
                    {
                        yyVal = support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), null);
                    }
                    break;
                    case 524:
                        // line 2161 "RubyParser.y"
                    {
                        yyVal = new InstAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 525:
                        // line 2164 "RubyParser.y"
                    {
                        yyVal = new GlobalAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 526:
                        // line 2167 "RubyParser.y"
                    {
                        if (support.isInDef() || support.isInSingle()) support.compile_error("dynamic constant assignment");

                        yyVal = new ConstDeclNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), null, NilImplicitNode.NIL);
                    }
                    break;
                    case 527:
                        // line 2172 "RubyParser.y"
                    {
                        yyVal = new ClassVarAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
                    }
                    break;
                    case 528:
                        // line 2175 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to nil");
                        yyVal = null;
                    }
                    break;
                    case 529:
                        // line 2179 "RubyParser.y"
                    {
                        support.compile_error("Can't change the value of self");
                        yyVal = null;
                    }
                    break;
                    case 530:
                        // line 2183 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to true");
                        yyVal = null;
                    }
                    break;
                    case 531:
                        // line 2187 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to false");
                        yyVal = null;
                    }
                    break;
                    case 532:
                        // line 2191 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to __FILE__");
                        yyVal = null;
                    }
                    break;
                    case 533:
                        // line 2195 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to __LINE__");
                        yyVal = null;
                    }
                    break;
                    case 534:
                        // line 2199 "RubyParser.y"
                    {
                        support.compile_error("Can't assign to __ENCODING__");
                        yyVal = null;
                    }
                    break;
                    case 535:
                        // line 2205 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 536:
                        // line 2208 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 537:
                        // line 2212 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 538:
                        // line 2215 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_BEG);
                    }
                    break;
                    case 539:
                        // line 2217 "RubyParser.y"
                    {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                    }
                    break;
                    case 540:
                        // line 2220 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 541:
                        // line 2226 "RubyParser.y"
                    {
                        yyVal = ((ArgsNode)yyVals[-1+yyTop]);
                        lexer.setState(LexState.EXPR_BEG);
                        lexer.commandStart = true;
                    }
                    break;
                    case 542:
                        // line 2231 "RubyParser.y"
                    {
                        yyVal = lexer.inKwarg;
                        lexer.inKwarg = true;
                    }
                    break;
                    case 543:
                        // line 2234 "RubyParser.y"
                    {
                        lexer.inKwarg = ((Boolean)yyVals[-2+yyTop]);
                        yyVal = ((ArgsNode)yyVals[-1+yyTop]);
                        lexer.setState(LexState.EXPR_BEG);
                        lexer.commandStart = true;
                    }
                    break;
                    case 544:
                        // line 2242 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 545:
                        // line 2245 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 546:
                        // line 2248 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(lexer.getPosition(), null, ((String)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 547:
                        // line 2251 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(((BlockArgNode)yyVals[0+yyTop]).getPosition(), null, null, ((BlockArgNode)yyVals[0+yyTop]));
                    }
                    break;
                    case 548:
                        // line 2255 "RubyParser.y"
                    {
                        yyVal = ((ArgsTailHolder)yyVals[0+yyTop]);
                    }
                    break;
                    case 549:
                        // line 2258 "RubyParser.y"
                    {
                        yyVal = support.new_args_tail(lexer.getPosition(), null, null, null);
                    }
                    break;
                    case 550:
                        // line 2263 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 551:
                        // line 2266 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-7+yyTop]).getPosition(), ((ListNode)yyVals[-7+yyTop]), ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 552:
                        // line 2269 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 553:
                        // line 2272 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 554:
                        // line 2275 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), null, ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 555:
                        // line 2278 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 556:
                        // line 2281 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 557:
                        // line 2284 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 558:
                        // line 2287 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), null, ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 559:
                        // line 2290 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), null, ((ListNode)yyVals[-1+yyTop]), null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 560:
                        // line 2293 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 561:
                        // line 2296 "RubyParser.y"
                    {
                        yyVal = support.new_args(((RestArgNode)yyVals[-1+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 562:
                        // line 2299 "RubyParser.y"
                    {
                        yyVal = support.new_args(((RestArgNode)yyVals[-3+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 563:
                        // line 2302 "RubyParser.y"
                    {
                        yyVal = support.new_args(((ArgsTailHolder)yyVals[0+yyTop]).getPosition(), null, null, null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
                    }
                    break;
                    case 564:
                        // line 2305 "RubyParser.y"
                    {
                        yyVal = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
                    }
                    break;
                    case 565:
                        // line 2309 "RubyParser.y"
                    {
                        support.yyerror("formal argument cannot be a constant");
                    }
                    break;
                    case 566:
                        // line 2312 "RubyParser.y"
                    {
                        support.yyerror("formal argument cannot be an instance variable");
                    }
                    break;
                    case 567:
                        // line 2315 "RubyParser.y"
                    {
                        support.yyerror("formal argument cannot be a global variable");
                    }
                    break;
                    case 568:
                        // line 2318 "RubyParser.y"
                    {
                        support.yyerror("formal argument cannot be a class variable");
                    }
                    break;
                    case 570:
                        // line 2324 "RubyParser.y"
                    {
                        yyVal = support.formal_argument(((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 571:
                        // line 2328 "RubyParser.y"
                    {
                        yyVal = support.arg_var(((String)yyVals[0+yyTop]));
                    }
                    break;
                    case 572:
                        // line 2331 "RubyParser.y"
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
                    case 573:
                        // line 2347 "RubyParser.y"
                    {
                        yyVal = new ArrayNode(lexer.getPosition(), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 574:
                        // line 2350 "RubyParser.y"
                    {
                        ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
                        yyVal = ((ListNode)yyVals[-2+yyTop]);
                    }
                    break;
                    case 575:
                        // line 2355 "RubyParser.y"
                    {
                        support.arg_var(support.formal_argument(((String)yyVals[0+yyTop])));
                        yyVal = ((String)yyVals[0+yyTop]);
                    }
                    break;
                    case 576:
                        // line 2360 "RubyParser.y"
                    {
                        yyVal = support.keyword_arg(((Node)yyVals[0+yyTop]).getPosition(), support.assignableLabelOrIdentifier(((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])));
                    }
                    break;
                    case 577:
                        // line 2363 "RubyParser.y"
                    {
                        yyVal = support.keyword_arg(lexer.getPosition(), support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), new RequiredKeywordArgumentValueNode()));
                    }
                    break;
                    case 578:
                        // line 2367 "RubyParser.y"
                    {
                        yyVal = support.keyword_arg(support.getPosition(((Node)yyVals[0+yyTop])), support.assignableLabelOrIdentifier(((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])));
                    }
                    break;
                    case 579:
                        // line 2370 "RubyParser.y"
                    {
                        yyVal = support.keyword_arg(lexer.getPosition(), support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), new RequiredKeywordArgumentValueNode()));
                    }
                    break;
                    case 580:
                        // line 2375 "RubyParser.y"
                    {
                        yyVal = new ArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 581:
                        // line 2378 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 582:
                        // line 2382 "RubyParser.y"
                    {
                        yyVal = new ArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 583:
                        // line 2385 "RubyParser.y"
                    {
                        yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 584:
                        // line 2389 "RubyParser.y"
                    {
                        yyVal = ((String)yyVals[0+yyTop]);
                    }
                    break;
                    case 585:
                        // line 2392 "RubyParser.y"
                    {
                        yyVal = ((String)yyVals[0+yyTop]);
                    }
                    break;
                    case 586:
                        // line 2396 "RubyParser.y"
                    {
                        support.shadowing_lvar(((String)yyVals[0+yyTop]));
                        yyVal = ((String)yyVals[0+yyTop]);
                    }
                    break;
                    case 587:
                        // line 2400 "RubyParser.y"
                    {
                        yyVal = support.internalId();
                    }
                    break;
                    case 588:
                        // line 2404 "RubyParser.y"
                    {
                        support.arg_var(((String)yyVals[-2+yyTop]));
                        yyVal = new OptArgNode(support.getPosition(((Node)yyVals[0+yyTop])), support.assignableLabelOrIdentifier(((String)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
                    }
                    break;
                    case 589:
                        // line 2409 "RubyParser.y"
                    {
                        support.arg_var(support.formal_argument(((String)yyVals[-2+yyTop])));
                        yyVal = new OptArgNode(support.getPosition(((Node)yyVals[0+yyTop])), support.assignableLabelOrIdentifier(((String)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
                    }
                    break;
                    case 590:
                        // line 2414 "RubyParser.y"
                    {
                        yyVal = new BlockNode(((Node)yyVals[0+yyTop]).getPosition()).add(((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 591:
                        // line 2417 "RubyParser.y"
                    {
                        yyVal = support.appendToBlock(((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 592:
                        // line 2421 "RubyParser.y"
                    {
                        yyVal = new BlockNode(((Node)yyVals[0+yyTop]).getPosition()).add(((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 593:
                        // line 2424 "RubyParser.y"
                    {
                        yyVal = support.appendToBlock(((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 596:
                        // line 2431 "RubyParser.y"
                    {
                        if (!support.is_local_id(((String)yyVals[0+yyTop]))) {
                            support.yyerror("rest argument must be local variable");
                        }

                        yyVal = new RestArgNode(support.arg_var(support.shadowing_lvar(((String)yyVals[0+yyTop]))));
                    }
                    break;
                    case 597:
                        // line 2438 "RubyParser.y"
                    {
                        yyVal = new UnnamedRestArgNode(lexer.getPosition(), "", support.getCurrentScope().addVariable("*"));
                    }
                    break;
                    case 600:
                        // line 2446 "RubyParser.y"
                    {
                        if (!support.is_local_id(((String)yyVals[0+yyTop]))) {
                            support.yyerror("block argument must be local variable");
                        }

                        yyVal = new BlockArgNode(support.arg_var(support.shadowing_lvar(((String)yyVals[0+yyTop]))));
                    }
                    break;
                    case 601:
                        // line 2454 "RubyParser.y"
                    {
                        yyVal = ((BlockArgNode)yyVals[0+yyTop]);
                    }
                    break;
                    case 602:
                        // line 2457 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 603:
                        // line 2461 "RubyParser.y"
                    {
                        if (!(((Node)yyVals[0+yyTop]) instanceof SelfNode)) {
                            support.checkExpression(((Node)yyVals[0+yyTop]));
                        }
                        yyVal = ((Node)yyVals[0+yyTop]);
                    }
                    break;
                    case 604:
                        // line 2467 "RubyParser.y"
                    {
                        lexer.setState(LexState.EXPR_BEG);
                    }
                    break;
                    case 605:
                        // line 2469 "RubyParser.y"
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
                    case 606:
                        // line 2480 "RubyParser.y"
                    {
                        yyVal = new HashNode(lexer.getPosition());
                    }
                    break;
                    case 607:
                        // line 2483 "RubyParser.y"
                    {
                        yyVal = ((HashNode)yyVals[-1+yyTop]);
                    }
                    break;
                    case 608:
                        // line 2488 "RubyParser.y"
                    {
                        yyVal = new HashNode(lexer.getPosition(), ((KeyValuePair)yyVals[0+yyTop]));
                    }
                    break;
                    case 609:
                        // line 2491 "RubyParser.y"
                    {
                        yyVal = ((HashNode)yyVals[-2+yyTop]).add(((KeyValuePair)yyVals[0+yyTop]));
                    }
                    break;
                    case 610:
                        // line 2496 "RubyParser.y"
                    {
                        yyVal = new KeyValuePair<Node,Node>(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 611:
                        // line 2499 "RubyParser.y"
                    {
                        SymbolNode label = new SymbolNode(support.getPosition(((Node)yyVals[0+yyTop])), new ByteList(((String)yyVals[-1+yyTop]).getBytes(), lexer.getEncoding()));
                        yyVal = new KeyValuePair<Node,Node>(label, ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 612:
                        // line 2503 "RubyParser.y"
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
                    case 613:
                        // line 2516 "RubyParser.y"
                    {
                        yyVal = new KeyValuePair<Node,Node>(null, ((Node)yyVals[0+yyTop]));
                    }
                    break;
                    case 630:
                        // line 2526 "RubyParser.y"
                    {
                        yyVal = ((String)yyVals[0+yyTop]);
                    }
                    break;
                    case 631:
                        // line 2529 "RubyParser.y"
                    {
                        yyVal = ((String)yyVals[0+yyTop]);
                    }
                    break;
                    case 639:
                        // line 2540 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    case 640:
                        // line 2544 "RubyParser.y"
                    {
                        yyVal = null;
                    }
                    break;
                    // line 9839 "-"
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
    // line 2549 "RubyParser.y"

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
// line 9891 "-"
