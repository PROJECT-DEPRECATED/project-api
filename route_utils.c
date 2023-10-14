#include <stdio.h>
#include <string.h>

#include "http.h"
#include "route.h"
#include "route_utils.h"

void set_header(response *resp, int status)
{
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

    sprintf(resp->header, "HTTP/1.1 %d %s\r\n", status, code);
}

void set_location(response *resp, char *uri)
{
    sprintf(resp->location, "Location: %s\r\n", uri);
}

void set_content(response *resp, content *data)
{
    if (strcmp(data->type, "") || data->type != NULL)
        sprintf(resp->con->type, "Content-Type: %s\r\n", data->type);
    
    if (strcmp(data->length, "") || data->length != NULL)
        sprintf(resp->con->length, "Content-Length: %s\r\n", data->length);

    sprintf(resp->con->content, "\r\n%s\r\n", data->content);
}

char *build_resp(response *resp)
{
    return strcat(
        resp->header,
        strcat(
            resp->location,
            strcat(
                resp->con->type,
                strcat(
                    resp->con->length,
                    resp->con->content
                )
            )
        )
    );
}
