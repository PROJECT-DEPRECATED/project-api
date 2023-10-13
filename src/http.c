#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <unistd.h>
#include <stdint.h>
#include <stdbool.h>
#include <arpa/inet.h>
#include <sys/socket.h>

#include "http.h"

void new_connection(uint16_t port)
{
    char buffer[BUF_SIZE];
    char resp[] = "HTTP/1.0 200 OK\r\n"
                  "Server: project-api/api\r\n"
                  "Content-type: application/json\r\n\r\n"
                  "{\"status\": 200, \"content\": \"Hello, World!\"}\r\n";
    
    int sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd == -1) {
        perror("error occurred to socket creating");
        return;
    }
    printf("socket created successfully\n");

    struct sockaddr_in host_addr;
    int host_addrlen = sizeof(host_addr);

    struct sockaddr_in client_addr;
    int client_addrlen = sizeof(client_addr);

    host_addr.sin_family = AF_INET;
    host_addr.sin_port = htons(port);
    host_addr.sin_addr.s_addr = htonl(INADDR_ANY);

    if (bind(sockfd, (struct sockaddr *)&host_addr, host_addrlen) != 0) {
        perror("error occurred to binding socket");
        return;
    }

    printf("socket successfully bind to address\n");

    if (listen(sockfd, SOMAXCONN) != 0) {
        perror("error occurred to listening connections");
        return;
    }
    
    printf("server listening for connections\n");

    do {
        int newsockfd = accept(sockfd,
                              (struct sockaddr *)&host_addr,
                              (socklen_t *)&host_addrlen);
        if (newsockfd < 0) {
            perror("error occurred to accept connection");
            return;
        }

        int valread = read(newsockfd, buffer, BUF_SIZE);
        if (valread < 0) {
            perror("error occurred to read");
            return;
        }

        char method[BUF_SIZE], uri[BUF_SIZE], version[BUF_SIZE];
        sscanf(buffer, "%s %s %s", method, uri, version);
        printf("[%s] %s %s \t%s:%u\n", method, version, uri,
               inet_ntoa(client_addr.sin_addr),
               ntohs(client_addr.sin_port));

        int valwrite = write(newsockfd, resp, strlen(resp));
        if (valwrite < 0) {
            perror("error occurred to write");
            return;
        }

        close(newsockfd);
    } while (true);
}
