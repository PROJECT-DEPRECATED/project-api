#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <unistd.h>
#include <stdint.h>
#include <stdbool.h>
#include <pthread.h>
#include <arpa/inet.h>
#include <sys/socket.h>

#include "http.h"
#include "router.h"

int sockfd;
int clientfd; // client socket
struct sockaddr_in host_addr;
struct sockaddr_in client_addr;

char uri[BUF_SIZE];
char method[BUF_SIZE];
char version[BUF_SIZE];

char buf[BUF_SIZE];

int host_addrlen;
int client_addrlen;

void run();

int client_sock()
{
    return clientfd;
}

void open(uint16_t port)
{
    int option = 1;
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &option, sizeof(option));
    if (sockfd == -1) {
        perror("error occurred to socket creating");
        return;
    }

    host_addrlen = sizeof(host_addr);
    client_addrlen = sizeof(client_addr);

    host_addr.sin_family = AF_INET;
    host_addr.sin_port = htons(port);
    host_addr.sin_addr.s_addr = htonl(INADDR_ANY);

    if (bind(sockfd, (struct sockaddr *)&host_addr, host_addrlen) != 0) {
        perror("error occurred to binding socket");
        return;
    }

    if (listen(sockfd, SOMAXCONN) != 0) {
        perror("error occurred to listening connections");
        return;
    }

    printf("listening server port: %u\n", port);
    do {
        run();
    } while (true);
}

void run()
{
    clientfd = accept(sockfd,
                     (struct sockaddr *)&host_addr,
                     (socklen_t *)&host_addrlen);
    if (clientfd < 0) {
        perror("error occurred to accept connection");
        return;
    }

    int valread = read(clientfd, buf, BUF_SIZE);
    if (valread < 0) {
        perror("error occurred to read");
        return;
    }

    router(uri, method, version);
    sscanf(buf, "%s %s %s", method, uri, version);
    printf("[%s] %s %s \t%s:%u\n",
        method, version, uri,
        inet_ntoa(client_addr.sin_addr),
        ntohs(client_addr.sin_port)
    );

    close(clientfd);
}
