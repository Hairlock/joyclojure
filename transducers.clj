;; Rich Hickley - transducers

(map inc (range 10))

(filter even? '(1 2 3 4 5 6 7 8 9 10))

(filter even? (map inc (range 10)))

;; Map and filter can be defined using reduce
(defn map-inc-reducer
  [res input]
  (conj res (inc input)))

(reduce map-inc-reducer [] (range 10))

;; Extract out inc
(defn map-reducer
  [f]
  (fn [res input]
    (conj res (f input))))

(reduce (map-reducer inc) [] (range 10))

(reduce (map-reducer dec) [] (range 10))

(reduce (map-reducer #(* % %)) [] (range 10))

(defn filter-even-reducer
  [result input]
  (if (even? input)
    (conj result input)
    result))

(reduce filter-even-reducer [] '(1 2 3 4 5 6 7 8 9 10))

(defn filter-reducer
  [predicate]
  (fn [result input]
    (if (predicate input)
      (conj result input)
      result)))

(reduce (filter-reducer even?) [] '(1 2 3 4 5 6 7 8 9 10))

(reduce
 (filter-reducer even?)
 []
 (reduce
  (map-reducer inc)
  []
  (range 10)))

;; above is equivalent to

(filter even? (map inc (range 10)))

;; Conj and + are both reducing functions result, input -> result
;; Let user pass in whatever reducing function they want

(defn mapping
  [f]
  (fn [reducing]
    (fn [result input]
      (reducing result (f input)))))

(defn filtering
  [predicate]
  (fn [reducing]
    (fn [result input]
      (if (predicate input)
        (reducing result input)
        result))))

(reduce
 ((filtering even?) conj)
 []
 (reduce
  ((mapping inc) conj)
  []
  (range 10)))


(((mapping inc) conj) [] 1)

(((mapping inc) conj) [2] 2)

(((mapping inc) conj) [2 3] 3)

;; As we can see ((mapping inc) conj) and the filtering one are reducing functions
;; like conj and +

((mapping inc) ((filtering even?) conj))

;; Above is also a reducing function

(reduce ((mapping inc) ((filtering even?) conj)) [] (range 10))

;; Clean above using comp (comp a b c d) -> (fn [r] (a (b (c (d r)))))

(def xform
  (comp
   (mapping inc)
   (filtering even?)))

(reduce (xform conj) [] (range 10))

(defn square [x] (* x x))

(def xform2
  (comp
   (filtering even?)
   (filtering #(< % 10))
   (mapping square)
   (mapping inc)))

(reduce (xform2 conj) [] (range 10))
