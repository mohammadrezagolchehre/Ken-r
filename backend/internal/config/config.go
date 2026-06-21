// Package config loads runtime configuration from the environment.
// All keys are prefixed KENAR_. No secrets live in the repo.
package config

import (
	"fmt"
	"os"
	"strings"
)

// Config holds all runtime settings for the backend.
type Config struct {
	HTTPAddr      string
	DefaultLocale string
	I18nDir       string

	DBDSN         string
	RedisAddr     string
	RedisPassword string

	MinIOEndpoint  string
	MinIOAccessKey string
	MinIOSecretKey string

	SMSProvider      string
	KavenegarAPIKey  string
	PushProvider     string
	PusheToken       string
}

// Load reads configuration from the environment, applying sane defaults.
// It returns an error only for values that cannot have a safe default.
func Load() (Config, error) {
	c := Config{
		HTTPAddr:      env("KENAR_HTTP_ADDR", ":8080"),
		DefaultLocale: env("KENAR_DEFAULT_LOCALE", "fa"),
		I18nDir:       env("KENAR_I18N_DIR", "i18n"),

		DBDSN:         env("KENAR_DB_DSN", ""),
		RedisAddr:     env("KENAR_REDIS_ADDR", "localhost:6379"),
		RedisPassword: env("KENAR_REDIS_PASSWORD", ""),

		MinIOEndpoint:  env("KENAR_MINIO_ENDPOINT", "localhost:9000"),
		MinIOAccessKey: env("KENAR_MINIO_ACCESS_KEY", ""),
		MinIOSecretKey: env("KENAR_MINIO_SECRET_KEY", ""),

		SMSProvider:     env("KENAR_SMS_PROVIDER", "kavenegar"),
		KavenegarAPIKey: env("KENAR_KAVENEGAR_API_KEY", ""),
		PushProvider:    env("KENAR_PUSH_PROVIDER", "pushe"),
		PusheToken:      env("KENAR_PUSHE_TOKEN", ""),
	}

	if c.DefaultLocale != "fa" && c.DefaultLocale != "en" {
		return Config{}, fmt.Errorf("config: KENAR_DEFAULT_LOCALE must be fa or en, got %q", c.DefaultLocale)
	}
	return c, nil
}

func env(key, def string) string {
	if v, ok := os.LookupEnv(key); ok && strings.TrimSpace(v) != "" {
		return v
	}
	return def
}
