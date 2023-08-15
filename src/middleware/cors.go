package middleware

import (
	"time"

	"github.com/gin-gonic/gin"
)

func Cors(context *gin.Context) {
	context.Header("Access-Control-Allow-Origin", "*")
	context.Header("Access-Control-Allow-Methods", "GET, POST")
	context.Header("Access-Control-Max-Age", (12 * time.Hour).String())
}
