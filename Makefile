MAKEFILE_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
PROJECT_ROOT := $(MAKEFILE_DIR)

# list of source files
JAVA_SRC := codegen/Parser.java \
	codegen/Scanner.java \
	codegen/SimpleLang.java

CLASS_FILES := $(patsubst %.java,bin/%.class,$(JAVA_SRC))


all: $(CLASS_FILES)

codegen/Parser.java: codegen/SimpleLang.ATG codegen/Parser.frame codegen/Scanner.frame
	java -jar $(PROJECT_ROOT)/libs/Coco.jar codegen/SimpleLang.ATG

bin:
	mkdir -p bin

bin/codegen/SimpleLang.class bin/codegen/Parser.class bin/codegen/Scanner.class: $(JAVA_SRC) bin
	javac -d bin $(JAVA_SRC)
