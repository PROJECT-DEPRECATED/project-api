#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdbool.h>
#include <arpa/inet.h>

#include "http.h"
#include "route.h"
#include "index.h"
#include "route_utils.h"

int route(request *data, char *uri, function func)
{
    if (strcmp(data->uri, uri)) {
        char resp[BUF_SIZE];
        response *res = (response *)malloc(sizeof(response));
        content d;
        strcpy(d.type, "text/html");
        strcpy(d.content, "<h1>404 Not Found</h1>");

        set_header(res, 404);
        set_content(res, &d);
        strcpy(resp, build_resp(res));

        send(data->conn->client_fd, resp, strlen(resp), 0);
        free(res);
        return 404;
    }

    return func(data->conn, data->req);
}

void router(connection *conn, const char *req)
{
    int status;
    char method[BUF_SIZE], version[BUF_SIZE], uri[BUF_SIZE];
    sscanf(req, "%s %s %s", method, uri, version);
    request *data = (request *)malloc(sizeof(request));
    data->conn = conn;
    strcpy(data->uri, uri);
    strcpy(data->req, req);

    status = route(data, "/", route_index);

    printf("[%s] %s %d %s \t%s:%u\n",
        method, version, status, uri,
        inet_ntoa(conn->client_addr.sin_addr),
        ntohs(conn->client_addr.sin_port)
    );

    close(conn->client_fd);
    free(conn);
    free(data);
}
