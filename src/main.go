package main

import (
	"flag"
	"fmt"
	"io"
	"os"

	"github.com/devproje/plog/level"
	"github.com/devproje/plog/log"
	"github.com/devproje/project-api/src/conf"
	"github.com/devproje/project-api/src/middleware"
	"github.com/devproje/project-api/src/routes"
	"github.com/devproje/project-api/src/utils"
	"github.com/gin-gonic/gin"
	"github.com/thinkerou/favicon"
)

const (
	VERSION = "v2.0.0"
)

var (
	mode string
	port int
)

func init() {
	flag.StringVar(&mode, "mode", "debug", "Service debug mode")
	flag.IntVar(&port, "port", 3000, "Service port number")
	flag.Parse()

	log.SetOutput(io.MultiWriter(os.Stdout, utils.Logging()))

	switch mode {
	case "release":
		log.SetLevel(level.Info)
		gin.SetMode(gin.ReleaseMode)
	case "debug":
		log.SetLevel(level.Trace)
		gin.SetMode(gin.DebugMode)

		log.Warnln("You're now into debug mode. If you want a change production mode, please add flag '-mode=release'.")
	default:
		log.Fatalln(fmt.Sprintf("'%s' not available mode type. (available mode: release, debug)", mode))
	}

	conf.VERSION = VERSION
}

func main() {
	app := gin.Default()

	app.Use(favicon.New("./static/favicon.ico"))
	app.Use(middleware.Cors)

	routes.Router(app)
	err := app.Run(fmt.Sprintf(":%d", port))
	if err != nil {
		log.Fatalln(err)
	}
}
