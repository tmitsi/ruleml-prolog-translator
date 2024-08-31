/*
 [The "BSD licence"]
 Copyright (c) 2023 - 2024 Theodoros Mitsikas, Ralph Schäfermeier, Adrian Paschke
 Copyright (c) 2013 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/** XML parser derived from ANTLR v4 ref guide book example */
parser grammar XMLParser;

options { tokenVocab=XMLLexer; }

document    :   prolog? misc* element misc* EOF ;

prolog      :   XMLDeclOpen attribute* SPECIAL_CLOSE ;



content     :   chardata?
                ((element | reference | CDATA | PI | COMMENT) chardata?)* ;

element     :   ruleML | assert |  implies | and | or | naf |atom | expr | plex | repo
            |   query | equal | rel | ind | var | fun | data | if | then
            |   unsupported
            ;

ruleML      :    '<' RuleML attribute* '>' content '<' '/' RuleML '>' ;
assert      :    '<' Assert attribute* '>' content '<' '/' Assert '>' ;
implies     :    '<' Implies attribute* '>' content content'<' '/' Implies '>' ;
and         :    '<' And attribute* '>' content '<' '/' And '>' ;
or          :    '<' Or attribute* '>' content '<' '/' Or '>' ;
atom        :    '<' Atom attribute* '>' content '<' '/' Atom '>' ;
expr        :    '<' Expr attribute* '>' content '<' '/' Expr '>' ;
plex        :    '<' Plex attribute* '>' content '<' '/' Plex '>' ;
repo        :    '<' Repo attribute* '>' content '<' '/' Repo '>' ;
naf         :    '<' Naf attribute* '>' content '<' '/' Naf '>' ;
equal       :    '<' Equal attribute* '>' content '<' '/' Equal '>' ;
rel         :    '<' Rel attribute* '>' content '<' '/' Rel '>' ;
ind         :    '<' Ind attribute* '>' content '<' '/' Ind '>' ;
var         :    '<' Var attribute* '>' content '<' '/' Var '>' ;
fun         :    '<' Fun attribute* '>' content '<' '/' Fun '>' ;
data        :    '<' Data attribute* '>' content '<' '/' Data '>' ;
if          :    '<' If attribute* '>' content '<' '/' If '>' ;
then        :    '<' Then attribute* '>' content '<' '/' Then '>' ;
query       :    '<' Query attribute* '>' content '<' '/' Query '>' ;

unsupported :    '<' Name  attribute* '>' content '<' '/' Name '>'
            |    '<' Name  attribute* '>' content
            ;


reference   :   EntityRef | CharRef ;

attribute   :   Name '=' STRING ;

/** ``All text that is not markup constitutes the character data of
 *  the document.''
 */
chardata    :   TEXT | SEA_WS ;

misc        :   COMMENT | PI | SEA_WS ;

