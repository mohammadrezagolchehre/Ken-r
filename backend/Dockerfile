# Kenâr backend — multi-stage build, small static image.
FROM golang:1.22-alpine AS build
WORKDIR /src
COPY go.mod ./
# go.sum copied when external deps are introduced.
RUN go mod download || true
COPY . .
RUN CGO_ENABLED=0 GOOS=linux go build -trimpath -ldflags="-s -w" -o /out/kenar ./cmd/server

FROM gcr.io/distroless/static-debian12:nonroot
WORKDIR /app
COPY --from=build /out/kenar /app/kenar
COPY --from=build /src/i18n /app/i18n
ENV KENAR_I18N_DIR=/app/i18n
EXPOSE 8080
USER nonroot:nonroot
ENTRYPOINT ["/app/kenar"]
