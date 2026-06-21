package pair

import (
	"strings"
	"testing"
	"time"
)

func TestNewInviteCode_FormatAndAlphabet(t *testing.T) {
	seen := make(map[string]bool)
	for i := 0; i < 200; i++ {
		code, err := NewInviteCode()
		if err != nil {
			t.Fatal(err)
		}
		if !strings.HasPrefix(code, InvitePrefix) {
			t.Fatalf("missing prefix: %q", code)
		}
		body := strings.TrimPrefix(code, InvitePrefix)
		if len(body) != InviteCodeLen {
			t.Fatalf("body len = %d, want %d (%q)", len(body), InviteCodeLen, code)
		}
		for _, c := range body {
			if !strings.ContainsRune(inviteAlphabet, c) {
				t.Fatalf("char %q not in alphabet (%q)", c, code)
			}
		}
		seen[code] = true
	}
	if len(seen) < 190 { // expect near-zero collisions
		t.Fatalf("too many collisions: %d unique of 200", len(seen))
	}
}

func TestNormalizeCode(t *testing.T) {
	cases := map[string]string{
		"kenar-7qf3":   "KENAR-7QF3",
		"  7QF3 ":      "KENAR-7QF3",
		"KENAR-AB CD":  "KENAR-ABCD",
		"":             "",
	}
	for in, want := range cases {
		if got := NormalizeCode(in); got != want {
			t.Errorf("NormalizeCode(%q) = %q, want %q", in, got, want)
		}
	}
}

func TestInvite_IsUsable(t *testing.T) {
	now := time.Now()
	valid := Invite{Status: InvitePending, ExpiresAt: now.Add(time.Hour)}
	if err := valid.IsUsable(now); err != nil {
		t.Fatalf("valid invite: %v", err)
	}
	expired := Invite{Status: InvitePending, ExpiresAt: now.Add(-time.Hour)}
	if err := expired.IsUsable(now); err != ErrInviteExpired {
		t.Fatalf("expired: got %v", err)
	}
	used := Invite{Status: InviteAccepted, ExpiresAt: now.Add(time.Hour)}
	if err := used.IsUsable(now); err != ErrInviteUsed {
		t.Fatalf("used: got %v", err)
	}
}

func TestPair_PartnerAndContains(t *testing.T) {
	p := Pair{UserA: "a", UserB: "b", Status: PairActive}
	if !p.Contains("a") || !p.Contains("b") || p.Contains("c") {
		t.Fatal("Contains wrong")
	}
	if partner, ok := p.Partner("a"); !ok || partner != "b" {
		t.Fatalf("Partner(a) = %q,%v", partner, ok)
	}
	if _, ok := p.Partner("c"); ok {
		t.Fatal("Partner(c) should be false")
	}
}

func TestPair_IsPremium(t *testing.T) {
	now := time.Now()
	future := now.Add(time.Hour)
	past := now.Add(-time.Hour)
	if (Pair{}).IsPremium(now) {
		t.Fatal("nil premium should be free")
	}
	if !(Pair{PremiumUntil: &future}).IsPremium(now) {
		t.Fatal("future premium should be active")
	}
	if (Pair{PremiumUntil: &past}).IsPremium(now) {
		t.Fatal("past premium should be inactive")
	}
}
