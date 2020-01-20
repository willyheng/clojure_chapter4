(ns fwpd.core)
(def filename "suspects.csv")

(slurp filename)

(def vamp-keys [:name :glitter-index])

(defn str->int [str] (Integer. str))

(def conversions {:name identity
                  :glitter-index str->int})

(defn convert
  [vamp-key value]
  ((get conversions vamp-key) value))

(convert :glitter-index "3")

(defn parse
  "Convert a CSV into rows of columns"
  [string]
  (map #(clojure.string/split % #",") (clojure.string/split string #"\n")))

 (def unmapped-row (first (parse (slurp filename))))
(def row-map {})
(def vamp-key :name)

(defn mapify
  "Return a seq of maps like {:name Edward Cullen :glitter-index 10"
  [rows]
  (map (fn [unmapped-row]
         (reduce (fn [row-map [vamp-key value]]
                   (assoc row-map vamp-key (convert vamp-key value)))
                 {}
                 (map vector vamp-keys unmapped-row)))
       rows))

(mapify (parse (slurp filename)))

(defn glitter-filter
  [minimum-glitter records]
  (filter #(>= (:glitter-index %) minimum-glitter) records))

(def suspects (mapify (parse (slurp filename))))

(glitter-filter 3 suspects)

;; Exercises

; Q1
(map :name (glitter-filter 3 suspects))

; Q2
(defn append
  "Append new suspect to list"
  [suspect]
  (conj suspects suspect))


(append {:name "hello" :glitter-index 5})

; Q3
(defn validate
  [map-keys suspect]
  (every? identity (map #(% suspect) map-keys)))

; Q5 
(defn write
  "Append new suspect to list"
  [{:keys [name glitter-index]}]
  (spit filename (str (slurp filename) "\n" name "," glitter-index)))

(defn to-csv
  "Convert map to csv string"
  [suspects]
  (def data-only (map (fn
                        [{:keys [name glitter-index]}]
                        [name glitter-index]) suspects))
  (clojure.string/join "\n"
                       (map #(clojure.string/join "," %)
                            data-only)))
