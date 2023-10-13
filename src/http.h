#ifndef __HTTP_H__
#define __HTTP_H__

#include <stdint.h>

#define BUF_SIZE 4096

typedef struct {
    int client_fd;
} connection;

void open_socket(uint16_t port);

#endif
