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
Integer		= [:digit:]+	// Digit is defined on jflex
Identifier	= ([:jletter:] | [_]) ([:jletter:] | [:digit:] | [_] )* // Will be used to read function names

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
	/* ********** */
	
	"("	{ return new Symbol(LPAR, yyline, yycolumn); }
	")"	{ return new Symbol(RPAR, yyline, yycolumn); }
	"{"
		{ return new Symbol(LB, yyline, yycolumn); }
	"}"	{ return new Symbol(RB, yyline, yycolumn); }
	";"	{ return new Symbol(SEMICOLON, yyline, yycolumn);}
	"return"
		{ return new Symbol(RETURN, yyline, yycolumn); }
	"int"
		{ return new Symbol(INT, yyline, yycolumn); }
	{Integer}
		{ return new Symbol(CST, yyline, yycolumn, new Constant(Integer.parseInt(yytext()))); }
	{Identifier}
		{ return new Symbol(IDENT, yyline, yycolumn, yytext()); }
	{WhiteSpace}
		{ }
	.	{ throw new Exception(String.format("Error in line %d, column %d: illegal character '%s'\n", yyline, yycolumn, yytext())); }
}
