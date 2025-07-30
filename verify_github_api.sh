#!/bin/bash

echo "🎬 GitHub API Migration Verification Script"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test counters
TESTS_PASSED=0
TESTS_FAILED=0
TOTAL_TESTS=8

echo "📡 Testing GitHub API Endpoints..."
echo ""

# Test 1: Primary API Endpoint
echo -n "1. Testing primary API endpoint... "
if curl -s --head "https://raw.githubusercontent.com/MovieAddict88/movie-api/main/free_movie_api.json" | grep -q "200 OK"; then
    echo -e "${GREEN}✅ PASS${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}❌ FAIL${NC}"
    ((TESTS_FAILED++))
fi

# Test 2: Ads Configuration Endpoint
echo -n "2. Testing ads configuration endpoint... "
if curl -s --head "https://raw.githubusercontent.com/MovieAddict88/movie-api/main/ads_config.json" | grep -q "200 OK"; then
    echo -e "${GREEN}✅ PASS${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}❌ FAIL${NC}"
    ((TESTS_FAILED++))
fi

# Test 3: JSON Structure Validation
echo -n "3. Validating JSON structure... "
API_RESPONSE=$(curl -s "https://raw.githubusercontent.com/MovieAddict88/movie-api/main/free_movie_api.json")
if echo "$API_RESPONSE" | jq empty 2>/dev/null; then
    echo -e "${GREEN}✅ PASS${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}❌ FAIL${NC}"
    ((TESTS_FAILED++))
fi

# Test 4: Video Source - Big Buck Bunny
echo -n "4. Testing Big Buck Bunny video source... "
if curl -s --head "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" | grep -q "200 OK"; then
    echo -e "${GREEN}✅ PASS${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}❌ FAIL${NC}"
    ((TESTS_FAILED++))
fi

# Test 5: Video Source - Elephants Dream
echo -n "5. Testing Elephants Dream video source... "
if curl -s --head "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4" | grep -q "200 OK"; then
    echo -e "${GREEN}✅ PASS${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}❌ FAIL${NC}"
    ((TESTS_FAILED++))
fi

# Test 6: Live Stream Source
echo -n "6. Testing live stream source... "
if curl -s --head "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8" | grep -q "200 OK"; then
    echo -e "${GREEN}✅ PASS${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}❌ FAIL${NC}"
    ((TESTS_FAILED++))
fi

# Test 7: API Response Content
echo -n "7. Checking API response contains required data... "
if echo "$API_RESPONSE" | jq -e '.home.slides | length > 0' > /dev/null 2>&1 && \
   echo "$API_RESPONSE" | jq -e '.movies | length > 0' > /dev/null 2>&1 && \
   echo "$API_RESPONSE" | jq -e '.channels | length > 0' > /dev/null 2>&1; then
    echo -e "${GREEN}✅ PASS${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}❌ FAIL${NC}"
    ((TESTS_FAILED++))
fi

# Test 8: Ads Configuration Content
echo -n "8. Checking ads configuration content... "
ADS_RESPONSE=$(curl -s "https://raw.githubusercontent.com/MovieAddict88/movie-api/main/ads_config.json")
if echo "$ADS_RESPONSE" | jq -e '.ads_config.admob.banner_id' > /dev/null 2>&1; then
    echo -e "${GREEN}✅ PASS${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}❌ FAIL${NC}"
    ((TESTS_FAILED++))
fi

echo ""
echo "📊 Test Results:"
echo "================"
echo -e "Tests Passed: ${GREEN}$TESTS_PASSED${NC}/${TOTAL_TESTS}"
echo -e "Tests Failed: ${RED}$TESTS_FAILED${NC}/${TOTAL_TESTS}"

# Calculate success percentage
SUCCESS_RATE=$((TESTS_PASSED * 100 / TOTAL_TESTS))
echo -e "Success Rate: ${GREEN}$SUCCESS_RATE%${NC}"

echo ""
if [ $SUCCESS_RATE -ge 90 ]; then
    echo -e "${GREEN}🎉 Migration Successful! 90%+ success rate achieved.${NC}"
    echo ""
    echo "📱 Next Steps:"
    echo "- Build the Android app"
    echo "- Test on device/emulator"
    echo "- Verify video playback"
    echo "- Check offline functionality"
    echo "- Deploy to production"
else
    echo -e "${RED}⚠️  Migration needs attention. Success rate below 90%.${NC}"
    echo ""
    echo "🔧 Troubleshooting:"
    echo "- Check network connectivity"
    echo "- Verify GitHub repository permissions"
    echo "- Ensure JSON files are properly formatted"
    echo "- Validate video source URLs"
fi

echo ""
echo "📋 Additional Verification:"
echo "=========================="
echo "Run these commands manually to verify specific components:"
echo ""
echo "# Test API response:"
echo "curl -s 'https://raw.githubusercontent.com/MovieAddict88/movie-api/main/free_movie_api.json' | jq '.api_info'"
echo ""
echo "# Test video source:"
echo "curl -I 'http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4'"
echo ""
echo "# Validate JSON structure:"
echo "curl -s 'https://raw.githubusercontent.com/MovieAddict88/movie-api/main/free_movie_api.json' | jq empty && echo 'Valid JSON'"
echo ""

exit $TESTS_FAILED