#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdbool.h>
#include <arpa/inet.h>

#include "route.h"
#include "index.h"
#include "../http.h"

void set_header(response *resp, int status)
{
    char str[BUF_SIZE];
    char code[BUF_SIZE];
    switch (status) {
    case 200:
        strcpy(code, "OK");
        break;
    case 301:
        strcpy(code, "Moved Permanently");
        break;
    case 401:
        strcpy(code, "Unauthorized");
        break;
    case 404:
        strcpy(code, "Not Found");
        break;
    case 500:
        strcpy(code, "Internal Server Error");
        break;
    default:
        perror("not available status code");
        return;
    }

    sprintf(str, "HTTP/1.1 %d %s\n", status, code);
    strcpy(resp->header, str);
}

void route(connection *conn, char *uri, char *ro, function func)
{
    if (strcmp(uri, ro)) {
        char resp[BUF_SIZE] = "HTTP/1.1 404 Not Found\r\n"
                              "Content-type: text/html\r\n\r\n"
                              "<h1>404 Not Found</h1>\r\n";
        send(conn->client_fd, resp, strlen(resp), 0);
        return;
    }

    func(conn);
}

void router(connection *conn, struct sockaddr_in client_addr)
{
    char buf[BUF_SIZE];

    char uri[BUF_SIZE];
    char method[BUF_SIZE];
    char version[BUF_SIZE];
    

    int valread = read(conn->client_fd, buf, BUF_SIZE);
    if (valread < 0) {
        perror("error occurred to read");
        return;
    }

    sscanf(buf, "%s %s %s", method, uri, version);
    printf("[%s] %s %s \t%s:%u\n",
        method, version, uri,
        inet_ntoa(client_addr.sin_addr),
        ntohs(client_addr.sin_port)
    );
    
    route(conn, uri, "/", route_index);

    close(conn->client_fd);
    free(conn);
}