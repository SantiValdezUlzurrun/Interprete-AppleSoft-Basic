(ns tp.core-test
  (:require [clojure.test :refer :all]
          [tp.basic :refer :all]))

(deftest testPalabrasReservadas
  (testing "Al Preguntarse Si Las Siguientes Palabras Son Reservadas El Resultado Es El Indicado"
    (is (= false (palabra-reservada? 'SPACE)))
    (is (= true (palabra-reservada? 'REM)))))


(deftest testOperadores
  (testing "Al Preguntarse Si Las Siguientes Simbolos Son Operadores El Resultado Es El Indicado"
    (is (= false (operador? (symbol "%"))))
    (is (= true (operador? '+)))))

(deftest testVariableFloat
  (testing "Al preguntarse si la variable es del tipo float se devuelve el resultado correcto"
    (is (= false (variable-float? 'X%)))
    (is (= false (variable-float? 'X$)))
    (is (= true (variable-float? 'X)))))

(deftest testVariableInteger
  (testing "Al preguntarse si la variable es del tipo integer se devuelve el resultado correcto"
    (is (= true (variable-integer? 'X%)))
    (is (= false (variable-integer? 'X$)))
    (is (= false (variable-integer? 'X)))))

(deftest testVariableString
  (testing "Al preguntarse si la variable es del tipo string se devuelve el resultado correcto"
    (is (= false (variable-string? 'X%)))
    (is (= true (variable-string? 'X$)))
    (is (= false (variable-string? 'X)))))

(deftest testeliminarCeroDecimal
  (testing "Al eliminar el cero decimal de los siguientes ejemplos el valor es el esperado"
    (is (= 1.5 (eliminar-cero-decimal 1.5)))
    (is (= 'A (eliminar-cero-decimal 'A)))
    (is (= 1 (eliminar-cero-decimal 1.0)))
    (is (= 1.5 (eliminar-cero-decimal 1.50)))))
 

(deftest testeliminarCeroEntero
  (testing "Al eliminar el cero entero de los siguientes ejemplos el valor es el esperado"
    (is (= nil (eliminar-cero-entero nil)))
    (is (= "A" (eliminar-cero-entero 'A)))
    (is (= "0" (eliminar-cero-entero 0)))
    (is (= "1.5" (eliminar-cero-entero 1.5)))
    (is (= "1" (eliminar-cero-entero 1)))
    (is (= "-1" (eliminar-cero-entero -1)))
    (is (= "-1.5" (eliminar-cero-entero -1.5))) 
    (is (= ".5" (eliminar-cero-entero 0.5)))
    (is (= "-.5" (eliminar-cero-entero -0.5)))))

(deftest test-anular-invalidos
  (testing "Al ingresar una sentencia se anulan los invalidos"
    (is (= '(IF X nil * Y < 12 THEN LET nil X = 0)
           (anular-invalidos '(IF X & * Y < 12 THEN LET ! X = 0))))))

(deftest test-cargar-linea
  (testing "Al recibir una linea de codigo y un ambiente retorna el ambiente correctamente actualizado"
    (is (= '[((10 (PRINT X))) [:ejecucion-inmediata 0] [] [] [] 0 {}] 
           (cargar-linea '(10 (PRINT X)) [() [:ejecucion-inmediata 0] [] [] [] 0 {}])))

    (is (= '[((10 (PRINT X)) (20 (X = 100))) [:ejecucion-inmediata 0] [] [] [] 0 {}]
           (cargar-linea '(20 (X = 100)) ['((10 (PRINT X))) [:ejecucion-inmediata 0] [] [] [] 0 {}])))
    
    (is (= '[((10 (PRINT X)) (15 (X = X + 1)) (20 (X = 100))) [:ejecucion-inmediata 0] [] [] [] 0 {}]
           (cargar-linea '(15 (X = X + 1)) ['((10 (PRINT X)) (20 (X = 100))) [:ejecucion-inmediata 0] [] [] [] 0 {}])))

    (is (= '[((10 (PRINT X)) (15 (X = X - 1)) (20 (X = 100))) [:ejecucion-inmediata 0] [] [] [] 0 {}]
           (cargar-linea '(15 (X = X - 1)) ['((10 (PRINT X)) (15 (X = X + 1)) (20 (X = 100))) [:ejecucion-inmediata 0] [] [] [] 0 {}])))))


(deftest test-eliminar-rem
  (testing "Al recibir una representacion intermedia elimina las sentencias REM correctamente"
  (is (= '((10 (PRINT X)) (20 (DATA HOLA)) (100 (DATA MUNDO , 10 , 20)))
      (eliminar-rem '((10 (PRINT X) (REM ESTE NO) (DATA 30)) (20 (DATA HOLA)) (100 (DATA MUNDO , 10 , 20))))))
  (is (= '((10 (PRINT X) (DATA 30)) (20) (100 (DATA MUNDO , 10 , 20))) 
      (eliminar-rem '((10 (PRINT X) (DATA 30)) (20 (REM HOLA)) (100 (DATA MUNDO , 10 , 20))))))))


(deftest test-expandir-nexts
  (testing "Al recibir una secuencia de sentencias expande los nexts correctamente"
    (is (= '((PRINT 1) (NEXT A) (NEXT B))
           (expandir-nexts (list '(PRINT 1) (list 'NEXT 'A (symbol ",") 'B)))))
    (is (= '(10 (PRINT X) (NEXT A))
           (expandir-nexts (list 10 '(PRINT X) '(NEXT A)))))
    (is (= '(10 (PRINT X) (PRINT Y))
           (expandir-nexts '(10 (PRINT X) (PRINT Y)))))
    (is (= '(10 (PRINT Y) (NEXT A) (DATA HOLA))
           (expandir-nexts '(10 (PRINT Y) (NEXT A) (DATA HOLA)))))
    (is (= '(10 (PRINT Y) (NEXT A) (NEXT B) (DATA HOLA))
           (expandir-nexts (list 10 '(PRINT Y) (list 'NEXT 'A (symbol ",") 'B) '(DATA HOLA)))))  
    (is (= '((NEXT A) (NEXT B) (PRINT 1))
           (expandir-nexts (list (list 'NEXT 'A (symbol ",") 'B) '(PRINT 1)))))
    (is (= '((NEXT A) (PRINT 1))
           (expandir-nexts '((NEXT A) (PRINT 1)))))))
 
(deftest test-contar-sentencias
  (testing "Al recibir un nro-linea y un amb cuenta las sentencias de esa linea correctamente"
    (is (= 2
        (contar-sentencias 10 [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [10 1] [] [] [] 0 {}])))
    (is (= 1
        (contar-sentencias 15 [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [10 1] [] [] [] 0 {}])))
    (is (= 2
        (contar-sentencias 20 [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [10 1] [] [] [] 0 {}])))))

(deftest test-buscar-lineas-restantes
  (testing "Al recibir un ambiente devuelve una lista con las lineas restantes de la siguiente forma"
    (is (= nil
          (buscar-lineas-restantes [() [:ejecucion-inmediata 0] [] [] [] 0 {}])))
    (is (= nil
           (buscar-lineas-restantes ['((PRINT X) (PRINT Y)) [:ejecucion-inmediata 2] [] [] [] 0 {}])))
    (is (= (list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J)))
           (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [10 2] [] [] [] 0 {}])))
    (is (= (list '(10 (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J)))
           (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [10 1] [] [] [] 0 {}])))
    (is (= (list '(10) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J)))
           (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [10 0] [] [] [] 0 {}])))
    (is (= (list '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J)))
           (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [15 1] [] [] [] 0 {}])))
    (is (= (list '(15) (list 20 (list 'NEXT 'I (symbol ",") 'J)))
           (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [15 0] [] [] [] 0 {}])))
    (is (= '((20 (NEXT I) (NEXT J)))
           (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 3] [] [] [] 0 {}])))
    (is (= '((20 (NEXT I) (NEXT J)))
           (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 2] [] [] [] 0 {}])))
    (is (= '((20 (NEXT J)))
           (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 1] [] [] [] 0 {}])))
    (is (= '((20))
           (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 0] [] [] [] 0 {}])))
    (is (= '((20))
           (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 -1] [] [] [] 0 {}])))
    (is (= nil
           (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [25 0] [] [] [] 0 {}])))))

(deftest test-preprocesar-expresion
  (testing "Al recibir una expresion y el ambiente remplaza correctamente los valores"
    (is (= '("HOLA" + " MUNDO" + "")
           (preprocesar-expresion '(X$ + " MUNDO" + Z$) ['((10 (PRINT X))) [10 1] [] [] [] 0 '{X$ "HOLA"}])))
    (is (= '(5 + 0 / 2 * 0)
           (preprocesar-expresion '(X + . / Y% * Z) ['((10 (PRINT X))) [10 1] [] [] [] 0 '{X 5 Y% 2}])))))

(deftest test-desambiguar
  (testing "Al desambiguar las siguientes expresiones el resultado es correcto"
    (is (= (list '-u 2 '* (symbol "(") '-u 3 '+ 5 '- (symbol "(") 2 '/ 7 (symbol ")") (symbol ")"))
           (desambiguar (list '- 2 '* (symbol "(") '- 3 '+ 5 '- (symbol "(") '+ 2 '/ 7 (symbol ")") (symbol ")")))))
    (is (= (list 'MID$ (symbol "(") 1 (symbol ",") 2 (symbol ")"))
           (desambiguar (list 'MID$ (symbol "(") 1 (symbol ",") 2 (symbol ")")))))
    (is (= (list 'MID3$ (symbol "(") 1 (symbol ",") 2 (symbol ",") 3 (symbol ")"))
           (desambiguar (list 'MID$ (symbol "(") 1 (symbol ",") 2 (symbol ",") 3 (symbol ")")))))
    (is (= (list 'MID3$ (symbol "(") 1 (symbol ",") '-u 2 '+ 'K (symbol ",") 3 (symbol ")"))
           (desambiguar (list 'MID$ (symbol "(") 1 (symbol ",") '- 2 '+ 'K (symbol ",") 3 (symbol ")")))))))

(deftest test-ejecutar-asignacion
  (testing "Al recibir una asignacion y un amb y devuelve el amb actualizado"
    (is (= '[((10 (PRINT X))) [10 1] [] [] [] 0 {X 5}]
           (ejecutar-asignacion '(X = 5) ['((10 (PRINT X))) [10 1] [] [] [] 0 {}])))
    (is (= '[((10 (PRINT X))) [10 1] [] [] [] 0 {X 5}]
           (ejecutar-asignacion '(X = 5) ['((10 (PRINT X))) [10 1] [] [] [] 0 '{X 2}])))
    (is (= '[((10 (PRINT X))) [10 1] [] [] [] 0 {X 3}]
           (ejecutar-asignacion '(X = X + 1) ['((10 (PRINT X))) [10 1] [] [] [] 0 '{X 2}])))
    (is (= '[((10 (PRINT X))) [10 1] [] [] [] 0 {X$ "HOLA MUNDO"}]
            (ejecutar-asignacion '(X$ = X$ + " MUNDO") ['((10 (PRINT X))) [10 1] [] [] [] 0 '{X$ "HOLA"}])))))

(deftest test-continuar-linea
  (testing "Al recibir un amb devuelve el resultado correcto"
    (is (= [nil [(list '(10 (PRINT X)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 3] [] [] [] 0 {}]]
           (continuar-linea [(list '(10 (PRINT X)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 3] [] [] [] 0 {}])))
    (is (= [:omitir-restante [(list '(10 (PRINT X)) '(15 (GOSUB 100) (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [15 1] [] [] [] 0 {}]]
           (continuar-linea [(list '(10 (PRINT X)) '(15 (GOSUB 100) (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 3] [[15 2]] [] [] 0 {}])))))

(deftest test-aridad
  (testing "Al recibir las sentencias correspondientes devuelve la aridad correcta"
    (is (= 0
           (aridad 'THEN)))
    (is (= 1
           (aridad 'SIN)))
    (is (= 2
           (aridad '*)))
    (is (= 2
           (aridad 'MID$)))
    (is (= 3
           (aridad 'MID3$)))))
