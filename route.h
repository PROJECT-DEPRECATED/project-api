#ifndef __ROUTER_H__
#define __ROUTER_H__

#include <arpa/inet.h>

#include "http.h"

typedef int(*function)(connection *conn, const char *req);

typedef struct {
    connection *conn;
    char uri[BUF_SIZE];
    char req[BUF_SIZE];
} request;

void router(connection *conn, const char *req);

#endif