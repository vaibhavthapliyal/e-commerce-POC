#!/bin/bash

# Colors for terminal output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Arrays to track started services
BACKGROUND_PIDS=()
STARTED_SERVICES=()

# Cleanup function to stop all services
cleanup() {
    echo -e "\n${YELLOW}Stopping all services...${NC}"
    
    # Kill background processes
    if [ ${#BACKGROUND_PIDS[@]} -gt 0 ]; then
        echo -e "${YELLOW}Stopping background processes...${NC}"
        for PID in "${BACKGROUND_PIDS[@]}"; do
            if ps -p $PID > /dev/null; then
                echo -e "${YELLOW}Killing process $PID${NC}"
                kill $PID 2>/dev/null
            fi
        done
    fi
    
    # Stop Homebrew services if started
    if [ "$BREW_KAFKA_STARTED" = true ]; then
        echo -e "${YELLOW}Stopping Kafka via Homebrew...${NC}"
        brew services stop kafka
        echo -e "${YELLOW}Stopping Zookeeper via Homebrew...${NC}"
        brew services stop zookeeper
    fi
    
    # Stop Redis if started via Homebrew
    if [ "$BREW_REDIS_STARTED" = true ]; then
        echo -e "${YELLOW}Stopping Redis via Homebrew...${NC}"
        brew services stop redis
    fi
    
    # If on macOS, send Ctrl+C to all Terminal windows we opened
    if [[ "$OSTYPE" == "darwin"* ]] && [ ${#STARTED_SERVICES[@]} -gt 0 ]; then
        echo -e "${YELLOW}Attempting to close service terminals...${NC}"
        for SERVICE in "${STARTED_SERVICES[@]}"; do
            osascript -e "tell application \"Terminal\" to close (every window whose name contains \"$SERVICE\")" 2>/dev/null
        done
    fi
    
    echo -e "${GREEN}All services stopped!${NC}"
    exit 0
}

# Register cleanup function on script exit
trap cleanup EXIT INT TERM

echo -e "${BLUE}Telecom E-commerce Application Launcher${NC}"
echo -e "${BLUE}======================================${NC}"

# Load NVM if available
echo -e "${YELLOW}Loading NVM (if available)...${NC}"
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"
[ -s "$NVM_DIR/bash_completion" ] && \. "$NVM_DIR/bash_completion"

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Detect OS type
if [[ "$OSTYPE" == "darwin"* ]]; then
    OS_TYPE="macOS"
    # Check if Homebrew is installed
    if ! command_exists brew; then
        echo -e "${YELLOW}Homebrew is not installed. Installing Homebrew...${NC}"
        echo "Visit https://brew.sh for manual installation if this hangs"
        # Skip automatic installation
        read -p "Press Enter to continue after installing Homebrew manually, or type 'skip' to proceed: " BREW_RESPONSE
        if [ "$BREW_RESPONSE" != "skip" ] && ! command_exists brew; then
            echo -e "${RED}Homebrew is required for installing dependencies on macOS. Exiting...${NC}"
            exit 1
        fi
    fi
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Check for specific Linux distributions
    if command_exists apt-get; then
        OS_TYPE="Ubuntu/Debian"
    elif command_exists dnf; then
        OS_TYPE="Fedora/RHEL"
    else
        OS_TYPE="Linux-Other"
    fi
else
    OS_TYPE="Unknown"
fi

echo -e "${YELLOW}Detected OS: ${OS_TYPE}${NC}"

# Function to install packages
install_package() {
    PACKAGE_NAME=$1
    INSTALL_CMD=$2
    
    echo -e "${YELLOW}Installing ${PACKAGE_NAME}...${NC}"
    eval $INSTALL_CMD
    
    # Check if installation was successful
    if command_exists $PACKAGE_NAME; then
        echo -e "${GREEN}✓ ${PACKAGE_NAME} installed successfully${NC}"
    else
        echo -e "${RED}✗ Failed to install ${PACKAGE_NAME}. Please install it manually.${NC}"
        exit 1
    fi
}

# Check and install dependencies
echo -e "\n${YELLOW}Checking dependencies...${NC}"

# Check Java
if command_exists java; then
    JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
    echo -e "${GREEN}✓ Java is installed (version $JAVA_VERSION)${NC}"
else
    echo -e "${RED}✗ Java is not installed.${NC}"
    read -p "Would you like to install Java? (yes/no): " INSTALL_JAVA
    
    if [[ "$INSTALL_JAVA" == "yes" ]]; then
        case $OS_TYPE in
            "macOS")
                install_package "java" "brew install --cask temurin"
                ;;
            "Ubuntu/Debian")
                sudo apt-get update
                install_package "java" "sudo apt-get install -y openjdk-11-jdk"
                ;;
            "Fedora/RHEL")
                install_package "java" "sudo dnf install -y java-11-openjdk-devel"
                ;;
            *)
                echo -e "${RED}Automatic installation not supported for your OS. Please install Java 11 or higher manually.${NC}"
                exit 1
                ;;
        esac
    else
        echo -e "${RED}Java is required to run this application. Exiting...${NC}"
        exit 1
    fi
