package mortengf.ai.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * A minimal MCP server that exposes a single tool: calculate.
 *
 * Communicates over stdio — Claude Desktop launches it as a subprocess
 * and talks to it through standard input/output, exactly like the
 * filesystem MCP server already configured in Claude Desktop.
 */
public class CalculatorMcpServer {

    public static void main(String[] args) throws InterruptedException {
        // JacksonMcpJsonMapper wraps ObjectMapper — required by the SDK
        var jsonMapper = new JacksonMcpJsonMapper(new ObjectMapper());
        var transport = new StdioServerTransportProvider(jsonMapper);

        var inputSchema = new McpSchema.JsonSchema(
                "object",
                Map.of(
                        "operation", Map.of(
                                "type", "string",
                                "enum", List.of("add", "subtract", "multiply", "divide"),
                                "description", "The mathematical operation to perform"
                        ),
                        "a", Map.of(
                                "type", "number",
                                "description", "First number"
                        ),
                        "b", Map.of(
                                "type", "number",
                                "description", "Second number"
                        )
                ),
                List.of("operation", "a", "b"),  // required fields
                false,                            // additionalProperties
                null,
                null
        );

        var tool = new McpSchema.Tool(
                "calculate",
                "Calculator",
                "Perform a mathematical calculation",
                inputSchema,
                null,
                null,
                null
        );

        var calculatorTool = new McpServerFeatures.SyncToolSpecification(
                tool,
                (exchange, arguments) -> {
                    String result = calculate(arguments);
                    return McpSchema.CallToolResult.builder()
                            .content(List.of(new McpSchema.TextContent(result)))
                            .isError(false)
                            .build();
                }
        );

        var skillTool = new McpServerFeatures.SyncToolSpecification(
                new McpSchema.Tool(
                        "get_skill",
                        "Calculator Skill",
                        "Returns guidance on how to best use the calculator tool",
                        new McpSchema.JsonSchema("object", Map.of(), List.of(), false, null, null),
                        null, null, null
                ),
                (exchange, arguments) -> {
                    try (var stream = CalculatorMcpServer.class.getResourceAsStream("/SKILL.md")) {
                        if (stream == null) {
                            throw new IOException("SKILL.md not found in JAR");
                        }
                        String guidance = new String(stream.readAllBytes());
                        return McpSchema.CallToolResult.builder()
                                .content(List.of(new McpSchema.TextContent(guidance)))
                                .isError(false)
                                .build();
                    } catch (IOException e) {
                        return McpSchema.CallToolResult.builder()
                                .content(List.of(new McpSchema.TextContent("Error reading SKILL.md: " + e.getMessage())))
                                .isError(true)
                                .build();
                    }
                }
        );

        McpSyncServer server = McpServer.sync(transport)
                .serverInfo("calculator-mcp-server", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .build())
                .tools(calculatorTool, skillTool)
                .build();

        Runtime.getRuntime().addShutdownHook(new Thread(server::close));
        Thread.currentThread().join();
    }

    private static String calculate(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");
        double a = ((Number) arguments.get("a")).doubleValue();
        double b = ((Number) arguments.get("b")).doubleValue();

        double result = switch (operation) {
            case "add"      -> a + b;
            case "subtract" -> a - b;
            case "multiply" -> a * b;
            case "divide"   -> b != 0 ? a / b : Double.NaN;
            default         -> throw new IllegalArgumentException("Unknown operation: " + operation);
        };

        if (Double.isNaN(result)) {
            return "Error: Division by zero is not allowed";
        }

        return String.valueOf(result);
    }
}