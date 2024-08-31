/*
BSD License

Copyright (c) 2023 - 2024 Theodoros Mitsikas, Ralph Schäfermeier, Adrian Paschke
Copyright (c) 2013, Tom Everett
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. Neither the name of the copyright holder nor the names of its contributors
   may be used to endorse or promote products derived from this software
   without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

grammar PrologGrammar;

@parser::members {
    private boolean queryMode = false;
    public void setQueryMode(boolean q) {
        queryMode = q;
    }

    public boolean getQueryMode() {
        return queryMode;
    }
}

@lexer::members {
public static final int COMMENTCH = 1;
}

document: (assert | query)* EOF ;

assert
    : ( implies
      | directive
      | fact)+
    ;

directive: ':-' compound_term '.' ;

implies
    : conclusion ':-' goal '.'
    ;

fact
    : {!queryMode}? conclusion '.'
    ;

query
    : {queryMode}? goal '.'
    | '?-' goal '.'
    ;


conclusion
    : compound_term
    ;

goal
    : conjunction
    | disjunction
    | compound_term
    | function
    | naf
    | '(' goal ')'
    ;

conjunction
    : (naf | compound_term | ('(' goal ')'))  ( ',' (naf | compound_term | ('(' goal ')') ))+
    ;

disjunction
    : (naf | compound_term | conjunction | ('(' goal ')'))  ( ';' (naf | compound_term | conjunction | ('(' goal ')')))+
    ;

naf
: '\\+' ( compound_term | ('(' goal ')'))
;

compound_term
    :  atom '(' arguments ')'
    |  equality
    |  atom
    ;

function
    :  atom '(' (arguments | '(' implies ')') ')'
    ;

equality
    : (atom | variable | list | number | function) | ('(' (atom | variable | list | number | function) ')')
      '='
      (atom | variable | list | number | function) | ('(' (atom | variable | list | number | function) ')')
    ;

arguments
    : argument (',' argument)*
    ;

argument
    : (atom | variable | list | number | function | equality)
    | '(' argument ')'
    ;

list
    : '[' arguments (repo)? ']'
    | '[' ']'
    ;

repo
    : rest_op argument
    ;

rest_op
    : '|'
    ;

number
    : float
    | integer
    ;

float
    : FLOAT
    ;

integer
    : DECIMAL
    | CHARACTER_CODE_CONSTANT
    | BINARY
    | OCTAL
    | HEX
    ;

variable
    : VARIABLE
    ;

VARIABLE
    : CAPITAL_LETTER ALPHANUMERIC*
    | '_' ALPHANUMERIC+
    | '_'
    ;

atom
    : LETTER_DIGIT
    | QUOTED
    | DOUBLE_QUOTED_LIST
    | BACK_QUOTED_STRING
//    | '!'               # cut
    ;

// Lexer
LETTER_DIGIT
    : SMALL_LETTER ALPHANUMERIC*
    ;



DECIMAL: ('-')? DIGIT+ ;
BINARY: '0b' [01]+ ;
OCTAL: '0o' [0-7]+ ;
HEX: '0x' HEX_DIGIT+ ;

CHARACTER_CODE_CONSTANT: '0' '\'' SINGLE_QUOTED_CHARACTER ;

FLOAT: DECIMAL '.' [0-9]* ( [eE] [+-] DECIMAL )? ;


GRAPHIC_TOKEN: (GRAPHIC | '\\')+ ; // 6.4.2
fragment GRAPHIC: [#$&*+./:<=>?@^~] | '-' ; // 6.5.1 graphic char

fragment SINGLE_QUOTED_CHARACTER: NON_QUOTE_CHAR | '\'\'' | '"' | '`' ;
fragment DOUBLE_QUOTED_CHARACTER: NON_QUOTE_CHAR | '\'' | '""' | '`' ;
fragment BACK_QUOTED_CHARACTER: NON_QUOTE_CHAR | '\'' | '"' | '``' ;
fragment NON_QUOTE_CHAR
    : GRAPHIC
    | ALPHANUMERIC
    | SOLO
    | ' ' // space char
    | META_ESCAPE
    | CONTROL_ESCAPE
    | OCTAL_ESCAPE
    | HEX_ESCAPE
    ;
fragment META_ESCAPE: '\\' [\\'"`] ;
fragment CONTROL_ESCAPE: '\\' [abrftnv] ;
fragment OCTAL_ESCAPE: '\\' [0-7]+ '\\' ;
fragment HEX_ESCAPE: '\\x' HEX_DIGIT+ '\\' ;

QUOTED:          '\'' (CONTINUATION_ESCAPE | SINGLE_QUOTED_CHARACTER )*? '\'' ;
DOUBLE_QUOTED_LIST: '"' (CONTINUATION_ESCAPE | DOUBLE_QUOTED_CHARACTER )*? '"';
BACK_QUOTED_STRING: '`' (CONTINUATION_ESCAPE | BACK_QUOTED_CHARACTER )*? '`';
fragment CONTINUATION_ESCAPE: '\\\n' ;

fragment ALPHANUMERIC: ALPHA | DIGIT ;
fragment ALPHA: '_' | SMALL_LETTER | CAPITAL_LETTER ;
fragment SMALL_LETTER: [a-z_];

fragment CAPITAL_LETTER: [A-Z];

fragment DIGIT: [0-9] ;
fragment HEX_DIGIT: [0-9a-fA-F] ;

fragment SOLO: [!(),;[{}|%] | ']' ;


WS
   : [ \t\r\n]+ -> skip
   ;

COMMENT: '%' ~[\n\r]* ( [\n\r] | EOF) -> channel(1) ;
MULTILINE_COMMENT: '/*' ( MULTILINE_COMMENT | . )*? ('*/' | EOF) -> channel(1);
