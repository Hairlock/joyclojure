;; Chapter 2

;; Lists - evaluated with first item resolved to function, macro, operator
;; if is a macro or operator the remaining resolved accordingly, else
;; passed to the function

;;(yankee hotel foxtrot)
(+ 1 2)

;; Vectors - store a series of values, each item evaluated in order
;; no function call/macro performed on vector itself
[1 2 :a :b :c]

;; Maps - store unique keys and on value per key, each item evaluated
;; before stored in the map. Ordering isn't guaranteed
{1 "one", 2 "two", 3 "three"}


;; Sets - store zero or more unique items
#{1 2 "three" :four 0x5}

;; Anonymous functions - unnamed functions called immediately
((fn [x y]
   (println "Making a set")
   #{x y})
 1 2)

;; Named functions - def and defn
(def make-set-def
  (fn [x y]
    (println "Making a set")
    #{x y}))

;;(make-set-def 1 2)

(defn make-set
  "Takes two values and makes a set from them"
  [x y]
  (println "Making a set")
  #{x y})

;; Arity - differences in argument count that a fn will accept
(defn arity2+ [first second & more]
  (vector first second more))

(arity2+ 1 2 3 4 5)

;; Shorthand anonymous fns - args implicitly declared with %
(def make-list #(list %1 %2))
(def make-list-more #(list %1 %2 %&))

(make-list 1 2)
(make-list-more 1 2 3 4 5)

;; Loops, locals, blocks
;; do - block of expressions that need to be treated as one
;; all eval'd but only last one returned

(do
  (def x 5)
  (def y 4)
  (+ x y)
  [x y])

;; let - defines scope of local variable that can't vary

(let [r 5
      pi 3.1415
      r-squared (* r r)]
  (println "radius is" r)
  (* pi r-squared))


;; recur - used for tail recursion, a form is in a tail
;; position when its value may be the return value of
;; the entire expression

(defn print-down-from
  [x]
  (when (pos? x)
    (println x)
    (recur (dec x))))

(print-down-from 2)

(defn sum-down-from
  [sum x]
  (if (pos? x)
    (recur (+ sum x) (dec x))
    sum))

(sum-down-from 0 3)

;; loop - used to loop to a point inside a fn instead of
;; top the top

(defn sum-down-from
  [initial-x]
  (loop [sum 0, x initial-x]
    (if (pos? x)
      (recur (+ sum x) (dec x))
      sum)))

(sum-down-from 5)

;; Evaluation
(cons 1 [2 3])
1
[2 3]

;; Quoting
(def age 9)
(quote age)
(cons 1 (quote (2 3)))
(cons 1 '(2 3))

;; Syntax-quote - prevents its args and subforms from being
;; evaluated

`(1 2 3)

;; Symbol auto-qualification
clojure.core/map
`map
`is-always-right
`(map even? [1 2 3])

;;Unquote - demarcates specific forms as requiring evaluation
`(+ 10 (* 3 2))
`(+ 10 ~(* 3 2))

;;Unquote splicing - @ means to splice rather than insert as
;; a nested list
(let [x '(2 3)] `(1 ~@x))

;; Auto-gensym - append # to generate unique symbol name
`potion#

;; Host language interop
java.util.Locale/JAPAN
(Math/sqrt 9)

;; Creating instances
(new java.awt.Point 0 1)

(java.util.HashMap.{"foo" 42 "bar" 9 "baz" "quux"})

(.-x (java.awt.Point. 10 20))

(.divide (java.math.BigDecimal. "42") 2M)

;; Setting instance fields
(let [origin (java.awt.Point. 0 0)]
  (set! (.-x origin) 15)
  (str origin))

;; .. macro
;; new java.util.Date().toString().endsWith("2014")
(.endsWith (.toString (java.util.Date.)) "2015")
(.. (java.util.Date.) toString (endsWith "2015"))

;;doto macro - initialize a fresh instance
;; java.util.HashMap props = new java.util.HashMap();
;; props.put("Home", "/home/me");
(doto (java.util.HashMap.)
  (.put "Home" "/home/me"))

;; Exception handling
;;(throw (Exception. "Throwing..."))

(defn throw-catch
  [f]
  [(try
     (f)
     (catch ArithmeticException e "Don't divide by 0")
     (catch Exception e (str "You are so bad" (.getMessage e)))
     (finally (println "returning... ")))])

(throw-catch #(/ 10 5))

;; Namespaces
(ns joy.ch2)

(defn report-ns []
  (str "The current namespace is " *ns*))

(report-ns)

;; loading ns
(ns joy.req
  (:require clojure.set :as s))

(s/intersection #{1 2 3} #{3 4 5})

(ns joy.use-ex
  (:require [clojure.string :refer (capitalize)]))

(map capitalize ["kilgore" "trout"])

(ns joy.ch1
  (:refer joy.ch2))

(ns joy.another
  (:refer clojure.set :rename {union onion}))

(onion #{1 2} #{4 5})


;; Import unqualified java classes - fully qualified ones
;; are always available
(ns joy.java
  (:import [java.util HashMap]
           [java.util.concurrent.atomic AtomicLong]))

(HashMap. {"happy?" true})
(AtomicLong. 42)










