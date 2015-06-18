;; Macros
(ns joyclojure.macros
  (:require [clojure.java.io :as io]))


(defn read-resource
  "Read a resource into a string"
  [path]
  (read-string (slurp (io/resource path))))

(defn read-resource
  [path]
  (-> path
      io/resource
      slurp
      read-string))

(macroexpand '(when true
                true
                false
                nil))

(defmacro postfix-notation
  "I'm too indie for prefix notation"
  [expression]
  (conj (butlast expression) (last expression)))

(postfix-notation (1 1 +))

(defmacro code-critic
  "phreases are courtesy of Hermes Conrad from Futurama"
  [{:keys [good bad]}]
  (list 'do
        (list 'println
              "Great squid of Madrid, this is bad code:"
              (list 'quote bad))
        (list 'println
              "Sweet gorilla of Manilla, this is good code:"
              (list 'quote good))))

(code-critic {:good (+ 1 1) :bad (1 + 1)})

(macroexpand '(postfix-notation (1 1 +))) ;; (+ 1 1)
(postfix-notation (1 1 +)) ;; 2


(macroexpand '(when (the-cows-come :home)
                (call me :pappy)
                (slap me :silly)))

(defmacro unless
  "Inverted 'if'"
  [test & branches]
  (conj (reverse branches) test 'if))

(macroexpand '(unless (done-been slapped? me)
                      (slap me :silly)
                      (say "I reckon that'll learn me")))


;; Syntax quote

(defmacro code-critic
  [{:keys [good bad]}]
  `(do (println "Great squid of Madrid, this is bad code:"
                (quote ~bad))
       (println "Sweet gorilla of Manilla, this is good code:"
                (quote ~good))))

'+ ;; +
`+ ;; clojure.core/+

`(+ 1 ~(inc 1))
`(+ 1 (inc 1))

(list '+ 1 (inc 1))

(concat '(+ 1) (list (inc 1)))

`(+ 1 ~(inc 1))

(defmacro code-praiser
  [code]
  (list 'println
        "Sweet gorilla of Manilla, this is good code:"
        (list 'quote code)))

(defmacro code-praiser
  [code]
  `(println
    "Sweet gorilla of Manilla, this is good code:"
    (quote ~code)))

(macroexpand '(code-praiser (+ 1 1)))
