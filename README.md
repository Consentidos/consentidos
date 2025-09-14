# consentidos

**Consentidos** is a backend system designed to manage the processes carried out by the clinic.

This project is built in Java (17) with Spring Boot (3.3.2). It uses hexagonal architecture and vertical slicing as its fundamental pillars.

For more information about these concepts or the project you can visit the [documentation](https://github.com/Consentidos/documentaciones).

## Installation
To get started with Consentidos, you'll need to set up the project and install its dependencies. Follow the steps below:
1. **Clone the repository:**
    ````shell
    git clone https://github.com/Consentidos/consentidos.git
    ````
2. **Navigate to the Project Directory:**
    ````shell
    cd consentidos
    ````
3. **Install Dependencies:**
    - Ensure you have [Gradle](https://gradle.org/) installed on your machine. Verify by running `gradle -v` in your terminal.
    - Ensure you have [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) on your machine or in your IDE.
    - Build the project and download all necessary dependencies using your tools from your IDE for Gradle or by running `gradle build` in your terminal.

    **Note**: If you encounter issues, make sure Gradle is properly installed and configured.

## Testing

This project includes comprehensive testing capabilities with coverage reporting. Below are the different ways to run tests:

### 🧪 Running Tests

#### Basic Test Execution
```shell
# Run all tests
./gradlew test

# Run tests with console output
./gradlew test --console=plain

# Run specific test class
./gradlew test --tests "com.veterinaria.consentidos.SomeTestClass"

# Run tests matching a pattern
./gradlew test --tests "*Helper*"
```

#### Test with Coverage Report
```shell
# Run tests and generate coverage report
./gradlew test jacocoTestReport

# The coverage report will be available at:
# build/reports/jacoco/test/html/index.html
```

#### Full Coverage Verification
```shell
# Run tests with coverage and verify minimum coverage (80%)
./gradlew fullCoverageCheck

# This task will:
# 1. Run all tests
# 2. Generate coverage report
# 3. Verify coverage meets minimum threshold
```

### 📊 Coverage Reports

Coverage reports are generated using JaCoCo and provide detailed information about:
- Line coverage
- Branch coverage
- Method coverage
- Class coverage

**Viewing Coverage Reports:**
1. **HTML Report**: Open `build/reports/jacoco/test/html/index.html` in your browser
2. **XML Report**: Located at `build/reports/jacoco/test/jacocoTestReport.xml` (for CI/CD)

**Quick Coverage Check:**
```shell
# Generate and open coverage report (Windows)
git coverage

# Or using Gradle tasks
./gradlew test jacocoTestReport && start build/reports/jacoco/test/html/index.html
```

### 🔧 VS Code Integration

If you're using VS Code, you can run tests using the integrated tasks:

1. Open Command Palette (`Ctrl+Shift+P`)
2. Type `Tasks: Run Task`
3. Select one of:
   - `test` - Run basic tests
   - `coverage-report` - Run tests with coverage
   - `full-coverage-check` - Run tests with coverage verification
   - `open-coverage-report` - Generate and open coverage report

### ⚡ Quick Test Commands

```shell
# Fast tests (no coverage)
./gradlew test -x jacocoTestReport

# Build without tests
./gradlew build -x test

# Clean and test
./gradlew clean test

# Test with info logging
./gradlew test --info
```

### 🎯 Quality Checks Before Commit/Push

We provide scripts to ensure code quality:

#### Pre-commit Checks (Fast)
```shell
# Windows
./pre-commit.bat

# Linux/macOS  
./pre-commit.sh
```

#### Pre-push Checks (Complete)
```shell
# Windows
./pre-push.bat

# Linux/macOS
./pre-push.sh
```

These scripts will:
- Compile source code
- Compile test code  
- Run all tests
- Generate coverage reports
- Verify coverage thresholds

For more detailed information about testing workflows, see [QUALITY_GUIDE.md](QUALITY_GUIDE.md).

### 📈 Coverage Standards

- **Minimum Coverage**: 80% overall
- **Reports Generated**: HTML (human-readable) and XML (CI/CD)
- **Coverage Types**: Line, Branch, Method, and Class coverage
- **Exclusions**: Application main class and configuration classes

