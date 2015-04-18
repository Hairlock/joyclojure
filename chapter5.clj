;; Chapter 5
;; Persistence, sequences and vectors

(def ds [:willie :barnabas :adam])

ds

(def ds1 (replace {:barnabas :quentin}) ds)
ds1

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