fi

# Check Maven
if command_exists mvn; then
    MVN_VERSION=$(mvn --version | head -1 | awk '{print $3}')
    echo -e "${GREEN}✓ Maven is installed (version $MVN_VERSION)${NC}"
else
    echo -e "${RED}✗ Maven is not installed.${NC}"
    read -p "Would you like to install Maven? (yes/no): " INSTALL_MVN
    
    if [[ "$INSTALL_MVN" == "yes" ]]; then
        case $OS_TYPE in
            "macOS")
                install_package "mvn" "brew install maven"
                ;;
            "Ubuntu/Debian")
                sudo apt-get update
                install_package "mvn" "sudo apt-get install -y maven"
                ;;
            "Fedora/RHEL")
                install_package "mvn" "sudo dnf install -y maven"
                ;;
            *)
                echo -e "${RED}Automatic installation not supported for your OS. Please install Maven manually.${NC}"
                exit 1
                ;;
        esac
    else
        echo -e "${RED}Maven is required to build this application. Exiting...${NC}"
        exit 1
    fi
fi

# Check Node.js
if command_exists node; then
    NODE_VERSION=$(node --version)
    echo -e "${GREEN}✓ Node.js is installed (version $NODE_VERSION)${NC}"
else
    echo -e "${RED}✗ Node.js is not installed or not in PATH.${NC}"
    
    # Check if nvm is available but node isn't in PATH
    if [ -d "$HOME/.nvm/versions/node" ]; then
        echo -e "${YELLOW}NVM installation detected. Attempting to use NVM directly...${NC}"
        # List available node versions
        NVM_NODE_VERSIONS=$(ls -1 $HOME/.nvm/versions/node)
        
        if [ -n "$NVM_NODE_VERSIONS" ]; then
            # Use the first available version
            NVM_NODE_PATH=$(echo "$NVM_NODE_VERSIONS" | head -1)
            echo -e "${YELLOW}Found Node.js version: $NVM_NODE_PATH${NC}"
            
            # Add to PATH directly for this script session
            export PATH="$HOME/.nvm/versions/node/$NVM_NODE_PATH/bin:$PATH"
            
            if command_exists node; then
                NODE_VERSION=$(node --version)
                echo -e "${GREEN}✓ Successfully loaded Node.js from NVM (version $NODE_VERSION)${NC}"
            else
                echo -e "${RED}Failed to load Node.js from NVM.${NC}"
                echo -e "${YELLOW}You may need to set up NVM properly in your shell profile.${NC}"
            fi
        else
            echo -e "${RED}NVM is installed but no Node.js versions were found.${NC}"
        fi
    fi
    
    # If node is still not available
    if ! command_exists node; then
        echo -e "${RED}✗ Node.js is not available. Would you like to install it?${NC}"
        read -p "Install Node.js? (yes/no): " INSTALL_NODE
        
        if [[ "$INSTALL_NODE" == "yes" ]]; then
            case $OS_TYPE in
                "macOS")
                    install_package "node" "brew install node"
                    ;;
                "Ubuntu/Debian")
                    curl -fsSL https://deb.nodesource.com/setup_16.x | sudo -E bash -
                    install_package "node" "sudo apt-get install -y nodejs"
                    ;;
                "Fedora/RHEL")
                    curl -fsSL https://rpm.nodesource.com/setup_16.x | sudo bash -
                    install_package "node" "sudo dnf install -y nodejs"
                    ;;
                *)
                    echo -e "${RED}Automatic installation not supported for your OS. Please install Node.js manually.${NC}"
                    exit 1
                    ;;
            esac
        else
            echo -e "${RED}Node.js is required for the frontend. Exiting...${NC}"
            exit 1
        fi
    fi
