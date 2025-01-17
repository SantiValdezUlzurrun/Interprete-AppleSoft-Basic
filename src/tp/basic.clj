(ns tp.basic)

; https://porkrind.org/a2
; https://www.calormen.com/jsbasic
; https://www.scullinsteel.com/apple2/
; https://fjkraan.home.xs4all.nl/comp/apple2faq/app2asoftfaq.html
; https://www.landsnail.com/a2ref.htm
; https://mitpress.mit.edu/sites/default/files/sicp/full-text/book/book.html

(declare driver-loop)                     ; NO TOCAR
(declare string-a-tokens)                 ; NO TOCAR
(declare evaluar-linea)                   ; NO TOCAR
(declare buscar-mensaje)                  ; NO TOCAR
(declare seleccionar-destino-de-on)       ; NO TOCAR
(declare leer-data)                       ; NO TOCAR
(declare leer-con-enter)                  ; NO TOCAR
(declare retornar-al-for)                 ; NO TOCAR
(declare continuar-programa)              ; NO TOCAR
(declare ejecutar-programa)               ; NO TOCAR
(declare mostrar-listado)                 ; NO TOCAR
(declare cargar-arch)                     ; NO TOCAR
(declare grabar-arch)                     ; NO TOCAR
(declare calcular-expresion)              ; NO TOCAR
(declare desambiguar-mas-menos)           ; NO TOCAR
(declare desambiguar-mid)                 ; NO TOCAR
(declare shunting-yard)                   ; NO TOCAR
(declare calcular-rpn)                    ; NO TOCAR
(declare imprimir)                        ; NO TOCAR
(declare desambiguar-comas)               ; NO TOCAR

(declare evaluar)                         ; COMPLETAR
                                            ;  DATA [X]
                                            ;  READ [X]
                                            ;  RESTORE [X]
                                            ;  CLEAR [X]
                                            ;  LET/= [X]
                                            ;  LIST [X]
                                            ;  END [X]
                                            ;  IF/THEN [X]
                                            ;  INPUT [X]
                                            ;  PRINT [X]
                                            ;  REM [X]
                                            ;  NEW [X]
                                            ;  RUN [X]
                                            ;  FOR/TO/NEXT/STEP [x]
                                            ;  GOSUB/RETURN [X]
                                            ;  GOTO [X]
                                            ;  IF [X]
                                            ;  ON/GOSUB/RETURN [X]
                                            ;  ON/GOTO [X]
                                            ;  ENV [X]
                                            ;  EXIT [X]
(declare aplicar)                         ; COMPLETAR [X]
                                            ;  ATN [X]
                                            ;  INT [X]
                                            ;  SIN [X]
                                            ;  LEN [X]
                                            ;  MID$ [X]
                                            ;  ASC  [X]
                                            ;  CHR$ [X]
                                            ;  STR$ [X]
                                            ;  + [X]
                                            ;  - [X]
                                            ;  * [X]
                                            ;  / [X]
                                            ;  ^ [X]
                                            ;  = [X]
                                            ;  <> [X]
                                            ;  < [X]
                                            ;  <= [X]
                                            ;  > [X]
                                            ;  >= [X]
                                            ;  AND [X]
                                            ;  OR [X]

