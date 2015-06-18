;; Chapter 6 - Being lazy and set in your ways

;; Immutability - all properties of imm objects are defined at
;; construction and cannot be changed after

;; Because an object is immutaable reasoning about its states is
;; simplified. And so testing such a system is simplified.

;; Equality on immutable objects means its always equal, not so with
;; mutable objects.

;; Sharing is cheap, since the object never changes all you need to do
;; is provide a reference to it. Imm objects are always thread safe,
;; it can be shared across different threads of execution.

(def baselist (list :barnabas :adam))
(def list1 (cons :willie baselist))
(def list2 (cons :phoenix baselist))

list1
list2

;; Next part of both list1 and list2 are identical
(identical? (next list1) (next list2))

(defn xconj [t v]
  (cond
   (nil? t) {:val v, :L nil, :R nil}
   (< v (:val t)) {:val (:val t),
                   :L (xconj (:L t) v),
                   :R (:R t)}
   :else          {:val (:val t),
                   :L (:L t),
                   :R (xconj (:R t) v)}))

(def tree1 (xconj nil 5))
tree1
(def tree1 (xconj tree1 3))
tree1
(def tree1 (xconj tree1 2))
tree1

(defn xseq [t]
  (when t
    (concat (xseq (:L t)) [(:val t)] (xseq (:R t)))))

(xseq tree1)

(def tree2 (xconj tree1 7))
(xseq tree2)


;; Laziness
;; Allows avoidance of errors in the evaluation of compound
;; structures

(defn and-chain [x y z]
  (and x y z (do
               (println "Made it!")
               :all-truthy)))

(and-chain () 42 true)
(and-chain true false true)

;; Non-lazy example
(defn rec-step [[x & xs]]
  (if x
    [x (rec-step xs)]
    []))

(rec-step [1 2 3 4])
;;(rec-step (range 200000)) ;; Causes stack overflow

;; Use lazy-seq and rest instead of next, prefer higher-order
;; functions when processing sequences
(defn lz-rec-step [s]
  (lazy-seq
   (if (seq s)
     [(first s) (lz-rec-step (rest s))]
     [])))

(lz-rec-step [1 2 3 4])
(dorun (lz-rec-step (range 200000)))

(defn simple-range [i limit]
  (lazy-seq
   (when (< i limit)
     (cons i (simple-range  (inc i) limit)))))

;; Use laziness to prevent the full realization of interim results
;; during a calculation. Holding onto the head of a sequence
;; prevents it from being garbage collected
(defn triangle
  [n]
  (/ (* n (+ n 1)) 2))

(def tri-nums (map triangle (iterate inc 1)))

(take 10 tri-nums)

(take 10 (filter even? tri-nums))

(nth tri-nums 99)

(take 2 (drop-while #(< % 10000) tri-nums))


;; Clojure not really lazy but their sequences are. Employ use of
;; call-by-need semantics (i.e macros). Also use explicit laziness
;; with delay and force. Delay used to defer evaluation until
;; force is invoked.

(defn defer-expensive
  [cheap expensive]
  (if-let [good-enough (force cheap)]
    good-enough
    (force expensive)))

(defer-expensive (delay :cheap)
  (delay (do (Thread/sleep 5000) :expensive)))

(defn inf-triangles [n]
  {:head (triangle n)
   :tail (delay (inf-triangles (inc n)))})

(defn head [l] (:head l))
(defn tail [l] (force (:tail l)))


(def tri-nums (inf-triangles 1))
(head tri-nums)
(head (tail tri-nums))
(head (tail (tail tri-nums)))

(defn taker [n l]
  (loop [t n, src l, ret []]
    (if (zero? t)
      ret
      (recur (dec t) (tail src) (conj ret (head src))))))

(defn nthr [l n]
  (if (zero? n)
    (head l)
    (recur (tail l) (dec n))))

(taker 10 tri-nums)

;; Quicksort

(defn rand-ints [n]
  (take n (repeatedly #(rand-int n))))

(rand-ints 10)

(defn sort-parts [work]
  (lazy-seq
   (loop [[part & parts] work]
     (if-let [[pivot & xs] (seq parts)]
       (let [smaller? #(< % pivot)]
         (recur (list*
                 (filter smaller? xs)
                 pivot
                 (remove smaller? xs)
                 parts)))
       (when-let [[x & parts] parts]
         (cons x (sort-parts parts)))))))

(defn qsort [xs]
  (sort-parts (list xs)))