fi

# Check PostgreSQL
if command_exists psql; then
    PSQL_VERSION=$(psql --version | head -1 | awk '{print $3}')
    echo -e "${GREEN}✓ PostgreSQL is installed (version $PSQL_VERSION)${NC}"
else
    echo -e "${RED}✗ PostgreSQL is not installed.${NC}"
    read -p "Would you like to install PostgreSQL? (yes/no): " INSTALL_PSQL
    
    if [[ "$INSTALL_PSQL" == "yes" ]]; then
        case $OS_TYPE in
            "macOS")
                install_package "psql" "brew install postgresql && brew services start postgresql"
                # Wait for PostgreSQL to start
                sleep 5
                ;;
            "Ubuntu/Debian")
                sudo apt-get update
                install_package "psql" "sudo apt-get install -y postgresql postgresql-contrib"
                # Start PostgreSQL service
                sudo systemctl start postgresql
                sudo systemctl enable postgresql
                ;;
            "Fedora/RHEL")
                install_package "psql" "sudo dnf install -y postgresql-server postgresql-contrib"
                # Initialize and start PostgreSQL
                sudo postgresql-setup --initdb
                sudo systemctl start postgresql
                sudo systemctl enable postgresql
                ;;
            *)
                echo -e "${RED}Automatic installation not supported for your OS. Please install PostgreSQL manually.${NC}"
                exit 1
                ;;
        esac
    else
        echo -e "${RED}PostgreSQL is required for the backend services. Exiting...${NC}"
        exit 1
    fi
fi

# Check Redis
if command_exists redis-cli; then
    REDIS_VERSION=$(redis-cli --version | awk '{print $2}')
    echo -e "${GREEN}✓ Redis is installed (version $REDIS_VERSION)${NC}"
else
    echo -e "${RED}✗ Redis is not installed.${NC}"
    read -p "Would you like to install Redis? (yes/no): " INSTALL_REDIS
    
    if [[ "$INSTALL_REDIS" == "yes" ]]; then
        case $OS_TYPE in
            "macOS")
                install_package "redis-cli" "brew install redis"
                ;;
            "Ubuntu/Debian")
                sudo apt-get update
                install_package "redis-cli" "sudo apt-get install -y redis-server"
                # Start Redis service
                sudo systemctl start redis-server
                sudo systemctl enable redis-server
                ;;
            "Fedora/RHEL")
                install_package "redis-cli" "sudo dnf install -y redis"
                # Start Redis service
                sudo systemctl start redis
                sudo systemctl enable redis
                ;;
            *)
                echo -e "${RED}Automatic installation not supported for your OS. Please install Redis manually.${NC}"
                exit 1
                ;;
        esac
    else
        echo -e "${RED}Redis is required for the backend services. Exiting...${NC}"
        exit 1
    fi
fi

# Setup PostgreSQL databases
echo -e "\n${YELLOW}Setting up PostgreSQL databases...${NC}"

# Check if postgres user exists and has the right password
if psql -U postgres -c '\q' &>/dev/null; then
    echo -e "${GREEN}✓ User 'postgres' exists and is accessible${NC}"
else
    echo -e "${YELLOW}⚠ Cannot access PostgreSQL with user 'postgres'. Setting up the user...${NC}"
    
    case $OS_TYPE in
        "macOS")
            # For macOS, create postgres user (usually doesn't exist by default)
            createuser -s postgres
            psql -c "ALTER USER postgres WITH PASSWORD 'postgres';" postgres
            ;;
        "Ubuntu/Debian" | "Fedora/RHEL")
            # For Linux, postgres user exists but password might not be set
            sudo -u postgres psql -c "ALTER USER postgres WITH PASSWORD 'postgres';"
            ;;
        *)
            echo -e "${YELLOW}⚠ Cannot configure postgres user automatically. You may need to do this manually.${NC}"
            read -p "Continue anyway? (yes/no): " CONTINUE_ANYWAY
            if [[ "$CONTINUE_ANYWAY" != "yes" ]]; then
                exit 1
            fi
            ;;
    esac
    
    echo -e "${GREEN}✓ Updated 'postgres' user password${NC}"
fi

