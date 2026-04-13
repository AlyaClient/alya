# Alya Client Code Style Guide

## Control Flow

Do NOT put a space between the keyword and the opening parenthesis.

```java
// wrong
if (condition) { ... }
while (condition) { ... }
for (int i = 0; i < n; i++) { ... }
switch (value) { ... }

// correct
if(condition) { ... }
while(condition) { ... }
for(int i = 0; i < n; i++) { ... }
switch(value) { ... }
```

This applies to: `if`, `while`, `for`, `switch`, `catch`, and all other control flow statements.

---

## Variable Names

Variable names must be fully descriptive. Abbreviations are not allowed.

```java
// wrong
int btn;
String usr;
boolean isConn;
int idx;

// correct
int button;
String user;
boolean isConnected;
int index;
```

Single-letter variable names are only permitted as loop counters (e.g. `i`,
`j`, `k`).

---

## Immutability

Declare classes, variables, parameters, and fields as `final` wherever possible.

```java
// wrong
int count = getCount();
String name = getName();

// correct
final int count = getCount();
final String name = getName();
```

This includes:
- Local variables that are not reassigned
- Method parameters that are not reassigned
- Methods not inside a final class
- Fields that are only assigned once (in the constructor or at declaration)
- Classes that are not extended or abstract

```java
// wrong
public void doSomething(String input) { ... }
public class MyClass { ... }

// correct
public void doSomething(final String input) { ... }
public final class MyClass { ... }
```
