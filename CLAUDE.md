# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development

- **Java:** 17
- **Build:** Maven — `mvn package -q` produces a fat JAR via `maven-shade-plugin` 3.5.1
- **Tests:** JUnit 5 (`junit-jupiter:5.10.2`) — run with `mvn test`
- **Key dependencies:** MCP SDK `io.modelcontextprotocol.sdk:mcp:1.0.0`, SLF4J `slf4j-simple:2.0.9`

## Git Workflow

After making changes, commit and push using the `/commit` skill. Commit messages follow conventional commits and explain *why*, not just *what*.

## Commands

```bash
# Build (produces fat JAR via maven-shade-plugin)
mvn package -q

# Run tests
mvn test

# Run a single test
mvn test -Dtest=CalculatorMcpServerTest#divide

# Run the server locally (stdio transport — blocks until killed)
java -jar target/calculator-mcp-server-1.0-SNAPSHOT.jar
```

## Architecture

Single-class MCP server (`CalculatorMcpServer`) that communicates over **stdio** using the [official MCP Java SDK](https://github.com/modelcontextprotocol/java-sdk) (`io.modelcontextprotocol.sdk:mcp`). Claude Desktop launches it as a subprocess and exchanges JSON-RPC messages through stdin/stdout.

The server exposes two tools:

- **`calculate`** — performs arithmetic (add, subtract, multiply, divide) via the static `calculate(Map)` method, which is package-private for testability.
- **`get_skill`** — reads `SKILL.md` from the classpath (packed into the JAR) and returns it as text, giving AI clients usage guidance without any hardcoded prompts.

`SKILL.md` lives in `src/main/resources/` and is the only resource file. Modifying it changes the guidance returned at runtime without touching Java code.

The build uses `maven-shade-plugin` to produce a single fat JAR with `CalculatorMcpServer` as the main class.

## Connecting to Claude Desktop

Add to `~/Library/Application Support/Claude/claude_desktop_config.json`:

```json
"calculator": {
  "command": "/path/to/java",
  "args": ["-jar", "/path/to/calculator-mcp-server/target/calculator-mcp-server-1.0-SNAPSHOT.jar"]
}
```

Find the Java path with: `sdk home java 17.0.1-tem`
