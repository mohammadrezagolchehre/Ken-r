// Command server is the Kenâr backend entrypoint. It loads config, the
// bilingual i18n catalogs, builds the HTTP router, and serves with graceful
// shutdown. Adapters (Postgres, Redis, MinIO, Pushe, Kavenegar) are wired in
// as the corresponding features land — see ROADMAP.md.
package main

import (
	"context"
	"errors"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/kenar/backend/internal/adapters/httpapi"
	"github.com/kenar/backend/internal/config"
	"github.com/kenar/backend/internal/platform/i18n"
	"github.com/kenar/backend/internal/platform/logger"
)

func main() {
	log := logger.New()

	cfg, err := config.Load()
	if err != nil {
		log.Error("config", "err", err)
		os.Exit(1)
	}

	bundle, err := i18n.Load(cfg.I18nDir, cfg.DefaultLocale)
	if err != nil {
		log.Error("i18n load", "err", err)
		os.Exit(1)
	}

	router := httpapi.NewRouter(httpapi.Deps{Logger: log, I18n: bundle})

	srv := &http.Server{
		Addr:              cfg.HTTPAddr,
		Handler:           router,
		ReadHeaderTimeout: 10 * time.Second,
	}

	go func() {
		log.Info("kenar backend listening", "addr", cfg.HTTPAddr, "default_locale", cfg.DefaultLocale)
		if err := srv.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			log.Error("http serve", "err", err)
			os.Exit(1)
		}
	}()

	ctx, stop := signal.NotifyContext(context.Background(), syscall.SIGINT, syscall.SIGTERM)
	defer stop()
	<-ctx.Done()

	log.Info("shutting down")
	shutdownCtx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()
	if err := srv.Shutdown(shutdownCtx); err != nil {
		log.Error("shutdown", "err", err)
	}
}
