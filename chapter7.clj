;; Chapter 7 - Functional Programming

;; Most of clojure's composite types can be used as functions of their elements
([:a :b] 0)
(map [:cthon :phthor :beowulf :grendel] #{0 3})

;; Clojure collections can act as functions and functions can act as data
;; The primary unit of computation is the function
(def fifth
  (comp first rest rest rest rest))

(fifth [1 2 3 4 5])

(defn fnth [n]
  (apply comp
         (cons first
               (take (dec n) (repeat rest)))))

((fnth 4) '[a b c d e])

;; Creating functions on demand using partial functions
((partial + 5) 100 200)
(#(apply + 5 %&) 100 200 )

((complement even?) 2)

;; Higher order functions - take a function as a parameter
(sort > [7 1 4])
(sort-by second [[:a 7], [:c 13], [:b 21]])

(def plays [{:band "Burial", :plays 979, :loved 9}
            {:band "Eno",    :plays 2333, :loved 15}
            {:band "Bill Evans" :plays 979, :loved 9}
            {:band "Magma",  :plays 2665, :loved 31}])

(def sort-by-loved-ratio
  (partial sort-by #(/ (:plays %) (:loved %))))

(sort-by-loved-ratio plays)

(defn columns [column-names]
  (fn [row]
    (vec (map row column-names))))

((columns [:plays :loved :band])
 {:band "Burial", :plays 979, :loved 9})

;; Ensure that lazy ssequences never fully realized in memory
;; by prefering higher-order functions for processing.

;; map, reduce, filter, for, some, repeatedly, sort-by, keep,
;; take-while, drop-while

;; Pure functions follow simple guidelines, they always return
;; the same result given the same arguments. No observable side
;; effects

;; Benefits include referential transparency (to time, it acts as
;; a constant)

(defn keys-apply [f ks m]
  (let [only (select-keys m ks)]
    (zipmap (keys only)
            (map f (vals only)))))

(keys-apply #(.toUpperCase %) #{:band} (plays 0))

(defn manip-map [f ks m]
  (merge m (keys-apply f ks m)))

(manip-map #(int (/ % 2)) #{:plays :loved} (plays 0))

(defn mega-love! [ks]
  (map (partial manip-map #(int (* % 1000)) ks) plays))

(mega-love! [:loved])

;; Mega love is referentially impure as it works on the global
;; variable plays

;; Clojure has named parameters
(defn slope
  [& {:keys [p1 p2] :or {p1 [0 0] p2 [1 1]}}]
  (float (/ (- (p2 1) (p1 1))
            (- (p2 0) (p1 0)))))

(slope :p2 [2 1])
(slope :p1 [4 15] :p2 [3 21])
(slope)

;; Can add constraints functions with pre and post conditions
(defn slope2 [p1 p2]
  {:pre [(not= p1 p2) (vector? p1) (vector? p2)]
   :post [(float? %)]}
  (/ (- (p2 1) (p1 1))
     (- (p2 0) (p1 0))))

;;(slope2 [10 10] [10 10])
;;(slope2 [10 1] '(1 20))

;; Can also use assert but its cumbersome with post and pre/post
;; allow assertions from a different source than the body of
;; the function. To turn off add (set! *assert* false)

;; To decouple assertions from functions
(defn put-things [m]
  (into m {:meat "beef" :veggie "broccoli"}))

(put-things {})

(defn vegan-constraints [f m]
  {:pre [(:veggie m)]
   :post [(:veggie %) (nil? (:meat %))]}
  (f m))

;;(vegan-constraints put-things {:veggie "carrot"})

(defn balanced-diet [f m]
  {:post [(:meat %) (:veggie %)]}
  (f m))

(balanced-diet put-things {})

(defn finicky [f m]
  {:post [(= :meat %) (:meat m)]}
  (f m))

;;(finicky put-things {:meat "chicken"})

