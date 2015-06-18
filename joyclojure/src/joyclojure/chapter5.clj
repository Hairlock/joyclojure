;; Chapter 5
;; Persistence, sequences and vectors

(def ds [:willie :barnabas :adam])

ds

;;(def ds1 (replace {:barnabas :quentin}) ds)
;;ds1

;; A sequencial collection holds a series of values
;; without reordering them
[1 2 3 4]
'(1 2 3 4)

;; A sequence is a sequential collection that represents
;; a series of values that may or may not exist yet
(map #() [])

;; A seq is any object that implements the seq API,
;; supportin gthe first and rest functions
(first [1 2 3])
(rest [1 2 3])

;; Building vectors
(vec (range 10))

(let [my-vector [:a :b :c]]
  (into my-vector (range 10)))

(into (vector-of :int) [Math/PI 2 1.3])

(def a-to-j (vec (map char (range 65 75))))

a-to-j
(nth a-to-j 4)
(get a-to-j 4)
(a-to-j 4)

(seq a-to-j)
(rseq a-to-j)

;; Assoc for vectors works on indices that already exist
;; and one step past the end

(assoc a-to-j 4 "no longer E")

(replace {2 :a, 4 :b} [1 2 3 2 3 4])

;; Vectors as stack - use conj/pop for push/pop
(def my-stack [1 2 3])

(peek my-stack)
(pop my-stack)
(conj my-stack 4)
(+ (peek my-stack) (peek (pop my-stack)))

;; For stacks use peek instead of last and conj instead of assoc
;; for growing the vector, use pop instead of dissoc for shrinking it

(subvec a-to-j 3 6)

;; Vectors as map entries
(first {:width 10, :height 30, :depth 15})
(vector? (first {:width 10, :height 20, :depth 15}))

(doseq [[dimension amount] {:width 10, :height 20, :depth 15}]
       (println (str (name dimension) ":") amount "inches"))

;; Vectors are versatile but aren't sparse. Vec of length n, can only
;; insert at index n, can't skip and insert at higher number.
;; Non-sequential index needed? Consider hashmap or sorted map
;; Not good as a queue, use PersistentQueue instead
;; Contains? use sets

;; A persistent list is a singly linked list where each node knows
;; its distance from the end

(cons 1 '(2 3))
(conj '(2 3) 1)

;; Use cons to add to the front of a lazy sez, range, etc. But to
;; get a bigger list use conj. When needing a stack test list and
;; vector to see which performs better. Use vectors to look up
;; items by index not lists

;; Using persistent queue
(defmethod print-method clojure.lang.PersistentQueue
  [q, w]

  (print-method '<- w)
  (print-method (seq q) w)
  (print-method '-< w))

clojure.lang.PersistentQueue/EMPTY

(def schedule
  (conj clojure.lang.PersistentQueue/EMPTY
        :wake-up :shower :brush-teeth))

schedule

;; PQ implemented using two seperate collections, the front is seq
;; and rear is a vector. Insertions in rear vector, removals in front
;; seq. When all items popped, back vec wrapped in a seq and
;; becomes front, an empty vec is used as new back

(peek schedule)
(pop schedule) ;; This returns a queue so conjs are speedy
(rest schedule) ;; dont use this to remove elements as its a seq

;; Persistent sets - collections of unsorted unique items
;; Sets are functions of their elements that return the matched element
;; or nil

(#{:a :b :c :d} :c)
(#{:a :b :c :d} :e)

(get #{:a 1 :b 2} :b)
(get #{:a 1 :b 2} :z :nothing-found)

(some #{:b} [:a 1 :b 2])
(some #{1 :b} [:a 1 :b 2])

;; Sorted set - arguments need to be mutually comparable
(sorted-set :b :c :a)
(sorted-set [3 4] [1 2])

;; Contains? returns true if a given key exists in a collection
(contains? [1 2 4 3] 4)

(clojure.set/intersection #{:humans :fruit-bats :zombies}
                          #{:chupacabra :zombies :humans})

(clojure.set/intersection #{:pez :gum :dots :skor}
                          #{:pez :skor :pocky}
                          #{:pocky :gum :skor})

;; Second example its an intersection between first two then with
;; the last

(clojure.set/union #{:humans :fruit-bats :zombies}
                   #{:chupacabra :zombies :humans})

;; Difference - removing all elements from set a that are in set b
(clojure.set/difference #{1 2 3 4} #{3 4 5 6})


;; Maps - should be used to store named values
;; Hash maps provide an unsorted key/value associative structure

(hash-map :a 1, :b 2, :c 3, :d 4, :e 5)

(let [m {:a 1, 1 :b, [1 2 3]  "4 5 6"}]
  [(get m :a) (get m [1 2 3])])

;; Providing a map to a seq returns a sequence of map entries
(seq {:a 1, :b 2})

(into {} (map vec '[(:a 1) (:b 2)]))

(apply hash-map [:a 1 :b 2])

(zipmap [:a :b] [1 2])

;; As can be seen above, hashmaps have no order guarantee, use
;; sorted maps for ordering

(sorted-map :thx 1138 :r2d 2)
(sorted-map-by #(compare (subs %1 1) (subs %2 1)) "bac" 2 "abc" 9)

;; Can jump efficiently to a key and "round up" to next closest key
;; that exists (or down) using subseq and rsubseq.
;; Also hashmaps treat type-different numbers differently but sorted
;; maps do not
(assoc {1 :int} 1.0 :float)
(assoc (sorted-map 1 :int) 1.0 :float)

;; If you need to maintain insertion ordering then use array maps
(seq (hash-map :a 1, :b 2, :c 3))
(seq (array-map :a 1, :b 2, :c 3))


;; Implement function to locate the positional index of an element
;; in a sequence

;; Bad example
(defn pos [e coll]
  (let [cmp (if (map? coll)
              #(= (second %1) %2)
              #(= %1 %2))]
    (loop [s coll idx 0]
      (when (seq s)
        (if (cmp (first s) e)
          (if (map? coll)
            (first (first s))
            idx)
          (recur (next s) (inc idx)))))))

(pos 3 [:a 1 :b 2 :c 3 :d 4])

;; Lay out collections as sequences of pairs
(defn index [coll]
  (cond
   (map? coll) (seq coll)
   (set? coll) (map vector coll coll)
   :else (map vector (iterate inc 0) coll)))

(index [:a 1 :b 2 :c 3 :d 4])
(index {:a 1 :b 2 :c 3 :d 4})
(index #{:a 1 :b 2 :c 3 :d 4})

(defn pos2 [e coll]
  (for [[i v] (index coll) :when (= e v)] i))

(pos2 3 [:a 1 :b 2 :c 3 :d 4])

(pos2 3 [:a 3 :b 3 :c 3 :d 4])

(defn pos3 [pred coll]
  (for [[i v] (index coll) :when (pred v)] i))

(pos3 even? [2 3 6 7])
(pos3 #{3 4} {:a 1 :b 2 :c 3 :d 4})


