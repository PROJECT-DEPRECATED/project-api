package api

import (
	"fmt"

	"github.com/devproje/project-api/src/utils"
)

// Request URL from seoul public api
const HANGANG_URL = "http://openapi.seoul.go.kr:8088/sample/json/WPOSInformationTime"

type Hangang struct {
	WaterPOS struct {
		Row []struct {
			Date   string `json:"MSR_DATE"`
			Time   string `json:"MSR_TIME"`
			SiteID string `json:"SITE_ID"`
			Temp   string `json:"W_TEMP"`
			PH     string `json:"W_PH"`
		} `json:"row"`
	} `json:"WPOSInformationTime"`
}

// Get han river's data
//
//	Area code
//	- 1: Tancheon
//	- 2: Jungnangchecon
//	- 3: Anyangcheon
//	- 4: Seonyudo
//	- 5: Noryangjin
func GetHangang(area int) (*Hangang, error) {
	data, err := utils.GET[Hangang](fmt.Sprintf("%s/%d/%d", HANGANG_URL, area, area))
	if err != nil {
		return nil, err
	}

	return data, err
}
