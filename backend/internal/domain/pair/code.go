package pair

import (
	"crypto/rand"
	"strings"
)

// inviteAlphabet excludes visually ambiguous characters (0/O, 1/I/L) so codes
// are easy to read aloud and type across fa/en keyboards.
const inviteAlphabet = "ABCDEFGHJKMNPQRSTUVWXYZ23456789"

// InviteCodeLen is the number of random characters after the prefix.
const InviteCodeLen = 4

// InvitePrefix brands the code and makes it recognizable when shared.
const InvitePrefix = "KENAR-"

// NewInviteCode returns a cryptographically random, human-friendly invite code
// such as "KENAR-7QF3". Caller is responsible for uniqueness checks against the
// store and retrying on the rare collision.
func NewInviteCode() (string, error) {
	buf := make([]byte, InviteCodeLen)
	if _, err := rand.Read(buf); err != nil {
		return "", err
	}
	var sb strings.Builder
	sb.WriteString(InvitePrefix)
	for _, b := range buf {
		sb.WriteByte(inviteAlphabet[int(b)%len(inviteAlphabet)])
	}
	return sb.String(), nil
}

// NormalizeCode upcases and trims a user-entered code for lookup, tolerating
// missing prefix and surrounding whitespace.
func NormalizeCode(in string) string {
	s := strings.ToUpper(strings.TrimSpace(in))
	s = strings.ReplaceAll(s, " ", "")
	if s != "" && !strings.HasPrefix(s, InvitePrefix) {
		s = InvitePrefix + s
	}
	return s
}
