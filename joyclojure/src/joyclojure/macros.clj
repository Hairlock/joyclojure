;; Macros
(ns joyclojure.macros
  (:require [clojure.java.io :as io]))


(defn read-resource
  "Read a resource into a string"
  [path]
  (read-string (slurp (io/resource path))))

(defn read-resource
  [path]
  (-> path
      io/resource
      slurp
      read-string))

(macroexpand '(when true
                true
                false
                nil))

(defmacro postfix-notation
  "I'm too indie for prefix notation"
  [expression]
  (conj (butlast expression) (last expression)))

(postfix-notation (1 1 +))

(defmacro code-critic
  "phreases are courtesy of Hermes Conrad from Futurama"
  [{:keys [good bad]}]
  (list 'do
        (list 'println
              "Great squid of Madrid, this is bad code:"
              (list 'quote bad))
        (list 'println
              "Sweet gorilla of Manila, this is good code:"
              (list 'quote good))))

(code-critic {:good (+ 1 1) :bad (1 + 1)})

(macroexpand '(postfix-notation (1 1 +))) ;; (+ 1 1)
(postfix-notation (1 1 +)) ;; 2


(macroexpand '(when (the-cows-come :home)
                (call me :pappy)
                (slap me :silly)))

(defmacro unless
  "Inverted 'if'"
  [test & branches]
  (conj (reverse branches) test 'if))

(macroexpand '(unless (done-been slapped? me)
                      (slap me :silly)
                      (say "I reckon that'll learn me")))


;; Syntax quote

(defmacro code-critic
  [{:keys [good bad]}]
  `(do (println "Great squid of Madrid, this is bad code:"
                (quote ~bad))
       (println "Sweet gorilla of Manila, this is good code:"
                (quote ~good))))

'+ ;; +
`+ ;; clojure.core/+

`(+ 1 ~(inc 1))
`(+ 1 (inc 1))

(list '+ 1 (inc 1))

(concat '(+ 1) (list (inc 1)))

`(+ 1 ~(inc 1))

(defmacro code-praiser
  [code]
  (list 'println
        "Sweet gorilla of Manila, this is good code:"
        (list 'quote code)))

(defmacro code-praiser
  [code]
  `(println
    "Sweet gorilla of Manila, this is good code:"
    (quote ~code)))

(macroexpand '(code-praiser (+ 1 1)))


;; ----------------------------------------------------------

(defn criticize-code
  [criticism code]
  `(println ~criticism (quote ~code)))

(defmacro code-critic
  [{:keys [good bad]}]
  `(do ~(criticize-code "Cursed bacteria of Liberia, this is bad code:" bad)
       ~(criticize-code "Sweet sacred boa of Western and Eastern samoa, this
                         is good code:" good)))

(defmacro code-critic
  [{:keys [good bad]}]
  `(do ~(map #(apply criticize-code %)
n             [["Great squid of Madrid, this is bad code:" bad]
              ["Sweet gorilla of Manila, this is good code:" good]])))

(code-critic {:good (+ 1 1) :bad (1 + 1)})

(clojure.pprint/pprint (macroexpand '(code-critic {:good (+ 1 1) :bad (1 + 1)})))

(do
 ((clojure.core/println "criticism" '(1 + 1))
  (clojure.core/println "criticism" '(+ 1 1))))


;; After evaluating first println call:
(do
 (nil
  (clojure.core/println "criticism" '(+ 1 1))))

;; After evaluating second println call:
(do
 (nil nil))

;; Need unquote splicing

`(+ ~(list 1 2 3))
`(+ ~@(list 1 2 3))


(defmacro code-critic
  [{:keys [good bad]}]
  `(do ~@(map #(apply criticize-code %)
             [["Great squid of Madrid, this is bad code:" bad]
              ["Sweet gorilla of Manila, this is good code:" good]])))

(code-critic {:good (+ 1 1) :bad (1 + 1)})

;; Variable Capture
(def message "Good job!")
(defmacro with-mischief
  [& stuff-to-do]
  (concat (list 'let ['message "Oh, big deal!"])
          stuff-to-do))

(with-mischief
  (println "Here's how I feel about that thing you did: " message))

;; Syntax quoting saves us here

(def message "Good job!")
(defmacro with-mischief
  [& stuff-to-do]
  `(let [message "Oh, big deal!"]
     ~@stuff-to-do))
(with-mischief
  (println "Here's how I feel about that thing you did: " message))

(gensym)
(gensym 'message)

(defmacro without-mischief
  [& stuff-to-do]
  (let [macro-message (gensym 'message)]
    `(let [~macro-message "Oh, big deal!"]
       ~@stuff-to-do
       (println "I still need to say: " ~macro-message))))

(without-mischief
 (println "Here's how I feel about that thing you did: " message))

;; Auto gensym
(defmacro gensym-example
  []
  `(let [name# "Larry Potter"] name#))

(gensym-example)

(macroexpand '(gensym-example))

;; Double evaluation
(defmacro report
  [to-try]
  `(if ~to-try
     (println (quote ~to-try) "was successful:" ~to-try)
     (println (quote ~to-try) "was not successful:" ~to-try)))

(defmacro report
  [to-try]
  `(let [result# ~to-try]
     (if result#
       (println (quote ~to-try) "was successful:" result#)
       (println (quote ~to-try) "was not successful:" result#))))

(report (do (Thread/sleep 1000) (+ 1 1)))

;; Macros all the way down

(report (= 1 1))
(report (= 1 2))

(doseq [code ['(= 1 1) '(= 1 2)]]
  (report code))

(defmacro doseq-macro
  [macroname & args]
  `(do
     ~@(map (fn [arg] (list macroname arg)) args)))

(doseq-macro report (= 1 1) (= 1 2))

;; ------------------------------------------------------------
;; Brave and True ale

(def shipping-details
  {:name "Mitchard Blimmons"
   :address "134 Wonderment Ln"
   :city ""
   :state "FL"
   :postal-code "32501"
   :email "mitchard.blimmonsgmail.com"})

(def shipping-details-validations
  {:name
   ["Please enter a name" not-empty]

   :address
   ["Please enter an address" not-empty]

   :city
   ["Please enter a city" not-empty]

   :postal-code
   ["Please enter a postal code" not-empty

    "Please enter a postal code that looks correct"
    #(or (empty? %)
         (not (re-seq #"[^0-9-]" %)))]

   :email
   ["Please enter an email address" not-empty

    "Your email address doesn't look like an email address"
    (or #(empty? %)
        #(re-seq #"@" %))]})

(defn error-messages-for
  "return a seq of error messages"
  [to-validate message-validator-pairs]
  (map first (filter #(not ((second %) to-validate))
                     (partition 2 message-validator-pairs))))

(error-messages-for "" ["Please enter a city" not-empty])

(error-messages-for "shine bright like a diamond"
                    ["Please enter a postal code" not-empty
                     "Please enter a postal code that looks like a US postal code"
                     #(or (empty? %)
                          (not (re-seq #"[^0-9-]" %)))])

(defn validate
  "returns a map with a vec of errors for each key"
  [to-validate validations]
  (reduce (fn [errors validation]
            (let [[fieldname validation-check-groups] validation
                  value (get to-validate fieldname)
                  error-messages (error-messages-for value validation-check-groups)]
              (if (empty? error-messages)
                errors
                (assoc errors fieldname error-messages))))
          {}
          validations))

(validate shipping-details shipping-details-validations)

;; Often validation looks like this
(let [errors (validate shipping-details shipping-details-validations)]
  (if (empty? errors)
    (render :success)
    (render :failure errors)))

(let [errors (validate shipping-details shipping-details-validation)]
  (if (empty? errors)
    (do (save-shipping-details shipping-details)
        (redirect-to (url-for :order-confirmation)))
    (render "shipping-details" {:errors errors})))

;; Both do the same thing - validate and bind to errors, check if errors
;; if there are do success, else do failure

(defmacro if-valid
  "Handle valiation more concisely"
  [to-validate validations errors-name & then-else]
  `(let [~errors-name (validate ~to-validate ~validations)]
     (if (empty? ~errors-name)
       ~@then-else)))

(if-valid shipping-details shipping-details-validations errors
 (println :success)
 (println :failure errors))

`(max ~(shuffle (range 10)))
`(max ~@(shuffle (range 10)))

;; To get c without namespace
`[:a ~(+ 1 1) c]
`[:a ~(+ 1 1) ~'c]

`{:a 1 :b '~(+ 1 2)}
`[:a ~(+ 1 1) '~'c]
`{:a 1 :b '~@(list 1 2)}

`(1 `(2 3) 4)
`(list 1 `(2 ~(- 9 6)) 4)
