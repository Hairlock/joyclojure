;; Joy of clojure

;; Protocols are like mixins, traits, interfaces in other languages
(defprotocol Concatenatable
  (cat [this other]))

(extend-type String
  Concatenatable
  (cat [this other]
    (.concat this other)))

(extend-type java.util.List
  Concatenatable
  (cat [this other]
    (concat this other)))

(cat "House "  "of Leaves")

(cat [1 2 3] [4 5 6])
