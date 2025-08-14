#!/bin/bash

# Scalable CDN System - Quick Demo for Recruiters
# Individual Assignment - Navigna Reddy
# This script sets up the entire system in under 2 minutes

set -e  # Exit on any error

echo "Starting Scalable Content Delivery System Demo..."
echo "=================================================="
echo ""

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo "ERROR: Docker is not running. Please start Docker Desktop and try again."
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose &> /dev/null; then
    echo "ERROR: Docker Compose is not installed. Please install Docker Compose and try again."
    exit 1
fi

echo "SUCCESS: Docker environment verified"
echo ""

# Clean up any existing containers
echo "Cleaning up existing containers..."
docker-compose down -v --remove-orphans 2>/dev/null || true
echo ""

# Start all services
echo "Starting all microservices..."
echo "This may take 2-3 minutes on first run (downloading images)..."
docker-compose up -d

echo ""
echo "Waiting for services to start..."

# Wait for services to be healthy
wait_for_service() {
    local service=$1
    local port=$2
    local max_attempts=60
    local attempt=0
    
    echo -n "   Waiting for $service..."
    while [ $attempt -lt $max_attempts ]; do
        if curl -sf http://localhost:$port/actuator/health >/dev/null 2>&1; then
            echo " READY!"
            return 0
        elif curl -sf http://localhost:$port >/dev/null 2>&1; then
            echo " READY!"
            return 0
        fi
        sleep 2
        attempt=$((attempt + 1))
        echo -n "."
    done
    echo " TIMEOUT (but service may still be starting)"
    return 1
}

# Wait for key services
wait_for_service "API Gateway" 8080
wait_for_service "Frontend" 3000
wait_for_service "Prometheus" 9090
wait_for_service "Grafana" 3001

echo ""
echo "Demo Environment Ready!"
echo "======================"
echo ""
echo "Access Points:"
echo "   Frontend Application: http://localhost:3000"
echo "   Grafana Monitoring:   http://localhost:3001 (admin/admin)"
echo "   Prometheus Metrics:   http://localhost:9090"
echo "   API Gateway Health:   http://localhost:8080/actuator/health"
echo ""

echo "Quick Demo Guide:"
echo "================="
echo ""
echo "1. USER REGISTRATION (30 seconds)"
echo "   -> Go to: http://localhost:3000"
echo "   -> Click 'Register' -> Fill form -> Create account"
echo ""
echo "2. FILE UPLOAD & MANAGEMENT (1 minute)"
echo "   -> Login -> Click 'Upload' -> Select file -> Add details -> Upload"
echo "   -> View in 'Library' -> Test download functionality"
echo ""
echo "3. SEARCH & FILTERING (30 seconds)"
echo "   -> Use search bar -> Try different filters"
echo "   -> Test pagination and sorting options"
echo ""
echo "4. ANALYTICS DASHBOARD (1 minute)"
echo "   -> Go to 'Analytics' -> View real-time metrics"
echo "   -> See usage patterns and system performance"
echo ""
echo "5. SYSTEM MONITORING (30 seconds)"
echo "   -> Go to: http://localhost:3001 (admin/admin)"
echo "   -> View Grafana dashboards -> See live metrics"
echo ""

echo "Sample API Tests:"
echo "================="
echo ""
echo "# Test API Gateway health"
echo "curl http://localhost:8080/actuator/health"
echo ""
echo "# Get content list"
echo "curl http://localhost:8080/api/v1/content"
echo ""
echo "# View Prometheus metrics"
echo "curl http://localhost:9090/metrics"
echo ""

echo "System Status:"
echo "=============="
docker-compose ps

echo ""
echo "To Stop Demo:"
echo "============="
echo "docker-compose down"
echo ""

echo "Contact Information:"
echo "==================="
echo "Developer: Navigna Reddy"
echo "Project: Individual Assignment - Scalable CDN System"
echo "Architecture: Microservices with React Frontend"
echo ""

echo "Key Technical Highlights:"
echo "========================"
echo "* 5 Microservices (API Gateway, Content, User, Analytics, Notifications)"
echo "* React TypeScript Frontend with modern UX"
echo "* PostgreSQL + Redis data layer"
echo "* Docker containerization & Kubernetes ready"
echo "* Prometheus monitoring & Grafana dashboards"
echo "* JWT authentication & security"
echo "* Production-ready architecture"
echo ""

echo "Demo is ready! Visit http://localhost:3000 to start exploring!" 