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

;; Experimenting in the REPL
(find-doc "xor")

(bit-xor 1 2)

(defn xors [max-x max-y]
  (for [x (range max-x) y (range max-y)]
    [x y (rem (bit-xor x y) 256)]))
(xors 2 2)

(defn f-values [f xs ys]
  (for [x (range xs) y (range ys)]
    [x y (rem (f x y) 256)]))


;; For allows you to iterate over a collection, perform
;; some action and collec the results in a seq
(for [meth (.getMethods java.awt.Frame)
      :let [name (.getName meth)]
      :when (re-find #"Vis" name)]
  name)

(def frame (java.awt.Frame.))

(.isVisible frame)
(.setVisible frame true)
(.setSize frame (java.awt.Dimension. 200 200))

(javadoc frame)

(def gfx (.getGraphics frame))
;;(.fillRect gfx 100 100 50 75)
;;(.setColor gfx (java.awt.Color. 255 128 0))
;;(.fillRect gfx 100 150 75 50)

(doseq [[x y xor] (xors 500 500)]
  (.setColor gfx (java.awt.Color. xor xor xor))
  (.fillRect gfx x y 1 1))

(.printStackTrace *e)

(defn clear [g]
  (.clearRect g 0 0 200 200))


(clear gfx)

(defn draw-values [f xs ys]
  (clear gfx)
  (.setSize frame (java.awt.Dimension. xs ys))
  (doseq [[x y v] (f-values f xs ys)]
    (.setColor gfx (java.awt.Color. v v v))
    (.fillRect gfx x y 1 1)))

(draw-values bit-and 256 256)
(draw-values + 256 256)
(draw-values * 256 256)


