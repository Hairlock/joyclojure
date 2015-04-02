;; Chapter 3

;; Truthiness
;; Every value looks true to if except false and nil
(def evil-false (Boolean. "false"))

evil-false
(if evil-false :truthy :falsey)

;; Nil
(when (nil? nil) "Not nil, false")

;; Nil punning - pun means a term with the same behavior
(seq [1 2 3])
(seq [])

;; 
(defn print-seq
  [s]
  (when (seq s)
    (prn (first s))
    (recur (rest s))))

(print-seq [])

;; Destructuring
;; Works on Matcher object and CharSequence & RandomAccess interfaces
(def guys-whole-name ["Guy" "Lewis" "Steele"])

(let [[f-name m-name l-name] guys-whole-name]
  (str l-name ", " f-name " " m-name))


(let [[a b c & more] (range 10)]
  (println "a b c are:" a b c)
  (println "more is:" more))

(let [range-vec (vec (range 10))
      [a b c & more :as all] range-vec]
  (println "a b c are:" a b c)
  (println "more is:" more)
  (println "all is:" all))

;; :keys, :strs, :syms can be used to clean up destructuring

(def guys-names-map
  {:f-name "Guy" :m-name "Lewis" :l-name "Steele"})

(let [{:keys [f-name m-name l-name]} guys-names-map]
  (str l-name ", " f-name " " m-name))

;; use :as to get at the original map
(let [{f-name :f-name, :as whole-name} guys-names-map]
  (println "First name is" f-name)
  (println "Whole name is below:")
  whole-name)

;; :or to provide defaults to replace nil if key not found

(let [{:keys [title f-name m-name l-name],
       :or {title "Mr."}} guys-names-map]
  (println title f-name m-name l-name))

;; works on lists too

(defn whole-name [& args]
  (let [{:keys [f-name m-name l-name]} args]
    (str l-name ", " f-name " " m-name)))

(whole-name :f-name "Guy" :m-name "Lewis" :l-name "Steele")

;; Associative destructuring

(let [{first-thing 0, last-thing 3} [1 2 3 4]]
  [first-thing last-thing])

;; Function parameters can destructure a map or seq
(defn print-last-name [{:keys [l-name]}]
  println l-name)

(print-last-name guys-names-map)
