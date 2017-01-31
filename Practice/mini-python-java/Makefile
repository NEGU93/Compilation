
JAVACUP   := java -jar ../../lib/java-cup-11a.jar

all: src/mini_python/Lexer.java src/mini_python/parser.java

.PHONY: tests
tests:
	bash run-tests

src/mini_python/parser.java src/mini_python/sym.java: src/mini_python/Parser.cup
	cd src/mini_python/ && $(JAVACUP) -package mini_python Parser.cup

%.java: %.flex
	rm -f $@
	jflex $<
