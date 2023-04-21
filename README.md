# Create: Computers
Computer mod for Create


## CScript

CScript is a custom interpreted language writen in Java that controls everything related to the computers in Create: Computers.

## Speed

Because it is interpreted, and in Java, it may run a lot slower for programs that require things like recursion.

For example, on my computer:

```python
function fib(n) {
  if (n < 2) return n;
  return fib(n - 1) + fib(n - 2); 
}

var before = clock();
print fib(40);
var after = clock();
print after - before;
```
This takes about a minute to run.

Good for you, this is a Minecraft mod, and you won't be needing the Fibonacci sequence to run farms in your base.

...
