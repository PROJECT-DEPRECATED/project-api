package routes

import (
	"fmt"
	"net/http"
	"strconv"
	"time"

	"github.com/devproje/project-api/src/api"
	"github.com/devproje/project-api/src/conf"
	"github.com/devproje/project-api/src/utils"
	"github.com/gin-gonic/gin"
)

func Router(engine *gin.Engine) {
	engine.GET("/", func(ctx *gin.Context) {
		ctx.Redirect(301, "https://github.com/devproje/project-api.git")
	})
	engine.GET("/version", func(ctx *gin.Context) {
		ctx.JSON(200, gin.H{
			"status":  200,
			"version": conf.VERSION,
		})
	})

	v2 := engine.Group("/v2")
	{
		hangang := func(ctx *gin.Context, area int) bool {
			before := time.Now()
			data, err := api.GetHangang(area)
			if !utils.InternlServerErrHandler(ctx, err) {
				return false
			}

			restime := time.Since(before)

			ctx.JSON(200, gin.H{
				"status":       200,
				"area":         data.WaterPOS.Row[0].SiteID,
				"date":         data.WaterPOS.Row[0].Date,
				"ph":           data.WaterPOS.Row[0].PH,
				"temp":         data.WaterPOS.Row[0].Temp,
				"time":         data.WaterPOS.Row[0].Time,
				"respond_time": fmt.Sprintf("%dms", restime.Milliseconds()),
			})

			return true
		}

		v2.GET("/hangang", func(ctx *gin.Context) {
			if !hangang(ctx, 2) {
				return
			}
		})
		v2.GET("/hangang/:area", func(ctx *gin.Context) {
			area, err := strconv.ParseInt(ctx.Param("area"), 0, 0)
			if !utils.InternlServerErrHandler(ctx, err) {
				return
			}

			if int(area) < 1 || int(area) > 5 {
				utils.NotFoundHandler(ctx, fmt.Errorf("not available area %d (available code: 1 ~ 5)", int(area)))
				return
			}

			if !hangang(ctx, int(area)) {
				return
			}
		})
		v2.GET("/mcprofile/:username", func(ctx *gin.Context) {
			username := ctx.Param("username")
			before := time.Now()
			mojang, status := api.GetMCProfile(username)
			respondTime := time.Since(before)

			switch status {
			case 404:
				utils.NotFoundHandler(ctx, fmt.Errorf("%s", http.StatusText(status)))
			case 400:
				ctx.JSON(status, gin.H{
					"status": status,
					"err":    http.StatusText(status),
				})
			case 204:
				ctx.JSON(status, gin.H{
					"status": status,
					"err":    http.StatusText(status),
				})
			case 200:
				ctx.JSON(status, gin.H{
					"status":       status,
					"respond_time": fmt.Sprintf("%sms", strconv.FormatInt(respondTime.Milliseconds(), 10)),
					"username":     mojang.Username,
					"unique_id":    api.RefactorUUID(mojang.UUID),
					"skin_url":     mojang.Textures.Skin.Url,
				})
			}
		})
	}
}
