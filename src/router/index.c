#include <string.h>
#include <sys/socket.h>

#include "index.h"
#include "../http.h"

void route_index(connection *conn)
{
    char resp[BUF_SIZE] = "HTTP/1.1 200 OK\r\n"
                          "Content-type: application/json\r\n\r\n"
                          "{\"status\": 200, \"content\": \"Hello, World!\"}\r\n";
    send(conn->client_fd, resp, strlen(resp), 0);
}
