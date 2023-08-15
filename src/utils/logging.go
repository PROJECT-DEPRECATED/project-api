package utils

import (
	"log"
	"os"
)

func Logging() *os.File {
	f, err := os.OpenFile("log.txt", os.O_WRONLY|os.O_CREATE|os.O_APPEND, 0664)
	if err != nil {
		log.Fatalf("Failed to create 'log.txt' file\n%v", err)
	}

	return f
}
