package mortengf.ai.mcp;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorMcpServerTest {

    @Test
    void add() {
        assertEquals("5.0", CalculatorMcpServer.calculate(Map.of("operation", "add", "a", 2, "b", 3)));
    }

    @Test
    void subtract() {
        assertEquals("1.0", CalculatorMcpServer.calculate(Map.of("operation", "subtract", "a", 4, "b", 3)));
    }

    @Test
    void multiply() {
        assertEquals("12.0", CalculatorMcpServer.calculate(Map.of("operation", "multiply", "a", 3, "b", 4)));
    }

    @Test
    void divide() {
        assertEquals("2.5", CalculatorMcpServer.calculate(Map.of("operation", "divide", "a", 5, "b", 2)));
    }

    @Test
    void divideByZero() {
        assertEquals("Error: Division by zero is not allowed",
                CalculatorMcpServer.calculate(Map.of("operation", "divide", "a", 7, "b", 0)));
    }

    @Test
    void unknownOperation() {
        assertThrows(IllegalArgumentException.class,
                () -> CalculatorMcpServer.calculate(Map.of("operation", "modulo", "a", 7, "b", 3)));
    }
}
