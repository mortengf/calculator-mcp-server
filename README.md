# calculator-mcp-server
A mini Java Model Context Protocol (MCP) server that exposes two tools:

- `calculate` — performs arithmetic (add, subtract, multiply, divide)
- `get_skill` — returns usage guidance from `SKILL.md`, helping AI clients understand how to best use the server

Built with the [official MCP Java SDK](https://github.com/modelcontextprotocol/java-sdk), using stdio transport so it can be connected directly to Claude Desktop.

## Project structure

```
src/
  main/
    java/mortengf/ai/mcp/
      CalculatorMcpServer.java   # MCP server with two tools: calculate and get_skill
    resources/
      SKILL.md                   # Usage guidance returned by the get_skill tool
```

## Motivation

[mini-agent](https://github.com/mortengf/mini-agent) implements a mini Java agent loop that calls Claude API directly, with `CalculatorTool` defined using Claude-specific JSON (`input_schema`).

This server solves the portability problem identified in `mini-agent`: tools are now defined in the MCP-standard format and can be used by any MCP-compatible AI client — not just Claude.

## What is a skill?

A skill is an MCP tool that returns guidance instead of performing an action. When an AI client connects to this server, it can call `get_skill` to understand how to best use the `calculate` tool.

The guidance lives in `SKILL.md` (packed into the JAR as a classpath resource) — the same pattern used by Claude Desktop's built-in skill system.

## Prerequisites

- Java 17+
- Maven 3.6+
- Claude Desktop (to connect the server as an MCP tool)

## Building

```bash
mvn package -q
```

## Connecting to Claude Desktop

Add this to `~/Library/Application Support/Claude/claude_desktop_config.json`:

```json
"calculator": {
  "command": "/path/to/java",
  "args": ["-jar", "/path/to/calculator-mcp-server/target/calculator-mcp-server-1.0-SNAPSHOT.jar"]
}
```

Replace `/path/to/java` with the output of:

```bash
sdk home java 17.0.1-tem
# → /Users/mgf/.sdkman/candidates/java/17.0.1-tem/bin/java
```

Restart Claude Desktop — you can now ask Claude to calculate something and it will use your tool.

## Example

> **You:** What is 1 / 0?
>
> **Claude:** Division by zero is not allowed.

> **You:** What is (123 * 456) + (789 / 3)?
>
> **Claude:** 56,351
