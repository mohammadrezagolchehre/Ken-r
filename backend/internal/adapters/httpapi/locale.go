package httpapi

import (
	"context"
	"net/http"
	"strings"

	"github.com/kenar/backend/internal/platform/i18n"
)

type ctxKey int

const localeKey ctxKey = iota

// LocaleMiddleware resolves the request locale (query ?lang=, then
// Accept-Language) to a supported locale and stores it in the context.
func LocaleMiddleware(bundle *i18n.Bundle) func(http.Handler) http.Handler {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			raw := r.URL.Query().Get("lang")
			if raw == "" {
				raw = firstLang(r.Header.Get("Accept-Language"))
			}
			loc := bundle.Normalize(raw)
			ctx := context.WithValue(r.Context(), localeKey, loc)
			next.ServeHTTP(w, r.WithContext(ctx))
		})
	}
}

// LocaleFrom returns the resolved locale stored by LocaleMiddleware.
func LocaleFrom(ctx context.Context) string {
	if v, ok := ctx.Value(localeKey).(string); ok {
		return v
	}
	return ""
}

// firstLang extracts the highest-priority language tag from an Accept-Language
// header, ignoring q-weights (good enough for two locales).
func firstLang(header string) string {
	if header == "" {
		return ""
	}
	first := strings.SplitN(header, ",", 2)[0]
	return strings.TrimSpace(strings.SplitN(first, ";", 2)[0])
}
