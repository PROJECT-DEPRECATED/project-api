package api

import (
	"encoding/base64"
	"encoding/json"
	"fmt"

	"github.com/devproje/project-api/src/utils"
)

type extractor struct {
	Username string `json:"name"`
	UUID     string `json:"id"`
}

type EncodedMojangAPI struct {
	Username   string `json:"name"`
	UUID       string `json:"id"`
	Properties []struct {
		Name  string `json:"name"`
		Value string `json:"value"`
	} `json:"properties"`
}

type MojangAPI struct {
	UUID     string `json:"profileId"`
	Username string `json:"profileName"`
	Textures struct {
		Skin struct {
			Url string `json:"url"`
		} `json:"SKIN"`
	} `json:"textures"`
}

func RefactorUUID(uuid string) string {
	var dash = func(str string, index int) string {
		return fmt.Sprintf("%s-%s", str[:index], str[index:])
	}
	i := 8
	return dash(dash(dash(dash(uuid, i), i+5), i+5*2), i+5*3)
}

func getUUID(username string) (*extractor, int) {
	url := fmt.Sprintf("https://api.mojang.com/users/profiles/minecraft/%s", username)
	data, err := utils.GET[extractor](url)
	if err != nil {
		return nil, 204
	}

	return data, 200
}

func decodeProperty(encoded EncodedMojangAPI) (*MojangAPI, error) {
	bytes, err := base64.StdEncoding.DecodeString(encoded.Properties[0].Value)
	if err != nil {
		return nil, err
	}

	var decoded MojangAPI
	err = json.Unmarshal(bytes, &decoded)
	if err != nil {
		return nil, err
	}

	return &decoded, nil
}

func GetMCProfile(username string) (*MojangAPI, int) {
	var uuid, status = getUUID(username)
	if status != 200 {
		return nil, status
	}

	url := fmt.Sprintf("https://sessionserver.mojang.com/session/minecraft/profile/%s", uuid.UUID)
	e, err := utils.GET[EncodedMojangAPI](url)
	if err != nil {
		return nil, 204
	}

	data, err := decodeProperty(*e)
	if err != nil {
		return nil, 500
	}

	return data, status
}
