#ifndef __HTTP_H__
#define __HTTP_H__

#include <stdint.h>

#define BUF_SIZE 4096

int client_sock(uint16_t port);
void open();

#endif
