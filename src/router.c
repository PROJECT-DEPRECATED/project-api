#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdbool.h>

#include "http.h"
#include "router.h"

struct response {
    char header[BUF_SIZE];
    char content_type[BUF_SIZE];
    char content[BUF_SIZE];
    char location[BUF_SIZE];
};

struct respdata {
    char uri[BUF_SIZE];
    char method[BUF_SIZE];
};

void set_header(struct response *rf, int status)
{
    char str[BUF_SIZE];
    char name[BUF_SIZE];

    switch (status) {
    case 200:
        strcpy(name, "OK");
        break;
    case 301:
        strcpy(name, "Moved Permanently");
        break;
    case 404:
        strcpy(name, "Not Found");
        break;
    default:
        perror("not available status code");
        return;
    }

    sprintf(str, "HTTP/1.1 %d %s\r\n", status, name);

    strcpy(rf->header, str);
}

void set_content(struct response *rf, int type, char *content)
{
    char str[BUF_SIZE];
    char ctype[BUF_SIZE];

    switch (type) {
    case 0:
        strcpy(ctype, "text/html");
        break;
    case 1:
        strcpy(ctype, "application/json");
        break;
    default:
        perror("not available type");
        return;
    }

    sprintf(str, "Content-type: %s\r\n\r\n", ctype);
    strcpy(rf->content_type, str);

    strcpy(str, "");
    sprintf(str, "%s\r\n", content);
    strcpy(rf->content, str);
}

void set_location(struct response *rf, char *url)
{
    char str[BUF_SIZE];
    sprintf(str, "Location: %s\r\n", url);

    strcpy(rf->location, str);
}

char *conv(struct response *rf)
{
    return strcat(
        rf->header,
        strcat(
            rf->location,
            strcat(
                rf->content_type,     
                rf->content
            )
        )
    );
}

bool route(struct respdata data, char *uri, char *method)
{
    return !strcmp(uri, data.uri) && !strcmp(method, data.method);
}

bool group(char *uri, char *r)
{
    char *ret;
    ret = strstr(uri, r);
    if (ret) {
        return true;
    }

    return false;
}

void router(char *uri, char *method, char *version)
{
    struct response *rf = malloc(sizeof(struct response));
    char resp[BUF_SIZE];
    int valwrite;

    struct respdata index = {"/", "GET"};
    if (route(index, uri, method)) {
        set_header(rf, 301);
        set_location(rf, "https://github.com/devproje/project-api.git");
        strcpy(resp, conv(rf));

        valwrite = write(client_sock(), resp, strlen(resp));
        if (valwrite < 0) {
            perror("error occurred to write");
            return;
        }

        return;
    }
    
    struct respdata test = {"/v3/test", "GET"};
    if (route(test, uri, method)) {
        set_header(rf, 200);
        set_content(rf, 1, "{\"status\": 200, \"content\": \"Hello, World!\"}");
        strcpy(resp, conv(rf));

        valwrite = write(client_sock(), resp, strlen(resp));
        if (valwrite < 0) {
            perror("error occurred to write");
            return;
        }

        return;
    }

    set_header(rf, 404);
    set_content(rf, 0, "<h1>404 Not Found</h1>");
    strcpy(resp, conv(rf));

    valwrite = write(client_sock(), resp, strlen(resp));
    if (valwrite < 0) {
        perror("error occurred to write");
        return;
    }

    return;
}