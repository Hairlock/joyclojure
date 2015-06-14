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

;; A closure is a function that has access to locals from the
;; context where it was created
(def times-two
  (let [x 2]
    (fn [y] (* y x))))

(times-two 2)

(def add-and-get
  (let [ai (java.util.concurrent.atomic.AtomicInteger.)]
    (fn [y] (.addAndGet ai y))))

(add-and-get 7)

(defn times-n [n]
  (let [x n]
    (fn [y] (* y x))))

(times-n 4)

(def times-four (times-n 4))

(times-four 10)

(defn times-n-2 [n]
  (fn [y] (* y n)))

(defn divisible [denom]
  (fn [num]
    (zero? (rem num denom))))

;; Don't need to store a closure in a var, create and call
;; immediately
((divisible 3) 6)

;; Anywhere a function is expected a closure can be used instead
(filter even? (range 10))

(filter (divisible 4) (range 10))

(defn filter-divisible [denom s]
  (filter #(zero? (rem % denom)) s))

(filter-divisible 4 (range 10))

;; Robot

(def bearings [{:x 0, :y 1} ; north
               {:x 1, :y 0} ; east
               {:x 0, :y -1} ;south
               {:x -1, :y 0} ; west
               ])

(defn forward [x y bearing-num]
  [(+ x (:x (bearings bearing-num)))
   (+ y (:y (bearings bearing-num)))])

;; Starting with bearing 0 (north) at 5,5 and going one step
(forward 5 5 0)
(forward 5 5 1) ;; east
(forward 5 5 2) ;; east

(defn bot [x y bearing-num]
  {:coords [x y]
   :bearing ([:north :east :south :west] bearing-num)
   :forward (fn [] (bot (+ x (:x (bearings bearing-num)))
                        (+ y (:y (bearings bearing-num)))
                        bearing-num))
   :turn-right (fn [] (bot x y (mod (+ 1 bearing-num) 4)))
   :turn-left (fn [] (bot x y (mod (- 1 bearing-num) 4)))})

(:coords (bot 5 5 0))

(:bearing (bot 5 5 0))

(:coords ((:forward (bot 5 5 0))))

(:bearing ((:forward ((:forward ((:turn-right (bot 5 5 0))))))))
(:coords ((:forward ((:forward ((:turn-right (bot 5 5 0))))))))

(defn mirror-bot [x y bearing-num]
  {:coords   [x y]
   :bearing  ([:north :east :south :west] bearing-num)
   :forward  (fn [] (mirror-bot (- x (:x (bearings bearing-num)))
                                (- y (:y (bearings bearing-num)))
                                bearing-num))
   :turn-right (fn [] (mirror-bot x y (mod (- 1 bearing-num) 4)))
   :turn-left (fn [] (mirror-bot x y (mod (+ 1 bearing-num) 4)))})

;; Thinking recursively
(defn pow [base exp]
  (if (zero? exp)
    1
    (* base (pow base (dec exp)))))

(pow 2 10)
;; (pow 2N 10000)

(defn pow2 [base exp]
  (letfn [(kapow [base exp acc]
            (if (zero? exp)
              acc
              (recur base (dec exp) (* base acc))))]
    (kapow base exp 1)))

(pow2 2N 10000)

(def simple-metric {:meter 1,
                    :km 1000,
                    :cm 1/100,
                    :mm [1/10 :cm]})

(defn convert [context descriptor]
  (reduce (fn [result [mag unit]]
            (+ result
               (let [val (get context unit)]
                 (if (vector? val)
                   (* mag (convert context val))
                   (* mag val)))))
          0
          (partition 2 descriptor)))
 
(convert simple-metric [50 :cm])
(float (convert simple-metric [3 :km 10 :meter 80 :cm 10 :mm]))

(get simple-metric :km)

;; Tail call optimization

(defn gcd [x y]
  (cond
   (> x y) (gcd (- x y) y)
   (< x y) (gcd x (- y x))
   :else x))

(defn elevator [commands]
  (letfn
      [(ff-open [[_ & r]]
         "When the elevator is open on the 1st floor
          it can either close or be done."
         #(case _
            :close (ff-closed r)
            :done  true
            false))
       (ff-closed [[_ & r]]
         "When the elevator is closed on the 1st floor
          it can either open or go up"
         #(case _
            :open  (ff-open r)
            :up    (sf-closed r)
            false))
       (sf-closed [[_ & r]]
         "When the elevator is closed on the 2nd floor
          it can either go down or open."
         #(case _
            :down  (ff-closed r)
            :open  (sf-open r)
            false))
       (sf-open [[_ & r]]
         "When the elevator is open on the 2nd floor
          it can either close or be done."
         #(case _
            :close (sf-closed r)
            :done true
            false))]
    (trampoline ff-open commands)))
