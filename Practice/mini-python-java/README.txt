Le but de ce TD est de r�aliser un interpr�te pour un fragment tr�s simple de Python, appel� mini-Python ; il n'est pas n�cessaire de conna�tre le langage Python. Un fichier mini-Python a la structure suivante :
# z�ro, une ou plusieurs d�finitions de fonctions au d�but du fichier
def fibaux(a, b, k):
    if k == 0:
        return a
    else:
        return fibaux(b, a+b, k-1)

def fib(n):
    return fibaux(0, 1, n)

# une ou plusieurs instructions � la fin du fichier
print "quelques valeurs de la suite de Fibonacci :"
for n in [0, 1, 11, 42]:
    print fib(n)
Plus g�n�ralement, un fichier mini-Python est compos� d'une liste optionnelle de d�clarations de fonctions, suivie d'une liste d'instructions. Attention : la derni�re instruction doit �tre suivie d'un retour chariot.
Les instructions sont : l'affectation, la conditionnelle, la boucle (for), l'affichage d'une expression avec print, le renvoie d'une valeur avec return ou l'�valuation d'une expression. Les expressions enti�res sont : une constante (bool�en, entier ou cha�ne de caract�res), l'acc�s � une variable, la construction d'une liste (avec la notation [e1, e2, ..., en]), l'acc�s � un �l�ment de liste (avec la notation e[i]), l'appel d'une fonction, ou une des op�rations +, -, * et /, =, <>, <, <=, >, >=, and, or et not.

On consid�re �galement deux fonctions primitives: range(n) construit la liste [0, 1, 2, ..., n-1] et len(l) renvoie la longueur de la liste l.

Code fourni

Afin de vous aider � construire cet interpr�te, nous vous fournissons sa structure de base (sous la forme d'un ensemble de fichiers Java et d'un Makefile) :
Archive � t�l�charger : mini-python-java.tar.gz

Une fois cette archive d�compress�e (par exemple avec tar zxvf mini-python-java.tar.gz), vous obtenez un r�pertoire mini-python-java/. Vous pouvez lancer Eclipse dans le r�pertoire o� se trouve mini-python-java/ avec eclipse -data . puis cr�er un projet mini-python-java (en d�cochant l'option Use default location). Ce projet contient un package mini_python avec les fichiers suivants :

Syntax.java	la syntaxe abstraite de mini-Python (complet)
Lexer.java, MyLexer.java	l'analyseur lexical (complet)
parser.java, sym.java	l'analyseur syntaxique (complet)
Interp.java	l'interpr�te (� compl�ter)
Main.java	le programme principal (complet)
Makefile	pour automatiser la compilation (complet)
La biblioth�que java-cup-11a-runtime.jar qui est contenue dans le sous-r�pertoire lib/ doit �tre ajout�e au projet (avec Java Build Path -> Libraries -> Add JARs). Le code fourni compile mais est incomplet. Les endroits � compl�ter contiennent throw new Todo().
Le programme accepte un nom de fichier � interpr�ter. En cas d'absence, il utilise le fichier test.py.

Question 1. Expressions arithm�tiques

On ne consid�re pour l'instant que des expressions arithm�tiques sans variables. Compl�ter l'interpr�te pour de telles expressions. Tester sur le programme suivant :
print 1 + 2*3
print (3*3 + 4*4) / 5
print 10-3-4
dont le r�sultat doit �tre
7
5
3
Les op�rations division et modulo doivent signaler une erreur en cas de division par z�ro, en levant l'exception Error.
Pour tester facilement, on peut �diter le fichier test.py fourni et lancer Main.

Question 2. Expressions bool�ennes et conditionnelles

Compl�ter les m�thodes isTrue et isFalse de la classe Value qui d�terminent respectivement si une valeur est vraie ou fausse. En Python, la valeur None, le bool�en False, l'entier 0, la cha�ne vide "" et la liste vide [] sont consid�r�es comme fausses et les autres valeurs comme vraies.
Compl�ter ensuite l'interpr�te avec les op�rations de comparaison et les op�rations and, or et not. En Python, la comparaison est structurelle. On pourra utiliser la m�thode compareTo d�j� �crite dans la classe Value.

Compl�ter enfin l'interpr�te avec la conditionnelle (construction Sif).

Tester sur le programme suivant :

print not True and 1/0==0
print 1<2
if False or True:
    print "ok"
else:
    print "oups"
dont le r�sultat doit �tre
False
True
ok
Question 3. Variables

Pour g�rer les variables (du programme principal mais aussi les variables locales et param�tres) on va utiliser une table de hachage associant � des noms de variables des valeurs. La table est dans le champ vars de la classe Interp.
Compl�ter l'interpr�te pour que l'on puisse acc�der aux variables (constructeurs Eleft et Lident). Tenter d'acc�der � une variable qui ne se trouve pas encore dans la table doit provoquer une erreur.

De m�me, compl�ter l'interpr�te pour que l'on puisse affecter une variable (m�thodes interp(Sassign s) et assign(Lident lv, Expr e)). Cette fois, la variable peut ou non se trouver dans la table. Dans le premier cas, sa valeur est modifi�e.

Enfin, compl�ter l'interpr�te pour que l'on puisse concat�ner deux cha�nes de caract�res avec l'op�ration +.

Tester sur le programme suivant :

x = 41
x = x+1
print x
b = True and False
print b
s = "hello" + " world!"
print s
dont le r�sultat doit �tre
42
False
hello world!
Question 4. Fonctions

On va ajouter maintenant le traitement des fonctions. Ces derni�res sont stock�es dans la table globale functions. � chaque nom de fonction est associ�e sa d�finition, c'est-�-dire la liste des param�tres de la fonction et l'instruction qui constitue le corps de la fonction. Le code fourni se charge du remplissage de cette table.
Compl�ter ensuite l'interpr�te avec l'appel de fonction. Pour un appel de la forme f(e1,...,en), � une fonction f de la forme def f(x1,...,xn): s il faut construire un nouvel environnement qui associe � chaque argument formel xi la valeur de ei. Cela veut dire construire un nouvel objet de la classe Interp. On peut alors interpr�ter l'instruction s (le corps de la fonction) dans ce nouvel environnement. L'instruction return sera interpr�t�e en utilisant l'exception Return (d�j� d�finie). Si l'ex�cution du corps de la fonction termine sans rencontrer d'instruction return, la valeur None est renvoy�e.

Tester sur le programme suivant :

def fact(n):
    if n <= 1: return 1
    return n * fact(n-1)

print fact(10)
dont le r�sultat doit �tre
3628800
Question 5. Listes

Ajouter enfin le support des listes. Pour cela, compl�ter l'interpr�te pour que l'on puisse concat�ner deux listes avec l'op�ration +, pour interpr�ter l'appel aux primitives len (longueur d'une liste) et range (liste [0, 1, 2, ..., n-1]), et enfin pour interpr�ter les constructions [e1, e2, ..., en] et e1[e2].
Compl�ter ensuite l'interpr�te avec l'affectation d'un �l�ment de liste.

Enfin, compl�ter l'interpr�te avec la construction for de mini-Python. La construction for x in e: s affecte la variable x successivement aux diff�rents �l�ments de la liste e et ex�cute � chaque fois l'instruction s. L'expression e doit �tre �valu�e une seule fois.

Tester sur le programme donn� au d�but du sujet. Le r�sultat doit �tre :

0
1
89
267914296
Question 6. D'autres tests

Des tests positifs et n�gatifs sont fournis. Pour lancer votre interpr�te sur ces tests, faire make tests.
