[![Clojure CI](https://github.com/SantiValdezUlzurrun/Interprete-AppleSoft-Basic/actions/workflows/clojure.yml/badge.svg)](https://github.com/SantiValdezUlzurrun/Interprete-AppleSoft-Basic/actions/workflows/clojure.yml)

# Intérprete de Applesoft Basic en Clojure

> Writing a compiler or interpreter is an excellent educational project and enhances skills in programming language understanding and design, data structure and algorithm design and a wide range of programming techniques. Studying compilers and interpreters makes you a better programmer.

## Enunciado:
En este trabajo práctico se pide desarrollar, en el lenguaje Clojure, un intérprete de Applesoft BASIC.

El intérprete a desarrollar debe ofrecer los dos modos de ejecución de Applesoft BASIC: ejecución inmediata y ejecución diferida.

Deberá estar basado en un REPL (read-eval-print-loop) que acepte, además de sentencias de Applesoft BASIC, dos comandos de Apple DOS 3.3 (LOAD y SAVE).

No será necesario utilizar espacios para separar los distintos símbolos del lenguaje.
Soportará tres tipos de datos:
* números enteros
* números de punto flotante
* cadenas de caracteres


### Ejecución:
```

java -jar clojure.jar

user=>(load-file "basic.clj")

user=>(driver-loop)

] LOAD NATO.BAS

] RUN
```

## Licencia
Este repositorio esta bajo la licencia MIT
