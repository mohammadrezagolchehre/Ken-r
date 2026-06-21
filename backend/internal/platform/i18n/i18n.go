// Package i18n loads the bilingual (fa/en) message catalogs and resolves
// keys to localized strings. Server-generated user-facing text MUST go through
// here — never hardcode strings. Persian (fa) is the default/primary locale.
package i18n

import (
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"
	"strings"
)

// SupportedLocales lists every locale the product ships, primary first.
var SupportedLocales = []string{"fa", "en"}

// Bundle holds the loaded catalogs and the default locale used as fallback.
type Bundle struct {
	defaultLocale string
	catalogs      map[string]map[string]string
}

// Load reads <dir>/<locale>.json for every supported locale.
func Load(dir, defaultLocale string) (*Bundle, error) {
	b := &Bundle{
		defaultLocale: defaultLocale,
		catalogs:      make(map[string]map[string]string),
	}
	for _, loc := range SupportedLocales {
		path := filepath.Join(dir, loc+".json")
		raw, err := os.ReadFile(path)
		if err != nil {
			return nil, fmt.Errorf("i18n: read %s: %w", path, err)
		}
		var parsed map[string]json.RawMessage
		if err := json.Unmarshal(raw, &parsed); err != nil {
			return nil, fmt.Errorf("i18n: parse %s: %w", path, err)
		}
		cat := make(map[string]string, len(parsed))
		for k, v := range parsed {
			if strings.HasPrefix(k, "_") { // skip _meta and other directives
				continue
			}
			var s string
			if err := json.Unmarshal(v, &s); err == nil {
				cat[k] = s
			}
		}
		b.catalogs[loc] = cat
	}
	if _, ok := b.catalogs[defaultLocale]; !ok {
		return nil, fmt.Errorf("i18n: default locale %q not loaded", defaultLocale)
	}
	return b, nil
}

// Normalize maps an arbitrary locale tag (e.g. "fa-IR", "en-US") to a supported
// locale, falling back to the default when unknown.
func (b *Bundle) Normalize(locale string) string {
	base := strings.ToLower(strings.SplitN(locale, "-", 2)[0])
	if _, ok := b.catalogs[base]; ok {
		return base
	}
	return b.defaultLocale
}

// T resolves key for locale, substituting {placeholder} args, and falls back to
// the default locale then to the key itself when missing.
func (b *Bundle) T(locale, key string, args map[string]string) string {
	loc := b.Normalize(locale)
	msg, ok := b.catalogs[loc][key]
	if !ok {
		if msg, ok = b.catalogs[b.defaultLocale][key]; !ok {
			return key
		}
	}
	for name, val := range args {
		msg = strings.ReplaceAll(msg, "{"+name+"}", val)
	}
	return msg
}
