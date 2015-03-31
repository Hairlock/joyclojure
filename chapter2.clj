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




