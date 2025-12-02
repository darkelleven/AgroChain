# Troubleshooting script for Android Emulator connection issues
# Run this script as Administrator in PowerShell

Write-Host "🔍 Checking connection issues..." -ForegroundColor Cyan

# Check if port 3000 is in use
Write-Host "`n1. Checking if port 3000 is available..." -ForegroundColor Yellow
$portInUse = Get-NetTCPConnection -LocalPort 3000 -ErrorAction SilentlyContinue
if ($portInUse) {
    Write-Host "   ⚠️  Port 3000 is already in use!" -ForegroundColor Red
    Write-Host "   Process using port 3000:" -ForegroundColor Yellow
    $portInUse | ForEach-Object {
        $process = Get-Process -Id $_.OwningProcess -ErrorAction SilentlyContinue
        Write-Host "   - PID $($_.OwningProcess): $($process.ProcessName)" -ForegroundColor Yellow
    }
} else {
    Write-Host "   ✅ Port 3000 is available" -ForegroundColor Green
}

# Check Windows Firewall rules
Write-Host "`n2. Checking Windows Firewall rules for port 3000..." -ForegroundColor Yellow
$firewallRules = Get-NetFirewallRule | Where-Object {
    ($_.DisplayName -like "*3000*" -or $_.DisplayName -like "*node*" -or $_.DisplayName -like "*express*")
} | Select-Object DisplayName, Enabled, Direction

if ($firewallRules) {
    Write-Host "   Found firewall rules:" -ForegroundColor Yellow
    $firewallRules | Format-Table -AutoSize
} else {
    Write-Host "   ⚠️  No firewall rule found for port 3000" -ForegroundColor Red
    Write-Host "   We'll create one below..." -ForegroundColor Yellow
}

# Test localhost connection
Write-Host "`n3. Testing localhost:3000 connection..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:3000/health" -TimeoutSec 5 -ErrorAction Stop
    Write-Host "   ✅ Server is responding on localhost:3000" -ForegroundColor Green
    Write-Host "   Response: $($response.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Cannot connect to localhost:3000" -ForegroundColor Red
    Write-Host "   Make sure the server is running!" -ForegroundColor Yellow
}

# Create firewall rule if needed
Write-Host "`n4. Creating/updating firewall rule..." -ForegroundColor Yellow
$ruleName = "Node.js Server Port 3000"

# Check if rule exists
$existingRule = Get-NetFirewallRule -DisplayName $ruleName -ErrorAction SilentlyContinue

if (-not $existingRule) {
    Write-Host "   Creating new firewall rule..." -ForegroundColor Yellow
    try {
        New-NetFirewallRule -DisplayName $ruleName `
            -Direction Inbound `
            -LocalPort 3000 `
            -Protocol TCP `
            -Action Allow `
            -Profile Any | Out-Null
        Write-Host "   ✅ Firewall rule created successfully!" -ForegroundColor Green
    } catch {
        Write-Host "   ❌ Failed to create firewall rule. Run PowerShell as Administrator!" -ForegroundColor Red
        Write-Host "   Error: $_" -ForegroundColor Red
    }
} else {
    Write-Host "   Firewall rule already exists. Ensuring it's enabled..." -ForegroundColor Yellow
    Set-NetFirewallRule -DisplayName $ruleName -Enabled True -Action Allow
    Write-Host "   ✅ Firewall rule enabled" -ForegroundColor Green
}

# Display network adapter info
Write-Host "`n5. Network adapter information:" -ForegroundColor Yellow
Get-NetIPAddress -AddressFamily IPv4 | Where-Object {
    $_.IPAddress -notlike "127.*" -and $_.IPAddress -notlike "169.254.*"
} | ForEach-Object {
    Write-Host "   - $($_.IPAddress) on $($_.InterfaceAlias)" -ForegroundColor Cyan
}

Write-Host "`n✅ Troubleshooting complete!" -ForegroundColor Green
Write-Host "`nNext steps:" -ForegroundColor Cyan
Write-Host "   1. Make sure your backend server is running: cd backend && npm start" -ForegroundColor White
Write-Host "   2. Test from browser: http://localhost:3000/test-connection" -ForegroundColor White
Write-Host "   3. From Android emulator, use: http://10.0.2.2:3000/test-connection" -ForegroundColor White