# Create databases if they don't exist
for DB in telecom_product telecom_cart telecom_payment telecom_discount telecom_notification; do
    if psql -U postgres -lqt | cut -d \| -f 1 | grep -qw $DB; then
        echo -e "${GREEN}✓ Database '$DB' already exists${NC}"
    else
        if psql -U postgres -c "CREATE DATABASE $DB;" &>/dev/null; then
            echo -e "${GREEN}✓ Created database '$DB'${NC}"
        else
            echo -e "${RED}✗ Failed to create database '$DB'${NC}"
        fi
    fi
done

# Check Kafka
KAFKA_DIR=""
BREW_KAFKA=false

# Check for Homebrew Kafka on macOS
if [[ "$OSTYPE" == "darwin"* ]] && command_exists brew; then
    if brew list kafka &>/dev/null || brew list kafka@3.5 &>/dev/null; then
        echo -e "${GREEN}✓ Kafka is installed via Homebrew${NC}"
        BREW_KAFKA=true
        # Get Homebrew prefix for Kafka
        if brew list kafka &>/dev/null; then
            BREW_KAFKA_PREFIX=$(brew --prefix kafka)
        else
            BREW_KAFKA_PREFIX=$(brew --prefix kafka@3.5)
        fi
        echo -e "${GREEN}✓ Kafka Homebrew prefix: ${BREW_KAFKA_PREFIX}${NC}"
    fi
fi

# If not installed via Homebrew, check for local installation
if [ "$BREW_KAFKA" = false ]; then
    if [ -d "kafka_2.13-3.5.1" ]; then
        KAFKA_DIR="kafka_2.13-3.5.1"
        echo -e "${GREEN}✓ Kafka directory found${NC}"
    elif command_exists kafka-topics.sh; then
        echo -e "${GREEN}✓ Kafka is installed (via PATH)${NC}"
    else
        echo -e "${YELLOW}⚠ Kafka not found locally.${NC}"
        read -p "Would you like to download and set up Kafka? (yes/no): " SETUP_KAFKA
        
        if [[ "$SETUP_KAFKA" == "yes" ]]; then
            echo -e "${YELLOW}Downloading Kafka...${NC}"
            if command_exists wget; then
                wget -q https://dlcdn.apache.org/kafka/3.5.1/kafka_2.13-3.5.1.tgz
            elif command_exists curl; then
                curl -s -o kafka_2.13-3.5.1.tgz https://dlcdn.apache.org/kafka/3.5.1/kafka_2.13-3.5.1.tgz
            else
                echo -e "${YELLOW}Installing wget for downloading Kafka...${NC}"
                case $OS_TYPE in
                    "macOS")
                        brew install wget
                        wget -q https://dlcdn.apache.org/kafka/3.5.1/kafka_2.13-3.5.1.tgz
                        ;;
                    "Ubuntu/Debian")
                        sudo apt-get update && sudo apt-get install -y wget
                        wget -q https://dlcdn.apache.org/kafka/3.5.1/kafka_2.13-3.5.1.tgz
                        ;;
                    "Fedora/RHEL")
                        sudo dnf install -y wget
                        wget -q https://dlcdn.apache.org/kafka/3.5.1/kafka_2.13-3.5.1.tgz
                        ;;
                    *)
                        echo -e "${RED}Cannot install wget. Please install Kafka manually.${NC}"
                        exit 1
                        ;;
                esac
            fi
            
            tar -xzf kafka_2.13-3.5.1.tgz
            rm kafka_2.13-3.5.1.tgz
            KAFKA_DIR="kafka_2.13-3.5.1"
            echo -e "${GREEN}✓ Kafka downloaded and extracted${NC}"
        else
            echo -e "${YELLOW}⚠ Skipping Kafka setup. The application might not work correctly without it.${NC}"
        fi
    fi
fi

# Build the project
echo -e "\n${YELLOW}Building the backend services...${NC}"

# Check if we need to build individual services or if there's a parent pom.xml
if [ -f "pom.xml" ]; then
    echo -e "${YELLOW}Found parent pom.xml, building all services at once...${NC}"
    mvn clean install -DskipTests
