;; Chapter 4
;; On Scalars

;; Scalar data types: number, symbol, keyword, string,
;; character


;; Truncation - limiting accuracy for a flating-point number

;; Promotion - clojure detects when overflow occus and promotes to
;; a numerical representation that can accomodate larger values

(let [butieatedit 3.14159265358979323846264338327950288419716939937]
  (println (class butieatedit))
  butieatedit)

(let [imadeuapi 3.14159265358979323846264338327950288419716939937M]
  (println (class imadeuapi))
  imadeuapi)

;; Overflow - integer and long values subject to overflow errors
;;(+ Long/MAX_VALUE Long/MAX_VALUE)

(unchecked-add (Long/MAX_VALUE) (Long/MAX_VALUE))

;; Underflow - number so small it collapses to 0
;;1.0E-430

;; Rounding errors
(let [approx-interval (/ 209715 2097152) ;;Patriot¡¯s approx 0.1
      actual-interval (/ 1 10) ;;Clojure can accurately represent 0.1
      hours (* 3600 100 10)
      actual-total (double (* hours actual-interval))
      approx-total (double (* hours approx-interval))]
(- actual-total approx-total))

;; Rationals - rational num is a fraction w/ arbitrarily precises numerator
;; and denominator. Clojure rational type allows for arb large num and denum
;; rationals aren't as fast as using floats/doubles
;; rationalize, rational?, ratio?

(def a (rationalize 1.0e50))
(def b (rationalize -1.0e50))
(def c (rationalize 17.0e00))

(+ (+ a b) c)

(+ a (+ b c))

;; To maintain perfect accuracy - 
;; never use java math libs unless they return results of BigDecimal
;; don't rationalize values that are java float or double primitives
;; write all high-precision calcs with rationals
;; only convert to a floating-point representation as a last resort

(numerator (/ 123 10))
(denominator (/ 123 10))

;; Keywords
;; Self-evaluating types prefixed with one or more colons
;; keywords always refer to themselves, whereas symbols do not
;; sometimes evaluated as functions

:a-keyword
(def population {:zombies 2700, :humans 9})

(get population :zombies)
(:zombies population)

(println (/ (:zombies population)
            (:humans population))
         "zombies per capita")

;; Keywords as directives
(defn pour [lb ub]
  (cond
   (= ub :toujours) (iterate inc lb)
   :else (range lb ub)))

(pour 1 10)
;;(pour 1 :toujours)

;; Qualifying keywords - double colon
::not-in-ns

(defn do-blowfish [directive]
  (case directive
    :aquarium/blowfish (println "feed the fish")
    :crypto/blowfish   (println "encode the message")
    :blowfish          (println "not sure what to do")))

(ns crypto)
(user/do-blowfish :blowfish)

;; Symbols - words that refer to other things
;; use quote operator to refer to them directly

(identical? 'goat 'goat)
(= 'goat 'goat)

(let [x 'goat, y x]
  (identical? x y))

;; Metadata
(let [x (with-meta 'goat {:ornery true})
      y (with-meta 'goat {:ornery false})]
  [(= x y)
   (identical? x y)
   (meta x)
   (meta y)])

;; Clojure is a Lisp-1 which means it uses the same name
;; resolution for function and value bindings

(defn best [f xs]
  (reduce #(if (f % %2) % %2) xs))

(best > [1 3 4 2 7 5 3])

;; Regexes in clojure
#"an example regex"
(class #"example")

(re-seq #"\w+" "one-two/three")

;; Capturing group causes each returned item to be a vector
(re-seq #"\w*(\w)" "one-two/three")

