package mini_c;

import java_cup.runtime.*;
import static mini_c.sym.*;

%%

%class Lexer
%unicode			/* The characters are unicode */
%cup				/* Syntax analyser with cup (Parser part) */
%cupdebug			/* ? */
%line				/* Decompose by line */
%column				/* Decompose by column */
%yylexthrow Exception
		/* Can throw exceptions */

%{
	/* No need for preamble in JAVA */
%}

WhiteSpace	= [ \t\r\n]+
Octal_int   = ("0") ([0-7])+
Hex_int     = ("0x") ([0-9a-fA-f])+
Integer		= (0 | [1-9]\d* )                                           // If it starts by 0 but has more stuff after, then it's not a decimal Int
Identifier	= ([:jletter:] | [_]) ([:jletter:] | [:digit:] | [_] )*
Character   = "'" [:jletter:] "'"                                       // TODO: not yet well implemented
BlockComment= [/][*][^*]*[*]+([^*/][^*]*[*]+)*[/]

%%
<YYINITIAL> {
	/* Operators */
	"=" { return new Symbol(EQUAL, yyline, yycolumn); }
	"=="
		{ return new Symbol(CMP, yyline, yycolumn, Binop.Beqeq); }
	"!="
		{ return new Symbol(CMP, yyline, yycolumn, Binop.Bneq); }
	"<"
		{ return new Symbol(CMP, yyline, yycolumn, Binop.Blt); }
	"<="
		{ return new Symbol(CMP, yyline, yycolumn, Binop.Ble); }
	">"
		{ return new Symbol(CMP, yyline, yycolumn, Binop.Bgt); }
	">="
		{ return new Symbol(CMP, yyline, yycolumn, Binop.Bge); }
	"+"
		{ return new Symbol(PLUS, yyline, yycolumn); }
	"-"
		{ return new Symbol(MINUS, yyline, yycolumn); }
	"*"
		{ return new Symbol(TIMES, yyline, yycolumn); }
	"/"
		{ return new Symbol(DIV, yyline, yycolumn); }
	"&&"
		{ return new Symbol(AND, yyline, yycolumn); }
	"||"
		{ return new Symbol(OR, yyline, yycolumn); }
	"!"
		{ return new Symbol(NOT, yyline, yycolumn); }
    "->"
        { return new Symbol(ARROW, yyline, yycolumn); }
        /* Comments */
    "//".* // Normal comment
        { /* DO NOTHING */ }
    {BlockComment}
        { /* DO NOTHING */ }
	/* loops */
	"if"
    	    { return new Symbol(IF); }
	"else"
	    { return new Symbol(ELSE); }
	"while"
	    { return new Symbol(WHILE); }
	// TODO: make the for, why not?
	/* More stuff */
	"("	{ return new Symbol(LPAR, yyline, yycolumn); }
	")"	{ return new Symbol(RPAR, yyline, yycolumn); }
	"{"
		{ return new Symbol(LB, yyline, yycolumn); }
	"}"	{ return new Symbol(RB, yyline, yycolumn); }
	";"	{ return new Symbol(SEMICOLON, yyline, yycolumn);}
	"," { return new Symbol(COMMA, yyline, yycolumn);}
	/* Fixed Words */
	"return"
		{ return new Symbol(RETURN, yyline, yycolumn); }
	"int"
		{ return new Symbol(INT, yyline, yycolumn); }
	"struct"
	    { return new Symbol(STRUCT, yyline, yycolumn); }
	"sizeof"
	    { return new Symbol(SIZEOF, yyline, yycolumn); }
    /* Ints, chars & spaces */
	{Integer}
		{ return new Symbol(CST, yyline, yycolumn, new Constant(Integer.parseInt(yytext()))); }
	{Hex_int}
	    { return new Symbol(CST, yyline, yycolumn, new Constant(Integer.decode(yytext()))); }
	{Octal_int}
    	{ return new Symbol(CST, yyline, yycolumn, new Constant(Integer.parseInt(yytext(), 8))); }
	{Identifier}
		{ return new Symbol(IDENT, yyline, yycolumn, yytext()); }
	{Character}
	    { return new Symbol(IDENT, yyline, yycolumn, yytext()); }
	{WhiteSpace}
		{ /* DO NOTHING */ }
	.	{ throw new Exception(String.format("Error in line %d, column %d: illegal character '%s'\n", yyline, yycolumn, yytext())); }
}
Contact GitHub API Training Shop Blog About
