#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>

#include "index.h"
#include "http.h"
#include "route_utils.h"

int route_index(connection *conn, const char *req)
{
    char resp[BUF_SIZE];
    content *data = (content *)malloc(sizeof(content));
    response *res = (response *)malloc(sizeof(response));

    strcpy(data->type, "application/json");
    strcpy(data->length, "500");
    strcpy(data->content, "{\"status\": 200, \"content\": \"Hello, World!\"}");
    
    set_header(res, 200);
    set_content(res, data);
    strcpy(resp, build_resp(res));

    send(conn->client_fd, resp, strlen(resp), 0);
    
    free(res);
    free(data);
    return 200;
}
