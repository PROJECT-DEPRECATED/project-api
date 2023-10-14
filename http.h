#ifndef __HTTP_H__
#define __HTTP_H__

#include <stdint.h>
#include <arpa/inet.h>

#define BUF_SIZE 4096

typedef struct {
    int client_fd;
    struct sockaddr_in client_addr;
} connection;

void open_socket(uint16_t port);

#endif
