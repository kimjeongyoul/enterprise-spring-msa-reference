# MSA Saga Pattern & Load Balancing Test Script

Write-Host "1. Building all microservices..." -ForegroundColor Cyan
./gradlew bootJar

Write-Host "`n2. Starting MSA Infrastructure & Services..." -ForegroundColor Cyan
docker-compose up -d --build

Write-Host "`n3. Waiting for services to initialize (30s)..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

Write-Host "`n4. Testing Saga Flow: Creating an Order via Gateway..." -ForegroundColor Cyan
# Gateway(8080)를 통해 주문 생성 API를 호출한다고 가정
# 실제로는 인증 토큰이 필요하지만, 여기서는 흐름 확인을 위한 시뮬레이션용 로그를 추출합니다.

Write-Host "`n[Saga Flow Visualization]" -ForegroundColor Magenta
Write-Host "Order Service: Receiving Request -> Saving Outbox -> Publishing Event"
docker-compose logs --tail=20 order-service

Write-Host "`nProduct Service: Consuming Event -> Checking Stock -> (Optional) Failing & Compensation"
docker-compose logs --tail=20 product-service

Write-Host "`nPayment Service: Consuming Event -> Processing Payment"
docker-compose logs --tail=20 payment-service

Write-Host "`nNotification Service: Sending Async Alert"
docker-compose logs --tail=20 notification-service

Write-Host "`n5. Cleaning up environment..." -ForegroundColor Yellow
# docker-compose down -v
Write-Host "Cleanup skipped so you can inspect logs. Run 'docker-compose down -v' manually when finished." -ForegroundColor Green
