/* The following code was generated by JFlex 1.4.3 on 2/8/17 12:34 PM */

package mini_c;

import java_cup.runtime.*;
import static mini_c.sym.*;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.3
 * on 2/8/17 12:34 PM from the specification file
 * <tt>Lexer.flex</tt>
 */
class Lexer implements java_cup.runtime.Scanner {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0, 0
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\1\1\27\2\0\1\1\22\0\1\1\1\20\1\7\1\0"+
    "\1\12\1\0\1\25\1\6\1\37\1\40\1\16\1\23\1\44\1\24"+
    "\1\0\1\15\1\2\7\3\2\11\1\0\1\43\1\21\1\17\1\22"+
    "\2\0\32\13\1\5\1\10\2\5\1\13\1\5\2\13\1\51\1\13"+
    "\1\32\1\31\1\12\1\36\1\30\2\12\1\33\1\12\1\50\1\53"+
    "\2\12\1\45\1\34\1\46\1\47\1\12\1\35\1\4\1\12\1\52"+
    "\1\41\1\26\1\42\44\0\4\12\4\0\1\12\12\0\1\12\4\0"+
    "\1\12\5\0\27\12\1\0\37\12\1\0\u01ca\12\4\0\14\12\16\0"+
    "\5\12\7\0\1\12\1\0\1\12\201\0\5\12\1\0\2\12\2\0"+
    "\4\12\10\0\1\12\1\0\3\12\1\0\1\12\1\0\24\12\1\0"+
    "\123\12\1\0\213\12\10\0\236\12\11\0\46\12\2\0\1\12\7\0"+
    "\47\12\7\0\1\12\100\0\33\12\5\0\3\12\30\0\1\12\24\0"+
    "\53\12\25\0\12\14\4\0\2\12\1\0\143\12\1\0\1\12\17\0"+
    "\2\12\7\0\2\12\12\14\3\12\2\0\1\12\20\0\1\12\1\0"+
    "\36\12\35\0\131\12\13\0\1\12\16\0\12\14\41\12\11\0\2\12"+
    "\4\0\1\12\5\0\26\12\4\0\1\12\11\0\1\12\3\0\1\12"+
    "\27\0\31\12\107\0\1\12\1\0\13\12\127\0\66\12\3\0\1\12"+
    "\22\0\1\12\7\0\12\12\4\0\12\14\1\0\7\12\1\0\7\12"+
    "\5\0\10\12\2\0\2\12\2\0\26\12\1\0\7\12\1\0\1\12"+
    "\3\0\4\12\3\0\1\12\20\0\1\12\15\0\2\12\1\0\3\12"+
    "\4\0\12\14\4\12\7\0\1\12\11\0\6\12\4\0\2\12\2\0"+
    "\26\12\1\0\7\12\1\0\2\12\1\0\2\12\1\0\2\12\37\0"+
    "\4\12\1\0\1\12\7\0\12\14\2\0\3\12\20\0\11\12\1\0"+
    "\3\12\1\0\26\12\1\0\7\12\1\0\2\12\1\0\5\12\3\0"+
    "\1\12\22\0\1\12\17\0\2\12\4\0\12\14\1\0\1\12\23\0"+
    "\10\12\2\0\2\12\2\0\26\12\1\0\7\12\1\0\2\12\1\0"+
    "\5\12\3\0\1\12\36\0\2\12\1\0\3\12\4\0\12\14\1\0"+
    "\1\12\21\0\1\12\1\0\6\12\3\0\3\12\1\0\4\12\3\0"+
    "\2\12\1\0\1\12\1\0\2\12\3\0\2\12\3\0\3\12\3\0"+
    "\14\12\26\0\1\12\25\0\12\14\11\0\1\12\13\0\10\12\1\0"+
    "\3\12\1\0\27\12\1\0\12\12\1\0\5\12\3\0\1\12\32\0"+
    "\2\12\6\0\2\12\4\0\12\14\25\0\10\12\1\0\3\12\1\0"+
    "\27\12\1\0\12\12\1\0\5\12\3\0\1\12\40\0\1\12\1\0"+
    "\2\12\4\0\12\14\1\0\2\12\22\0\10\12\1\0\3\12\1\0"+
    "\51\12\2\0\1\12\20\0\1\12\21\0\2\12\4\0\12\14\12\0"+
    "\6\12\5\0\22\12\3\0\30\12\1\0\11\12\1\0\1\12\2\0"+
    "\7\12\72\0\60\12\1\0\2\12\13\0\10\12\11\0\12\14\47\0"+
    "\2\12\1\0\1\12\2\0\2\12\1\0\1\12\2\0\1\12\6\0"+
    "\4\12\1\0\7\12\1\0\3\12\1\0\1\12\1\0\1\12\2\0"+
    "\2\12\1\0\4\12\1\0\2\12\11\0\1\12\2\0\5\12\1\0"+
    "\1\12\11\0\12\14\2\0\4\12\40\0\1\12\37\0\12\14\26\0"+
    "\10\12\1\0\44\12\33\0\5\12\163\0\53\12\24\0\1\12\12\14"+
    "\6\0\6\12\4\0\4\12\3\0\1\12\3\0\2\12\7\0\3\12"+
    "\4\0\15\12\14\0\1\12\1\0\12\14\6\0\46\12\1\0\1\12"+
    "\5\0\1\12\2\0\53\12\1\0\u014d\12\1\0\4\12\2\0\7\12"+
    "\1\0\1\12\1\0\4\12\2\0\51\12\1\0\4\12\2\0\41\12"+
    "\1\0\4\12\2\0\7\12\1\0\1\12\1\0\4\12\2\0\17\12"+
    "\1\0\71\12\1\0\4\12\2\0\103\12\45\0\20\12\20\0\125\12"+
    "\14\0\u026c\12\2\0\21\12\1\0\32\12\5\0\113\12\3\0\3\12"+
    "\17\0\15\12\1\0\4\12\16\0\22\12\16\0\22\12\16\0\15\12"+
    "\1\0\3\12\17\0\64\12\43\0\1\12\3\0\2\12\3\0\12\14"+
    "\46\0\12\14\6\0\130\12\10\0\51\12\1\0\1\12\5\0\106\12"+
    "\12\0\35\12\51\0\12\14\36\12\2\0\5\12\13\0\54\12\25\0"+
    "\7\12\10\0\12\14\46\0\27\12\11\0\65\12\53\0\12\14\6\0"+
    "\12\14\15\0\1\12\135\0\57\12\21\0\7\12\4\0\12\14\51\0"+
    "\36\12\15\0\2\12\12\14\54\12\32\0\44\12\34\0\12\14\3\0"+
    "\3\12\12\14\44\12\153\0\4\12\1\0\4\12\3\0\2\12\11\0"+
    "\300\12\100\0\u0116\12\2\0\6\12\2\0\46\12\2\0\6\12\2\0"+
    "\10\12\1\0\1\12\1\0\1\12\1\0\1\12\1\0\37\12\2\0"+
    "\65\12\1\0\7\12\1\0\1\12\3\0\3\12\1\0\7\12\3\0"+
    "\4\12\2\0\6\12\4\0\15\12\5\0\3\12\1\0\7\12\102\0"+
    "\2\12\23\0\1\12\34\0\1\12\15\0\1\12\20\0\15\12\3\0"+
    "\33\12\107\0\1\12\4\0\1\12\2\0\12\12\1\0\1\12\3\0"+
    "\5\12\6\0\1\12\1\0\1\12\1\0\1\12\1\0\4\12\1\0"+
    "\13\12\2\0\4\12\5\0\5\12\4\0\1\12\21\0\51\12\u0a77\0"+
    "\57\12\1\0\57\12\1\0\205\12\6\0\4\12\3\0\2\12\14\0"+
    "\46\12\1\0\1\12\5\0\1\12\2\0\70\12\7\0\1\12\20\0"+
    "\27\12\11\0\7\12\1\0\7\12\1\0\7\12\1\0\7\12\1\0"+
    "\7\12\1\0\7\12\1\0\7\12\1\0\7\12\120\0\1\12\u01d5\0"+
    "\3\12\31\0\11\12\7\0\5\12\2\0\5\12\4\0\126\12\6\0"+
    "\3\12\1\0\132\12\1\0\4\12\5\0\51\12\3\0\136\12\21\0"+
    "\33\12\65\0\20\12\u0200\0\u19b6\12\112\0\u51cd\12\63\0\u048d\12\103\0"+
    "\56\12\2\0\u010d\12\3\0\20\12\12\14\2\12\24\0\57\12\20\0"+
    "\31\12\10\0\120\12\47\0\11\12\2\0\147\12\2\0\4\12\1\0"+
    "\4\12\14\0\13\12\115\0\12\12\1\0\3\12\1\0\4\12\1\0"+
    "\27\12\25\0\1\12\7\0\64\12\16\0\62\12\34\0\12\14\30\0"+
    "\6\12\3\0\1\12\4\0\12\14\34\12\12\0\27\12\31\0\35\12"+
    "\7\0\57\12\34\0\1\12\12\14\46\0\51\12\27\0\3\12\1\0"+
    "\10\12\4\0\12\14\6\0\27\12\3\0\1\12\5\0\60\12\1\0"+
    "\1\12\3\0\2\12\2\0\5\12\2\0\1\12\1\0\1\12\30\0"+
    "\3\12\2\0\13\12\7\0\3\12\14\0\6\12\2\0\6\12\2\0"+
    "\6\12\11\0\7\12\1\0\7\12\221\0\43\12\15\0\12\14\6\0"+
    "\u2ba4\12\14\0\27\12\4\0\61\12\u2104\0\u016e\12\2\0\152\12\46\0"+
    "\7\12\14\0\5\12\5\0\1\12\1\0\12\12\1\0\15\12\1\0"+
    "\5\12\1\0\1\12\1\0\2\12\1\0\2\12\1\0\154\12\41\0"+
    "\u016b\12\22\0\100\12\2\0\66\12\50\0\15\12\66\0\2\12\30\0"+
    "\3\12\31\0\1\12\6\0\5\12\1\0\207\12\7\0\1\12\13\0"+
    "\12\14\7\0\32\12\4\0\1\12\1\0\32\12\13\0\131\12\3\0"+
    "\6\12\2\0\6\12\2\0\6\12\2\0\3\12\3\0\2\12\3\0"+
    "\2\12\31\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\1\2\2\3\1\4\1\1\1\5\1\6"+
    "\1\7\1\10\1\11\1\12\1\13\1\14\2\1\4\4"+
    "\1\15\1\16\1\17\1\20\1\21\1\22\1\4\1\23"+
    "\2\0\1\2\1\0\1\24\1\25\1\26\1\27\1\30"+
    "\1\31\1\32\1\33\6\4\1\34\1\35\1\0\1\36"+
    "\5\4\1\2\1\37\6\4\1\40\1\4\1\41\1\42"+
    "\1\43";

