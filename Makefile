CC=gcc
CFLAG=-Wall -Wextra
OBJS=src/main.o src/http.o src/router.o
OUTPUT=project-api

all: $(OUTPUT)

$(OUTPUT): $(OBJS)
	gcc -o $@ $^

main.o:   main.c http.h
http.o:   http.c http.h router.h
router.o: router.c router.h http.h

clean:
	rm -f $(OBJS)
	rm -f $(OUTPUT)

.PHONY: all, clean

