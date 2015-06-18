;; Chapter 8 - Macros

(ns joyclojure.core)

;; In clojure functions are made entirely of data. Function definitions
;; are represented using an aggregation of various data structures.

(eval 42)

(eval '(list 1 2))
;; (eval (list 1 2)) fails because it tries to call 1 as a function

(eval (list (symbol "+") 1 2)) ;; 3

(defn contextual-eval [ctx expr]
  (eval
   `(let [~@(mapcat (fn [[k v]] [k `'~v]) ctx)]
      ~expr)))

(contextual-eval '{a 1, b 2} '(+ a b))

;; Nested syntax quotes
(let [x 9, y '(- x)]
  (println `y)
  (println ``y)
  (println ``~y)
  (println ``~~y)
  (contextual-eval {'x 36} ``~~y))

