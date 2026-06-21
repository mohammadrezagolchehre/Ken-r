// Package httpapi is the HTTP transport adapter. It wires routes to use cases
// and translates domain results/errors into JSON responses with localized text.
package httpapi

import (
	"encoding/json"
	"log/slog"
	"net/http"

	"github.com/kenar/backend/internal/platform/i18n"
)

// Deps are the collaborators the HTTP layer needs. Use-case services are added
// here as features land (pairing, auth, widgets ...).
type Deps struct {
	Logger *slog.Logger
	I18n   *i18n.Bundle
}

// NewRouter builds the HTTP handler with middleware applied.
func NewRouter(d Deps) http.Handler {
	mux := http.NewServeMux()

	mux.HandleFunc("GET /healthz", func(w http.ResponseWriter, r *http.Request) {
		writeJSON(w, http.StatusOK, map[string]string{"status": "ok"})
	})

	// Locale demo / sanity endpoint: returns a localized greeting so the
	// bilingual pipeline is verifiable end-to-end from day one.
	mux.HandleFunc("GET /v1/locale", func(w http.ResponseWriter, r *http.Request) {
		loc := LocaleFrom(r.Context())
		writeJSON(w, http.StatusOK, map[string]string{
			"locale":  loc,
			"message": d.I18n.T(loc, "invite.accepted", nil),
		})
	})

	return LocaleMiddleware(d.I18n)(logRequests(d.Logger)(mux))
}

func logRequests(log *slog.Logger) func(http.Handler) http.Handler {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			log.Info("request", "method", r.Method, "path", r.URL.Path)
			next.ServeHTTP(w, r)
		})
	}
}

func writeJSON(w http.ResponseWriter, status int, body any) {
	w.Header().Set("Content-Type", "application/json; charset=utf-8")
	w.WriteHeader(status)
	_ = json.NewEncoder(w).Encode(body)
}
