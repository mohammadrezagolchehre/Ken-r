// Package logger provides a small structured-logging helper over slog.
package logger

import (
	"log/slog"
	"os"
)

// New returns a JSON structured logger writing to stdout.
func New() *slog.Logger {
	h := slog.NewJSONHandler(os.Stdout, &slog.HandlerOptions{Level: slog.LevelInfo})
	return slog.New(h)
}