  private static int [] zzUnpackAction() {
    int [] result = new int[69];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\54\0\130\0\204\0\260\0\334\0\u0108\0\u0134"+
    "\0\54\0\u0160\0\u018c\0\u01b8\0\u01e4\0\54\0\u0210\0\u023c"+
    "\0\u0268\0\u0294\0\u02c0\0\u02ec\0\u0318\0\54\0\54\0\54"+
    "\0\54\0\54\0\54\0\u0344\0\u0370\0\u039c\0\u03c8\0\u03f4"+
    "\0\u0420\0\54\0\54\0\54\0\54\0\54\0\54\0\54"+
    "\0\334\0\u044c\0\u0478\0\u04a4\0\u04d0\0\u04fc\0\u0528\0\u039c"+
    "\0\54\0\u0554\0\334\0\u0580\0\u05ac\0\u05d8\0\u0604\0\u0630"+
    "\0\54\0\334\0\u065c\0\u0688\0\u06b4\0\u06e0\0\u070c\0\u0738"+
    "\0\334\0\u0764\0\334\0\334\0\334";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[69];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\2\1\3\1\4\1\5\1\6\1\2\1\7\2\2"+
    "\1\5\2\6\1\2\1\10\1\11\1\12\1\13\1\14"+
    "\1\15\1\16\1\17\1\20\1\21\1\3\1\22\1\6"+
    "\1\23\1\6\1\24\1\25\1\6\1\26\1\27\1\30"+
    "\1\31\1\32\1\33\1\34\6\6\55\0\1\3\25\0"+
    "\1\3\26\0\2\35\1\36\51\0\2\5\5\0\1\5"+
    "\44\0\3\6\4\0\4\6\13\0\7\6\6\0\7\6"+
    "\6\37\3\0\43\37\15\0\1\40\1\41\54\0\1\42"+
    "\53\0\1\43\53\0\1\44\53\0\1\45\56\0\1\46"+
    "\56\0\1\47\54\0\1\50\27\0\3\6\4\0\4\6"+
    "\13\0\1\6\1\51\5\6\6\0\3\6\1\52\3\6"+
    "\2\0\3\6\4\0\4\6\13\0\3\6\1\53\3\6"+
    "\6\0\7\6\2\0\3\6\4\0\4\6\13\0\1\54"+
    "\6\6\6\0\1\6\1\55\5\6\2\0\3\6\4\0"+
    "\4\6\13\0\6\6\1\56\6\0\7\6\2\0\3\6"+
    "\4\0\4\6\13\0\2\6\1\57\4\6\6\0\7\6"+
    "\2\0\2\35\52\0\2\60\1\0\1\60\2\0\2\60"+
    "\1\0\1\60\15\0\2\60\16\0\1\60\10\0\1\61"+
    "\45\0\27\40\1\0\24\40\16\41\1\62\35\41\2\0"+
    "\3\6\4\0\4\6\13\0\7\6\6\0\1\6\1\63"+
    "\5\6\2\0\3\6\4\0\4\6\13\0\4\6\1\64"+
    "\2\6\6\0\7\6\2\0\3\6\4\0\4\6\13\0"+
    "\7\6\6\0\5\6\1\65\1\6\2\0\3\6\4\0"+
    "\4\6\13\0\7\6\6\0\1\66\6\6\2\0\3\6"+
    "\4\0\4\6\13\0\1\67\6\6\6\0\7\6\2\0"+
    "\3\6\4\0\4\6\13\0\7\6\6\0\1\6\1\70"+
    "\5\6\15\41\1\71\1\62\35\41\2\0\3\6\4\0"+
    "\4\6\13\0\2\6\1\72\4\6\6\0\7\6\2\0"+
    "\3\6\4\0\4\6\13\0\2\6\1\73\4\6\6\0"+
    "\7\6\2\0\3\6\4\0\4\6\13\0\7\6\6\0"+
    "\2\6\1\74\4\6\2\0\3\6\4\0\4\6\13\0"+
    "\3\6\1\75\3\6\6\0\7\6\2\0\3\6\4\0"+
    "\4\6\13\0\7\6\6\0\2\6\1\76\4\6\2\0"+
    "\3\6\4\0\4\6\13\0\7\6\6\0\6\6\1\77"+
    "\2\0\3\6\4\0\4\6\13\0\7\6\6\0\4\6"+
    "\1\100\2\6\2\0\3\6\4\0\4\6\13\0\2\6"+
    "\1\101\4\6\6\0\7\6\2\0\3\6\4\0\4\6"+
    "\13\0\7\6\6\0\1\102\6\6\2\0\3\6\4\0"+
    "\4\6\13\0\1\6\1\103\5\6\6\0\7\6\2\0"+
    "\3\6\4\0\4\6\13\0\7\6\6\0\1\6\1\104"+
    "\5\6\2\0\3\6\4\0\4\6\13\0\7\6\6\0"+
    "\3\6\1\105\3\6";

  private static int [] zzUnpackTrans() {
    int [] result = new int[1936];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\1\11\6\1\1\11\4\1\1\11\7\1\6\11"+
    "\2\1\2\0\1\1\1\0\7\11\10\1\1\11\1\0"+
    "\6\1\1\11\14\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[69];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
	/* No need for preamble in JAVA */


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  Lexer(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  Lexer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 1784) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead > 0) {
      zzEndRead+= numRead;
      return false;
    }
    // unlikely but not impossible: read 0 characters, but not at end of stream    
    if (numRead == 0) {
      int c = zzReader.read();
      if (c == -1) {
        return true;
      } else {
        zzBuffer[zzEndRead++] = (char) c;
        return false;
      }     
    }

	// numRead < 0
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() throws java.io.IOException {
    if (!zzEOFDone) {
      zzEOFDone = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public java_cup.runtime.Symbol next_token() throws java.io.IOException, Exception {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      for (zzCurrentPosL = zzStartRead; zzCurrentPosL < zzMarkedPosL;
                                                             zzCurrentPosL++) {
        switch (zzBufferL[zzCurrentPosL]) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn++;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 35: 
          { return new Symbol(RETURN, yyline, yycolumn);
          }
        case 36: break;
        case 27: 
          { return new Symbol(IF);
          }
        case 37: break;
        case 26: 
          { return new Symbol(OR, yyline, yycolumn);
          }
        case 38: break;
        case 18: 
          { return new Symbol(COMMA, yyline, yycolumn);
          }
        case 39: break;
        case 16: 
          { return new Symbol(RB, yyline, yycolumn);
          }
        case 40: break;
        case 10: 
          { return new Symbol(CMP, yyline, yycolumn, Binop.Bgt);
          }
        case 41: break;
        case 7: 
          { return new Symbol(EQUAL, yyline, yycolumn);
          }
        case 42: break;
        case 4: 
          { return new Symbol(IDENT, yyline, yycolumn, yytext());
          }
        case 43: break;
        case 15: 
          { return new Symbol(LB, yyline, yycolumn);
          }
        case 44: break;
        case 28: 
          { return new Symbol(CST, yyline, yycolumn, new Constant(Integer.decode(yytext())));
          }
        case 45: break;
        case 19: 
          { return new Symbol(CST, yyline, yycolumn, new Constant(Integer.parseInt(yytext(), 8)));
          }
        case 46: break;
        case 1: 
          { throw new Exception(String.format("Error in line %d, column %d: illegal character '%s'\n", yyline, yycolumn, yytext()));
          }
        case 47: break;
        case 3: 
          { return new Symbol(CST, yyline, yycolumn, new Constant(Integer.parseInt(yytext())));
          }
        case 48: break;
        case 33: 
          { return new Symbol(SIZEOF, yyline, yycolumn);
          }
        case 49: break;
        case 6: 
          { return new Symbol(TIMES, yyline, yycolumn);
          }
        case 50: break;
        case 30: 
          { return new Symbol(INT, yyline, yycolumn);
          }
        case 51: break;
        case 14: 
          { return new Symbol(RPAR, yyline, yycolumn);
          }
        case 52: break;
        case 31: 
          { return new Symbol(ELSE);
          }
        case 53: break;
        case 21: 
          { return new Symbol(CMP, yyline, yycolumn, Binop.Bneq);
          }
        case 54: break;
        case 29: 
          { return new Symbol(CST, yyline, yycolumn, new Constant(yytext().charAt(1)));
          }
        case 55: break;
        case 12: 
          { return new Symbol(MINUS, yyline, yycolumn);
          }
        case 56: break;
        case 22: 
          { return new Symbol(CMP, yyline, yycolumn, Binop.Ble);
          }
        case 57: break;
        case 20: 
          { return new Symbol(CMP, yyline, yycolumn, Binop.Beqeq);
          }
        case 58: break;
        case 32: 
          { return new Symbol(WHILE);
          }
        case 59: break;
        case 8: 
          { return new Symbol(NOT, yyline, yycolumn);
          }
        case 60: break;
        case 34: 
          { return new Symbol(STRUCT, yyline, yycolumn);
          }
        case 61: break;
        case 24: 
          { return new Symbol(ARROW, yyline, yycolumn);
          }
        case 62: break;
        case 23: 
          { return new Symbol(CMP, yyline, yycolumn, Binop.Bge);
          }
        case 63: break;
        case 13: 
          { return new Symbol(LPAR, yyline, yycolumn);
          }
        case 64: break;
        case 11: 
          { return new Symbol(PLUS, yyline, yycolumn);
          }
        case 65: break;
        case 17: 
          { return new Symbol(SEMICOLON, yyline, yycolumn);
          }
        case 66: break;
        case 5: 
          { return new Symbol(DIV, yyline, yycolumn);
          }
        case 67: break;
        case 9: 
          { return new Symbol(CMP, yyline, yycolumn, Binop.Blt);
          }
        case 68: break;
        case 2: 
          { /* DO NOTHING */
          }
        case 69: break;
        case 25: 
          { return new Symbol(AND, yyline, yycolumn);
          }
        case 70: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            zzDoEOF();
              { return new java_cup.runtime.Symbol(sym.EOF); }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }

  /**
   * Converts an int token code into the name of the
   * token by reflection on the cup symbol class/interface sym
   *
   * This code was contributed by Karl Meissner <meissnersd@yahoo.com>
   */
  private String getTokenName(int token) {
    try {
      java.lang.reflect.Field [] classFields = sym.class.getFields();
      for (int i = 0; i < classFields.length; i++) {
        if (classFields[i].getInt(null) == token) {
          return classFields[i].getName();
        }
      }
    } catch (Exception e) {
      e.printStackTrace(System.err);
    }

    return "UNKNOWN TOKEN";
  }

  /**
   * Same as next_token but also prints the token to standard out
   * for debugging.
   *
   * This code was contributed by Karl Meissner <meissnersd@yahoo.com>
   */
  public java_cup.runtime.Symbol debug_next_token() throws java.io.IOException, Exception {
    java_cup.runtime.Symbol s = next_token();
    System.out.println( "line:" + (yyline+1) + " col:" + (yycolumn+1) + " --"+ yytext() + "--" + getTokenName(s.sym) + "--");
    return s;
  }

  /**
   * Runs the scanner on input files.
   *
   * This main method is the debugging routine for the scanner.
   * It prints debugging information about each returned token to
   * System.out until the end of file is reached, or an error occured.
   *
   * @param argv   the command line, contains the filenames to run
   *               the scanner on.
   */
  public static void main(String argv[]) {
    if (argv.length == 0) {
      System.out.println("Usage : java Lexer <inputfile>");
    }
    else {
      for (int i = 0; i < argv.length; i++) {
        Lexer scanner = null;
        try {
          scanner = new Lexer( new java.io.FileReader(argv[i]) );
          while ( !scanner.zzAtEOF ) scanner.debug_next_token();
        }
        catch (java.io.FileNotFoundException e) {
          System.out.println("File not found : \""+argv[i]+"\"");
        }
        catch (java.io.IOException e) {
          System.out.println("IO error scanning file \""+argv[i]+"\"");
          System.out.println(e);
        }
        catch (Exception e) {
          System.out.println("Unexpected exception:");
          e.printStackTrace();
        }
      }
    }
  }


}
