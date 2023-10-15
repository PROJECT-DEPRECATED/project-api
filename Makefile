CC=gcc
CFLAG=-Wall -Wextra
OBJS=main.o http.o route.o \
	 route_utils.o index.o
OUTPUT=project-api

all: $(OUTPUT)

$(OUTPUT): $(OBJS)
	gcc -o $@ $^

main.o:	 	   main.c http.h
route.o: 	   route.c route.h
http.o:  	   http.c http.h route.h
index.o: 	   index.c index.h route.h http.h
route_utils.o: route_utils.c route_utils.h route.h http.h

clean:
	rm -f $(OBJS)
	rm -f $(OUTPUT)

.PHONY: all, clean

