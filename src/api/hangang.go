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

	for i, d := range data.WaterPOS.Row {
		rawDate := d.Date
		day := fmt.Sprintf("%c%c", rawDate[len(rawDate)-2], rawDate[len(rawDate)-1])
		month := fmt.Sprintf("%c%c", rawDate[len(rawDate)-4], rawDate[len(rawDate)-3])
		year := fmt.Sprintf("%c%c%c%c", rawDate[0], rawDate[1], rawDate[2], rawDate[3])

		data.WaterPOS.Row[i].Date = fmt.Sprintf("%s-%s-%s", year, month, day)
	}

	return data, err
}