else
    echo -e "${YELLOW}Building individual services...${NC}"
    
    # Build each service individually
    for SERVICE_DIR in backend/*/; do
        if [ -f "${SERVICE_DIR}pom.xml" ]; then
            echo -e "${YELLOW}Building ${SERVICE_DIR}...${NC}"
            (cd "${SERVICE_DIR}" && mvn clean install -DskipTests)
        fi
    done
fi

# Start services
echo -e "\n${YELLOW}Starting services...${NC}"

# Function to start a service in a new terminal
start_service() {
    SERVICE_NAME=$1
    SERVICE_DIR=$2
    SERVICE_CMD=$3
    
    # Check if service directory exists
    if [ ! -d "$SERVICE_DIR" ]; then
        echo -e "${RED}✗ Service directory '$SERVICE_DIR' not found. Skipping...${NC}"
        return
    fi
    
    echo -e "${YELLOW}Starting $SERVICE_NAME...${NC}"
    
    # Add to list of started services
    STARTED_SERVICES+=("$SERVICE_NAME")
    
    # Get the current directory with proper quoting for spaces
    CURRENT_DIR="$(pwd)"
    
    # Ensure service command has proper environment with properly quoted paths
    WRAPPED_CMD="cd \"$CURRENT_DIR/$SERVICE_DIR\" && export NVM_DIR=\"\$HOME/.nvm\" && [ -s \"\$NVM_DIR/nvm.sh\" ] && \. \"\$NVM_DIR/nvm.sh\" && $SERVICE_CMD"
    
    # Different terminal commands based on OS
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        if [[ -n "$TERM_PROGRAM" && "$TERM_PROGRAM" == "cursor" ]]; then
            # Inside Cursor IDE, just create a background process
            echo -e "${YELLOW}Running $SERVICE_NAME in background (IDE detected)${NC}"
            bash -c "$WRAPPED_CMD" &
            BACKGROUND_PIDS+=($!)
        else
            # Regular macOS terminal - Fix AppleScript escaping
            # Create a properly escaped version of the command for AppleScript
            ESCAPED_CMD=$(echo "$WRAPPED_CMD" | sed 's/\\/\\\\/g' | sed 's/"/\\"/g')
            osascript -e "tell application \"Terminal\" to do script \"echo \\\"$SERVICE_NAME\\\"; $ESCAPED_CMD\""
        fi
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        # Linux
        if command_exists gnome-terminal; then
            gnome-terminal --title="$SERVICE_NAME" -- bash -c "$WRAPPED_CMD; exec bash"
        elif command_exists xterm; then
            xterm -title "$SERVICE_NAME" -e "$WRAPPED_CMD" &
            BACKGROUND_PIDS+=($!)
        else
            echo -e "${RED}No terminal emulator found (gnome-terminal or xterm).${NC}"
            echo -e "${YELLOW}Installing gnome-terminal...${NC}"
            if command_exists apt-get; then
                sudo apt-get update && sudo apt-get install -y gnome-terminal
                gnome-terminal --title="$SERVICE_NAME" -- bash -c "$WRAPPED_CMD; exec bash"
            elif command_exists dnf; then
                sudo dnf install -y gnome-terminal
                gnome-terminal --title="$SERVICE_NAME" -- bash -c "$WRAPPED_CMD; exec bash"
            else
                echo -e "${RED}Cannot install terminal emulator automatically.${NC}"
                echo -e "${YELLOW}Please start manually: $WRAPPED_CMD${NC}"
            fi
        fi
    else
        echo -e "${RED}Unsupported OS. Please start the service manually:${NC}"
        echo -e "${YELLOW}$WRAPPED_CMD${NC}"
    fi
}

# Start Redis
echo -e "${YELLOW}Starting Redis...${NC}"
if [[ "$OSTYPE" == "darwin"* ]] && command_exists brew; then
    # For macOS with Homebrew
    brew services start redis
    BREW_REDIS_STARTED=true
    echo -e "${GREEN}✓ Redis started via Homebrew${NC}"
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # For Linux
    if command_exists systemctl; then
        sudo systemctl start redis-server || sudo systemctl start redis
        echo -e "${GREEN}✓ Redis started via systemd${NC}"
    else
        # Fallback to direct command
        redis-server &
        BACKGROUND_PIDS+=($!)
        echo -e "${GREEN}✓ Redis started in background${NC}"
    fi
else
    # Fallback for other systems
    redis-server &
    BACKGROUND_PIDS+=($!)
    echo -e "${GREEN}✓ Redis started in background${NC}"
fi

# Wait for Redis to be ready
echo -e "${YELLOW}Waiting for Redis to be ready...${NC}"
sleep 3
if redis-cli ping &>/dev/null; then
    echo -e "${GREEN}✓ Redis is running and responding${NC}"
else
    echo -e "${RED}✗ Redis is not responding. The application might not work correctly.${NC}"
fi

# Start Kafka if we have it
if [[ -n "$KAFKA_DIR" ]]; then
    start_service "Zookeeper" "$KAFKA_DIR" "bin/zookeeper-server-start.sh config/zookeeper.properties"
    sleep 5  # Wait for ZooKeeper to start
    start_service "Kafka" "$KAFKA_DIR" "bin/kafka-server-start.sh config/server.properties"
    sleep 5  # Wait for Kafka to start
    
    # Create Kafka topics
    echo -e "${YELLOW}Creating Kafka topics...${NC}"
    $KAFKA_DIR/bin/kafka-topics.sh --create --topic payment-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 --if-not-exists
    echo -e "${GREEN}✓ Kafka topics created${NC}"
elif [ "$BREW_KAFKA" = true ]; then
    # For Homebrew Kafka, we use the brew services command
    echo -e "${YELLOW}Starting Zookeeper via Homebrew...${NC}"
    brew services start zookeeper
    sleep 5  # Wait for ZooKeeper to start
    
    echo -e "${YELLOW}Starting Kafka via Homebrew...${NC}"
    brew services start kafka
    BREW_KAFKA_STARTED=true
    sleep 5  # Wait for Kafka to start
    
    # Create Kafka topics using Homebrew Kafka
    echo -e "${YELLOW}Creating Kafka topics...${NC}"
    "$BREW_KAFKA_PREFIX/bin/kafka-topics" --create --topic payment-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 --if-not-exists
    echo -e "${GREEN}✓ Kafka topics created${NC}"
fi

# Check for Eureka server
if [ -d "backend/eureka-server" ]; then
    start_service "Eureka Server" "backend/eureka-server" "mvn spring-boot:run"
    echo -e "${YELLOW}Waiting for Eureka Server to start...${NC}"
    sleep 10  # Wait for Eureka to start
else
    echo -e "${YELLOW}⚠ Eureka Server directory not found. Skipping...${NC}"
    echo -e "${YELLOW}Some services may fail to start without service discovery.${NC}"
fi

# Start all other services in the correct order if they exist
if [ -d "backend/api-gateway" ]; then
    start_service "API Gateway" "backend/api-gateway" "mvn spring-boot:run"
    sleep 3  # Give the gateway a moment to start
else
    echo -e "${YELLOW}⚠ API Gateway directory not found. Skipping...${NC}"
fi

# Start core services
for SERVICE in product-service cart-service discount-service payment-service notification-service; do
    if [ -d "backend/$SERVICE" ]; then
        # Use the service name directly with first letter capitalized
        SERVICE_NAME=$(echo "$SERVICE" | sed 's/\b\(.\)/\u\1/g')
        start_service "$SERVICE_NAME" "backend/$SERVICE" "mvn spring-boot:run"
        sleep 2  # Give each service a moment to start
    else
        echo -e "${YELLOW}⚠ $SERVICE directory not found. Skipping...${NC}"
    fi
done

# Give backend services time to register with Eureka
echo -e "${YELLOW}Waiting for backend services to start and register...${NC}"
sleep 10

# Check for frontend directory
if [ -d "frontend" ]; then
    # Start the frontend
    echo -e "\n${YELLOW}Starting the frontend...${NC}"
    start_service "Frontend" "frontend" "npm install && npm run dev"
else
    echo -e "${YELLOW}⚠ Frontend directory not found. Skipping...${NC}"
fi

echo -e "\n${GREEN}Services started successfully!${NC}"
echo -e "${BLUE}Access the application at:${NC}"
echo -e "  - Frontend: ${GREEN}http://localhost:3000${NC}"
echo -e "  - API Gateway: ${GREEN}http://localhost:9000${NC}"

# If Eureka exists, show its URL
if [ -d "backend/eureka-server" ]; then
    echo -e "  - Eureka Dashboard: ${GREEN}http://localhost:8761${NC}"
fi

echo -e "\n${YELLOW}Press Ctrl+C to stop all services and exit${NC}"

# Wait for user to terminate
while true; do
    sleep 1
done 