/* The following code was generated by JFlex 1.4.3 on 1/18/17 12:38 PM */

package mini_c;

import java_cup.runtime.*;
import static mini_c.sym.*;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.3
 * on 1/18/17 12:38 PM from the specification file
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
    "\11\0\1\0\1\15\2\0\1\0\22\0\1\0\3\0\1\2\3\0"+
    "\1\3\1\4\6\0\12\1\1\0\1\7\5\0\32\2\4\0\1\2"+
    "\1\0\4\2\1\11\10\2\1\14\3\2\1\10\1\2\1\12\1\13"+
    "\5\2\1\5\1\0\1\6\44\0\4\2\4\0\1\2\12\0\1\2"+
    "\4\0\1\2\5\0\27\2\1\0\37\2\1\0\u01ca\2\4\0\14\2"+
    "\16\0\5\2\7\0\1\2\1\0\1\2\201\0\5\2\1\0\2\2"+
    "\2\0\4\2\10\0\1\2\1\0\3\2\1\0\1\2\1\0\24\2"+
    "\1\0\123\2\1\0\213\2\10\0\236\2\11\0\46\2\2\0\1\2"+
    "\7\0\47\2\7\0\1\2\100\0\33\2\5\0\3\2\30\0\1\2"+
    "\24\0\53\2\25\0\12\1\4\0\2\2\1\0\143\2\1\0\1\2"+
    "\17\0\2\2\7\0\2\2\12\1\3\2\2\0\1\2\20\0\1\2"+
    "\1\0\36\2\35\0\131\2\13\0\1\2\16\0\12\1\41\2\11\0"+
    "\2\2\4\0\1\2\5\0\26\2\4\0\1\2\11\0\1\2\3\0"+
    "\1\2\27\0\31\2\107\0\1\2\1\0\13\2\127\0\66\2\3\0"+
    "\1\2\22\0\1\2\7\0\12\2\4\0\12\1\1\0\7\2\1\0"+
    "\7\2\5\0\10\2\2\0\2\2\2\0\26\2\1\0\7\2\1\0"+
    "\1\2\3\0\4\2\3\0\1\2\20\0\1\2\15\0\2\2\1\0"+
    "\3\2\4\0\12\1\4\2\7\0\1\2\11\0\6\2\4\0\2\2"+
    "\2\0\26\2\1\0\7\2\1\0\2\2\1\0\2\2\1\0\2\2"+
    "\37\0\4\2\1\0\1\2\7\0\12\1\2\0\3\2\20\0\11\2"+
    "\1\0\3\2\1\0\26\2\1\0\7\2\1\0\2\2\1\0\5\2"+
    "\3\0\1\2\22\0\1\2\17\0\2\2\4\0\12\1\1\0\1\2"+
    "\23\0\10\2\2\0\2\2\2\0\26\2\1\0\7\2\1\0\2\2"+
    "\1\0\5\2\3\0\1\2\36\0\2\2\1\0\3\2\4\0\12\1"+
    "\1\0\1\2\21\0\1\2\1\0\6\2\3\0\3\2\1\0\4\2"+
    "\3\0\2\2\1\0\1\2\1\0\2\2\3\0\2\2\3\0\3\2"+
    "\3\0\14\2\26\0\1\2\25\0\12\1\11\0\1\2\13\0\10\2"+
    "\1\0\3\2\1\0\27\2\1\0\12\2\1\0\5\2\3\0\1\2"+
    "\32\0\2\2\6\0\2\2\4\0\12\1\25\0\10\2\1\0\3\2"+
    "\1\0\27\2\1\0\12\2\1\0\5\2\3\0\1\2\40\0\1\2"+
    "\1\0\2\2\4\0\12\1\1\0\2\2\22\0\10\2\1\0\3\2"+
    "\1\0\51\2\2\0\1\2\20\0\1\2\21\0\2\2\4\0\12\1"+
    "\12\0\6\2\5\0\22\2\3\0\30\2\1\0\11\2\1\0\1\2"+
    "\2\0\7\2\72\0\60\2\1\0\2\2\13\0\10\2\11\0\12\1"+
    "\47\0\2\2\1\0\1\2\2\0\2\2\1\0\1\2\2\0\1\2"+
    "\6\0\4\2\1\0\7\2\1\0\3\2\1\0\1\2\1\0\1\2"+
    "\2\0\2\2\1\0\4\2\1\0\2\2\11\0\1\2\2\0\5\2"+
    "\1\0\1\2\11\0\12\1\2\0\4\2\40\0\1\2\37\0\12\1"+
    "\26\0\10\2\1\0\44\2\33\0\5\2\163\0\53\2\24\0\1\2"+
    "\12\1\6\0\6\2\4\0\4\2\3\0\1\2\3\0\2\2\7\0"+
    "\3\2\4\0\15\2\14\0\1\2\1\0\12\1\6\0\46\2\1\0"+
    "\1\2\5\0\1\2\2\0\53\2\1\0\u014d\2\1\0\4\2\2\0"+
    "\7\2\1\0\1\2\1\0\4\2\2\0\51\2\1\0\4\2\2\0"+
    "\41\2\1\0\4\2\2\0\7\2\1\0\1\2\1\0\4\2\2\0"+
    "\17\2\1\0\71\2\1\0\4\2\2\0\103\2\45\0\20\2\20\0"+
    "\125\2\14\0\u026c\2\2\0\21\2\1\0\32\2\5\0\113\2\3\0"+
    "\3\2\17\0\15\2\1\0\4\2\16\0\22\2\16\0\22\2\16\0"+
    "\15\2\1\0\3\2\17\0\64\2\43\0\1\2\3\0\2\2\3\0"+
    "\12\1\46\0\12\1\6\0\130\2\10\0\51\2\1\0\1\2\5\0"+
    "\106\2\12\0\35\2\51\0\12\1\36\2\2\0\5\2\13\0\54\2"+
    "\25\0\7\2\10\0\12\1\46\0\27\2\11\0\65\2\53\0\12\1"+
    "\6\0\12\1\15\0\1\2\135\0\57\2\21\0\7\2\4\0\12\1"+
    "\51\0\36\2\15\0\2\2\12\1\54\2\32\0\44\2\34\0\12\1"+
    "\3\0\3\2\12\1\44\2\153\0\4\2\1\0\4\2\3\0\2\2"+
    "\11\0\300\2\100\0\u0116\2\2\0\6\2\2\0\46\2\2\0\6\2"+
    "\2\0\10\2\1\0\1\2\1\0\1\2\1\0\1\2\1\0\37\2"+
    "\2\0\65\2\1\0\7\2\1\0\1\2\3\0\3\2\1\0\7\2"+
    "\3\0\4\2\2\0\6\2\4\0\15\2\5\0\3\2\1\0\7\2"+
    "\102\0\2\2\23\0\1\2\34\0\1\2\15\0\1\2\20\0\15\2"+
    "\3\0\33\2\107\0\1\2\4\0\1\2\2\0\12\2\1\0\1\2"+
    "\3\0\5\2\6\0\1\2\1\0\1\2\1\0\1\2\1\0\4\2"+
    "\1\0\13\2\2\0\4\2\5\0\5\2\4\0\1\2\21\0\51\2"+
    "\u0a77\0\57\2\1\0\57\2\1\0\205\2\6\0\4\2\3\0\2\2"+
    "\14\0\46\2\1\0\1\2\5\0\1\2\2\0\70\2\7\0\1\2"+
    "\20\0\27\2\11\0\7\2\1\0\7\2\1\0\7\2\1\0\7\2"+
    "\1\0\7\2\1\0\7\2\1\0\7\2\1\0\7\2\120\0\1\2"+
    "\u01d5\0\3\2\31\0\11\2\7\0\5\2\2\0\5\2\4\0\126\2"+
    "\6\0\3\2\1\0\132\2\1\0\4\2\5\0\51\2\3\0\136\2"+
    "\21\0\33\2\65\0\20\2\u0200\0\u19b6\2\112\0\u51cd\2\63\0\u048d\2"+
    "\103\0\56\2\2\0\u010d\2\3\0\20\2\12\1\2\2\24\0\57\2"+
    "\20\0\31\2\10\0\120\2\47\0\11\2\2\0\147\2\2\0\4\2"+
    "\1\0\4\2\14\0\13\2\115\0\12\2\1\0\3\2\1\0\4\2"+
    "\1\0\27\2\25\0\1\2\7\0\64\2\16\0\62\2\34\0\12\1"+
    "\30\0\6\2\3\0\1\2\4\0\12\1\34\2\12\0\27\2\31\0"+
    "\35\2\7\0\57\2\34\0\1\2\12\1\46\0\51\2\27\0\3\2"+
    "\1\0\10\2\4\0\12\1\6\0\27\2\3\0\1\2\5\0\60\2"+
    "\1\0\1\2\3\0\2\2\2\0\5\2\2\0\1\2\1\0\1\2"+
    "\30\0\3\2\2\0\13\2\7\0\3\2\14\0\6\2\2\0\6\2"+
    "\2\0\6\2\11\0\7\2\1\0\7\2\221\0\43\2\15\0\12\1"+
    "\6\0\u2ba4\2\14\0\27\2\4\0\61\2\u2104\0\u016e\2\2\0\152\2"+
    "\46\0\7\2\14\0\5\2\5\0\1\2\1\0\12\2\1\0\15\2"+
    "\1\0\5\2\1\0\1\2\1\0\2\2\1\0\2\2\1\0\154\2"+
    "\41\0\u016b\2\22\0\100\2\2\0\66\2\50\0\15\2\66\0\2\2"+
    "\30\0\3\2\31\0\1\2\6\0\5\2\1\0\207\2\7\0\1\2"+
    "\13\0\12\1\7\0\32\2\4\0\1\2\1\0\32\2\13\0\131\2"+
    "\3\0\6\2\2\0\6\2\2\0\6\2\2\0\3\2\3\0\2\2"+
    "\3\0\2\2\31\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7"+
    "\1\10\5\3\1\11";

  private static int [] zzUnpackAction() {
    int [] result = new int[15];
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
    "\0\0\0\16\0\34\0\52\0\16\0\16\0\16\0\16"+
    "\0\16\0\70\0\106\0\124\0\142\0\160\0\52";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[15];
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
    "\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11"+
    "\1\12\4\4\20\0\1\3\15\0\2\4\5\0\5\4"+
    "\2\0\2\4\5\0\1\4\1\13\3\4\2\0\2\4"+
    "\5\0\2\4\1\14\2\4\2\0\2\4\5\0\3\4"+
    "\1\15\1\4\2\0\2\4\5\0\1\16\4\4\2\0"+
    "\2\4\5\0\4\4\1\17\1\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[126];
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
    "\1\0\1\11\2\1\5\11\6\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[15];
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
    while (i < 1726) {
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
  public java_cup.runtime.Symbol next_token() throws java.io.IOException {
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
        case 2: 
          { return new Symbol(CST, yyline, yycolumn, Integer.parseInt(yytext()));
          }
        case 10: break;
        case 3: 
          { return new Symbol(IDENT, yyline, yycolumn, yytext());
          }
        case 11: break;
        case 4: 
          { return new Symbol(LPAR, yyline, yycolumn);
          }
        case 12: break;
        case 8: 
          { return new Symbol(SEMICOLON, yyline, yycolumn);
          }
        case 13: break;
        case 6: 
          { return new Symbol(LB, yyline, yycolumn);
          }
        case 14: break;
        case 1: 
          { throw new Exception(String.format("Error in line %d, column %d: illegal character '%s'\n", yyline, yycolumn, yytexy()));
          }
        case 15: break;
        case 9: 
          { return new Symbol(RETURN, yyline, yycolumn):
          }
        case 16: break;
        case 5: 
          { return new Symbol(RPAR, yyline, yycolumn);
          }
        case 17: break;
        case 7: 
          { return new Symbol(RB, yyline, yycolumn);
          }
        case 18: break;
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
  public java_cup.runtime.Symbol debug_next_token() throws java.io.IOException {
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