(declare palabra-reservada?)              ; IMPLEMENTAR [X]
(declare operador?)                       ; IMPLEMENTAR [X]
(declare anular-invalidos)                ; IMPLEMENTAR [X]
(declare cargar-linea)                    ; IMPLEMENTAR [X]
(declare expandir-nexts)                  ; IMPLEMENTAR [X]
(declare dar-error)                       ; IMPLEMENTAR [X]
(declare variable-float?)                 ; IMPLEMENTAR [X]
(declare variable-integer?)               ; IMPLEMENTAR [X]
(declare variable-string?)                ; IMPLEMENTAR [X]
(declare contar-sentencias)               ; IMPLEMENTAR [X]
(declare buscar-lineas-restantes)         ; IMPLEMENTAR [X]
(declare continuar-linea)                 ; IMPLEMENTAR [X]
(declare extraer-data)                    ; IMPLEMENTAR [X]
(declare ejecutar-asignacion)             ; IMPLEMENTAR [X]
(declare preprocesar-expresion)           ; IMPLEMENTAR [X]
(declare desambiguar)                     ; IMPLEMENTAR [X]
(declare precedencia)                     ; IMPLEMENTAR [X]
(declare aridad)                          ; IMPLEMENTAR [X]
(declare eliminar-cero-decimal)           ; IMPLEMENTAR [X]
(declare eliminar-cero-entero)            ; IMPLEMENTAR [X]

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; driver-loop: el REPL del interprete de Applesoft BASIC
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn driver-loop
   ([]
      (println)
      (println "Interprete de Applesoft II BASIC en Clojure")
      (println "Trabajo Practico de 75.14/95.48 Lenguajes Formales - 2020")
      (println)
      (println "Inspirado en:    ****************************************")
      (println "                 *  APPLESOFT ][  FLOATING POINT BASIC  *")
      (println "                 *               APRIL 1978             *")
      (println "                 ****************************************")
      (println "                 COPYRIGHT 1978 APPLE COMPUTER")
      (println "                 COPYRIGHT 1976 BY MICROSOFT")
      (println "                 ALL RIGHTS RESERVED")
      (println)
      (println "Corriendo en:                   APPLE II")
      (println "                     DOS VERSION 3.3  SYSTEM MASTER")
      (println "                            JANUARY 1, 1983")
      (println "                 COPYRIGHT APPLE COMPUTER, INC. 1980,1982")
      (flush)
      (driver-loop ['() [:ejecucion-inmediata 0] [] [] [] 0 {}]))  ; [(prog-mem)  [prog-ptrs]  [gosub-return-stack]  [for-next-stack]  [data-mem]  data-ptr  {var-mem}]
   ([amb]
      (prn) (print "] ") (flush)
      (try (let [linea (string-a-tokens (read-line)), cabeza (first linea)]
                (cond (= cabeza '(EXIT)) 'GOODBYE
                      (= cabeza '(ENV)) (do (prn amb) (flush) (driver-loop amb))
                      (integer? cabeza) (if (and (>= cabeza 0) (<= cabeza 63999))
                                            (driver-loop (cargar-linea linea amb))
                                            (do (dar-error 16 (amb 1)) (driver-loop amb))) ; Syntax error
                      (empty? linea) (driver-loop amb)
                      :else (driver-loop (second (evaluar-linea linea (assoc amb 1 [:ejecucion-inmediata (count (expandir-nexts linea))]))))))
           (catch Exception e (dar-error (str "?ERROR " (clojure.string/trim (clojure.string/upper-case (get (Throwable->map e) :cause)))) (amb 1)) (driver-loop amb))))
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; string-a-tokens: analisis lexico y traduccion del codigo a la
; representacion intermedia (listas de listas de simbolos) que
; sera ejecutada (o vuelta atras cuando deba ser mostrada)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn string-a-tokens [s]
  (let [nueva (str s ":"),
        mayu (clojure.string/upper-case nueva),
        sin-cad (clojure.string/replace mayu #"\"(.*?)\"" #(clojure.string/join (take (+ (count (% 1)) 2) (repeat "@")))),
        ini-rem (clojure.string/index-of sin-cad "REM"),
        pre-rem (subs mayu 0 (if (nil? ini-rem) (count mayu) ini-rem)),
        pos-rem (subs mayu (if (nil? ini-rem) (- (count mayu) 1) (+ ini-rem 3)) (- (count mayu) 1)),
        sin-rem (->> pre-rem
                    (re-seq #"EXIT|ENV|DATA[^\:]*?\:|REM|NEW|CLEAR|LIST|RUN|LOAD|SAVE|LET|AND|OR|INT|SIN|ATN|LEN|MID\$|STR\$|CHR\$|ASC|GOTO|ON|IF|THEN|FOR|TO|STEP|NEXT|GOSUB|RETURN|END|INPUT|READ|RESTORE|PRINT|\<\=|\>\=|\<\>|\<|\>|\=|\(|\)|\?|\;|\:|\,|\+|\-|\*|\/|\^|\"[^\"]*\"|\d+\.\d+E[+-]?\d+|\d+\.E[+-]?\d+|\.\d+E[+-]?\d+|\d+E[+-]?\d+|\d+\.\d+|\d+\.|\.\d+|\.|\d+|[A-Z][A-Z0-9]*[\%\$]?|[A-Z]|\!|\"|\#|\$|\%|\&|\'|\@|\[|\\|\]|\_|\{|\||\}|\~")
                    (map #(if (and (> (count %) 4) (= "DATA" (subs % 0 4))) (clojure.string/split % #":") [%]))
                    (map first)
                    (remove nil?)
                    (replace '{"?" "PRINT"})
                    (map #(if (and (> (count %) 1) (clojure.string/starts-with? % ".")) (str 0 %) %))
                    (map #(if (and (>= (count %) 4) (= "DATA" (subs % 0 4))) (let [provisorio (interpose "," (clojure.string/split (clojure.string/triml (subs % 4)) #",[ ]*"))] (list "DATA" (if (= ((frequencies %) \,) ((frequencies provisorio) ",")) provisorio (list provisorio ",")) ":")) %))
                    (flatten)
                    (map #(let [aux (try (clojure.edn/read-string %) (catch Exception e (symbol %)))] (if (or (number? aux) (string? aux)) aux (symbol %))))
                    (#(let [aux (first %)] (if (and (integer? aux) (not (neg? aux))) (concat (list aux) (list (symbol ":")) (rest %)) %)))
                    (partition-by #(= % (symbol ":")))
                    (remove #(.contains % (symbol ":")))
                    (#(if (and (= (count (first %)) 1) (number? (ffirst %))) (concat (first %) (rest %)) %)))]
       (if (empty? pos-rem)
           sin-rem
           (concat sin-rem (list (list 'REM (symbol (clojure.string/trim pos-rem)))))))
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; evaluar-linea: recibe una lista de sentencias y las evalua
; mientras sea posible hacerlo
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn evaluar-linea
  ([sentencias amb]
    (let [sentencias-con-nexts-expandidos (expandir-nexts sentencias)]
         (evaluar-linea sentencias-con-nexts-expandidos sentencias-con-nexts-expandidos amb)))
  ([linea sentencias amb]
    (if (empty? sentencias)
        [:sin-errores amb]
        (let [sentencia (anular-invalidos (first sentencias)), par-resul (evaluar sentencia amb)]
             (if (or (nil? (first par-resul)) (contains? #{:omitir-restante, :error-parcial, :for-inconcluso} (first par-resul)))
                 (if (and (= (first (amb 1)) :ejecucion-inmediata) (= (first par-resul) :for-inconcluso))
                     (recur linea (take-last (second (second (second par-resul))) linea) (second par-resul))
                      par-resul)
                 (recur linea (next sentencias) (assoc (par-resul 1) 1 [(first (amb 1)) (count (next sentencias))]))))))
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; buscar-mensaje: retorna el mensaje correspondiente a un error
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn buscar-mensaje [cod]
  (case cod
    0 "?NEXT WITHOUT FOR ERROR"
    6 "FILE NOT FOUND"
    15 "NOT DIRECT COMMAND"
    16 "?SYNTAX ERROR"
    22 "?RETURN WITHOUT GOSUB ERROR"
    42 "?OUT OF DATA ERROR"
    53 "?ILLEGAL QUANTITY ERROR"
    69 "?OVERFLOW ERROR"
    90 "?UNDEF'D STATEMENT ERROR"
    100 "?ILLEGAL DIRECT ERROR"
    133 "?DIVISION BY ZERO ERROR"
    163 "?TYPE MISMATCH ERROR"
    176 "?STRING TOO LONG ERROR"
    200 "?LOAD WITHIN PROGRAM ERROR"
    201 "?SAVE WITHIN PROGRAM ERROR"
    cod)
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; seleccionar-destino-de-on: recibe una lista de numeros
; separados por comas, un indice y el ambiente, y retorna el
; numero a que hace referencia el indice (se cuenta desde 1)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn seleccionar-destino-de-on
  ([destinos indice amb]
    (cond
      (or (neg? indice) (> indice 255)) (do (dar-error 53 (amb 1)) nil)  ; Illegal quantity error
      (zero? indice) :omitir-restante  
      :else (seleccionar-destino-de-on (if (= (last destinos) (symbol ",")) (concat destinos [0]) destinos) indice amb 1)))
  ([destinos indice amb contador]
    (cond
      (nil? destinos) :omitir-restante
      (= contador indice) (if (= (first destinos) (symbol ",")) 0 (first destinos))
      (= (first destinos) (symbol ",")) (recur (next destinos) indice amb (inc contador))
      (or (= (count destinos) 1)
          (and (> (count destinos) 1) (= (second destinos) (symbol ",")))) (recur (nnext destinos) indice amb (inc contador))
      :else (do (dar-error 16 (amb 1)) nil)))  ; Syntax error
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; leer-data: recibe una lista de variables separadas por comas
; y un ambiente, y returna una dupla (un vector) con un
; resultado (usado luego por evaluar-linea) y un ambiente
; actualizado incluyendo las variables cargadas con los valores
; definidos en la(s) sentencia(s) DATA
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn leer-data
  ([param-de-read amb]
    (cond
      (= (first (amb 1)) :ejecucion-inmediata) (do (dar-error 15 (amb 1)) [nil amb])  ; Not direct command
      (empty? param-de-read) (do (dar-error 16 (amb 1)) [nil amb])  ; Syntax error
      :else (leer-data param-de-read (drop (amb 5) (amb 4)) amb)))
  ([variables entradas amb]
    (cond
      (empty? variables) [:sin-errores amb]
      (empty? entradas) (do (dar-error 42 (amb 1)) [:error-parcial amb])  ; Out of data error
      :else (let [res (ejecutar-asignacion (list (first variables) '= (if (variable-string? (first variables)) (str (first entradas)) (if (= (first entradas) "") 0 (first entradas)))) amb)]
                 (if (nil? res)
                     [nil amb]
                     (if (or (= (count (next variables)) 1)
                             (and (> (count (next variables)) 1) (not= (fnext variables) (symbol ","))))
                         (do (dar-error 16 (amb 1)) [:error-parcial res])  ; Syntax error
                         (recur (nnext variables) (next entradas) (assoc res 5 (inc (res 5)))))))))
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; leer-con-enter: recibe una lista con una cadena opcional
; seguida de variables separadas por comas y un ambiente, y
; retorna una dupla (un vector) con un resultado (usado luego
; por evaluar-linea) y un ambiente actualizado incluyendo las
; variables cargadas con los valores leidos del teclado
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn leer-con-enter
  ([param-de-input amb]
    (leer-con-enter param-de-input param-de-input amb))
  ([param-orig param-actualizados amb]
    (let [prim-arg (first param-actualizados), es-cadena (string? prim-arg)]
         (if (and es-cadena (not= (second param-actualizados) (symbol ";")))
             (do (dar-error 16 (amb 1)) [nil amb])  ; Syntax error
             (do (if es-cadena
                     (print prim-arg)
                     (print "? "))
                 (flush)
                 (if (= (first (amb 1)) :ejecucion-inmediata)
                     (do (dar-error 100 (amb 1)) [nil amb])  ; Illegal direct error
                     (let [variables (if es-cadena (nnext param-actualizados) param-actualizados),
                           valores (butlast (map clojure.string/trim (.split (apply str (.concat (read-line) ",.")) ","))),
                           entradas (map #(let [entr (try (clojure.edn/read-string %) (catch Exception e (str %)))] (if (number? entr) entr (clojure.string/upper-case (str %)))) valores)]
                           (if (empty? variables)
                               (do (dar-error 16 (amb 1)) [nil amb])  ; Syntax error
                               (leer-con-enter variables entradas param-orig param-actualizados amb amb))))))))
  ([variables entradas param-orig param-actualizados amb-orig amb-actualizado]
    (cond
      (and (empty? variables) (empty? entradas)) [:sin-errores amb-actualizado]
      (and (empty? variables) (not (empty? entradas))) (do (println "?EXTRA IGNORED") (flush) [:sin-errores amb-actualizado])
      (and (not (empty? variables)) (empty? entradas)) (leer-con-enter param-orig (concat (list "?? " (symbol ";")) variables) amb-actualizado)
      (and (not (variable-string? (first variables))) (string? (first entradas))) (do (println "?REENTER") (flush) (leer-con-enter param-orig param-orig amb-orig))
      :else (let [res (ejecutar-asignacion (list (first variables) '= (if (variable-string? (first variables)) (str (first entradas)) (first entradas))) amb-actualizado)]
                 (if (nil? res)
                     [nil amb-actualizado]
                     (if (or (= (count (next variables)) 1)
                             (and (> (count (next variables)) 1) (not= (fnext variables) (symbol ","))))
                         (do (dar-error 16 (amb-actualizado 1)) [:error-parcial res])  ; Syntax error
                         (recur (nnext variables) (next entradas) param-orig param-actualizados amb-orig res))))))
)
 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; retornar-al-for: implementa la sentencia NEXT, retornando una
; dupla (un vector) con un resultado (usado luego por
; evaluar-linea) y un ambiente actualizado con el nuevo valor
; de la variable de control
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn retornar-al-for [amb var-next]
  (if (empty? (amb 3))
      (do (dar-error 0 (amb 1)) [nil amb])  ; Next without for error
      (let [datos-for (peek (amb 3)),
            var-for (nth datos-for 0),
            valor-final (nth datos-for 1),
            valor-step (nth datos-for 2),
            origen (nth datos-for 3)]
           (if (and (some? var-next) (not= var-next var-for))
               (retornar-al-for (assoc amb 3 (pop (amb 3))) var-next)
               (let [var-actualizada (+ (calcular-expresion (list var-for) amb) valor-step), 
                     res (ejecutar-asignacion (list var-for '= var-actualizada) amb)]
                    (if (or (and (neg? valor-step) (>= var-actualizada valor-final))
                            (and (pos? valor-step) (<= var-actualizada valor-final)))
                        [:for-inconcluso (assoc res 1 [(origen 0) (dec (origen 1))])]
                        [:sin-errores (assoc res 3 (pop (amb 3)))])))))
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; continuar-programa: recibe un ambiente que fue modificado por
; GOTO o GOSUB y continua la ejecucion del programa a partir de
; ese ambiente
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn continuar-programa [amb]
  (ejecutar-programa amb (buscar-lineas-restantes amb))
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; ejecutar-programa: recibe un ambiente e inicia la ejecucion
; del programa a partir de ese ambiente 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn ejecutar-programa
  ([amb]
    (let [ini [(amb 0) (amb 1) [] [] (vec (extraer-data (amb 0))) 0 {}]]  ; [(prog-mem)  [prog-ptrs]  [gosub-return-stack]  [for-next-stack]  [data-mem]  data-ptr  {var-mem}]
         (ejecutar-programa ini (buscar-lineas-restantes ini))))
  ([amb prg]
    (if (or (nil? prg) (= (first (amb 1)) :ejecucion-inmediata))   
        [:sin-errores amb]
        (let [antes (assoc amb 1 [(ffirst prg) (second (amb 1))]), res (evaluar-linea (nfirst prg) antes), nuevo-amb (second res)]
             (cond (nil? (first res)) [nil amb]   ; hubo error total 
                   (= (first res) :error-parcial) [nil (second res)]   ; hubo error parcial
                   :else (let [proximo (if (and (= (first (antes 1)) (first (nuevo-amb 1))) (not= (first res) :for-inconcluso))
                                           (next prg)   ; no hubo quiebre de secuencia
                                           (buscar-lineas-restantes nuevo-amb)),
                               nueva-posic (if (nil? proximo) (nuevo-amb 1) [(ffirst proximo) (count (expandir-nexts (nfirst proximo)))])]
                              (recur (assoc nuevo-amb 1 nueva-posic) proximo))))))
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; mostrar-listado: recibe la representacion intermedia de un
; programa y lo lista usando la representacion normal
; (usualmente mas legible que la ingresada originalmente)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn mostrar-listado
  ([lineas]
    (if (empty? lineas)
        nil
        (mostrar-listado (next lineas) (first lineas))))
  ([lineas sentencias]
    (if (empty? sentencias)
        (do (prn) (mostrar-listado lineas))
        (mostrar-listado lineas (next sentencias) (first sentencias))))
  ([lineas sentencias elementos]
    (if (and (not (seq? elementos)) (integer? elementos))
        (do (pr elementos) (print "  ") (mostrar-listado lineas sentencias))
        (if (empty? elementos)
            (do (if (not (empty? sentencias)) (print ": "))
                (mostrar-listado lineas sentencias))
            (do (pr (first elementos))
                (if (not (or (contains? #{(symbol "(") (symbol ",") (symbol ";")} (first elementos))
                             (contains? #{(symbol ")") (symbol ",") (symbol ";")} (fnext elementos))
                             (nil? (fnext elementos)))) (print " "))
                (recur lineas sentencias (next elementos))))))
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; cargar-arch: recibe un nombre de archivo y retorna la
; representacion intermedia del codigo contenido en el
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn cargar-arch [nom nro-linea]
  (if (.exists (clojure.java.io/file nom))
      (remove empty? (with-open [rdr (clojure.java.io/reader nom)] (doall (map string-a-tokens (line-seq rdr)))))
      (dar-error 6 nro-linea))  ; File not found
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; grabar-arch: recibe un nombre de archivo, graba en el
; el listado del programa usando la representacion normal y
; retorna el ambiente
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn grabar-arch [nom amb]
  (let [arch (clojure.java.io/writer nom)]
       (do (binding [*out* arch] (mostrar-listado (amb 0)))
           (.close arch)
           amb))
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; calcular-expresion: recibe una expresion y un ambiente, y
; retorna el valor de la expresion, por ejemplo:
; user=> (calcular-expresion '(X + 5) ['((10 (PRINT X))) [10 1] [] [] [] 0 '{X 2}])
; 7
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn calcular-expresion [expr amb]
  (calcular-rpn (shunting-yard (desambiguar (preprocesar-expresion expr amb))) (amb 1))
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; desambiguar-mas-menos: recibe una expresion y la retorna sin
; los + unarios y con los - unarios reemplazados por -u  
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn desambiguar-mas-menos
  ([expr] (desambiguar-mas-menos expr nil []))
  ([expr ant res]
    (if (nil? expr)
        (remove nil? res)
        (let [act (first expr), nuevo (if (or (nil? ant) (and (symbol? ant) (operador? ant)) (= (str ant) "(") (= (str ant) ","))
                                          (case act
                                             + nil
                                             - '-u
                                             act)
                                          act)] 
             (recur (next expr) act (conj res nuevo)))))
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; desambiguar-mid: recibe una expresion y la retorna con los
; MID$ ternarios reemplazados por MID3$ 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn desambiguar-mid
  ([expr]
    (cond
      (contains? (set expr) 'MID$) (desambiguar-mid expr 0 (count expr) 0 0 0 true)
      (contains? (set expr) 'MID2$) (apply list (replace '{MID2$ MID$} expr))
      :else (apply list expr)))
  ([expr act fin pos cont-paren cont-comas buscando]
    (if (= act fin)
        (desambiguar-mid expr)
        (let [nuevo (nth expr act)]
             (cond
               (and (= nuevo 'MID$) buscando) (recur expr (inc act) fin act cont-paren cont-comas false)
               (and (= nuevo (symbol "(")) (not buscando)) (recur expr (inc act) fin pos (inc cont-paren) cont-comas buscando)
               (and (= nuevo (symbol ")")) (not buscando))
                                      (if (= cont-paren 1)
                                          (if (= cont-comas 2)
                                              (recur (assoc (vec expr) pos 'MID3$) (inc act) fin 0 0 0 true)
                                              (recur (assoc (vec expr) pos 'MID2$) (inc act) fin 0 0 0 true))
                                          (recur expr (inc act) fin pos (dec cont-paren) cont-comas buscando))
               (and (= nuevo (symbol ",")) (= cont-paren 1)) (recur expr (inc act) fin pos cont-paren (inc cont-comas) buscando)
               :else (recur expr (inc act) fin pos cont-paren cont-comas buscando)))))
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; shunting-yard: implementa el algoritmo del Patio de Maniobras
; de Dijkstra que convierte una expresion a RPN (Reverse Polish
; Notation), por ejemplo:
; user=> (shunting-yard '(1 + 2))
; (1 2 +)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn shunting-yard [tokens]
  (remove #(= % (symbol ","))
    (flatten
      (reduce
        (fn [[rpn pila] token]
          (let [op-mas? #(and (some? (precedencia %)) (>= (precedencia %) (precedencia token)))
                no-abre-paren? #(not= (str %) "(")]
            (cond
              (= (str token) "(") [rpn (cons token pila)]
              (= (str token) ")") [(vec (concat rpn (take-while no-abre-paren? pila))) (rest (drop-while no-abre-paren? pila))]
              (some? (precedencia token)) [(vec (concat rpn (take-while op-mas? pila))) (cons token (drop-while op-mas? pila))]
              :else [(conj rpn token) pila])))
        [[] ()]
        tokens)))
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; calcular-rpn: Recibe una expresion en RPN y un numero de linea
; y retorna el valor de la expresion o un mensaje de error en la
; linea indicada
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn calcular-rpn [tokens nro-linea]
  (try
    (let [resu-redu
         (reduce
           (fn [pila token]
               (let [ari (aridad token),
                     resu (eliminar-cero-decimal 
                            (case ari
                              1 (aplicar token (first pila) nro-linea)
                              2 (aplicar token (second pila) (first pila) nro-linea)
                              3 (aplicar token (nth pila 2) (nth pila 1) (nth pila 0) nro-linea)
                              token))]
                    (if (nil? resu)
                        (reduced resu)
                        (cons resu (drop ari pila)))))
           [] tokens)]
           (if (> (count resu-redu) 1)
               (dar-error 16 nro-linea)  ; Syntax error
               (first resu-redu)))
    (catch NumberFormatException e 0)
    (catch ClassCastException e (dar-error 163 nro-linea)) ; Type mismatch error
    (catch UnsupportedOperationException e (dar-error 163 nro-linea)) ; Type mismatch error
    (catch IllegalArgumentException e (dar-error 69 nro-linea))  ; Overflow error
    (catch Exception e (dar-error 16 nro-linea)))  ; Syntax error
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; imprimir: recibe una lista de expresiones (separadas o no
; mediante puntos y comas o comas) y un ambiente, y las muestra
; interpretando los separadores como tabulaciones (las comas) o
; concatenaciones (los puntos y comas). Salvo cuando la lista
; termina en punto y coma, imprime un salto de linea al terminar
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn imprimir
  ([v]
     (let [expresiones (v 0), amb (v 1)]
          (cond
            (empty? expresiones) (do (prn) (flush) :sin-errores)
            (and (empty? (next expresiones)) (= (first expresiones) (list (symbol ";")))) (do (pr) (flush) :sin-errores)
            (and (empty? (next expresiones)) (= (first expresiones) (list (symbol ",t")))) (do (printf "\t\t") (flush) :sin-errores)
            (= (first expresiones) (list (symbol ";"))) (do (pr) (flush) (recur [(next expresiones) amb]))
            (= (first expresiones) (list (symbol ",t"))) (do (printf "\t\t") (flush) (recur [(next expresiones) amb]))
            :else (let [resu (eliminar-cero-entero (calcular-expresion (first expresiones) amb))]
                        (if (nil? resu)
                            resu
                            (do (print resu) (flush) (recur [(next expresiones) amb])))))))
  ([lista-expr amb]
    (let [nueva (cons (conj [] (first lista-expr)) (rest lista-expr)),
          variable? #(or (variable-integer? %) (variable-float? %) (variable-string? %)),
          funcion? #(and (> (aridad %) 0) (not (operador? %))),		  
          interc (reduce #(if (and (or (number? (last %1)) (string? (last %1)) (variable? (last %1)) (= (symbol ")") (last %1)))
                                   (or (number? %2) (string? %2) (variable? %2) (funcion? %2) (= (symbol "(") %2)))
                         (conj (conj %1 (symbol ";")) %2) (conj %1 %2)) nueva),
          ex (partition-by #(= % (symbol ",t")) (desambiguar-comas interc)),
          expresiones (apply concat (map #(partition-by (fn [x] (= x (symbol ";"))) %) ex))]
         (imprimir [expresiones amb])))
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; desambiguar-comas: recibe una expresion en forma de lista y
; la devuelve con las comas que esten afuera de los pares de
; parentesis remplazadas por el simbolo ,t (las demas, que se
; usan para separar argumentos, se mantienen intactas)  
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn desambiguar-comas
  ([lista-expr]
    (desambiguar-comas lista-expr 0 []))
  ([lista-expr cont-paren res]
    (if (nil? lista-expr)
        res
        (let [act (first lista-expr),
              paren (cond
                       (= act (symbol "(")) (inc cont-paren)
                       (= act (symbol ")")) (dec cont-paren)
                       :else cont-paren),
              nuevo (if (and (= act (symbol ",")) (zero? paren)) (symbol ",t") act)] 
             (recur (next lista-expr) paren (conj res nuevo)))))
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; A PARTIR DE ESTE PUNTO HAY QUE COMPLETAR LAS FUNCIONES DADAS ;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; evaluar: ejecuta una sentencia y retorna una dupla (un vector)
; con un resultado (usado luego por evaluar-linea) y un ambiente
; actualizado
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn evaluar [sentencia amb] 
    (if (or (contains? (set sentencia) nil) (and (palabra-reservada? (first sentencia)) (= (second sentencia) '=)))
      (do (dar-error 16 (amb 1)) [nil amb])  ; Syntax error  
      (case (first sentencia)
        PRINT (let [args (next sentencia), resu (imprimir args amb)]
                   (if (and (nil? resu) (some? args))
                       [nil amb]
                       [:sin-errores amb]))
        LOAD (if (= (first (amb 1)) :ejecucion-inmediata)
                 (let [nuevo-amb (cargar-arch (apply str (next sentencia)) (amb 1))]
                      (if (nil? nuevo-amb)
                          [nil amb]
                          [:sin-errores [nuevo-amb [:ejecucion-inmediata 0] [] [] [] 0 {}]]))  ; [(prog-mem)  [prog-ptrs]  [gosub-return-stack]  [for-next-stack]  [data-mem]  data-ptr  {var-mem}]
                 (do (dar-error 200 (amb 1)) [nil amb]))  ; Load within program error
        SAVE (if (= (first (amb 1)) :ejecucion-inmediata)
                 (let [resu (grabar-arch (apply str (next sentencia)) amb)]
                      (if (nil? resu)
                          [nil amb]
                          [:sin-errores amb]))
                 (do (dar-error 201 (amb 1)) [nil amb]))  ; Save within program error
        DATA [:sin-errores amb];(leer-data (rest sentencia) amb)
        READ (let [asignacion (list (second sentencia) '= (str ((amb 4) (amb 5)))),
                    nuevo-amb (ejecutar-asignacion asignacion (update amb 5 #(+ 1 %)))] 
                [:sin-errores nuevo-amb])
        RESTORE [:sin-errores (assoc amb 5 0)]
        REM [:omitir-restante amb]
        CLEAR [:sin-errores (assoc amb 6 (hash-map))]
        LET (let [resu (ejecutar-asignacion (rest sentencia) amb)]
                 (if (nil? resu)
                     [nil amb]
                     [:sin-errores resu]))
        LIST (do (mostrar-listado (amb 0)) [:sin-errores amb])
        NEW [:sin-errores ['() [:ejecucion-inmediata 0] [] [] [] 0 {}]]  ; [(prog-mem)  [prog-ptrs]  [gosub-return-stack]  [for-next-stack]  [data-mem]  data-ptr  {var-mem}]
        RUN (cond
              (empty? (amb 0)) [:sin-errores amb]  ; no hay programa
              (= (count sentencia) 1) (ejecutar-programa (assoc amb 1 [(ffirst (amb 0)) (count (expandir-nexts (nfirst (amb 0))))]))  ; no hay argumentos   
              (= (count (next sentencia)) 1) (ejecutar-programa (assoc amb 1 [(fnext sentencia) (contar-sentencias (fnext sentencia) amb)]))  ; hay solo un argumento
              :else (do (dar-error 16 (amb 1)) [nil amb]))  ; Syntax error
        END (let [nuevo-prog-ptr [(first (last (amb 0))) 0]]
             [:omitir-restante (assoc amb 1 nuevo-prog-ptr)])
        GOTO (let [num-linea (if (some? (second sentencia)) (second sentencia) 0)]
                  (if (not (contains? (into (hash-set) (map first (amb 0))) num-linea))
                      (do (dar-error 90 (amb 1)) [nil amb])  ; Undef'd statement error
                      (let [nuevo-amb (assoc amb 1 [num-linea (contar-sentencias num-linea amb)])]
                           (if (= (first (amb 1)) :ejecucion-inmediata)
                               (continuar-programa nuevo-amb)
                               [:omitir-restante nuevo-amb]))))
        IF (let [separados (split-with #(not (contains? #{"THEN" "GOTO"} (str %))) (next sentencia)),
                 condicion-de-if (first separados),
                 resto-if (second separados),
                 sentencia-de-if (cond
                                   (= (first resto-if) 'GOTO) resto-if
                                   (= (first resto-if) 'THEN) (if (number? (second resto-if))
                                                                  (cons 'GOTO (next resto-if))
                                                                  (next resto-if))
                                   :else (do (dar-error 16 (amb 1)) nil)),  ; Syntax error
                 resu (calcular-expresion condicion-de-if amb)]
                (if (zero? resu)
                    [:omitir-restante amb]
                    (recur sentencia-de-if amb))) 
        INPUT (leer-con-enter (next sentencia) amb)
        ON (let [separados (split-with #(not (contains? #{"GOTO" "GOSUB"} (str %))) (next sentencia)),
                 indice-de-on (calcular-expresion (first separados) amb),
                 sentencia-de-on (first (second separados)),
                 destino-de-on (seleccionar-destino-de-on (next (second separados)) indice-de-on amb)]
                (cond
                  (nil? destino-de-on) [nil amb]
                  (= destino-de-on :omitir-restante) [:sin-errores amb]
                  :else (recur (list sentencia-de-on destino-de-on) amb)))
        GOSUB (let [num-linea (if (some? (second sentencia)) (second sentencia) 0)]
                   (if (not (contains? (into (hash-set) (map first (amb 0))) num-linea))
                       (do (dar-error 90 (amb 1)) [nil amb])  ; Undef'd statement error
                       (let [pos-actual (amb 1),
                             nuevo-amb (assoc (assoc amb 1 [num-linea (contar-sentencias num-linea amb)]) 2 (conj (amb 2) pos-actual))]
                            (if (= (first (amb 1)) :ejecucion-inmediata)
                                (continuar-programa nuevo-amb)
                                [:omitir-restante nuevo-amb]))))
        RETURN (continuar-linea amb)
        FOR (let [separados (partition-by #(contains? #{"TO" "STEP"} (str %)) (next sentencia))]
                 (if (not (or (and (= (count separados) 3) (variable-float? (ffirst separados)) (= (nth separados 1) '(TO)))
                              (and (= (count separados) 5) (variable-float? (ffirst separados)) (= (nth separados 1) '(TO)) (= (nth separados 3) '(STEP)))))
                     (do (dar-error 16 (amb 1)) [nil amb])  ; Syntax error
                     (let [valor-final (calcular-expresion (nth separados 2) amb),
                           valor-step (if (= (count separados) 5) (calcular-expresion (nth separados 4) amb) 1)]
                          (if (or (nil? valor-final) (nil? valor-step))
                              [nil amb]
                              (recur (first separados) (assoc amb 3 (conj (amb 3) [(ffirst separados) valor-final valor-step (amb 1)])))))))
        NEXT (if (<= (count (next sentencia)) 1)
                 (retornar-al-for amb (fnext sentencia))
                  (do (dar-error 16 (amb 1)) [nil amb]))  ; Syntax error
        (if (= (second sentencia) '=)
            (let [resu (ejecutar-asignacion sentencia amb)]
                 (if (nil? resu)
                     [nil amb]
                     [:sin-errores resu]))
            (do (dar-error 16 (amb 1)) [nil amb]))))  ; Syntax error
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; aplicar: aplica un operador a sus operandos y retorna el valor
; resultante (si ocurre un error, muestra un mensaje y retorna
; nil)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(declare stoc)

(defn aplicar
  ([operador operando nro-linea]
    (if (nil? operando)
        (dar-error 16 nro-linea)  ; Syntax error
        (case operador
          -u (- operando)
          LEN (count operando)
          ASC (int (stoc operando))
          STR$ (if (not (number? operando)) (dar-error 163 nro-linea) (eliminar-cero-entero operando)) ; Type mismatch error
          CHR$ (if (or (< operando 0) (> operando 255)) (dar-error 53 nro-linea) (str (char operando))) ; Illegal quantity error
          INT (int operando)
          ATN (Math/atan operando)
          SIN (Math/sin operando))))
  ([operador operando1 operando2 nro-linea]
    (if (or (nil? operando1) (nil? operando2))
        (dar-error 16 nro-linea)  ; Syntax error
        (if (= operador (symbol "^"))
            (Math/pow operando1 operando2)
        (case operador
          = (if (and (string? operando1) (string? operando2))
                (if (= operando1 operando2) 1 0)
                (if (= (+ 0 operando1) (+ 0 operando2)) 1 0))
          <> (if (and (string? operando1) (string? operando2))
                (if (not= operando1 operando2) 1 0)
                (if (not= (+ 0 operando1) (+ 0 operando2)) 1 0))
          < (if (and (string? operando1) (string? operando2))
                (if (< (int (stoc operando1)) (int (stoc operando2))) 1 0)
                (if (< (+ 0 operando1) (+ 0 operando2)) 1 0))
          <= (if (and (string? operando1) (string? operando2))
                (if (<= (int (stoc operando1)) (int (stoc operando2))) 1 0)
                (if (<= (+ 0 operando1) (+ 0 operando2)) 1 0))
          > (if (and (string? operando1) (string? operando2))
                (if (> (int (stoc operando1)) (int (stoc operando2))) 1 0)
                (if (> (+ 0 operando1) (+ 0 operando2)) 1 0))
          >= (if (and (string? operando1) (string? operando2))
                (if (>= (int (stoc operando1)) (int (stoc operando2))) 1 0)
                (if (>= (+ 0 operando1) (+ 0 operando2)) 1 0))
          + (if (and (string? operando1) (string? operando2))
                (str operando1 operando2)
                (+ operando1 operando2))
          - (- operando1 operando2)
          * (* operando1 operando2)
          / (if (= operando2 0) (dar-error 133 nro-linea) (/ operando1 operando2))  ; Division by zero error
          AND (let [op1 (+ 0 operando1), op2 (+ 0 operando2)] (if (and (not= op1 0) (not= op2 0)) 1 0))
          OR (let [op1 (+ 0 operando1), op2 (+ 0 operando2)] (if (or (not= op1 0) (not= op2 0)) 1 0))
          MID$ (if (< operando2 1)
                   (dar-error 53 nro-linea)  ; Illegal quantity error
                   (let [ini (dec operando2)] (if (>= ini (count operando1)) "" (subs operando1 ini))))))))
  ([operador operando1 operando2 operando3 nro-linea]
    (if (or (nil? operando1) (nil? operando2) (nil? operando3)) (dar-error 16 nro-linea)  ; Syntax error
        (case operador
          MID3$ (let [tam (count operando1), ini (dec operando2), fin (+ (dec operando2) operando3)]
                     (cond
                       (or (< operando2 1) (< operando3 0)) (dar-error 53 nro-linea)  ; Illegal quantity error
                       (>= ini tam) ""
                       (>= fin tam) (subs operando1 ini tam)
                       :else (subs operando1 ini fin))))))
)

;Symbol to char
(defn stoc [x]
  ((vec (str x)) 0))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; A PARTIR DE ESTE PUNTO HAY QUE IMPLEMENTAR LAS FUNCIONES DADAS ;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; palabra-reservada?: predicado para determinar si un
; identificador es una palabra reservada, por ejemplo:
; user=> (palabra-reservada? 'REM)
; true
; user=> (palabra-reservada? 'SPACE)
; false
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn palabra-reservada? [x]
  (let [palabras '("ABS", "AND", "ASC", "AT", "ATN", "CALL", "CHR$", "CLEAR", "COLOR=", "CONT", "COS", "DATA", "DEF", "DEL", "DIM", "DRAW", "END", "EXP", "FLASH", "FN", "FOR", "FRE", "GET", "GOSUB", "GOTO", "GR", "HCOLOR=", "HGR", "HGR2", "HIMEM:", "HLIN", "HOME", "HPLOT", "HTAB", "IF", "IN#", "INPUT", "INT", "INVERSE", "LEFT$", "LEN", "LET", "LIST", "LOAD", "LOG", "LOMEM:", "MID$", "NEW", "NEXT", "NORMAL", "NOT", "NOTRACE", "ON", "ONERR", "OR", "PDL", "PEEK", "PLOT", "POKE", "POP", "POS", "PRINT", "PR#", "READ", "RECALL", "REM", "RESTORE", "RESUME", "RETURN", "RIGHT$", "RND", "ROT=", "RUN", "SAVE", "SCALE=", "SCRN", "SGN", "SHLOAD", "SIN", "SPC", "SPEED=", "SQR", "STEP", "STOP", "STORE", "STR$", "TAB", "TAN", "TEXT", "THEN", "TO", "TRACE", "USR", "VAL", "VLIN", "VTAB", "WAIT", "XDRAW", "&")]
    (.contains palabras (str x))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; operador?: predicado para determinar si un identificador es un
; operador, por ejemplo:
; user=> (operador? '+)
; true
; user=> (operador? (symbol "+"))
; true
; user=> (operador? (symbol "%"))
; false
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn operador? [x]
  (let [operadores '("+", "-", "*", "/", "^", "=", "<>", "<", "<=", ">", ">=", "AND", "OR")]
  (.contains operadores (str x))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; anular-invalidos: recibe una lista de simbolos y la retorna con
; aquellos que son invalidos reemplazados por nil, por ejemplo:
; user=> (anular-invalidos '(IF X & * Y < 12 THEN LET ! X = 0))
; (IF X nil * Y < 12 THEN LET nil X = 0)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn anular-invalidos [sentencia]
  (let [simbolos-invalidos #"\!|\"|\#|\$|\&|\'|\:|\{|\}|\[|\]|\_|\|\~|\%"]
  (map #(if (some? (re-matches simbolos-invalidos (str %))) nil %) sentencia)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; cargar-linea: recibe una linea de codigo y un ambiente y retorna
; el ambiente actualizado, por ejemplo:
; user=> (cargar-linea '(10 (PRINT X)) [() [:ejecucion-inmediata 0] [] [] [] 0 {}])
; [((10 (PRINT X))) [:ejecucion-inmediata 0] [] [] [] 0 {}]
; user=> (cargar-linea '(20 (X = 100)) ['((10 (PRINT X))) [:ejecucion-inmediata 0] [] [] [] 0 {}])
; [((10 (PRINT X)) (20 (X = 100))) [:ejecucion-inmediata 0] [] [] [] 0 {}]
; user=> (cargar-linea '(15 (X = X + 1)) ['((10 (PRINT X)) (20 (X = 100))) [:ejecucion-inmediata 0] [] [] [] 0 {}])
; [((10 (PRINT X)) (15 (X = X + 1)) (20 (X = 100))) [:ejecucion-inmediata 0] [] [] [] 0 {}]
; user=> (cargar-linea '(15 (X = X - 1)) ['((10 (PRINT X)) (15 (X = X + 1)) (20 (X = 100))) [:ejecucion-inmediata 0] [] [] [] 0 {}])
; [((10 (PRINT X)) (15 (X = X - 1)) (20 (X = 100))) [:ejecucion-inmediata 0] [] [] [] 0 {}]
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(declare menor-numero)
(declare eliminar-linea)
(declare misma-linea)
(declare insertar-prog)
(declare linea-repetida?)

(defn cargar-linea [linea amb]
  (apply vector (conj (drop 1 amb) (insertar-prog (first amb) linea))))

(defn insertar-prog [prog linea]
 (if (linea-repetida? prog (first linea))
  (insertar-prog (eliminar-linea prog (first linea)) linea)
  (apply list (sort menor-numero (conj (vec prog) linea)))))

(defn linea-repetida? [prog n]
  (not (not-any? (partial (fn [a b] (= a b)) n) (apply vector (map first prog)))))

(defn eliminar-linea [prog n]
  (remove (partial misma-linea n) prog))

(defn misma-linea [n linea]
  (= (first linea) n))

(defn menor-numero [l1 l2]
  (< (first l1) (first l2)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; expandir-nexts: recibe una lista de sentencias y la devuelve con
; las sentencias NEXT compuestas expresadas como sentencias NEXT
; simples, por ejemplo:
; user=> (def n (list '(PRINT 1) (list 'NEXT 'A (symbol ",") 'B)))
; #'user/n
; user=> n
; ((PRINT 1) (NEXT A , B))
; user=> (expandir-nexts n)
; ((PRINT 1) (NEXT A) (NEXT B))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(declare tiene-id?)
(declare expandir)
(declare agregar-acumulado)
(declare conj-r)

(defn expandir-nexts [n]
  (if (and (not (empty? n)) (number? (first n)))
    (expandir (list (first n)) (rest n))
  (expandir '() n)))

(defn expandir [acumulado resto]
  (if (not (empty? resto))
    (agregar-acumulado acumulado (first resto) (rest resto))
  acumulado))

(defn agregar-acumulado [acumulado primero resto]
  (if (and (= 'NEXT (first primero)) (tiene-id? (symbol ",") primero))
    (let [nexts (map (partial conj-r 'NEXT) (rest (map list (remove #(= (symbol ",") %) primero))))
          nuevo-acum (concat acumulado (list (first nexts))),
          nuevo-resto (concat (rest nexts) resto)]
      (expandir nuevo-acum nuevo-resto))
  (expandir (concat acumulado (list primero)) resto)))

(defn conj-r [e l]
  (conj l e))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; dar-error: recibe un error (codigo o mensaje) y el puntero de 
; programa, muestra el error correspondiente y retorna nil, por
; ejemplo:
; user=> (dar-error 16 [:ejecucion-inmediata 4])
;
; ?SYNTAX ERRORnil
; user=> (dar-error "?ERROR DISK FULL" [:ejecucion-inmediata 4])
;
; ?ERROR DISK FULLnil
; user=> (dar-error 16 [100 3])
;
; ?SYNTAX ERROR IN 100nil
; user=> (dar-error "?ERROR DISK FULL" [100 3])
;
; ?ERROR DISK FULL IN 100nil
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn dar-error [cod prog-ptrs]
  (if (= :ejecucion-inmediata (prog-ptrs 0))
    (print (buscar-mensaje cod))
  (print (str (buscar-mensaje cod) " IN " (str (prog-ptrs 0)))))
  nil)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; variable-float?: predicado para determinar si un identificador
; es una variable de punto flotante, por ejemplo:
; user=> (variable-float? 'X)
; true
; user=> (variable-float? 'X%)
; false
; user=> (variable-float? 'X$)
; false
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn variable-float? [x]
  (and (some? (re-matches #"[A-Z]\w*" (str x))) (not (palabra-reservada? x)) (not (string? x)))) 

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; variable-integer?: predicado para determinar si un identificador
; es una variable entera, por ejemplo:
; user=> (variable-integer? 'X%)
; true
; user=> (variable-integer? 'X)
; false
; user=> (variable-integer? 'X$)
; false
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn variable-integer? [x]
  (and (some? (re-matches #"[A-Z]\w*[%]" (str x))) (not (palabra-reservada? x)) (not (string? x))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; variable-string?: predicado para determinar si un identificador
; es una variable de cadena, por ejemplo:
; user=> (variable-string? 'X$)
; true
; user=> (variable-string? 'X)
; false
; user=> (variable-string? 'X%)
; false
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn variable-string? [x]
  (and (some? (re-matches #"[A-Z]\w*[$]" (str x))) (not (palabra-reservada? x)) (not (string? x))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; contar-sentencias: recibe un numero de linea y un ambiente y
; retorna la cantidad de sentencias que hay en la linea indicada,
; por ejemplo:
; user=> (contar-sentencias 10 [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [10 1] [] [] [] 0 {}])
; 2
; user=> (contar-sentencias 15 [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [10 1] [] [] [] 0 {}])
; 1
; user=> (contar-sentencias 20 [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [10 1] [] [] [] 0 {}])
; 2
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(declare buscar-sentencia)
(declare indice-linea)

(defn contar-sentencias [nro-linea amb]
  (let [sentencia (buscar-sentencia nro-linea (amb 0))]
    (+ -1 (count (expandir-nexts sentencia)))))

(defn buscar-sentencia [nro-linea prg]
  ((vec prg) (indice-linea nro-linea prg)))

(defn indice-linea [nro-linea prg]
  (count (take-while (complement #(= nro-linea (first %))) prg)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; buscar-lineas-restantes: recibe un ambiente y retorna la
; representacion intermedia del programa a partir del puntero de
; programa (que indica la linea y cuantas sentencias de la misma
; aun quedan por ejecutar), por ejemplo:
; user=> (buscar-lineas-restantes [() [:ejecucion-inmediata 0] [] [] [] 0 {}])
; nil
; user=> (buscar-lineas-restantes ['((PRINT X) (PRINT Y)) [:ejecucion-inmediata 2] [] [] [] 0 {}])
; nil
; user=> (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [10 2] [] [] [] 0 {}])
; ((10 (PRINT X) (PRINT Y)) (15 (X = X + 1)) (20 (NEXT I , J)))
; user=> (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [10 1] [] [] [] 0 {}])
; ((10 (PRINT Y)) (15 (X = X + 1)) (20 (NEXT I , J)))
; user=> (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [10 0] [] [] [] 0 {}])
; ((10) (15 (X = X + 1)) (20 (NEXT I , J)))
; user=> (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [15 1] [] [] [] 0 {}])
; ((15 (X = X + 1)) (20 (NEXT I , J)))
; user=> (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [15 0] [] [] [] 0 {}])
; ((15) (20 (NEXT I , J)))
; user=> (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 3] [] [] [] 0 {}])
; ((20 (NEXT I) (NEXT J)))
; user=> (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 2] [] [] [] 0 {}])
; ((20 (NEXT I) (NEXT J)))
; user=> (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 1] [] [] [] 0 {}])
; ((20 (NEXT J)))
; user=> (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 0] [] [] [] 0 {}])
; ((20))
; user=> (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 -1] [] [] [] 0 {}])
; ((20))
; user=> (buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [25 0] [] [] [] 0 {}])
; nil
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(declare tiene-linea-nro?)

(defn buscar-lineas-restantes
  ([amb] (buscar-lineas-restantes (amb 1) (amb 0)))
  ([act prg]
    (if (or (= :ejecucion-inmediata (act 0)) 
             (not (tiene-linea-nro? (act 0) (map first prg)))) 
      nil
    (let [sin-lineas-viejas (remove #(< (first %) (act 0)) prg),
          sentencias (->> sin-lineas-viejas
                     (first)
                     (rest)
                     (expandir-nexts)),
          sentencias-actual (drop (- (count sentencias) (act 1)) sentencias),
          linea (conj sentencias-actual (act 0)),
          restantes (rest sin-lineas-viejas)]
      (concat (list linea) restantes)))))

(defn tiene-linea-nro? [nro nro-lineas]
  (not (not-any? #(= nro %) nro-lineas)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; continuar-linea: implementa la sentencia RETURN, retornando una
; dupla (un vector) con un resultado (usado luego por
; evaluar-linea) y un ambiente actualizado con el nuevo valor del
; puntero de programa, por ejemplo:
; user=> (continuar-linea [(list '(10 (PRINT X)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 3] [] [] [] 0 {}])
; 
; ?RETURN WITHOUT GOSUB ERROR IN 20[nil [((10 (PRINT X)) (15 (X = X + 1)) (20 (NEXT I , J))) [20 3] [] [] [] 0 {}]]
; user=> (continuar-linea [(list '(10 (PRINT X)) '(15 (GOSUB 100) (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 3] [[15 2]] [] [] 0 {}])
; [:omitir-restante [((10 (PRINT X)) (15 (GOSUB 100) (X = X + 1)) (20 (NEXT I , J))) [15 1] [] [] [] 0 {}]]
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn continuar-linea [amb]
  (if (empty? (amb 2))
    [(dar-error 22 (amb 1)) amb]
    (let [nuevo-prog-ptr (update (peek (amb 2)) 1 #(- % 1)),
          nuevo-stack (vec (drop-last 1 (amb 2))),
          nuevo-amb (assoc (assoc amb 2 nuevo-stack) 1 nuevo-prog-ptr)]
  [:omitir-restante nuevo-amb])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; extraer-data: recibe la representación intermedia de un programa
; y retorna una lista con todos los valores embebidos en las
; sentencias DATA, por ejemplo:
; user=> (extraer-data '(()))
; ()
; user=> (extraer-data (list '(10 (PRINT X) (REM ESTE NO) (DATA 30)) '(20 (DATA HOLA)) (list 100 (list 'DATA 'MUNDO (symbol ",") 10 (symbol ",") 20))))
; ("HOLA" "MUNDO" 10 20)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(declare eliminar-rem)
(declare borrar-rem)
(declare tiene-id?)
(declare indice-rem)
(declare obtener-lineas-data)

(declare eliminar-numero-linea)

(declare obtener-data)


(defn extraer-data [prg]
  (->> prg
       (eliminar-rem)
       (obtener-lineas-data)
       (map obtener-lineas-data)
       (flatten)
       (remove #(or ( = 'DATA %) (= (symbol ",") %)))
       (map #(if (number? %) % (str %))))) 

(defn eliminar-rem [prg]
  (map borrar-rem prg))

(defn tiene-id? [id linea]
  (not (not-any? #(= id %) (flatten linea))))

(defn borrar-rem [linea]
  (if (tiene-id? 'REM linea)
    (let [indice (indice-rem linea)]
      (take indice linea))
  linea))

(defn indice-rem [linea]
  (count (take-while (complement (partial tiene-id? 'REM)) linea)))

(defn obtener-lineas-data [prg]
  (remove (complement (partial tiene-id? 'DATA)) (vec prg)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; ejecutar-asignacion: recibe una asignacion y un ambiente, y
; retorna el ambiente actualizado al efectuar la asignacion, por
; ejemplo:
; user=> (ejecutar-asignacion '(X = 5) ['((10 (PRINT X))) [10 1] [] [] [] 0 {}])
; [((10 (PRINT X))) [10 1] [] [] [] 0 {X 5}]
; user=> (ejecutar-asignacion '(X = 5) ['((10 (PRINT X))) [10 1] [] [] [] 0 '{X 2}])
; [((10 (PRINT X))) [10 1] [] [] [] 0 {X 5}]
; user=> (ejecutar-asignacion '(X = X + 1) ['((10 (PRINT X))) [10 1] [] [] [] 0 '{X 2}])
; [((10 (PRINT X))) [10 1] [] [] [] 0 {X 3}]
; user=> (ejecutar-asignacion '(X$ = X$ + " MUNDO") ['((10 (PRINT X))) [10 1] [] [] [] 0 '{X$ "HOLA"}])
; [((10 (PRINT X))) [10 1] [] [] [] 0 {X$ "HOLA MUNDO"}]
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn ejecutar-asignacion [sentencia amb]
  (let [nuevo-vec (assoc (amb 6) (first sentencia) (calcular-expresion (drop 2 sentencia) amb))] 
      (assoc amb 6 nuevo-vec)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; preprocesar-expresion: recibe una expresion y la retorna con
; las variables reemplazadas por sus valores y el punto por el
; cero, por ejemplo:
; user=> (preprocesar-expresion '(X$ + " MUNDO" + Z$) ['((10 (PRINT X))) [10 1] [] [] [] 0 '{X$ "HOLA"}])
; ("HOLA" + " MUNDO" + "")
; user=> (preprocesar-expresion '(X + . / Y% * Z) ['((10 (PRINT X))) [10 1] [] [] [] 0 '{X 5 Y% 2}])
; (5 + 0 / 2 * 0)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn preprocesar-expresion [expr amb]
  (->> expr
       (replace (amb 6))
       (map #(if (= (symbol ".") %) 0 %))
       (map #(if (variable-string? %) "" %))
       (map #(if (variable-float? %) 0 %))
       (map #(if (variable-integer? %) 0 %))))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; desambiguar: recibe un expresion y la retorna sin los + unarios,
; con los - unarios reemplazados por -u y los MID$ ternarios
; reemplazados por MID3$, por ejemplo: 
; user=> (desambiguar (list '- 2 '* (symbol "(") '- 3 '+ 5 '- (symbol "(") '+ 2 '/ 7 (symbol ")") (symbol ")")))
; (-u 2 * ( -u 3 + 5 - ( 2 / 7 ) ))
; user=> (desambiguar (list 'MID$ (symbol "(") 1 (symbol ",") 2 (symbol ")")))
; (MID$ ( 1 , 2 ))
; user=> (desambiguar (list 'MID$ (symbol "(") 1 (symbol ",") 2 (symbol ",") 3 (symbol ")")))
; (MID3$ ( 1 , 2 , 3 ))
; user=> (desambiguar (list 'MID$ (symbol "(") 1 (symbol ",") '- 2 '+ 'K (symbol ",") 3 (symbol ")")))
; (MID3$ ( 1 , -u 2 + K , 3 ))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(declare desambiguar-mid-ternario)
(declare tiene-3-parametros?)
(declare eliminar-mas-unario)
(declare remplazar-menos-unario)

(defn desambiguar [expr]
  ((comp desambiguar-mas-menos desambiguar-mid) expr))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; precedencia: recibe un token y retorna el valor de su
; precedencia, por ejemplo:
; user=> (precedencia 'OR)
; 1
; user=> (precedencia 'AND)
; 2
; user=> (precedencia '*)
; 6
; user=> (precedencia '-u)
; 7
; user=> (precedencia 'MID$)
; 9
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn precedencia [token] 
  (let [tokens (hash-map 'ATN 23, 'INT 22, 'SIN 21, 'LEN 20, 'MID$ 19, 'MID3$ 18, 'ASC 17, 'CHR$ 16, 'STR$ 15,  (symbol "-u") 14, '* 13, '/ 12, (symbol "^") 11, '+ 9, '- 10, '= 8, '<> 7, '< 6, '<= 5, '> 4, '>= 3,'AND 2, 'OR 1, (symbol ",") 0)] 
    (if (.contains (keys tokens) token)
      (tokens token)
      nil)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; aridad: recibe un token y retorna el valor de su aridad, por
; ejemplo:
; user=> (aridad 'THEN)
; 0
; user=> (aridad 'SIN)
; 1
; user=> (aridad '*)
; 2
; user=> (aridad 'MID$)
; 2
; user=> (aridad 'MID3$)
; 3
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn aridad [token]
  (let [tokens (hash-map 'INPUT 0, 'PRINT 1, 'DATA 1, 'READ 1, 'REM 1, 'RESTORE 0, 'CLEAR 0, 'LET 2, 'LIST 0, 'NEW 0, 'END 0, 'FOR 1, 'TO 1, 'NEXT 1, 'STEP 1, 'GOSUB 1, 'RETURN 1, 'GOTO 1, 'IF 1, 'THEN 0, 'RETURN 0, 'ON 1, 'ENV 0, 'EXIT 0, 'ATN 1, 'INT 1, 'SIN 1, 'LEN 1, 'MID$ 2, 'MID3$ 3,'ASC 1, 'CHR$ 1, 'STR$ 1,'- 2, '+ 2, (symbol "-u") 1, '* 2, '/ 2, (symbol "^") 2 , '= 2, '<> 2, '< 2, '<= 2, '> 2, '>= 2, 'AND 2, 'OR 2)] 
    (if (.contains (keys tokens) token)
      (tokens token)
      0)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; eliminar-cero-decimal: recibe un numero y lo retorna sin ceros
; decimales no significativos, por ejemplo: 
; user=> (eliminar-cero-decimal 1.5)
; 1.5
; user=> (eliminar-cero-decimal 1.50)
; 1.5
; user=> (eliminar-cero-decimal 1.0)
; 1
; user=> (eliminar-cero-decimal 'A)
; A
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(declare pasar-a-entero)

(defn eliminar-cero-decimal [n]
  (if (number? n)
    (pasar-a-entero (read-string (apply str (seq (str (float n))))))
    n))

(defn pasar-a-entero [n]
  (if (= '(\0) (take-last 1 (seq (str n))))
    (int n)
    n))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; eliminar-cero-entero: recibe un simbolo y lo retorna convertido
; en cadena, omitiendo para los numeros del intervalo (-1..1) el
; cero a la izquierda del punto, por ejemplo:
; user=> (eliminar-cero-entero nil)
; nil
; user=> (eliminar-cero-entero 'A)
; "A"
; user=> (eliminar-cero-entero 0)
; "0"
; user=> (eliminar-cero-entero 1.5)
; "1.5"
; user=> (eliminar-cero-entero 1)
; "1"
; user=> (eliminar-cero-entero -1)
; "-1"
; user=> (eliminar-cero-entero -1.5)
; "-1.5"
; user=> (eliminar-cero-entero 0.5)
; ".5"
; user=> (eliminar-cero-entero -0.5)
; "-.5"
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(declare cero-entero?)

(defn eliminar-cero-entero [n] 
  (cond
   (nil? n) n
   (= 0 n) (str n)
   (and (number? n) (cero-entero? (float n))) (let [posicion (let [primero (first (seq (str n)))]
                                                               (if (= \0 primero)
                                                                 0
                                                                 1))] 
                                                (apply str (update (vec (str (float n))) posicion #(if (= \0 %) "" %)))) 
    true (str n)))

(defn cero-entero? [n]
  (let [primero (first (seq (str (Math/abs n))))]
    (= \0 primero)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Despues de cargarse el archivo, debe mostrarse el valor true 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
true
