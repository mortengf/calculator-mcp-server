# Calculator Tool — Usage Guidance

## When to use
- Always use the `calculate` tool for arithmetic — never compute in your head
- Use it even for simple calculations to ensure accuracy

## How to use
- Break complex expressions into steps, one operation at a time
- Use the result of one call as input `a` or `b` in the next call
- Supported operations: add, subtract, multiply, divide

## Example: (123 * 456) + (789 / 3)
1. calculate(multiply, 123, 456) → 56088
2. calculate(divide, 789, 3)     → 263
3. calculate(add, 56088, 263)    → 56351

## Error handling
- Division by zero returns an error message — handle it gracefully
