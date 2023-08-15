TARGET=project-api
SOURCES=src/*.go src/**/*.go
MAIN=src/main.go

TAG=default

$(TARGET): $(MAIN) $(SOURCES)
	go build -o $(TARGET) $(MAIN)

debug:
	go run main.go -debug

docker:
	docker-compose build --no-cache --build-arg	FILE_NAME="$(TARGET)"

publish:
	./scripts/publish.sh $(TAG)

clean:
	rm $(TARGET)

rmlog:
	rm log.txt
