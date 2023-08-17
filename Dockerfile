FROM golang:1.21.0-alpine3.17

# Ready to build
RUN apk add make
WORKDIR /opt/app/src
COPY . .

# Build
RUN ./configure
RUN make
RUN mv ./project-api /opt/app

# Grab static data
WORKDIR /opt/app
RUN cp -r ./src/static /opt/app

# Remove build source
RUN rm -rf ./src

ENTRYPOINT [ "/opt/app/project-api", "-mode=release" ]
