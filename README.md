# RIHNParser

**A Rudimentary IUPAC Hydrocarbon Nomenclature Parser**

`RIHNParser` is a compiler-design project that parses a limited subset of IUPAC nomenclature for hydrocarbons and transforms the input string into a graph-based representation of the chemical compound.

This project implements the core stages of a compiler:

1.  **Lexical Analysis (Lexer):** Converts the input string into a stream of tokens.
2.  **Syntax Analysis (Parser):** Consumes the tokens to build an Abstract Syntax Tree (AST), represented here by the `Hydrocarbon` object.
3.  **Semantic Analysis (Semantic Analyzer):** Traverses the AST to validate chemical and naming rules (e.g., valency, locant rules).
4.  **Code Generation (Graph Generator):** Transforms the validated AST into a final graph data structure, represented by `Compound` and `Atom` objects.

The project also includes a **Spring Boot web application** that provides an interface to use the parser and visualize the resulting compound graph using D3.js.

-----

## Features

* **Compiler Pipeline:** Demonstrates a full lexer -\> parser -\> analyzer -\> generator pipeline.
* **Syntactic Validation:** Enforces the language's structure defined by a formal EBNF grammar.
* **Semantic Validation:** Checks for chemical correctness, such as:
    * **Valency Rules:** Ensures no carbon atom exceeds its valence of 4.
    * **Locant Rules:** Validates that locants are in the correct range, are not duplicated, and follow the "lowest set of locants" rule.
    * **Cyclic Rules:** Enforces that cyclic compounds must have at least 3 carbons.
* **Graph Generation:** Creates a true graph model of the hydrocarbon, including all carbon and hydrogen atoms and their respective bonds (single, double, triple).
* **Web-based Visualization:** Provides a web UI to input a hydrocarbon name and see a graph of the resulting molecule.

-----

## Technology Stack

* **Core:** Java 21
* **Backend & Web:** Spring Boot
* **Testing:** JUnit 5
* **Build:** Apache Maven

-----

## How It Works: The Pipeline

The entire parsing process is orchestrated by the `Rihn.getCompound(String input)` method.

1.  **Lexer (`Lexer.java`)**
    The `Lexer` scans the input string (e.g., `"cyclohexa-1,3,5-triene"`) and produces a list of tokens, such as `CYCLO`, `WORD` ("hexa"), `HYPHEN`, `DIGIT` ("1"), `COMMA`, etc..

2.  **Parser (`Parser.java`)**
    The `Parser` consumes the list of tokens, using recursive descent to match them against the project's formal grammar. It builds an in-memory `Hydrocarbon` object, which serves as the Abstract Syntax Tree (AST) root node.

3.  **Semantic Analyzer (`SemanticAnalyzer.java`)**
    The `SemanticAnalyzer` walks the `Hydrocarbon` AST to validate its meaning. This is where most chemical rules are enforced. For example, it throws a `SemanticAnalyzerException` if the user enters "cyclomethane" (cyclic compounds must have \>2 carbons) or "pent-2-en-3-yne" (carbon \#3 would exceed its valency).

4.  **Graph Generator (`CompoundGenerator.java`)**
    Once the `Hydrocarbon` object is validated, the `CompoundGenerator` builds the final graph. It first constructs the carbon chain, adding bonds (double, triple, or single) at the specified locant positions. Then, it iterates through the carbon atoms and "fills" their remaining valency with new `Atom` objects for Hydrogen. The final `Compound` object is the graph representation of the molecule.

5.  **Visualization (`CompoundJSONGenerator.java`)**
    If run via the web application, the `Compound` object is passed to the `CompoundJSONGenerator`. This utility converts the `Atom` and `Bond` objects into a `nodes` and `links` JSON format, which is then passed to the D3.js library in the `index.html` template to be rendered as a force-directed graph.

-----

## Supported Grammar (EBNF)

The parser is built to recognize the following formal grammar:

```ebnf
hydrocarbon       ::= "cyclo"? stem ( type_alkane | type_alkene | type_alkyne | type_enyne ) "e"
type_enyne        ::= ( complex_group | simple_group ) "en" enyne_group "yn"
type_alkyne       ::= ( complex_group | simple_group ) "yn"
type_alkene       ::= ( complex_group | simple_group ) "en"
type_alkane       ::= "an"
complex_group     ::= "a" locants multiplying_affix
enyne_group       ::= (locant | locants) multiplying_affix?
simple_group      ::= locant
locants           ::= "-" digit ("," digit)+ "-"
locant            ::= "-" digit "-"
stem              ::= "meth" | "eth" | "prop" | "but" | "pent" | "hex" | "hept" | "oct" | "non" | "dec"
multiplying_affix ::= "di" | "tri" | "tetra" | "penta" | "hexa" | "hepta" | "octa" | "nona"
digit             ::= "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
```

### Examples

* `cyclopropane`
* `cyclohexa-1,3,5-triene`
* `eth-1-yne`
* `prop-1-ene`
* `propa-1,2-diene`
* `hex-2-en-4-yne`
* `hexa-1,3-diene`

-----

## Usage as a Library

```java
import com.ihortymkiv.chemistry.Compound;
import com.ihortymkiv.rihn.Rihn;

// ...

String name = "propane";
try {
    Compound compound = Rihn.getCompound(name);
    // Do something with the compound graph
} catch (Exception e) {
    System.err.println("Error parsing " + name + ": " + e.getMessage());
}
```

---

## Build & Run

### Prerequisites

* Java 21 (JDK 21)
* Apache Maven

### Running Tests
1.  Clone the repository.
2.  Navigate to the project's root directory (where `pom.xml` is located).
3.  Run tests:
    ```sh
    mvn test
    ```

### Running the Web Application

1.  Clone the repository.
2.  Navigate to the project's root directory (where `pom.xml` is located).
3.  Run the application using the Spring Boot Maven plugin:
    ```sh
    mvn spring-boot:run
    ```
4.  Open your web browser and navigate to `http://localhost:8080` (or the port specified in the console).

-----

## Project Structure

* `src/main/java/com/ihortymkiv/chemistry/`
    * Contains the graph data structure classes (`Atom`, `Compound`) and related exceptions. This is the "target language" or final output of the compiler.
* `src/main/java/com/ihortymkiv/rihn/`
    * Contains the core compiler logic.
    * `Rihn.java`: Public-facing API for the compiler.
    * `Lexer.java`: Lexical analyzer.
    * `Parser.java`: Syntax analyzer (builds the `Hydrocarbon` AST).
    * `SemanticAnalyzer.java`: Semantic validator.
    * `CompoundGenerator.java`: Converts the AST to the `Compound` graph.
    * `*.java` (Type, Stem, Locants, etc.): Classes that define the nodes of the Abstract Syntax Tree, exceptions and utilities.
* `src/main/java/com/ihortymkiv/web/`
    * `SpringWebApplication.java`: The Spring Boot controller that handles web requests.
    * `CompoundJSONGenerator.java`: Utility to convert the `Compound` graph to JSON for D3.js.
* `src/main/resources/`
    * `templates/index.html`: The Thymeleaf/HTML template for the web UI.
    * `static/`: Contains the CSS and JavaScript (D3.js) assets.
* `src/test/java/`
    * Contains JUnit tests for the lexer, parser, semantic analyzer, and graph generator.

-----

## License

This project is licensed under the **MIT License**.