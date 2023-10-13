run_worker:
	docker compose -f "docker-compose.yml" up -d --build

stop_worker:
	docker compose -f "docker-compose.yml" down