CC=gcc
CFLAG=-Wall -Wextra
OBJS=src/main.o src/http.o src/router/route.o src/router/index.o
OUTPUT=project-api

all: $(OUTPUT)

$(OUTPUT): $(OBJS)
	gcc -o $@ $^

main.o:   main.c http.h
http.o:   http.c http.h router/route.h
route.o: router/route.c router/route.h http.h
index.o: router/index.c router/index.h router/route.h http.h

clean:
	rm -f $(OBJS)
	rm -f $(OUTPUT)

.PHONY: all, clean

