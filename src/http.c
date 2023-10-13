#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <unistd.h>
#include <stdint.h>
#include <stdlib.h>
#include <stdbool.h>
#include <pthread.h>
#include <arpa/inet.h>
#include <sys/socket.h>

#include "http.h"
#include "router/route.h"

void on_accept(int server_fd)
{
    struct sockaddr_in client_addr;
    socklen_t client_len = sizeof(client_addr);
    int client_fd = accept(server_fd, (struct sockaddr *)&client_addr, &client_len);

    if (client_fd < 0) {
        perror("error occurred to accepting connection");
        return;
    }

    connection *conn = (connection *)malloc(sizeof(connection));
    conn->client_fd = client_fd;

    router(conn, client_addr);
}

void open_socket(uint16_t port)
{
    int server_fd;
    int option = 1;
    struct sockaddr_in server_addr;

    server_fd = socket(AF_INET, SOCK_STREAM, 0);
    setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR, &option, sizeof(option));
    if (server_fd < 0) {
        perror("error occurred to creating socket");
        return;
    }

    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(port);
    server_addr.sin_addr.s_addr = INADDR_ANY;

    if (bind(server_fd, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
        perror("error occurred to binding socket");
        return;
    }

    if (listen(server_fd, 10) < 0) {
        perror("error occurred to listening connection");
        return;
    }

    printf("server listening on port %d\n", port);

    do {
        on_accept(server_fd);
    } while (true);
}

// void open(uint16_t port)
// {
//     int option = 1;
//     sockfd = socket(AF_INET, SOCK_STREAM, 0);
//     setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &option, sizeof(option));
//     if (sockfd == -1) {
//         perror("error occurred to socket creating");
//         return;
//     }

//     host_addrlen = sizeof(host_addr);
//     client_addrlen = sizeof(client_addr);

//     host_addr.sin_family = AF_INET;
//     host_addr.sin_port = htons(port);
//     host_addr.sin_addr.s_addr = htonl(INADDR_ANY);

//     if (bind(sockfd, (struct sockaddr *)&host_addr, host_addrlen) != 0) {
//         perror("error occurred to binding socket");
//         return;
//     }

//     if (listen(sockfd, SOMAXCONN) != 0) {
//         perror("error occurred to listening connections");
//         return;
//     }

//     printf("listening server port: %u\n", port);
//     do {
//         run();
//     } while (true);
// }

// void run()
// {
//     clientfd = accept(sockfd,
//                      (struct sockaddr *)&host_addr,
//                      (socklen_t *)&host_addrlen);
//     if (clientfd < 0) {
//         perror("error occurred to accept connection");
//         return;
//     }

//     int valread = read(clientfd, buf, BUF_SIZE);
//     if (valread < 0) {
//         perror("error occurred to read");
//         return;
//     }

//     router(uri, method, version);
//     sscanf(buf, "%s %s %s", method, uri, version);
//     printf("[%s] %s %s \t%s:%u\n",
//         method, version, uri,
//         inet_ntoa(client_addr.sin_addr),
//         ntohs(client_addr.sin_port)
//     );

//     close(clientfd);
// }
