srcdir := src
libdir := lib
bindir := bin
mainpackage := app
mainfile := Algorithm
mainargs:= 4
sourcefiles := $(shell find $(srcdir) -name '*.java')

classfiles  = $(sourcefiles:.java=.class)

classpath =  \
$(libdir)/*:$(bindir):$(srcdir)

VPATH := $(shell find src -type d -print | tr '\012' ':' | sed 's/:$$//')

vpath %.jar $(libdir)/*
vpath %.class $(bindir)/*

destination = \
$(bindir)

build: $(classfiles)

%.class: %.java
	javac -g -d $(destination) -cp $(classpath) $< 

run: build
	java -cp $(classpath):. $(mainpackage).$(mainfile) $(mainargs)


clean:
	rm -R $(bindir)