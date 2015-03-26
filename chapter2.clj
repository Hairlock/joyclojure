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
