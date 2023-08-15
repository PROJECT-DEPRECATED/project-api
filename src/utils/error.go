package utils

import (
	"github.com/devproje/plog/log"
	"github.com/gin-gonic/gin"
)

func errorHandle(ctx *gin.Context, err error, status int) {
	log.Errorln(err)
	ctx.JSON(status, gin.H{
		"status": status,
		"err":    err.Error(),
	})
}

func NotFoundHandler(ctx *gin.Context, err error) bool {
	if err == nil {
		return true
	}

	errorHandle(ctx, err, 404)
	return false
}

func InternlServerErrHandler(ctx *gin.Context, err error) bool {
	if err == nil {
		return true
	}

	errorHandle(ctx, err, 500)
	return false
}
