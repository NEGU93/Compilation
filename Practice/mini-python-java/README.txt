Le but de ce TD est de réaliser un interprète pour un fragment très simple de Python, appelé mini-Python ; il n'est pas nécessaire de connaître le langage Python. Un fichier mini-Python a la structure suivante :
# zéro, une ou plusieurs définitions de fonctions au début du fichier
def fibaux(a, b, k):
    if k == 0:
        return a
    else:
        return fibaux(b, a+b, k-1)

def fib(n):
    return fibaux(0, 1, n)

# une ou plusieurs instructions à la fin du fichier
print "quelques valeurs de la suite de Fibonacci :"
for n in [0, 1, 11, 42]:
    print fib(n)
Plus généralement, un fichier mini-Python est composé d'une liste optionnelle de déclarations de fonctions, suivie d'une liste d'instructions. Attention : la dernière instruction doit être suivie d'un retour chariot.
Les instructions sont : l'affectation, la conditionnelle, la boucle (for), l'affichage d'une expression avec print, le renvoie d'une valeur avec return ou l'évaluation d'une expression. Les expressions entières sont : une constante (booléen, entier ou chaîne de caractères), l'accès à une variable, la construction d'une liste (avec la notation [e1, e2, ..., en]), l'accès à un élément de liste (avec la notation e[i]), l'appel d'une fonction, ou une des opérations +, -, * et /, =, <>, <, <=, >, >=, and, or et not.

On considère également deux fonctions primitives: range(n) construit la liste [0, 1, 2, ..., n-1] et len(l) renvoie la longueur de la liste l.

Code fourni

Afin de vous aider à construire cet interprète, nous vous fournissons sa structure de base (sous la forme d'un ensemble de fichiers Java et d'un Makefile) :
Archive à télécharger : mini-python-java.tar.gz

Une fois cette archive décompressée (par exemple avec tar zxvf mini-python-java.tar.gz), vous obtenez un répertoire mini-python-java/. Vous pouvez lancer Eclipse dans le répertoire où se trouve mini-python-java/ avec eclipse -data . puis créer un projet mini-python-java (en décochant l'option Use default location). Ce projet contient un package mini_python avec les fichiers suivants :

Syntax.java	la syntaxe abstraite de mini-Python (complet)
Lexer.java, MyLexer.java	l'analyseur lexical (complet)
parser.java, sym.java	l'analyseur syntaxique (complet)
Interp.java	l'interprète (à compléter)
Main.java	le programme principal (complet)
Makefile	pour automatiser la compilation (complet)
La bibliothèque java-cup-11a-runtime.jar qui est contenue dans le sous-répertoire lib/ doit être ajoutée au projet (avec Java Build Path -> Libraries -> Add JARs). Le code fourni compile mais est incomplet. Les endroits à compléter contiennent throw new Todo().
Le programme accepte un nom de fichier à interpréter. En cas d'absence, il utilise le fichier test.py.

Question 1. Expressions arithmétiques

On ne considère pour l'instant que des expressions arithmétiques sans variables. Compléter l'interprète pour de telles expressions. Tester sur le programme suivant :
print 1 + 2*3
print (3*3 + 4*4) / 5
print 10-3-4
dont le résultat doit être
7
5
3
Les opérations division et modulo doivent signaler une erreur en cas de division par zéro, en levant l'exception Error.
Pour tester facilement, on peut éditer le fichier test.py fourni et lancer Main.

Question 2. Expressions booléennes et conditionnelles

Compléter les méthodes isTrue et isFalse de la classe Value qui déterminent respectivement si une valeur est vraie ou fausse. En Python, la valeur None, le booléen False, l'entier 0, la chaîne vide "" et la liste vide [] sont considérées comme fausses et les autres valeurs comme vraies.
Compléter ensuite l'interprète avec les opérations de comparaison et les opérations and, or et not. En Python, la comparaison est structurelle. On pourra utiliser la méthode compareTo déjà écrite dans la classe Value.

Compléter enfin l'interprète avec la conditionnelle (construction Sif).

Tester sur le programme suivant :

print not True and 1/0==0
print 1<2
if False or True:
    print "ok"
else:
    print "oups"
dont le résultat doit être
False
True
ok
Question 3. Variables

Pour gérer les variables (du programme principal mais aussi les variables locales et paramètres) on va utiliser une table de hachage associant à des noms de variables des valeurs. La table est dans le champ vars de la classe Interp.
Compléter l'interprète pour que l'on puisse accéder aux variables (constructeurs Eleft et Lident). Tenter d'accéder à une variable qui ne se trouve pas encore dans la table doit provoquer une erreur.

De même, compléter l'interprète pour que l'on puisse affecter une variable (méthodes interp(Sassign s) et assign(Lident lv, Expr e)). Cette fois, la variable peut ou non se trouver dans la table. Dans le premier cas, sa valeur est modifiée.

Enfin, compléter l'interprète pour que l'on puisse concaténer deux chaînes de caractères avec l'opération +.

Tester sur le programme suivant :

x = 41
x = x+1
print x
b = True and False
print b
s = "hello" + " world!"
print s
dont le résultat doit être
42
False
hello world!
Question 4. Fonctions

On va ajouter maintenant le traitement des fonctions. Ces dernières sont stockées dans la table globale functions. À chaque nom de fonction est associée sa définition, c'est-à-dire la liste des paramètres de la fonction et l'instruction qui constitue le corps de la fonction. Le code fourni se charge du remplissage de cette table.
Compléter ensuite l'interprète avec l'appel de fonction. Pour un appel de la forme f(e1,...,en), à une fonction f de la forme def f(x1,...,xn): s il faut construire un nouvel environnement qui associe à chaque argument formel xi la valeur de ei. Cela veut dire construire un nouvel objet de la classe Interp. On peut alors interpréter l'instruction s (le corps de la fonction) dans ce nouvel environnement. L'instruction return sera interprétée en utilisant l'exception Return (déjà définie). Si l'exécution du corps de la fonction termine sans rencontrer d'instruction return, la valeur None est renvoyée.

Tester sur le programme suivant :

def fact(n):
    if n <= 1: return 1
    return n * fact(n-1)

print fact(10)
dont le résultat doit être
3628800
Question 5. Listes

Ajouter enfin le support des listes. Pour cela, compléter l'interprète pour que l'on puisse concaténer deux listes avec l'opération +, pour interpréter l'appel aux primitives len (longueur d'une liste) et range (liste [0, 1, 2, ..., n-1]), et enfin pour interpréter les constructions [e1, e2, ..., en] et e1[e2].
Compléter ensuite l'interprète avec l'affectation d'un élément de liste.

Enfin, compléter l'interprète avec la construction for de mini-Python. La construction for x in e: s affecte la variable x successivement aux différents éléments de la liste e et exécute à chaque fois l'instruction s. L'expression e doit être évaluée une seule fois.

Tester sur le programme donné au début du sujet. Le résultat doit être :

0
1
89
267914296
Question 6. D'autres tests

Des tests positifs et négatifs sont fournis. Pour lancer votre interprète sur ces tests, faire make tests.
