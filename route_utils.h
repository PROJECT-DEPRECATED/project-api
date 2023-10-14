#ifndef __ROUTE_UTILS_H__
#define __ROUTE_UTILS_H__

#include "route.h"
#include "http.h"
#include "route_utils.h"

typedef struct {
    char type[BUF_SIZE];
    char length[BUF_SIZE];
    char content[BUF_SIZE];
} content;

typedef struct {
    char header[BUF_SIZE];
    char location[BUF_SIZE];
    content con[BUF_SIZE];
} response;

void set_header(response *resp, int status);
void set_location(response *resp, char *uri);
void set_content(response *resp, content *data);
char *build_resp(response *resp);

#endif