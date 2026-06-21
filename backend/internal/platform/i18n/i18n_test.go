package i18n

import (
	"os"
	"path/filepath"
	"testing"
)

func writeCatalogs(t *testing.T) string {
	t.Helper()
	dir := t.TempDir()
	fa := `{"_meta":{"locale":"fa"},"greet":"سلام {name}","only_fa":"فقط فارسی"}`
	en := `{"_meta":{"locale":"en"},"greet":"Hello {name}"}`
	if err := os.WriteFile(filepath.Join(dir, "fa.json"), []byte(fa), 0o600); err != nil {
		t.Fatal(err)
	}
	if err := os.WriteFile(filepath.Join(dir, "en.json"), []byte(en), 0o600); err != nil {
		t.Fatal(err)
	}
	return dir
}

func TestT_Substitution(t *testing.T) {
	b, err := Load(writeCatalogs(t), "fa")
	if err != nil {
		t.Fatal(err)
	}
	if got := b.T("en", "greet", map[string]string{"name": "Sara"}); got != "Hello Sara" {
		t.Fatalf("en greet = %q", got)
	}
	if got := b.T("fa", "greet", map[string]string{"name": "سارا"}); got != "سلام سارا" {
		t.Fatalf("fa greet = %q", got)
	}
}

func TestT_FallsBackToDefaultLocale(t *testing.T) {
	b, err := Load(writeCatalogs(t), "fa")
	if err != nil {
		t.Fatal(err)
	}
	// Key missing in en -> falls back to fa (default).
	if got := b.T("en", "only_fa", nil); got != "فقط فارسی" {
		t.Fatalf("fallback = %q", got)
	}
	// Missing everywhere -> returns the key.
	if got := b.T("en", "nope", nil); got != "nope" {
		t.Fatalf("missing = %q", got)
	}
}

func TestNormalize(t *testing.T) {
	b, err := Load(writeCatalogs(t), "fa")
	if err != nil {
		t.Fatal(err)
	}
	cases := map[string]string{"fa-IR": "fa", "en-US": "en", "de": "fa", "": "fa"}
	for in, want := range cases {
		if got := b.Normalize(in); got != want {
			t.Errorf("Normalize(%q) = %q, want %q", in, got, want)
		}
	}
}
