#!/bin/bash
# Local CI verification script for samurai-graph
# Runs format check, compile, test, and package in sequence.

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

START_TIME=$(date +%s)
FAILED=0
FAILED_STEP=""

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "========================================"
echo " Samurai Graph - Local CI Verification"
echo "========================================"
echo ""
echo "Project: $PROJECT_DIR"
echo "Started: $(date)"
echo ""

run_step() {
    local step_name="$1"
    shift
    echo -e "${YELLOW}[STEP]${NC} $step_name"
    if "$@" > /dev/null 2>&1; then
        echo -e "${GREEN}[PASS]${NC} $step_name"
        echo ""
    else
        echo -e "${RED}[FAIL]${NC} $step_name"
        FAILED=1
        FAILED_STEP="$step_name"
        echo ""
        echo -e "${RED}Aborting due to failure in: $step_name${NC}"
        END_TIME=$(date +%s)
        ELAPSED=$((END_TIME - START_TIME))
        echo "Elapsed time: ${ELAPSED}s"
        exit 1
    fi
}

cd "$PROJECT_DIR"

run_step "Clean" mvn clean
run_step "Format Check (spotless:check)" mvn spotless:check
run_step "Compile (with lint)" mvn compile
run_step "Unit Tests" mvn test
run_step "Package (fat JAR)" mvn package -DskipTests

END_TIME=$(date +%s)
ELAPSED=$((END_TIME - START_TIME))

echo "========================================"
echo -e "${GREEN}All steps passed!${NC}"
echo "Elapsed time: ${ELAPSED}s"
echo "========================================"
exit 0
