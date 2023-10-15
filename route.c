#include <stdio.h>
#include <errno.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdbool.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/socket.h>

#include "http.h"
#include "route.h"
#include "index.h"
#include "route_utils.h"

int route_404(request *data)
{
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
        
}

void favicon(connection *conn, const char *req)
{
    char buf[BUF_SIZE];
    char resp[BUF_SIZE];
    char fpath[BUF_SIZE];
    FILE *file;

    sscanf(req, "GET %s", fpath);
    if (fpath[0] == '/')
        memmove(fpath, fpath + 1, strlen(fpath));
    
    if (strcmp(fpath, "favicon.ico"))
        return;

    file = fopen("./static/favicon.ico", "rb");
    if (!file)
        return;

    fseek(file, 0L, SEEK_END);
    long content_size = ftell(file);
    fseek(file, 0L, SEEK_SET);

    snprintf(resp, BUF_SIZE, "HTTP/1.1 200 OK\r\nContent-Length: %ld\r\n\r\n", content_size);
    send(conn->client_fd, resp, strlen(resp), 0);

    size_t bytes_read;
    while ((bytes_read = fread(buf, 1, sizeof(buf), file)) > 0) {
        send(conn->client_fd, buf, bytes_read, 0);
    }

    fclose(file);
}

int route(request *data, char *uri, function func)
{
    if (strcmp(data->uri, uri)) {
        route_404(data);
        return 404;
    }

    int status = func(data->conn, data->req);
    if (status == 404)
        route_404(data);

    return status;
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

    favicon(conn, req);
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
