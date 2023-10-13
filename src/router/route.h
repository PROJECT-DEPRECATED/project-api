#ifndef __ROUTER_H__
#define __ROUTER_H__

#include <arpa/inet.h>

#include "../http.h"

typedef void(*function)(connection *conn);
typedef struct {
    char header[BUF_SIZE];
    char location[BUF_SIZE];
    char content_type[BUF_SIZE];
    char content_length[BUF_SIZE];
    char content[BUF_SIZE];
} response;

void router(connection *conn, struct sockaddr_in client_addr);

#endif